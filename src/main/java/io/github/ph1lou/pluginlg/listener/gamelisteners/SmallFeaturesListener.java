package io.github.ph1lou.pluginlg.listener.gamelisteners;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.enumlg.Day;
import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import io.github.ph1lou.pluginlg.game.GameManager;
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
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class SmallFeaturesListener implements Listener {

    final MainLG main;
    final GameManager game;
    
    public SmallFeaturesListener(MainLG main, GameManager game) {
        this.main = main;
        this.game=game;
    }

    @EventHandler
    private void onPlayerTeleport(PlayerTeleportEvent event){

        if(!event.getPlayer().getWorld().equals(game.getWorld())) return;

        if(event.getCause().equals(PlayerTeleportEvent.TeleportCause.NETHER_PORTAL)){
            event.setCancelled(true);
        }
        else if(event.getCause().equals(PlayerTeleportEvent.TeleportCause.END_PORTAL)){
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onDrinkMilk(PlayerInteractEvent event) {

        if(!event.getPlayer().getWorld().equals(game.getWorld())) return;

        Action a = event.getAction();
        if (a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK) {

            if (event.getPlayer().getInventory().getItemInHand().getType() == Material.MILK_BUCKET) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onCraft(PrepareItemCraftEvent event) {

        if(!event.getViewers().isEmpty() && !event.getViewers().get(0).getWorld().equals(game.getWorld())) return;

        final CraftingInventory inv = event.getInventory();
        final ItemStack AIR = new ItemStack(Material.AIR);

        if(inv.getResult()==null) return;

        if (inv.getResult().getType() == Material.GOLDEN_APPLE && inv.getResult().getDurability() == 1) {
            inv.setResult(AIR);
        }
    }

    @EventHandler
    public void onAppleEat(PlayerItemConsumeEvent event) {

        if(!event.getPlayer().getWorld().equals(game.getWorld())) return;

        if (event.getItem().getType().equals(Material.GOLDEN_APPLE)) {

            if (event.getItem().getDurability() == 1) {
                event.setCancelled(true);
            }

            Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
                if (event.getItem().getDurability() == 1) {
                    event.getPlayer().getInventory().remove(event.getItem());
                    return;
                }
                if (game.config.getGoldenAppleParticles() == 2) {
                    return;
                }

                if (game.isDay(Day.NIGHT) && game.config.getGoldenAppleParticles() == 1) {
                    String playername = event.getPlayer().getName();
                    if (game.playerLG.containsKey(playername) && game.playerLG.get(playername).hasPower() && !game.playerLG.get(playername).isRole(RoleLG.PETITE_FILLE) && !game.playerLG.get(playername).isRole(RoleLG.LOUP_PERFIDE)) {
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

        if(!event.getWorld().equals(game.getWorld())) return;

        event.setCancelled(true);
        event.getWorld().setWeatherDuration(0);
        event.getWorld().setThundering(false);
    }

    @EventHandler
    public void onEnderManDeath(EntityDeathEvent event) {

        if(!event.getEntity().getWorld().equals(game.getWorld())) return;

        if (!event.getEntity().getType().equals(EntityType.ENDERMAN)) return;

        List<ItemStack> loots = event.getDrops();

        loots.clear();

        if (Math.random() * 100 < game.config.getPearlRate()) {
            loots.add(new ItemStack(Material.ENDER_PEARL));
        }
    }


}