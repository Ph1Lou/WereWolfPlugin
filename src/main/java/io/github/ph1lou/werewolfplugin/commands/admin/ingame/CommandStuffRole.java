package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.events.UpdateStuffEvent;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandStuffRole implements Commands {


    private final Main main;

    public CommandStuffRole(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        GameManager game = main.getCurrentGame();

        if (!game.getStuffs().getStuffRoles().containsKey(args[0])) {
            player.sendMessage(game.translate("werewolf.check.invalid_key"));
            return;
        }

        game.getStuffs().getStuffRoles().get(args[0]).clear();
        for (ItemStack i : player.getInventory().getContents()) {
            game.getStuffs().getStuffRoles().get(args[0]).add(i);
        }
        player.sendMessage(game.translate("werewolf.commands.admin.loot_role.perform"));
        player.getInventory().clear();
        player.setGameMode(GameMode.ADVENTURE);

        Bukkit.getPluginManager().callEvent(new UpdateStuffEvent());
    }
}
