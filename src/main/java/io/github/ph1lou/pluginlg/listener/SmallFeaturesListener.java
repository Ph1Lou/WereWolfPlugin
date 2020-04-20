package io.github.ph1lou.pluginlg.listener;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.enumlg.Day;
import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class SmallFeaturesListener implements Listener {

    final MainLG main;

    public SmallFeaturesListener(MainLG main) {
        this.main = main;
    }


    @EventHandler
    public void onDrinkMilk(PlayerInteractEvent event) {
        Action a = event.getAction();
        if (a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK) {

            if (event.getPlayer().getInventory().getItemInHand().getType() == Material.MILK_BUCKET) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onCraft(PrepareItemCraftEvent event) {

        final CraftingInventory inv = event.getInventory();
        final ItemStack AIR = new ItemStack(Material.AIR);
        if (inv.getResult().getType() == Material.GOLDEN_APPLE && inv.getResult().getDurability() == 1) {
            inv.setResult(AIR);
        }
    }

    @EventHandler
    public void onAppleEat(PlayerItemConsumeEvent event) {

        if (event.getItem().getType().equals(Material.GOLDEN_APPLE)) {

            if (event.getItem().getDurability() == 1) {
                event.setCancelled(true);
            }

            Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
                if (event.getItem().getDurability() == 1) {
                    event.getPlayer().getInventory().remove(event.getItem());
                    return;
                }
                if (main.config.getGoldenAppleParticles() == 2) {
                    return;
                }

                if (main.isDay(Day.NIGHT) && main.config.getGoldenAppleParticles() == 1) {
                    String playername = event.getPlayer().getName();
                    if (main.playerLG.containsKey(playername) && main.playerLG.get(playername).hasPower() && !main.playerLG.get(playername).isRole(RoleLG.PETITE_FILLE) && !main.playerLG.get(playername).isRole(RoleLG.LOUP_PERFIDE)) {
                        return;
                    }
                }

                if (event.getPlayer().hasPotionEffect(PotionEffectType.ABSORPTION)) {
                    event.getPlayer().removePotionEffect(PotionEffectType.ABSORPTION);
                    event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 0, false, false));
                }
                if (event.getPlayer().hasPotionEffect(PotionEffectType.REGENERATION)) {
                    event.getPlayer().removePotionEffect(PotionEffectType.REGENERATION);
                    event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 180, 0, false, false));
                }

            }, 1L);
        }
    }

    @EventHandler
    public void WeatherChangeEvent(WeatherChangeEvent event) {
        event.setCancelled(true);
        event.getWorld().setWeatherDuration(0);
        event.getWorld().setThundering(false);
    }

    @EventHandler
    public void onEnderManDeath(EntityDeathEvent event) {

        if (!event.getEntity().getType().equals(EntityType.ENDERMAN)) return;

        List<ItemStack> loots = event.getDrops();

        loots.clear();

        if (Math.random() * 100 < main.config.getPearlRate()) {
            loots.add(new ItemStack(Material.ENDER_PEARL));
        }
    }


}