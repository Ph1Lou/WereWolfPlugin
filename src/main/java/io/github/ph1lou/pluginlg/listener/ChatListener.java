package io.github.ph1lou.pluginlg.listener;


import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.enumlg.ToolLG;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.UUID;

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
            player.sendMessage(game.translate("werewolf.check.disabled_command"));
        } else if (args[0].equalsIgnoreCase("/me") || args[0].equalsIgnoreCase("/minecraft:me")) {
            event.setCancelled(true);
            player.sendMessage(game.translate("werewolf.check.disabled_command"));
        } else if (args[0].equalsIgnoreCase("/tellRaw") || args[0].equalsIgnoreCase("/msg") || args[0].equalsIgnoreCase("/tell") || args[0].equalsIgnoreCase("/minecraft:tell")) {

            event.setCancelled(true);
            if (args.length <= 2) return;
            if (Bukkit.getPlayer(args[1]) == null) {
                player.sendMessage(game.
                        translate("werewolf.check.offline_player"));
                return;
            }

            Player recipient = Bukkit.getPlayer(args[1]);

            if (!recipient.hasPermission("a.use") && !player.hasPermission("tell.use")) {

                if (!game.getHosts().contains(recipient.getUniqueId()) && !game.getHosts().contains(player.getUniqueId())) {
                    if (!game.getModerators().contains(recipient.getUniqueId()) && !game.getModerators().contains(player.getUniqueId())) {
                        player.sendMessage(game.translate("werewolf.check.permission_denied"));
                        return;
                    }
                }
            }

            StringBuilder sb = new StringBuilder();
            for (String w : args) {
                sb.append(w).append(" ");
            }
            sb.delete(0, args[0].length() + args[1].length() + 2);
            recipient.sendMessage(game.translate("werewolf.commands.message.received", player.getName(), sb.toString()));
            player.sendMessage(game.translate("werewolf.commands.message.send", args[1], sb.toString()));
            recipient.playSound(recipient.getLocation(), Sound.ANVIL_USE, 1, 20);
        }


    }

    @EventHandler
    private void onChat(AsyncPlayerChatEvent event) {

        if (!game.getConfig().getConfigValues().get(ToolLG.CHAT)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(game.translate("werewolf.commands.admin.chat.off"));
        }
        UUID uuid = event.getPlayer().getUniqueId();

        String format;

        if(event.getPlayer().getName().equals("Ph1Lou")){
            format=game.translate("werewolf.commands.admin.chat.template","§5%s§r","%s");
        }
        else format=game.translate("werewolf.commands.admin.chat.template","%s","%s");

        if(game.getHosts().contains(uuid)){
            event.setFormat(game.translate("werewolf.commands.admin.host.tag")+format);
        }
        else if(game.getModerators().contains(uuid)){
            event.setFormat(game.translate("werewolf.commands.admin.moderator.tag")+format);
        }
        else event.setFormat(format);

    }


}
