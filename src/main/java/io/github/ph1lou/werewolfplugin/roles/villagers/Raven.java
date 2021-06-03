package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.PotionModifier;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Aura;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.TimerBase;
import io.github.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import io.github.ph1lou.werewolfapi.events.game.vote.VoteEvent;
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

public class Raven extends RoleWithLimitedSelectionDuration implements IAffectedPlayers {

    private final List<IPlayerWW> affectedPlayer = new ArrayList<>();
    private IPlayerWW last;

    public Raven(WereWolfAPI api, IPlayerWW playerWW, String key) {

        super(api, playerWW, key);
        setPower(false);
    }

    @Override
    public void addAffectedPlayer(IPlayerWW playerWW) {
        this.affectedPlayer.add(playerWW);
        this.last = playerWW;
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
            this.last.addPotionModifier(PotionModifier.remove(PotionEffectType.SPEED,"raven"));

            this.last.getRole().removeAuraModifier("cursed");
            this.last.sendMessageWithKey("werewolf.role.raven.no_longer_curse");
            this.last = null;
        }

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        setPower(true);

        this.getPlayerWW().sendMessageWithKey("werewolf.role.raven.curse_message",
                Utils.conversion(
                        game.getConfig()
                                .getTimerValue(TimerBase.POWER_DURATION.getKey())));
    }


    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.role.raven.description"))
                .setItems(game.translate("werewolf.role.raven.item"))
                .setEffects(game.translate("werewolf.role.raven.effect"))
                .build();
    }


    @Override
    public void recoverPower() {
    }

    @Override
    public Aura getDefaultAura() {
        return Aura.DARK;
    }


    @EventHandler
    public void onVoteEvent(VoteEvent event) {

        if (!event.getPlayerWW().equals(getPlayerWW())) return;

        game.getVote().getVotes().merge(event.getTargetWW(), 1, Integer::sum);

    }

    @EventHandler
    private void onPlayerDamage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        UUID uuid = player.getUniqueId();

        if (!getPlayerUUID().equals(uuid)) return;

        if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
            event.setCancelled(true);
        }
    }
}
