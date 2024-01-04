package fr.ph1lou.werewolfplugin.commands.admin.ingame;

import fr.ph1lou.werewolfapi.annotations.AdminCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.events.UpdateStuffEvent;
import fr.ph1lou.werewolfapi.game.IStuffManager;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

@AdminCommand(key = "werewolf.commands.admin.loot_death.command", descriptionKey = "",
        moderatorAccess = true,
        autoCompletion = false,
        argNumbers = 0)
public class CommandLootDeath implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        IStuffManager stuffManager = game.getStuffs();
        UUID uuid = player.getUniqueId();

        stuffManager.clearDeathLoot();

        Arrays.stream(player.getInventory().getContents())
                .filter(Objects::nonNull)
                .forEach(stuffManager::addDeathLoot);

        player.sendMessage(game.translate(Prefix.GREEN, "werewolf.commands.admin.loot_death.perform"));
        if(game.isState(StateGame.LOBBY)){
            player.setGameMode(GameMode.ADVENTURE);
        }
        else{
            player.setGameMode(GameMode.SURVIVAL);
        }

        if (!stuffManager.isInTempStuff(uuid)) {
            return;
        }
        ItemStack[] items = stuffManager.recoverTempStuff(uuid);
        for (int i = 0; i < 40; i++) {
            player.getInventory().setItem(i, items[i]);
        }

        Bukkit.getPluginManager().callEvent(new UpdateStuffEvent());
    }
}
