package fr.ph1lou.werewolfplugin.registers;

import fr.ph1lou.werewolfplugin.commands.admin.ingame.CommandAdminHelp;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.registers.impl.CommandRegister;
import fr.ph1lou.werewolfplugin.commands.admin.CommandChange;
import fr.ph1lou.werewolfplugin.commands.admin.CommandGeneration;
import fr.ph1lou.werewolfplugin.commands.admin.CommandSize;
import fr.ph1lou.werewolfplugin.commands.admin.CommandStop;
import fr.ph1lou.werewolfplugin.commands.admin.ingame.CommandAdminRole;
import fr.ph1lou.werewolfplugin.commands.admin.ingame.CommandChat;
import fr.ph1lou.werewolfplugin.commands.admin.ingame.CommandConfig;
import fr.ph1lou.werewolfplugin.commands.admin.ingame.CommandDisconnected;
import fr.ph1lou.werewolfplugin.commands.admin.ingame.CommandFinalHeal;
import fr.ph1lou.werewolfplugin.commands.admin.ingame.CommandGamemode;
import fr.ph1lou.werewolfplugin.commands.admin.ingame.CommandGroup;
import fr.ph1lou.werewolfplugin.commands.admin.ingame.CommandHost;
import fr.ph1lou.werewolfplugin.commands.admin.ingame.CommandInfo;
import fr.ph1lou.werewolfplugin.commands.admin.ingame.CommandInventory;
import fr.ph1lou.werewolfplugin.commands.admin.ingame.CommandKill;
import fr.ph1lou.werewolfplugin.commands.admin.ingame.CommandLate;
import fr.ph1lou.werewolfplugin.commands.admin.ingame.CommandLootDeath;
import fr.ph1lou.werewolfplugin.commands.admin.ingame.CommandLootStart;
import fr.ph1lou.werewolfplugin.commands.admin.ingame.CommandModerator;
import fr.ph1lou.werewolfplugin.commands.admin.ingame.CommandName;
import fr.ph1lou.werewolfplugin.commands.admin.ingame.CommandPreview;
import fr.ph1lou.werewolfplugin.commands.admin.ingame.CommandRevive;
import fr.ph1lou.werewolfplugin.commands.admin.ingame.CommandSetGroup;
import fr.ph1lou.werewolfplugin.commands.admin.ingame.CommandStart;
import fr.ph1lou.werewolfplugin.commands.admin.ingame.CommandStuffRole;
import fr.ph1lou.werewolfplugin.commands.admin.ingame.CommandTP;
import fr.ph1lou.werewolfplugin.commands.admin.ingame.CommandTPGroup;
import fr.ph1lou.werewolfplugin.commands.admin.ingame.CommandWhitelist;

import java.util.ArrayList;
import java.util.List;

public class AdminCommandsRegister {

