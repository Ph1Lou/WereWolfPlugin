package fr.ph1lou.werewolfplugin.commands.roles.villager.info.librarian;

import fr.ph1lou.werewolfapi.annotations.Params;
import fr.ph1lou.werewolfapi.annotations.PlayerArg;
import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.librarian.LibrarianRequestEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.ILimitedUse;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.versions.VersionUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;

@RoleCommand(key = "werewolf.roles.librarian.command",
        roleKeys = RoleBase.LIBRARIAN,
        argNumbers = 1,
        requiredLimitedUse = 3,
        args = @Params(players = @PlayerArg(index = 0, states = { StatePlayer.ALIVE }, excludeSelf = true,
                rejectIfAlreadyAffected = true,
                alreadyAffectedMessageKey = "werewolf.roles.librarian.waiting")))
public class CommandLibrarian implements IParameterizedCommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {

        IRole librarian = playerWW.getRole();

        IPlayerWW playerWW1 = ctx.player(0);


        ((ILimitedUse) librarian).setUse(((ILimitedUse) librarian).getUse() + 1);
        LibrarianRequestEvent librarianRequestEvent = new LibrarianRequestEvent(playerWW, playerWW1);
        Bukkit.getPluginManager().callEvent(librarianRequestEvent);

        if (librarianRequestEvent.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }

        ((IAffectedPlayers) librarian).addAffectedPlayer(playerWW1);

        TextComponent contributionMessage = VersionUtils.getVersionUtils().createClickableText(game.translate(
                Prefix.YELLOW, "werewolf.roles.librarian.message"),
                String.format(
                        "/ww %s",
                        game.translate("werewolf.roles.librarian.request_command")),
                ClickEvent.Action.SUGGEST_COMMAND
                );

        playerWW1.sendMessage(contributionMessage);

        playerWW.sendMessageWithKey(
                Prefix.YELLOW, "werewolf.roles.librarian.perform",
                Formatter.player(playerWW1.getName()));
    }
}
