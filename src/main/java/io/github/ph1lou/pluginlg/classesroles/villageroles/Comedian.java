package io.github.ph1lou.pluginlg.classesroles.villageroles;

import io.github.ph1lou.pluginlg.events.DayEvent;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.enumlg.RoleLG;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import io.github.ph1lou.pluginlgapi.enumlg.TimerLG;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Comedian extends RolesWithLimitedSelectionDuration {

    private final List<PotionEffectType> comedianEffects = new ArrayList<>(Collections.singletonList(PotionEffectType.BLINDNESS));

    public Comedian(GameManager game, UUID uuid) {
        super(game,uuid);
        setPower(false);
    }

    public List<PotionEffectType> getPotionEffects() {
        return comedianEffects;
    }

    public PotionEffectType getLastPotionEffect() {
        return comedianEffects.get(comedianEffects.size() - 1);
    }

    public void addPotionEffect(PotionEffectType comedianEffect) {
        this.comedianEffects.add(comedianEffect);
    }


    @EventHandler
    public void onDay(DayEvent event) {

        if(!event.getUuid().equals(game.getGameUUID())){
            return;
        }

        if(!game.playerLG.get(getPlayerUUID()).isState(State.ALIVE)){
            return;
        }
        setPower(true);

        if(Bukkit.getPlayer(getPlayerUUID())==null){
            return;
        }
        Player player = Bukkit.getPlayer(getPlayerUUID());

        player.removePotionEffect(getLastPotionEffect());
        if (getPotionEffects().size() < 4) {
            player.sendMessage(game.translate("werewolf.role.comedian.wear_mask_message", game.score.conversion(game.config.getTimerValues().get(TimerLG.POWER_DURATION))));
        }
    }

    @Override
    public RoleLG getRoleEnum() {
        return RoleLG.COMEDIAN;
    }

    @Override
    public String getDescription() {
        return game.translate("werewolf.role.comedian.description");
    }

    @Override
    public String getDisplay() {
        return game.translate("werewolf.role.comedian.display");
    }
}
