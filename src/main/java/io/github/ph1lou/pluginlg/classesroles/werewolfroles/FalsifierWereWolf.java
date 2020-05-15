package io.github.ph1lou.pluginlg.classesroles.werewolfroles;

import io.github.ph1lou.pluginlg.classesroles.RolesImpl;
import io.github.ph1lou.pluginlg.events.SelectionEndEvent;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.enumlg.Camp;
import io.github.ph1lou.pluginlgapi.enumlg.RoleLG;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FalsifierWereWolf extends RolesWereWolf {

    private Camp posterCamp = Camp.WEREWOLF;
    private RolesImpl posterRole = this;

    public FalsifierWereWolf(GameManager game, UUID uuid) {
        super(game,uuid);
    }

    public void setPosterCamp(Camp camp) {
        this.posterCamp =camp;
    }

    public boolean isPosterCamp(Camp camp) {
        return(this.posterCamp.equals(camp));
    }

    public Camp getPosterCamp() {
        return(this.posterCamp);
    }

    public RolesImpl getPosterRole() {
        return(this.posterRole);
    }

    public void setPosterRole(RolesImpl roleLG) {
        this.posterRole =roleLG;
    }

    @EventHandler
    public void onSelectionEnd(SelectionEndEvent event) {

        if(!event.getUuid().equals(game.getGameUUID())){
            return;
        }

        if(!game.playerLG.get(getPlayerUUID()).isState(State.ALIVE)){
            return;
        }

        if(Bukkit.getPlayer(getPlayerUUID())==null){
            return;
        }
        Player player = Bukkit.getPlayer(getPlayerUUID());

        List<UUID> players = new ArrayList<>();
        for (UUID uuid : game.playerLG.keySet()) {
            if (game.playerLG.get(uuid).isState(State.ALIVE) && !uuid.equals(player.getUniqueId())) {
                players.add(uuid);
            }
        }
        if(players.size()<=0){
            return;
        }

        UUID pc = players.get((int) Math.floor(Math.random() * players.size()));

        setPosterRole(game.playerLG.get(pc).getRole());
        setPosterCamp(getPosterRole().getCamp());
        player.sendMessage(game.translate("werewolf.role.falsifier_werewolf.display_role_message",getPosterRole().getDisplay()));
    }


    @Override
    public RoleLG getRoleEnum() {
        return RoleLG.FALSIFIER_WEREWOLF;
    }


    @Override
    public String getDescription() {
        return game.translate("werewolf.role.falsifier_werewolf.description");
    }

    @Override
    public String getDisplay() {
        return game.translate("werewolf.role.falsifier_werewolf.display");
    }

    @Override
    public void stolen(UUID uuid) {

        if(Bukkit.getPlayer(getPlayerUUID())==null){
            return;
        }

        Bukkit.getPlayer(getPlayerUUID()).sendMessage(game.translate("werewolf.role.falsifier_werewolf.display_role_message",getPosterRole().getDisplay()));
    }
}
