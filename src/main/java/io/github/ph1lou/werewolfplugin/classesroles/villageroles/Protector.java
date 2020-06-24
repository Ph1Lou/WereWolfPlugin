package io.github.ph1lou.werewolfplugin.classesroles.villageroles;



import io.github.ph1lou.pluginlgapi.GetWereWolfAPI;
import io.github.ph1lou.pluginlgapi.WereWolfAPI;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import io.github.ph1lou.pluginlgapi.enumlg.TimerLG;
import io.github.ph1lou.pluginlgapi.events.DayEvent;
import io.github.ph1lou.pluginlgapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.pluginlgapi.rolesattributs.RolesWithLimitedSelectionDuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Protector extends RolesWithLimitedSelectionDuration implements AffectedPlayers {

    private final List<UUID> affectedPlayer = new ArrayList<>();

    public Protector(GetWereWolfAPI main, WereWolfAPI game, UUID uuid){
        super(main,game,uuid);
        setPower(false);
    }

    @Override
    public void addAffectedPlayer(UUID uuid) {
        this.affectedPlayer.add(uuid);
    }

    @Override
    public void removeAffectedPlayer(UUID uuid) {
        this.affectedPlayer.remove(uuid);
    }

    @Override
    public void clearAffectedPlayer() {
        this.affectedPlayer.clear();
    }

    @Override
    public List<UUID> getAffectedPlayers() {
        return (this.affectedPlayer);
    }

    @EventHandler
    public void onDay(DayEvent event) {

        if(!game.getPlayersWW().get(getPlayerUUID()).isState(State.ALIVE)){
            return;
        }

        setPower(true);

        if(Bukkit.getPlayer(getPlayerUUID())==null){
            return;
        }
        Player player = Bukkit.getPlayer(getPlayerUUID());

        player.sendMessage(game.translate("werewolf.role.protector.protection_message", game.conversion(game.getConfig().getTimerValues().get(TimerLG.POWER_DURATION))));
    }

    @Override
    public String getDescription() {
        return game.translate("werewolf.role.protector.description");
    }

    @Override
    public String getDisplay() {
        return "werewolf.role.protector.display";
    }
}
