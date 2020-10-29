package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.enumlg.StateLG;
import io.github.ph1lou.werewolfapi.events.UpdateStuffEvent;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandLootDeath implements Commands {


    private final Main main;

    public CommandLootDeath(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        GameManager game = main.getCurrentGame();

        if (!game.isState(StateLG.LOBBY)) {
            player.sendMessage(game.translate("werewolf.check.already_begin"));
            return;
        }
        game.getStuffs().clearDeathLoot();
        for (ItemStack i : player.getInventory().getContents()) {
            game.getStuffs().addDeathLoot(i);
        }
        player.sendMessage(game.translate("werewolf.commands.admin.loot_death.perform"));
        player.getInventory().clear();
        player.setGameMode(GameMode.ADVENTURE);

        Bukkit.getPluginManager().callEvent(new UpdateStuffEvent());
    }
}
