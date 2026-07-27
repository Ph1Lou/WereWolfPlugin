package fr.ph1lou.werewolfplugin.commands.roles.villager.shaman;

import fr.ph1lou.werewolfapi.annotations.Params;
import fr.ph1lou.werewolfapi.annotations.PlayerArg;
import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommandRole;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.events.roles.shaman.ShamanEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.AuraModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import org.bukkit.Bukkit;

@RoleCommand(key = "werewolf.roles.shaman.command",
        roleKeys = RoleBase.SHAMAN,
        argNumbers = 1,
        autoCompletion = false,
        args = @Params(players = @PlayerArg(index = 0, byUuid = true, rejectIfAlreadyAffected = true)))
public class CommandShaman implements IParameterizedCommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {

        IPlayerWW playerWW1 = ctx.player(0);

        if (playerWW.getHealth() < 3) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.shaman.not_enough_life");
            return;
        }

        if (game.getTimer() - 14 - playerWW1.getDeathTime() > 30) {
            playerWW.sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.shaman.cannot_use");
            return;
        }

        IPlayerWW killerWW = playerWW1.getLastKiller().orElse(null);

        if (killerWW == null) {
            return;
        }

        ((IAffectedPlayers) playerWW.getRole()).addAffectedPlayer(playerWW1);

        ShamanEvent shamanEvent = new ShamanEvent(playerWW, playerWW1);

        Bukkit.getPluginManager().callEvent(shamanEvent);

        if (shamanEvent.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }

        playerWW.removePlayerMaxHealth(3);
        playerWW.getRole().addAuraModifier(new AuraModifier(playerWW.getRole().getKey(), Aura.DARK, 1, false));

        if (game.getRandom().nextBoolean()) {
            playerWW.sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.shaman.victim_name",
                    Formatter.player(killerWW.getName()));
        } else {
            IRole role = killerWW.getRole();
            playerWW.sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.shaman.victim_role",
                    Formatter.role(game.translate(role.getDisplayRole())));
        }

    }
}
