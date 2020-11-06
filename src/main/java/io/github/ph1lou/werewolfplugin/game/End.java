package io.github.ph1lou.werewolfplugin.game;

import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enumlg.*;
import io.github.ph1lou.werewolfapi.events.EndPlayerMessageEvent;
import io.github.ph1lou.werewolfapi.events.WinConditionsCheckEvent;
import io.github.ph1lou.werewolfapi.events.WinEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.AngelRole;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfapi.versions.VersionUtils;
import io.github.ph1lou.werewolfplugin.roles.neutrals.Succubus;
import io.github.ph1lou.werewolfplugin.roles.villagers.Cupid;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class End {


    private String winner = null;
    private final GameManager game;

    public End(GameManager game) {
        this.game = game;
    }

    public void check_victory() {

        if (game.getConfig().isTrollSV()) return;

        int player = game.getScore().getPlayerSize();

        if (!game.getCursedLoversRange().isEmpty()) {
            return;
        }

        if (game.isState(StateGame.END)) return;

        List<Set<UUID>> teamsAngel = getAngeTeam();
        List<Set<UUID>> teamsSuccubus = getSuccubusTeam();

        if (game.getLoversRange().size() + game.getAmnesiacLoversRange().size() == 1) {

            Set<UUID> team;

            if(game.getLoversRange().size()==1){
                team = new HashSet<>(game.getLoversRange().get(0));
                for (UUID uuid : game.getPlayersWW().keySet()) {
                    PlayerWW plg =game.getPlayersWW().get(uuid);
                    if (plg.isState(StatePlayer.JUDGEMENT)) return;
                    if (plg.isState(StatePlayer.ALIVE) && plg.getRole().isKey(RolesBase.CUPID.getKey())) {
                        Cupid cupid = (Cupid) plg.getRole();
                        if (game.getLoversRange().get(0).contains(cupid.getAffectedPlayers().get(0))) {
                            team.add(uuid);
                        }
                    }
                }
            }
            else{
                team = new HashSet<>(game.getAmnesiacLoversRange().get(0));
                for(UUID uuid:game.getAmnesiacLoversRange().get(0)){
                    if(game.getPlayersWW().get(uuid).isState(StatePlayer.JUDGEMENT)) return;
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
                winner = game.getLoversRange().size() == 1 ?
                        RolesBase.LOVER.getKey() : RolesBase.AMNESIAC_LOVER.getKey();
                fin();
                return;
            }
        }

        if (game.getConfig().getConfigValues().get(ConfigsBase.VICTORY_LOVERS.getKey()) &&
                (!game.getLoversRange().isEmpty() ||
                        !game.getAmnesiacLoversRange().isEmpty())) {
            return;
        }
        WinConditionsCheckEvent winConditionsCheckEvent = new WinConditionsCheckEvent();
        Bukkit.getPluginManager().callEvent(winConditionsCheckEvent);

        if (winConditionsCheckEvent.isCancelled()) {
            winner = winConditionsCheckEvent.getVictoryTeam();
            fin();
            return;
        }

        if (!teamsAngel.isEmpty() && teamsAngel.get(0).size() > 1 &&
                teamsAngel.get(0).size() == game.getScore().getPlayerSize()) {

            winner = RolesBase.GUARDIAN_ANGEL.getKey();
            fin();
            return;
        }

        if (!teamsSuccubus.isEmpty() && teamsSuccubus.get(0).size() > 1 &&
                teamsSuccubus.get(0).size() == game.getScore().getPlayerSize()) {

            winner = RolesBase.SUCCUBUS.getKey();
            fin();
            return;
        }

        Camp camp = null;

        for (UUID uuid : game.getPlayersWW().keySet()) {
            if (game.getPlayersWW().get(uuid).isState(StatePlayer.JUDGEMENT)) return;
            PlayerWW plg = game.getPlayersWW().get(uuid);
            if (plg.isState(StatePlayer.ALIVE)) {
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
            winner = "werewolf.categories.werewolf";
            fin();
            return;

        }
        if(camp.equals(Camp.VILLAGER)) {
            winner = "werewolf.categories.villager";
            fin();
            return;
        }

        if(camp.equals(Camp.NEUTRAL)) {

            if (player != 1) {
                return;
            }
            Roles role=null;

            for(UUID uuid:game.getPlayersWW().keySet()) {

                PlayerWW plg = game.getPlayersWW().get(uuid);
                if(plg.isState(StatePlayer.ALIVE)) {
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
            winner = role.getKey();
            fin();
        }
    }

    public void fin() {

        Bukkit.getPluginManager().callEvent(new WinEvent(winner == null ?
                "werewolf.end.death" : winner,
                game.getPlayersWW().values()
                        .stream()
                        .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                        .map(playerWW -> playerWW.getRole().getPlayerUUID())
                        .collect(Collectors.toList())));

        String subtitles_victory = winner == null ?
                game.translate("werewolf.end.death") : game.translate(winner);

        game.setState(StateGame.END);

        game.getScore().getKillCounter();

        game.getConfig().getConfigValues().put(ConfigsBase.CHAT.getKey(), true);

        for (UUID uuid : game.getPlayersWW().keySet()) {

            PlayerWW plg = game.getPlayersWW().get(uuid);
            String role = game.translate(plg.getRole().getKey());
            String playerName = plg.getName();
            StringBuilder sb = new StringBuilder();

            if(plg.isThief()){
                role = game.translate(RolesBase.THIEF.getKey());
            }
            if (plg.isState(StatePlayer.DEATH)) {
                sb.append(game.translate("werewolf.end.reveal_death", playerName, role));
            } else {
                sb.append(game.translate("werewolf.end.reveal", playerName, role));
            }
            if(plg.isThief()){
                role = game.translate(plg.getRole().getKey());
                sb.append(game.translate("werewolf.end.thief", role));
            }

            EndPlayerMessageEvent endPlayerMessageEvent = new EndPlayerMessageEvent(uuid,sb);
            Bukkit.getPluginManager().callEvent(endPlayerMessageEvent);

            if(!plg.getLovers().isEmpty()){
                StringBuilder sb2 = new StringBuilder();
                for(UUID lover:plg.getLovers()){
                    sb2.append(game.getPlayersWW().get(lover).getName()).append(" ");
                }
                sb.append(game.translate("werewolf.end.lover",sb2.toString()));
            }
            if(plg.getCursedLovers()!=null){
                sb.append(game.translate("werewolf.end.cursed_lover",game.getPlayersWW().get(plg.getCursedLovers()).getName()));
            }
            if(plg.getAmnesiacLoverUUID()!=null){
                sb.append(game.translate("werewolf.end.lover",game.getPlayersWW().get(plg.getAmnesiacLoverUUID()).getName()));
            }
            Bukkit.broadcastMessage(sb.toString());
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(game.translate("werewolf.end.message", subtitles_victory));
            VersionUtils.getVersionUtils().sendTitle(p, game.translate("werewolf.end.victory"), subtitles_victory, 20, 60, 20);
            TextComponent msg = new TextComponent(game.translate("werewolf.bug"));
            msg.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/GXXCVUA"));
            p.spigot().sendMessage(msg);
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(game.getMain(), game::stopGame, 600);
        Bukkit.broadcastMessage(game.translate("werewolf.announcement.restart"));
    }


    private List<Set<UUID>> getAngeTeam(){

        List<Set<UUID>> temp= new ArrayList<>();

        for(UUID uuid:game.getPlayersWW().keySet()) {

            PlayerWW plg1 = game.getPlayersWW().get(uuid);

            if(plg1.isState(StatePlayer.ALIVE)) {
                List<UUID> teamange= new ArrayList<>();
                teamange.add(uuid);
                if(plg1.getRole() instanceof AngelRole){
                    AngelRole angel = (AngelRole) plg1.getRole();
                    if(angel.isChoice(AngelForm.GUARDIAN_ANGEL)){
                        if(angel instanceof AffectedPlayers){
                            AffectedPlayers affectedPlayers = (AffectedPlayers) angel;

                            if(!affectedPlayers.getAffectedPlayers().isEmpty() && game.getPlayersWW().get(affectedPlayers.getAffectedPlayers().get(0)).isState(StatePlayer.ALIVE)){
                                teamange.add(affectedPlayers.getAffectedPlayers().get(0));
                            }
                        }

                    }
                }

                for (int i=0; i< teamange.size(); i++) {
                    for (UUID uuid2 : game.getPlayersWW().keySet()) {

                        PlayerWW plg2 = game.getPlayersWW().get(uuid2);

                        if (plg2.getRole() instanceof AngelRole){
                            AngelRole angel = (AngelRole) plg2.getRole();

                            if(angel.isChoice(AngelForm.GUARDIAN_ANGEL)){
                                if(angel instanceof AffectedPlayers) {
                                    AffectedPlayers affectedPlayers = (AffectedPlayers) angel;

                                    if(affectedPlayers.getAffectedPlayers().contains(teamange.get(i))){
                                        if(plg2.isState(StatePlayer.ALIVE)) {
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

        for (UUID uuid : game.getPlayersWW().keySet()) {

            PlayerWW plg1 = game.getPlayersWW().get(uuid);

            if (plg1.isState(StatePlayer.ALIVE)) {

                List<UUID> teamSuccubus = new ArrayList<>();
                teamSuccubus.add(uuid);

                if (plg1.getRole().isKey(RolesBase.SUCCUBUS.getKey())) {
                    Succubus succubus = (Succubus) plg1.getRole();
                    if (!succubus.getAffectedPlayers().isEmpty() && !succubus.hasPower() && game.getPlayersWW().get(succubus.getAffectedPlayers().get(0)).isState(StatePlayer.ALIVE)) {
                        teamSuccubus.add(succubus.getAffectedPlayers().get(0));
                    }
                }

                for (int i = 0; i < teamSuccubus.size(); i++) {
                    for (UUID uuid2 : game.getPlayersWW().keySet()) {
                        PlayerWW plg2 = game.getPlayersWW().get(uuid2);
                        if (plg2.getRole().isKey(RolesBase.SUCCUBUS.getKey())) {
                            Succubus succubus = (Succubus) plg2.getRole();
                            if (succubus.getAffectedPlayers().contains(teamSuccubus.get(i))) {
                                if (plg2.isState(StatePlayer.ALIVE) && !succubus.hasPower()) {
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
