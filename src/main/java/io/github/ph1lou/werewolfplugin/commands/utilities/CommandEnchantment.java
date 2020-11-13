package io.github.ph1lou.werewolfplugin.commands.utilities;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class CommandEnchantment implements Commands {


    private final Main main;

    public CommandEnchantment(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {


        WereWolfAPI game = main.getWereWolfAPI();

        player.sendMessage(
                game.translate(
                        "werewolf.menu.enchantments.title"
                )
        );

        player.sendMessage(
                game.translate(
                        "werewolf.menu.enchantments.iron_protection",
                        game.getConfig().getLimitProtectionIron()));

        player.sendMessage(
                game.translate(
                        "werewolf.menu.enchantments.diamond_protection",
                        game.getConfig().getLimitProtectionDiamond()));

        player.sendMessage(
                game.translate("werewolf.menu.enchantments.sharpness_iron",
                        game.getConfig().getLimitSharpnessIron()));

        player.sendMessage(
                game.translate("werewolf.menu.enchantments.sharpness_diamond",
                        game.getConfig().getLimitSharpnessDiamond()));


        player.sendMessage(
                game.translate(
                        "werewolf.menu.enchantments.power",
                        game.getConfig().getLimitPowerBow()));

        player.sendMessage(
                game.translate(
                        "werewolf.menu.enchantments.punch",
                        game.getConfig().getLimitPunch()));

        player.sendMessage(
                game.translate(
                        "werewolf.menu.enchantments.knock_back",
                        game.getConfig().getLimitKnockBack()));

        player.sendMessage(
                game.translate("werewolf.menu.enchantments.depth_rider",
                        game.getConfig().getLimitDepthStrider()));

        player.sendMessage(
                Arrays.asList(
                        game.translate(
                                "werewolf.menu.enchantments.knock_back_invisible"),
                        "")
                        .get(game.getConfig().getKnockBackMode()));


    }
}
