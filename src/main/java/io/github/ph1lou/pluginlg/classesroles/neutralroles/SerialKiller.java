package io.github.ph1lou.pluginlg.classesroles.neutralroles;


import io.github.ph1lou.pluginlgapi.GetWereWolfAPI;
import io.github.ph1lou.pluginlgapi.PlayerWW;
import io.github.ph1lou.pluginlgapi.WereWolfAPI;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import io.github.ph1lou.pluginlgapi.events.DayEvent;
import io.github.ph1lou.pluginlgapi.events.EnchantmentEvent;
import io.github.ph1lou.pluginlgapi.events.FinalDeathEvent;
import io.github.ph1lou.pluginlgapi.rolesattributs.Power;
import io.github.ph1lou.pluginlgapi.rolesattributs.RolesNeutral;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class SerialKiller extends RolesNeutral implements Power {

    public SerialKiller(GetWereWolfAPI main, WereWolfAPI game, UUID uuid) {
        super(main,game,uuid);
    }

    private boolean power=true;
    @Override
    public void setPower(Boolean power) {
        this.power=power;
    }

    @Override
    public Boolean hasPower() {
        return(this.power);
    }

    @Override
    public String getDescription() {
        return game.translate("werewolf.role.serial_killer.description");
    }

    @Override
    public String getDisplay() {
        return "werewolf.role.serial_killer.display";
    }

    @Override
    public void stolen(UUID uuid) {

        if(Bukkit.getPlayer(getPlayerUUID())==null){
            return;
        }

        Player player= Bukkit.getPlayer(getPlayerUUID());

        if (Bukkit.getPlayer(uuid)!=null) {
            player.setMaxHealth(Bukkit.getPlayer(uuid).getMaxHealth()+game.getPlayersWW().get(uuid).getLostHeart());
        }
    }

    @EventHandler
    public void onEnchantment(EnchantmentEvent event){

        if(!event.getPlayerUUID().equals(getPlayerUUID())) return;

        ItemStack item = event.getItem();

        if(event.getEnchants().containsKey(Enchantment.PROTECTION_ENVIRONMENTAL)){

            if (item.getType().equals(Material.DIAMOND_BOOTS) || item.getType().equals(Material.DIAMOND_LEGGINGS) ||  item.getType().equals(Material.DIAMOND_HELMET) ||  item.getType().equals(Material.DIAMOND_CHESTPLATE)){
                event.getFinalEnchants().put(Enchantment.PROTECTION_ENVIRONMENTAL,Math.min(event.getEnchants().get(Enchantment.PROTECTION_ENVIRONMENTAL),game.getConfig().getLimitProtectionDiamond()+1));
            }
            else {
                event.getFinalEnchants().put(Enchantment.PROTECTION_ENVIRONMENTAL,Math.min(event.getEnchants().get(Enchantment.PROTECTION_ENVIRONMENTAL),game.getConfig().getLimitProtectionIron()+1));
            }
        }
        if(event.getEnchants().containsKey(Enchantment.DAMAGE_ALL)){
            if (item.getType().equals(Material.DIAMOND_SWORD)) {
                event.getFinalEnchants().put(Enchantment.DAMAGE_ALL, Math.min(event.getEnchants().get(Enchantment.DAMAGE_ALL), game.getConfig().getLimitSharpnessDiamond()+1));
            }
            else {
                event.getFinalEnchants().put(Enchantment.DAMAGE_ALL, Math.min(event.getEnchants().get(Enchantment.DAMAGE_ALL), game.getConfig().getLimitSharpnessIron()+1));
            }
        }
        if(event.getEnchants().containsKey(Enchantment.ARROW_DAMAGE)){
            event.getFinalEnchants().put(Enchantment.ARROW_DAMAGE,Math.min(event.getEnchants().get(Enchantment.ARROW_DAMAGE),game.getConfig().getLimitPowerBow()+1));
        }
    }

    @EventHandler
    public void onDay(DayEvent event) {
        if(!hasPower()) return;

        if (!game.getPlayersWW().get(getPlayerUUID()).isState(State.ALIVE)) return;

        if(Bukkit.getPlayer(getPlayerUUID())==null){
            return;
        }

        Player player = Bukkit.getPlayer(getPlayerUUID());

        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, -1, false, false));
    }


    @Override
    public void recoverPotionEffect(Player player) {
        super.recoverPotionEffect(player);
        if(!hasPower()) return;
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE,Integer.MAX_VALUE,-1,false,false));
    }

    @EventHandler
    public void onFinalDeath(FinalDeathEvent event) {

        UUID uuid = event.getUuid();

        PlayerWW target = game.getPlayersWW().get(uuid);

        if(target.getLastKiller()==null) return;

        if(!target.getLastKiller().equals(getPlayerUUID())) return;

        if(Bukkit.getPlayer(getPlayerUUID())!=null){
            Player killer = Bukkit.getPlayer(getPlayerUUID());
            killer.setMaxHealth(killer.getMaxHealth()+2);
            killer.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE));
            if(hasPower()){
                killer.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                setPower(false);
            }
        }
    }

}
