package fr.ph1lou.werewolfplugin.roles.neutrals;

import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.Day;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.NightEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.game.utils.EndPlayerMessageEvent;
import fr.ph1lou.werewolfapi.events.roles.amnesiac.AmnesiacTransformationEvent;
import fr.ph1lou.werewolfapi.events.werewolf.NewWereWolfEvent;
import fr.ph1lou.werewolfapi.role.interfaces.ITransformed;
import fr.ph1lou.werewolfapi.role.impl.RoleNeutral;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

@Role(key = RoleBase.AMNESIAC_WEREWOLF,
        category = Category.NEUTRAL,
        attributes = RoleAttribute.HYBRID)
public class AmnesicWerewolf extends RoleNeutral implements ITransformed {

    private boolean transformed = false;

    public AmnesicWerewolf(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
    }

    @EventHandler
    public void onNight(NightEvent event) {

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (!isAbilityEnabled()) return;

        this.getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.INCREASE_DAMAGE,"werewolf"));

    }



    @EventHandler
    public void onDay(DayEvent event) {

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        this.getPlayerWW()
                .addPotionModifier(
                        PotionModifier.remove(PotionEffectType.INCREASE_DAMAGE,"werewolf",0));

    }

    @EventHandler
    public void onFinalDeath(FinalDeathEvent event) {

        IPlayerWW playerWW = event.getPlayerWW();

        if (!playerWW.getLastKiller().isPresent()) return;

        if (!playerWW.getLastKiller().get().equals(getPlayerWW())) return;

        if (this.isTransformed()) return;

        AmnesiacTransformationEvent amnesiacTransformationEvent =
                new AmnesiacTransformationEvent(this.getPlayerWW(), playerWW);

        Bukkit.getPluginManager().callEvent(amnesiacTransformationEvent);

        if (amnesiacTransformationEvent.isCancelled()) {
            this.getPlayerWW().sendMessageWithKey(Prefix.RED , "werewolf.check.transformation");
            return;
        }

        this.setTransformed(true);

        if (!super.isWereWolf()) {
            Bukkit.getPluginManager().callEvent(
                    new NewWereWolfEvent(getPlayerWW()));
        }

    }

    @Override
    public void recoverPotionEffect() {

        this.getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.NIGHT_VISION,"werewolf"));


        if (game.isDay(Day.DAY)) return;

        if (!isAbilityEnabled()) return;

        this.getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.INCREASE_DAMAGE,"werewolf"));

    }

    @Override
    public @NotNull String getDescription() {

        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.role.amnesiac_werewolf.description"))
                .setEffects(game.translate("werewolf.description.werewolf"))
                .build();
    }

    @Override
    public void recoverPower() {
    }

    @Override
    public boolean isTransformed() {
        return this.transformed;
    }

    @EventHandler
    public void onEndPlayerMessage(EndPlayerMessageEvent event) {

        if (!event.getPlayerWW().equals(getPlayerWW())) return;

        StringBuilder sb = event.getEndMessage();
        if(this.isTransformed()){
            sb.append(game.translate("werewolf.end.transform"));
        }
    }

    @Override
    public void setTransformed(boolean transformed) {
        this.transformed=transformed;
    }

    @Override
    public boolean isWereWolf() {
        return this.isTransformed() || super.isWereWolf();
    }

    @Override
    public void disableAbilitiesRole() {

        if(!this.getPlayerWW().isState(StatePlayer.ALIVE)){
            return;
        }

        this.getPlayerWW().addPotionModifier(PotionModifier.remove(PotionEffectType.INCREASE_DAMAGE,"werewolf",0));
    }

}
