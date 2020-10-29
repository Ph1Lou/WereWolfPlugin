package io.github.ph1lou.werewolfplugin.commands;

import io.github.ph1lou.werewolfapi.CommandRegister;
import io.github.ph1lou.werewolfapi.enumlg.StateLG;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.commands.admin.CommandChange;
import io.github.ph1lou.werewolfplugin.commands.admin.CommandGeneration;
import io.github.ph1lou.werewolfplugin.commands.admin.CommandSize;
import io.github.ph1lou.werewolfplugin.commands.admin.CommandStop;
import io.github.ph1lou.werewolfplugin.commands.admin.ingame.*;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import io.github.ph1lou.werewolfplugin.game.ModerationManager;
import org.bukkit.command.Command;
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

public class Admin implements TabExecutor {

    private final List<CommandRegister> listAdminCommands = new ArrayList<>();
    private final Main main;

    public Admin(Main main) {

        this.main = main;

        listAdminCommands.add(new CommandRegister().registerCommand(new CommandName(main)).setName("name").setHostAccess());

        listAdminCommands.add(new CommandRegister().registerCommand(new CommandStart(main)).setName("start").setHostAccess().addStateWW(StateLG.LOBBY).addArgNumbers(0));
        listAdminCommands.add(new CommandRegister().registerCommand(new CommandChat(main)).setName("chat").setHostAccess().setModeratorAccess().addArgNumbers(0));
        listAdminCommands.add(new CommandRegister().registerCommand(new CommandInfo(main)).setName("info").setHostAccess().setModeratorAccess());
        listAdminCommands.add(new CommandRegister().registerCommand(new CommandGeneration(main)).setName("generation").setHostAccess().setModeratorAccess().addStateWW(StateLG.LOBBY).addArgNumbers(0));
        listAdminCommands.add(new CommandRegister().registerCommand(new CommandSetGroup(main)).setName("setgroup").setHostAccess().setModeratorAccess().addArgNumbers(1));
        listAdminCommands.add(new CommandRegister().registerCommand(new CommandGroup(main)).setName("group").setHostAccess().setModeratorAccess().addStateWW(StateLG.GAME).addArgNumbers(0));
        listAdminCommands.add(new CommandRegister().registerCommand(new CommandConfig()).setName("config").setHostAccess().setModeratorAccess().addArgNumbers(0));
        listAdminCommands.add(new CommandRegister().registerCommand(new CommandKill(main)).setName("kill").setHostAccess().setModeratorAccess().addStateWW(StateLG.GAME).addStateWW(StateLG.START).addArgNumbers(1));
        listAdminCommands.add(new CommandRegister().registerCommand(new CommandDisconnected(main)).setName("disc").setHostAccess().setModeratorAccess().addStateWW(StateLG.START).addStateWW(StateLG.GAME).addArgNumbers(0));
        listAdminCommands.add(new CommandRegister().registerCommand(new CommandInventory(main)).setName("inv").setHostAccess().setModeratorAccess().addArgNumbers(1));
        listAdminCommands.add(new CommandRegister().registerCommand(new CommandTPGroup(main)).setName("tpgroup").setHostAccess().setModeratorAccess().addArgNumbers(1).addArgNumbers(2).addStateWW(StateLG.GAME));
        listAdminCommands.add(new CommandRegister().registerCommand(new CommandRole(main)).setName("role").setHostAccess().setModeratorAccess().addStateWW(StateLG.GAME).addStateWW(StateLG.END).addArgNumbers(0).addArgNumbers(1));
        listAdminCommands.add(new CommandRegister().registerCommand(new CommandRevive(main)).setName("revive").setHostAccess().addStateWW(StateLG.GAME).addArgNumbers(1));
        listAdminCommands.add(new CommandRegister().registerCommand(new CommandFinalHeal(main)).setName("fh").setHostAccess().setModeratorAccess().addStateWW(StateLG.GAME).addStateWW(StateLG.START).addArgNumbers(0));
        listAdminCommands.add(new CommandRegister().registerCommand(new CommandLootStart(main)).setName("lootStart").setHostAccess().setModeratorAccess().unsetAutoCompletion().addStateWW(StateLG.LOBBY).addArgNumbers(1));
        listAdminCommands.add(new CommandRegister().registerCommand(new CommandLootDeath(main)).setName("lootDeath").setHostAccess().setModeratorAccess().unsetAutoCompletion().addArgNumbers(1));
        listAdminCommands.add(new CommandRegister().registerCommand(new CommandStuffRole(main)).setName("stuffRole").setHostAccess().setModeratorAccess().unsetAutoCompletion().addArgNumbers(1).addStateWW(StateLG.LOBBY).addStateWW(StateLG.START));
        listAdminCommands.add(new CommandRegister().registerCommand(new CommandAdminHelp(main)).setName("h").setHostAccess().setModeratorAccess());
        listAdminCommands.add(new CommandRegister().registerCommand(new CommandStop(main)).setName("stop").setHostAccess().addArgNumbers(0).addStateWW(StateLG.GAME).addStateWW(StateLG.END));
        listAdminCommands.add(new CommandRegister().registerCommand(new CommandWhitelist(main)).setName("whitelist").setHostAccess().setModeratorAccess().addStateWW(StateLG.LOBBY).addArgNumbers(1));
        listAdminCommands.add(new CommandRegister().registerCommand(new CommandModerator(main)).setName("moderator").setHostAccess().addArgNumbers(1));
        listAdminCommands.add(new CommandRegister().registerCommand(new CommandHost(main)).setName("host").setHostAccess().addArgNumbers(1));
        listAdminCommands.add(new CommandRegister().registerCommand(new CommandGamemode(main)).setName("gamemode").setHostAccess().setModeratorAccess().addArgNumbers(1));
        listAdminCommands.add(new CommandRegister().registerCommand(new CommandTP(main)).setName("tp").setHostAccess().setModeratorAccess().addArgNumbers(1).addArgNumbers(2));
        listAdminCommands.add(new CommandRegister().registerCommand(new CommandSize(main)).setName("size").setHostAccess().setModeratorAccess().addStateWW(StateLG.LOBBY).addArgNumbers(0));
        listAdminCommands.add(new CommandRegister().registerCommand(new CommandChange(main)).setName("change").setHostAccess().addStateWW(StateLG.LOBBY).addArgNumbers(0));
        listAdminCommands.add(new CommandRegister().registerCommand(new CommandPreview(main)).setName("preview").setHostAccess().setModeratorAccess().addStateWW(StateLG.LOBBY).addArgNumbers(0));

        registerExternCommand();
    }


