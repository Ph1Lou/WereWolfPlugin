package io.github.ph1lou.werewolfplugin.roles.werewolfs;

import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.PotionModifier;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Aura;
import io.github.ph1lou.werewolfapi.enums.Prefix;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IPower;
import io.github.ph1lou.werewolfapi.rolesattributs.RoleWereWolf;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class BigBadWerewolf extends RoleWereWolf implements IPower {

    private boolean power = true;

    public BigBadWerewolf(WereWolfAPI game, IPlayerWW playerWW, String key) {
        super(game, playerWW, key);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game,this)
                .setDescription(game.translate("werewolf.description.werewolf"))
                .setEffects(game.translate("werewolf.role.big_bad_werewolf.effects"))
                .build();
    }

    @Override
    public void recoverPower() {

    }

    @Override
    public void disableAbilities() {
        super.disableAbilities();
        this.getPlayerWW()
                .addPotionModifier(PotionModifier.remove(PotionEffectType.INCREASE_DAMAGE,
                        "big_bad_werewolf",0));

    }

    @EventHandler
    public void onDeath(FinalDeathEvent event){

        if(!this.hasPower()){
            return;
        }

        if(event.getPlayerWW().getRole().isWereWolf()){
            this.getPlayerWW().addPotionModifier(PotionModifier.remove(PotionEffectType.INCREASE_DAMAGE,
                    "big_bad_werewolf",0));
            if(!this.getPlayerWW().isState(StatePlayer.DEATH)){
                this.getPlayerWW().sendMessageWithKey(Prefix.ORANGE.getKey(),
                        "werewolf.role.big_bad_werewolf.werewolf_death");
            }
            this.setPower(false);
        }
    }

    @Override
    public void recoverPotionEffect() {
        if(this.isAbilityEnabled() && this.hasPower()){
            this.getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.INCREASE_DAMAGE,"big_bad_werewolf"));
        }
    }



    @Override
    public Aura getAura() {
        return Aura.DARK; //toujours dark
    }

    @Override
    public void setPower(boolean power) {
        this.power=power;
    }

    @Override
    public boolean hasPower() {
        return this.power;
    }
}
