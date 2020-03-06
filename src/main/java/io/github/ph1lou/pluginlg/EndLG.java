package io.github.ph1lou.pluginlg;

import io.github.ph1lou.pluginlg.enumlg.*;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class EndLG {

    private String subtitles_victory ="";
    private final MainLG main;

    public EndLG(MainLG main) {
        this.main=main;
    }

    public void check_victory() {

        int player=main.score.getPlayerSize();

        if(main.couple_manage.couple_range.size()==1) {

            Set<String> team = new HashSet<>(main.couple_manage.couple_range.get(0));

            for(String p:main.playerlg.keySet()) {
                if(main.playerlg.get(p).isState(State.JUGEMENT)) return;
                if (main.playerlg.get(p).isState(State.VIVANT) && main.playerlg.get(p).isRole(RoleLG.CUPIDON) && main.couple_manage.couple_range.get(0).contains(main.playerlg.get(p).getAffectedPlayer().get(0))){
                    team.add(p);
                }
            }
            for(Set<String> teamange:getAngeTeam()){
                for(String t:teamange){
                    if(team.contains(t)){
                        team.addAll(teamange);
                        break;
                    }
                }
            }
            if(player ==team.size()) {
                subtitles_victory =main.text.getText(4)+main.text.translaterole.get(RoleLG.COUPLE);
                fin();
                return;
            }
        }

        if(main.config.tool_switch.get(ToolLG.VICTORY_COUPLE) && !main.couple_manage.couple_range.isEmpty()) {
            return;
        }

        if(!getAngeTeam().isEmpty() && getAngeTeam().get(0).size()>1 && getAngeTeam().get(0).size()==main.score.getPlayerSize()) {
            subtitles_victory =main.text.getText(4)+main.text.translaterole.get(RoleLG.ANGE_GARDIEN);
            fin();
            return;
        }

        Camp camp = null;

        for(String p:main.playerlg.keySet()) {
            if(main.playerlg.get(p).isState(State.JUGEMENT)) return;
            PlayerLG plg = main.playerlg.get(p);
            if(plg.isState(State.VIVANT)) {
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
            subtitles_victory =main.text.getText(4)+main.text.translaterole.get(RoleLG.LOUP_GAROU);
            fin();
            return;

        }
        if(camp.equals(Camp.VILLAGE)) {
            subtitles_victory =main.text.getText(4)+main.text.translaterole.get(RoleLG.VILLAGEOIS);
            fin();
            return;
        }

        if(camp.equals(Camp.NEUTRE)) {

            if(!main.config.tool_switch.get(ToolLG.VICTORY_NEUTRAL) && player !=1 ) {
                return;
            }
            RoleLG role=null;

            for(String p:main.playerlg.keySet()) {

                PlayerLG plg = main.playerlg.get(p);
                if(plg.isState(State.VIVANT)) {
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
            subtitles_victory =main.text.getText(4)+main.text.translaterole.get(role);
            fin();
        }
    }

    public void fin() {

        main.setState(StateLG.FIN);
        main.score.getKillCounter();
        Bukkit.broadcastMessage(main.text.esthetique("§m", "§6",main.text.getText(3)+ subtitles_victory));
        main.config.tool_switch.put(ToolLG.CHAT,true);

        for(String p:main.playerlg.keySet()) {

            if(main.playerlg.get(p).isState(State.MORT)) {
                if(main.playerlg.get(p).isVoleur()) {
                    Bukkit.broadcastMessage("§m§l"+p+main.text.getText(187)+main.text.translaterole.get(RoleLG.VOLEUR)+main.text.getText(188)+main.text.translaterole.get(main.playerlg.get(p).getRole()));
                }
                else Bukkit.broadcastMessage("§m§l"+p+main.text.getText(187)+main.text.translaterole.get(main.playerlg.get(p).getRole()));
            }
            else {
                if(main.playerlg.get(p).isVoleur()) {
                    Bukkit.broadcastMessage("§e§l"+p+main.text.getText(187)+main.text.translaterole.get(RoleLG.VOLEUR)+main.text.getText(188)+main.text.translaterole.get(main.playerlg.get(p).getRole()));
                }
                else Bukkit.broadcastMessage("§e§l"+p+main.text.getText(187)+main.text.translaterole.get(main.playerlg.get(p).getRole()));
            }
            main.score.updateBoard();
        }
        for(Player player:Bukkit.getOnlinePlayers()) {
            Title.sendTitle(player,20,60, 20,main.text.getText(3), subtitles_victory);

            TextComponent msg = new TextComponent(main.text.getText(186));
            msg.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,"https://discord.gg/GXXCVUA"));
            player.spigot().sendMessage(msg);

        }
    }


    private List<Set<String>> getAngeTeam(){

        List<Set<String>> temp= new ArrayList<>();

        for(String p1:main.playerlg.keySet()) {

            if(main.playerlg.get(p1).isState(State.VIVANT)) {
                List<String> teamange= new ArrayList<>();
                teamange.add(p1);
                if(main.playerlg.get(p1).isRole(RoleLG.ANGE_GARDIEN) && !main.playerlg.get(p1).getAffectedPlayer().isEmpty() && main.playerlg.get(main.playerlg.get(p1).getAffectedPlayer().get(0)).isState(State.VIVANT)){
                    teamange.add(main.playerlg.get(p1).getAffectedPlayer().get(0));
                }

                for (int i=0;i< teamange.size();i++) {
                    if (!main.playerlg.get(teamange.get(i)).getTargetOf().isEmpty()) {
                        for (String p2 : main.playerlg.get(teamange.get(i)).getTargetOf()) {
                            if (main.playerlg.get(p2).isRole(RoleLG.ANGE_GARDIEN) && main.playerlg.get(p2).isState(State.VIVANT)) {
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
}
