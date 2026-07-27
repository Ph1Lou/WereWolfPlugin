package fr.ph1lou.werewolfplugin.commands.roles.villager.info.analyst;

import fr.ph1lou.werewolfapi.annotations.Params;
import fr.ph1lou.werewolfapi.annotations.PlayerArg;
import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommandRole;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Camp;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalPotionEffectType;
import fr.ph1lou.werewolfapi.events.roles.analyst.AnalystExtraDetailsEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.AuraModifier;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfplugin.roles.villagers.Analyst;
import org.bukkit.Bukkit;

import java.util.stream.Collectors;

@RoleCommand(key = "werewolf.roles.analyst.command_analyse",
        roleKeys = RoleBase.ANALYST,
        requiredPower = true,
        argNumbers = 1,
        args = @Params(players = @PlayerArg(index = 0, states = { StatePlayer.ALIVE })))
public class CommandAnalystAnalyse implements IParameterizedCommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {

        Analyst analyst = (Analyst) playerWW.getRole();

        IPlayerWW playerWW1 = ctx.player(0);

        if (!analyst.getAffectedPlayers().contains(playerWW1)) {
            playerWW.sendMessageWithKey(Prefix.ORANGE, "werewolf.roles.analyst.not_affected");
            return;
        }

        analyst.setPower(false);

        AnalystExtraDetailsEvent analystEvent = new AnalystExtraDetailsEvent(playerWW, playerWW1, playerWW1.getPotionModifiers()
                .stream()
                .filter(PotionModifier::isAdd)
                .map(PotionModifier::getPotionEffectType)
                .collect(Collectors.toSet()));

        Bukkit.getPluginManager().callEvent(analystEvent);

        if (analystEvent.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }

        playerWW.sendMessageWithKey(Prefix.GREEN, "werewolf.roles.analyst.effects",
                Formatter.player(playerWW1.getName()),
                Formatter.format("&list&", analystEvent.getPotions()
                        .stream()
                        .map(UniversalPotionEffectType::name)
                        .collect(Collectors.joining(", "))));

        if (playerWW1.getRole().isCamp(Camp.VILLAGER)) {
            playerWW1.sendMessageWithKey(Prefix.RED, "werewolf.roles.analyst.call_back");
        } else {
            playerWW1.sendMessageWithKey(Prefix.RED, "werewolf.roles.analyst.call_back_no_villager",
                    Formatter.player(playerWW.getName()));
        }

        analyst.addAuraModifier(new AuraModifier(analyst.getKey(), Aura.DARK, 1, false));
    }
}
