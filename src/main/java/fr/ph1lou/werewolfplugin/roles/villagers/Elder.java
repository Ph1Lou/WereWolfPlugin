package fr.ph1lou.werewolfplugin.roles.villagers;



import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Camp;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.events.game.life_cycle.SecondDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.elder.ElderResurrectionEvent;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.impl.RoleVillage;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;


@Role(key = RoleBase.ELDER, category = Category.VILLAGER,
        attributes = RoleAttribute.VILLAGER)
public class Elder extends RoleVillage implements IPower {

    private boolean power = true;

    public Elder(WereWolfAPI api, IPlayerWW playerWW, String key) {
        super(api, playerWW, key);
    }

    @Override
    public void setPower(boolean power) {
        this.power = power;
    }

    @Override
    public boolean hasPower() {
        return (this.power);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.role.elder.description"))
                .setPower(game.translate(power ? "werewolf.role.elder.available" : "werewolf.role.elder.not_available"))
                .setEffects(game.translate("werewolf.role.elder.effect"))
                .build();
    }


    @Override
    public void recoverPower() {

    }


    @Override
    public void recoverPotionEffect() {

        if(!isAbilityEnabled()) return;

        this.getPlayerWW().addPotionModifier(PotionModifier
                .add(PotionEffectType.DAMAGE_RESISTANCE,"elder"));
    }

    @Override
    public Aura getDefaultAura() {
        return Aura.NEUTRAL;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSecondDeathEvent(SecondDeathEvent event) {

        if (event.isCancelled()) return;

        if (!event.getPlayerWW().equals(getPlayerWW())) return;

        Optional<IPlayerWW> killerWW = this.getPlayerWW().getLastKiller();

        if (!hasPower()) {
            return;
        }

        if (!isAbilityEnabled()) return;


        ElderResurrectionEvent elderResurrectionEvent =
                new ElderResurrectionEvent(this.getPlayerWW(),
                        killerWW.isPresent()
                                && killerWW.get()
                                .getRole().isCamp(Camp.VILLAGER));

        Bukkit.getPluginManager().callEvent(elderResurrectionEvent);
        setPower(false);

        if (elderResurrectionEvent.isCancelled()) {
            this.getPlayerWW().sendMessageWithKey(Prefix.RED , "werewolf.check.cancel");
        } else {
            if (elderResurrectionEvent.isKillerAVillager()) {
                killerWW.ifPresent(playerWW -> {
                    playerWW.removePlayerHealth(10);
                    playerWW.getRole().disableAbilities();
                    playerWW.sendMessageWithKey(Prefix.RED,"werewolf.role.elder.info_villager");
                });
            }
            else{
                killerWW.ifPresent(playerWW -> {
                    if(playerWW.getRole().isWereWolf()){
                        event.setCancelled(true);
                        game.resurrection(getPlayerWW());
                    }
                });
            }
        }
    }

    @Override
    public void disableAbilitiesRole() {

        this.getPlayerWW().addPotionModifier(PotionModifier.remove(PotionEffectType.DAMAGE_RESISTANCE,
                "elder",0));
    }
}
