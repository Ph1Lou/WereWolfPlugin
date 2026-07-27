package fr.ph1lou.werewolfplugin.commands.utilities;

import fr.ph1lou.werewolfapi.annotations.Params;
import fr.ph1lou.werewolfapi.annotations.PlayerArg;
import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommandRole;
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

@RoleCommand(key = "werewolf.commands.player.guess.command",
        roleKeys = {RoleBase.MASTERMIND,
                RoleBase.SILENCER_WEREWOLF
        },
        argNumbers = 1,
        args = @Params(players = @PlayerArg(index = 0, states = {StatePlayer.ALIVE, StatePlayer.JUDGEMENT})))
public class CommandGuess implements IParameterizedCommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {

        IPlayerWW targetWW = ctx.player(0);

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

        if (player != null) {
            GuessInventory.getInventory(targetWW, categories).open(player);
        }
    }
}
