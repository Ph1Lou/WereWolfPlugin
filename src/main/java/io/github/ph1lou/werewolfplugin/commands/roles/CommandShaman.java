package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.ICommands;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.events.roles.shaman.ShamanEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.IDisplay;
import io.github.ph1lou.werewolfapi.rolesattributs.IRole;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public class CommandShaman implements ICommands {

    private final Main main;

    public CommandShaman(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();
        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid);

        if (playerWW == null) {
            return;
        }

        UUID argUUID = UUID.fromString(args[0]);
        IPlayerWW playerWW1 = game.getPlayerWW(argUUID);

        if (playerWW1 == null) {
            return;
        }

        if (player.getHealth() < 3) {
            playerWW.sendMessageWithKey("werewolf.role.shaman.not_enough_life");
            return;
        }

        int nTimesAffected;
        try {
            nTimesAffected = Integer.parseInt(args[1]);
        } catch(Exception e) {
            Bukkit.getLogger().warning("Failed to parse second argument");
            return;
        }


        if (game.getScore().getTimer() - playerWW1.getDeathTime() > 30 ||
                ((IAffectedPlayers) playerWW.getRole()).getAffectedPlayers().stream()
                        .filter(p -> p.equals(playerWW1)).count() > nTimesAffected) {
            playerWW.sendMessageWithKey("werewolf.role.shaman.cannot_use");
            return;
        }

        ((IAffectedPlayers) playerWW.getRole()).addAffectedPlayer(playerWW1);

        ShamanEvent shamanEvent = new ShamanEvent(playerWW, playerWW1);

        playerWW.removePlayerMaxHealth(2);

        if (game.getRandom().nextBoolean()) {
            playerWW.sendMessageWithKey("werewolf.role.shaman.victim_name", playerWW1.getName());
        } else {
            IRole role = playerWW1.getRole();
            playerWW.sendMessageWithKey("werewolf.role.shaman.victim_role",
                    game.translate(role.getDisplayRole()));
        }

    }
}
