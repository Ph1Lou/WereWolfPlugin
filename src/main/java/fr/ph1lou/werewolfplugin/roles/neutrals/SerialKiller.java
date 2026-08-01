package fr.ph1lou.werewolfplugin.roles.neutrals;


import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.enums.UniversalPotionEffectType;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.serial_killer.SerialKillerEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleNeutral;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Role(key = RoleBase.SERIAL_KILLER,
        defaultAura = Aura.DARK,
        category = Category.NEUTRAL,
        attribute = RoleAttribute.NEUTRAL,
        sharpnessDiamondModifier = 1,
        sharpnessIronModifier = 1,
        protectionDiamondModifier = 1,
        protectionIronModifier = 1,
        powerModifier = 1,
        configValues = {@IntValue(key = IntValueBase.SERIAL_KILLER_HEALTH_PER_KILL,
                defaultValue = 2, meetUpValue = 2, step = 1, item = UniversalMaterial.RED_DYE),
                @IntValue(key = IntValueBase.SERIAL_KILLER_GOLDEN_APPLES,
                        defaultValue = 1, meetUpValue = 1, step = 1, item = UniversalMaterial.GOLDEN_APPLE)})
public class SerialKiller extends RoleNeutral implements IPower {

    private boolean power = true;
    private int extraHeart = 0;

    public SerialKiller(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
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
                .setPower(game.translate("werewolf.roles.serial_killer.power"))
                .setItems(game.translate("werewolf.roles.serial_killer.items"))
                .setEffects(game.translate("werewolf.roles.serial_killer.effect"))
                .addExtraLines(game.translate("werewolf.roles.serial_killer.hearts", Formatter.format("&heart&", extraHeart / 2)))
                .build();
    }


    @Override
    public void recoverPower() {
        this.getPlayerWW().addPlayerMaxHealth(extraHeart);
    }


    @Override
    public void recoverPotionEffect() {

        if (!hasPower()) return;

        this.getPlayerWW().addPotionModifier(PotionModifier.add(UniversalPotionEffectType.STRENGTH, this.getKey()));
    }


    @EventHandler
    public void onFinalDeath(FinalDeathEvent event) {


        IPlayerWW playerWW = event.getPlayerWW();

        if (!playerWW.getLastKiller().isPresent()) return;

        if (!playerWW.getLastKiller().get().equals(getPlayerWW())) return;

        Bukkit.getPluginManager().callEvent(new SerialKillerEvent(
                this.getPlayerWW(),
                playerWW));
        if (hasPower()) {
            this.getPlayerWW().addPotionModifier(PotionModifier.remove(UniversalPotionEffectType.STRENGTH, this.getKey(), 0));
            setPower(false);
        }
        if (!isAbilityEnabled()) return;

        int healthPerKill = game.getConfig().getValue(IntValueBase.SERIAL_KILLER_HEALTH_PER_KILL);
        this.getPlayerWW().addPlayerMaxHealth(healthPerKill);
        extraHeart += healthPerKill;
        this.getPlayerWW().addItem(new ItemStack(Material.GOLDEN_APPLE,
                Math.max(1, game.getConfig().getValue(IntValueBase.SERIAL_KILLER_GOLDEN_APPLES))));
    }

    @Override
    public void disableAbilitiesRole() {

        if (this.hasPower()) {
            this.getPlayerWW().addPotionModifier(PotionModifier.remove(UniversalPotionEffectType.STRENGTH, this.getKey(), 0));
        }

    }

}
