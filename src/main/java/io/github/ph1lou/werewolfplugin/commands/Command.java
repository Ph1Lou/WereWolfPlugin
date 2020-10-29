package io.github.ph1lou.werewolfplugin.commands;


import io.github.ph1lou.werewolfapi.CommandRegister;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enumlg.State;
import io.github.ph1lou.werewolfapi.enumlg.StateLG;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.commands.roles.*;
import io.github.ph1lou.werewolfplugin.commands.utilities.*;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


public class Command implements TabExecutor {

    private final List<CommandRegister> listCommands = new ArrayList<>();
    private final Main main;

    public Command(Main main, GameManager game) {

        this.main = main;

        listCommands.add(new CommandRegister(main).registerCommand(new CommandSeer(main)).setName(game.translate("werewolf.role.seer.command")).addRoleKey("werewolf.role.seer.display").addRoleKey("werewolf.role.seer.display").setRoleOnly().setAlive());
        listCommands.add(new CommandRegister(main).registerCommand(new CommandCupid(main)).setName(game.translate("werewolf.role.cupid.command")).addRoleKey("werewolf.role.cupid.display").setRoleOnly().setAlive());
        listCommands.add(new CommandRegister(main).registerCommand(new CommandDetective(main)).setName(game.translate("werewolf.role.detective.command")).addRoleKey("werewolf.role.detective.display").setRoleOnly().setAlive());
        listCommands.add(new CommandRegister(main).registerCommand(new CommandFallenAngel(main)).setName(game.translate("werewolf.role.angel.command_2")).addRoleKey("werewolf.role.angel.display").setRoleOnly().setAlive());
        listCommands.add(new CommandRegister(main).registerCommand(new CommandGuardianAngel(main)).setName(game.translate("werewolf.role.angel.command_1")).addRoleKey("werewolf.role.angel.display").setRoleOnly().setAlive());
        listCommands.add(new CommandRegister(main).registerCommand(new CommandInfect(main)).setName(game.translate("werewolf.role.infect_father_of_the_wolves.command")).addRoleKey("werewolf.role.infect_father_of_the_wolves.display").setRoleOnly().unsetAutoCompletion().setAlive());
        listCommands.add(new CommandRegister(main).registerCommand(new CommandFox(main)).setName(game.translate("werewolf.role.fox.command")).addRoleKey("werewolf.role.fox.display").setRoleOnly().setAlive());
        listCommands.add(new CommandRegister(main).registerCommand(new CommandLovers(main)).setName(game.translate("werewolf.role.lover.command")).setAlive());
        listCommands.add(new CommandRegister(main).registerCommand(new CommandProtector(main)).setName(game.translate("werewolf.role.protector.command")).addRoleKey("werewolf.role.protector.display").setRoleOnly().setAlive());
        listCommands.add(new CommandRegister(main).registerCommand(new CommandRaven(main)).setName(game.translate("werewolf.role.raven.command")).addRoleKey("werewolf.role.raven.display").setRoleOnly().setAlive());
        listCommands.add(new CommandRegister(main).registerCommand(new CommandCitizenCancelVote(main)).setName(game.translate("werewolf.role.citizen.command_2")).addRoleKey("werewolf.role.citizen.display").setRoleOnly().setAlive());
        listCommands.add(new CommandRegister(main).registerCommand(new CommandCitizenSeeVote(main)).setName(game.translate("werewolf.role.citizen.command_1")).addRoleKey("werewolf.role.citizen.display").setRoleOnly().setAlive());
        listCommands.add(new CommandRegister(main).registerCommand(new CommandTroubleMaker(main)).setName(game.translate("werewolf.role.troublemaker.command")).addRoleKey("werewolf.role.troublemaker.display").setRoleOnly().setAlive());
        listCommands.add(new CommandRegister(main).registerCommand(new CommandWereWolf(main)).setName(game.translate("werewolf.role.werewolf.command")));
        listCommands.add(new CommandRegister(main).registerCommand(new CommandWildChild(main)).setName(game.translate("werewolf.role.wild_child.command")).addRoleKey("werewolf.role.wild_child.display").setRoleOnly().setAlive());
        listCommands.add(new CommandRegister(main).registerCommand(new CommandComedian(main)).setName(game.translate("werewolf.role.comedian.command")).addRoleKey("werewolf.role.comedian.display").setRoleOnly().setAlive());
        listCommands.add(new CommandRegister(main).registerCommand(new CommandWitch(main)).setName(game.translate("werewolf.role.witch.command")).addRoleKey("werewolf.role.witch.display").setRoleOnly().unsetAutoCompletion().setAlive());
        listCommands.add(new CommandRegister(main).registerCommand(new CommandTrapper(main)).setName(game.translate("werewolf.role.trapper.command")).addRoleKey("werewolf.role.trapper.display").setRoleOnly().setAlive());
        listCommands.add(new CommandRegister(main).registerCommand(new CommandAngelRegen(main)).setName(game.translate("werewolf.role.guardian_angel.command")).addRoleKey("werewolf.role.angel.display").addRoleKey("werewolf.role.guardian_angel.display").setRoleOnly().setAlive());
        listCommands.add(new CommandRegister(main).registerCommand(new CommandSuccubus(main)).setName(game.translate("werewolf.role.succubus.command")).addRoleKey("werewolf.role.succubus.display").setRoleOnly().setAlive());
        listCommands.add(new CommandRegister(main).registerCommand(new CommandFlutePlayer(main)).setName(game.translate("werewolf.role.flute_player.command")).addRoleKey("werewolf.role.flute_player.display").setRoleOnly().setAlive());
        listCommands.add(new CommandRegister(main).registerCommand(new CommandLibrarian(main)).setName(game.translate("werewolf.role.librarian.command")).addRoleKey("werewolf.role.librarian.display").setRoleOnly().setAlive());
        listCommands.add(new CommandRegister(main).registerCommand(new CommandSendToLibrarian(main)).setName(game.translate("werewolf.role.librarian.request_command")).setAlive());
        listCommands.add(new CommandRegister(main).registerCommand(new CommandRole(main)).setName(game.translate("werewolf.menu.roles.command_1")).setAlive());
        listCommands.add(new CommandRegister(main).registerCommand(new CommandRank(main)).setName(game.translate("werewolf.menu.rank.command")));
        listCommands.add(new CommandRegister(main).registerCommand(new CommandRules(main)).setName(game.translate("werewolf.menu.global.command")));
        listCommands.add(new CommandRegister(main).registerCommand(new CommandCompo(main)).setName(game.translate("werewolf.menu.roles.command_2")));
        listCommands.add(new CommandRegister(main).registerCommand(new CommandScenarios(main)).setName(game.translate("werewolf.menu.scenarios.command")));
        listCommands.add(new CommandRegister(main).registerCommand(new CommandEnchantment(main)).setName(game.translate("werewolf.menu.enchantments.command")));
        listCommands.add(new CommandRegister(main).registerCommand(new CommandTimer(main)).setName(game.translate("werewolf.menu.timers.command")));
        listCommands.add(new CommandRegister(main).registerCommand(new CommandVote(main)).setName(game.translate("werewolf.vote.command")).setAlive());
        listCommands.add(new CommandRegister(main).registerCommand(new CommandHelp(main)).setName("h"));
        listCommands.add(new CommandRegister(main).registerCommand(new CommandAnonymeChat(main)).setName("?").setAlive());

        registerExternCommand();
    }


