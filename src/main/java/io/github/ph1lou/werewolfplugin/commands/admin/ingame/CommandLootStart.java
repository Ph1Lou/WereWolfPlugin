package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.enumlg.StateLG;
import io.github.ph1lou.werewolfapi.events.UpdateStuffEvent;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

public class CommandLootStart implements Commands {


    private final Main main;

    public CommandLootStart(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        GameManager game = main.getCurrentGame();

        if (!game.isState(StateLG.LOBBY)) {
            player.sendMessage(game.translate("werewolf.check.already_begin"));
            return;
        }

        PlayerInventory inventory = player.getInventory();

        game.getStuffs().clearStartLoot();

        for (int j = 0; j < 40; j++) {
            game.getStuffs().getStartLoot().setItem(j, inventory.getItem(j));
            inventory.setItem(j, null);
        }

        player.sendMessage(game.translate("werewolf.commands.admin.stuff_start.perform"));
        player.setGameMode(GameMode.ADVENTURE);

        Bukkit.getPluginManager().callEvent(new UpdateStuffEvent());
    }
}
