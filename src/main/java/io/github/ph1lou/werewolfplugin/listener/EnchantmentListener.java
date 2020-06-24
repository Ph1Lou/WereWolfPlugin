package io.github.ph1lou.werewolfplugin.listener;

import io.github.ph1lou.pluginlgapi.events.EnchantmentEvent;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EnchantmentListener implements Listener {

    final GameManager game;


    public EnchantmentListener(GameManager game) {
        this.game=game;
    }

    @EventHandler
    public void onPrepareAnvilEvent(InventoryClickEvent event) {

        if(event.getInventory() ==null) return;
        if(!event.getInventory().getType().equals(InventoryType.ANVIL)) return;
        if(event.getSlot()!=2) return;
        ItemStack current = event.getCurrentItem();
        if (current==null) return;
        if(current.getEnchantments().isEmpty()){
            if(current.getType().equals(Material.ENCHANTED_BOOK)){
                EnchantmentStorageMeta meta = (EnchantmentStorageMeta) current.getItemMeta();
                event.setCurrentItem(checkEnchant(meta.getStoredEnchants(),(Player) event.getWhoClicked(),current));
            }
        }
        else event.setCurrentItem(checkEnchant(current.getEnchantments(),(Player) event.getWhoClicked(),current));
    }

    @EventHandler
    public void onItemEnchant(EnchantItemEvent event) {
        event.getInventory().setItem(0,checkEnchant(event.getEnchantsToAdd(),event.getEnchanter(),event.getItem()));
    }


    private ItemStack checkEnchant(Map<Enchantment,Integer> enchant, Player player, ItemStack item){

        Map<Enchantment,Integer> tempEnchant = new HashMap<>();
        ItemStack result = new ItemStack(item);
        UUID uuid = player.getUniqueId();

        for(Enchantment e:enchant.keySet()){

            result.removeEnchantment(e);

            if(Enchantment.KNOCKBACK.equals(e)){
                if(game.getConfig().getLimitKnockBack()==2){
                    tempEnchant.put(e,enchant.get(e));
                }
            }
            else if(Enchantment.PROTECTION_ENVIRONMENTAL.equals(e)){

                if (item.getType().equals(Material.DIAMOND_BOOTS) || item.getType().equals(Material.DIAMOND_LEGGINGS) ||  item.getType().equals(Material.DIAMOND_HELMET) ||  item.getType().equals(Material.DIAMOND_CHESTPLATE)){
                    tempEnchant.put(e,Math.min(enchant.get(e),game.getConfig().getLimitProtectionDiamond()));
                }
                else {
                    tempEnchant.put(e,Math.min(enchant.get(e),game.getConfig().getLimitProtectionIron()));
                }
            }
            else if(Enchantment.DAMAGE_ALL.equals(e)){
                if (item.getType().equals(Material.DIAMOND_SWORD)) {
                    tempEnchant.put(e, Math.min(enchant.get(e), game.getConfig().getLimitSharpnessDiamond()));
                }
                else {
                    tempEnchant.put(e, Math.min(enchant.get(e), Math.min(enchant.get(e), game.getConfig().getLimitSharpnessIron())));
                }
            }
            else if(Enchantment.ARROW_KNOCKBACK.equals(e)){
                if(game.getConfig().getLimitPunch()==2){
                    tempEnchant.put(e,enchant.get(e));
                }
            }
            else if(Enchantment.ARROW_DAMAGE.equals(e)){
                tempEnchant.put(e,Math.min(enchant.get(e),game.getConfig().getLimitPowerBow()));
            }
            else tempEnchant.put(e,enchant.get(e));
        }

        EnchantmentEvent enchantEvent = new EnchantmentEvent(uuid,result,enchant,tempEnchant);
        Bukkit.getPluginManager().callEvent(enchantEvent);

        if(!result.getType().equals(Material.ENCHANTED_BOOK) && !result.getType().equals(Material.BOOK)){
            result.addUnsafeEnchantments(tempEnchant);
        }
        else{
            if(!tempEnchant.isEmpty()){
                result=new ItemStack(Material.ENCHANTED_BOOK);
                EnchantmentStorageMeta meta = (EnchantmentStorageMeta) result.getItemMeta();
                for(Enchantment e:tempEnchant.keySet())
                    meta.addStoredEnchant(e,tempEnchant.get(e),false);
                result.setItemMeta(meta);
            }
           else  result=new ItemStack(Material.BOOK);
        }
        return result;
    }

}
