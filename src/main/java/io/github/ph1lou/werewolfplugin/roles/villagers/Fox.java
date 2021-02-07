package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enums.Camp;
import io.github.ph1lou.werewolfapi.enums.Sound;
import io.github.ph1lou.werewolfapi.enums.StateGame;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.TimersBase;
import io.github.ph1lou.werewolfapi.events.DayEvent;
import io.github.ph1lou.werewolfapi.events.SniffEvent;
import io.github.ph1lou.werewolfapi.events.UpdateEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.Display;
import io.github.ph1lou.werewolfapi.rolesattributs.LimitedUse;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.Progress;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesVillage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Fox extends RolesVillage implements Progress, LimitedUse, AffectedPlayers, Power {

    private float progress = 0;
    private int use = 0;
    private boolean power = false;
    private final List<PlayerWW> affectedPlayer = new ArrayList<>();

    public Fox(GetWereWolfAPI main, PlayerWW playerWW, String key) {
        super(main, playerWW, key);
    }

    @Override
    public void setPower(boolean power) {
        this.power = power;
    }

    @Override
    public boolean hasPower() {
        return (this.power);
    }

    @Override
    public void addAffectedPlayer(PlayerWW playerWW) {
        this.affectedPlayer.add(playerWW);
    }

    @Override
    public void removeAffectedPlayer(PlayerWW playerWW) {
        this.affectedPlayer.remove(playerWW);
    }

    @Override
    public void clearAffectedPlayer() {
        this.affectedPlayer.clear();
    }

    @Override
    public List<PlayerWW> getAffectedPlayers() {
        return (this.affectedPlayer);
    }

    @Override
    public int getUse() {
        return use;
    }

    @Override
    public void setUse(int use) {
        this.use = use;
    }

    @Override
    public float getProgress() {
        return (this.progress);
    }

    @Override
    public void setProgress(float progress) {
        this.progress = progress;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeathByFox(PlayerDeathEvent event) {

        if (event.getEntity().getKiller() == null) return;

        Player killer = event.getEntity().getKiller();

        if (!getPlayerUUID().equals(killer.getUniqueId())) return;

        killer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,
                3600,
                0,
                false,
                false));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDay(DayEvent event) {

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (getUse() >= game.getConfig().getUseOfFlair()) {
            return;
        }

        setPower(true);
        getPlayerWW().sendMessageWithKey("werewolf.role.fox.smell_message",
                game.getConfig().getUseOfFlair() - getUse());
    }


    @Override
    public @NotNull String getDescription() {
        return super.getDescription() +
                game.translate("werewolf.description.description",
                        game.translate("werewolf.role.fox.description",
                                game.getConfig().getDistanceFox(),
                                game.getScore().conversion(game.getConfig()
                                        .getTimerValue(TimersBase.FOX_SMELL_DURATION.getKey())),
                                game.getConfig().getUseOfFlair() - use)) +
                game.translate("werewolf.description.effect", game.translate("werewolf.role.fox.effect"));
    }


    @Override
    public void recoverPower() {

    }


    @EventHandler
    public void onUpdate(UpdateEvent event) {

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) {
            return;
        }
        if (!getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }
        if (getAffectedPlayers().isEmpty()) {
            return;
        }

        PlayerWW playerWW = getAffectedPlayers().get(0);
        Player flair = Bukkit.getPlayer(playerWW.getUUID());

        if (!playerWW.isState(StatePlayer.ALIVE)) {
            return;
        }

        if (flair == null) {
            return;
        }

        Location renardLocation = player.getLocation();
        Location playerLocation = flair.getLocation();

        try {
            if (renardLocation.distance(playerLocation) >
                    game.getConfig().getDistanceFox()) {
                return;
            }
        } catch (Exception ignored) {
            return;
        }

        float temp = getProgress() + 100f /
                (game.getConfig().getTimerValue(TimersBase.FOX_SMELL_DURATION.getKey()) + 1);

        setProgress(temp);

        if (temp % 10 > 0 && temp % 10 <= 100f /
                (game.getConfig().getTimerValue(TimersBase.FOX_SMELL_DURATION.getKey()) + 1)) {
            player.sendMessage(game.translate("werewolf.role.fox.progress",
                    Math.min(100, Math.floor(temp))));
        }

        if (temp >= 100) {

            boolean isWereWolf = true;

            if (playerWW.getRole() instanceof Display &&
                    (!((Display) playerWW.getRole()).isDisplayCamp(Camp.WEREWOLF.getKey()))) {
                isWereWolf = false;
            } else if (!playerWW.getRole().isWereWolf()) {
                isWereWolf = false;
            }

            SniffEvent sniffEvent = new SniffEvent(getPlayerWW(),
                    playerWW, isWereWolf);

            Bukkit.getPluginManager().callEvent(sniffEvent);

            if (!sniffEvent.isCancelled()) {
                if (sniffEvent.isWereWolf()) {
                    player.sendMessage(game.translate(
                            "werewolf.role.fox.werewolf",
                            playerWW.getName()));
                } else {
                    player.sendMessage(game.translate(
                            "werewolf.role.fox.not_werewolf",
                            playerWW.getName()));
                }
                player.sendMessage(game.translate("werewolf.role.fox.warn"));
                Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin) main, () -> {
                    if (game.isState(StateGame.GAME)) {
                        playerWW.sendMessageWithKey("werewolf.role.fox.smell", Sound.DONKEY_ANGRY);
                    }
                }, 20 * 60 * 5);
            } else {
                player.sendMessage(game.translate("werewolf.check.cancel"));
            }

            clearAffectedPlayer();
            setProgress(0f);
        }
    }
}
