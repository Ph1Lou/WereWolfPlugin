package fr.ph1lou.werewolfplugin.listeners;

import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.game.utils.GoldenAppleParticleEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfapi.versions.VersionUtils;
import fr.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SmallFeaturesListener implements Listener {

    public final static String GOLDEN_APPLE = "golden_apple";
    private final GameManager game;

    public SmallFeaturesListener(WereWolfAPI game) {
        this.game = (GameManager) game;
    }

    @EventHandler
    public void onBarrierEditionInventoryMode(InventoryCreativeEvent event) {

        if (event.getClickedInventory() == null) {
            return;
        }

        if (event.getCurrentItem() == null) {
            return;
        }

        if (event.getClickedInventory().getType() != InventoryType.PLAYER) {
            return;
        }

        if (event.getCurrentItem().getType() == Material.BARRIER) {

            if(event.getSlotType() != InventoryType.SlotType.ARMOR){
                return;
            }
            event.setCancelled(true);
            ((Player) event.getWhoClicked()).updateInventory();
        }

        if (event.getCursor().getType() == Material.BARRIER) {

            if(event.getSlotType() != InventoryType.SlotType.CONTAINER){
                return;
            }
            event.setCancelled(true);
            ((Player) event.getWhoClicked()).updateInventory();
        }

    }



    @EventHandler
    public void onDrinkMilk(PlayerInteractEvent event) {

        Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {

            if (VersionUtils.getVersionUtils()
                    .getItemInHand(event.getPlayer()).getType() == Material.MILK_BUCKET) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onCraft(PrepareItemCraftEvent event) {

        CraftingInventory inv = event.getInventory();
        ItemStack AIR = new ItemStack(Material.AIR);

        if (inv.getResult() == null) return;

        if (UniversalMaterial.ENCHANTED_GOLDEN_APPLE.getStack(
                inv.getResult().getAmount()).equals(inv.getResult())) {
            inv.setResult(AIR);
        }
    }

    @EventHandler
    public void onAppleEat(PlayerItemConsumeEvent event) {

        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem();
        IPlayerWW playerWW = game.getPlayerWW(player.getUniqueId()).orElse(null);

        if (playerWW == null) return;

        if (UniversalMaterial.ENCHANTED_GOLDEN_APPLE.getStack(
                itemStack.getAmount()).equals(itemStack)) {
            event.setCancelled(true);
            BukkitUtils.scheduleSyncDelayedTask(game, () ->
                    player.getInventory().remove(itemStack));

        } else if (event.getItem().getType().equals(Material.GOLDEN_APPLE)) {

            event.setCancelled(true);
            player.setFoodLevel(player.getFoodLevel() + 4);
            player.setSaturation(player.getSaturation() + 9.6f);

            ItemStack itemStack1 = VersionUtils.getVersionUtils().getItemInHand(player);

            if (itemStack1.getType() != Material.GOLDEN_APPLE) {
                return;
            }

            if (itemStack.getAmount() > 1) {
                itemStack.setAmount(itemStack.getAmount() - 1);
                VersionUtils.getVersionUtils().setItemInHand(player, itemStack);
            } else {
                VersionUtils.getVersionUtils().setItemInHand(player, null);
            }
            if (game.getConfig().getGoldenAppleParticles() == 2) {
                this.addGoldenPotionEffectsWithParticles(player);
            } else if (game.getConfig().getGoldenAppleParticles() == 1) {

                GoldenAppleParticleEvent goldenAppleParticleEvent =
                        new GoldenAppleParticleEvent(playerWW);

                Bukkit.getPluginManager().callEvent(goldenAppleParticleEvent);

                if (!goldenAppleParticleEvent.isCancelled()) {
                    this.addGoldenPotionEffectsWithParticles(player);
                }
            }

            this.setGoldenAppleEffects(playerWW);
        }
    }

    private void addGoldenPotionEffectsWithParticles(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));

    }

    private void setGoldenAppleEffects(IPlayerWW playerWW) {
        playerWW.addPotionModifier(PotionModifier.add(
                PotionEffectType.ABSORPTION,
                2400,
                0,
                GOLDEN_APPLE));

        playerWW.addPotionModifier(PotionModifier.add(
                PotionEffectType.REGENERATION,
                100,
                1,
                GOLDEN_APPLE));
    }

    @EventHandler
    public void WeatherChangeEvent(WeatherChangeEvent event) {
        event.setCancelled(true);
        event.getWorld().setWeatherDuration(0);
        event.getWorld().setThundering(false);
    }


    @EventHandler
    public void initWorld(WorldInitEvent event) {
        event.getWorld().setKeepSpawnInMemory(false); //for avoid crash when map change
    }

    @EventHandler
    public void onLoad(EntitySpawnEvent event){
        if(game.isState(StateGame.LOBBY)){
            event.setCancelled(true);
        }
    }
}