package io.github.ph1lou.werewolfplugin.roles.lovers;

import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.RolesBase;
import io.github.ph1lou.werewolfapi.enumlg.Sounds;
import io.github.ph1lou.werewolfapi.enumlg.StatePlayer;
import io.github.ph1lou.werewolfapi.events.*;
import io.github.ph1lou.werewolfapi.rolesattributs.LoverAPI;
import io.github.ph1lou.werewolfapi.versions.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class CursedLover implements LoverAPI, Listener {

    private final WereWolfAPI game;
    private PlayerWW cursedLover1;
    private PlayerWW cursedLover2;
    private boolean announce1 = false;
    private boolean announce2 = false;
    private boolean power1 = false;
    private boolean power2 = false;
    private boolean death = false;


    public CursedLover(WereWolfAPI game, PlayerWW cursedLover1, PlayerWW cursedLover2) {
        this.game = game;
        this.cursedLover1 = cursedLover1;
        this.cursedLover2 = cursedLover2;
        getLovers().forEach(playerWW -> playerWW.getLovers().add(this));
    }

    public PlayerWW getOtherLover(PlayerWW playerWW) {
        return playerWW.equals(cursedLover1) ? cursedLover1 : cursedLover2;
    }

    public List<? extends PlayerWW> getLovers() {
        return new ArrayList<>(Arrays.asList(cursedLover1, cursedLover2));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onFinalDeath(FinalDeathEvent event) {

        if (death) return;

        if (!getLovers().contains(event.getPlayerWW())) return;

        death = true;
        PlayerWW playerWW1 = getOtherLover(event.getPlayerWW());

        Bukkit.getPluginManager().callEvent(
                new CursedLoverDeathEvent(event.getPlayerWW(), playerWW1));

        Player cursedLover = Bukkit.getPlayer(playerWW1.getUUID());

        if (cursedLover != null) {

            cursedLover.sendMessage(game.translate("werewolf.role.cursed_lover.death_cursed_lover"));
            VersionUtils.getVersionUtils()
                    .setPlayerMaxHealth(cursedLover,
                            Math.max(VersionUtils.getVersionUtils()
                                    .getPlayerMaxHealth(cursedLover) - 2, 1));
        }

        game.getConfig().setCursedLoverSize(game.getConfig().getCursedLoverSize() - 1);
    }

    public void announceCursedLoversOnJoin(Player player) {


        UUID uuid = player.getUniqueId();
        if (cursedLover1.getUUID().equals(uuid)) {
            if (!announce1) {
                if (!power1) {
                    setPower(player);
                }
                power1 = true;
                player.sendMessage(game.translate("werewolf.role.cursed_lover.description",
                        cursedLover2.getName()));
                Sounds.SHEEP_SHEAR.play(player);
                announce1 = true;
            }
        } else if (cursedLover2.getUUID().equals(uuid)) {
            if (!announce2) {
                if (!power2) {
                    setPower(player);
                }
                power2 = true;
                setPower(player);
                player.sendMessage(game.translate("werewolf.role.cursed_lover.description",
                        cursedLover1.getName()));
                Sounds.SHEEP_SHEAR.play(player);
                announce2 = true;
            }
        }
    }

    public void setPower(Player player) {

        VersionUtils.getVersionUtils().setPlayerMaxHealth(player,
                VersionUtils.getVersionUtils().getPlayerMaxHealth(player) + 2);

        Sounds.SHEEP_SHEAR.play(player);
    }

    @EventHandler
    private void onPlayerDamage(EntityDamageEvent event) {

        Player player = (Player) event.getEntity();
        UUID uuid = player.getUniqueId();
        PlayerWW playerWW = game.getPlayerWW(uuid);

        if (getLovers().contains(playerWW)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        announceCursedLoversOnJoin(player);
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

        sb.append(ChatColor.BLACK).append("♥ ");

        event.setSuffix(sb.toString());
    }

    @EventHandler
    public void onEndPlayerMessage(EndPlayerMessageEvent event) {

        PlayerWW playerWW = event.getPlayerWW();

        if (!getLovers().contains(playerWW)) return;

        PlayerWW playerWW1 = getOtherLover(playerWW);

        StringBuilder sb = event.getEndMessage();

        sb.append(game.translate("werewolf.end.cursed_lover",
                playerWW1.getName() + " "));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDetectVictoryCancel(WinConditionsCheckEvent event) {

        if (event.isCancelled()) return;

        if (death) return;

        event.setCancelled(true);
    }

    @Override
    public String getKey() {
        return RolesBase.CURSED_LOVER.getKey();
    }

    @Override
    public boolean isAlive() {
        return !death;
    }

    @Override
    public boolean isKey(String key) {
        return getKey().equals(key);
    }

    @Override
    public void swap(PlayerWW playerWW, PlayerWW playerWW1) {

        if (cursedLover1.equals(playerWW)) {
            cursedLover1 = playerWW1;
            power1 = false;
        } else {
            cursedLover2 = playerWW1;
            power2 = false;
        }

        announce1 = false;
        announce2 = false;

        for (PlayerWW playerWW2 : getLovers()) {
            Player player = Bukkit.getPlayer(playerWW2.getUUID());
            if (player != null) {
                announceCursedLoversOnJoin(player);
            }
        }
    }

}
