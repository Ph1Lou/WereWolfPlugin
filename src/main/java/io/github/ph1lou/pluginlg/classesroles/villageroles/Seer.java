package io.github.ph1lou.pluginlg.classesroles.villageroles;


import io.github.ph1lou.pluginlgapi.GetWereWolfAPI;
import io.github.ph1lou.pluginlgapi.WereWolfAPI;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import io.github.ph1lou.pluginlgapi.enumlg.TimerLG;
import io.github.ph1lou.pluginlgapi.enumlg.ToolLG;
import io.github.ph1lou.pluginlgapi.events.ChestEvent;
import io.github.ph1lou.pluginlgapi.events.DayEvent;
import io.github.ph1lou.pluginlgapi.events.FinalDeathEvent;
import io.github.ph1lou.pluginlgapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.pluginlgapi.rolesattributs.RolesWithLimitedSelectionDuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Seer extends RolesWithLimitedSelectionDuration implements AffectedPlayers {

    private int dayNumber=-8;
    private final List<UUID> affectedPlayer = new ArrayList<>();

    public Seer(GetWereWolfAPI main, WereWolfAPI game, UUID uuid) {

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

        if (game.getConfig().getConfigValues().get(ToolLG.SEER_EVERY_OTHER_DAY) && event.getNumber()==dayNumber+1) {
            return;
        }

        setPower(true);
        dayNumber=event.getNumber();
        if(Bukkit.getPlayer(getPlayerUUID())==null){
            return;
        }
        Player player = Bukkit.getPlayer(getPlayerUUID());

        player.sendMessage(game.translate("werewolf.role.seer.see_camp_message", game.conversion(game.getConfig().getTimerValues().get(TimerLG.POWER_DURATION))));
    }


    @Override
    public String getDescription() {
        return game.translate("werewolf.role.seer.description");
    }

    @Override
    public String getDisplay() {
        return "werewolf.role.seer.display";
    }

    @Override
    public void recoverPotionEffect(Player player) {
        super.recoverPotionEffect(player);
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION,Integer.MAX_VALUE,0,false,false));
    }

    @EventHandler
    public void onFinalDeath(FinalDeathEvent event) {

        UUID uuid = event.getUuid();
        if (!game.getConfig().getConfigValues().get(ToolLG.EVENT_SEER_DEATH)) return;

        if(!uuid.equals(getPlayerUUID())) return;

        Bukkit.getPluginManager().callEvent(new ChestEvent());
        game.getConfig().getConfigValues().put(ToolLG.EVENT_SEER_DEATH, false);
    }


}
