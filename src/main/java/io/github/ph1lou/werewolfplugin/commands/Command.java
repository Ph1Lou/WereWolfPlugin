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

        listCommands.add(new CommandRegister().registerCommand(new CommandSeer(main)).setName(game.translate("werewolf.role.seer.command")).addRoleKey("werewolf.role.seer.display").addRoleKey("werewolf.role.seer.display").setRoleOnly().addStateAccess(State.ALIVE).addStateWW(StateLG.GAME).addArgNumbers(1));
        listCommands.add(new CommandRegister().registerCommand(new CommandCupid(main)).setName(game.translate("werewolf.role.cupid.command")).addRoleKey("werewolf.role.cupid.display").setRoleOnly().addStateAccess(State.ALIVE).addStateWW(StateLG.GAME).addArgNumbers(2));
        listCommands.add(new CommandRegister().registerCommand(new CommandDetective(main)).setName(game.translate("werewolf.role.detective.command")).addRoleKey("werewolf.role.detective.display").setRoleOnly().addStateAccess(State.ALIVE).addStateWW(StateLG.GAME).addArgNumbers(2));
        listCommands.add(new CommandRegister().registerCommand(new CommandFallenAngel(main)).setName(game.translate("werewolf.role.angel.command_2")).addRoleKey("werewolf.role.angel.display").setRoleOnly().addStateAccess(State.ALIVE).addStateWW(StateLG.GAME).addArgNumbers(1));
        listCommands.add(new CommandRegister().registerCommand(new CommandGuardianAngel(main)).setName(game.translate("werewolf.role.angel.command_1")).addRoleKey("werewolf.role.angel.display").setRoleOnly().addStateAccess(State.ALIVE).addStateWW(StateLG.GAME).addArgNumbers(1));
        listCommands.add(new CommandRegister().registerCommand(new CommandInfect(main)).setName(game.translate("werewolf.role.infect_father_of_the_wolves.command")).addRoleKey("werewolf.role.infect_father_of_the_wolves.display").setRoleOnly().unsetAutoCompletion().addStateAccess(State.ALIVE).addStateWW(StateLG.GAME).addArgNumbers(1));
        listCommands.add(new CommandRegister().registerCommand(new CommandFox(main)).setName(game.translate("werewolf.role.fox.command")).addRoleKey("werewolf.role.fox.display").setRoleOnly().addStateAccess(State.ALIVE).addStateWW(StateLG.GAME).addArgNumbers(1));
        listCommands.add(new CommandRegister().registerCommand(new CommandLovers(main)).setName(game.translate("werewolf.role.lover.command")).addStateAccess(State.ALIVE).addStateWW(StateLG.GAME).addArgNumbers(2).addArgNumbers(3));
        listCommands.add(new CommandRegister().registerCommand(new CommandProtector(main)).setName(game.translate("werewolf.role.protector.command")).addRoleKey("werewolf.role.protector.display").setRoleOnly().addStateAccess(State.ALIVE).addStateWW(StateLG.GAME).addArgNumbers(1));
        listCommands.add(new CommandRegister().registerCommand(new CommandRaven(main)).setName(game.translate("werewolf.role.raven.command")).addRoleKey("werewolf.role.raven.display").setRoleOnly().addStateAccess(State.ALIVE).addStateWW(StateLG.GAME).addArgNumbers(1));
        listCommands.add(new CommandRegister().registerCommand(new CommandCitizenCancelVote(main)).setName(game.translate("werewolf.role.citizen.command_2")).addRoleKey("werewolf.role.citizen.display").setRoleOnly().addStateAccess(State.ALIVE).addStateWW(StateLG.GAME).addArgNumbers(0));
        listCommands.add(new CommandRegister().registerCommand(new CommandCitizenSeeVote(main)).setName(game.translate("werewolf.role.citizen.command_1")).addRoleKey("werewolf.role.citizen.display").setRoleOnly().addStateAccess(State.ALIVE).addStateWW(StateLG.GAME).addArgNumbers(0));
        listCommands.add(new CommandRegister().registerCommand(new CommandTroubleMaker(main)).setName(game.translate("werewolf.role.troublemaker.command")).addRoleKey("werewolf.role.troublemaker.display").setRoleOnly().addStateAccess(State.ALIVE).addStateWW(StateLG.GAME).addArgNumbers(1));
        listCommands.add(new CommandRegister().registerCommand(new CommandWereWolf(main)).setName(game.translate("werewolf.role.werewolf.command")).addStateWW(StateLG.GAME).addArgNumbers(0));
        listCommands.add(new CommandRegister().registerCommand(new CommandWildChild(main)).setName(game.translate("werewolf.role.wild_child.command")).addRoleKey("werewolf.role.wild_child.display").setRoleOnly().addStateAccess(State.ALIVE).addStateWW(StateLG.GAME).addArgNumbers(1));
        listCommands.add(new CommandRegister().registerCommand(new CommandComedian(main)).setName(game.translate("werewolf.role.comedian.command")).addRoleKey("werewolf.role.comedian.display").setRoleOnly().addStateAccess(State.ALIVE).addStateWW(StateLG.GAME).addArgNumbers(1));
        listCommands.add(new CommandRegister().registerCommand(new CommandWitch(main)).setName(game.translate("werewolf.role.witch.command")).addRoleKey("werewolf.role.witch.display").setRoleOnly().unsetAutoCompletion().addStateAccess(State.ALIVE).addStateWW(StateLG.GAME).addArgNumbers(1));
        listCommands.add(new CommandRegister().registerCommand(new CommandTrapper(main)).setName(game.translate("werewolf.role.trapper.command")).addRoleKey("werewolf.role.trapper.display").setRoleOnly().addStateAccess(State.ALIVE).addStateWW(StateLG.GAME).addArgNumbers(1));
        listCommands.add(new CommandRegister().registerCommand(new CommandAngelRegen(main)).setName(game.translate("werewolf.role.guardian_angel.command")).addRoleKey("werewolf.role.angel.display").addRoleKey("werewolf.role.guardian_angel.display").setRoleOnly().addStateAccess(State.ALIVE).addStateWW(StateLG.GAME).addArgNumbers(0));
        listCommands.add(new CommandRegister().registerCommand(new CommandSuccubus(main)).setName(game.translate("werewolf.role.succubus.command")).addRoleKey("werewolf.role.succubus.display").setRoleOnly().addStateAccess(State.ALIVE).addStateWW(StateLG.GAME).addArgNumbers(1));
        listCommands.add(new CommandRegister().registerCommand(new CommandFlutePlayer(main)).setName(game.translate("werewolf.role.flute_player.command")).addRoleKey("werewolf.role.flute_player.display").setRoleOnly().addStateAccess(State.ALIVE).addStateWW(StateLG.GAME).addArgNumbers(1).addArgNumbers(2));
        listCommands.add(new CommandRegister().registerCommand(new CommandLibrarian(main)).setName(game.translate("werewolf.role.librarian.command")).addRoleKey("werewolf.role.librarian.display").setRoleOnly().addStateAccess(State.ALIVE).addStateWW(StateLG.GAME).addArgNumbers(1));
        listCommands.add(new CommandRegister().registerCommand(new CommandSendToLibrarian(main)).setName(game.translate("werewolf.role.librarian.request_command")).addStateAccess(State.ALIVE).addStateWW(StateLG.GAME));
        listCommands.add(new CommandRegister().registerCommand(new CommandRole(main)).setName(game.translate("werewolf.menu.roles.command_1")).addStateAccess(State.ALIVE).addStateWW(StateLG.GAME).addArgNumbers(0));
        listCommands.add(new CommandRegister().registerCommand(new CommandRank(main)).setName(game.translate("werewolf.menu.rank.command")).addStateWW(StateLG.GAME).addArgNumbers(0));
        listCommands.add(new CommandRegister().registerCommand(new CommandRules(main)).setName(game.translate("werewolf.menu.global.command")).addStateWW(StateLG.GAME).addArgNumbers(0));
        listCommands.add(new CommandRegister().registerCommand(new CommandCompo(main)).setName(game.translate("werewolf.menu.roles.command_2")).addStateWW(StateLG.GAME).addArgNumbers(0));
        listCommands.add(new CommandRegister().registerCommand(new CommandScenarios(main)).setName(game.translate("werewolf.menu.scenarios.command")).addStateWW(StateLG.GAME).addArgNumbers(0));
        listCommands.add(new CommandRegister().registerCommand(new CommandEnchantment(main)).setName(game.translate("werewolf.menu.enchantments.command")).addStateWW(StateLG.GAME).addArgNumbers(0));
        listCommands.add(new CommandRegister().registerCommand(new CommandTimer(main)).setName(game.translate("werewolf.menu.timers.command")).addStateWW(StateLG.GAME).addArgNumbers(0));
        listCommands.add(new CommandRegister().registerCommand(new CommandVote(main)).setName(game.translate("werewolf.vote.command")).addStateAccess(State.ALIVE).addStateWW(StateLG.GAME).addArgNumbers(1));
        listCommands.add(new CommandRegister().registerCommand(new CommandHelp(main)).setName("h"));
        listCommands.add(new CommandRegister().registerCommand(new CommandAnonymeChat(main)).setName("?").addStateWW(StateLG.GAME).addStateWW(StateLG.START).setRequiredPlayerInGame().addStateAccess(State.ALIVE));

        registerExternCommand();
    }


    private void registerExternCommand() {

        main.getListCommands().forEach(commandRegister -> {
            listCommands.removeAll(listCommands.stream()
                    .filter(commandRegister1 -> commandRegister1.getName().equalsIgnoreCase(commandRegister.getName()))
                    .collect(Collectors.toList()));
            listCommands.add(commandRegister);
        });
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
            execute("h", player, new String[0]);
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
            execute("h", player, new String[0]);
            return;
        }

        if (commandRegister.isRequiredPlayerInGame()) {

            if (!game.getPlayersWW().containsKey(uuid)) {
                player.sendMessage(game.translate("werewolf.check.not_in_game"));
                return;
            }

            PlayerWW plg = game.getPlayersWW().get(uuid);

            if (!commandRegister.isStateAccess(plg.getState())) {

                player.sendMessage(game.translate("werewolf.check.death"));
                return;

            }

            if (commandRegister.isRoleOnly()) {

                if (!commandRegister.isRoleKey(plg.getRole().getDisplay())) {
                    player.sendMessage(game.translate("werewolf.check.role", game.translate("werewolf.role.comedian.display")));
                    return;
                }

            }
        }

        if (!commandRegister.isStateWW(game.getState())) {
            player.sendMessage(game.translate("werewolf.check.game_not_in_progress"));
            return;
        }

        if (!commandRegister.isArgNumbers(args.length)) {
            player.sendMessage(game.translate("werewolf.check.parameters", commandRegister.getMinArgNumbers()));
            return;
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
