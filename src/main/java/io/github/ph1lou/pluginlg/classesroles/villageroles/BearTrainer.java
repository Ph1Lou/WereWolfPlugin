package io.github.ph1lou.pluginlg.classesroles.villageroles;

import io.github.ph1lou.pluginlgapi.GetWereWolfAPI;
import io.github.ph1lou.pluginlgapi.PlayerWW;
import io.github.ph1lou.pluginlgapi.WereWolfAPI;
import io.github.ph1lou.pluginlgapi.enumlg.Camp;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import io.github.ph1lou.pluginlgapi.events.DayEvent;
import io.github.ph1lou.pluginlgapi.rolesattributs.Display;
import io.github.ph1lou.pluginlgapi.rolesattributs.RolesVillage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.UUID;

public class BearTrainer extends RolesVillage {

    public BearTrainer(GetWereWolfAPI main, WereWolfAPI game, UUID uuid) {
        super(main,game,uuid);
    }

    @EventHandler
    public void onDay(DayEvent event) {


        if(!game.getPlayersWW().get(getPlayerUUID()).isState(State.ALIVE)){
            return;
        }
        if(Bukkit.getPlayer(getPlayerUUID())==null){
            return;
        }

        StringBuilder builder = new StringBuilder();
        boolean ok = false;
        Location oursLocation = Bukkit.getPlayer(getPlayerUUID()).getLocation();

        for (Player pls : Bukkit.getOnlinePlayers()) {

            if (game.getPlayersWW().containsKey(pls.getUniqueId())) {

                PlayerWW plo = game.getPlayersWW().get(pls.getUniqueId());

                if (!(plo.getRole() instanceof Display) || ((Display) plo.getRole()).isDisplayCamp(Camp.WEREWOLF)) {
                    if (plo.getRole().isWereWolf() && plo.isState(State.ALIVE)) {
                        if (oursLocation.distance(pls.getLocation()) < game.getConfig().getDistanceBearTrainer()) {
                            builder.append(game.translate("werewolf.role.bear_trainer.growling"));
                            ok = true;
                        }
                    }
                }
            }
        }
        if (ok) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendMessage(game.translate("werewolf.role.bear_trainer.growling_message", builder.toString()));
                p.playSound(p.getLocation(), Sound.WOLF_GROWL, 1, 20);
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
