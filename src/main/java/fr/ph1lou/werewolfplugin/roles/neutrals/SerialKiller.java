package fr.ph1lou.werewolfplugin.roles.neutrals;


import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.game.utils.EnchantmentEvent;
import fr.ph1lou.werewolfapi.events.roles.serial_killer.SerialKillerEvent;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.impl.RoleNeutral;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class SerialKiller extends RoleNeutral implements IPower {

    public SerialKiller(WereWolfAPI api, IPlayerWW playerWW, String key) {
        super(api, playerWW, key);
    }

    private boolean power = true;
    private int extraHeart = 0;

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
                .setPower(game.translate("werewolf.role.serial_killer.power"))
                .setEquipments(game.translate("werewolf.role.serial_killer.limit"))
                .setItems(game.translate("werewolf.role.serial_killer.items"))
                .setEffects(game.translate("werewolf.role.serial_killer.effect"))
                .addExtraLines(game.translate("werewolf.role.serial_killer.hearts", Formatter.format("&heart&",extraHeart / 2)))
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

        if (event.getEnchants().containsKey(Enchantment.PROTECTION_ENVIRONMENTAL)) {

            if (item.getType().equals(Material.DIAMOND_BOOTS) ||
                    item.getType().equals(Material.DIAMOND_LEGGINGS) ||
                    item.getType().equals(Material.DIAMOND_HELMET) ||
                    item.getType().equals(Material.DIAMOND_CHESTPLATE)) {
                event.getFinalEnchants().put(Enchantment.PROTECTION_ENVIRONMENTAL,
                        Math.min(event.getEnchants().get(
                                Enchantment.PROTECTION_ENVIRONMENTAL),
                                game.getConfig().getLimitProtectionDiamond() + 1));
            } else {
                event.getFinalEnchants().put(Enchantment.PROTECTION_ENVIRONMENTAL,
                        Math.min(event.getEnchants().get(
                                Enchantment.PROTECTION_ENVIRONMENTAL),
                                game.getConfig().getLimitProtectionIron() + 1));
            }
        }
        if(event.getEnchants().containsKey(Enchantment.DAMAGE_ALL)){
            if (item.getType().equals(Material.DIAMOND_SWORD)) {
                event.getFinalEnchants().put(Enchantment.DAMAGE_ALL,
                        Math.min(event.getEnchants().get(Enchantment.DAMAGE_ALL),
                                game.getConfig().getLimitSharpnessDiamond() + 1));
            }
            else {
                event.getFinalEnchants().put(Enchantment.DAMAGE_ALL,
                        Math.min(event.getEnchants().get(Enchantment.DAMAGE_ALL),
                                game.getConfig().getLimitSharpnessIron() + 1));
            }
        }
        if(event.getEnchants().containsKey(Enchantment.ARROW_DAMAGE)) {
            event.getFinalEnchants().put(Enchantment.ARROW_DAMAGE,
                    Math.min(event.getEnchants().get(Enchantment.ARROW_DAMAGE),
                            game.getConfig().getLimitPowerBow() + 1));
        }
    }


    @Override
    public void recoverPotionEffect() {

        if (!hasPower()) return;

        this.getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.INCREASE_DAMAGE,"serial_killer"));
    }

    @Override
    public Aura getDefaultAura() {
        return Aura.DARK;
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
            this.getPlayerWW().addPotionModifier(PotionModifier.remove(PotionEffectType.INCREASE_DAMAGE,"serial_killer",0));
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
            this.getPlayerWW().addPotionModifier(PotionModifier.remove(PotionEffectType.INCREASE_DAMAGE,"serial_killer",0));
        }

    }

}
