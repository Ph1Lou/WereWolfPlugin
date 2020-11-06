package io.github.ph1lou.werewolfplugin.listeners;

import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.UniversalMaterial;
import io.github.ph1lou.werewolfapi.events.GoldenAppleParticleEvent;
import io.github.ph1lou.werewolfapi.versions.VersionUtils;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class SmallFeaturesListener implements Listener {

    private final Main main;
    private final WereWolfAPI game;

    public SmallFeaturesListener(Main main) {
        this.main = main;
        this.game = main.getWereWolfAPI();
    }

    @EventHandler
    private void onPlayerTeleport(PlayerTeleportEvent event) {

        if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.NETHER_PORTAL)) {
            event.setCancelled(true);
        }
        else if(event.getCause().equals(PlayerTeleportEvent.TeleportCause.END_PORTAL)){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrinkMilk(PlayerInteractEvent event) {

        Action a = event.getAction();
        if (a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK) {

            if (VersionUtils.getVersionUtils()
                    .getItemInHand(event.getPlayer()).getType() == Material.MILK_BUCKET) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onCraft(PrepareItemCraftEvent event) {

        final CraftingInventory inv = event.getInventory();
        final ItemStack AIR = new ItemStack(Material.AIR);

        if (inv.getResult() == null) return;

        if (UniversalMaterial.ENCHANTED_GOLDEN_APPLE.getStack(
                inv.getResult().getAmount()).equals(inv.getResult())) {
            inv.setResult(AIR);
        }
    }

    @EventHandler
    public void onAppleEat(PlayerItemConsumeEvent event) {

        if (UniversalMaterial.ENCHANTED_GOLDEN_APPLE.getStack(
                event.getItem().getAmount()).equals(event.getItem())) {
            event.setCancelled(true);
            Bukkit.getScheduler().scheduleSyncDelayedTask(main, () ->
                    event.getPlayer().getInventory().remove(event.getItem()));

        } else if (event.getItem().getType().equals(Material.GOLDEN_APPLE)) {

            Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {

                if (game.getConfig().getGoldenAppleParticles() == 2) {
                    return;
                }

                if (game.getConfig().getGoldenAppleParticles() == 1) {
                    GoldenAppleParticleEvent goldenAppleParticleEvent =
                            new GoldenAppleParticleEvent(event.getPlayer().getUniqueId());

                    Bukkit.getPluginManager().callEvent(goldenAppleParticleEvent);

                    if (event.isCancelled()) return;
                }

                Player player = event.getPlayer();

                if (player.hasPotionEffect(PotionEffectType.ABSORPTION)) {
                    player.removePotionEffect(PotionEffectType.ABSORPTION);
                    player.addPotionEffect(new PotionEffect(
                            PotionEffectType.ABSORPTION,
                            2400,
                            0,
                            false,
                            false));
                }
                if (player.hasPotionEffect(PotionEffectType.REGENERATION)) {
                    player.removePotionEffect(PotionEffectType.REGENERATION);
                    player.addPotionEffect(new PotionEffect(
                            PotionEffectType.REGENERATION,
                            180,
                            0,
                            false,
                            false));
                }

                event.setCancelled(true);
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

        if (Math.random() * 100 < game.getConfig().getPearlRate()) {
            loots.add(new ItemStack(Material.ENDER_PEARL));
        }
    }

}