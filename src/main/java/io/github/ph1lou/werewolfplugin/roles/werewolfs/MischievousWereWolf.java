package io.github.ph1lou.werewolfplugin.roles.werewolfs;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.Day;
import io.github.ph1lou.werewolfapi.enumlg.StateGame;
import io.github.ph1lou.werewolfapi.enumlg.StatePlayer;
import io.github.ph1lou.werewolfapi.events.*;
import io.github.ph1lou.werewolfapi.rolesattributs.InvisibleState;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesWereWolf;
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
import org.javatuples.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class MischievousWereWolf extends RolesWereWolf implements InvisibleState {

    private boolean invisible = false;

    public MischievousWereWolf(GetWereWolfAPI main, WereWolfAPI game, UUID uuid, String key) {
        super(main, game, uuid, key);
    }

    @EventHandler
    public void onUpdateNameTag(UpdatePlayerNameTag event) {

        if (!event.getPlayerUUID().equals(getPlayerUUID())) return;

        if (event.isVisibility()) {
            event.setVisibility(!invisible);
        }
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

        player.sendMessage(game.translate(
                "werewolf.role.little_girl.remove_armor"));
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.INCREASE_DAMAGE,
                Integer.MAX_VALUE,
                -1,
                false,
                false));
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

        if (!isInvisible()) {
            return;
        }

        game.getPlayersWW().values()
                .stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .map(PlayerWW::getRole)
                .filter(roles -> !roles.equals(this))
                .filter(roles -> roles instanceof InvisibleState)
                .map(roles -> (InvisibleState) roles)
                .filter(InvisibleState::isInvisible)
                .map(invisibleState -> {
                    if (((Roles) invisibleState).isWereWolf()) {
                        return new Pair<>(Material.REDSTONE_BLOCK,
                                ((Roles) invisibleState).getPlayerUUID());
                    } else return new Pair<>(Material.LAPIS_BLOCK,
                            ((Roles) invisibleState).getPlayerUUID());
                })
                .map(objects -> new Pair<>(objects.getValue0(),
                        Bukkit.getPlayer(objects.getValue1())))
                .filter(objects -> objects.getValue1() != null)
                .map(objects -> new Pair<>(objects.getValue0(),
                        objects.getValue1().getLocation()))
                .forEach(objects -> player.playEffect(objects.getValue1(),
                        Effect.STEP_SOUND, objects.getValue0()));
    }

    @EventHandler
    public void onDay(DayEvent event) {


        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (!game.getPlayersWW().get(getPlayerUUID()).isState(StatePlayer.ALIVE)) {
            return;
        }

        if (player == null) {
            return;
        }

        if (isInvisible()) {
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
            player.removePotionEffect(PotionEffectType.WEAKNESS);
            setInvisible(false);
            Bukkit.getPluginManager().callEvent(new InvisibleEvent(
                    getPlayerUUID(),
                    false));
            player.sendMessage(game.translate(
                    "werewolf.role.little_girl.visible"));
        }
        else player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
    }


    @EventHandler
    public void onGoldenAppleEat(GoldenAppleParticleEvent event) {

        event.setCancelled(true);

        if (!event.getPlayerUUID().equals(getPlayerUUID())) return;

        if (!isInvisible()) return;

        if (game.isDay(Day.DAY)) return;

        event.setCancelled(false);

    }

    @EventHandler
    public void onEnchantment(EnchantmentEvent event){

        if(!event.getPlayerUUID().equals(getPlayerUUID())) return;

        if(game.getConfig().getLimitKnockBack()==1){
            if(event.getEnchants().containsKey(Enchantment.KNOCKBACK)) {
                event.getFinalEnchants().put(
                        Enchantment.KNOCKBACK,
                        event.getEnchants().get(Enchantment.KNOCKBACK));
            }
        }
    }

    @EventHandler
    public void onDayWillCome(DayWillComeEvent event) {

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (!game.getPlayersWW().get(getPlayerUUID()).isState(StatePlayer.ALIVE)) {
            return;
        }

        if (player == null) {
            return;
        }

        player.sendMessage(game.translate(
                "werewolf.role.little_girl.soon_to_be_day"));
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
    public @NotNull String getDescription() {
        return game.translate("werewolf.role.mischievous_werewolf.description");
    }


    @Override
    public void recoverPowerAfterStolen() {
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

        if (!uuid.equals(getPlayerUUID())) return;

        Inventory inventory = player.getInventory();

        if (!game.isState(StateGame.GAME)) return;
        if (!game.isDay(Day.NIGHT)) return;
        if (!game.getPlayersWW().get(getPlayerUUID())
                .isState(StatePlayer.ALIVE)) return;

        if (inventory.getItem(36) == null &&
                inventory.getItem(37) == null &&
                inventory.getItem(38) == null &&
                inventory.getItem(39) == null) {
            if (!isInvisible()) {
                player.sendMessage(game.translate(
                        "werewolf.role.little_girl.remove_armor_perform"));
                player.addPotionEffect(new PotionEffect(
                        PotionEffectType.INVISIBILITY,
                        Integer.MAX_VALUE,
                        0,
                        false,
                        false));
                player.addPotionEffect(new PotionEffect(
                        PotionEffectType.WEAKNESS,
                        Integer.MAX_VALUE,
                        0,
                        false,
                        false));
                player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                setInvisible(true);
                Bukkit.getPluginManager().callEvent(
                        new InvisibleEvent(uuid, true));
            }
        } else if (isInvisible()) {
            player.sendMessage(game.translate("werewolf.role.little_girl.visible"));
            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, -1, false, false));
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
            player.removePotionEffect(PotionEffectType.WEAKNESS);
            setInvisible(false);
            Bukkit.getPluginManager().callEvent(
                    new InvisibleEvent(uuid, false));
        }
    }

    @EventHandler
    public void onResurrection(ResurrectionEvent event){

        if(!event.getPlayerUUID().equals(getPlayerUUID())) return;

        setInvisible(false);

    }
}
