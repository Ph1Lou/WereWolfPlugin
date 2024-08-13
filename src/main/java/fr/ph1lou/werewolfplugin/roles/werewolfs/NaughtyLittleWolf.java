package fr.ph1lou.werewolfplugin.roles.werewolfs;


import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.Day;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.NightEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.role.impl.RoleWereWolf;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import fr.ph1lou.werewolfapi.enums.UniversalPotionEffectType;
import org.jetbrains.annotations.NotNull;

@Role(key = RoleBase.NAUGHTY_LITTLE_WOLF,
        defaultAura = Aura.DARK,
        category = Category.WEREWOLF,
        attribute = RoleAttribute.WEREWOLF)
public class NaughtyLittleWolf extends RoleWereWolf {

    public NaughtyLittleWolf(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
    }


    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setEffects(game.translate("werewolf.roles.naughty_little_wolf.effect"))
                .build();
    }

    @Override
    public void recoverPower() {

    }


    @Override
    public void recoverPotionEffect() {
        if (game.isDay(Day.NIGHT)) {
            this.getPlayerWW().addPotionModifier(PotionModifier.add(UniversalPotionEffectType.SPEED, this.getKey()));
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onNight(NightEvent event) {

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (!isAbilityEnabled()) {
            return;
        }

        this.getPlayerWW().addPotionModifier(PotionModifier.add(UniversalPotionEffectType.SPEED, this.getKey()));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDay(DayEvent event) {
        this.getPlayerWW().addPotionModifier(PotionModifier.remove(UniversalPotionEffectType.SPEED, this.getKey(), 0));
    }

    @Override
    public void disableAbilitiesRole() {

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        this.getPlayerWW().addPotionModifier(PotionModifier.remove(UniversalPotionEffectType.SPEED, this.getKey(), 0));
    }


}