    public static List<CommandRegister> registerAdminCommands(){

        List<CommandRegister> adminCommandsRegister = new ArrayList<>();

        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.set_game_name.command", new CommandName())
                        .setDescription("werewolf.commands.admin.set_game_name.description")
                        .setHostAccess());

        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.start.command", new CommandStart())
                        .setHostAccess()
                        .addStateWW(StateGame.LOBBY)
                        .setDescription("werewolf.commands.admin.start.description")
                        .addArgNumbers(0));

        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.chat.command", new CommandChat())
                        .setHostAccess()
                        .setModeratorAccess()
                        .setDescription("werewolf.commands.admin.chat.description")
                        .addArgNumbers(0));

        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.info.command", new CommandInfo())
                        .setHostAccess()
                        .setDescription("werewolf.commands.admin.info.description")
                        .setModeratorAccess());

        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.generation.command", new CommandGeneration())
                        .setHostAccess().setModeratorAccess()
                        .addStateWW(StateGame.LOBBY)
                        .setDescription("werewolf.commands.admin.generation.description")
                        .addArgNumbers(0));

        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.group.command_2", new CommandSetGroup())
                        .setHostAccess()
                        .setModeratorAccess()
                        .setDescription("werewolf.commands.admin.group.description2")
                        .addArgNumbers(1));

        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.group.command", new CommandGroup())
                        .setHostAccess()
                        .setModeratorAccess()
                        .addStateWW(StateGame.GAME)
                        .setDescription("werewolf.commands.admin.group.description1")
                        .addArgNumbers(0));

        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.menu.command", new CommandConfig())
                        .setHostAccess()
                        .setDescription("werewolf.menu.description")
                        .setModeratorAccess()
                        .addArgNumbers(0));

        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.kill.command", new CommandKill())
                        .setHostAccess()
                        .setModeratorAccess()
                        .addStateWW(StateGame.GAME)
                        .addStateWW(StateGame.START)
                        .setDescription("werewolf.commands.kill.description")
                        .addArgNumbers(1));

        adminCommandsRegister.
                add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.disconnected.command", new CommandDisconnected())
                        .setHostAccess()
                        .setModeratorAccess()
                        .addStateWW(StateGame.START)
                        .addStateWW(StateGame.GAME)
                        .setDescription("werewolf.commands.admin.disconnected.description")
                        .addArgNumbers(0));

        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.inventory.command", new CommandInventory())
                        .setHostAccess()
                        .setModeratorAccess()
                        .setDescription("werewolf.commands.inventory.description")
                        .addArgNumbers(1));

        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.tp_group.command", new CommandTPGroup())
                        .setHostAccess()
                        .setModeratorAccess()
                        .addArgNumbers(1)
                        .addArgNumbers(2)
                        .setDescription("werewolf.commands.admin.tp_group.description")
                        .addStateWW(StateGame.GAME));

        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.role.command", new CommandAdminRole())
                        .setHostAccess()
                        .setModeratorAccess()
                        .addStateWW(StateGame.GAME)
                        .addStateWW(StateGame.END)
                        .addArgNumbers(0)
                        .setDescription("werewolf.commands.admin.role.description")
                        .addArgNumbers(1));

        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.revive.command", new CommandRevive())
                        .setHostAccess()
                        .addStateWW(StateGame.GAME)
                        .setDescription("werewolf.commands.admin.revive.description")
                        .addArgNumbers(1));

        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.final_heal.command", new CommandFinalHeal())
                        .setHostAccess()
                        .setModeratorAccess()
                        .addStateWW(StateGame.GAME)
                        .addStateWW(StateGame.START)
                        .setDescription("werewolf.commands.admin.final_heal.description")
                        .addArgNumbers(0));

        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.stuff_start.command", new CommandLootStart())
                        .setHostAccess()
                        .setModeratorAccess()
                        .unsetAutoCompletion()
                        .addStateWW(StateGame.LOBBY)
                        .addArgNumbers(0));

        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.loot_death.command", new CommandLootDeath())
                        .setHostAccess()
                        .setModeratorAccess()
                        .unsetAutoCompletion()
                        .addArgNumbers(0));

        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.loot_role.command", new CommandStuffRole())
                        .setHostAccess()
                        .setModeratorAccess()
                        .unsetAutoCompletion()
                        .addArgNumbers(1)
                        .addStateWW(StateGame.LOBBY)
                        .addStateWW(StateGame.START)
                        .addStateWW(StateGame.TRANSPORTATION));

        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.late.command", new CommandLate())
                        .setHostAccess()
                        .setModeratorAccess()
                        .addArgNumbers(1)
                        .addStateWW(StateGame.START)
                        .setDescription("werewolf.commands.late.description")
                        .addStateWW(StateGame.TRANSPORTATION));

        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.help.command", new CommandAdminHelp())
                        .setHostAccess()
                        .setModeratorAccess());

        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.stop.command", new CommandStop())
                        .setHostAccess()
                        .addArgNumbers(0)
                        .addStateWW(StateGame.GAME)
                        .addStateWW(StateGame.START)
                        .addStateWW(StateGame.TRANSPORTATION)
                        .setDescription("werewolf.commands.admin.stop.description")
                        .addStateWW(StateGame.END));

        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.whitelist.command", new CommandWhitelist())
                        .setHostAccess()
                        .setModeratorAccess()
                        .addStateWW(StateGame.LOBBY)
                        .setDescription("werewolf.commands.admin.whitelist.description")
                        .addArgNumbers(1));

        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.moderator.command", new CommandModerator())
                        .setHostAccess()
                        .setDescription("werewolf.commands.admin.moderator.description")
                        .addArgNumbers(1));

        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.host.command", new CommandHost())
                        .setHostAccess()
                        .setDescription("werewolf.commands.admin.host.description")
                        .addArgNumbers(1));

        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.gamemode.command", new CommandGamemode())
                        .setHostAccess()
                        .setModeratorAccess()
                        .setDescription("werewolf.commands.admin.gamemode.description")
                        .addArgNumbers(1));

        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.teleportation.command", new CommandTP())
                        .setHostAccess()
                        .setModeratorAccess()
                        .addArgNumbers(1)
                        .setDescription("werewolf.commands.admin.teleportation.description")
                        .addArgNumbers(2));

        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.size.command", new CommandSize())
                        .setHostAccess()
                        .setModeratorAccess()
                        .addStateWW(StateGame.LOBBY)
                        .setDescription("werewolf.commands.admin.size.description")
                        .addArgNumbers(0));

        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.change.command", new CommandChange())
                        .setHostAccess()
                        .addStateWW(StateGame.LOBBY)
                        .unsetAutoCompletion()
                        .setDescription("werewolf.commands.admin.change.description")
                        .addArgNumbers(0));

        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.preview.command", new CommandPreview())
                        .setHostAccess()
                        .setModeratorAccess()
                        .addStateWW(StateGame.LOBBY)
                        .setDescription("werewolf.commands.admin.preview.description")
                        .addArgNumbers(0));

        return adminCommandsRegister;

    }
}
