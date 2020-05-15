package io.github.ph1lou.pluginlg.game;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.classesroles.RolesImpl;
import io.github.ph1lou.pluginlg.classesroles.neutralroles.Angel;
import io.github.ph1lou.pluginlg.classesroles.neutralroles.Succubus;
import io.github.ph1lou.pluginlg.classesroles.villageroles.Cupid;
import io.github.ph1lou.pluginlg.classesroles.villageroles.WildChild;
import io.github.ph1lou.pluginlg.utils.Title;
import io.github.ph1lou.pluginlgapi.WinEvent;
import io.github.ph1lou.pluginlgapi.enumlg.*;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class EndLG {


    private RoleLG winner = null;
    private final MainLG main;
    private final GameManager game;

    public EndLG(MainLG main, GameManager game) {
        this.game = game;
        this.main = main;
    }

    public void check_victory() {

        int player = game.score.getPlayerSize();

        if (!game.loversManage.cursedLoversRange.isEmpty()) {
            return;
        }

        List<Set<UUID>> teamsAngel = getAngeTeam();
        List<Set<UUID>> teamsSuccubus = getSuccubusTeam();

        if (game.loversManage.loversRange.size()+game.loversManage.amnesiacLoversRange.size()==1) {

            Set<UUID> team;

            if(game.loversManage.loversRange.size()==1){
                team = new HashSet<>(game.loversManage.loversRange.get(0));
                for (UUID uuid : game.playerLG.keySet()) {
                    PlayerLG plg =game.playerLG.get(uuid);
                    if (plg.isState(State.JUDGEMENT)) return;
                    if (plg.isState(State.ALIVE) && plg.getRole() instanceof Cupid){
                        Cupid cupid = (Cupid) plg.getRole();
                        if(game.loversManage.loversRange.get(0).contains(cupid.getAffectedPlayers().get(0))) {
                            team.add(uuid);
                        }
                    }
                }
            }
            else{
                team = new HashSet<>(game.loversManage.amnesiacLoversRange.get(0));
                for(UUID uuid:game.loversManage.amnesiacLoversRange.get(0)){
                    if(game.playerLG.get(uuid).isState(State.JUDGEMENT)) return;
                }
            }

            for (Set<UUID> teamange : teamsAngel) {
                for (UUID t : teamange) {
                    if (team.contains(t)) {
                        team.addAll(teamange);
                        break;
                    }
                }
            }
            for (Set<UUID> teamSuccubus : teamsSuccubus) {
                for (UUID t : teamSuccubus) {
                    if (team.contains(t)) {
                        team.addAll(teamSuccubus);
                        break;
                    }
                }
            }

            if (player == team.size()) {
                winner = game.loversManage.loversRange.size()==1?RoleLG.LOVER :RoleLG.AMNESIAC_LOVER;
                fin();
                return;
            }
        }

        if (game.config.getConfigValues().get(ToolLG.VICTORY_COUPLE) && (!game.loversManage.loversRange.isEmpty() || !game.loversManage.amnesiacLoversRange.isEmpty())) {
            return;
        }

        if (!teamsAngel.isEmpty() && teamsAngel.get(0).size() > 1 && teamsAngel.get(0).size() == game.score.getPlayerSize()) {
            winner = RoleLG.GUARDIAN_ANGEL;
            fin();
            return;
        }

        if (!teamsSuccubus.isEmpty() && teamsSuccubus.get(0).size() > 1 && teamsSuccubus.get(0).size() == game.score.getPlayerSize()) {
            winner = RoleLG.SUCCUBUS;
            fin();
            return;
        }

        Camp camp = null;

        for (UUID uuid : game.playerLG.keySet()) {
            if (game.playerLG.get(uuid).isState(State.JUDGEMENT)) return;
            PlayerLG plg = game.playerLG.get(uuid);
            if (plg.isState(State.ALIVE)) {
                if(camp==null) {
                    camp=plg.getRole().getCamp();
                }
                else if(!plg.getRole().isCamp(camp)){
                    return;
                }
            }
        }
        if(camp==null) {
            fin();
            return;
        }
        if(camp.equals(Camp.WEREWOLF)) {
            winner = RoleLG.WEREWOLF;
            fin();
            return;

        }
        if(camp.equals(Camp.VILLAGER)) {
            winner = RoleLG.VILLAGER;
            fin();
            return;
        }

        if(camp.equals(Camp.NEUTRAL)) {

            if (player != 1) {
                return;
            }
            RolesImpl role=null;

            for(UUID uuid:game.playerLG.keySet()) {

                PlayerLG plg = game.playerLG.get(uuid);
                if(plg.isState(State.ALIVE)) {
                    if(role==null) {
                        role=plg.getRole();
                    }
                    else if(!plg.isRole(role)){
                        return;
                    }
                }
            }
            if(role == null){
                return;
            }
            winner = role.getRoleEnum();
            fin();
        }
    }

    public void fin() {

        String subtitles_victory = winner == null ? game.translate("werewolf.end.death") : game.translate("werewolf.end.team", game.translate(winner.getKey()));
        List<UUID> players = new ArrayList<>();
        for (UUID uuid : game.playerLG.keySet()) {
            if (game.playerLG.get(uuid).isState(State.ALIVE)) {
                players.add(uuid);
            }
        }
        Bukkit.getPluginManager().callEvent(new WinEvent(subtitles_victory, players));

        game.setState(StateLG.END);

        game.score.getKillCounter();

        game.score.updateBoard();
        game.config.getConfigValues().put(ToolLG.CHAT, true);

        StringBuilder sb = new StringBuilder();

        for (UUID uuid : game.playerLG.keySet()) {

            PlayerLG plg = game.playerLG.get(uuid);
            String role = plg.getRole().getDisplay();
            String playerName = plg.getName();

            if(plg.isThief()){
                role=game.translate(RoleLG.THIEF.getKey());
            }
            if (plg.isState(State.DEATH)) {
                sb.append(game.translate("werewolf.end.reveal_death", playerName, role));
            } else {
                sb.append(game.translate("werewolf.end.reveal", playerName, role));
            }

            if(plg.isThief()){
                role =plg.getRole().getDisplay();
                sb.append(game.translate("werewolf.end.thief", role));
            }
            if(plg.getRole() instanceof WildChild && ((WildChild) plg.getRole()).getTransformed()){
                WildChild wildChild = (WildChild) plg.getRole();
                if(!wildChild.getAffectedPlayers().isEmpty()){
                    sb.append(game.translate("werewolf.end.model",game.playerLG.get(wildChild.getAffectedPlayers().get(0)).getName()));
                }
                sb.append(game.translate("werewolf.end.transform"));
            }
            if(plg.getInfected()){
                sb.append(game.translate("werewolf.end.infect"));
            }
            if(!plg.getLovers().isEmpty()){
                StringBuilder sb2 = new StringBuilder();
                for(UUID lover:plg.getLovers()){
                    sb2.append(game.playerLG.get(lover).getName()).append(" ");
                }
                sb.append(game.translate("werewolf.end.lover",sb2.toString()));
            }
            if(plg.getCursedLovers()!=null){
                sb.append(game.translate("werewolf.end.cursed_lover",game.playerLG.get(plg.getCursedLovers()).getName()));
            }
            if(plg.getAmnesiacLoverUUID()!=null){
                sb.append(game.translate("werewolf.end.lover",game.playerLG.get(plg.getAmnesiacLoverUUID()).getName()));
            }
            sb.append("\n");
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(game.translate("werewolf.end.message", subtitles_victory));
            p.sendMessage(sb.toString());
            Title.sendTitle(p, 20, 60, 20, game.translate("werewolf.end.victory"), subtitles_victory);
            TextComponent msg = new TextComponent(game.translate("werewolf.bug"));
            msg.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/GXXCVUA"));
            p.spigot().sendMessage(msg);
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(main, game::stopGame, 600);
        Bukkit.broadcastMessage(game.translate("werewolf.announcement.restart"));
    }


    private List<Set<UUID>> getAngeTeam(){

        List<Set<UUID>> temp= new ArrayList<>();

        for(UUID uuid:game.playerLG.keySet()) {

            PlayerLG plg1 = game.playerLG.get(uuid);

            if(plg1.isState(State.ALIVE)) {
                List<UUID> teamange= new ArrayList<>();
                teamange.add(uuid);
                if(plg1.getRole() instanceof Angel){
                    Angel angel = (Angel) plg1.getRole();
                    if(angel.getChoice().equals(RoleLG.GUARDIAN_ANGEL)){
                        if(!angel.getAffectedPlayers().isEmpty() && game.playerLG.get(angel.getAffectedPlayers().get(0)).isState(State.ALIVE)){
                            teamange.add(angel.getAffectedPlayers().get(0));
                        }
                    }
                }

                for (int i=0; i< teamange.size(); i++) {
                    for (UUID uuid2 : game.playerLG.keySet()) {

                        PlayerLG plg2 = game.playerLG.get(uuid2);

                        if (plg2.getRole() instanceof Angel){
                            Angel angel = (Angel) plg2.getRole();

                            if(angel.getChoice().equals(RoleLG.GUARDIAN_ANGEL)){
                                if(angel.getAffectedPlayers().contains(teamange.get(i))){
                                    if(plg2.isState(State.ALIVE)) {
                                        if (!teamange.contains(uuid2)) {
                                            teamange.add(uuid2);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                int i;
                for(i=0; i<temp.size(); i++){
                    if (temp.get(i).size() < teamange.size()) {
                        temp.add(i, new HashSet<>(teamange));
                        break;
                    }
                }
                if (i == temp.size()) {
                    temp.add(new HashSet<>(teamange));
                }
            }
        }

        return temp;
    }

    private List<Set<UUID>> getSuccubusTeam() {

        List<Set<UUID>> temp = new ArrayList<>();

        for (UUID uuid : game.playerLG.keySet()) {

            PlayerLG plg1 = game.playerLG.get(uuid);

            if (plg1.isState(State.ALIVE)) {

                List<UUID> teamSuccubus = new ArrayList<>();
                teamSuccubus.add(uuid);

                if (plg1.getRole() instanceof Succubus ){
                    Succubus succubus = (Succubus) plg1.getRole();
                    if(!succubus.getAffectedPlayers().isEmpty() && !succubus.hasPower() && game.playerLG.get(succubus.getAffectedPlayers().get(0)).isState(State.ALIVE)){
                        teamSuccubus.add(succubus.getAffectedPlayers().get(0));
                    }
                }

                for (int i = 0; i < teamSuccubus.size(); i++) {
                    for (UUID uuid2 : game.playerLG.keySet()) {
                        PlayerLG plg2 = game.playerLG.get(uuid2);
                        if (plg2.getRole() instanceof Succubus){
                            Succubus succubus= (Succubus) plg2.getRole();
                            if(succubus.getAffectedPlayers().contains(teamSuccubus.get(i))){
                                if(plg2.isState(State.ALIVE) && !succubus.hasPower()) {
                                    if (!teamSuccubus.contains(uuid2)) {
                                        teamSuccubus.add(uuid2);
                                    }
                                }
                            }
                        }
                    }
                }
                int i;
                for (i = 0; i < temp.size(); i++) {
                    if (temp.get(i).size() < teamSuccubus.size()) {
                        temp.add(i, new HashSet<>(teamSuccubus));
                        break;
                    }
                }
                if (i == temp.size()) {
                    temp.add(new HashSet<>(teamSuccubus));
                }
            }
        }
        return temp;
    }

}
