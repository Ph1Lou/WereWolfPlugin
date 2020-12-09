package io.github.ph1lou.werewolfplugin.game;

import io.github.ph1lou.werewolfapi.ConfigWereWolfAPI;
import io.github.ph1lou.werewolfapi.LoverAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enums.*;
import io.github.ph1lou.werewolfapi.events.*;
import io.github.ph1lou.werewolfapi.versions.VersionUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
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

        if (game.getScore().getPlayerSize() == 0) {
            winner = "werewolf.end.death";
            fin();
            return;
        }

        ConfigWereWolfAPI config = game.getConfig();

        if (config.getAmnesiacLoverSize() *
                config.getLoverSize() <= 1) {

            for (LoverAPI loverAPI : game.getLoversManager().getLovers()) {
                Set<PlayerWW> lovers = new HashSet<>(loverAPI.getLovers());

                if (loverAPI.isAlive()) {
                    AroundLover aroundLover = new AroundLover(lovers);
                    Bukkit.getPluginManager().callEvent(aroundLover);

                    if (aroundLover.getPlayerWWS().size() == game.getScore().getPlayerSize()) {
                        winner = loverAPI.getKey();
                        fin();
                        return;
                    }
                }
            }
        }


        WinConditionsCheckEvent winConditionsCheckEvent = new WinConditionsCheckEvent();
        Bukkit.getPluginManager().callEvent(winConditionsCheckEvent);

        if (winConditionsCheckEvent.isCancelled()) {
            String winnerTeam = winConditionsCheckEvent.getVictoryTeam();

            if (winnerTeam.isEmpty()) return;

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
                game.getPlayerWW()
                        .stream()
                        .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                        .collect(Collectors.toSet())));

        String subtitles_victory = game.translate(winner);

        game.setState(StateGame.END);

        game.getScore().getKillCounter();

        game.getConfig().getConfigValues().put(ConfigsBase.CHAT.getKey(), true);

        for (PlayerWW playerWW1 : game.getPlayerWW()) {

            String role = game.translate(playerWW1.getRole().getKey());
            String playerName = playerWW1.getName();
            StringBuilder sb = new StringBuilder();

            if (playerWW1.isThief()) {
                role = game.translate(RolesBase.THIEF.getKey());
            }
            if (playerWW1.isState(StatePlayer.DEATH)) {
                sb.append(game.translate("werewolf.end.reveal_death", playerName, role));
            } else {
                sb.append(game.translate("werewolf.end.reveal", playerName, role));
            }
            if (playerWW1.isThief()) {
                role = game.translate(playerWW1.getRole().getKey());
                sb.append(game.translate("werewolf.end.thief", role));
            }

            EndPlayerMessageEvent endPlayerMessageEvent = new EndPlayerMessageEvent(playerWW1, sb);
            Bukkit.getPluginManager().callEvent(endPlayerMessageEvent);

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