    private void registerExternCommand() {
        for (CommandRegister commandRegister : main.getListAdminCommands()) {

            listAdminCommands.removeAll(listAdminCommands.stream()
                    .filter(commandRegister1 -> commandRegister1.getName().equalsIgnoreCase(commandRegister.getName()))
                    .collect(Collectors.toList()));
            listAdminCommands.add(commandRegister);
        }
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {

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

        for (CommandRegister commandRegister1 : this.listAdminCommands) {
            if (commandRegister1.getName().equalsIgnoreCase(commandName)) {
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
            player.sendMessage(game.translate("werewolf.check.game_not_in_progress"));
            return;
        }

        if (!commandRegister.isArgNumbers(args.length)) {
            player.sendMessage(game.translate("werewolf.check.parameters", commandRegister.getMinArgNumbers()));
            return;
        }

        commandRegister.getCommand().execute(player, args);
    }

    private boolean checkPermission(CommandRegister commandRegister, Player player) {

        GameManager game = main.getCurrentGame();
        ModerationManager moderationManager = game.getModerationManager();
        UUID uuid = player.getUniqueId();

        boolean pass = false;

        if (commandRegister.isHostAccess() && moderationManager.getHosts().contains(uuid)) {
            pass = true;
        }

        if (commandRegister.isModeratorAccess() && moderationManager.getModerators().contains(uuid)) {
            pass = true;
        }

        if (player.hasPermission(commandRegister.getPermission())) {
            pass = true;
        }

        return pass;
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (!(sender instanceof Player)) return null;

        Player player = (Player) sender;

        if (args.length > 1) {
            return null;
        }

        return listAdminCommands.stream()
                .filter(commandRegister -> (args[0].isEmpty() || commandRegister.getName().contains(args[0])))
                .filter(CommandRegister::isAutoCompletion)
                .filter(commandRegister -> checkPermission(commandRegister, player))
                .map(CommandRegister::getName)
                .collect(Collectors.toList());
    }

}
