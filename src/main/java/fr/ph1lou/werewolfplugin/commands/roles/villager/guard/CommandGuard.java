package fr.ph1lou.werewolfplugin.commands.roles.villager.guard;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.ICommandRole;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.guard.GuardEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.AuraModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfplugin.roles.villagers.Guard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@RoleCommand(key = "werewolf.roles.guard.command",
        roleKeys = RoleBase.GUARD,
        requiredPower = true,
        argNumbers = 1)
public class CommandGuard implements ICommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, String[] args) {

        Guard guard = (Guard) playerWW.getRole();

        Player playerArg = Bukkit.getPlayer(args[0]);

        if (playerArg == null) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.offline_player");
            return;
        }
        UUID argUUID = playerArg.getUniqueId();
        IPlayerWW playerWW1 = game.getPlayerWW(argUUID).orElse(null);

        if (playerWW1 == null || !playerWW1.isState(StatePlayer.ALIVE)) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.player_not_found");
            return;
        }

        if (((IAffectedPlayers) guard).getAffectedPlayers().contains(playerWW1)) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.already_get_power");
            return;
        }

        if (!guard.isPowerFinal()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.power");
            return;
        }

        ((IPower) guard).setPower(false);

        GuardEvent guardEvent = new GuardEvent(playerWW, playerWW1);

        Bukkit.getPluginManager().callEvent(guardEvent);

        if (guardEvent.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }

        ((IAffectedPlayers) guard).addAffectedPlayer(playerWW1);
        playerWW1.getRole().addAuraModifier(new AuraModifier(playerWW.getRole().getKey(),
                Aura.LIGHT,
                40,
                true));

        playerWW.sendMessageWithKey(Prefix.GREEN, "werewolf.roles.guard.perform",
                Formatter.player(playerArg.getName()));
    }
}
