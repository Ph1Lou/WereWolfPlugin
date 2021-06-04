package io.github.ph1lou.werewolfplugin.commands.utilities;

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
