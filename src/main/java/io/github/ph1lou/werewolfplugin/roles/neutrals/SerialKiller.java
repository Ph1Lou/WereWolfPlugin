package io.github.ph1lou.werewolfplugin.roles.neutrals;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.StatePlayer;
import io.github.ph1lou.werewolfapi.events.*;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesNeutral;
import io.github.ph1lou.werewolfapi.versions.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SerialKiller extends RolesNeutral implements Power {

    public SerialKiller(GetWereWolfAPI main, WereWolfAPI game, UUID uuid, String key) {
        super(main,game,uuid, key);
    }

    private boolean power = true;
    private int extraHeart = 0;
    @Override
    public void setPower(Boolean power) {
        this.power=power;
    }

    @Override
    public Boolean hasPower() {
        return(this.power);
    }

    @Override
    public @NotNull String getDescription() {
        return game.translate("werewolf.role.serial_killer.description");
    }


    @Override
    public void recoverPowerAfterStolen() {

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) {
            return;
        }

        VersionUtils.getVersionUtils().setPlayerMaxHealth(player,
                20 + extraHeart);

    }

    @EventHandler
    public void onEnchantment(EnchantmentEvent event){

        if(!event.getPlayerUUID().equals(getPlayerUUID())) return;

        ItemStack item = event.getItem();

        if(event.getEnchants().containsKey(Enchantment.PROTECTION_ENVIRONMENTAL)) {

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

    @EventHandler
    public void onDay(DayEvent event) {
        restoreStrength();
    }

    @EventHandler
    public void onNight(NightEvent event){
        restoreStrength();
    }


    public void restoreStrength() {

        if (!hasPower()) return;

        if (!game.getPlayersWW().get(getPlayerUUID()).isState(StatePlayer.ALIVE)) return;

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) return;

        player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE,
                Integer.MAX_VALUE,
                0,
                false,
                false));
    }


    @Override
    public void recoverPotionEffect() {

        super.recoverPotionEffect();

        if (!hasPower()) return;

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) return;

        player.addPotionEffect(
                new PotionEffect(PotionEffectType.INCREASE_DAMAGE,
                        Integer.MAX_VALUE,
                        -1,
                        false,
                        false));
    }

    @EventHandler
    public void onFinalDeath(FinalDeathEvent event) {

        UUID uuid = event.getUuid();
        Player killer = Bukkit.getPlayer(getPlayerUUID());
        PlayerWW target = game.getPlayersWW().get(uuid);

        if (target.getLastKiller() == null) return;

        if (!target.getLastKiller().equals(getPlayerUUID())) return;

        if (killer != null) {
            Bukkit.getPluginManager().callEvent(new SerialKillerEvent(
                    getPlayerUUID(),
                    uuid));
            VersionUtils.getVersionUtils().setPlayerMaxHealth(
                    killer,
                    VersionUtils.getVersionUtils().getPlayerMaxHealth(killer) + 2);
            extraHeart += 2;
            killer.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE));
            if (hasPower()) {
                killer.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                setPower(false);
            }
        }
    }

}
