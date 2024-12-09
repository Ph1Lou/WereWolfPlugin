package fr.ph1lou.werewolfplugin.listeners;

import fr.ph1lou.werewolfapi.events.game.utils.EnchantmentEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import fr.ph1lou.werewolfapi.enums.UniversalEnchantment;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EnchantmentListener implements Listener {

    private final WereWolfAPI game;


    public EnchantmentListener(WereWolfAPI game) {
        this.game = game;
    }

    @EventHandler
    public void onPrepareAnvilEvent(InventoryClickEvent event) {

        if (!event.getInventory().getType().equals(InventoryType.ANVIL)) return;
        if (event.getSlot() != 2) return;
        ItemStack current = event.getCurrentItem();

        if (current == null) return;
        if (current.getEnchantments().isEmpty()) {
            if (current.getType().equals(Material.ENCHANTED_BOOK)) {
                EnchantmentStorageMeta meta = (EnchantmentStorageMeta) current.getItemMeta();
                if (meta != null) {
                    event.setCurrentItem(checkEnchant(meta.getStoredEnchants(),
                            (Player) event.getWhoClicked(), current));
                }
            }
        } else event.setCurrentItem(checkEnchant(current.getEnchantments(),
                (Player) event.getWhoClicked(), current));
    }

    @EventHandler
    public void onItemEnchant(EnchantItemEvent event) {
        BukkitUtils.scheduleSyncDelayedTask(game, () -> event.getInventory().setItem(0, checkEnchant(event.getEnchantsToAdd(),
                event.getEnchanter(), event.getItem())));

    }


    private ItemStack checkEnchant(Map<Enchantment, Integer> enchant, Player player, ItemStack item) {

        Map<Enchantment, Integer> tempEnchant = new HashMap<>();
        ItemStack result = new ItemStack(item);
        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) return result;

        for (Enchantment e : enchant.keySet()) {

            result.removeEnchantment(e);
            if (UniversalEnchantment.KNOCKBACK.getEnchantment().equals(e)) {
                if (!game.getConfig().isKnockBackForInvisibleRoleOnly()) {
                    tempEnchant.put(e, Math.min(enchant.get(e),
                            game.getConfig().getLimitKnockBack()));
                }
            } else if (UniversalEnchantment.PROTECTION.getEnchantment().equals(e)) {

                if (item.getType().equals(Material.DIAMOND_BOOTS) ||
                        item.getType().equals(Material.DIAMOND_LEGGINGS) ||
                        item.getType().equals(Material.DIAMOND_HELMET) ||
                        item.getType().equals(Material.DIAMOND_CHESTPLATE)) {
                    tempEnchant.put(e, Math.min(enchant.get(e),
                            game.getConfig().getLimitProtectionDiamond()));
                } else {
                    tempEnchant.put(e, Math.min(enchant.get(e),
                            game.getConfig().getLimitProtectionIron()));
                }
            } else if (UniversalEnchantment.SHARPNESS.getEnchantment().equals(e)) {
                if (item.getType().equals(Material.DIAMOND_SWORD)) {
                    tempEnchant.put(e, Math.min(enchant.get(e),
                            game.getConfig().getLimitSharpnessDiamond()));
                } else {
                    tempEnchant.put(e, Math.min(enchant.get(e),
                            Math.min(enchant.get(e), game.getConfig().getLimitSharpnessIron())));
                }
            } else if (UniversalEnchantment.PUNCH.getEnchantment().equals(e)) {
                tempEnchant.put(e, Math.min(enchant.get(e), game.getConfig().getLimitPunch()));

            } else if (UniversalEnchantment.POWER.getEnchantment().equals(e)) {
                tempEnchant.put(e, Math.min(enchant.get(e), game.getConfig().getLimitPowerBow()));
            } else if (UniversalEnchantment.DEPTH_STRIDER.getEnchantment().equals(e)) {
                tempEnchant.put(e, Math.min(enchant.get(e), game.getConfig().getLimitDepthStrider()));
            } else tempEnchant.put(e, enchant.get(e));
        }

        EnchantmentEvent enchantEvent = new EnchantmentEvent(playerWW, result, UniversalEnchantment.getUniversalEnchantment(enchant), UniversalEnchantment.getUniversalEnchantment(tempEnchant));
        Bukkit.getPluginManager().callEvent(enchantEvent);

        if (!result.getType().equals(Material.ENCHANTED_BOOK) && !result.getType().equals(Material.BOOK)) {
            result.addUnsafeEnchantments(tempEnchant);
        } else {
            tempEnchant.entrySet().removeIf(enchantmentIntegerEntry -> enchantmentIntegerEntry.getValue() == 0);
            if (!tempEnchant.isEmpty()) {
                    result = new ItemStack(Material.ENCHANTED_BOOK);
                EnchantmentStorageMeta meta = (EnchantmentStorageMeta) result.getItemMeta();
                if (meta != null) {
                    for (Enchantment e : tempEnchant.keySet())
                        meta.addStoredEnchant(e, tempEnchant.get(e), false);
                    result.setItemMeta(meta);
                }
            } else {
                result = new ItemStack(Material.BOOK);
            }
        }
        return result;
    }

}