    private void registerExternCommand() {
        for (CommandRegister commandRegister : main.getListCommands()) {

            listCommands.removeAll(listCommands.stream()
                    .filter(commandRegister1 -> commandRegister1.getName().equalsIgnoreCase(commandRegister.getName()))
                    .collect(Collectors.toList()));
            listCommands.add(commandRegister);
        }
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command cmd, @NotNull String label, String[] args) {

        GameManager game = main.getCurrentGame();

        if (!(sender instanceof Player)) {
            sender.sendMessage(game.translate("werewolf.check.console"));
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            execute("h", player, null);
        } else {
            execute(args[0], player, Arrays.copyOfRange(args, 1, args.length));
        }

        return true;
    }

    private void execute(String commandName, Player player, String[] args) {

        CommandRegister commandRegister = null;
        GameManager game = main.getCurrentGame();
        UUID uuid = player.getUniqueId();

        for (CommandRegister commandRegister1 : this.listCommands) {
            if (commandRegister1.getName().equalsIgnoreCase(commandName)) {
                commandRegister = commandRegister1;
            }
        }

        if (commandRegister == null) {
            execute("h", player, null);
            return;
        }

        if (commandRegister.isRequiredPlayerInGame()) {

            if (!game.getPlayersWW().containsKey(uuid)) {
                player.sendMessage(game.translate("werewolf.check.not_in_game"));
                return;
            }

            PlayerWW plg = game.getPlayersWW().get(uuid);

            if (commandRegister.isAlive()) {

                if (!plg.isState(State.ALIVE)) {
                    player.sendMessage(game.translate("werewolf.check.death"));
                    return;
                }
            }

            if (commandRegister.isRoleOnly()) {

                if (!game.isState(StateLG.GAME)) {
                    player.sendMessage(game.translate("werewolf.check.game_not_in_progress"));
                    return;
                }

                if (!commandRegister.isRoleKey(plg.getRole().getDisplay())) {
                    player.sendMessage(game.translate("werewolf.check.role", game.translate("werewolf.role.comedian.display")));
                    return;
                }

            }
        }

        commandRegister.getCommand().execute(player, args);
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (!(sender instanceof Player)) return null;

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        GameManager game = main.getCurrentGame();
        PlayerWW playerWW = game.getPlayerWW(uuid);

        if (args.length > 1) {
            return null;
        }

        return listCommands.stream()
                .filter(commandRegister -> (args[0].isEmpty() || commandRegister.getName().contains(args[0])))
                .filter(commandRegister -> !commandRegister.isRoleOnly() || (playerWW != null && commandRegister.isRoleKey(playerWW.getRole().getDisplay())))
                .filter(CommandRegister::isAutoCompletion)
                .map(CommandRegister::getName)
                .collect(Collectors.toList());
    }
}
