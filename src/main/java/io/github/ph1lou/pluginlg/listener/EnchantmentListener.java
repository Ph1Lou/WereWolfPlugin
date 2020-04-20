package io.github.ph1lou.pluginlg.listener;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.PlayerLG;
import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import io.github.ph1lou.pluginlg.enumlg.ScenarioLG;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EnchantmentListener implements Listener {

    final MainLG main;


    public EnchantmentListener(MainLG main) {
        this.main=main;
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
        String playername = player.getName();
        if(!main.playerLG.containsKey(playername)) return item;
        PlayerLG plg = main.playerLG.get(playername);

        for(Enchantment e:enchant.keySet()){

            result.removeEnchantment(e);

            if(Arrays.asList(Enchantment.ARROW_FIRE,Enchantment.FIRE_ASPECT).contains(e)){
                if (!main.config.scenarioValues.get(ScenarioLG.NO_FIRE_WEAPONS)) {
                    tempEnchant.put(e, enchant.get(e));
                }
            }
            else if(Enchantment.KNOCKBACK.equals(e)){
                if(main.config.getLimitKnockBack()==2){
                    tempEnchant.put(e,enchant.get(e));
                }
                else if(main.config.getLimitKnockBack()==1 && (plg.isRole(RoleLG.PETITE_FILLE) || plg.isRole(RoleLG.LOUP_PERFIDE))){
                    tempEnchant.put(e,enchant.get(e));
                }
            }
            else if(Enchantment.PROTECTION_ENVIRONMENTAL.equals(e)){

                if (item.getType().equals(Material.DIAMOND_BOOTS) || item.getType().equals(Material.DIAMOND_LEGGINGS) ||  item.getType().equals(Material.DIAMOND_HELMET) ||  item.getType().equals(Material.DIAMOND_CHESTPLATE)){

                    if (!plg.isRole(RoleLG.TUEUR_EN_SERIE) && !plg.isRole(RoleLG.ASSASSIN)){
                        tempEnchant.put(e,Math.min(enchant.get(e),main.config.getLimitProtectionDiamond()));
                    }
                    else tempEnchant.put(e,Math.min(enchant.get(e),main.config.getLimitProtectionDiamond()+1));
                }
                else {
                    if (!plg.isRole(RoleLG.TUEUR_EN_SERIE) && !plg.isRole(RoleLG.ASSASSIN)){
                        tempEnchant.put(e,Math.min(enchant.get(e),main.config.getLimitProtectionIron()));
                    }
                    else tempEnchant.put(e,Math.min(enchant.get(e),main.config.getLimitProtectionIron()+1));
                }
            }
            else if(Enchantment.DAMAGE_ALL.equals(e)){
                if (item.getType().equals(Material.DIAMOND_SWORD)) {
                    if (!plg.isRole(RoleLG.TUEUR_EN_SERIE) && !plg.isRole(RoleLG.ASSASSIN)) {
                        tempEnchant.put(e, Math.min(enchant.get(e), main.config.getLimitSharpnessDiamond()));
                    } else tempEnchant.put(e, Math.min(enchant.get(e), main.config.getLimitSharpnessDiamond() + 1));
                } else {
                    if (!plg.isRole(RoleLG.TUEUR_EN_SERIE) && !plg.isRole(RoleLG.ASSASSIN)) {
                        tempEnchant.put(e, Math.min(enchant.get(e), main.config.getLimitSharpnessIron()));
                    } else
                        tempEnchant.put(e, Math.min(enchant.get(e), Math.min(4, main.config.getLimitSharpnessIron() + 1)));
                }
            }
            else if(Enchantment.ARROW_KNOCKBACK.equals(e)){
                if(main.config.getLimitPunch()==2){
                    tempEnchant.put(e,enchant.get(e));
                }
                else if(main.config.getLimitPunch()==1 && plg.isRole(RoleLG.CUPIDON)){
                    tempEnchant.put(e,enchant.get(e));
                }
            }
            else if(Enchantment.ARROW_DAMAGE.equals(e)){
                if (!plg.isRole(RoleLG.TUEUR_EN_SERIE) && !plg.isRole(RoleLG.ASSASSIN) && !plg.isRole(RoleLG.CUPIDON)){
                    tempEnchant.put(e,Math.min(enchant.get(e),main.config.getLimitPowerBow()));
                }
                else tempEnchant.put(e,Math.min(enchant.get(e),main.config.getLimitPowerBow()+1));
            }
            else tempEnchant.put(e,enchant.get(e));
        }

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
