package io.github.ph1lou.werewolfplugin.commands.utilities;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.enumlg.StateLG;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import io.github.ph1lou.werewolfplugin.game.ModerationManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommandAnonymeChat implements Commands {


    private final Main main;
    private final int cesar = (int) (Math.random() * 26);
    private final Map<String, UUID> players = new HashMap<>();
    private final char[] alphabet1 = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '_'};

    public CommandAnonymeChat(Main main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        GameManager game = main.getCurrentGame();
        if (!(sender instanceof Player)) {
            sender.sendMessage(game.translate("werewolf.check.console"));
            return;
        }

        Player player1 = (Player) sender;

        if (game.isState(StateLG.LOBBY)) {
            player1.sendMessage(game.translate("werewolf.check.game_not_in_progress"));
            return;
        }

        StringBuilder sb = new StringBuilder();

        for (String w : args) {
            sb.append(w).append(" ");
        }
        ModerationManager moderationManager = game.getModerationManager();

        if (args.length > 2 && args[0].charAt(0) == '?') {

            if (moderationManager.getHosts().contains(player1.getUniqueId())) {
                UUID uuid = this.players.get(args[1]);
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    String response = sb.substring(args[0].length() + args[1].length() + 2);
                    player.sendMessage(game.translate("werewolf.commands.message.received", player1.getName(), response));
                    player1.sendMessage(game.translate("werewolf.commands.message.send", game.translate("werewolf.commands.admin.anonymous_chat.anonyme"), response));
                    return;
                }
            }
        }


        int i = 0;

        for (UUID uuid : moderationManager.getModerators()) {
            Player player = Bukkit.getPlayer(uuid);

            if (player != null) {
                TextComponent anonymeMessage = new TextComponent(game.translate("werewolf.commands.admin.anonymous_chat.send", player1, sb.toString()));
                anonymeMessage.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell " + sender.getName()));
                player.spigot().sendMessage(anonymeMessage);
                i++;
            }
        }

        if (i == 0) {
            for (UUID uuid : moderationManager.getHosts()) {
                Player player = Bukkit.getPlayer(uuid);

                if (player != null) {
                    TextComponent anonymeMessage = new TextComponent(game.translate("werewolf.commands.admin.anonymous_chat.send", game.translate("werewolf.commands.admin.anonymous_chat.anonyme"), sb.toString()));
                    anonymeMessage.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ww ? ? " + hash(cesar, player1) + " Message"));
                    player.spigot().sendMessage(anonymeMessage);
                    i++;
                }
            }
        }

        if (i == 0) {
            player1.sendMessage(game.translate("werewolf.commands.admin.anonymous_chat.failure"));
        } else
            player1.sendMessage(game.translate("werewolf.commands.message.send", game.translate("werewolf.commands.admin.moderator.name"), sb.toString()));


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

    // Donne la nouvelle position dans l'alphabet en fonction de n
    private int newPos(int pos, int n) {
        int pos2 = pos;
        if (pos <= -1) { // -1 signifie que le caractere n'a pas été trouvé dans l'alphabet (caractere spécial, chiffre, espace, etc.)
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
