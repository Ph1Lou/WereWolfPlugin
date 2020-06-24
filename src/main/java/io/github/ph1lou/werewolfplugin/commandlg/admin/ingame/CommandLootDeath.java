package io.github.ph1lou.werewolfplugin.commandlg.admin.ingame;

import io.github.ph1lou.pluginlgapi.Commands;
import io.github.ph1lou.pluginlgapi.enumlg.StateLG;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandLootDeath implements Commands {


    private final Main main;

    public CommandLootDeath(Main main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        GameManager game = main.currentGame;

        if (!(sender instanceof Player)) {
            sender.sendMessage(game.translate("werewolf.check.console"));
            return;
        }


        if (!sender.hasPermission("a.use") && !sender.hasPermission("a.lootDeath.use") && !game.getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(game.translate("werewolf.check.permission_denied"));
            return;
        }

        if (!game.isState(StateLG.LOBBY)) {
            sender.sendMessage(game.translate("werewolf.check.already_begin"));
            return;
        }
        game.getStuffs().clearDeathLoot();
        for (ItemStack i : ((Player) sender).getInventory().getContents()) {
            if (i != null) {
                game.getStuffs().addDeathLoot(i);
            }
        }
        sender.sendMessage(game.translate("werewolf.commands.admin.loot_death.perform"));
        ((Player) sender).getInventory().clear();
        ((Player) sender).setGameMode(GameMode.ADVENTURE);
    }
}
