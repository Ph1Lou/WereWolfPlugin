package io.github.ph1lou.werewolfplugin.roles.lovers;

import com.google.common.collect.Sets;
import io.github.ph1lou.werewolfapi.LoverAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.LoverType;
import io.github.ph1lou.werewolfapi.enums.Sound;
import io.github.ph1lou.werewolfapi.enums.StateGame;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.ActionBarEvent;
import io.github.ph1lou.werewolfapi.events.AmnesiacLoverDeathEvent;
import io.github.ph1lou.werewolfapi.events.AroundLover;
import io.github.ph1lou.werewolfapi.events.EndPlayerMessageEvent;
import io.github.ph1lou.werewolfapi.events.FinalDeathEvent;
import io.github.ph1lou.werewolfapi.events.RevealAmnesiacLoversEvent;
import io.github.ph1lou.werewolfapi.events.UpdateEvent;
import io.github.ph1lou.werewolfapi.events.UpdateModeratorNameTag;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class AmnesiacLover implements LoverAPI, Listener {

    private final WereWolfAPI game;
    private PlayerWW amnesiacLover1;
    private PlayerWW amnesiacLover2;
    private boolean find = false;
    private boolean death = false;

    public AmnesiacLover(WereWolfAPI game, PlayerWW amnesiacLover1, PlayerWW amnesiacLover2) {
        this.game = game;
        this.amnesiacLover1 = amnesiacLover1;
        this.amnesiacLover2 = amnesiacLover2;
        getLovers().forEach(playerWW -> playerWW.addLover(this));
    }

    public List<? extends PlayerWW> getLovers() {
        return new ArrayList<>(Arrays.asList(amnesiacLover1, amnesiacLover2));
    }

    public PlayerWW getOtherLover(PlayerWW playerWW) {
        return playerWW.equals(amnesiacLover1) ? amnesiacLover2 : amnesiacLover1;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onFinalDeath(FinalDeathEvent event) {

        if (death) return;

        if (!getLovers().contains(event.getPlayerWW())) return;

        death = true;

        HandlerList.unregisterAll(this);

        if (!find) return;

        PlayerWW playerWW1 = getOtherLover(event.getPlayerWW());

        Bukkit.broadcastMessage(game.translate("werewolf.role.lover.lover_death",
                playerWW1.getName()));
        Bukkit.getPluginManager().callEvent(
                new AmnesiacLoverDeathEvent(event.getPlayerWW(), playerWW1));
        game.death(playerWW1);
        game.getConfig().setAmnesiacLoverSize(game.getConfig().getAmnesiacLoverSize() - 1);
    }


    @EventHandler
    public void onDetectionAmnesiacLover(UpdateEvent event) {

        if (find) return;

        if(death) return;

        Player player1 = Bukkit.getPlayer(amnesiacLover1.getUUID());
        Player player2 = Bukkit.getPlayer(amnesiacLover2.getUUID());

        if (player1 == null || player2 == null) return;

        try {
            if (player1.getLocation().distance(player2.getLocation()) <
                    game.getConfig().getDistanceAmnesiacLovers()) {

                Bukkit.getPluginManager().callEvent(new RevealAmnesiacLoversEvent(
                        Sets.newHashSet(amnesiacLover1, amnesiacLover2)));

                find = true;
                announceAmnesiacLoversOnJoin(amnesiacLover1);
                announceAmnesiacLoversOnJoin(amnesiacLover2);
                game.getConfig().setAmnesiacLoverSize(
                        game.getConfig().getAmnesiacLoverSize() + 1);
                game.checkVictory();

            }
        } catch (Exception ignored) {

        }
    }

    public void announceAmnesiacLoversOnJoin(PlayerWW player) {

        if (!find) return;

        if (amnesiacLover1.equals(player)) {
            player.sendMessage(game.translate("werewolf.role.lover.description",
                    amnesiacLover2.getName()));
            Sound.PORTAL_TRAVEL.play(player);
        } else if (amnesiacLover2.equals(player)) {
            player.sendMessage(game.translate("werewolf.role.lover.description",
                    amnesiacLover1.getName()));
            Sound.PORTAL_TRAVEL.play(player);
        }
    }


    @EventHandler
    public void onModeratorScoreBoard(UpdateModeratorNameTag event) {

        StringBuilder sb = new StringBuilder(event.getSuffix());

        PlayerWW playerWW = game.getPlayerWW(event.getPlayerUUID());

        if (playerWW == null) return;

        if (!getLovers().contains(playerWW)) return;

        if (playerWW.isState(StatePlayer.DEATH)) {
            return;
        }

        sb.append(ChatColor.DARK_PURPLE).append(" ♥");

        event.setSuffix(sb.toString());
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
    public void onActionBarGameLoverEvent(ActionBarEvent event) {

        if(!find) return;

        if(death) return;

        if (!game.isState(StateGame.GAME)) return;

        UUID uuid = event.getPlayerUUID();
        PlayerWW playerWW = game.getPlayerWW(uuid);

        if (!getLovers().contains(playerWW)) return;

        StringBuilder sb = new StringBuilder(event.getActionBar());
        Player player = Bukkit.getPlayer(uuid);

        if (player == null) return;

        if (playerWW == null) return;

        if (!playerWW.isState(StatePlayer.ALIVE)) return;

        if (!find) {
            return;
        }

        buildActionbarLover(player,
                sb,
                new ArrayList<>(Arrays.asList(amnesiacLover1, amnesiacLover2)));

        event.setActionBar(sb.toString());

    }

    @EventHandler
    public void onEndPlayerMessage(EndPlayerMessageEvent event) {

        PlayerWW playerWW = event.getPlayerWW();

        if (!getLovers().contains(playerWW)) return;

        PlayerWW playerWW1 = getOtherLover(playerWW);

        StringBuilder sb = event.getEndMessage();

        sb.append(game.translate("werewolf.end.lover",
                playerWW1.getName() + " "));
    }

    @Override
    public String getKey() {
        return LoverType.AMNESIAC_LOVER.getKey();
    }

    @Override
    public boolean isAlive() {
        return !death;
    }

    @Override
    public boolean isKey(String key) {
        return getKey().equals(key);
    }

    public boolean isRevealed() {
        return find;
    }

    @Override
    public boolean swap(PlayerWW playerWW, PlayerWW playerWW1) {

        if (playerWW.equals(playerWW1)) return false;

        if (death) return false;

        if (amnesiacLover1.equals(playerWW)) {
            amnesiacLover1 = playerWW1;
        } else {
            amnesiacLover2 = playerWW1;
        }


        for (PlayerWW playerWW2 : getLovers()) {
            announceAmnesiacLoversOnJoin(playerWW2);
        }
        return true;
    }

    @EventHandler
    public void onAroundLover(AroundLover event) {

        if (death) return;

        for (PlayerWW playerWW : event.getPlayerWWS()) {
            if (getLovers().contains(playerWW)) {
                event.addPlayer(getOtherLover(playerWW));
                break;
            }
        }
    }
}
