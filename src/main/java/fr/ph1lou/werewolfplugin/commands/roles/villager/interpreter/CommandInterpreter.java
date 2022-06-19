package fr.ph1lou.werewolfplugin.commands.roles.villager.interpreter;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.ICommandRole;
import fr.ph1lou.werewolfapi.events.roles.interpreter.InterpreterEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfplugin.roles.villagers.Interpreter;
import org.bukkit.Bukkit;

@RoleCommand(key = "werewolf.roles.interpreter.command",
        roleKeys = RoleBase.INTERPRETER,
        requiredPower = true,
        autoCompletion = false,
        argNumbers = 1)
public class CommandInterpreter implements ICommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, String[] args) {

        Interpreter interpreter = (Interpreter) playerWW.getRole();

        if(!interpreter.isRoleValid(args[0])){
            return;
        }

        interpreter.setPower(false);

        InterpreterEvent interpreterEvent = new InterpreterEvent(playerWW, args[0]);
        Bukkit.getPluginManager().callEvent(interpreterEvent);

        if (interpreterEvent.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.cancel");
            return;
        }

        interpreter.activateRole(args[0]);
    }
}
