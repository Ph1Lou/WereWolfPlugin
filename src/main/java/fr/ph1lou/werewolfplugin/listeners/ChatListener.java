package fr.ph1lou.werewolfplugin.listeners;

import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.enums.Sound;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.events.werewolf.WereWolfSiteChatEvent;
import fr.ph1lou.werewolfapi.game.IModerationManager;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfplugin.statistiks.StatistiksUtils;
import fr.ph1lou.werewolfplugin.utils.Contributor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ChatListener implements Listener {

    private final WereWolfAPI game;
    private final List<? extends Contributor> contributors;

    public ChatListener(WereWolfAPI game) {
        this.game = game;
        this.contributors = StatistiksUtils.getContributors();
    }

    @EventHandler
    private void onCommand(PlayerCommandPreprocessEvent event) {

        if(event.isCancelled()){
            return;
        }

        Player player = event.getPlayer();
        String[] args = event.getMessage().split(" ");
        IModerationManager moderationManager = game.getModerationManager();

        if (args[0].equalsIgnoreCase("/rl") ||
                args[0].equalsIgnoreCase("/reload") ||
                args[0].equalsIgnoreCase("/bukkit:rl") ||
                args[0].equalsIgnoreCase("/bukkit:reload")) {
            event.setCancelled(true);
            player.sendMessage(game.translate(Prefix.RED, "werewolf.check.disabled_command"));
        } else if (args[0].equalsIgnoreCase("/stop") ||
                args[0].equalsIgnoreCase("/bukkit:stop")) {
            event.setCancelled(true);
            player.performCommand(String.format("a %s", game.translate("werewolf.commands.admin.stop.command")));
        } else if (args[0].equalsIgnoreCase("/me") ||
                args[0].equalsIgnoreCase("/minecraft:me")) {
            event.setCancelled(true);
            player.sendMessage(game.translate(Prefix.RED, "werewolf.check.disabled_command"));
        } else if (args[0].equalsIgnoreCase("/tellRaw") ||
                args[0].equalsIgnoreCase("/msg") ||
                args[0].equalsIgnoreCase("/tell") ||
                args[0].equalsIgnoreCase("/minecraft:tell")) {

            event.setCancelled(true);
            if (args.length <= 2) return;

            Player recipient = Bukkit.getPlayer(args[1]);

            if (recipient == null) {
                player.sendMessage(game.
                        translate(Prefix.RED, "werewolf.check.offline_player"));
                return;
            }

            if (!recipient.hasPermission("a.tell") && !player.hasPermission("a.tell")) {

                if (!moderationManager.isStaff(recipient.getUniqueId()) &&
                        !moderationManager.isStaff(player.getUniqueId())) {
                    player.sendMessage(game.translate(Prefix.RED, "werewolf.check.permission_denied"));
                    return;
                }
            }

            StringBuilder sb = new StringBuilder();
            for (String w : args) {
                sb.append(w).append(" ");
            }
            sb.delete(0, args[0].length() + args[1].length() + 2);
            recipient.sendMessage(game.translate("werewolf.commands.player.message.received",
                    Formatter.player(player.getName()),
                    Formatter.format("&message&", sb.toString())));
            player.sendMessage(game.translate("werewolf.commands.player.message.send",
                    Formatter.player(args[1]),
                    Formatter.format("&message&", sb.toString())));
            Sound.ANVIL_USE.play(recipient);
        }
    }

    @EventHandler
    private void onChat(AsyncPlayerChatEvent event) {

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);
        IModerationManager moderationManager = game.getModerationManager();
        String format;
        Contributor contributor = this.contributors
                .stream()
                .filter(contributor1 -> contributor1.getUuid().equals(player.getUniqueId()) ||
                        (playerWW != null &&
                                contributor1.getUuid().equals(playerWW.getReviewUUID())))
                .findFirst()
                .orElse(null);

        if (contributor != null) {

            switch (contributor.getLevel()) {
                case 0:
                    format = "§b✦§r %s"; //Ph1Lou
                    break;
                case 1:
                    format = "§5Ѧ§r %s"; //friend
                    break;
                case 2:
                    format = "§9Ͻ§r %s"; //addon maker
                    break;
                case 3:
                    format = "§4ǂ§r %s"; //donator
                    break;
                case 4:
                    format = "§aΨ§r %s"; //tester
                    break;
                default:
                    format = "§6ø§r %s";
                    break;
                //¤
            }

            format = game.translate("werewolf.commands.admin.chat.template",
                    Formatter.player(format),
                    Formatter.format("&message&", "%s"));
        } else {
            format = game.translate("werewolf.commands.admin.chat.template",
                    Formatter.player("%s"),
                    Formatter.format("&message&", "%s"));
        }

        if (moderationManager.getHosts().contains(uuid)) {
            event.setFormat(game.translate("werewolf.commands.admin.host.tag") + format);
        } else if (moderationManager.getModerators().contains(uuid)) {
            event.setFormat(game.translate("werewolf.commands.admin.moderator.tag") + format);
        } else {
            event.setFormat(format);
        }

        if (!game.getConfig().isConfigActive(ConfigBase.CHAT)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(game.translate(Prefix.RED, "werewolf.commands.admin.chat.off"));

        } else if (game.getConfig().isConfigActive(ConfigBase.PROXIMITY_CHAT) &&
                !game.isState(StateGame.LOBBY)) {
            event.setCancelled(true);

            for (Player p : Bukkit.getOnlinePlayers()) {

                if (p.getWorld().equals(player.getWorld())) {
                    double distance = p.getLocation().distance(player.getLocation());

                    if (distance < 20) {
                        p.sendMessage(String.format(event.getFormat(), player.getName(),
                                event.getMessage()));

                    } else if (distance <= 50) {
                        p.sendMessage(String.format(event.getFormat(),
                                player.getName(),
                                obfuscation(event.getMessage(), ((int) distance - 20) / 70f)));
                    }
                }
            }

        }
    }

    private String obfuscation(String message, float percentage) {

        StringBuilder returnMessage = new StringBuilder();
        for (int i = 0; i < message.length(); i++) {
            char charOfMessage = message.charAt(i);

            if (Math.random() < percentage) {
                if (charOfMessage != ' ') {
                    returnMessage.append("?");
                } else {
                    returnMessage.append(" ");
                }
            } else {
                returnMessage.append(charOfMessage);
            }
        }

        return returnMessage.toString();
    }

    @EventHandler(ignoreCancelled = true)
    public void onChatWW(WereWolfSiteChatEvent event) {

        game.getModerationManager().getModerators().stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .forEach(player -> player.sendMessage(game.translate("werewolf.commands.player.ww_chat.modo",
                        Formatter.format("&name&", event.getPlayerWW().getName()),
                        Formatter.format("&message&", event.getMessage()))));

    }
}

