package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.ICommands;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.roles.librarian.LibrarianRequestEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.ILimitedUse;
import io.github.ph1lou.werewolfapi.rolesattributs.IRole;
import io.github.ph1lou.werewolfplugin.Main;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandLibrarian implements ICommands {


    private final Main main;

    public CommandLibrarian(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();
        String playername = player.getName();
        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid);

        if (playerWW == null) return;

        IRole librarian = playerWW.getRole();

        if (args[0].equalsIgnoreCase(playername)) {
            playerWW.sendMessageWithKey("werewolf.check.not_yourself");
            return;
        }

        Player selectionPlayer = Bukkit.getPlayer(args[0]);

        if (selectionPlayer == null) {
            playerWW.sendMessageWithKey("werewolf.check.offline_player");
            return;
        }

        UUID argUUID = selectionPlayer.getUniqueId();
        IPlayerWW playerWW1 = game.getPlayerWW(argUUID);

        if (playerWW1 == null ||
                !playerWW1.isState(StatePlayer.ALIVE)) {

            playerWW.sendMessageWithKey("werewolf.check.player_not_found");
            return;
        }

        if (((IAffectedPlayers) librarian).getAffectedPlayers().contains(playerWW1)) {
            playerWW.sendMessageWithKey("werewolf.role.librarian.waiting");
            return;
        }


        if (((ILimitedUse) librarian).getUse() >= 3) {
            playerWW.sendMessageWithKey("werewolf.check.power");
            return;
        }

        ((ILimitedUse) librarian).setUse(((ILimitedUse) librarian).getUse() + 1);
        LibrarianRequestEvent librarianRequestEvent = new LibrarianRequestEvent(playerWW, playerWW1);
        Bukkit.getPluginManager().callEvent(librarianRequestEvent);

        if (librarianRequestEvent.isCancelled()) {
            playerWW.sendMessageWithKey("werewolf.check.cancel");
            return;
        }

        ((IAffectedPlayers) librarian).addAffectedPlayer(playerWW1);

        TextComponent contributionMessage = new TextComponent(game.translate(
                "werewolf.role.librarian.message"));
        contributionMessage
                .setClickEvent(new ClickEvent(
                        ClickEvent.Action.SUGGEST_COMMAND,
                        String.format(
                                "/ww %s",
                                game.translate("werewolf.role.librarian.request_command"))));
        selectionPlayer.spigot().sendMessage(contributionMessage);

        playerWW.sendMessageWithKey(
                "werewolf.role.librarian.perform",
                selectionPlayer.getName());
    }
}
