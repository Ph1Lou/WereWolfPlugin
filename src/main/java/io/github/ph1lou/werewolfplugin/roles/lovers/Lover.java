package io.github.ph1lou.werewolfplugin.roles.lovers;

import io.github.ph1lou.werewolfapi.LoverAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.LoverType;
import io.github.ph1lou.werewolfapi.enums.RolesBase;
import io.github.ph1lou.werewolfapi.enums.Sound;
import io.github.ph1lou.werewolfapi.enums.StateGame;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.ActionBarEvent;
import io.github.ph1lou.werewolfapi.events.AroundLover;
import io.github.ph1lou.werewolfapi.events.EndPlayerMessageEvent;
import io.github.ph1lou.werewolfapi.events.FinalDeathEvent;
import io.github.ph1lou.werewolfapi.events.LoverDeathEvent;
import io.github.ph1lou.werewolfapi.events.UpdateModeratorNameTag;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Lover implements LoverAPI, Listener {

    private final List<PlayerWW> lovers;
    private final WereWolfAPI game;
    private boolean death = false;

    public Lover(WereWolfAPI game, List<PlayerWW> lovers) {
        this.game = game;
        this.lovers = lovers;
        lovers.forEach(playerWW -> playerWW.addLover(this));
    }

    public List<? extends PlayerWW> getLovers() {
        return lovers;
    }


    public void announceLovers() {
        lovers.forEach(this::announceLovers);
    }

    public void announceLovers(PlayerWW playerWW) {

        if (death) return;


        if (!lovers.contains(playerWW)) return;

        StringBuilder couple = new StringBuilder();

        for (PlayerWW playerWW1 : lovers) {
            if (!playerWW.equals(playerWW1)) {
                couple.append(playerWW1.getName()).append(" ");
            }
        }
        playerWW.sendMessage(game.translate("werewolf.role.lover.description", couple.toString()));
        Sound.SHEEP_SHEAR.play(playerWW);
    }


    @EventHandler
    public void onActionBarGameLoverEvent(ActionBarEvent event) {

        if (!game.isState(StateGame.GAME)) return;

        UUID uuid = event.getPlayerUUID();
        PlayerWW playerWW = game.getPlayerWW(uuid);

        if (!lovers.contains(playerWW)) return;

        StringBuilder sb = new StringBuilder(event.getActionBar());
        Player player = Bukkit.getPlayer(uuid);

        if (player == null) return;

        if (playerWW == null) return;

        if (!playerWW.isState(StatePlayer.ALIVE)) return;

        buildActionbarLover(player, sb, lovers);

        event.setActionBar(sb.toString());

    }

    private void buildActionbarLover(Player player, StringBuilder sb, List<PlayerWW> list) {

        list
                .stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .filter(playerWW -> !playerWW.getUUID().equals(player.getUniqueId()))
                .peek(playerWW -> sb.append(" §d♥ ")
                        .append(playerWW.getName())
                        .append(" "))
                .forEach(playerWW -> sb
                        .append(game.getScore()
                                .updateArrow(player,
                                        playerWW.getLocation())));
    }

    @EventHandler
    public void onModeratorScoreBoard(UpdateModeratorNameTag event) {

        StringBuilder sb = new StringBuilder(event.getSuffix());

        PlayerWW playerWW = game.getPlayerWW(event.getPlayerUUID());

        if (playerWW == null) return;

        if (!lovers.contains(playerWW)) return;

        if (playerWW.isState(StatePlayer.DEATH)) {
            return;
        }

        sb.append(ChatColor.LIGHT_PURPLE).append(" ♥");

        event.setSuffix(sb.toString());
    }



    @EventHandler(priority = EventPriority.HIGH)
    public void onFinalDeath(FinalDeathEvent event) {

        if (death) return;

        if (!lovers.contains(event.getPlayerWW())) return;

        death = true;
        lovers.stream()
                .filter(playerWW1 -> !playerWW1.equals(event.getPlayerWW()))
                .forEach(playerWW1 -> {
                    Bukkit.broadcastMessage(
                            game.translate("werewolf.role.lover.lover_death",
                                    playerWW1.getName()));
                    Bukkit.getPluginManager().callEvent(
                            new LoverDeathEvent(event.getPlayerWW(), playerWW1));
                    game.death(playerWW1);
                });

        game.getConfig().removeOneLover(LoverType.LOVER.getKey());

        HandlerList.unregisterAll(this);

    }

    @EventHandler
    public void onEndPlayerMessage(EndPlayerMessageEvent event) {

        PlayerWW playerWW = event.getPlayerWW();

        if (!lovers.contains(playerWW)) return;

        StringBuilder sb = event.getEndMessage();
        StringBuilder sb2 = new StringBuilder();
        lovers.stream()
                .filter(playerWW1 -> !playerWW.equals(playerWW1))
                .forEach(playerWW1 -> sb2.append(playerWW1.getName()).append(" "));

        sb.append(game.translate("werewolf.end.lover", sb2.toString()));
    }


    @Override
    public String getKey() {
        return LoverType.LOVER.getKey();
    }

    @Override
    public boolean isAlive() {
        return !death;
    }

    @Override
    public boolean swap(PlayerWW playerWW, PlayerWW playerWW1) {

        if (playerWW.equals(playerWW1)) return false;

        if (death) return false;

        lovers.remove(playerWW);
        lovers.add(playerWW1);

        lovers.forEach(this::announceLovers);

        game.getPlayerWW()
                .stream().map(PlayerWW::getRole)
                .filter(roles -> roles.isKey(RolesBase.CUPID.getKey()))
                .map(roles -> (AffectedPlayers) roles)
                .filter(affectedPlayers -> affectedPlayers.getAffectedPlayers().contains(playerWW))
                .forEach(affectedPlayers -> {
                    affectedPlayers.removeAffectedPlayer(playerWW);
                    affectedPlayers.addAffectedPlayer(playerWW1);
                });

        return true;
    }

    @Override
    public boolean isKey(String key) {
        return getKey().equals(key);
    }

    @EventHandler
    public void onAroundLover(AroundLover event) {


        if (death) return;

        for (PlayerWW playerWW : event.getPlayerWWS()) {
            if (getLovers().contains(playerWW)) {
                for (PlayerWW playerWW1 : getLovers()) {
                    event.addPlayer(playerWW1);
                }
                break;
            }
        }
    }

    public void addLover(PlayerWW playerWW) {

        if (lovers.contains(playerWW)) return;

        lovers.forEach(playerWW1 -> playerWW1.sendMessage(game.translate("werewolf.random_events.triple.lover_join", playerWW.getName())));

        playerWW.sendMessage(game.translate("werewolf.random_events.triple.join", getLovers().stream()
                .map(PlayerWW::getName)
                .collect(Collectors.joining(" "))));

        lovers.add(playerWW);

    }

/*
    public void thiefLoversRange(UUID playerUUID) {


        int cp = -1;
        int ck = -1;


        for (int i = 0; i < game.getLoversRange().size(); i++) {
            List<UUID> loverList = game.getLoversRange().get(i);
            if (loverList.contains(playerUUID) && !loverList.contains(getPlayerUUID())) {
                loverList.remove(playerUUID);
                loverList.add(getPlayerUUID());
                cp = i;
            } else if (!loverList.contains(playerUUID) &&
                    loverList.contains(getPlayerUUID())) {
                ck = i;
            }
        }
        if (cp != -1 && ck != -1) {
            game.getLoversRange().get(ck).remove(getPlayerUUID());
            game.getLoversRange().get(cp).addAll(game.getLoversRange().get(ck));
            game.getLoversRange().remove(ck);
        }
    }*/
}
