package fr.ph1lou.werewolfplugin.commands.utilities;

import fr.ph1lou.werewolfapi.annotations.PlayerCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.game.IModerationManager;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.versions.VersionUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@PlayerCommand(key = "werewolf.commands.player.anonymous_chat.command",
        descriptionKey = "werewolf.commands.player.anonymous_chat.description",
        statesGame = { StateGame.START, StateGame.GAME },
        statesPlayer = StatePlayer.ALIVE)
public class CommandAnonymeChat implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        StringBuilder sb = new StringBuilder();


        if (args.length == 0) {
            player.sendMessage(game.translate(Prefix.RED, "werewolf.commands.player.anonymous_chat.empty"));
            return;
        }


        for (String w : args) {
            sb.append(w).append(" ");
        }
        IModerationManager moderationManager = game.getModerationManager();

        if (args.length > 2 && args[0].charAt(0) == '?') {

            if (moderationManager.getHosts().contains(player.getUniqueId())) {
                try{
                    UUID uuid = UUID.fromString(args[1]);
                    Player player1 = Bukkit.getPlayer(uuid);
                    if (player1 != null) {
                        String response = sb.substring(args[0].length() + args[1].length() + 2);
                        player1.sendMessage(game.translate("werewolf.commands.player.message.received",
                                Formatter.player(player.getName()),
                                Formatter.format("&message&", response)));
                        player.sendMessage(game.translate("werewolf.commands.player.message.send",
                                Formatter.player(game.translate("werewolf.commands.player.anonymous_chat.anonyme")),
                                Formatter.format("&message&", response)));
                    }
                }
                catch (IllegalArgumentException ignored){
                }

            }
            return;
        }

        int i = 0;

        for (UUID uuid : moderationManager.getModerators()) {
            Player player1 = Bukkit.getPlayer(uuid);

            if (player1 != null) {
                TextComponent anonymizeMessage = VersionUtils.getVersionUtils().createClickableText(game.translate("werewolf.commands.player.anonymous_chat.send",
                        Formatter.player(player.getName()),
                        Formatter.format("&message&", sb.toString())),
                        String.format("/tell %s", player.getName()),
                        ClickEvent.Action.SUGGEST_COMMAND);
                player1.spigot().sendMessage(anonymizeMessage);
                i++;
            }
        }

        if (i == 0) {
            for (UUID uuid : moderationManager.getHosts()) {
                Player player1 = Bukkit.getPlayer(uuid);

                if (player1 != null) {
                    TextComponent anonymeMessage =
                            VersionUtils.getVersionUtils().createClickableText(game.translate(
                                            "werewolf.commands.player.anonymous_chat.send",
                                            Formatter.player(game.translate("werewolf.commands.player.anonymous_chat.anonyme")),
                                            Formatter.format("&message&", sb.toString())),
                                    String.format("/ww %s ? %s ",
                                            game.translate("werewolf.commands.player.anonymous_chat.command"),
                                            player.getUniqueId()),
                                    ClickEvent.Action.SUGGEST_COMMAND
                            );
                    player1.spigot().sendMessage(anonymeMessage);
                    i++;
                }
            }
        }

        if (i == 0) {
            player.sendMessage(game.translate(Prefix.RED, "werewolf.commands.player.anonymous_chat.failure"));
        } else
            player.sendMessage(game.translate("werewolf.commands.player.message.send",
                    Formatter.player(game.translate("werewolf.commands.admin.moderator.name")),
                    Formatter.format("&message&", sb.toString())));


    }
}
