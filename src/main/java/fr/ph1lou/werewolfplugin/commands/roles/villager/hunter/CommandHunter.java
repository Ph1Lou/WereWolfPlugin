package fr.ph1lou.werewolfplugin.commands.roles.villager.hunter;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.hunter.HunterShotEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@RoleCommand(key = "werewolf.role.hunter.command",
        roleKeys = RoleBase.HUNTER,
        requiredPower = true,
        statePlayer = StatePlayer.DEATH,
        argNumbers = 1)
public class CommandHunter implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {
        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) {
            return;
        }

        Player playerArg = Bukkit.getPlayer(args[0]);

        if (playerArg == null) {
            playerWW.sendMessageWithKey("werewolf.check.offline_player");
            return;
        }

        UUID argUUID = playerArg.getUniqueId();
        IPlayerWW targetWW = game.getPlayerWW(argUUID).orElse(null);

        if (targetWW == null || !targetWW.isState(StatePlayer.ALIVE)) {
            playerWW.sendMessageWithKey("werewolf.check.player_not_found");
            return;
        }

        if (playerWW.getLastKiller().isPresent() && playerWW.getLastKiller().get().equals(targetWW)) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.role.hunter.invalid_target");
            return;
        }
        ((IPower) playerWW.getRole()).setPower(false);
        HunterShotEvent event = new HunterShotEvent(targetWW, playerWW);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) return;

        event.getTarget().removePlayerHealth(10);
        Bukkit.broadcastMessage(game.translate(Prefix.RED, "werewolf.role.hunter.success", Formatter.format("&target&", event.getTarget().getName())));
    }
}
