package fr.ph1lou.werewolfplugin.roles.werewolfs;

import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.Sound;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
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
        attribute = RoleAttribute.WEREWOLF,
        configValues = {@IntValue(key = IntValueBase.AMNESIAC_WEREWOLF_PROXIMITY_RANGE,
                defaultValue = 10, meetUpValue = 10, step = 1, item = UniversalMaterial.COMPASS),
                @IntValue(key = IntValueBase.AMNESIAC_WEREWOLF_HOWLING_RANGE,
                        defaultValue = 50, meetUpValue = 50, step = 5, item = UniversalMaterial.SKELETON_SKULL)},
        timers = {@Timer(key = TimerBase.AMNESIAC_WEREWOLF_REVEAL_MIN,
                defaultValue = 70 * 60, meetUpValue = 70 * 60, step = 60),
                @Timer(key = TimerBase.AMNESIAC_WEREWOLF_REVEAL_MAX,
                        defaultValue = 90 * 60, meetUpValue = 90 * 60, step = 60)})
public class AmnesicWerewolf extends RoleWereWolf implements IPower {

    private boolean power = true;
    private int revealTime = Integer.MAX_VALUE;
    private final Set<IPlayerWW> knownWolves = new HashSet<>();

    public AmnesicWerewolf(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
    }

    @Override
    public void recoverPower() {
        int revealMin = game.getConfig().getTimerValue(TimerBase.AMNESIAC_WEREWOLF_REVEAL_MIN);
        int revealMax = game.getConfig().getTimerValue(TimerBase.AMNESIAC_WEREWOLF_REVEAL_MAX);
        this.revealTime = revealMin + game.getRandom().nextInt(Math.max(1, revealMax - revealMin + 1));
        this.knownWolves.add(getPlayerWW());
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.amnesiac_werewolf.description",
                        Formatter.format("&number&", g -> String.valueOf(g.getConfig().getTimerValue(TimerBase.AMNESIAC_WEREWOLF_REVEAL_MIN) / 60)),
                        Formatter.format("&number1&", g -> String.valueOf(g.getConfig().getTimerValue(TimerBase.AMNESIAC_WEREWOLF_REVEAL_MAX) / 60)),
                        Formatter.format("&number2&", game.getConfig().getValue(IntValueBase.AMNESIAC_WEREWOLF_PROXIMITY_RANGE))))
                .setEffects(game.translate("werewolf.description.werewolf"))
                .setPower(game.translate("werewolf.roles.amnesiac_werewolf.power",
                        Formatter.format("&number&", g -> String.valueOf(g.getConfig().getTimerValue(TimerBase.AMNESIAC_WEREWOLF_REVEAL_MIN) / 60)),
                        Formatter.format("&number1&", g -> String.valueOf(g.getConfig().getTimerValue(TimerBase.AMNESIAC_WEREWOLF_REVEAL_MAX) / 60)),
                        Formatter.format("&number2&", game.getConfig().getValue(IntValueBase.AMNESIAC_WEREWOLF_PROXIMITY_RANGE))))
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
                .filter(p -> p.distance(this.getPlayerWW()) < game.getConfig().getValue(IntValueBase.AMNESIAC_WEREWOLF_PROXIMITY_RANGE))
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
                .filter(p -> p.distance(this.getPlayerWW()) < game.getConfig().getValue(IntValueBase.AMNESIAC_WEREWOLF_HOWLING_RANGE))
                .count();

        game.getAlivePlayersWW().stream()
                .filter(p -> !p.equals(this.getPlayerWW()))
                .filter(p -> p.distance(this.getPlayerWW()) < game.getConfig().getValue(IntValueBase.AMNESIAC_WEREWOLF_HOWLING_RANGE))
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
