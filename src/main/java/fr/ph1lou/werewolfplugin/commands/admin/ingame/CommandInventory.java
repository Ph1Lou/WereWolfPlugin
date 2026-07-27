package fr.ph1lou.werewolfplugin.commands.admin.ingame;

import fr.ph1lou.werewolfapi.annotations.AdminCommand;
import fr.ph1lou.werewolfapi.annotations.Params;
import fr.ph1lou.werewolfapi.annotations.PlayerBukkitArg;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommand;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

@AdminCommand(key = "werewolf.commands.admin.inventory.command",
        descriptionKey = "werewolf.commands.admin.inventory.description",
        moderatorAccess = true,
        argNumbers = 1,
        args = @Params(bukkitPlayers = @PlayerBukkitArg(index = 0)))
public class CommandInventory implements IParameterizedCommand {

    @Override
    public void execute(WereWolfAPI game, Player player, CommandContext ctx) {

        Player pInv = ctx.bukkitPlayer(0);

        Inventory inv = Bukkit.createInventory(null, 45, ctx.args()[0]);

        for (int i = 0; i < 40; i++) {
            inv.setItem(i, pInv.getInventory().getItem(i));
        }

        player.openInventory(inv);
    }
}
