package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.ICommands;
import io.github.ph1lou.werewolfplugin.guis.Config;
import org.bukkit.entity.Player;

public class CommandConfig implements ICommands {

    @Override
    public void execute(Player player, String[] args) {
        Config.INVENTORY.open(player);
    }
}
