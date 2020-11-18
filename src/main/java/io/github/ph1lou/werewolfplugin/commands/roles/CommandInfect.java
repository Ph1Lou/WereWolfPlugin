package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.ConfigsBase;
import io.github.ph1lou.werewolfapi.enumlg.StatePlayer;
import io.github.ph1lou.werewolfapi.events.InfectionEvent;
import io.github.ph1lou.werewolfapi.events.NewWereWolfEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;
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
        PlayerWW plg = game.getPlayersWW().get(uuid);
        Roles infect = plg.getRole();

        if (args.length != 1) {
            player.sendMessage(game.translate("werewolf.check.player_input"));
            return;
        }

        if(!((Power)infect).hasPower()) {
            player.sendMessage(game.translate("werewolf.check.power"));
            return;
        }

        if(Bukkit.getPlayer(UUID.fromString(args[0]))==null){
            player.sendMessage(game.translate("werewolf.check.offline_player"));
            return;
        }
        UUID argUUID = UUID.fromString(args[0]);

        if (!game.getConfig().getConfigValues().get(ConfigsBase.AUTO_REZ_INFECT.getKey()) && argUUID.equals(uuid)) {
            player.sendMessage(game.translate("werewolf.check.not_yourself"));
            return;
        }

        if(!game.getPlayersWW().containsKey(argUUID)) {
            player.sendMessage(game.translate("werewolf.check.player_not_found"));
            return;
        }
        PlayerWW plg1 = game.getPlayersWW().get(argUUID);

        if (!plg1.isState(StatePlayer.JUDGEMENT)) {
            player.sendMessage(game.translate("werewolf.check.not_in_judgement"));
            return;
        }

        if (plg1.getLastKiller() == null ||
                !Objects.requireNonNull(
                        game.getPlayerWW(plg1.getLastKiller()))
                        .getRole()
                        .isWereWolf() ||
                game.getScore().getTimer() - plg1.getDeathTime() > 7) {

            player.sendMessage(game.translate("werewolf.role.infect_father_of_the_wolves.player_cannot_be_infected"));
            return;
        }


        ((Power) infect).setPower(false);

        InfectionEvent infectionEvent = new InfectionEvent(uuid, argUUID);
        Bukkit.getPluginManager().callEvent(infectionEvent);

        if (infectionEvent.isCancelled()) {
            player.sendMessage(game.translate("werewolf.check.cancel"));
            return;
        }

        ((AffectedPlayers) infect).addAffectedPlayer(argUUID);

        player.sendMessage(game.translate("werewolf.role.infect_father_of_the_wolves.infection_perform", plg1.getName()));
        game.resurrection(argUUID);

        if (!plg1.getRole().isWereWolf()) { //si déjà loup
            plg1.getRole().setInfected(); //pour qu'il sois actualisé en tan que loup
            Bukkit.getPluginManager().callEvent(
                    new NewWereWolfEvent(argUUID));
        }

        plg1.getRole().setInfected(); //répétition indispensable
        game.checkVictory();
    }
}
