package io.github.ph1lou.pluginlg.game;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.classesroles.neutralroles.Succubus;
import io.github.ph1lou.pluginlg.classesroles.villageroles.Cupid;
import io.github.ph1lou.pluginlg.utils.Title;
import io.github.ph1lou.pluginlgapi.PlayerWW;
import io.github.ph1lou.pluginlgapi.enumlg.*;
import io.github.ph1lou.pluginlgapi.events.EndPlayerMessageEvent;
import io.github.ph1lou.pluginlgapi.events.WinConditionsCheckEvent;
import io.github.ph1lou.pluginlgapi.events.WinEvent;
import io.github.ph1lou.pluginlgapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.pluginlgapi.rolesattributs.AngelRole;
import io.github.ph1lou.pluginlgapi.rolesattributs.Roles;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class EndLG {


    private String winner = null;
    private final MainLG main;
    private final GameManager game;

    public EndLG(MainLG main, GameManager game) {
        this.game = game;
        this.main = main;
    }

    public void check_victory() {

        int player = game.score.getPlayerSize();

        if (!game.getCursedLoversRange().isEmpty()) {
            return;
        }

        if (game.isState(StateLG.END)) return;

        List<Set<UUID>> teamsAngel = getAngeTeam();
        List<Set<UUID>> teamsSuccubus = getSuccubusTeam();

        if (game.getLoversRange().size()+game.getAmnesiacLoversRange().size()==1) {

            Set<UUID> team;

            if(game.getLoversRange().size()==1){
                team = new HashSet<>(game.getLoversRange().get(0));
                for (UUID uuid : game.playerLG.keySet()) {
                    PlayerWW plg =game.playerLG.get(uuid);
                    if (plg.isState(State.JUDGEMENT)) return;
                    if (plg.isState(State.ALIVE) && plg.getRole() .isDisplay("werewolf.role.cupid.display")){
                        Cupid cupid = (Cupid) plg.getRole();
                        if(game.getLoversRange().get(0).contains(cupid.getAffectedPlayers().get(0))) {
                            team.add(uuid);
                        }
                    }
                }
            }
            else{
                team = new HashSet<>(game.getAmnesiacLoversRange().get(0));
                for(UUID uuid:game.getAmnesiacLoversRange().get(0)){
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
                winner = game.getLoversRange().size()==1?"werewolf.role.lover.display" :"werewolf.role.amnesiac_lover.display";
                fin();
                return;
            }
        }

        if (game.getConfig().getConfigValues().get(ToolLG.VICTORY_COUPLE) && (!game.getLoversRange().isEmpty() || !game.getAmnesiacLoversRange().isEmpty())) {
            return;
        }
        WinConditionsCheckEvent winConditionsCheckEvent = new WinConditionsCheckEvent();
        Bukkit.getPluginManager().callEvent(winConditionsCheckEvent);

        if(winConditionsCheckEvent.isCancelled()){
            winner = winConditionsCheckEvent.getVictoryTeam();
            fin();
            return;
        }

        if (!teamsAngel.isEmpty() && teamsAngel.get(0).size() > 1 && teamsAngel.get(0).size() == game.score.getPlayerSize()) {
            winner = "werewolf.role.guardian_angel.display";
            fin();
            return;
        }

        if (!teamsSuccubus.isEmpty() && teamsSuccubus.get(0).size() > 1 && teamsSuccubus.get(0).size() == game.score.getPlayerSize()) {
            winner = "werewolf.role.succubus.display";
            fin();
            return;
        }

        Camp camp = null;

        for (UUID uuid : game.playerLG.keySet()) {
            if (game.playerLG.get(uuid).isState(State.JUDGEMENT)) return;
            PlayerWW plg = game.playerLG.get(uuid);
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
            winner = "werewolf.role.werewolf.display";
            fin();
            return;

        }
        if(camp.equals(Camp.VILLAGER)) {
            winner = "werewolf.role.villager.display";
            fin();
            return;
        }

        if(camp.equals(Camp.NEUTRAL)) {

            if (player != 1) {
                return;
            }
            Roles role=null;

            for(UUID uuid:game.playerLG.keySet()) {

                PlayerWW plg = game.playerLG.get(uuid);
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
            winner = role.getDisplay();
            fin();
        }
    }

    public void fin() {

        String subtitles_victory = winner == null ? game.translate("werewolf.end.death") : game.translate(winner);
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
        game.getConfig().getConfigValues().put(ToolLG.CHAT, true);


        for (UUID uuid : game.playerLG.keySet()) {

            PlayerWW plg = game.playerLG.get(uuid);
            String role = game.translate(plg.getRole().getDisplay());
            String playerName = plg.getName();
            StringBuilder sb = new StringBuilder();

            if(plg.isThief()){
                role=game.translate("werewolf.role.thief.display");
            }
            if (plg.isState(State.DEATH)) {
                sb.append(game.translate("werewolf.end.reveal_death", playerName, role));
            } else {
                sb.append(game.translate("werewolf.end.reveal", playerName, role));
            }
            if(plg.isThief()){
                role = game.translate(plg.getRole().getDisplay());
                sb.append(game.translate("werewolf.end.thief", role));
            }

            EndPlayerMessageEvent endPlayerMessageEvent = new EndPlayerMessageEvent(uuid,sb);
            Bukkit.getPluginManager().callEvent(endPlayerMessageEvent);

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
            Bukkit.broadcastMessage(sb.toString());
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(game.translate("werewolf.end.message", subtitles_victory));
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

            PlayerWW plg1 = game.playerLG.get(uuid);

            if(plg1.isState(State.ALIVE)) {
                List<UUID> teamange= new ArrayList<>();
                teamange.add(uuid);
                if(plg1.getRole() instanceof AngelRole){
                    AngelRole angel = (AngelRole) plg1.getRole();
                    if(angel.isChoice(AngelForm.GUARDIAN_ANGEL)){
                        if(angel instanceof AffectedPlayers){
                            AffectedPlayers affectedPlayers = (AffectedPlayers) angel;

                            if(!affectedPlayers.getAffectedPlayers().isEmpty() && game.playerLG.get(affectedPlayers.getAffectedPlayers().get(0)).isState(State.ALIVE)){
                                teamange.add(affectedPlayers.getAffectedPlayers().get(0));
                            }
                        }

                    }
                }

                for (int i=0; i< teamange.size(); i++) {
                    for (UUID uuid2 : game.playerLG.keySet()) {

                        PlayerWW plg2 = game.playerLG.get(uuid2);

                        if (plg2.getRole() instanceof AngelRole){
                            AngelRole angel = (AngelRole) plg2.getRole();

                            if(angel.isChoice(AngelForm.GUARDIAN_ANGEL)){
                                if(angel instanceof AffectedPlayers) {
                                    AffectedPlayers affectedPlayers = (AffectedPlayers) angel;

                                    if(affectedPlayers.getAffectedPlayers().contains(teamange.get(i))){
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

            PlayerWW plg1 = game.playerLG.get(uuid);

            if (plg1.isState(State.ALIVE)) {

                List<UUID> teamSuccubus = new ArrayList<>();
                teamSuccubus.add(uuid);

                if (plg1.getRole().isDisplay("werewolf.role.succubus.display")){
                    Succubus succubus = (Succubus) plg1.getRole();
                    if(!succubus.getAffectedPlayers().isEmpty() && !succubus.hasPower() && game.playerLG.get(succubus.getAffectedPlayers().get(0)).isState(State.ALIVE)){
                        teamSuccubus.add(succubus.getAffectedPlayers().get(0));
                    }
                }

                for (int i = 0; i < teamSuccubus.size(); i++) {
                    for (UUID uuid2 : game.playerLG.keySet()) {
                        PlayerWW plg2 = game.playerLG.get(uuid2);
                        if (plg2.getRole().isDisplay("werewolf.role.succubus.display")){
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
