package fr.ph1lou.werewolfplugin.commands.utilities;

import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
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
                        Formatter.number(game.getConfig().getLimitProtectionIron())));

        player.sendMessage(
                game.translate(
                        "werewolf.menu.enchantments.diamond_protection",
                        Formatter.number(game.getConfig().getLimitProtectionDiamond())));

        player.sendMessage(
                game.translate("werewolf.menu.enchantments.sharpness_iron",
                        Formatter.number(game.getConfig().getLimitSharpnessIron())));

        player.sendMessage(
                game.translate("werewolf.menu.enchantments.sharpness_diamond",
                        Formatter.number(game.getConfig().getLimitSharpnessDiamond())));


        player.sendMessage(
                game.translate(
                        "werewolf.menu.enchantments.power",
                        Formatter.number(game.getConfig().getLimitPowerBow())));

        player.sendMessage(
                game.translate(
                        "werewolf.menu.enchantments.punch",
                        Formatter.number(game.getConfig().getLimitPunch())));

        player.sendMessage(
                game.translate(
                        "werewolf.menu.enchantments.knock_back",
                        Formatter.number(game.getConfig().getLimitKnockBack())));

        player.sendMessage(
                game.translate("werewolf.menu.enchantments.depth_rider",
                        Formatter.number(game.getConfig().getLimitDepthStrider())));

        player.sendMessage(
                Arrays.asList(
                        game.translate(
                                "werewolf.menu.enchantments.knock_back_invisible"),
                        "")
                        .get(game.getConfig().getKnockBackMode()));


    }
}
