package fr.ph1lou.werewolfplugin.roles.werewolfs;

import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.role.impl.RoleWereWolf;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;


@Role(key = RoleBase.BIG_BAD_WEREWOLF,
        category = Category.WEREWOLF,
        attributes = {RoleAttribute.WEREWOLF})
public class BigBadWerewolf extends RoleWereWolf implements IPower {

    private boolean power = true;

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
                .addPotionModifier(PotionModifier.remove(PotionEffectType.INCREASE_DAMAGE,
                        this.getKey(), 0));

    }

    @EventHandler
    public void onDeath(FinalDeathEvent event) {

        if (!this.hasPower()) {
            return;
        }

        if (event.getPlayerWW().getRole().isWereWolf()) {
            this.getPlayerWW().addPotionModifier(PotionModifier.remove(PotionEffectType.INCREASE_DAMAGE,
                    this.getKey(), 0));
            if (!this.getPlayerWW().isState(StatePlayer.DEATH)) {
                this.getPlayerWW().sendMessageWithKey(Prefix.ORANGE,
                        "werewolf.roles.big_bad_werewolf.werewolf_death");
            }
            this.setPower(false);
        }
    }

    @Override
    public void recoverPotionEffect() {
        if (this.isAbilityEnabled() && this.hasPower()) {
            this.getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.INCREASE_DAMAGE, this.getKey()));
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
