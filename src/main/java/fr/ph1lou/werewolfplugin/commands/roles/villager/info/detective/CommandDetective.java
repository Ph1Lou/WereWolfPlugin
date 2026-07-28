package fr.ph1lou.werewolfplugin.commands.roles.villager.info.detective;

import com.google.common.collect.Sets;
import fr.ph1lou.werewolfapi.annotations.Params;
import fr.ph1lou.werewolfapi.annotations.PlayerArg;
import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.detective.InvestigateEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfplugin.roles.villagers.Detective;
import org.bukkit.Bukkit;

@RoleCommand(key = "werewolf.roles.detective.command",
        roleKeys = RoleBase.DETECTIVE,
        requiredPower = true,
        argNumbers = 2,
        args = @Params(players = {
                @PlayerArg(index = 0, states = { StatePlayer.ALIVE, StatePlayer.JUDGEMENT }, excludeSelf = true,
                        rejectIfAlreadyAffected = true,
                        alreadyAffectedMessageKey = "werewolf.roles.detective.already_inspect"),
                @PlayerArg(index = 1, states = { StatePlayer.ALIVE, StatePlayer.JUDGEMENT }, excludeSelf = true,
                        rejectIfAlreadyAffected = true,
                        alreadyAffectedMessageKey = "werewolf.roles.detective.already_inspect")
        }))
public class CommandDetective implements IParameterizedCommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {

        IRole detective = playerWW.getRole();

        IPlayerWW playerWW1 = ctx.player(0);
        IPlayerWW playerWW2 = ctx.player(1);

        if (playerWW1.getUUID().equals(playerWW2.getUUID())) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.two_distinct_player");
            return;
        }

        if (!detective.getPlayersMet().contains(playerWW1) || !detective.getPlayersMet().contains(playerWW2)) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.detective.not_met");
            return;
        }

        String isLG1 = playerWW1.getRole().getDisplayCamp();
        String isLG2 = playerWW2.getRole().getDisplayCamp();

        ((IPower) detective).setPower(false);

        InvestigateEvent event = new InvestigateEvent(playerWW, Sets.newHashSet(playerWW1, playerWW2), isLG1.equals(isLG2));
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }

        ((IAffectedPlayers) detective).addAffectedPlayer(playerWW1);
        ((IAffectedPlayers) detective).addAffectedPlayer(playerWW2);

        Detective detectiveRole = (Detective) detective;
        String code = detectiveRole.getCode(event.isSameCamp());

        playerWW.sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.detective.code",
                Formatter.format("&code&", game.translate("werewolf.roles.detective.color_" + code)));

        detectiveRole.onInvestigated();
        if (detectiveRole.canGuess()) {
            playerWW.sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.detective.guess_available",
                    Formatter.format("&command&", game.translate("werewolf.roles.detective.command_guess")));
        }
    }
}
