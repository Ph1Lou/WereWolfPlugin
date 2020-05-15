package io.github.ph1lou.pluginlg.commandlg.utilities;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.game.GameManager;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class CommandEnchantment extends Commands {


    public CommandEnchantment(MainLG main) {
        super(main);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {


        GameManager game = main.currentGame;

        sender.sendMessage(Arrays.asList(game.translate("werewolf.menu.enchantments.knock_back_disable"),game.translate("werewolf.menu.enchantments.knock_back_invisible"),game.translate("werewolf.menu.enchantments.knock_back_enable")).get(game.config.getLimitKnockBack()));
        sender.sendMessage(Arrays.asList(game.translate("werewolf.menu.enchantments.punch_disable"),game.translate("werewolf.menu.enchantments.punch_cupid"),game.translate("werewolf.menu.enchantments.punch_enable")).get(game.config.getLimitPunch()));
        sender.sendMessage(game.translate("werewolf.menu.enchantments.iron_protection", game.config.getLimitProtectionIron()));
        sender.sendMessage(game.translate("werewolf.menu.enchantments.diamond_protection", game.config.getLimitProtectionDiamond()));
        sender.sendMessage(game.translate("werewolf.menu.enchantments.power", game.config.getLimitPowerBow()));
        sender.sendMessage(game.translate("werewolf.menu.enchantments.sharpness_iron", game.config.getLimitSharpnessIron()));
        sender.sendMessage(game.translate("werewolf.menu.enchantments.sharpness_diamond", game.config.getLimitSharpnessDiamond()));

    }
}
