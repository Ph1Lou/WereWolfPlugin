package io.github.ph1lou.werewolfplugin.roles.neutrals;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enumlg.Day;
import io.github.ph1lou.werewolfapi.enumlg.StatePlayer;
import io.github.ph1lou.werewolfapi.events.DayEvent;
import io.github.ph1lou.werewolfapi.events.NightEvent;
import io.github.ph1lou.werewolfapi.events.StealEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesNeutral;
import io.github.ph1lou.werewolfapi.versions.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class WhiteWereWolf extends RolesNeutral {

    public WhiteWereWolf(GetWereWolfAPI main, PlayerWW playerWW, String key) {
        super(main, playerWW, key);
    }

    @EventHandler
    public void onNight(NightEvent event) {


        if (!getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) {
            return;
        }

        player.addPotionEffect(
                new PotionEffect(PotionEffectType.INCREASE_DAMAGE,
                        Integer.MAX_VALUE,
                        -1,
                        false,
                        false));
    }

    @EventHandler
    public void onDay(DayEvent event) {


        if (!getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) {
            return;
        }

        player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
    }

    @EventHandler
    public void onStealEvent(StealEvent event) {

        if (!event.getThiefWW().equals(getPlayerWW())) return;


        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) {
            return;
        }

        VersionUtils.getVersionUtils().setPlayerMaxHealth(player,
                VersionUtils.getVersionUtils().getPlayerMaxHealth(player) + 10);
    }

    @Override
    public void recoverPower() {


        Player player = Bukkit.getPlayer(getPlayerUUID());
        if (player == null) return;
        VersionUtils.getVersionUtils().setPlayerMaxHealth(player, 30);
    }

    @Override
    public void recoverPotionEffect() {

        super.recoverPotionEffect();

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) return;

        player.addPotionEffect(
                new PotionEffect(PotionEffectType.NIGHT_VISION,
                        Integer.MAX_VALUE,
                        0,
                        false,
                        false));

        if (game.isDay(Day.DAY)) return;

        player.addPotionEffect(new PotionEffect(
                PotionEffectType.INCREASE_DAMAGE,
                Integer.MAX_VALUE,
                -1,
                false,
                false));
    }

    @Override
    public @NotNull String getDescription() {
        return game.translate("werewolf.role.white_werewolf.description");
    }


    @Override
    public boolean isWereWolf() {
        return true;
    }

    @Override
    public boolean isNeutral(){
        return true;
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
        killer.addPotionEffect(new PotionEffect(
                PotionEffectType.ABSORPTION,
                1200,
                0,
                false,
                false));
    }
}
