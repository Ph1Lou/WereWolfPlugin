package io.github.ph1lou.pluginlg.listener;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.enumlg.ToolLG;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ChatListener implements Listener {

    final MainLG main;

    public ChatListener(MainLG main) {
        this.main = main;
    }

    @EventHandler
    private void onCommand(PlayerCommandPreprocessEvent event) {

        Player player = event.getPlayer();
        String[] args = event.getMessage().split(" ");
        if (args[0].equalsIgnoreCase("/me") || args[0].equalsIgnoreCase("/minecraft:me")) {
            event.setCancelled(true);
            player.sendMessage(main.text.getText(131));
        }

        if (args[0].equalsIgnoreCase("/tellRaw") || args[0].equalsIgnoreCase("/msg") || args[0].equalsIgnoreCase("/tell") || args[0].equalsIgnoreCase("/minecraft:tell")) {

            event.setCancelled(true);
            if (args.length <= 2) return;
            if (Bukkit.getPlayer(args[1]) == null) {
                player.sendMessage(main.text.getText(132));
                return;
            }
            if (Bukkit.getPlayer(args[1]).hasPermission("adminLG.use") || player.hasPermission("adminLG.use") || Bukkit.getPlayer(args[1]).hasPermission("tell.use") || player.hasPermission("tell.use")) {
                Player recipient = Bukkit.getPlayer(args[1]);
                StringBuilder sb = new StringBuilder();
                for (String w : args) {
                    sb.append(w).append(" ");
                }
                sb.delete(0, args[0].length() + args[1].length() + 2);
                recipient.sendMessage(String.format(main.text.getText(133), player.getName(), sb.toString()));
                player.sendMessage(String.format(main.text.getText(134), args[1], sb.toString()));
                recipient.playSound(recipient.getLocation(), Sound.ANVIL_USE, 1, 20);
                return;
            }
            player.sendMessage(main.text.getText(131));
        }
    }

    @EventHandler
    private void onChat(AsyncPlayerChatEvent event) {
        if (!main.config.configValues.get(ToolLG.CHAT)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(main.text.getText(123));
        }

    }


}
