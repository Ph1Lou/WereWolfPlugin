package fr.ph1lou.werewolfplugin.commands.roles.villager.bohemian;

import fr.ph1lou.werewolfapi.annotations.IntArg;
import fr.ph1lou.werewolfapi.annotations.Params;
import fr.ph1lou.werewolfapi.annotations.PlayerArg;
import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.bohemian.BohemianSacrificeEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfplugin.roles.villagers.Bohemian;
import org.bukkit.Bukkit;

@RoleCommand(key = "werewolf.roles.bohemian.command",
        roleKeys = RoleBase.BOHEMIAN,
        argNumbers = 2,
        args = @Params(players = @PlayerArg(index = 0, states = { StatePlayer.ALIVE, StatePlayer.JUDGEMENT }, excludeSelf = true),
                integers = @IntArg(index = 1, min = 1, max = 3)))
public class CommandBohemian implements IParameterizedCommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {

        Bohemian bohemian = (Bohemian) playerWW.getRole();
        IPlayerWW target = ctx.player(0);
        int amount = ctx.intValue(1);

        if (!bohemian.hasPower()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.bohemian.cooldown");
            return;
        }

        if (bohemian.getCurrentHonor() < amount) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.bohemian.not_enough_honor",
                    Formatter.number(bohemian.getCurrentHonor()));
            return;
        }

        BohemianSacrificeEvent event = new BohemianSacrificeEvent(playerWW, target, amount);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }

        bohemian.sacrificeHonor(amount);
        bohemian.setPower(false);

        switch (amount) {
            case 1:
                playerWW.sendMessageWithKey(Prefix.GREEN, "werewolf.roles.bohemian.info_honor",
                        Formatter.player(target.getName()),
                        Formatter.number(target.getHonor()));
                break;
            case 2:
                int count = bohemian.getTargetCount(target.getUUID());
                String nature = bohemian.getTargetNature(target.getUUID());
                String natureText = nature == null
                        ? game.translate("werewolf.roles.bohemian.no_command")
                        : game.translate(nature);
                playerWW.sendMessageWithKey(Prefix.GREEN, "werewolf.roles.bohemian.info_commands",
                        Formatter.player(target.getName()),
                        Formatter.number(count),
                        Formatter.format("&nature&", natureText));
                break;
            case 3:
                playerWW.sendMessageWithKey(Prefix.GREEN, "werewolf.roles.bohemian.info_camp",
                        Formatter.player(target.getName()),
                        Formatter.format("&camp&", game.translate(target.getRole().getDisplayCamp())));
                break;
            default:
                break;
        }
    }
}
