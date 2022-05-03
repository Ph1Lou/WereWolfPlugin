package fr.ph1lou.werewolfplugin.commands.roles.villager.interpreter;

import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.enums.Prefix;
import fr.ph1lou.werewolfapi.events.roles.interpreter.InterpreterEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfplugin.roles.villagers.Interpreter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandInterpreter implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) return;

        Interpreter interpreter = (Interpreter) playerWW.getRole();

        if(!interpreter.isRoleValid(args[0])){
            return;
        }

        interpreter.setPower(false);

        InterpreterEvent interpreterEvent = new InterpreterEvent(playerWW, args[0]);
        Bukkit.getPluginManager().callEvent(interpreterEvent);

        if (interpreterEvent.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.cancel");
            return;
        }

        interpreter.activateRole(args[0]);
    }
}
