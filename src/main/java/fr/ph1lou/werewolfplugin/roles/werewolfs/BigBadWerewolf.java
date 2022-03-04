package fr.ph1lou.werewolfplugin.roles.werewolfs;

import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Prefix;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.impl.RoleWereWolf;
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
    public void disableAbilitiesRole() {
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
