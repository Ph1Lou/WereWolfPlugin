package fr.ph1lou.werewolfplugin.roles.villagers;


import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.UniversalPotionEffectType;
import fr.ph1lou.werewolfapi.events.buildings.UrnVoteCastEvent;
import fr.ph1lou.werewolfapi.events.buildings.UrnVoteResultEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleImpl;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.stream.Collectors;

@Role(key = RoleBase.RAVEN,
        defaultAura = Aura.DARK,
        category = Category.VILLAGER,
        attribute = RoleAttribute.VILLAGER)
public class Raven extends RoleImpl {

    /** Weakness I duration applied on the last urn-vote session (ticks). */
    private static final int WEAKNESS_DURATION = 10 * 60 * 20;

    /** Index of the last urn-vote session (UrnVote1/2/3StartEvent => 3 sessions). */
    private static final int LAST_URN_SESSION = 3;

    @Nullable
    private IPlayerWW ravenVote;

    public Raven(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.raven.description"))
                .build();
    }

    @Override
    public void recoverPower() {
    }

    /**
     * The Raven may vote for anyone, regardless of whether they have actually
     * met them during the game.
     */
    @Override
    public Set<IPlayerWW> getPlayersMet() {
        return game.getPlayersWW().stream()
                .filter(playerWW -> !playerWW.equals(this.getPlayerWW()))
                .collect(Collectors.toSet());
    }

    /**
     * Remember the target the Raven voted for during the current urn-vote session
     * (remote vote via {@code /lg vote} or any other cast path).
     */
    @EventHandler
    public void onUrnVoteCast(UrnVoteCastEvent event) {

        if (event.isBlank()) return;
        if (!event.getPlayerWW().equals(getPlayerWW())) return;

        this.ravenVote = event.getTargetWW();
    }

    /**
     * When the player elected by the urn vote is the one the Raven voted for,
     * apply the penalty:
     * <ul>
     *     <li>not the last session: the elected player's next ballot is discarded at tally;</li>
     *     <li>last session of the game: the elected player gets Weakness I for 10 minutes instead.</li>
     * </ul>
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onUrnVoteResult(UrnVoteResultEvent event) {

        if (event.isCancelled()) return;

        IPlayerWW elected = this.ravenVote;
        this.ravenVote = null;

        if (elected == null) return;
        if (!elected.equals(event.getPlayerWW())) return;

        if (event.getSession() >= LAST_URN_SESSION) {
            elected.addPotionModifier(PotionModifier.add(UniversalPotionEffectType.WEAKNESS,
                    WEAKNESS_DURATION, 0, this.getKey()));
            elected.sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.raven.weakness_applied");
            this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.raven.penalty_last_vote",
                    Formatter.player(elected.getName()));
        } else {
            game.getBuildingsManager().getUrnVoteManager()
                    .markBallotDiscarded(elected, event.getSession() + 1);
            this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.raven.penalty_next_vote",
                    Formatter.player(elected.getName()));
        }
    }
}
