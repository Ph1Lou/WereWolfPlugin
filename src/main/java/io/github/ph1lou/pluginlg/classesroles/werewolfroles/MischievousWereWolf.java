package io.github.ph1lou.pluginlg.classesroles.werewolfroles;


import io.github.ph1lou.pluginlgapi.GetWereWolfAPI;
import io.github.ph1lou.pluginlgapi.PlayerWW;
import io.github.ph1lou.pluginlgapi.WereWolfAPI;
import io.github.ph1lou.pluginlgapi.enumlg.Camp;
import io.github.ph1lou.pluginlgapi.enumlg.Day;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import io.github.ph1lou.pluginlgapi.enumlg.StateLG;
import io.github.ph1lou.pluginlgapi.events.*;
import io.github.ph1lou.pluginlgapi.rolesattributs.InvisibleState;
import io.github.ph1lou.pluginlgapi.rolesattributs.RolesWereWolf;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class MischievousWereWolf extends RolesWereWolf implements InvisibleState {

    private boolean invisible =false;

    public MischievousWereWolf(GetWereWolfAPI main, WereWolfAPI game, UUID uuid) {
        super(main,game,uuid);
    }


    @EventHandler
    public void onNight(NightEvent event) {


        if(!game.getPlayersWW().get(getPlayerUUID()).isState(State.ALIVE)){
            return;
        }

        if(Bukkit.getPlayer(getPlayerUUID())==null){
            return;
        }
        Player player = Bukkit.getPlayer(getPlayerUUID());
        player.sendMessage(game.translate("werewolf.role.little_girl.remove_armor"));
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, -1, false, false));
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {

        if(Bukkit.getPlayer(getPlayerUUID())==null){
            return;
        }

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (!game.isDay(Day.NIGHT)) {
            return;
        }

        PlayerWW plg = game.getPlayersWW().get(getPlayerUUID());

        if(!plg.isState(State.ALIVE)) {
            return;
        }

        if(!isInvisible()){
            return;
        }

        for(UUID uuid:game.getPlayersWW().keySet()){

            PlayerWW plg2 = game.getPlayersWW().get(uuid);

            if(Bukkit.getPlayer(uuid)!=null){
                Player player2 = Bukkit.getPlayer(uuid);
                if(!uuid.equals(getPlayerUUID())){
                    if(plg2.isState(State.ALIVE)){
                        if(plg2.getRole() instanceof InvisibleState){
                            InvisibleState rolePower2= (InvisibleState) plg2.getRole();

                            if(rolePower2.isInvisible()){
                                if (plg2.getRole().isCamp(Camp.WEREWOLF)) {
                                    player.playEffect(player2.getLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
                                }
                                else{
                                    player.playEffect(player2.getLocation(), Effect.STEP_SOUND, Material.LAPIS_BLOCK);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDay(DayEvent event) {

        if(!game.getPlayersWW().get(getPlayerUUID()).isState(State.ALIVE)){
            return;
        }

        if(Bukkit.getPlayer(getPlayerUUID())==null){
            return;
        }
        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (isInvisible()) {
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
            player.removePotionEffect(PotionEffectType.WEAKNESS);
            setInvisible(false);
            Bukkit.getPluginManager().callEvent(new InvisibleEvent(getPlayerUUID(),false));
            player.sendMessage(game.translate("werewolf.role.little_girl.visible"));
            game.updateNameTag();
        }
        else player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
    }

    @EventHandler
    public void onGoldenAppleEat(GoldenAppleParticleEvent event){

        if(!event.getPlayerUUID().equals(getPlayerUUID())) return;

        if (game.isDay(Day.NIGHT)) return;

        if(game.getConfig().getGoldenAppleParticles() != 1) return;


        if(!isInvisible()) return;

        event.setCancelled(true);

    }

    @EventHandler
    public void onEnchantment(EnchantmentEvent event){

        if(!event.getPlayerUUID().equals(getPlayerUUID())) return;

        if(game.getConfig().getLimitKnockBack()==1){
            if(event.getEnchants().containsKey(Enchantment.KNOCKBACK)){
                event.getFinalEnchants().put(Enchantment.KNOCKBACK,event.getEnchants().get(Enchantment.KNOCKBACK));
            }
        }
    }

    @EventHandler
    public void onDayWillCome(DayWillComeEvent event) {


        if(!game.getPlayersWW().get(getPlayerUUID()).isState(State.ALIVE)){
            return;
        }

        if(Bukkit.getPlayer(getPlayerUUID())==null){
            return;
        }
        Player player = Bukkit.getPlayer(getPlayerUUID());
        player.sendMessage(game.translate("werewolf.role.little_girl.soon_to_be_day"));
    }

    @EventHandler
    public void onFinalDeath(FinalDeathEvent event) {

        if (!event.getUuid().equals(getPlayerUUID())) return;

        PlayerWW plg = game.getPlayersWW().get(getPlayerUUID());

        setInvisible(false);

        if(game.getConfig().getLimitKnockBack()==2) return;

        for (ItemStack i : plg.getItemDeath()) {
            if (i != null) {
                i.removeEnchantment(Enchantment.KNOCKBACK);
            }
        }
    }


    @Override
    public String getDescription() {
        return game.translate("werewolf.role.mischievous_werewolf.description");
    }

    @Override
    public String getDisplay() {
        return "werewolf.role.mischievous_werewolf.display";
    }

    @Override
    public void stolen(UUID uuid) {
        setInvisible(false);
    }

    @Override
    public boolean isInvisible() {
        return this.invisible;
    }

    @Override
    public void setInvisible(boolean invisible) {
        this.invisible=invisible;
    }

    @EventHandler
    private void onCloseInvent(InventoryCloseEvent event) {

        Player player = (Player) event.getPlayer();
        UUID uuid = player.getUniqueId();

        if(!uuid.equals(getPlayerUUID())) return;

        Inventory inventory = player.getInventory();

        if (!game.isState(StateLG.GAME)) return;
        if (!game.isDay(Day.NIGHT)) return;


        if (inventory.getItem(36) == null && inventory.getItem(37) == null && inventory.getItem(38) == null && inventory.getItem(39) == null) {
            if (!isInvisible()) {
                player.sendMessage(game.translate("werewolf.role.little_girl.remove_armor_perform"));
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, Integer.MAX_VALUE, 0, false, false));
                player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                setInvisible(true);
                Bukkit.getPluginManager().callEvent(new InvisibleEvent(uuid,true));
                game.updateNameTag();
            }
        } else if (isInvisible()) {
            player.sendMessage(game.translate("werewolf.role.little_girl.visible"));
            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, -1, false, false));
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
            player.removePotionEffect(PotionEffectType.WEAKNESS);
            setInvisible(false);
            Bukkit.getPluginManager().callEvent(new InvisibleEvent(uuid,false));
            game.updateNameTag();
        }
    }
}
