package io.github.ph1lou.werewolfplugin.game;

import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enumlg.*;
import io.github.ph1lou.werewolfapi.events.*;
import io.github.ph1lou.werewolfapi.versions.VersionUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Collectors;

public class End {


    private String winner = null;
    private final GameManager game;

    public End(GameManager game) {
        this.game = game;
    }

    public void check_victory() {

        if (game.getConfig().isTrollSV()) return;

        if (game.isState(StateGame.END)) return;

        if (!game.getCursedLoversRange().isEmpty()) {
            return; //Si il y a encore un couple maudit, comme ils ne peuvent pas gagner ensemble on cancel
        }

        if (game.getScore().getPlayerSize() == 0) {
            winner = "werewolf.end.death";
            fin();
            return;
        }

        if (game.getLoversRange().size() == 1) {
            AroundLover aroundLover = new AroundLover(new HashSet<>(game.getLoversRange().get(0)));
            Bukkit.getPluginManager().callEvent(aroundLover);

            if (aroundLover.getUuidS().size() == game.getScore().getPlayerSize()) {
                winner = RolesBase.LOVER.getKey();
                fin();
                return;
            }
        }

        if (game.getAmnesiacLoversRange().size() == 1) {
            AroundLover aroundLover = new AroundLover(new HashSet<>(game.getAmnesiacLoversRange().get(0)));
            Bukkit.getPluginManager().callEvent(aroundLover);

            if (aroundLover.getUuidS().size() == game.getScore().getPlayerSize()) {
                winner = RolesBase.AMNESIAC_LOVER.getKey();
                fin();
                return;
            }
        }

        if (game.getConfig().getConfigValues().get(ConfigsBase.VICTORY_LOVERS.getKey())
                && (!game.getLoversRange().isEmpty() ||
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

        CountRemainingRolesCategoriesEvent event =
                new CountRemainingRolesCategoriesEvent();

        Bukkit.getPluginManager().callEvent(event);

        if (event.getWerewolf() == game.getScore().getPlayerSize()) {
            winner = Category.WEREWOLF.getKey();
            fin();
            return;


        }
        if (event.getVillager() == game.getScore().getPlayerSize()) {
            winner = Category.VILLAGER.getKey();
            fin();
        }
    }

    public void fin() {

        Bukkit.getPluginManager().callEvent(new WinEvent(winner,
                game.getPlayersWW().values()
                        .stream()
                        .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                        .map(playerWW -> playerWW.getRole().getPlayerUUID())
                        .collect(Collectors.toList())));

        String subtitles_victory = game.translate(winner);

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




}
