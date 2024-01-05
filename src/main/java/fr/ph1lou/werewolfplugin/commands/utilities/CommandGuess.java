package fr.ph1lou.werewolfplugin.commands.utilities;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.ICommandRole;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.role.interfaces.IGuesser;
import fr.ph1lou.werewolfplugin.guis.GuessInventory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@RoleCommand(key = "werewolf.commands.player.guess.command",
        roleKeys = { RoleBase.MASTERMIND,
                RoleBase.SILENCER_WEREWOLF
        },
        argNumbers = 1)
public class CommandGuess implements ICommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, String[] args) {

        Player playerArg = Bukkit.getPlayer(args[0]);

        if (playerArg == null) {
            playerWW.sendMessageWithKey("werewolf.check.offline_player");
            return;
        }

        UUID argUUID = playerArg.getUniqueId();
        IPlayerWW targetWW = game.getPlayerWW(argUUID).orElse(null);

        if (targetWW == null || targetWW.isState(StatePlayer.DEATH)) {
            playerWW.sendMessageWithKey("werewolf.check.player_not_found");
            return;
        }

        if (!(playerWW.getRole() instanceof IGuesser)) {
            playerWW.sendMessageWithKey("werewolf.check.permission_denied");
            return;
        }

        IGuesser role = (IGuesser) playerWW.getRole();

        if (!role.canGuess(targetWW)) return;

        Set<Category> categories = role.getAvailableCategories();
        if (categories.isEmpty()) {
            categories = new HashSet<>();
            categories.add(Category.VILLAGER);
            categories.add(Category.NEUTRAL);
            categories.add(Category.WEREWOLF);
        }

        Player player = Bukkit.getPlayer(playerWW.getUUID());

        if(player != null){
            GuessInventory.getInventory(targetWW, categories).open(player);
        }
    }
}
