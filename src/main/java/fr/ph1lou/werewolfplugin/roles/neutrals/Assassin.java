package fr.ph1lou.werewolfplugin.roles.neutrals;


import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.Day;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.UniversalPotionEffectType;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.NightEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.role.impl.RoleNeutral;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;


@Role(key = RoleBase.ASSASSIN,
        defaultAura = Aura.DARK,
        category = Category.NEUTRAL,
        attribute = RoleAttribute.NEUTRAL,
        sharpnessDiamondModifier = 1,
        sharpnessIronModifier = 1,
        protectionDiamondModifier = 1,
        protectionIronModifier = 1,
        powerModifier = 1)
public class Assassin extends RoleNeutral {

    public Assassin(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onNight(NightEvent event) {

        this.getPlayerWW()
                .addPotionModifier(PotionModifier.remove(UniversalPotionEffectType.STRENGTH, this.getKey(), 0));

    }

    @EventHandler
    public void onDay(DayEvent event) {

        if (!isAbilityEnabled()) return;

        this.getPlayerWW()
                .addPotionModifier(PotionModifier.add(UniversalPotionEffectType.STRENGTH, this.getKey()));

    }

    @Override
    public @NotNull String getDescription() {

        return new DescriptionBuilder(game, this)
                .setItems(game.translate("werewolf.roles.assassin.items"))
                .setEffects(game.translate("werewolf.roles.assassin.effect"))
                .build();
    }


    @Override
    public void recoverPower() {

    }


    @Override
    public void recoverPotionEffect() {

        if (game.isDay(Day.NIGHT)) return;

        if (!isAbilityEnabled()) return;

        this.getPlayerWW().addPotionModifier(PotionModifier.add(UniversalPotionEffectType.STRENGTH, this.getKey()));
    }

    @Override
    public void disableAbilitiesRole() {

        this.getPlayerWW().addPotionModifier(PotionModifier.remove(UniversalPotionEffectType.STRENGTH, this.getKey(), 0));
    }
}
