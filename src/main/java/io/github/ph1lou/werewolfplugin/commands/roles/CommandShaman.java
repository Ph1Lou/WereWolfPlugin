package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.AuraModifier;
import io.github.ph1lou.werewolfapi.Formatter;
import io.github.ph1lou.werewolfapi.ICommand;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Aura;
import io.github.ph1lou.werewolfapi.enums.Prefix;
import io.github.ph1lou.werewolfapi.events.roles.shaman.ShamanEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.IRole;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandShaman implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) {
            return;
        }

        UUID argUUID = UUID.fromString(args[0]);
        IPlayerWW playerWW1 = game.getPlayerWW(argUUID).orElse(null);

        if (playerWW1 == null) {
            return;
        }

        if (player.getHealth() < 3) {
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.role.shaman.not_enough_life");
            return;
        }

        int nTimesAffected;
        try {
            nTimesAffected = Integer.parseInt(args[1]);
        } catch(Exception e) {
            Bukkit.getLogger().warning("Failed to parse second argument");
            return;
        }


        if (game.getTimer() - playerWW1.getDeathTime() > 30 ||
                ((IAffectedPlayers) playerWW.getRole()).getAffectedPlayers().stream()
                        .filter(p -> p.equals(playerWW1)).count() > nTimesAffected) {
            playerWW.sendMessageWithKey(Prefix.YELLOW.getKey() , "werewolf.role.shaman.cannot_use");
            return;
        }

        ((IAffectedPlayers) playerWW.getRole()).addAffectedPlayer(playerWW1);

        ShamanEvent shamanEvent = new ShamanEvent(playerWW, playerWW1);

        Bukkit.getPluginManager().callEvent(shamanEvent);

        if(shamanEvent.isCancelled()){
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.cancel");
            return;
        }

        playerWW.removePlayerMaxHealth(2);
        playerWW.getRole().addAuraModifier(new AuraModifier("shaman", Aura.DARK,1,false));

        if (game.getRandom().nextBoolean()) {
            playerWW.sendMessageWithKey(Prefix.YELLOW.getKey() , "werewolf.role.shaman.victim_name",
                    Formatter.player(playerWW1.getName()));
        } else {
            IRole role = playerWW1.getRole();
            playerWW.sendMessageWithKey(Prefix.YELLOW.getKey() , "werewolf.role.shaman.victim_role",
                    Formatter.role(game.translate(role.getDisplayRole())));
        }

    }
}
