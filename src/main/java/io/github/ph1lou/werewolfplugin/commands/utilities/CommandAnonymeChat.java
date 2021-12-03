package io.github.ph1lou.werewolfplugin.commands.utilities;

import io.github.ph1lou.werewolfapi.Formatter;
import io.github.ph1lou.werewolfapi.ICommand;
import io.github.ph1lou.werewolfapi.IModerationManager;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Prefix;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommandAnonymeChat implements ICommand {

    private final int cesar = (int) (Math.random() * 26) + 1;
    private final Map<String, UUID> players = new HashMap<>();
    private final char[] alphabet1 = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '_'};

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        StringBuilder sb = new StringBuilder();


        if (args.length == 0) {
            player.sendMessage(game.translate(Prefix.RED.getKey() , "werewolf.commands.admin.anonymous_chat.empty"));
            return;
        }


        for (String w : args) {
            sb.append(w).append(" ");
        }
        IModerationManager moderationManager = game.getModerationManager();

        if (args.length > 2 && args[0].charAt(0) == '?') {

            if (moderationManager.getHosts().contains(player.getUniqueId())) {
                UUID uuid = this.players.get(args[1]);
                Player player1 = Bukkit.getPlayer(uuid);
                if (player1 != null) {
                    String response = sb.substring(args[0].length() + args[1].length() + 2);
                    player1.sendMessage(game.translate("werewolf.commands.message.received",
                            Formatter.player(player.getName()),
                            Formatter.format("&message&",response)));
                    player.sendMessage(game.translate("werewolf.commands.message.send",
                            Formatter.player(game.translate("werewolf.commands.admin.anonymous_chat.anonyme")),
                            Formatter.format("&message&",response)));
                    return;
                }
            }
        }


        int i = 0;

        for (UUID uuid : moderationManager.getModerators()) {
            Player player1 = Bukkit.getPlayer(uuid);

            if (player1 != null) {
                TextComponent anonymeMessage = new TextComponent(game.translate("werewolf.commands.admin.anonymous_chat.send",
                        Formatter.player(player.getName()),
                        Formatter.format("&message&",sb.toString())));
                anonymeMessage.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.format("/tell %s", player.getName())));
                player1.spigot().sendMessage(anonymeMessage);
                i++;
            }
        }

        if (i == 0) {
            for (UUID uuid : moderationManager.getHosts()) {
                Player player1 = Bukkit.getPlayer(uuid);

                if (player1 != null) {
                    TextComponent anonymeMessage =
                            new TextComponent(game.translate(
                                    "werewolf.commands.admin.anonymous_chat.send",
                                    Formatter.player(game.translate("werewolf.commands.admin.anonymous_chat.anonyme")),
                                    Formatter.format("&message&",sb.toString())));
                    anonymeMessage.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                            String.format("/ww %s ? %s %s",
                                    game.translate("werewolf.commands.admin.anonymous_chat.command"),
                                    hash(cesar, player), "Message")));
                    player1.spigot().sendMessage(anonymeMessage);
                    i++;
                }
            }
        }

        if (i == 0) {
            player.sendMessage(game.translate(Prefix.RED.getKey() , "werewolf.commands.admin.anonymous_chat.failure"));
        } else
            player.sendMessage(game.translate("werewolf.commands.message.send",
                    Formatter.player(game.translate("werewolf.commands.admin.moderator.name")),
                    Formatter.format("&message&",sb.toString())));


    }

    private String hash(int n, Player player) {

        char[] charSIn = player.getName().toLowerCase().toCharArray();
        char[] charSOut = new char[charSIn.length];
        int pos1, pos2;
        for (int i = 0; i < charSIn.length; i++) {
            pos1 = posChar(charSIn[i], alphabet1);
            pos2 = newPos(pos1, n);
            if (pos2 == -1) charSOut[i] = ' '; // si -1, c'est que ce n'est pas une lettre, on met un espace à la place
            else charSOut[i] = alphabet1[pos2];
        }

        String output = new String(charSOut);
        players.put(output, player.getUniqueId());
        return output; // on fait un string avec le tableau de char

    }

    private int posChar(char c, char[] tab) {
        for (int i = 0; i < tab.length; i++) {
            if (tab[i] == c) {
                return i;
            }
        }
        return -1;
    }


    private int newPos(int pos, int n) {
        int pos2 = pos;
        if (pos <= -1) {
            pos2 = -1;
        } else {
            int i = 0;
            while (i < Math.abs(n)) {
                if (n < 0) {
                    if (pos2 - 1 == -1) pos2 = 25;
                    else pos2--;
                } else {
                    if (pos2 + 1 >= 25) pos2 = 0;
                    else pos2++;
                }
                i++;
            }
        }
        return pos2;
    }
}
