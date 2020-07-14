package io.github.ph1lou.werewolfplugin.tasks;

import io.github.ph1lou.werewolfapi.enumlg.StateLG;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;


public class LobbyTask extends BukkitRunnable {

    private final Main main;
    private final GameManager game;

    public LobbyTask(Main main, GameManager game) {
        this.main = main;
        this.game=game;
    }

    @Override
    public void run() {

        if (game.isState(StateLG.END)) {
            cancel();
            return;
        }

        game.getScore().updateBoard();

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (game.getWft() == null) {
                if (p.isOp() || p.hasPermission("a.use") || p.hasPermission("a.generation.use") || game.getHosts().contains(p.getUniqueId())) {
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(game.translate("werewolf.action_bar.generation")));
                }
            } else if (game.getWft().getPercentageCompleted() < 100) {
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(game.translate("werewolf.action_bar.generation",new DecimalFormat("0.0").format(game.getWft().getPercentageCompleted()))));
            } else {
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(game.translate("werewolf.action_bar.complete")));
            }
        }

        if (game.isState(StateLG.TRANSPORTATION)) {
            TransportationTask transportationTask = new TransportationTask(main,game);
            transportationTask.runTaskTimer(main, 0, 4);
            cancel();
        }
    }
}
