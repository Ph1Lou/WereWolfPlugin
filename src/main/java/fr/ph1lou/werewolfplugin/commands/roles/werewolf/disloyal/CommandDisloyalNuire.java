package fr.ph1lou.werewolfplugin.commands.roles.werewolf.disloyal;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommandRole;
import fr.ph1lou.werewolfapi.events.roles.disloyal_werewolf.DisloyalHarmEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfplugin.roles.werewolfs.DisloyalWerewolf;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.Set;

@RoleCommand(key = "werewolf.roles.disloyal_werewolf.nuire_command",
        roleKeys = RoleBase.DISLOYAL_WEREWOLF,
        requiredLimitedUse = 2,
        argNumbers = 0)
public class CommandDisloyalNuire implements IParameterizedCommandRole {

    private static final double HARM_DISTANCE = 30d;

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {

        DisloyalWerewolf disloyal = (DisloyalWerewolf) playerWW.getRole();
        IPlayerWW self = playerWW;

        Set<IPlayerWW> targets = new HashSet<>();
        for (IPlayerWW p : game.getAlivePlayersWW()) {
            if (p.equals(self)) continue;
            if (p.getLocation().getWorld() != self.getLocation().getWorld()) continue;
            if (p.distance(self) < HARM_DISTANCE) {
                targets.add(p);
            }
        }

        DisloyalHarmEvent event = new DisloyalHarmEvent(self, targets);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }

        int affected = 0;
        for (IPlayerWW p : targets) {
            p.modifyHonor(-1);
            affected++;
            if (p.getRole().isWereWolf()) {
                p.sendMessageWithKey(Prefix.RED, "werewolf.roles.disloyal_werewolf.nuire_warn",
                        Formatter.player(self.getName()));
            }
        }

        disloyal.setUse(disloyal.getUse() + 1);

        playerWW.sendMessageWithKey(Prefix.GREEN, "werewolf.roles.disloyal_werewolf.nuire_perform",
                Formatter.number(affected));
    }
}
