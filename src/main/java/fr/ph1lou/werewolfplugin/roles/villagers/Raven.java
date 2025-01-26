package fr.ph1lou.werewolfplugin.roles.villagers;


import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalPotionEffectType;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.game.vote.VoteEvent;
import fr.ph1lou.werewolfapi.events.game.vote.VoteResultEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleWithLimitedSelectionDuration;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.utils.Utils;
import fr.ph1lou.werewolfapi.vote.IVoteManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Role(key = RoleBase.RAVEN,
        auraDescriptionSpecialUseCase = "werewolf.roles.raven.aura",
        defaultAura = Aura.DARK,
        category = Category.VILLAGER,
        attribute = RoleAttribute.VILLAGER)
public class Raven extends RoleWithLimitedSelectionDuration implements IAffectedPlayers {

    private final List<IPlayerWW> affectedPlayer = new ArrayList<>();
    @Nullable
    private IPlayerWW last;

    @Nullable
    private IPlayerWW vote;

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
            this.last.addPotionModifier(PotionModifier.remove(UniversalPotionEffectType.JUMP_BOOST, this.getKey(), 0));
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
    public Set<IPlayerWW> getPlayersMet() {
        return game.getPlayersWW().stream()
                .filter(playerWW -> !playerWW.equals(this.getPlayerWW()))
                .collect(Collectors.toSet());
    }

    @EventHandler
    public void onVoteEvent(VoteEvent event) {

        if (!event.getPlayerWW().equals(getPlayerWW())) return;

        IVoteManager voteManager = game.getVoteManager();
        voteManager.setVotes(event.getTargetWW(), 1 + voteManager.getVotes(event.getTargetWW()));

        vote = event.getTargetWW();
    }

    @EventHandler
    public void onVoteEvent(VoteResultEvent event) {

        if (this.vote == null) {
            return;
        }

        if (!this.vote.equals(event.getPlayerWW())) return;

        Player player = Bukkit.getPlayer(this.getPlayerUUID());

        if (player == null) {
            return;
        }

        Utils.sendParticleArrow(player.getLocation(), player, Utils.getAngle(this.getPlayerWW().getLocation(), this.vote.getLocation()), 20);
    }

    @EventHandler
    public void onVoteEndEvent(VoteResultEvent event) {

        if (event.getPlayerWW() == null) {
            return;
        }

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (!Objects.equals(game.getVoteManager().getPlayerVote(this.getPlayerWW()).orElse(null), event.getPlayerWW())) {
            return;
        }

        this.getPlayerWW().sendMessageWithKey(Prefix.BLUE, "werewolf.roles.raven.raven_player_voted");
        this.getPlayerWW().addPotionModifier(PotionModifier.add(UniversalPotionEffectType.STRENGTH, 60 * 20, -1, this.getKey()));
        this.getPlayerWW().addPotionModifier(PotionModifier.add(UniversalPotionEffectType.SPEED, 60 * 20, -1, this.getKey()));
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
