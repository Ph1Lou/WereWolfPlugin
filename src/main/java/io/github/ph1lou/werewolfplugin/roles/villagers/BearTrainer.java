package io.github.ph1lou.werewolfplugin.roles.villagers;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.Camp;
import io.github.ph1lou.werewolfapi.enumlg.Sounds;
import io.github.ph1lou.werewolfapi.enumlg.State;
import io.github.ph1lou.werewolfapi.events.DayEvent;
import io.github.ph1lou.werewolfapi.events.GrowlEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.Display;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesVillage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class BearTrainer extends RolesVillage {

    public BearTrainer(GetWereWolfAPI main, WereWolfAPI game, UUID uuid) {
        super(main,game,uuid);
    }

    @EventHandler
    public void onDay(DayEvent event) {

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (!game.getPlayersWW().get(getPlayerUUID()).isState(State.ALIVE)) {
            return;
        }
        if (player == null) return;

        Location oursLocation = player.getLocation();
        List<UUID> growled = new ArrayList<>();
        for (Player pls : Bukkit.getOnlinePlayers()) {

            if (game.getPlayersWW().containsKey(pls.getUniqueId())) {

                PlayerWW plo = game.getPlayersWW().get(pls.getUniqueId());

                if (!(plo.getRole() instanceof Display) || ((Display) plo.getRole()).isDisplayCamp(Camp.WEREWOLF)) {
                    if (plo.getRole().isWereWolf() && plo.isState(State.ALIVE)) {
                        if (oursLocation.distance(pls.getLocation()) < game.getConfig().getDistanceBearTrainer()) {
                            growled.add(pls.getUniqueId());
                        }
                    }
                }
            }
        }
        GrowlEvent growlEvent = new GrowlEvent(getPlayerUUID(), growled);
        Bukkit.getPluginManager().callEvent(growlEvent);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onGrowl(GrowlEvent event) {

        if (event.getPlayersUUID().isEmpty()) {
            event.setCancelled(true);
            return;
        }

        if (!event.getPlayerUUID().equals(getPlayerUUID())) return;

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) return;

        if (event.isCancelled()) {
            player.sendMessage(game.translate("werewolf.check.cancel"));
            return;
        }

        String builder = event.getPlayersUUID().stream().map(ignored -> game.translate("werewolf.role.bear_trainer.growling")).collect(Collectors.joining());

        for (Player p : Bukkit.getOnlinePlayers()) {
            Sounds.WOLF_GROWL.play(p);
        }
        Bukkit.broadcastMessage(game.translate("werewolf.role.bear_trainer.growling_message", builder));
    }

    @Override
    public String getDescription() {
        return game.translate("werewolf.role.bear_trainer.description");
    }

    @Override
    public String getDisplay() {
        return "werewolf.role.bear_trainer.display";
    }
}
