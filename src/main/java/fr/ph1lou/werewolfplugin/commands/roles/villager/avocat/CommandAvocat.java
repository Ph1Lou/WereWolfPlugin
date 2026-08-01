package fr.ph1lou.werewolfplugin.commands.roles.villager.avocat;

import fr.ph1lou.werewolfapi.annotations.Params;
import fr.ph1lou.werewolfapi.annotations.PlayerArg;
import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.avocat.AvocatDefendEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfplugin.roles.villagers.Avocat;
import org.bukkit.Bukkit;

@RoleCommand(key = "werewolf.roles.avocat.command",
        roleKeys = RoleBase.AVOCAT,
        argNumbers = 1,
        args = @Params(players = @PlayerArg(index = 0, states = { StatePlayer.ALIVE, StatePlayer.JUDGEMENT })))
public class CommandAvocat implements IParameterizedCommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {

        Avocat avocat = (Avocat) playerWW.getRole();
        IPlayerWW target = ctx.player(0);

        if (!avocat.canDefend()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.avocat.cooldown",
                    Formatter.number(avocat.getRemainingCooldown() / 60 + 1));
            return;
        }

        if (avocat.isLastDefended(target)) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.avocat.same_target");
            return;
        }

        AvocatDefendEvent event = new AvocatDefendEvent(playerWW, target);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }

        avocat.defend(target);

        playerWW.sendMessageWithKey(Prefix.GREEN, "werewolf.roles.avocat.perform",
                Formatter.player(target.getName()),
                Formatter.number(game.getConfig().getValue(IntValueBase.AVOCAT_DEFENSE_DURATION)));
    }
}
