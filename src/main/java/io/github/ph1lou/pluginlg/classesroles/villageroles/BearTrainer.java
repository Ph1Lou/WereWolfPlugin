package io.github.ph1lou.pluginlg.classesroles.villageroles;

import io.github.ph1lou.pluginlg.classesroles.werewolfroles.FalsifierWereWolf;
import io.github.ph1lou.pluginlg.events.DayEvent;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.game.PlayerLG;
import io.github.ph1lou.pluginlgapi.enumlg.Camp;
import io.github.ph1lou.pluginlgapi.enumlg.RoleLG;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.UUID;

public class BearTrainer extends RolesVillage {

    public BearTrainer(GameManager game, UUID uuid) {
        super(game,uuid);
    }

    @EventHandler
    public void onDay(DayEvent event) {

        if(!event.getUuid().equals(game.getGameUUID())){
            return;
        }

        if(!game.playerLG.get(getPlayerUUID()).isState(State.ALIVE)){
            return;
        }
        if(Bukkit.getPlayer(getPlayerUUID())==null){
            return;
        }

        StringBuilder builder = new StringBuilder();
        boolean ok = false;
        Location oursLocation = Bukkit.getPlayer(getPlayerUUID()).getLocation();

        for (Player pls : Bukkit.getOnlinePlayers()) {

            if (game.playerLG.containsKey(pls.getUniqueId())) {

                PlayerLG plo = game.playerLG.get(pls.getUniqueId());

                if (!(plo.getRole() instanceof FalsifierWereWolf) || ((FalsifierWereWolf) plo.getRole()).isPosterCamp(Camp.WEREWOLF)) {
                    if (game.roleManage.isWereWolf(plo) && plo.isState(State.ALIVE)) {
                        if (oursLocation.distance(pls.getLocation()) < game.config.getDistanceBearTrainer()) {
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
    public RoleLG getRoleEnum() {
        return RoleLG.BEAR_TRAINER;
    }

    @Override
    public String getDescription() {
        return game.translate("werewolf.role.bear_trainer.description");
    }

    @Override
    public String getDisplay() {
        return game.translate("werewolf.role.bear_trainer.display");
    }
}
