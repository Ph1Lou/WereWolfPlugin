package io.github.ph1lou.pluginlg;

import io.github.ph1lou.pluginlg.enumlg.*;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EndLG {

    private String subtitles_victory ="";
    private final MainLG main;

    public EndLG(MainLG main) {
        this.main=main;
    }

    public void check_victory() {

        int player = main.score.getPlayerSize();

        if (!main.loversManage.cursedLoversRange.isEmpty()) {
            return;
        }

        if (main.loversManage.loversRange.size() == 1) {

            Set<String> team = new HashSet<>(main.loversManage.loversRange.get(0));

            for (String p : main.playerLG.keySet()) {
                if (main.playerLG.get(p).isState(State.JUDGEMENT)) return;
                if (main.playerLG.get(p).isState(State.LIVING) && main.playerLG.get(p).isRole(RoleLG.CUPIDON) && main.loversManage.loversRange.get(0).contains(main.playerLG.get(p).getAffectedPlayer().get(0))) {
                    team.add(p);
                }
            }
            for (Set<String> teamange : getAngeTeam()) {
                for (String t : teamange) {
                    if (team.contains(t)) {
                        team.addAll(teamange);
                        break;
                    }
                }
            }
            if(player ==team.size()) {
                subtitles_victory =String.format(main.text.getText(4),main.text.translateRole.get(RoleLG.COUPLE));
                fin();
                return;
            }
        }

        if (main.config.configValues.get(ToolLG.VICTORY_COUPLE) && !main.loversManage.loversRange.isEmpty()) {
            return;
        }

        if(!getAngeTeam().isEmpty() && getAngeTeam().get(0).size()>1 && getAngeTeam().get(0).size()==main.score.getPlayerSize()) {
            subtitles_victory =String.format(main.text.getText(4),main.text.translateRole.get(RoleLG.ANGE_GARDIEN));
            fin();
            return;
        }

        Camp camp = null;

        for(String p:main.playerLG.keySet()) {
            if(main.playerLG.get(p).isState(State.JUDGEMENT)) return;
            PlayerLG plg = main.playerLG.get(p);
            if(plg.isState(State.LIVING)) {
                if(camp==null) {
                    camp=plg.getCamp();
                }
                else if(!plg.isCamp(camp)){
                    return;
                }
            }
        }
        if(camp==null) {

            subtitles_victory =main.text.getText(5);
            fin();
            return;
        }
        if(camp.equals(Camp.LG)) {
            subtitles_victory =String.format(main.text.getText(4),main.text.translateRole.get(RoleLG.LOUP_GAROU));
            fin();
            return;

        }
        if(camp.equals(Camp.VILLAGE)) {
            subtitles_victory =String.format(main.text.getText(4),main.text.translateRole.get(RoleLG.VILLAGEOIS));
            fin();
            return;
        }

        if(camp.equals(Camp.NEUTRAL)) {

            if (!main.config.configValues.get(ToolLG.VICTORY_NEUTRAL) && player != 1) {
                return;
            }
            RoleLG role=null;

            for(String p:main.playerLG.keySet()) {

                PlayerLG plg = main.playerLG.get(p);
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
            subtitles_victory =String.format(main.text.getText(4),main.text.translateRole.get(role));
            fin();
        }
    }

    public void fin() {

        main.setState(StateLG.FIN);
        main.spark.updateDiscord();
        main.score.getKillCounter();
        main.score.updateBoard();
        Bukkit.broadcastMessage(String.format(main.text.getText(3), subtitles_victory));
        main.config.configValues.put(ToolLG.CHAT, true);

        for(String p:main.playerLG.keySet()) {

            if(main.playerLG.get(p).isState(State.MORT)) {
                if(main.playerLG.get(p).isThief()) {
                    Bukkit.broadcastMessage(String.format(main.text.getText(187),p,main.text.translateRole.get(RoleLG.VOLEUR))+String.format(main.text.getText(188),main.text.translateRole.get(main.playerLG.get(p).getRole())));
                }
                else Bukkit.broadcastMessage(String.format(main.text.getText(187),p,main.text.translateRole.get(main.playerLG.get(p).getRole())));
            }
            else {
                if(main.playerLG.get(p).isThief()) {
                    Bukkit.broadcastMessage(String.format(main.text.getText(10),p,main.text.translateRole.get(RoleLG.VOLEUR))+String.format(main.text.getText(188),main.text.translateRole.get(main.playerLG.get(p).getRole())));
                }
                else Bukkit.broadcastMessage(String.format(main.text.getText(10),p,main.text.translateRole.get(main.playerLG.get(p).getRole())));
            }
        }

        for(Player player:Bukkit.getOnlinePlayers()) {
            Title.sendTitle(player,20,60, 20,String.format(main.text.getText(15),""), subtitles_victory);
            TextComponent msg = new TextComponent(main.text.getText(186));
            msg.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,"https://discord.gg/GXXCVUA"));
            player.spigot().sendMessage(msg);
        }

    }


    private List<Set<String>> getAngeTeam(){

        List<Set<String>> temp= new ArrayList<>();

        for(String p1:main.playerLG.keySet()) {

            if(main.playerLG.get(p1).isState(State.LIVING)) {
                List<String> teamange= new ArrayList<>();
                teamange.add(p1);
                if(main.playerLG.get(p1).isRole(RoleLG.ANGE_GARDIEN) && !main.playerLG.get(p1).getAffectedPlayer().isEmpty() && main.playerLG.get(main.playerLG.get(p1).getAffectedPlayer().get(0)).isState(State.LIVING)){
                    teamange.add(main.playerLG.get(p1).getAffectedPlayer().get(0));
                }

                for (int i=0;i< teamange.size();i++) {
                    if (!main.playerLG.get(teamange.get(i)).getTargetOf().isEmpty()) {
                        for (String p2 : main.playerLG.get(teamange.get(i)).getTargetOf()) {
                            if (main.playerLG.get(p2).isRole(RoleLG.ANGE_GARDIEN) && main.playerLG.get(p2).isState(State.LIVING)) {
                                if(!teamange.contains(p2)){
                                    teamange.add(p2);
                                }
                            }
                        }
                    }
                }
                int i;
                for(i=0;i<temp.size();i++){
                    if(temp.get(i).size()<teamange.size()){
                        temp.add(i,new HashSet<>(teamange));
                        break;
                    }
                }
                if(i==temp.size()){
                    temp.add(new HashSet<>(teamange));
                }
            }
        }
        return temp;
    }

    public String getVictoryTeam(){
        return this.subtitles_victory;
    }
}
