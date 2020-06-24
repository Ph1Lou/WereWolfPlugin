package io.github.ph1lou.werewolfplugin.classesroles.villageroles;


import io.github.ph1lou.pluginlgapi.GetWereWolfAPI;
import io.github.ph1lou.pluginlgapi.PlayerWW;
import io.github.ph1lou.pluginlgapi.WereWolfAPI;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import io.github.ph1lou.pluginlgapi.enumlg.ToolLG;
import io.github.ph1lou.pluginlgapi.events.ThirdDeathEvent;
import io.github.ph1lou.pluginlgapi.events.WitchResurrectionEvent;
import io.github.ph1lou.pluginlgapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.pluginlgapi.rolesattributs.Power;
import io.github.ph1lou.pluginlgapi.rolesattributs.RolesVillage;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Witch extends RolesVillage implements AffectedPlayers, Power {

    private final List<UUID> affectedPlayer = new ArrayList<>();

    public Witch(GetWereWolfAPI main, WereWolfAPI game, UUID uuid) {
        super(main,game,uuid);
    }

    private boolean power=true;
    @Override
    public void setPower(Boolean power) {
        this.power=power;
    }

    @Override
    public Boolean hasPower() {
        return(this.power);
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

    @Override
    public String getDescription() {
        return game.translate("werewolf.role.witch.description");
    }

    @Override
    public String getDisplay() {
        return "werewolf.role.witch.display";
    }

    @EventHandler
    public void onThirdDeathEvent(ThirdDeathEvent event){

        if(event.isCancelled()) return;

        if(!hasPower()) return;

        PlayerWW plg = game.getPlayersWW().get(event.getUuid());

        if (event.getUuid().equals(getPlayerUUID())) {
            if(game.getConfig().getConfigValues().get(ToolLG.AUTO_REZ_WITCH)){
                WitchResurrectionEvent witchResurrectionEvent=new WitchResurrectionEvent(getPlayerUUID(),event.getUuid());
                Bukkit.getPluginManager().callEvent(witchResurrectionEvent);

                if(witchResurrectionEvent.isCancelled()){
                    if(Bukkit.getPlayer(getPlayerUUID())!=null){
                        Bukkit.getPlayer(getPlayerUUID()).sendMessage(game.translate("werewolf.check.cancel"));
                    }
                    return;
                }

                setPower(false);
                game.resurrection(getPlayerUUID());
                event.setCancelled(true);
            }
        } else {

            if (!game.getPlayersWW().get(getPlayerUUID()).isState(State.ALIVE) ) return;

            if(Bukkit.getPlayer(getPlayerUUID()) != null){
                TextComponent witch_msg = new TextComponent(game.translate("werewolf.role.witch.resuscitation_message", plg.getName()));
                witch_msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ww "+game.translate("werewolf.role.witch.command") +" "+ event.getUuid()));
                Bukkit.getPlayer(getPlayerUUID()).spigot().sendMessage(witch_msg);
            }
        }
    }

}
