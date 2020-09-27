package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.State;
import io.github.ph1lou.werewolfapi.events.FinalDeathEvent;
import io.github.ph1lou.werewolfapi.events.SisterDeathEvent;
import io.github.ph1lou.werewolfapi.events.UpdateEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesVillage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.UUID;

public class Sister extends RolesVillage {

    public Sister(GetWereWolfAPI main, WereWolfAPI game, UUID uuid) {
        super(main,game,uuid);
    }


    @Override
    public String getDescription() {
        return game.translate("werewolf.role.sister.description");
    }

    @Override
    public String getDisplay() {
        return "werewolf.role.sister.display";
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {

        getPlayerUUID();

        Player sister1 = Bukkit.getPlayer(getPlayerUUID());

        if (sister1 == null) {
            return;
        }
        if (!game.getPlayersWW().get(getPlayerUUID()).isState(State.ALIVE)) {
            return;
        }

        Location location = sister1.getLocation();

        for (UUID uuid : game.getPlayersWW().keySet()) {
            PlayerWW plg = game.getPlayersWW().get(uuid);
            if(!uuid.equals(getPlayerUUID())){
                if(plg.isState(State.ALIVE)){
                    if(plg.getRole().isDisplay("werewolf.role.sister.display")) {
                        Player sister2 = Bukkit.getPlayer(uuid);
                        if (sister2 != null) {

                            Location location2 = sister2.getLocation();

                            if (location.distance(location2) < 20) {
                                if (sister1.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
                                    for (PotionEffect po : sister1.getActivePotionEffects()) {
                                        if (po.getDuration() != Integer.MAX_VALUE) {
                                            sister1.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                                        }
                                    }
                                }
                                sister1.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 0, false, false));
                                if (sister2.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
                                    for (PotionEffect po : sister2.getActivePotionEffects()) {
                                        if (po.getDuration() != Integer.MAX_VALUE) {
                                            sister2.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                                        }
                                    }
                                }
                                sister2.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 0, false, false));
                            }
                        }
                    }
                }
            }
        }
    }


    @EventHandler
    public void onSisterDeathReveal(SisterDeathEvent event) {

        if (event.isCancelled()) return;

        PlayerWW plg = game.getPlayersWW().get(getPlayerUUID());
        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (!plg.isState(State.ALIVE)) return;

        event.getAllSisters().add(getPlayerUUID());

        if (player == null) return;

        player.sendMessage(game.translate("werewolf.role.sister.reveal_killer", game.getPlayersWW().get(event.getSister()).getName(), event.getKiller() != null ? game.getPlayersWW().get(event.getKiller()).getName() : game.translate("werewolf.utils.pve")));

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSisterDeathRevealEnd(SisterDeathEvent event) {
        if (event.getAllSisters().isEmpty()) event.setCancelled(true);
    }

    @EventHandler
    public void onSisterDeath(FinalDeathEvent event) {

        UUID uuid = event.getUuid();
        PlayerWW plg = game.getPlayersWW().get(uuid);

        if (!uuid.equals(getPlayerUUID())) return;

        Bukkit.getPluginManager().callEvent(new SisterDeathEvent(uuid, new ArrayList<>(), plg.getLastKiller()));

    }
}
