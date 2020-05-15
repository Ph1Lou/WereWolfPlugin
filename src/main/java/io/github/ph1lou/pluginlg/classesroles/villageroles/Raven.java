package io.github.ph1lou.pluginlg.classesroles.villageroles;



import io.github.ph1lou.pluginlg.classesroles.AffectedPlayers;
import io.github.ph1lou.pluginlg.events.DayEvent;
import io.github.ph1lou.pluginlg.events.VoteEvent;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.enumlg.RoleLG;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import io.github.ph1lou.pluginlgapi.enumlg.TimerLG;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Raven extends RolesWithLimitedSelectionDuration implements AffectedPlayers {

    private final List<UUID> affectedPlayer = new ArrayList<>();

    public Raven(GameManager game, UUID uuid) {

        super(game,uuid);
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

        if(!event.getUuid().equals(game.getGameUUID())){
            return;
        }

        if(!game.playerLG.get(getPlayerUUID()).isState(State.ALIVE)){
            return;
        }

        setPower(true);

        if(Bukkit.getPlayer(getPlayerUUID())==null){
            return;
        }
        Player player = Bukkit.getPlayer(getPlayerUUID());

        player.sendMessage(game.translate("werewolf.role.raven.curse_message", game.score.conversion(game.config.getTimerValues().get(TimerLG.POWER_DURATION))));
    }


    @Override
    public RoleLG getRoleEnum() {
        return RoleLG.RAVEN;
    }

    @Override
    public String getDescription() {
        return game.translate("werewolf.role.raven.description");
    }

    @Override
    public String getDisplay() {
        return game.translate("werewolf.role.raven.display");
    }


    @EventHandler
    public void onVoteEvent(VoteEvent event){

        if(!event.getVoterUUID().equals(getPlayerUUID())) return;

        game.playerLG.get(event.getVoteUUID()).incVote();
    }
}
