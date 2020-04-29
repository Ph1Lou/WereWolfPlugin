package io.github.ph1lou.pluginlg.listener;

import io.github.ph1lou.pluginlg.enumlg.ToolLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ChatListener implements Listener {

    final GameManager game;

    public ChatListener(GameManager game) {
        this.game = game;
    }

    @EventHandler
    private void onCommand(PlayerCommandPreprocessEvent event) {

        Player player = event.getPlayer();
        String[] args = event.getMessage().split(" ");

        if (args[0].equalsIgnoreCase("/rl") || args[0].equalsIgnoreCase("/reload") || args[0].equalsIgnoreCase("/bukkit:rl") || args[0].equalsIgnoreCase("/bukkit:reload")) {
            event.setCancelled(true);
            player.sendMessage(game.getText(274));
        } else if (args[0].equalsIgnoreCase("/me") || args[0].equalsIgnoreCase("/minecraft:me")) {
            event.setCancelled(true);
            player.sendMessage(game.text.getText(131));
        } else if (args[0].equalsIgnoreCase("/tellRaw") || args[0].equalsIgnoreCase("/msg") || args[0].equalsIgnoreCase("/tell") || args[0].equalsIgnoreCase("/minecraft:tell")) {

            event.setCancelled(true);
            if (args.length <= 2) return;
            if (Bukkit.getPlayer(args[1]) == null) {
                player.sendMessage(game.text.getText(132));
                return;
            }

            Player recipient = Bukkit.getPlayer(args[1]);

            if (!recipient.hasPermission("a.use") && !player.hasPermission("tell.use")) {

                if (!game.getHosts().contains(recipient.getUniqueId()) && !game.getHosts().contains(player.getUniqueId())) {
                    if (!game.getModerators().contains(recipient.getUniqueId()) && !game.getModerators().contains(player.getUniqueId())) {
                        player.sendMessage(game.text.getText(131));
                        return;
                    }
                }
            }

            StringBuilder sb = new StringBuilder();
            for (String w : args) {
                sb.append(w).append(" ");
            }
            sb.delete(0, args[0].length() + args[1].length() + 2);
            recipient.sendMessage(String.format(game.text.getText(133), player.getName(), sb.toString()));
            player.sendMessage(String.format(game.text.getText(134), args[1], sb.toString()));
            recipient.playSound(recipient.getLocation(), Sound.ANVIL_USE, 1, 20);
        }


    }

    @EventHandler
    private void onChat(AsyncPlayerChatEvent event) {

        if (!game.config.configValues.get(ToolLG.CHAT)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(game.text.getText(123));
        }

    }


}
