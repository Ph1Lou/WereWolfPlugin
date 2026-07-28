package fr.ph1lou.werewolfplugin.commands.roles.villager.guard;

import fr.ph1lou.werewolfapi.annotations.Params;
import fr.ph1lou.werewolfapi.annotations.PlayerArg;
import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommandRole;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.guard.GuardEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.AuraModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfplugin.roles.villagers.Guard;
import org.bukkit.Bukkit;

@RoleCommand(key = "werewolf.roles.guard.command",
        roleKeys = RoleBase.GUARD,
        requiredPower = true,
        argNumbers = 1,
        args = @Params(players = @PlayerArg(index = 0, states = { StatePlayer.ALIVE }, excludeSelf = true,
                rejectIfAlreadyAffected = true)))
public class CommandGuard implements IParameterizedCommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {

        Guard guard = (Guard) playerWW.getRole();

        IPlayerWW playerWW1 = ctx.player(0);

        if (!guard.isPowerFinal()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.power");
            return;
        }

        guard.setPower(false);

        GuardEvent guardEvent = new GuardEvent(playerWW, playerWW1);

        Bukkit.getPluginManager().callEvent(guardEvent);

        if (guardEvent.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }

        guard.addAffectedPlayer(playerWW1);
        playerWW1.getRole().addAuraModifier(new AuraModifier(playerWW.getRole().getKey(),
                Aura.LIGHT,
                40,
                true));

        playerWW.sendMessageWithKey(Prefix.GREEN, "werewolf.roles.guard.perform",
                Formatter.player(playerWW1.getName()));
        playerWW1.sendMessageWithKey(Prefix.GREEN, "werewolf.roles.guard.protected");

        IPlayerWW protectedPlayer = playerWW1;
        String guardKey = playerWW.getRole().getKey();
        BukkitUtils.scheduleSyncDelayedTask(game, () -> {
            protectedPlayer.getRole().removeAuraModifier(guardKey);
            if (protectedPlayer.equals(guard.getLast())) {
                guard.clearLast();
            }
        }, 10 * 60 * 20L);
    }
}
