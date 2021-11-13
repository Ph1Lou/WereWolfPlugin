package io.github.ph1lou.werewolfplugin.commands.utilities;

import io.github.ph1lou.werewolfapi.Formatter;
import io.github.ph1lou.werewolfapi.ICommand;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class CommandEnchantment implements ICommand {


    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        player.sendMessage(
                game.translate(
                        "werewolf.menu.enchantments.title"
                )
        );

        player.sendMessage(
                game.translate(
                        "werewolf.menu.enchantments.iron_protection",
                        Formatter.format("&number&",game.getConfig().getLimitProtectionIron())));

        player.sendMessage(
                game.translate(
                        "werewolf.menu.enchantments.diamond_protection",
                        Formatter.format("&number&",game.getConfig().getLimitProtectionDiamond())));

        player.sendMessage(
                game.translate("werewolf.menu.enchantments.sharpness_iron",
                        Formatter.format("&number&",game.getConfig().getLimitSharpnessIron())));

        player.sendMessage(
                game.translate("werewolf.menu.enchantments.sharpness_diamond",
                        Formatter.format("&number&",game.getConfig().getLimitSharpnessDiamond())));


        player.sendMessage(
                game.translate(
                        "werewolf.menu.enchantments.power",
                        Formatter.format("&number&",game.getConfig().getLimitPowerBow())));

        player.sendMessage(
                game.translate(
                        "werewolf.menu.enchantments.punch",
                        Formatter.format("&number&",game.getConfig().getLimitPunch())));

        player.sendMessage(
                game.translate(
                        "werewolf.menu.enchantments.knock_back",
                        Formatter.format("&number&",game.getConfig().getLimitKnockBack())));

        player.sendMessage(
                game.translate("werewolf.menu.enchantments.depth_rider",
                        Formatter.format("&number&",game.getConfig().getLimitDepthStrider())));

        player.sendMessage(
                Arrays.asList(
                        game.translate(
                                "werewolf.menu.enchantments.knock_back_invisible"),
                        "")
                        .get(game.getConfig().getKnockBackMode()));


    }
}
