package io.github.ph1lou.werewolfplugin.roles.lovers;

import com.google.common.collect.Sets;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.RolesBase;
import io.github.ph1lou.werewolfapi.enumlg.Sounds;
import io.github.ph1lou.werewolfapi.enumlg.StateGame;
import io.github.ph1lou.werewolfapi.enumlg.StatePlayer;
import io.github.ph1lou.werewolfapi.events.*;
import io.github.ph1lou.werewolfapi.rolesattributs.LoverAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.*;

public class AmnesiacLover implements LoverAPI, Listener {

    private final WereWolfAPI game;
    private PlayerWW amnesiacLover1;
    private PlayerWW amnesiacLover2;
    private boolean announce1 = false;
    private boolean announce2 = false;
    private boolean find = false;
    private boolean death = false;

    public AmnesiacLover(WereWolfAPI game, PlayerWW amnesiacLover1, PlayerWW amnesiacLover2) {
        this.game = game;
        this.amnesiacLover1 = amnesiacLover1;
        this.amnesiacLover2 = amnesiacLover2;
        getLovers().forEach(playerWW -> playerWW.getLovers().add(this));
    }

    public List<? extends PlayerWW> getLovers() {
        return new ArrayList<>(Arrays.asList(amnesiacLover1, amnesiacLover2));
    }

    public PlayerWW getOtherLover(PlayerWW playerWW) {
        return playerWW.equals(amnesiacLover1) ? amnesiacLover1 : amnesiacLover2;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onFinalDeath(FinalDeathEvent event) {

        if (death) return;

        if (!getLovers().contains(event.getPlayerWW())) return;

        death = true;

        if (!find) return;

        PlayerWW playerWW1 = getOtherLover(event.getPlayerWW());

        Bukkit.broadcastMessage(game.translate("werewolf.role.lover.lover_death",
                playerWW1.getName()));
        Bukkit.getPluginManager().callEvent(
                new AmnesiacLoverDeathEvent(event.getPlayerWW(), playerWW1));
        game.death(playerWW1);
        game.getConfig().setAmnesiacLoverSize(game.getConfig().getAmnesiacLoverSize() - 1);
    }

    public void detectionAmnesiacLover() {

        if (find) return;

        Player player1 = Bukkit.getPlayer(amnesiacLover1.getUUID());
        Player player2 = Bukkit.getPlayer(amnesiacLover2.getUUID());

        if (player1 == null || player2 == null) return;

        try {
            if (player1.getLocation().distance(player2.getLocation()) <
                    game.getConfig().getDistanceAmnesiacLovers()) {

                Bukkit.getPluginManager().callEvent(new RevealAmnesiacLoversEvent(
                        Sets.newHashSet(amnesiacLover1, amnesiacLover2)));

                find = true;
                announceAmnesiacLoversOnJoin(player1);
                announceAmnesiacLoversOnJoin(player2);
                game.getConfig().setAmnesiacLoverSize(
                        game.getConfig().getAmnesiacLoverSize() + 1);
                game.checkVictory();

            }
        } catch (Exception ignored) {

        }
    }

    public void announceAmnesiacLoversOnJoin(Player player) {

        if (!find) return;

        UUID uuid = player.getUniqueId();
        if (amnesiacLover1.getUUID().equals(uuid)) {
            if (!announce1) {
                player.sendMessage(game.translate("werewolf.role.lover.description",
                        amnesiacLover2.getName()));
                Sounds.PORTAL_TRAVEL.play(player);
            }
            announce1 = true;
        } else if (amnesiacLover2.getUUID().equals(uuid)) {
            if (!announce2) {
                player.sendMessage(game.translate("werewolf.role.lover.description",
                        amnesiacLover1.getName()));
                Sounds.PORTAL_TRAVEL.play(player);
            }
            announce2 = true;
        }
    }


    @EventHandler
    public void onModeratorScoreBoard(UpdateModeratorNameTag event) {

        if (!find) return;

        StringBuilder sb = new StringBuilder(event.getSuffix());

        PlayerWW playerWW = game.getPlayerWW(event.getPlayerUUID());

        if (playerWW == null) return;

        if (!getLovers().contains(playerWW)) return;

        if (playerWW.isState(StatePlayer.DEATH)) {
            return;
        }

        sb.append(ChatColor.DARK_PURPLE).append("♥ ");

        event.setSuffix(sb.toString());
    }

    private void buildActionbarLover(Player player, StringBuilder sb, List<PlayerWW> list) {

        list
                .stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .peek(playerWW -> sb.append(" §d♥ ")
                        .append(playerWW.getName())
                        .append(" "))
                .map(PlayerWW::getUUID)
                .filter(uuid -> !uuid.equals(player.getUniqueId()))
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .forEach(player1 -> sb
                        .append(game.getScore()
                                .updateArrow(player,
                                        player1.getLocation())));
    }

    @EventHandler
    public void onActionBarGameLoverEvent(ActionBarEvent event) {

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
        return RolesBase.AMNESIAC_LOVER.getKey();
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
    public void swap(PlayerWW playerWW, PlayerWW playerWW1) {

        if (amnesiacLover1.equals(playerWW)) {
            amnesiacLover1 = playerWW1;
        } else {
            amnesiacLover2 = playerWW1;
        }


        announce1 = false;
        announce2 = false;

        for (PlayerWW playerWW2 : getLovers()) {
            Player player = Bukkit.getPlayer(playerWW2.getUUID());
            if (player != null) {
                announceAmnesiacLoversOnJoin(player);
            }
        }

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        announceAmnesiacLoversOnJoin(player);
    }


}
