package fr.ph1lou.werewolfplugin.roles.werewolfs;

import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.Sound;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.werewolf.AppearInWereWolfListEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleWereWolf;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

@Role(key = RoleBase.AMNESIAC_WEREWOLF,
        defaultAura = Aura.DARK,
        category = Category.WEREWOLF,
        attribute = RoleAttribute.WEREWOLF)
public class AmnesicWerewolf extends RoleWereWolf implements IPower {

    public static final double PROXIMITY_RANGE = 10d;
    public static final double HOWLING_RANGE = 50d;
    private static final int REVEAL_MIN = 70 * 60;
    private static final int REVEAL_MAX = 90 * 60;

    private boolean power = true;
    private int revealTime = REVEAL_MAX;
    private final Set<IPlayerWW> knownWolves = new HashSet<>();

    public AmnesicWerewolf(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
    }

    @Override
    public void recoverPower() {
        this.revealTime = REVEAL_MIN + game.getRandom().nextInt(REVEAL_MAX - REVEAL_MIN + 1);
        this.knownWolves.add(getPlayerWW());
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.amnesiac_werewolf.description"))
                .setEffects(game.translate("werewolf.description.werewolf"))
                .setPower(game.translate("werewolf.roles.amnesiac_werewolf.power"))
                .setCommand(game.translate("werewolf.roles.amnesiac_werewolf.command"))
                .build();
    }

    @Override
    public void second() {

        if (!this.isAbilityEnabled()) return;
        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;

        game.getAlivePlayersWW().stream()
                .filter(p -> !p.equals(this.getPlayerWW()))
                .filter(p -> p.getRole().isWereWolf())
                .filter(p -> p.distance(this.getPlayerWW()) < PROXIMITY_RANGE)
                .forEach(knownWolves::add);
    }

    @EventHandler
    public void onAppearInWereWolfList(AppearInWereWolfListEvent event) {

        if (event.getPlayerWW().equals(this.getPlayerWW())) {
            event.setAppear(this.knownWolves.contains(event.getTargetWW()));
            return;
        }

        if (event.getTargetWW().equals(this.getPlayerWW())
                && game.getTimer() < this.revealTime) {
            event.setAppear(false);
        }
    }

    public void howl() {

        int nearbyWolves = (int) game.getAlivePlayersWW().stream()
                .filter(p -> !p.equals(this.getPlayerWW()))
                .filter(p -> p.getRole().isWereWolf())
                .filter(p -> p.distance(this.getPlayerWW()) < HOWLING_RANGE)
                .count();

        game.getAlivePlayersWW().stream()
                .filter(p -> !p.equals(this.getPlayerWW()))
                .filter(p -> p.distance(this.getPlayerWW()) < HOWLING_RANGE)
                .forEach(p -> {
                    Sound.WOLF_HOWL.play(p);
                    p.sendMessageWithKey(Prefix.RED, "werewolf.roles.amnesiac_werewolf.howl_warn");
                });

        this.setPower(false);

        this.getPlayerWW().sendMessageWithKey(Prefix.GREEN,
                "werewolf.roles.amnesiac_werewolf.howl_perform",
                Formatter.number(nearbyWolves));
    }

    @Override
    public void setPower(boolean power) {
        this.power = power;
    }

    @Override
    public boolean hasPower() {
        return this.power;
    }
}
