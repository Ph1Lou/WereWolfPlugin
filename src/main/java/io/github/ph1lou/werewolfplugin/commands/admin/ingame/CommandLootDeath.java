package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.enumlg.StateLG;
import io.github.ph1lou.werewolfapi.events.UpdateStuffEvent;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
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

        GameManager game = main.getCurrentGame();

        if (!(sender instanceof Player)) {
            sender.sendMessage(game.translate("werewolf.check.console"));
            return;
        }


        if (!sender.hasPermission("a.lootDeath.use") && !game.getModerationManager().getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(game.translate("werewolf.check.permission_denied"));
            return;
        }

        if (!game.isState(StateLG.LOBBY)) {
            sender.sendMessage(game.translate("werewolf.check.already_begin"));
            return;
        }
        game.getStuffs().clearDeathLoot();
        for (ItemStack i : ((Player) sender).getInventory().getContents()) {
            game.getStuffs().addDeathLoot(i);
        }
        sender.sendMessage(game.translate("werewolf.commands.admin.loot_death.perform"));
        ((Player) sender).getInventory().clear();
        ((Player) sender).setGameMode(GameMode.ADVENTURE);

        Bukkit.getPluginManager().callEvent(new UpdateStuffEvent());
    }
}
