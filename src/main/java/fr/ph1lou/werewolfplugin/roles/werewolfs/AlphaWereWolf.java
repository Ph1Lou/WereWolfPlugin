package fr.ph1lou.werewolfplugin.roles.werewolfs;

import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.RequireAbility;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.Day;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.game.day_cycle.NightEvent;
import fr.ph1lou.werewolfapi.events.werewolf.WereWolfChatPrefixEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleWereWolf;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.bukkit.event.EventHandler;
import fr.ph1lou.werewolfapi.enums.UniversalPotionEffectType;
import org.jetbrains.annotations.NotNull;

@Role(key = RoleBase.ALPHA_WEREWOLF,
        defaultAura = Aura.DARK,
        category = Category.WEREWOLF,
        attribute = RoleAttribute.WEREWOLF,
        configValues = @IntValue(key = IntValueBase.ALPHA_ABSORPTION_AMPLIFIER,
                defaultValue = 0, meetUpValue = 0, step = 1, item = UniversalMaterial.GOLDEN_APPLE),
        timers = @Timer(key = TimerBase.ALPHA_ABSORPTION_DURATION,
                defaultValue = 300, meetUpValue = 300, step = 30))
public class AlphaWereWolf extends RoleWereWolf {

    public AlphaWereWolf(WereWolfAPI game, IPlayerWW playerWW) {
        super(game, playerWW);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.alpha_werewolf.description"))
                .setEffects(game.translate("werewolf.description.werewolf"))
                .setPower(game.translate("werewolf.roles.alpha_werewolf.effect"))
                .build();
    }

    @Override
    public void recoverPower() {
    }

    @Override
    public void recoverPotionEffect() {

        if (game.isDay(Day.NIGHT)) {
            if (!this.isAbilityEnabled()) {
                return;
            }
            this.getPlayerWW().addPotionModifier(PotionModifier.add(UniversalPotionEffectType.ABSORPTION, game.getConfig().getTimerValue(TimerBase.ALPHA_ABSORPTION_DURATION) * 20, game.getConfig().getValue(IntValueBase.ALPHA_ABSORPTION_AMPLIFIER), this.getKey()));
        }
    }

    @EventHandler
    @RequireAbility
    public void onNight(NightEvent event) {
        this.getPlayerWW().addPotionModifier(PotionModifier.add(UniversalPotionEffectType.ABSORPTION, 6000, 0, this.getKey()));
    }

    @EventHandler
    public void onChat(WereWolfChatPrefixEvent event) {

        if (!event.getPlayerWW().equals(this.getPlayerWW())) return;

        if (!event.getRequester().getRole().isWereWolf()) return;

        event.setPrefix("werewolf.roles.alpha_werewolf.prefix");

        event.addFormatter(Formatter.format("&alpha&", this.getPlayerWW().getName()));
    }
}
