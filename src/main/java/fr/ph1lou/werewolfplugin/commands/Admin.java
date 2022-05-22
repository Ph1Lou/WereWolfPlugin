package fr.ph1lou.werewolfplugin.commands;

import fr.ph1lou.werewolfapi.annotations.AdminCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.game.IModerationManager;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.utils.Wrapper;
import fr.ph1lou.werewolfplugin.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Admin implements TabExecutor {

    private final Main main;
    private static Admin instance;

    public Admin(Main main) {
        this.main = main;
        instance = this;
    }

    public static Admin get() {
        return instance;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();

        if (!(sender instanceof Player)) {
            sender.sendMessage(game.translate(Prefix.RED , "werewolf.check.console"));
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

        Wrapper<ICommand, AdminCommand> commandRegister = null;
        WereWolfAPI game = main.getWereWolfAPI();

        for (Wrapper<ICommand, AdminCommand> commandRegister1 : main.getRegisterManager().getAdminCommandsRegister()) {
            if (game.translate(commandRegister1.getMetaDatas().key()).equalsIgnoreCase(commandName)) {
                commandRegister = commandRegister1;
            }
        }

        if (commandRegister == null) {
            execute("h", player, new String[0]);
            return;
        }

        if (accessCommand(commandRegister, player, args.length, true)) {
            commandRegister.getObject().ifPresent(iCommand -> iCommand.execute(game,player, args));
        }

    }


    public boolean accessCommand(Wrapper<ICommand, AdminCommand> commandRegister, Player player, int args, boolean seePermissionMessages) {

        WereWolfAPI game = main.getWereWolfAPI();

        if (commandRegister.getMetaDatas().statesGame().length > 0 &&
                Arrays.stream(commandRegister.getMetaDatas().statesGame())
                        .noneMatch(stateGame -> stateGame == game.getState())) {
            if (seePermissionMessages) {
                player.sendMessage(game.translate(Prefix.RED , "werewolf.check.state"));
            }
            return false;
        }

        if (commandRegister.getMetaDatas().argNumbers().length > 0 &&
            Arrays.stream(commandRegister.getMetaDatas().argNumbers()).noneMatch(value -> value == args)) {
            if (seePermissionMessages) {
                player.sendMessage(game.translate(Prefix.RED , "werewolf.check.parameters",
                        Formatter.number(Arrays.stream(commandRegister.getMetaDatas().argNumbers()).min().orElse(0))));
            }
            return false;
        }

        if (!checkPermission(commandRegister, player)) {
            if (seePermissionMessages) {
                player.sendMessage(game.translate(Prefix.RED , "werewolf.check.permission_denied"));
            }
            return false;
        }

        return true;
    }


    public boolean checkAccess(String commandeKey, Player player, boolean seePermissionMessages) {
        for (Wrapper<ICommand, AdminCommand> commandRegister : main.getRegisterManager().getAdminCommandsRegister()) {
            if (commandRegister.getMetaDatas().key().equals(commandeKey)) {
                return accessCommand(commandRegister, player, Arrays.stream(commandRegister.getMetaDatas().argNumbers()).min().orElse(0), seePermissionMessages);
            }
        }
        return false;
    }


    private boolean checkPermission(Wrapper<ICommand, AdminCommand> commandRegister, Player player) {

        WereWolfAPI game = main.getWereWolfAPI();
        IModerationManager moderationManager = game.getModerationManager();
        UUID uuid = player.getUniqueId();

        boolean pass = commandRegister.getMetaDatas().hostAccess() && moderationManager.getHosts().contains(uuid);

        if (commandRegister.getMetaDatas().moderatorAccess() && moderationManager.getModerators().contains(uuid)) {
            pass = true;
        }

        if (player.hasPermission("a." + game.translate(commandRegister.getMetaDatas().key()))) {
            pass = true;
        }

        return pass;
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (!(sender instanceof Player)) return null;

        Player player = (Player) sender;
        WereWolfAPI game = main.getWereWolfAPI();

        if (args.length > 1) {
            return null;
        }

        return main.getRegisterManager().getAdminCommandsRegister().stream()
                .filter(commandRegister -> (args[0].isEmpty() || game.translate(commandRegister.getMetaDatas().key()).startsWith(args[0])))
                .filter(iCommandAdminCommandWrapper -> iCommandAdminCommandWrapper.getMetaDatas().autoCompletion())
                .filter(commandRegister -> checkPermission(commandRegister, player))
                .filter(commandRegister -> commandRegister.getMetaDatas().statesGame().length == 0 ||
                        Arrays.stream(commandRegister.getMetaDatas().statesGame()).anyMatch(stateGame -> stateGame == game.getState()))
                .map(commandRegister -> game.translate(commandRegister.getMetaDatas().key()))
                .collect(Collectors.toList());
    }

}
