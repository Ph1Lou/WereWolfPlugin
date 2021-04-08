package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.TimersBase;
import io.github.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import io.github.ph1lou.werewolfapi.events.game.day_cycle.NightEvent;
import io.github.ph1lou.werewolfapi.events.roles.StealEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.RoleWithLimitedSelectionDuration;
import io.github.ph1lou.werewolfapi.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Protector extends RoleWithLimitedSelectionDuration implements IAffectedPlayers {

    private final List<IPlayerWW> affectedPlayer = new ArrayList<>();
    private IPlayerWW last;


    public Protector(WereWolfAPI api, IPlayerWW playerWW, String key) {
        super(api, playerWW, key);
        setPower(false);
    }

    @Override
    public void addAffectedPlayer(IPlayerWW playerWW) {
        this.affectedPlayer.add(playerWW);
        this.last = playerWW;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onNight(NightEvent event) {
        if (this.last == null) return;

        this.last.addPotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
    }

    @Override
    public void removeAffectedPlayer(IPlayerWW playerWW) {
        this.affectedPlayer.remove(playerWW);
    }

    @Override
    public void clearAffectedPlayer() {
        this.affectedPlayer.clear();
    }

    @Override
    public List<IPlayerWW> getAffectedPlayers() {
        return (this.affectedPlayer);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDay(DayEvent event) {


        if (this.last != null) {


            this.last.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
            this.last.sendMessageWithKey("werewolf.role.protector.no_longer_protected");
            this.last = null;
        }


        if (!getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        setPower(true);

        getPlayerWW().sendMessageWithKey(
                "werewolf.role.protector.protection_message",
                Utils.conversion(
                        game.getConfig().getTimerValue(TimersBase.POWER_DURATION.getKey())));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onStealProtection(StealEvent event) {

        if (this.last == null) return;

        if (!event.getThiefWW().equals(this.last)) return;

        this.last.addPotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(() -> game.translate("werewolf.role.protector.description"))
                .setItems(() -> game.translate("werewolf.role.protector.items"))
                .build();
    }


    @Override
    public void recoverPower() {

    }


    @EventHandler
    private void onPlayerDamage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid);

        if (playerWW == null) return;

        if (!playerWW.equals(last)) return;

        if (!event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) return;

        event.setCancelled(true);

    }
}