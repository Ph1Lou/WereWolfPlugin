package fr.ph1lou.werewolfplugin.commands.roles.villager.hunter;

import fr.ph1lou.werewolfapi.annotations.Params;
import fr.ph1lou.werewolfapi.annotations.PlayerArg;
import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommandRole;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.hunter.HunterShotEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.AuraModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import org.bukkit.Bukkit;

@RoleCommand(key = "werewolf.roles.hunter.command",
        roleKeys = RoleBase.HUNTER,
        requiredPower = true,
        statesPlayer = StatePlayer.DEATH,
        argNumbers = 1,
        args = @Params(players = @PlayerArg(index = 0, states = { StatePlayer.ALIVE })))
public class CommandHunter implements IParameterizedCommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {

        IPlayerWW targetWW = ctx.player(0);

        if (playerWW.getLastKiller().isPresent() && playerWW.getLastKiller().get().equals(targetWW)) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.hunter.invalid_target");
            return;
        }
        ((IPower) playerWW.getRole()).setPower(false);
        HunterShotEvent event = new HunterShotEvent(playerWW, targetWW);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) return;

        playerWW.getRole().addAuraModifier(new AuraModifier(playerWW.getRole().getKey(), Aura.DARK, 1, false));

        event.getTargetWW().removePlayerHealth(10);
        Bukkit.broadcastMessage(game.translate(Prefix.RED, "werewolf.roles.hunter.success", Formatter.format("&target&",
                event.getTargetWW().getName())));
    }
}
