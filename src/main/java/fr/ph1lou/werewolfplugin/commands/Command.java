package fr.ph1lou.werewolfplugin.commands;


import fr.ph1lou.werewolfapi.annotations.PlayerCommand;
import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.commands.ICommandRole;
import fr.ph1lou.werewolfapi.utils.Wrapper;
import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
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
import java.util.stream.Stream;


public class Command implements TabExecutor {

    private final Main main;

    public Command(Main main) {
        this.main = main;
    }



    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command cmd, @NotNull String label, String[] args) {

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

        WereWolfAPI game = main.getWereWolfAPI();
        List<String> messages = new ArrayList<>();

        main.getRegisterManager().getRoleCommandsRegister()
                .stream()
                .filter(commandRegister1 -> game.translate(commandRegister1.getMetaDatas().key()).equalsIgnoreCase(commandName))
                .forEach(commandRegister1 -> messages.add(checkCommands(game, player, commandRegister1, args)));

        main.getRegisterManager().getPlayerCommandsRegister()
                .stream()
                .filter(commandRegister1 -> game.translate(commandRegister1.getMetaDatas().key()).equalsIgnoreCase(commandName))
                .forEach(commandRegister1 -> messages.add(checkCommand(game, player, commandRegister1, args)));

        if (messages.isEmpty()) {
            execute("h", player, new String[0]);
            return;
        }

        if (messages.stream().anyMatch(String::isEmpty)) {
            return;
        }

        player.sendMessage(messages.stream().filter(s -> !s.equals("ignored")).findFirst().orElse(""));

    }

    private String checkCommands(WereWolfAPI game, Player player, Wrapper<ICommandRole, RoleCommand> wrapper, String[] args) {

        UUID uuid = player.getUniqueId();
        RoleCommand command = wrapper.getMetaDatas();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) {
            return game.translate(Prefix.RED , "werewolf.check.not_in_game");
        }

        if (command.roleKeys().length > 0) {
            if (Arrays.stream(command.roleKeys()).noneMatch(s -> s.equals(playerWW.getRole().getKey()))) {
                return "ignored";
            }
        }

        if (command.statesPlayer().length > 0) {
            if(Arrays.stream(command.statesPlayer()).noneMatch(statePlayer -> statePlayer == playerWW.getState())){
                return game.translate(Prefix.RED , "werewolf.check.state_player");
            }
        }

        if (command.requiredPower()) {
            if(!(playerWW.getRole() instanceof IPower) || !((IPower) playerWW.getRole()).hasPower()){
                return game.translate(Prefix.RED , "werewolf.check.power");
            }
        }

        if(command.requiredAbilityEnabled()){
            if (!playerWW.getRole().isAbilityEnabled()) {
                return game.translate(Prefix.RED , "werewolf.check.ability_disabled");
            }
        }

        if(command.statesGame().length > 0){
            if (Arrays.stream(command.statesGame()).noneMatch(stateGame -> stateGame == game.getState())) {
                return game.translate(Prefix.RED , "werewolf.check.state");
            }
        }

        if(command.argNumbers().length > 0){
            if (Arrays.stream(command.argNumbers()).noneMatch(argNumber -> argNumber == args.length)) {
                return game.translate(Prefix.RED , "werewolf.check.parameters",
                        Formatter.number(Arrays.stream(command.argNumbers()).min().orElse(0)));
            }
        }
        wrapper.getObject().ifPresent(iCommand -> iCommand.execute(game,playerWW, args));
        return "";
    }

    private String checkCommand(WereWolfAPI game, Player player, Wrapper<ICommand, PlayerCommand> wrapper, String[] args) {

        UUID uuid = player.getUniqueId();
        PlayerCommand command = wrapper.getMetaDatas();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);


        if (command.statesPlayer().length > 0) {
            if (playerWW == null) {
                return game.translate(Prefix.RED , "werewolf.check.not_in_game");
            }
            if(Arrays.stream(command.statesPlayer()).noneMatch(statePlayer -> statePlayer == playerWW.getState())){
                return game.translate(Prefix.RED , "werewolf.check.state_player");
            }
        }

        if(command.statesGame().length > 0){
            if (Arrays.stream(command.statesGame()).noneMatch(stateGame -> stateGame == game.getState())) {
                return game.translate(Prefix.RED , "werewolf.check.state");
            }
        }

        if(command.argNumbers().length > 0){
            if (Arrays.stream(command.argNumbers()).noneMatch(argNumber -> argNumber == args.length)) {
                return game.translate(Prefix.RED , "werewolf.check.parameters",
                        Formatter.number(Arrays.stream(command.argNumbers()).min().orElse(0)));
            }
        }
        wrapper.getObject().ifPresent(iCommand -> iCommand.execute(game,player, args));
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
                .filter(commandRegister -> (args[0].isEmpty() || game.translate(commandRegister.getMetaDatas().key()).contains(args[0])))
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
                        .filter(commandRegister -> (args[0].isEmpty() || game.translate(commandRegister.getMetaDatas().key()).contains(args[0])))
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
