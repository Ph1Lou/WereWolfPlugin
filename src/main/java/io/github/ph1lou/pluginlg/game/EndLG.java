package io.github.ph1lou.pluginlg.game;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.enumlg.*;
import io.github.ph1lou.pluginlg.events.WinEvent;
import io.github.ph1lou.pluginlg.utils.Title;
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
        List<Set<String>> teamsAngel = getAngeTeam();
        List<Set<String>> teamsSuccubus = getSuccubusTeam();

        if (game.loversManage.loversRange.size() == 1) {

            Set<String> team = new HashSet<>(game.loversManage.loversRange.get(0));

            for (String p : game.playerLG.keySet()) {
                if (game.playerLG.get(p).isState(State.JUDGEMENT)) return;
                if (game.playerLG.get(p).isState(State.LIVING) && game.playerLG.get(p).isRole(RoleLG.CUPIDON) && game.loversManage.loversRange.get(0).contains(game.playerLG.get(p).getAffectedPlayer().get(0))) {
                    team.add(p);
                }
            }
            for (Set<String> teamange : teamsAngel) {
                for (String t : teamange) {
                    if (team.contains(t)) {
                        team.addAll(teamange);
                        break;
                    }
                }
            }
            for (Set<String> teamSuccubus : teamsSuccubus) {
                for (String t : teamSuccubus) {
                    if (team.contains(t)) {
                        team.addAll(teamSuccubus);
                        break;
                    }
                }
            }

            if (player == team.size()) {
                winner = RoleLG.COUPLE;
                fin();
                return;
            }
        }

        if (game.config.configValues.get(ToolLG.VICTORY_COUPLE) && !game.loversManage.loversRange.isEmpty()) {
            return;
        }

        if (!teamsAngel.isEmpty() && teamsAngel.get(0).size() > 1 && teamsAngel.get(0).size() == game.score.getPlayerSize()) {
            winner = RoleLG.ANGE_GARDIEN;
            fin();
            return;
        }

        if (!teamsSuccubus.isEmpty() && teamsSuccubus.get(0).size() > 1 && teamsSuccubus.get(0).size() == game.score.getPlayerSize()) {
            winner = RoleLG.SUCCUBUS;
            fin();
            return;
        }

        Camp camp = null;

        for (String p : game.playerLG.keySet()) {
            if (game.playerLG.get(p).isState(State.JUDGEMENT)) return;
            PlayerLG plg = game.playerLG.get(p);
            if (plg.isState(State.LIVING)) {
                if(camp==null) {
                    camp=plg.getCamp();
                }
                else if(!plg.isCamp(camp)){
                    return;
                }
            }
        }
        if(camp==null) {
            fin();
            return;
        }
        if(camp.equals(Camp.LG)) {
            winner = RoleLG.LOUP_GAROU;
            fin();
            return;

        }
        if(camp.equals(Camp.VILLAGE)) {
            winner = RoleLG.VILLAGEOIS;
            fin();
            return;
        }

        if(camp.equals(Camp.NEUTRAL)) {

            if (!game.config.configValues.get(ToolLG.VICTORY_NEUTRAL) && player != 1) {
                return;
            }
            RoleLG role=null;

            for(String p:game.playerLG.keySet()) {

                PlayerLG plg = game.playerLG.get(p);
                if(plg.isState(State.LIVING)) {
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
            winner = role;
            fin();
        }
    }

    public void fin() {

        String subtitles_victory = winner == null ? game.text.getText(5) : String.format(game.text.getText(4), game.text.translateRole.get(winner));
        List<UUID> players = new ArrayList<>();
        for (String playerName : game.playerLG.keySet()) {
            if (game.playerLG.get(playerName).isState(State.LIVING)) {
                if (Bukkit.getPlayer(playerName) != null) {
                    players.add(Bukkit.getPlayer(playerName).getUniqueId());
                }
            }
        }
        Bukkit.getPluginManager().callEvent(new WinEvent(winner, players));
        game.setState(StateLG.FIN);
        game.score.getKillCounter();
        game.score.updateBoard();
        game.config.configValues.put(ToolLG.CHAT, true);

        StringBuilder sb = new StringBuilder();

        for (String p : game.playerLG.keySet()) {

            if (game.playerLG.get(p).isState(State.MORT)) {
                if(game.playerLG.get(p).isThief()) {
                    sb.append(String.format(game.text.getText(187), p, game.text.translateRole.get(RoleLG.VOLEUR))).append(String.format(game.text.getText(188), game.text.translateRole.get(game.playerLG.get(p).getRole()))).append("\n");
                } else
                    sb.append(String.format(game.text.getText(187), p, game.text.translateRole.get(game.playerLG.get(p).getRole()))).append("\n");
            } else {
                if (game.playerLG.get(p).isThief()) {
                    sb.append(String.format(game.text.getText(10), p, game.text.translateRole.get(RoleLG.VOLEUR))).append(String.format(game.text.getText(188), game.text.translateRole.get(game.playerLG.get(p).getRole()))).append("\n");
                } else
                    sb.append(String.format(game.text.getText(10), p, game.text.translateRole.get(game.playerLG.get(p).getRole()))).append("\n");
            }
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(String.format(game.text.getText(3), subtitles_victory));
            p.sendMessage(sb.toString());
            Title.sendTitle(p, 20, 60, 20, String.format(game.text.getText(15), ""), subtitles_victory);
            TextComponent msg = new TextComponent(game.text.getText(186));
            msg.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/GXXCVUA"));
            p.spigot().sendMessage(msg);
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(main, game::deleteGame, 600);
        Bukkit.broadcastMessage(game.getText(0));
    }


    private List<Set<String>> getAngeTeam(){

        List<Set<String>> temp= new ArrayList<>();

        for(String p1:game.playerLG.keySet()) {

            if(game.playerLG.get(p1).isState(State.LIVING)) {
                List<String> teamange= new ArrayList<>();
                teamange.add(p1);
                if(game.playerLG.get(p1).isRole(RoleLG.ANGE_GARDIEN) && !game.playerLG.get(p1).getAffectedPlayer().isEmpty() && game.playerLG.get(game.playerLG.get(p1).getAffectedPlayer().get(0)).isState(State.LIVING)){
                    teamange.add(game.playerLG.get(p1).getAffectedPlayer().get(0));
                }

                for (int i=0; i< teamange.size(); i++) {
                    for (String p2 : game.playerLG.get(teamange.get(i)).getTargetOf()) {
                        if (game.playerLG.get(p2).isRole(RoleLG.ANGE_GARDIEN) && game.playerLG.get(p2).isState(State.LIVING)) {
                            if (!teamange.contains(p2)) {
                                teamange.add(p2);
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

    private List<Set<String>> getSuccubusTeam() {

        List<Set<String>> temp = new ArrayList<>();

        for (String p1 : game.playerLG.keySet()) {

            if (game.playerLG.get(p1).isState(State.LIVING)) {
                List<String> teamSuccubus = new ArrayList<>();
                teamSuccubus.add(p1);
                PlayerLG plg = game.playerLG.get(p1);

                if (plg.isRole(RoleLG.SUCCUBUS) && !plg.getAffectedPlayer().isEmpty() && !plg.hasPower() && game.playerLG.get(plg.getAffectedPlayer().get(0)).isState(State.LIVING)) {
                    teamSuccubus.add(game.playerLG.get(p1).getAffectedPlayer().get(0));
                }

                for (int i = 0; i < teamSuccubus.size(); i++) {
                    for (String p2 : game.playerLG.get(teamSuccubus.get(i)).getTargetOf()) {
                        PlayerLG plg2 = game.playerLG.get(p2);
                        if (plg2.isRole(RoleLG.SUCCUBUS) && plg2.isState(State.LIVING) && !plg2.hasPower()) {
                            if (!teamSuccubus.contains(p2)) {
                                teamSuccubus.add(p2);
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
