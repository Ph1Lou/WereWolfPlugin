package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.Camp;
import io.github.ph1lou.werewolfapi.enumlg.StatePlayer;
import io.github.ph1lou.werewolfapi.enumlg.TimersBase;
import io.github.ph1lou.werewolfapi.events.DayEvent;
import io.github.ph1lou.werewolfapi.events.SniffEvent;
import io.github.ph1lou.werewolfapi.events.UpdateEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Fox extends RolesVillage implements Progress, LimitedUse, AffectedPlayers, Power {

    private float progress = 0;
    private int use = 0;
    private boolean power = false;
    private final List<UUID> affectedPlayer = new ArrayList<>();

    public Fox(GetWereWolfAPI main, WereWolfAPI game, UUID uuid, String key) {
        super(main,game,uuid, key);
    }

    @Override
    public void setPower(Boolean power) {
        this.power=power;
    }

    @Override
    public Boolean hasPower() {
        return(this.power);
    }


    @Override
    public void addAffectedPlayer(UUID uuid) {
        this.affectedPlayer.add(uuid);
    }

    @Override
    public void removeAffectedPlayer(UUID uuid) {
        this.affectedPlayer.remove(uuid);
    }

    @Override
    public void clearAffectedPlayer() {
        this.affectedPlayer.clear();
    }

    @Override
    public List<UUID> getAffectedPlayers() {
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
    public void setProgress(Float progress) {
        this.progress = progress;
    }

    @EventHandler
    public void onDay(DayEvent event) {

        if (!game.getPlayersWW().get(getPlayerUUID()).isState(StatePlayer.ALIVE)) {
            return;
        }

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) {
            return;
        }

        if (getUse() >= game.getConfig().getUseOfFlair()) {
            return;
        }

        setPower(true);
        player.sendMessage(game.translate("werewolf.role.fox.smell_message", game.getConfig().getUseOfFlair() - getUse()));
    }


    @Override
    public @NotNull String getDescription() {
        return game.translate("werewolf.role.fox.description");
    }


    @Override
    public void recoverPotionEffect() {

        super.recoverPotionEffect();

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) return;

        player.removePotionEffect(PotionEffectType.SPEED);
        player.addPotionEffect(
                new PotionEffect(
                        PotionEffectType.SPEED,
                        Integer.MAX_VALUE,
                        0,
                        false,
                        false));

    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) {
            return;
        }
        if (!game.getPlayersWW().get(getPlayerUUID()).isState(StatePlayer.ALIVE)) {
            return;
        }
        if (getAffectedPlayers().isEmpty()) {
            return;
        }

        UUID playerSmellUUID = getAffectedPlayers().get(0);
        PlayerWW plf = game.getPlayersWW().get(playerSmellUUID);
        Player flair = Bukkit.getPlayer(playerSmellUUID);

        if (!plf.isState(StatePlayer.ALIVE)) {
            return;
        }

        if (flair == null) {
            return;
        }

        Location renardLocation = player.getLocation();
        Location playerLocation = flair.getLocation();

        if (renardLocation.distance(playerLocation) >
                game.getConfig().getDistanceFox()) {
            return;
        }

        float temp = getProgress() + 100f /
                (game.getConfig().getTimerValues()
                        .get(TimersBase.FOX_SMELL_DURATION.getKey()) + 1);

        setProgress(temp);

        if (temp % 10 > 0 && temp % 10 <= 100f /
                (game.getConfig().getTimerValues()
                        .get(TimersBase.FOX_SMELL_DURATION.getKey()) + 1)) {
            player.sendMessage(game.translate("werewolf.role.fox.progress",
                    Math.min(100, Math.floor(temp))));
        }

        if (temp >= 100) {

            boolean isWereWolf = true;

            if (plf.getRole() instanceof Display &&
                    (!((Display) plf.getRole()).isDisplayCamp(Camp.WEREWOLF))) {
                isWereWolf = false;
            } else if (!plf.getRole().isWereWolf()) {
                isWereWolf = false;
            }

            SniffEvent sniffEvent = new SniffEvent(getPlayerUUID(),
                    playerSmellUUID, isWereWolf);

            Bukkit.getPluginManager().callEvent(sniffEvent);

            if (!sniffEvent.isCancelled()) {
                if (sniffEvent.isWereWolf()) {
                    player.sendMessage(game.translate(
                            "werewolf.role.fox.werewolf",
                            plf.getName()));
                } else {
                    player.sendMessage(game.translate(
                            "werewolf.role.fox.not_werewolf",
                            plf.getName()));
                }
            } else player.sendMessage(game.translate("werewolf.check.cancel"));


            clearAffectedPlayer();
            setProgress(0f);
        }
    }
}
