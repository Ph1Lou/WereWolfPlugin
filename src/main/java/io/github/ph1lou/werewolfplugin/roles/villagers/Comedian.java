package io.github.ph1lou.werewolfplugin.roles.villagers;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.State;
import io.github.ph1lou.werewolfapi.enumlg.TimerLG;
import io.github.ph1lou.werewolfapi.events.DayEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.PotionEffects;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesWithLimitedSelectionDuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Comedian extends RolesWithLimitedSelectionDuration implements PotionEffects {

    private final List<PotionEffectType> comedianEffects = new ArrayList<>(Collections.singletonList(PotionEffectType.BLINDNESS));

    public Comedian(GetWereWolfAPI main, WereWolfAPI game, UUID uuid) {
        super(main,game,uuid);
        setPower(false);
    }


    @Override
    public List<PotionEffectType> getPotionEffects() {
        return comedianEffects;
    }

    @Override
    public PotionEffectType getLastPotionEffect() {
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

        if(!game.getPlayersWW().get(getPlayerUUID()).isState(State.ALIVE)){
            return;
        }
        setPower(true);

        if(Bukkit.getPlayer(getPlayerUUID())==null){
            return;
        }
        Player player = Bukkit.getPlayer(getPlayerUUID());

        player.removePotionEffect(getLastPotionEffect());
        if (getPotionEffects().size() < 4) {
            player.sendMessage(game.translate("werewolf.role.comedian.wear_mask_message", game.getScore().conversion(game.getConfig().getTimerValues().get(TimerLG.POWER_DURATION))));
        }
    }

    @Override
    public String getDescription() {
        return game.translate("werewolf.role.comedian.description");
    }

    @Override
    public String getDisplay() {
        return "werewolf.role.comedian.display";
    }
}
