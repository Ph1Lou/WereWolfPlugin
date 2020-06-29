package io.github.ph1lou.werewolfplugin.commands.utilities;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class CommandEnchantment implements Commands {


    private final Main main;

    public CommandEnchantment(Main main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {


        GameManager game = main.getCurrentGame();

        sender.sendMessage(Arrays.asList(game.translate("werewolf.menu.enchantments.knock_back_disable"),game.translate("werewolf.menu.enchantments.knock_back_invisible"),game.translate("werewolf.menu.enchantments.knock_back_enable")).get(game.getConfig().getLimitKnockBack()));
        sender.sendMessage(Arrays.asList(game.translate("werewolf.menu.enchantments.punch_disable"),game.translate("werewolf.menu.enchantments.punch_cupid"),game.translate("werewolf.menu.enchantments.punch_enable")).get(game.getConfig().getLimitPunch()));
        sender.sendMessage(game.translate("werewolf.menu.enchantments.iron_protection", game.getConfig().getLimitProtectionIron()));
        sender.sendMessage(game.translate("werewolf.menu.enchantments.diamond_protection", game.getConfig().getLimitProtectionDiamond()));
        sender.sendMessage(game.translate("werewolf.menu.enchantments.power", game.getConfig().getLimitPowerBow()));
        sender.sendMessage(game.translate("werewolf.menu.enchantments.sharpness_iron", game.getConfig().getLimitSharpnessIron()));
        sender.sendMessage(game.translate("werewolf.menu.enchantments.sharpness_diamond", game.getConfig().getLimitSharpnessDiamond()));

    }
}
