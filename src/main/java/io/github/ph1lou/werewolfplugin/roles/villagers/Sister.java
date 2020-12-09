package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enums.RolesBase;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.FinalDeathEvent;
import io.github.ph1lou.werewolfapi.events.SisterDeathEvent;
import io.github.ph1lou.werewolfapi.events.UpdateEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesVillage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Objects;

public class Sister extends RolesVillage {

    public Sister(GetWereWolfAPI main, PlayerWW playerWW, String key) {
        super(main, playerWW, key);
    }


    @Override
    public @NotNull String getDescription() {

        StringBuilder list = new StringBuilder();

        game.getPlayerWW()
                .stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .filter(playerWW -> !playerWW.getRole().equals(this))
                .filter(playerWW -> playerWW.isKey(RolesBase.SISTER.getKey()))
                .forEach(playerWW -> list.append(playerWW.getName()).append(" "));

        return game.translate("werewolf.role.sister.description") +
                "\n§f" +
                game.translate("werewolf.role.sister.sisters_list", list.toString());
    }


    @Override
    public void recoverPower() {

    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {

        Player sister = Bukkit.getPlayer(getPlayerUUID());

        if (sister == null) {
            return;
        }
        if (!getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        Location location = sister.getLocation();

        boolean recoverResistance = game.getPlayerWW()
                .stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .map(PlayerWW::getRole)
                .filter(roles -> !roles.equals(this))
                .filter(roles -> roles.isKey(RolesBase.SISTER.getKey()))
                .map(Roles::getPlayerUUID)
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .filter(player -> {
                    try {
                        return location.distance(player.getLocation()) < 20;
                    } catch (Exception ignored) {
                        return false;
                    }
                })
                .findFirst()
                .orElse(null) != null;


        if (recoverResistance) {
            sister.getActivePotionEffects()
                    .stream()
                    .filter(potionEffect -> potionEffect.getDuration()
                            != Integer.MAX_VALUE)
                    .map(PotionEffect::getType)
                    .filter(potionEffectType -> potionEffectType.equals(
                            PotionEffectType.DAMAGE_RESISTANCE))
                    .forEach(sister::removePotionEffect);
            sister.addPotionEffect(new PotionEffect(
                    PotionEffectType.DAMAGE_RESISTANCE,
                    100,
                    0,
                    false,
                    false));
        }


    }

    @EventHandler
    public void onSisterDeathReveal(SisterDeathEvent event) {

        if (event.isCancelled()) return;

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) return;

        event.getAllSisters().add(getPlayerWW());

        if (player == null) return;

        PlayerWW sisterWW = event.getSister();
        PlayerWW killerWW = event.getKiller();


        player.sendMessage(game.translate("werewolf.role.sister.reveal_killer",
                sisterWW.getName(),
                killerWW != null ?
                        killerWW.getName() :
                        game.translate("werewolf.utils.pve")));

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSisterDeathRevealEnd(SisterDeathEvent event) {
        if (event.getAllSisters().isEmpty()) event.setCancelled(true);
    }

    @EventHandler
    public void onSisterDeath(FinalDeathEvent event) {

        PlayerWW playerWW = event.getPlayerWW();

        if (!playerWW.equals(getPlayerWW())) return;

        Bukkit.getPluginManager().callEvent(new SisterDeathEvent(playerWW,
                new HashSet<>(), getPlayerWW().getLastKiller()));

    }
}
