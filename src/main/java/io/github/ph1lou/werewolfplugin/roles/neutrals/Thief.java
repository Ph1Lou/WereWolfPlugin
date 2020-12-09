package io.github.ph1lou.werewolfplugin.roles.neutrals;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.LoverAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enums.ConfigsBase;
import io.github.ph1lou.werewolfapi.enums.RolesBase;
import io.github.ph1lou.werewolfapi.enums.StateGame;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.*;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesNeutral;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Thief extends RolesNeutral implements AffectedPlayers, Power {

    private final List<PlayerWW> affectedPlayer = new ArrayList<>();

    public Thief(GetWereWolfAPI main, PlayerWW playerWW, String key) {
        super(main, playerWW, key);
    }

    private boolean power = true;

    @Override
    public void setPower(boolean power) {
        this.power = power;
    }

    @Override
    public boolean hasPower() {
        return (this.power);
    }

    @Override
    public void addAffectedPlayer(PlayerWW playerWW) {
        this.affectedPlayer.add(playerWW);
    }

    @Override
    public void removeAffectedPlayer(PlayerWW playerWW) {
        this.affectedPlayer.remove(playerWW);
    }

    @Override
    public void clearAffectedPlayer() {
        this.affectedPlayer.clear();
    }

    @Override
    public List<PlayerWW> getAffectedPlayers() {
        return (this.affectedPlayer);
    }


    @Override
    public @NotNull String getDescription() {
        return game.translate("werewolf.role.thief.description");
    }


    @Override
    public void recoverPower() {

    }

    @Override
    public void recoverPotionEffect() {

        super.recoverPotionEffect();

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) return;

        player.addPotionEffect(
                new PotionEffect(
                        PotionEffectType.DAMAGE_RESISTANCE,
                        Integer.MAX_VALUE,
                        0,
                        false,
                        false));
    }

    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent event) {

        if (event.getEntity().getKiller() == null) return;
        Player killer = event.getEntity().getKiller();

        if (!killer.getUniqueId().equals(getPlayerUUID())) return;

        killer.removePotionEffect(PotionEffectType.ABSORPTION);
        killer.addPotionEffect(new PotionEffect(
                PotionEffectType.SPEED,
                1200,
                0,
                false,
                false));
        killer.addPotionEffect(
                new PotionEffect(
                        PotionEffectType.ABSORPTION,
                        1200,
                        0,
                        false,
                        false));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onFirstDeathEvent(FirstDeathEvent event){


        PlayerWW playerWW = event.getPlayerWW();

        if (playerWW.getLastKiller() == null) return;

        if (!playerWW.getLastKiller().equals(getPlayerWW())) return;

        if(!hasPower())return;

        event.setCancelled(true);

        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin) main, () -> {
            if (!game.isState(StateGame.END)) {
                if (getPlayerWW().isState(StatePlayer.ALIVE)
                        && hasPower()) {
                    thiefRecoverRole(playerWW);
                } else {
                    Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin) main, () -> {
                        if (!game.isState(StateGame.END)) {
                            Bukkit.getPluginManager().callEvent(
                                    new FirstDeathEvent(playerWW));
                        }

                    }, 20L);
                }
            }

        },7*20);
    }


    public void thiefRecoverRole(PlayerWW playerWW) {

        Roles role = playerWW.getRole();
        Player killer = Bukkit.getPlayer(getPlayerUUID());
        boolean isInfected = getInfected();


        if (killer != null) {

            setPower(false);
            getPlayerWW().setThief(true);
            HandlerList.unregisterAll((Listener) getPlayerWW().getRole());
            Roles roleClone = role.publicClone();
            getPlayerWW().setRole(roleClone);
            Objects.requireNonNull(roleClone).setPlayerWW(getPlayerWW());
            Bukkit.getPluginManager().registerEvents((Listener) roleClone, (Plugin) main);

            if (isInfected) {
                roleClone.setInfected();
            } else if (roleClone.isWereWolf()) {
                Bukkit.getPluginManager().callEvent(new NewWereWolfEvent(getPlayerWW()));
            }

            killer.sendMessage(game.translate("werewolf.role.thief.realized_theft",
                    game.translate(role.getKey())));
            killer.sendMessage(game.translate("werewolf.announcement.review_role"));

            killer.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
            Bukkit.getPluginManager().callEvent(new StealEvent(getPlayerWW(),
                    playerWW,
                    roleClone.getKey()));


            getPlayerWW().getRole().recoverPotionEffect();

            boolean ok = true;

            for (LoverAPI loverAPI : getPlayerWW().getLovers()) {
                if (!loverAPI.isKey(RolesBase.LOVER.getKey())) {
                    ok = false;
                } else if (!game.getConfig().getConfigValues()
                        .get(ConfigsBase.POLYGAMY.getKey())) {
                    ok = false;
                }
            }

            if (ok) {
                for (LoverAPI loverAPI : playerWW.getLovers()) {
                    loverAPI.swap(playerWW, getPlayerWW());
                    getPlayerWW().getLovers().add(loverAPI);
                }
                playerWW.getLovers().clear();
            }
        }
        game.death(playerWW);
    }

    
    @EventHandler
    public void onDay(DayEvent event) {
        restoreResistance();
    }

    @EventHandler
    public void onNight(NightEvent event){
        restoreResistance();
    }


    public void restoreResistance() {

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (!hasPower()) return;

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (player == null) return;

        player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.DAMAGE_RESISTANCE,
                Integer.MAX_VALUE,
                0,
                false,
                false));
    }
}
