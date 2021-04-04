package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.ICommands;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Sound;
import io.github.ph1lou.werewolfapi.versions.VersionUtils;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandFinalHeal implements ICommands {


    private final Main main;

    public CommandFinalHeal(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();

        Bukkit.getOnlinePlayers().forEach(player1 -> {
            player1.setHealth(VersionUtils.getVersionUtils().getPlayerMaxHealth(player1));
            Sound.NOTE_STICKS.play(player1);
            player1.sendMessage(game.translate("werewolf.commands.admin.final_heal.send"));
        });
    }
}
