package fr.ph1lou.werewolfplugin.roles.villagers;


import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.game.vote.VoteEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleWithLimitedSelectionDuration;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Role(key = RoleBase.RAVEN,
        category = Category.VILLAGER,
        attributes = RoleAttribute.VILLAGER)
public class Raven extends RoleWithLimitedSelectionDuration implements IAffectedPlayers {

    private final List<IPlayerWW> affectedPlayer = new ArrayList<>();
    private IPlayerWW last;

    public Raven(WereWolfAPI api, IPlayerWW playerWW) {

        super(api, playerWW);
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
            this.last.addPotionModifier(PotionModifier.remove(PotionEffectType.JUMP, this.getKey(), 0));

            this.last.getRole().removeAuraModifier(this.getKey());
            this.last.sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.raven.no_longer_curse");
            this.last = null;
        }

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        setPower(true);

        this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.raven.curse_message",
                Formatter.timer(game, TimerBase.POWER_DURATION));
    }


    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.raven.description"))
                .setItems(game.translate("werewolf.roles.raven.item"))
                .setEffects(game.translate("werewolf.roles.raven.effect"))
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

        game.getVoteManager().getVotes().merge(event.getTargetWW(), 1, Integer::sum);

    }

    @EventHandler
    private void onPlayerDamage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Player)) return;

        if (!isAbilityEnabled()) return;

        Player player = (Player) event.getEntity();
        UUID uuid = player.getUniqueId();

        if (!getPlayerUUID().equals(uuid)) return;

        if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
            event.setCancelled(true);
        }
    }
}
