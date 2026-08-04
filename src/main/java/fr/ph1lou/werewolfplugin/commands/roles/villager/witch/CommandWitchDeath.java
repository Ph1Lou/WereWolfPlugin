package fr.ph1lou.werewolfplugin.commands.roles.villager.witch;

import fr.ph1lou.werewolfapi.annotations.Params;
import fr.ph1lou.werewolfapi.annotations.PlayerArg;
import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.witch.WitchCurseEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfplugin.roles.villagers.Witch;
import org.bukkit.Bukkit;

import java.util.Optional;

@RoleCommand(key = "werewolf.roles.witch.command_death",
        roleKeys = RoleBase.WITCH,
        requiredPowerKey = Witch.POWER_DEATH,
        argNumbers = 1,
        autoCompletion = false,
        args = @Params(players = @PlayerArg(index = 0, byUuid = true,
                states = {StatePlayer.JUDGEMENT},
                excludeSelf = true,
                stateMessageKey = "werewolf.check.not_in_judgement")))
public class CommandWitchDeath implements IParameterizedCommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {

        Witch witch = (Witch) playerWW.getRole();

        IPlayerWW target = ctx.player(0);

        if (target == null) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.player_not_found");
            return;
        }

        if (!witch.isOfferValid(target)) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.witch.too_late");
            return;
        }

        Optional<IPlayerWW> killerOpt = witch.getKillerOf(target);

        if (!killerOpt.isPresent()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.witch.no_killer",
                    Formatter.player(target.getName()));
            return;
        }

        IPlayerWW killer = killerOpt.get();

        WitchCurseEvent witchCurseEvent = new WitchCurseEvent(playerWW, killer);
        Bukkit.getPluginManager().callEvent(witchCurseEvent);

        if (witchCurseEvent.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }

        witch.usePower(Witch.POWER_DEATH);
        witch.clearOffer(target);
        witch.addCursedKiller(killer);
        playerWW.sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.witch.curse_perform",
                Formatter.player(killer.getName()));
        killer.sendMessageWithKey(Prefix.RED, "werewolf.roles.witch.curse_target_notify");
    }
}
