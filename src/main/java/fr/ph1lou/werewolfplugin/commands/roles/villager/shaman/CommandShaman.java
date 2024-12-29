package fr.ph1lou.werewolfplugin.commands.roles.villager.shaman;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.ICommandRole;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.events.roles.shaman.ShamanEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.AuraModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import org.bukkit.Bukkit;

import java.util.UUID;

@RoleCommand(key = "werewolf.roles.shaman.command",
        roleKeys = RoleBase.SHAMAN,
        argNumbers = 1,
        autoCompletion = false)
public class CommandShaman implements ICommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, String[] args) {

        UUID argUUID = UUID.fromString(args[0]);
        IPlayerWW playerWW1 = game.getPlayerWW(argUUID).orElse(null);

        if (playerWW1 == null) {
            return;
        }

        if (playerWW.getHealth() < 3) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.shaman.not_enough_life");
            return;
        }

        if (game.getTimer() - 14 - playerWW1.getDeathTime() > 30) {
            playerWW.sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.shaman.cannot_use");
            return;
        }

        if (((IAffectedPlayers) playerWW.getRole()).getAffectedPlayers().contains(playerWW1)) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.already_get_power");
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
