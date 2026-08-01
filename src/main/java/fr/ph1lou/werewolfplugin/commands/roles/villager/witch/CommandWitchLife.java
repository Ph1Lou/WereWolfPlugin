package fr.ph1lou.werewolfplugin.commands.roles.villager.witch;

import fr.ph1lou.werewolfapi.annotations.Params;
import fr.ph1lou.werewolfapi.annotations.PlayerArg;
import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.witch.WitchResurrectionEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfplugin.roles.villagers.Witch;
import org.bukkit.Bukkit;

@RoleCommand(key = "werewolf.roles.witch.command_life",
        roleKeys = RoleBase.WITCH,
        argNumbers = 1,
        autoCompletion = false,
        args = @Params(players = @PlayerArg(index = 0, byUuid = true,
                states = {StatePlayer.JUDGEMENT},
                stateMessageKey = "werewolf.check.not_in_judgement")))
public class CommandWitchLife implements IParameterizedCommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {

        Witch witch = (Witch) playerWW.getRole();

        if (!witch.hasLifePotion()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.witch.potion_already_used");
            return;
        }

        IPlayerWW target = ctx.player(0);

        if (target == null) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.player_not_found");
            return;
        }

        boolean self = target.equals(playerWW);

        if (self && !game.getConfig().isConfigActive(ConfigBase.WITCH_AUTO_RESURRECTION)) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.not_yourself");
            return;
        }

        if (!self && !witch.isOfferValid(target)) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.witch.too_late");
            return;
        }

        WitchResurrectionEvent witchResurrectionEvent = new WitchResurrectionEvent(playerWW, target);
        Bukkit.getPluginManager().callEvent(witchResurrectionEvent);

        if (witchResurrectionEvent.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }

        witch.setLifePotion(false);
        if (!self) {
            witch.clearOffer(target);
        }
        game.resurrection(target);
        playerWW.sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.witch.resuscitation_perform",
                Formatter.player(target.getName()));
    }
}
