package io.github.ph1lou.pluginlg.classesroles;

import io.github.ph1lou.pluginlg.events.NewWereWolfEvent;
import io.github.ph1lou.pluginlg.events.WereWolfListEvent;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.game.PlayerLG;
import io.github.ph1lou.pluginlgapi.enumlg.*;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;

import java.util.UUID;

public abstract class RolesImpl implements Roles, Listener,Cloneable {

    public final GameManager game;

    private Camp camp = Camp.VILLAGER;
    private UUID uuid;

    public RolesImpl (GameManager game, UUID uuid){
        this.game=game;
        this.uuid=uuid;
    }



    public void setCamp(Camp camp) {
        this.camp=camp;
    }

    @Override
    public boolean isCamp(Camp camp) {
        return(this.camp.equals(camp));
    }

    public Camp getCamp() {
        return(this.camp);
    }

    public UUID getPlayerUUID(){
        return uuid;
    }

    public void setPlayerUUID(UUID uuid) {
        this.uuid=uuid;
    }

    @Override
    public void stolen(UUID uuid) {

    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public void recoverPotionEffect(Player player) {
        if (game.config.getScenarioValues().get(ScenarioLG.CAT_EYES)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION,Integer.MAX_VALUE,0,false,false));
        }
    }

    @EventHandler
    public void onWereWolfList(WereWolfListEvent event){

        PlayerLG lg = game.playerLG.get(uuid);

        if(!isWereWolf()){
            return;
        }

        Team team=game.board.getTeam(lg.getName());

        if (game.config.getConfigValues().get(ToolLG.RED_NAME_TAG)) {
            if(game.getHosts().contains(uuid)){
                team.setPrefix(game.translate("werewolf.commands.admin.host.tag")+"§4");
            }
            else team.setPrefix("§4");
        }
        lg.setScoreBoard(game.board);

        if(!lg.isState(State.ALIVE)) {
            return;
        }

        if (Bukkit.getPlayer(uuid) == null) {
            return;
        }

        Player player = Bukkit.getPlayer(uuid);
        player.sendMessage(game.translate("werewolf.role.werewolf.see_others"));
        player.playSound(player.getLocation(), Sound.WOLF_HOWL, 1, 20);
        player.setScoreboard(game.board);
    }

    @EventHandler
    public void onNewWereWolf(NewWereWolfEvent event) {

        PlayerLG plg = game.playerLG.get(uuid);

        if(isWereWolf()){
            if (game.config.getTimerValues().get(TimerLG.WEREWOLF_LIST) < 0) {
                if (plg.isState(State.ALIVE)) {
                    if(Bukkit.getPlayer(uuid) != null){
                        Player lg1 = Bukkit.getPlayer(uuid);
                        lg1.sendMessage(game.translate("werewolf.role.werewolf.new_werewolf"));
                        lg1.playSound(lg1.getLocation(), Sound.WOLF_HOWL, 1, 20);
                    }
                }
            }
        }

        else if(uuid.equals(event.getUuid())){

            setCamp(Camp.WEREWOLF);

            if (game.config.getTimerValues().get(TimerLG.WEREWOLF_LIST) < 0) {

                Team team=game.board.getTeam(plg.getName());

                if (game.config.getConfigValues().get(ToolLG.RED_NAME_TAG)) {
                    if(game.getHosts().contains(uuid)){
                        team.setPrefix(game.translate("werewolf.commands.admin.host.tag")+"§4");
                    }
                    else team.setPrefix("§4");
                }
                plg.setScoreBoard(game.board);
            }

            if(Bukkit.getPlayer(uuid)!=null) {
                Player player = Bukkit.getPlayer(uuid);
                player.setScoreboard(game.board);
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION,Integer.MAX_VALUE,0,false,false));
                player.sendMessage(game.translate("werewolf.role.werewolf.go_to_the_werewolf_camp"));
                player.playSound(player.getLocation(),Sound.WOLF_HOWL, 1, 20);
                if (game.isDay(Day.NIGHT)) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE,-1,false,false));
                }
            }
        }
    }

    @Override
    public boolean isWereWolf() {

        return game.playerLG.get(uuid).getInfected();
    }
}
