package fr.ph1lou.werewolfplugin.roles.neutrals;


import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.UniversalEnchantment;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.game.utils.EnchantmentEvent;
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
import fr.ph1lou.werewolfapi.enums.UniversalPotionEffectType;
import org.jetbrains.annotations.NotNull;

@Role(key = RoleBase.SERIAL_KILLER,
        defaultAura = Aura.DARK,
        category = Category.NEUTRAL,
        attribute = RoleAttribute.NEUTRAL)
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
                .setEquipments(game.translate("werewolf.roles.serial_killer.limit"))
                .setItems(game.translate("werewolf.roles.serial_killer.items"))
                .setEffects(game.translate("werewolf.roles.serial_killer.effect"))
                .addExtraLines(game.translate("werewolf.roles.serial_killer.hearts", Formatter.format("&heart&", extraHeart / 2)))
                .build();
    }


    @Override
    public void recoverPower() {
        this.getPlayerWW().addPlayerMaxHealth(extraHeart);
    }

    @EventHandler
    public void onEnchantment(EnchantmentEvent event) {

        if (!event.getPlayerWW().equals(getPlayerWW())) return;

        ItemStack item = event.getItem();

        if (event.getEnchants().containsKey(UniversalEnchantment.PROTECTION)) {

            if (item.getType().equals(Material.DIAMOND_BOOTS) ||
                    item.getType().equals(Material.DIAMOND_LEGGINGS) ||
                    item.getType().equals(Material.DIAMOND_HELMET) ||
                    item.getType().equals(Material.DIAMOND_CHESTPLATE)) {
                event.getFinalEnchants().put(UniversalEnchantment.PROTECTION,
                        Math.min(event.getEnchants().get(
                                        UniversalEnchantment.PROTECTION),
                                game.getConfig().getLimitProtectionDiamond() + 1));
            } else {
                event.getFinalEnchants().put(UniversalEnchantment.PROTECTION,
                        Math.min(event.getEnchants().get(
                                        UniversalEnchantment.PROTECTION),
                                game.getConfig().getLimitProtectionIron() + 1));
            }
        }
        if (event.getEnchants().containsKey(UniversalEnchantment.SHARPNESS)) {
            if (item.getType().equals(Material.DIAMOND_SWORD)) {
                event.getFinalEnchants().put(UniversalEnchantment.SHARPNESS,
                        Math.min(event.getEnchants().get(UniversalEnchantment.SHARPNESS),
                                game.getConfig().getLimitSharpnessDiamond() + 1));
            } else {
                event.getFinalEnchants().put(UniversalEnchantment.SHARPNESS,
                        Math.min(event.getEnchants().get(UniversalEnchantment.SHARPNESS),
                                game.getConfig().getLimitSharpnessIron() + 1));
            }
        }
        if (event.getEnchants().containsKey(UniversalEnchantment.POWER)) {
            event.getFinalEnchants().put(UniversalEnchantment.POWER,
                    Math.min(event.getEnchants().get(UniversalEnchantment.POWER),
                            game.getConfig().getLimitPowerBow() + 1));
        }
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

        this.getPlayerWW().addPlayerMaxHealth(2);
        extraHeart += 2;
        this.getPlayerWW().addItem(new ItemStack(Material.GOLDEN_APPLE));
    }

    @Override
    public void disableAbilitiesRole() {

        if (this.hasPower()) {
            this.getPlayerWW().addPotionModifier(PotionModifier.remove(UniversalPotionEffectType.STRENGTH, this.getKey(), 0));
        }

    }

}
