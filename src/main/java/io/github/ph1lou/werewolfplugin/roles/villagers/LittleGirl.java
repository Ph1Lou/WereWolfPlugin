package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.Camp;
import io.github.ph1lou.werewolfapi.enumlg.Day;
import io.github.ph1lou.werewolfapi.enumlg.StateGame;
import io.github.ph1lou.werewolfapi.enumlg.StatePlayer;
import io.github.ph1lou.werewolfapi.events.*;
import io.github.ph1lou.werewolfapi.rolesattributs.InvisibleState;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesVillage;
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
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class LittleGirl extends RolesVillage implements InvisibleState {

    private boolean invisible = false;

    public LittleGirl(GetWereWolfAPI main, WereWolfAPI game, UUID uuid, String key) {
        super(main,game,uuid, key);
    }


    @EventHandler
    public void onNight(NightEvent event) {

        if (!game.getPlayersWW().get(getPlayerUUID()).isState(StatePlayer.ALIVE)) {
            return;
        }

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) {
            return;
        }

        player.sendMessage(game.translate("werewolf.role.little_girl.remove_armor"));
    }

    @EventHandler
    public void onDay(DayEvent event) {

        if (!game.getPlayersWW().get(getPlayerUUID()).isState(StatePlayer.ALIVE)) {
            return;
        }

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) {
            return;
        }

        if (!isInvisible()) return;

        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        player.removePotionEffect(PotionEffectType.WEAKNESS);
        setInvisible(false);
        Bukkit.getPluginManager().callEvent(new InvisibleEvent(getPlayerUUID(), false));
        player.sendMessage(game.translate("werewolf.role.little_girl.visible"));
    }

    @EventHandler
    public void onGoldenAppleEat(GoldenAppleParticleEvent event){

        event.setCancelled(true);

        if (!event.getPlayerUUID().equals(getPlayerUUID())) return;

        if (!isInvisible()) return;

        event.setCancelled(false);

    }


    @EventHandler
    public void onDayWillCome(DayWillComeEvent event) {


        if (!game.getPlayersWW().get(getPlayerUUID()).isState(StatePlayer.ALIVE)) {
            return;
        }

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) {
            return;
        }

        player.sendMessage(game.translate("werewolf.role.little_girl.soon_to_be_day"));
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) {
            return;
        }

        if (!game.isDay(Day.NIGHT)) {
            return;
        }

        PlayerWW plg = game.getPlayersWW().get(getPlayerUUID());

        if (!plg.isState(StatePlayer.ALIVE)) {
            return;
        }

        if(!isInvisible()){
            return;
        }

        for(UUID uuid:game.getPlayersWW().keySet()) {

            PlayerWW plg2 = game.getPlayersWW().get(uuid);
            Player player2 = Bukkit.getPlayer(uuid);

            if (player2 != null) {
                if (!uuid.equals(getPlayerUUID())) {
                    if (plg2.isState(StatePlayer.ALIVE)) {
                        if (plg2.getRole() instanceof InvisibleState) {
                            InvisibleState rolePower2 = (InvisibleState) plg2.getRole();

                            if (rolePower2.isInvisible()) {
                                if (plg2.getRole().isCamp(Camp.WEREWOLF)) {
                                    player.playEffect(player2.getLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
                                } else {
                                    player.playEffect(player2.getLocation(), Effect.STEP_SOUND, Material.LAPIS_BLOCK);
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    @Override
    public String getDescription() {
        return game.translate("werewolf.role.little_girl.description");
    }


    @Override
    public void stolen(@NotNull UUID uuid) {
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

    @Override
    public void recoverPotionEffect(@NotNull Player player) {
        super.recoverPotionEffect(player);
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));
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
    private void onCloseInvent(InventoryCloseEvent event) {

        Player player = (Player) event.getPlayer();
        UUID uuid = player.getUniqueId();

        if(!uuid.equals(getPlayerUUID())) return;

        Inventory inventory = player.getInventory();

        if (!game.isState(StateGame.GAME)) return;
        if (!game.isDay(Day.NIGHT)) return;
        if (!game.getPlayersWW().get(getPlayerUUID()).isState(StatePlayer.ALIVE)) return;

        if (inventory.getItem(36) == null && inventory.getItem(37) == null && inventory.getItem(38) == null && inventory.getItem(39) == null) {
            if (!isInvisible()) {
                player.sendMessage(game.translate("werewolf.role.little_girl.remove_armor_perform"));
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, Integer.MAX_VALUE, 0, false, false));
                if (getInfected()) {
                    player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                }
                setInvisible(true);
                Bukkit.getPluginManager().callEvent(new InvisibleEvent(uuid, true));
            }
        } else if (isInvisible()) {
            player.sendMessage(game.translate("werewolf.role.little_girl.visible"));
            if (getInfected()) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, -1, false, false));
            }
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
            player.removePotionEffect(PotionEffectType.WEAKNESS);
            setInvisible(false);
            Bukkit.getPluginManager().callEvent(new InvisibleEvent(uuid, false));
        }
    }


    @EventHandler
    public void onResurrection(ResurrectionEvent event){

        if(!event.getPlayerUUID().equals(getPlayerUUID())) return;

        setInvisible(false);
    }

}
