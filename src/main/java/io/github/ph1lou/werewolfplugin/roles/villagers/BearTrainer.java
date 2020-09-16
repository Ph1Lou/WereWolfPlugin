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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

        StringBuilder builder = new StringBuilder();
        boolean ok = false;
        Location oursLocation = player.getLocation();
        List<UUID> growled = new ArrayList<>();
        for (Player pls : Bukkit.getOnlinePlayers()) {

            if (game.getPlayersWW().containsKey(pls.getUniqueId())) {

                PlayerWW plo = game.getPlayersWW().get(pls.getUniqueId());

                if (!(plo.getRole() instanceof Display) || ((Display) plo.getRole()).isDisplayCamp(Camp.WEREWOLF)) {
                    if (plo.getRole().isWereWolf() && plo.isState(State.ALIVE)) {
                        if (oursLocation.distance(pls.getLocation()) < game.getConfig().getDistanceBearTrainer()) {
                            growled.add(pls.getUniqueId());
                            ok = true;
                        }
                    }
                }
            }
        }
        if (ok) {
            GrowlEvent growlEvent = new GrowlEvent(getPlayerUUID(), growled);
            Bukkit.getPluginManager().callEvent(growlEvent);

            if (growlEvent.isCancelled()) {
                player.sendMessage(game.translate("werewolf.check.cancel"));
                return;
            }
            for (UUID ignored : growlEvent.getPlayersUUID()) {
                builder.append(game.translate("werewolf.role.bear_trainer.growling"));
            }
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendMessage(game.translate("werewolf.role.bear_trainer.growling_message", builder.toString()));
                Sounds.WOLF_GROWL.play(p);
            }

        }
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
