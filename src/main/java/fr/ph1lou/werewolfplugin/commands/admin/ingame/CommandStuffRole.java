package fr.ph1lou.werewolfplugin.commands.admin.ingame;

import fr.ph1lou.werewolfapi.annotations.AdminCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.events.UpdateStuffEvent;
import fr.ph1lou.werewolfapi.game.IStuffManager;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@AdminCommand(key = "werewolf.commands.admin.loot_role.command", descriptionKey = "",
        statesGame = {StateGame.LOBBY, StateGame.TRANSPORTATION, StateGame.START},
        argNumbers = 1,
        autoCompletion = false,
        moderatorAccess = true)
public class CommandStuffRole implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        IStuffManager stuffManager = game.getStuffs();
        UUID uuid = player.getUniqueId();

        if (!stuffManager.isInTempStuff(uuid)) {
            return;
        }

        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < 36; i++) {
            items.add(player.getInventory().getItem(i));
        }

        stuffManager.setStuffRole(args[0], items.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        player.sendMessage(game.translate(Prefix.GREEN, "werewolf.commands.admin.loot_role.perform",
                Formatter.role(game.translate(args[0]))));


        ItemStack[] oldItems = stuffManager.recoverTempStuff(uuid);
        for (int i = 0; i < 40; i++) {
            player.getInventory().setItem(i, oldItems[i]);
        }

        if(game.isState(StateGame.LOBBY)){
            player.setGameMode(GameMode.ADVENTURE);
        }
        else{
            player.setGameMode(GameMode.SURVIVAL);
        }

        Bukkit.getPluginManager().callEvent(new UpdateStuffEvent());
    }
}
