package io.github.ph1lou.werewolfplugin.roles.villagers;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.TimersBase;
import io.github.ph1lou.werewolfapi.events.DayEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.PotionEffects;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesWithLimitedSelectionDuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Comedian extends RolesWithLimitedSelectionDuration implements PotionEffects {

    private final List<PotionEffectType> comedianEffects = new ArrayList<>();

    public Comedian(GetWereWolfAPI main, PlayerWW playerWW, String key) {
        super(main, playerWW, key);
        setPower(false);
    }


    @Override
    public List<PotionEffectType> getPotionEffects() {
        return comedianEffects;
    }

    @Override
    public PotionEffectType getLastPotionEffect() {
        if (comedianEffects.isEmpty()) return PotionEffectType.BLINDNESS;
        return comedianEffects.get(comedianEffects.size() - 1);
    }

    @Override
    public void addPotionEffect(PotionEffectType comedianEffect) {
        this.comedianEffects.add(comedianEffect);
    }

    @Override
    public void removePotionEffect(PotionEffectType potionEffectType) {
        this.comedianEffects.remove(potionEffectType);
    }


    @EventHandler
    public void onDay(DayEvent event) {

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) return;

        player.removePotionEffect(getLastPotionEffect());

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (getPotionEffects().size() >= 3) return;

        setPower(true);

        player.sendMessage(game.translate("werewolf.role.comedian.wear_mask_message",
                game.getScore().conversion(
                        game.getConfig().getTimerValues().get(
                                TimersBase.POWER_DURATION.getKey()))));

    }

    @Override
    public @NotNull String getDescription() {
        return game.translate("werewolf.role.comedian.description");
    }


    @Override
    public void recoverPower() {

    }

}
