package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.ConfigsBase;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.InfectionEvent;
import io.github.ph1lou.werewolfapi.events.NewWereWolfEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandInfect implements Commands {


    private final Main main;

    public CommandInfect(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {


        WereWolfAPI game = main.getWereWolfAPI();
        UUID uuid = player.getUniqueId();
        PlayerWW playerWW = game.getPlayerWW(uuid);

        if (playerWW == null) return;

        Roles infect = playerWW.getRole();

        if(Bukkit.getPlayer(UUID.fromString(args[0]))==null){
            player.sendMessage(game.translate("werewolf.check.offline_player"));
            return;
        }
        UUID argUUID = UUID.fromString(args[0]);
        PlayerWW playerWW1 = game.getPlayerWW(argUUID);

        if (!game.getConfig().isConfigActive(ConfigsBase.AUTO_REZ_INFECT.getKey()) && argUUID.equals(uuid)) {
            player.sendMessage(game.translate("werewolf.check.not_yourself"));
            return;
        }

        if (playerWW1 == null) {
            player.sendMessage(game.translate("werewolf.check.player_not_found"));
            return;
        }

        if (!playerWW1.isState(StatePlayer.JUDGEMENT)) {
            player.sendMessage(game.translate("werewolf.check.not_in_judgement"));
            return;
        }


        PlayerWW killerWW = playerWW1.getLastKiller();

        if (killerWW == null ||
                !killerWW
                        .getRole()
                        .isWereWolf() ||
                game.getScore().getTimer() - playerWW1.getDeathTime() > 7) {

            player.sendMessage(game.translate("werewolf.role.infect_father_of_the_wolves.player_cannot_be_infected"));
            return;
        }


        ((Power) infect).setPower(false);

        InfectionEvent infectionEvent = new InfectionEvent(playerWW, playerWW1);
        Bukkit.getPluginManager().callEvent(infectionEvent);

        if (infectionEvent.isCancelled()) {
            player.sendMessage(game.translate("werewolf.check.cancel"));
            return;
        }

        ((AffectedPlayers) infect).addAffectedPlayer(playerWW1);

        player.sendMessage(game.translate("werewolf.role.infect_father_of_the_wolves.infection_perform",
                playerWW1.getName()));
        game.resurrection(playerWW1);

        if (!playerWW1.getRole().isWereWolf()) { //si déjà loup
            playerWW1.getRole().setInfected(); //pour qu'il sois actualisé en tan que loup
            Bukkit.getPluginManager().callEvent(
                    new NewWereWolfEvent(playerWW1));
        }

        playerWW1.getRole().setInfected(); //répétition indispensable
        game.checkVictory();
    }
}
