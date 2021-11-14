package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.ICommand;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Prefix;
import io.github.ph1lou.werewolfapi.enums.Sound;
import io.github.ph1lou.werewolfapi.versions.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandFinalHeal implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        Bukkit.getOnlinePlayers().forEach(player1 -> {
            player1.setHealth(VersionUtils.getVersionUtils().getPlayerMaxHealth(player1));
            Sound.NOTE_STICKS.play(player1);
            player1.sendMessage(game.translate(Prefix.ORANGE.getKey() , "werewolf.commands.admin.final_heal.send"));
        });
    }
}
