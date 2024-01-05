package fr.ph1lou.werewolfplugin.commands.roles.villager.info.oracle;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.ICommandRole;
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
import org.bukkit.entity.Player;

import java.util.UUID;

@RoleCommand(key = "werewolf.roles.oracle.command",
        roleKeys = RoleBase.ORACLE,
        requiredPower = true,
        argNumbers = 1)
public class CommandOracle implements ICommandRole {


    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, String[] args) {

        IRole oracle = playerWW.getRole();

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
