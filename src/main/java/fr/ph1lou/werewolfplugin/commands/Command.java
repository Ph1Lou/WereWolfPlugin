package fr.ph1lou.werewolfplugin.commands;


import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.Prefix;
import fr.ph1lou.werewolfapi.registers.impl.CommandRegister;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
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

    private final Main main;

    public Command(Main main) {
        this.main = main;
    }



    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command cmd, @NotNull String label, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();

        if (!(sender instanceof Player)) {
            sender.sendMessage(game.translate(Prefix.RED.getKey() , "werewolf.check.console"));
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

        WereWolfAPI game = main.getWereWolfAPI();
        List<String> messages = new ArrayList<>();

        main.getRegisterManager().getCommandsRegister()
                .stream()
                .filter(commandRegister1 -> game.translate(commandRegister1.getKey()).equalsIgnoreCase(commandName))
                .forEach(commandRegister1 -> messages.add(checkCommands(game, player, commandRegister1, args)));

        if (messages.isEmpty()) {
            execute("h", player, new String[0]);
            return;
        }

        if (messages.stream().anyMatch(String::isEmpty)) {
            return;
        }

        player.sendMessage(messages.stream().filter(s -> !s.equals("ignored")).findFirst().orElse(""));

    }

    private String checkCommands(WereWolfAPI game, Player player, CommandRegister commandRegister, String[] args) {

        UUID uuid = player.getUniqueId();

        if (commandRegister.isRequiredPlayerInGame()) {

            IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

            if (playerWW == null) {
                return game.translate(Prefix.RED.getKey() , "werewolf.check.not_in_game");
            }

            if (commandRegister.isRoleOnly()) {
                if (!commandRegister.isRoleKey(playerWW.getRole().getKey())) {
                    return "ignored";
                }
            }

            if (!commandRegister.isStateAccess(playerWW.getState())) {
                return game.translate(Prefix.RED.getKey() , "werewolf.check.state_player");
            }

            if (commandRegister.isRequiredPower() && (!(playerWW.getRole() instanceof IPower) || !((IPower) playerWW.getRole()).hasPower())) {
                return game.translate(Prefix.RED.getKey() , "werewolf.check.power");
            }

            if (commandRegister.isRequiredAbilityEnabled() && !playerWW.getRole().isAbilityEnabled()) {
                return game.translate(Prefix.RED.getKey() , "werewolf.check.ability_disabled");
            }
        }

        if (!commandRegister.isStateWW(game.getState())) {
            return game.translate(Prefix.RED.getKey() , "werewolf.check.state");
        }

        if (!commandRegister.isArgNumbers(args.length)) {
            return game.translate(Prefix.RED.getKey() , "werewolf.check.parameters",
                    Formatter.number(commandRegister.getMinArgNumbers()));
        }

        commandRegister.getCommand().execute(game,player, args);

        return "";
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (!(sender instanceof Player)) return null;

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        WereWolfAPI game = main.getWereWolfAPI();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (args.length > 1) {
            return null;
        }

        return main.getRegisterManager().getCommandsRegister().stream()
                .filter(commandRegister -> (args[0].isEmpty() || game.translate(commandRegister.getKey()).contains(args[0])))
                .filter(commandRegister -> !commandRegister.isRoleOnly() || (playerWW != null && commandRegister.isRoleKey(playerWW.getRole().getKey())))
                .filter(CommandRegister::isAutoCompletion)
                .filter(commandRegister -> commandRegister.isStateWW(game.getState()))
                .filter(commandRegister -> !commandRegister.isRequiredPlayerInGame() || playerWW != null)
                .filter(commandRegister -> playerWW == null || commandRegister.isStateAccess(playerWW.getState()))
                .map(commandRegister -> game.translate(commandRegister.getKey()))
                .collect(Collectors.toList());
    }
}
