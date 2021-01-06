package io.github.ph1lou.werewolfplugin.roles.lovers;

import io.github.ph1lou.werewolfapi.LoverAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.LoverType;
import io.github.ph1lou.werewolfapi.enums.Sound;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class CursedLover implements LoverAPI, Listener {

    private final WereWolfAPI game;
    private PlayerWW cursedLover1;
    private PlayerWW cursedLover2;
    private boolean power1 = false;
    private boolean power2 = false;
    private boolean death = false;


    public CursedLover(WereWolfAPI game, PlayerWW cursedLover1, PlayerWW cursedLover2) {
        this.game = game;
        this.cursedLover1 = cursedLover1;
        this.cursedLover2 = cursedLover2;
        getLovers().forEach(playerWW -> playerWW.addLover(this));
    }

    public PlayerWW getOtherLover(PlayerWW playerWW) {
        return playerWW.equals(cursedLover1) ? cursedLover2 : cursedLover1;
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

        playerWW1.sendMessage(game.translate("werewolf.role.cursed_lover.death_cursed_lover"));

        playerWW1.removePlayerMaxHealth(2);

        game.getConfig().setCursedLoverSize(game.getConfig().getCursedLoverSize() - 1);
    }

    public void announceCursedLoversOnJoin(PlayerWW playerWW) {

        if (cursedLover1.equals(playerWW)) {
            if (!power1) {
                setPower(playerWW);
            }
            power1 = true;
            playerWW.sendMessage(game.translate("werewolf.role.cursed_lover.description",
                    cursedLover2.getName()));
            Sound.SHEEP_SHEAR.play(playerWW);
        } else if (cursedLover2.equals(playerWW)) {
            if (!power2) {
                setPower(playerWW);
            }
            power2 = true;
            setPower(playerWW);
            playerWW.sendMessage(game.translate("werewolf.role.cursed_lover.description",
                    cursedLover1.getName()));
            Sound.SHEEP_SHEAR.play(playerWW);
        }
    }

    public void setPower(PlayerWW player) {

        player.addPlayerMaxHealth(2);

        Sound.SHEEP_SHEAR.play(player);
    }

    @EventHandler
    private void onPlayerDamage(EntityDamageEvent event) {

        if (death) return;

        if (!event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) return;

        if(!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        UUID uuid = player.getUniqueId();
        PlayerWW playerWW = game.getPlayerWW(uuid);

        if (getLovers().contains(playerWW)) {
            event.setCancelled(true);
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

        sb.append(ChatColor.BLACK).append(" ♥");

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
        return LoverType.CURSED_LOVER.getKey();
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
    public boolean swap(PlayerWW playerWW, PlayerWW playerWW1) {

        if (playerWW.equals(playerWW1)) return false;

        if (death) return false;

        if (cursedLover1.equals(playerWW)) {
            cursedLover1 = playerWW1;
            power1 = false;
        } else {
            cursedLover2 = playerWW1;
            power2 = false;
        }

        for (PlayerWW playerWW2 : getLovers()) {
            announceCursedLoversOnJoin(playerWW2);
        }

        return true;
    }


}
