package fr.ph1lou.werewolfplugin.commands.roles.villager.info.librarian;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.librarian.LibrarianRequestEvent;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.ILimitedUse;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@RoleCommand(key = "werewolf.role.librarian.command",
        roleKeys = RoleBase.LIBRARIAN,
        argNumbers = 1)
public class CommandLibrarian implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        String playername = player.getName();
        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) return;

        IRole librarian = playerWW.getRole();

        if (args[0].equalsIgnoreCase(playername)) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.not_yourself");
            return;
        }

        Player selectionPlayer = Bukkit.getPlayer(args[0]);

        if (selectionPlayer == null) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.offline_player");
            return;
        }

        UUID argUUID = selectionPlayer.getUniqueId();
        IPlayerWW playerWW1 = game.getPlayerWW(argUUID).orElse(null);

        if (playerWW1 == null ||
                !playerWW1.isState(StatePlayer.ALIVE)) {

            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.player_not_found");
            return;
        }

        if (((IAffectedPlayers) librarian).getAffectedPlayers().contains(playerWW1)) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.role.librarian.waiting");
            return;
        }


        if (((ILimitedUse) librarian).getUse() >= 3) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.power");
            return;
        }

        ((ILimitedUse) librarian).setUse(((ILimitedUse) librarian).getUse() + 1);
        LibrarianRequestEvent librarianRequestEvent = new LibrarianRequestEvent(playerWW, playerWW1);
        Bukkit.getPluginManager().callEvent(librarianRequestEvent);

        if (librarianRequestEvent.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.cancel");
            return;
        }

        ((IAffectedPlayers) librarian).addAffectedPlayer(playerWW1);

        TextComponent contributionMessage = new TextComponent(game.translate(
                Prefix.YELLOW , "werewolf.role.librarian.message"));
        contributionMessage
                .setClickEvent(new ClickEvent(
                        ClickEvent.Action.SUGGEST_COMMAND,
                        String.format(
                                "/ww %s",
                                game.translate("werewolf.role.librarian.request_command"))));
        selectionPlayer.spigot().sendMessage(contributionMessage);

        playerWW.sendMessageWithKey(
                Prefix.YELLOW , "werewolf.role.librarian.perform",
                Formatter.player(selectionPlayer.getName()));
    }
}
