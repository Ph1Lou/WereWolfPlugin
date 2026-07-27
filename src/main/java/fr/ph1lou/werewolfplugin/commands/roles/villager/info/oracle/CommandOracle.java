package fr.ph1lou.werewolfplugin.commands.roles.villager.info.oracle;

import fr.ph1lou.werewolfapi.annotations.Params;
import fr.ph1lou.werewolfapi.annotations.PlayerArg;
import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommandRole;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.oracle.OracleEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import org.bukkit.Bukkit;

@RoleCommand(key = "werewolf.roles.oracle.command",
        roleKeys = RoleBase.ORACLE,
        requiredPower = true,
        argNumbers = 1,
        args = @Params(players = @PlayerArg(index = 0, states = { StatePlayer.ALIVE })))
public class CommandOracle implements IParameterizedCommandRole {


    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {

        IRole oracle = playerWW.getRole();

        IPlayerWW playerWW1 = ctx.player(0);

        Aura aura = playerWW1.getRole().getAura();

        OracleEvent oracleEvent = new OracleEvent(playerWW, playerWW1, aura);
        ((IPower) oracle).setPower(false);
        Bukkit.getPluginManager().callEvent(oracleEvent);

        if (oracleEvent.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }

        ((IAffectedPlayers) oracle).addAffectedPlayer(playerWW1);

        playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.oracle.message",
                Formatter.player(playerWW1.getName()),
                Formatter.format("&aura&", game.translate(aura.getKey())));
    }
}
