package fr.ph1lou.werewolfplugin.commands;

import fr.ph1lou.werewolfapi.annotations.AdminCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.game.IModerationManager;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.utils.Wrapper;
import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfplugin.Register;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class Admin implements TabExecutor {

    private final Main main;
    private static Admin instance;
    private final Map<String ,ICommand> commands = new HashMap<>();

    public Admin(Main main) {
        this.main = main;
        instance = this;
        Register.get().getAdminCommandsRegister()
                .forEach(iCommandAdminCommandWrapper -> commands.put(iCommandAdminCommandWrapper.getMetaDatas().key(),
                        this.instantiate(iCommandAdminCommandWrapper.getClazz())));

    }

    public <T> T instantiate(Class<T> clazz){

        if(ICommand.class.isAssignableFrom(clazz)){
            try {
                return clazz.getConstructor().newInstance();
            } catch (InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
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

        WereWolfAPI game = main.getWereWolfAPI();
        AtomicBoolean found = new AtomicBoolean(false);

        main.getRegisterManager().getAdminCommandsRegister()
                .stream().filter(iCommandAdminCommandWrapper -> game.translate(iCommandAdminCommandWrapper
                        .getMetaDatas().key()).equalsIgnoreCase(commandName))
                .filter(iCommandAdminCommandWrapper -> accessCommand(iCommandAdminCommandWrapper.getMetaDatas(), player, args.length, true))
                .filter(iCommandAdminCommandWrapper -> this.commands.containsKey(iCommandAdminCommandWrapper.getMetaDatas().key()))
                .forEach(iCommandAdminCommandWrapper -> {
                    this.commands.get(iCommandAdminCommandWrapper.getMetaDatas().key()).execute(game, player, args);
                    found.set(true);
                });

        if (!found.get() && !commandName.equals("h")) {
            execute("h", player, new String[0]);
        }
    }


    public boolean accessCommand(AdminCommand adminCommand, Player player, int args, boolean seePermissionMessages) {

        WereWolfAPI game = main.getWereWolfAPI();

        if (adminCommand.statesGame().length > 0 &&
                Arrays.stream(adminCommand.statesGame())
                        .noneMatch(stateGame -> stateGame == game.getState())) {
            if (seePermissionMessages) {
                player.sendMessage(game.translate(Prefix.RED , "werewolf.check.state"));
            }
            return false;
        }

        if (adminCommand.argNumbers().length > 0 &&
            Arrays.stream(adminCommand.argNumbers()).noneMatch(value -> value == args)) {
            if (seePermissionMessages) {
                player.sendMessage(game.translate(Prefix.RED , "werewolf.check.parameters",
                        Formatter.number(Arrays.stream(adminCommand.argNumbers()).min().orElse(0))));
            }
            return false;
        }

        if (!checkPermission(adminCommand, player)) {
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
                return accessCommand(commandRegister.getMetaDatas(), player, Arrays.stream(commandRegister.getMetaDatas().argNumbers()).min().orElse(0), seePermissionMessages);
            }
        }
        return false;
    }


    private boolean checkPermission(AdminCommand adminCommand, Player player) {

        WereWolfAPI game = main.getWereWolfAPI();
        IModerationManager moderationManager = game.getModerationManager();
        UUID uuid = player.getUniqueId();

        boolean pass = adminCommand.hostAccess() && moderationManager.getHosts().contains(uuid);

        if (adminCommand.moderatorAccess() && moderationManager.getModerators().contains(uuid)) {
            pass = true;
        }

        if (player.hasPermission("a." + game.translate(adminCommand.key()))) {
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
                .map(Wrapper::getMetaDatas)
                .filter(commandRegister -> (args[0].isEmpty() || game.translate(commandRegister.key()).startsWith(args[0])))
                .filter(AdminCommand::autoCompletion)
                .filter(commandRegister -> checkPermission(commandRegister, player))
                .filter(commandRegister -> commandRegister.statesGame().length == 0 ||
                        Arrays.stream(commandRegister.statesGame()).anyMatch(stateGame -> stateGame == game.getState()))
                .map(commandRegister -> game.translate(commandRegister.key()))
                .collect(Collectors.toList());
    }

}
