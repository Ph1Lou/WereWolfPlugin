package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.TimersBase;
import io.github.ph1lou.werewolfapi.events.DayEvent;
import io.github.ph1lou.werewolfapi.events.NightEvent;
import io.github.ph1lou.werewolfapi.events.StealEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesWithLimitedSelectionDuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Protector extends RolesWithLimitedSelectionDuration implements AffectedPlayers {

    private final List<PlayerWW> affectedPlayer = new ArrayList<>();
    private PlayerWW last;


    public Protector(GetWereWolfAPI main, PlayerWW playerWW, String key) {
        super(main, playerWW, key);
        setPower(false);
    }

    @Override
    public void addAffectedPlayer(PlayerWW playerWW) {
        this.affectedPlayer.add(playerWW);
        this.last = playerWW;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onNight(NightEvent event) {
        if (this.last == null) return;

        this.last.addPotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
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
                game.getScore().conversion(
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
        return super.getDescription() +
                game.translate("werewolf.description.description", game.translate("werewolf.role.protector.description")) +
                game.translate("werewolf.description.item", game.translate("werewolf.role.protector.items"));
    }


    @Override
    public void recoverPower() {

    }


    @EventHandler
    private void onPlayerDamage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        UUID uuid = player.getUniqueId();
        PlayerWW playerWW = game.getPlayerWW(uuid);

        if (playerWW == null) return;

        if (!playerWW.equals(last)) return;

        if (!event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) return;

        event.setCancelled(true);

    }
}