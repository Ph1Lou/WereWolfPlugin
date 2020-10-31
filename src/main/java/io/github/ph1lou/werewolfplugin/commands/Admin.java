package io.github.ph1lou.werewolfplugin.commands;

import io.github.ph1lou.werewolfapi.CommandRegister;
import io.github.ph1lou.werewolfapi.ModerationManagerAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfplugin.Main;
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

    public Admin(Main main) {
        this.main = main;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();

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
        WereWolfAPI game = main.getWereWolfAPI();

        for (CommandRegister commandRegister1 : main.getRegisterManager().getAdminCommandsRegister()) {
            if (game.translate(commandRegister1.getKey()).equalsIgnoreCase(commandName)) {
                commandRegister = commandRegister1;
            }
        }

        if (commandRegister == null) {
            execute("h", player, new String[0]);
            return;
        }

        if (!checkPermission(commandRegister, player)) {
            player.sendMessage(game.translate("werewolf.check.permission_denied"));
            return;
        }

        if (!commandRegister.isStateWW(game.getState())) {
            player.sendMessage(game.translate("werewolf.check.state"));
            return;
        }

        if (!commandRegister.isArgNumbers(args.length)) {
            player.sendMessage(game.translate("werewolf.check.parameters", commandRegister.getMinArgNumbers()));
            return;
        }

        commandRegister.getCommand().execute(player, args);
    }

    private boolean checkPermission(CommandRegister commandRegister, Player player) {

        WereWolfAPI game = main.getWereWolfAPI();
        ModerationManagerAPI moderationManager = game.getModerationManager();
        UUID uuid = player.getUniqueId();

        boolean pass = false;

        if (commandRegister.isHostAccess() && moderationManager.getHosts().contains(uuid)) {
            pass = true;
        }

        if (commandRegister.isModeratorAccess() && moderationManager.getModerators().contains(uuid)) {
            pass = true;
        }

        if (player.hasPermission("a"+game.translate(commandRegister.getKey()))) {
            pass = true;
        }

        return pass;
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (!(sender instanceof Player)) return null;

        Player player = (Player) sender;
        WereWolfAPI game = main.getWereWolfAPI();
        UUID uuid = player.getUniqueId();
        PlayerWW playerWW = game.getPlayerWW(uuid);

        if (args.length > 1) {
            return null;
        }

        return main.getRegisterManager().getAdminCommandsRegister().stream()
                .filter(commandRegister -> (args[0].isEmpty() || game.translate(commandRegister.getKey()).contains(args[0])))
                .filter(CommandRegister::isAutoCompletion)
                .filter(commandRegister -> checkPermission(commandRegister, player))
                .filter(commandRegister -> commandRegister.isStateWW(game.getState()))
                .filter(commandRegister -> !commandRegister.isRequiredPlayerInGame() || playerWW != null)
                .filter(commandRegister -> playerWW == null || commandRegister.isStateAccess(playerWW.getState()))
                .map(commandRegister -> game.translate(commandRegister.getKey()))
                .collect(Collectors.toList());
    }

}
