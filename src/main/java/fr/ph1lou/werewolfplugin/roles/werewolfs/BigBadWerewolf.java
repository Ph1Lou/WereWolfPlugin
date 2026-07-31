package fr.ph1lou.werewolfplugin.roles.werewolfs;

import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.werewolf.RequestStrengthRateEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.role.impl.RoleWereWolf;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.bukkit.event.EventHandler;
import fr.ph1lou.werewolfapi.enums.UniversalPotionEffectType;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;


@Role(key = RoleBase.BIG_BAD_WEREWOLF,
        defaultAura = Aura.DARK,
        auraDescriptionSpecialUseCase = "werewolf.roles.big_bad_werewolf.aura",
        category = Category.WEREWOLF,
        attribute = RoleAttribute.WEREWOLF)
public class BigBadWerewolf extends RoleWereWolf implements IPower {

    private boolean power = true;
    private int strengthLevel = 2;
    private boolean wolfDiedThisCycle = false;

    public BigBadWerewolf(WereWolfAPI game, IPlayerWW playerWW) {
        super(game, playerWW);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.description.werewolf"))
                .setEffects(game.translate("werewolf.roles.big_bad_werewolf.effects"))
                .build();
    }

    @Override
    public void recoverPower() {

    }

    @Override
    public void disableAbilitiesRole() {
        this.getPlayerWW()
                .addPotionModifier(PotionModifier.remove(UniversalPotionEffectType.STRENGTH,
                        this.getKey(), 0));

    }

    @EventHandler
    public void onDeath(FinalDeathEvent event) {

        if (!this.isAbilityEnabled()) {
            return;
        }

        if (event.getPlayerWW().equals(this.getPlayerWW())) {
            return;
        }

        if (event.getPlayerWW().getRole().isWereWolf()) {
            wolfDiedThisCycle = true;
            strengthLevel = 0;
            this.getPlayerWW().addPotionModifier(PotionModifier.remove(UniversalPotionEffectType.STRENGTH,
                    this.getKey(), 0));
            if (!this.getPlayerWW().isState(StatePlayer.DEATH)) {
                this.getPlayerWW().sendMessageWithKey(Prefix.ORANGE,
                        "werewolf.roles.big_bad_werewolf.werewolf_death");
            }
        }
    }

    @EventHandler
    public void onDay(DayEvent event) {

        if (!this.isAbilityEnabled()) {
            return;
        }

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (wolfDiedThisCycle) {
            wolfDiedThisCycle = false;
            strengthLevel = 0;
            this.getPlayerWW().addPotionModifier(PotionModifier.remove(UniversalPotionEffectType.STRENGTH,
                    this.getKey(), 0));
        } else if (strengthLevel == 0) {
            strengthLevel = 1;
            this.getPlayerWW().addPotionModifier(PotionModifier.add(UniversalPotionEffectType.STRENGTH, this.getKey()));
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onStrengthRate(RequestStrengthRateEvent event) {
        if (strengthLevel != 1) return;
        if (!event.getPlayerWW().equals(this.getPlayerWW())) return;
        event.setStrengthRate(event.getStrengthRate() / 2);
    }

    @Override
    public void recoverPotionEffect() {
        if (this.isAbilityEnabled() && strengthLevel >= 1) {
            this.getPlayerWW().addPotionModifier(PotionModifier.add(UniversalPotionEffectType.STRENGTH, this.getKey()));
        }
    }


    @Override
    public Aura getAura() {
        return Aura.DARK; //toujours dark
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
