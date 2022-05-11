package fr.ph1lou.werewolfplugin.commands.roles.neutral.necromancer;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import org.bukkit.entity.Player;

@RoleCommand(key = "werewolf.role.necromancer.command",
        roleKeys = RoleBase.NECROMANCER,
        requiredPower = true,
        argNumbers = 0)
public class CommandNecromancer implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {
        IPlayerWW playerWW = game.getPlayerWW(player.getUniqueId()).orElse(null);

        if(playerWW == null){
            return;
        }

        playerWW.sendMessageWithKey(Prefix.YELLOW,"werewolf.role.necromancer.use");

        ((IPower)playerWW.getRole()).setPower(false);
    }
}
