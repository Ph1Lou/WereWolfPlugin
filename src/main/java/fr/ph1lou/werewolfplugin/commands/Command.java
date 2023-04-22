package fr.ph1lou.werewolfplugin.commands;


import fr.ph1lou.werewolfapi.annotations.PlayerCommand;
import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.commands.ICommandRole;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfplugin.Register;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Command implements TabExecutor {

    private final Main main;
    private final Map<String, ICommand> commands = new HashMap<>();
    private final Map<String, ICommandRole> commandsRoles = new HashMap<>();


    public Command(Main main) {
        this.main = main;
        Register.get().getRoleCommandsRegister()
                .forEach(iCommandRoleRoleCommandWrapper -> this.commandsRoles.put(iCommandRoleRoleCommandWrapper.getMetaDatas().key(),
                        this.instantiate(iCommandRoleRoleCommandWrapper.getClazz())));
        Register.get().getPlayerCommandsRegister()
                .forEach(iCommandRoleRoleCommandWrapper -> this.commands.put(iCommandRoleRoleCommandWrapper.getMetaDatas().key(),
                        this.instantiate(iCommandRoleRoleCommandWrapper.getClazz())));
    }

    public <T> T instantiate(Class<T> clazz) {

        if (ICommand.class.isAssignableFrom(clazz)) {
            try {
                return clazz.getConstructor().newInstance();
            } catch (InstantiationException | NoSuchMethodException | IllegalAccessException |
                     InvocationTargetException e) {
                e.printStackTrace();
            }
        } else if (ICommandRole.class.isAssignableFrom(clazz)) {
            try {
                return clazz.getConstructor().newInstance();
            } catch (InstantiationException | NoSuchMethodException | IllegalAccessException |
                     InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command cmd, @NotNull String label, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();

        if (!(sender instanceof Player)) {
            sender.sendMessage(game.translate(Prefix.RED, "werewolf.check.console"));
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


        main.getRegisterManager().getRoleCommandsRegister()
                .stream()
                .filter(commandRegister1 -> game.translate(commandRegister1.getMetaDatas().key()).equalsIgnoreCase(commandName))
                .filter(iCommandRoleRoleCommandWrapper -> this.commandsRoles.containsKey(iCommandRoleRoleCommandWrapper.getMetaDatas().key()))
                .forEach(commandRegister1 -> messages.add(checkCommands(game, player, commandRegister1.getMetaDatas(),
                        this.commandsRoles.get(commandRegister1.getMetaDatas().key()),
                        args)));

        main.getRegisterManager().getPlayerCommandsRegister()
                .stream()
                .filter(commandRegister1 -> game.translate(commandRegister1.getMetaDatas().key()).equalsIgnoreCase(commandName))
                .filter(iCommandPlayerCommandWrapper -> this.commands.containsKey(iCommandPlayerCommandWrapper.getMetaDatas().key()))
                .forEach(commandRegister1 -> messages.add(checkCommand(game, player,
                        commandRegister1.getMetaDatas(),
                        this.commands.get(commandRegister1.getMetaDatas().key()), args)));

        if (messages.isEmpty()) {
            execute("h", player, new String[0]);
            return;
        }

        if (messages.stream().anyMatch(String::isEmpty)) {
            return;
        }

        player.sendMessage(messages.stream().filter(s -> !s.equals("ignored")).findFirst().orElse(""));

    }

    private String checkCommands(WereWolfAPI game, Player player, RoleCommand command, ICommandRole commandRole, String[] args) {

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) {
            return game.translate(Prefix.RED, "werewolf.check.not_in_game");
        }

        if (command.roleKeys().length > 0) {
            if (Arrays.stream(command.roleKeys()).noneMatch(s -> s.equals(playerWW.getRole().getKey()))) {
                return "ignored";
            }
        }

        if (command.statesPlayer().length > 0) {
            if (Arrays.stream(command.statesPlayer()).noneMatch(statePlayer -> statePlayer == playerWW.getState())) {
                return game.translate(Prefix.RED, "werewolf.check.state_player");
            }
        }

        if (command.requiredPower()) {
            if (!(playerWW.getRole() instanceof IPower) || !((IPower) playerWW.getRole()).hasPower()) {
                return game.translate(Prefix.RED, "werewolf.check.power");
            }
        }

        if (command.requiredAbilityEnabled()) {
            if (!playerWW.getRole().isAbilityEnabled()) {
                return game.translate(Prefix.RED, "werewolf.check.ability_disabled");
            }
        }

        if (command.statesGame().length > 0) {
            if (Arrays.stream(command.statesGame()).noneMatch(stateGame -> stateGame == game.getState())) {
                return game.translate(Prefix.RED, "werewolf.check.state");
            }
        }

        if (command.argNumbers().length > 0) {
            if (Arrays.stream(command.argNumbers()).noneMatch(argNumber -> argNumber == args.length)) {
                return game.translate(Prefix.RED, "werewolf.check.parameters",
                        Formatter.number(Arrays.stream(command.argNumbers()).min().orElse(0)));
            }
        }

        commandRole.execute(game, playerWW, args);
        return "";
    }

    private String checkCommand(WereWolfAPI game, Player player, PlayerCommand command, ICommand commandPlayer, String[] args) {

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);


        if (command.statesPlayer().length > 0) {
            if (playerWW == null) {
                return game.translate(Prefix.RED, "werewolf.check.not_in_game");
            }
            if (Arrays.stream(command.statesPlayer()).noneMatch(statePlayer -> statePlayer == playerWW.getState())) {
                return game.translate(Prefix.RED, "werewolf.check.state_player");
            }
        }

        if (command.statesGame().length > 0) {
            if (Arrays.stream(command.statesGame()).noneMatch(stateGame -> stateGame == game.getState())) {
                return game.translate(Prefix.RED, "werewolf.check.state");
            }
        }

        if (command.argNumbers().length > 0) {
            if (Arrays.stream(command.argNumbers()).noneMatch(argNumber -> argNumber == args.length)) {
                return game.translate(Prefix.RED, "werewolf.check.parameters",
                        Formatter.number(Arrays.stream(command.argNumbers()).min().orElse(0)));
            }
        }
        commandPlayer.execute(game, player, args);
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

        return Stream.concat(main.getRegisterManager().getRoleCommandsRegister().stream()
                                .filter(commandRegister -> (args[0].isEmpty() || game.translate(commandRegister.getMetaDatas().key()).startsWith(args[0])))
                                .filter(commandRegister -> commandRegister.getMetaDatas().roleKeys().length == 0 ||
                                        (playerWW != null && Arrays.stream(commandRegister.getMetaDatas().roleKeys()).anyMatch(roleKey -> roleKey.equals(playerWW.getRole().getKey()))))
                                .filter(iCommandCommandWrapper -> iCommandCommandWrapper.getMetaDatas().autoCompletion())
                                .filter(commandRegister -> commandRegister.getMetaDatas().statesGame().length == 0 ||
                                        Arrays.stream(commandRegister.getMetaDatas().statesGame()).anyMatch(stateGame -> stateGame == game.getState()))
                                .filter(commandRegister -> playerWW == null ||
                                        commandRegister.getMetaDatas().statesPlayer().length == 0 ||
                                        Arrays.stream(commandRegister.getMetaDatas().statesPlayer()).anyMatch(statePlayer -> statePlayer == playerWW.getState()))
                                .map(commandRegister -> game.translate(commandRegister.getMetaDatas().key())),
                        main.getRegisterManager().getPlayerCommandsRegister().stream()
                                .filter(commandRegister -> (args[0].isEmpty() || game.translate(commandRegister.getMetaDatas().key()).startsWith(args[0])))
                                .filter(iCommandCommandWrapper -> iCommandCommandWrapper.getMetaDatas().autoCompletion())
                                .filter(commandRegister -> commandRegister.getMetaDatas().statesGame().length == 0 ||
                                        Arrays.stream(commandRegister.getMetaDatas().statesGame()).anyMatch(stateGame -> stateGame == game.getState()))
                                .filter(commandRegister -> playerWW == null ||
                                        commandRegister.getMetaDatas().statesPlayer().length == 0 ||
                                        Arrays.stream(commandRegister.getMetaDatas().statesPlayer()).anyMatch(statePlayer -> statePlayer == playerWW.getState()))
                                .map(commandRegister -> game.translate(commandRegister.getMetaDatas().key())))
                .collect(Collectors.toList());
    }
}
