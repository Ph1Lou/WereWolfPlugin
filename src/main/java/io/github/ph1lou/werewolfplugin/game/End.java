package io.github.ph1lou.werewolfplugin.game;


import io.github.ph1lou.werewolfapi.IConfiguration;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.enums.Category;
import io.github.ph1lou.werewolfapi.enums.ConfigsBase;
import io.github.ph1lou.werewolfapi.enums.LoverType;
import io.github.ph1lou.werewolfapi.enums.StateGame;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.TimersBase;
import io.github.ph1lou.werewolfapi.events.game.game_cycle.WinEvent;
import io.github.ph1lou.werewolfapi.events.game.utils.CountRemainingRolesCategoriesEvent;
import io.github.ph1lou.werewolfapi.events.game.utils.EndPlayerMessageEvent;
import io.github.ph1lou.werewolfapi.events.game.utils.WinConditionsCheckEvent;
import io.github.ph1lou.werewolfapi.events.lovers.AroundLover;
import io.github.ph1lou.werewolfapi.utils.BukkitUtils;
import io.github.ph1lou.werewolfapi.utils.Utils;
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
        IConfiguration config = game.getConfig();

        if (config.getLoverCount(LoverType.AMNESIAC_LOVER.getKey()) *
                config.getLoverCount(LoverType.LOVER.getKey()) <= 1) {

            game.getLoversManager().getLovers().stream()
                    .filter(ILover -> !ILover.isKey(LoverType.CURSED_LOVER.getKey()))
                    .forEach(ILover -> {
                        Set<IPlayerWW> lovers = new HashSet<>(ILover.getLovers());

                        if (ILover.isAlive()) {
                            AroundLover aroundLover = new AroundLover(lovers);
                            Bukkit.getPluginManager().callEvent(aroundLover);

                            if (aroundLover.getPlayerWWS().size() == game.getScore().getPlayerSize()) {
                                winner = ILover.getKey();
                                fin();
                            }
                        }
                    });
        }

        if (winner != null) return;

        WinConditionsCheckEvent winConditionsCheckEvent = new WinConditionsCheckEvent();
        Bukkit.getPluginManager().callEvent(winConditionsCheckEvent);

        if (winConditionsCheckEvent.isCancelled()) {
            String winnerTeam = winConditionsCheckEvent.getVictoryTeam();

            if (winnerTeam.isEmpty()) return;

            winner = winnerTeam;
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

        game.getConfig().setConfig(ConfigsBase.CHAT.getKey(), true);

        for (IPlayerWW playerWW1 : game.getPlayerWW()) {

            String role = game.translate(playerWW1.getRole().getDeathRole());
            String playerName = playerWW1.getName();
            StringBuilder sb = new StringBuilder();

            if (playerWW1.isState(StatePlayer.DEATH)) {
                sb.append(game.translate("werewolf.end.reveal_death", playerName, role));
            } else {
                sb.append(game.translate("werewolf.end.reveal", playerName, role));
            }
            if (!playerWW1.getRole().getKey().equals(playerWW1.getRole().getDeathRole())) {
                sb.append(game.translate("werewolf.end.thief", game.translate(playerWW1.getRole().getKey())));
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

        BukkitUtils.scheduleSyncDelayedTask(game::stopGame, 20L * game.getConfig().getTimerValue(TimersBase.AUTO_RESTART_DURATION.getKey()));
        Bukkit.broadcastMessage(game.translate("werewolf.announcement.restart", Utils.conversion(game.getConfig().getTimerValue(TimersBase.AUTO_RESTART_DURATION.getKey()))));
    }




}
