package fr.ph1lou.werewolfplugin.commands.utilities;

import fr.ph1lou.werewolfapi.annotations.PlayerCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import org.bukkit.entity.Player;

@PlayerCommand(key = "werewolf.commands.player.enchantments.command",
        descriptionKey = "werewolf.commands.player.enchantments.description",
        argNumbers = 0)
public class CommandEnchantments implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        player.sendMessage(
                game.translate(
                        Prefix.BLUE, "werewolf.commands.player.enchantments.title"
                )
        );

        player.sendMessage(
                game.translate(
                        "werewolf.commands.player.enchantments.iron_diamond_protection",
                        Formatter.number(game.getConfig().getLimitProtectionIron()),
                        Formatter.format("&number2&",
                                game.getConfig().getLimitProtectionDiamond())));

        player.sendMessage(
                game.translate("werewolf.commands.player.enchantments.iron_diamond_sharpness",
                        Formatter.number(game.getConfig().getLimitSharpnessIron()),
                        Formatter.format("&number2&",
                                game.getConfig().getLimitSharpnessDiamond())));

        player.sendMessage(
                game.translate(
                        "werewolf.commands.player.enchantments.punch_power",
                        Formatter.number(game.getConfig().getLimitPowerBow()),
                        Formatter.format("&number2&",
                                game.getConfig().getLimitPunch())));

        player.sendMessage(
                game.translate(
                        "werewolf.commands.player.enchantments.knock_back_depth_rider",
                        Formatter.number(game.getConfig().getLimitKnockBack()),
                        Formatter.format("&number2&", game.getConfig().getLimitDepthStrider())));

        if (game.getConfig().isKnockBackForInvisibleRoleOnly()) {
            player.sendMessage(game.translate("werewolf.commands.player.enchantments.knock_back_invisible"));
        }

        player.sendMessage(game.translate("werewolf.commands.player.enchantments.note"));

    }
}
