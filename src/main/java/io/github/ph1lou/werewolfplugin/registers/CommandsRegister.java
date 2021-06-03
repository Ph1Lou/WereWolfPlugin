package io.github.ph1lou.werewolfplugin.registers;

import io.github.ph1lou.werewolfapi.enums.RolesBase;
import io.github.ph1lou.werewolfapi.enums.StateGame;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.registers.CommandRegister;
import io.github.ph1lou.werewolfplugin.commands.roles.CommandAngelRegen;
import io.github.ph1lou.werewolfplugin.commands.roles.CommandCitizenCancelVote;
import io.github.ph1lou.werewolfplugin.commands.roles.CommandCitizenSeeVote;
import io.github.ph1lou.werewolfplugin.commands.roles.CommandComedian;
import io.github.ph1lou.werewolfplugin.commands.roles.CommandCupid;
import io.github.ph1lou.werewolfplugin.commands.roles.CommandDetective;
import io.github.ph1lou.werewolfplugin.commands.roles.CommandFallenAngel;
import io.github.ph1lou.werewolfplugin.commands.roles.CommandFox;
import io.github.ph1lou.werewolfplugin.commands.roles.CommandGuard;
import io.github.ph1lou.werewolfplugin.commands.roles.CommandGuardianAngel;
import io.github.ph1lou.werewolfplugin.commands.roles.CommandInfect;
import io.github.ph1lou.werewolfplugin.commands.roles.CommandLibrarian;
import io.github.ph1lou.werewolfplugin.commands.roles.CommandLovers;
import io.github.ph1lou.werewolfplugin.commands.roles.CommandOracle;
import io.github.ph1lou.werewolfplugin.commands.roles.CommandPriestess;
import io.github.ph1lou.werewolfplugin.commands.roles.CommandProtector;
import io.github.ph1lou.werewolfplugin.commands.roles.CommandRaven;
import io.github.ph1lou.werewolfplugin.commands.roles.CommandSeer;
import io.github.ph1lou.werewolfplugin.commands.roles.CommandSendToLibrarian;
import io.github.ph1lou.werewolfplugin.commands.roles.CommandShaman;
import io.github.ph1lou.werewolfplugin.commands.roles.CommandSisterSeeName;
import io.github.ph1lou.werewolfplugin.commands.roles.CommandSisterSeeRole;
import io.github.ph1lou.werewolfplugin.commands.roles.CommandSuccubus;
import io.github.ph1lou.werewolfplugin.commands.roles.CommandTrapper;
import io.github.ph1lou.werewolfplugin.commands.roles.CommandTroubleMaker;
import io.github.ph1lou.werewolfplugin.commands.roles.CommandWereWolf;
import io.github.ph1lou.werewolfplugin.commands.roles.CommandWereWolfChat;
import io.github.ph1lou.werewolfplugin.commands.roles.CommandWildChild;
import io.github.ph1lou.werewolfplugin.commands.roles.CommandWitch;
import io.github.ph1lou.werewolfplugin.commands.roles.CommandWolfDog;
import io.github.ph1lou.werewolfplugin.commands.utilities.CommandAnonymeChat;
import io.github.ph1lou.werewolfplugin.commands.utilities.CommandCompo;
import io.github.ph1lou.werewolfplugin.commands.utilities.CommandDoc;
import io.github.ph1lou.werewolfplugin.commands.utilities.CommandEnchantment;
import io.github.ph1lou.werewolfplugin.commands.utilities.CommandHelp;
import io.github.ph1lou.werewolfplugin.commands.utilities.CommandRandomEvents;
import io.github.ph1lou.werewolfplugin.commands.utilities.CommandRank;
import io.github.ph1lou.werewolfplugin.commands.utilities.CommandRole;
import io.github.ph1lou.werewolfplugin.commands.utilities.CommandRules;
import io.github.ph1lou.werewolfplugin.commands.utilities.CommandScenarios;
import io.github.ph1lou.werewolfplugin.commands.utilities.CommandTimer;
import io.github.ph1lou.werewolfplugin.commands.utilities.CommandVote;

import java.util.ArrayList;
import java.util.List;

public class CommandsRegister {

    public static List<CommandRegister> registerCommands(){
        List<CommandRegister> commandsRegister = new ArrayList<>();

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.ww_chat.name", new CommandWereWolfChat())
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.seer.command", new CommandSeer())
                        .addRoleKey(RolesBase.SEER.getKey())
                        .addRoleKey(RolesBase.CHATTY_SEER.getKey())
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .setRequiredPower()
                        .addArgNumbers(1));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.cupid.command", new CommandCupid())
                        .addRoleKey(RolesBase.CUPID.getKey())
                        .setRequiredPower()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(2));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.detective.command", new CommandDetective())
                        .addRoleKey(RolesBase.DETECTIVE.getKey())
                        .setRequiredPower()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(2));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.angel.command_2", new CommandFallenAngel())
                        .addRoleKey(RolesBase.ANGEL.getKey())
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .unsetAutoCompletion()
                        .addArgNumbers(0));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.angel.command_1", new CommandGuardianAngel())
                        .addRoleKey(RolesBase.ANGEL.getKey())
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .unsetAutoCompletion()
                        .addArgNumbers(0));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.infect_father_of_the_wolves.command", new CommandInfect())
                        .addRoleKey(RolesBase.INFECT.getKey())
                        .unsetAutoCompletion()
                        .setRequiredPower()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.fox.command", new CommandFox())
                        .addRoleKey(RolesBase.FOX.getKey())
                        .setRequiredPower()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.lover.command", new CommandLovers())
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1)
                        .addArgNumbers(2));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.protector.command", new CommandProtector())
                        .addRoleKey(RolesBase.PROTECTOR.getKey())
                        .setRequiredPower()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.sister.command_name", new CommandSisterSeeName())
                        .addRoleKey(RolesBase.SISTER.getKey())
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .unsetAutoCompletion()
                        .addArgNumbers(1));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.sister.command_role", new CommandSisterSeeRole())
                        .addRoleKey(RolesBase.SISTER.getKey())
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .unsetAutoCompletion()
                        .addArgNumbers(1));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.raven.command", new CommandRaven())
                        .addRoleKey(RolesBase.RAVEN.getKey())
                        .setRequiredPower()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.citizen.command_2", new CommandCitizenCancelVote())
                        .addRoleKey(RolesBase.CITIZEN.getKey())
                        .addStateAccess(StatePlayer.ALIVE)
                        .unsetAutoCompletion()
                        .setRequiredPower()
                        .addStateWW(StateGame.GAME).addArgNumbers(0));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.citizen.command_1", new CommandCitizenSeeVote())
                        .addRoleKey(RolesBase.CITIZEN.getKey())
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .unsetAutoCompletion()
                        .addArgNumbers(0));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.guard.command", new CommandGuard())
                        .addRoleKey(RolesBase.GUARD.getKey())
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .setRequiredPower()
                        .addArgNumbers(1));


        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.troublemaker.command", new CommandTroubleMaker())
                        .addRoleKey(RolesBase.TROUBLEMAKER.getKey())
                        .setRequiredPower()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.werewolf.command", new CommandWereWolf())
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(0));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.wild_child.command", new CommandWildChild())
                        .addRoleKey(RolesBase.WILD_CHILD.getKey())
                        .setRequiredPower()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.comedian.command", new CommandComedian())
                        .addRoleKey(RolesBase.COMEDIAN.getKey())

                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .setRequiredPower()
                        .addArgNumbers(1));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.witch.command", new CommandWitch())
                        .addRoleKey(RolesBase.WITCH.getKey())
                        .unsetAutoCompletion()
                        .setRequiredPower()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.trapper.command", new CommandTrapper())
                        .addRoleKey(RolesBase.TRAPPER.getKey())
                        .setRequiredPower()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.guardian_angel.command", new CommandAngelRegen())
                        .addRoleKey(RolesBase.ANGEL.getKey())
                        .addRoleKey(RolesBase.GUARDIAN_ANGEL.getKey())
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(0));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.wolf_dog.command", new CommandWolfDog())
                        .addRoleKey(RolesBase.WOLF_DOG.getKey())
                        .setRequiredPower()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(0));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.succubus.command", new CommandSuccubus())
                        .addRoleKey(RolesBase.SUCCUBUS.getKey())
                        .setRequiredPower()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.priestess.command", new CommandPriestess())
                        .addRoleKey(RolesBase.PRIESTESS.getKey())
                        .setRequiredPower()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.librarian.command", new CommandLibrarian())
                        .addRoleKey(RolesBase.LIBRARIAN.getKey())
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.librarian.request_command", new CommandSendToLibrarian())
                        .addStateAccess(StatePlayer.ALIVE)
                        .unsetAutoCompletion()
                        .addStateWW(StateGame.GAME));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.shaman.command", new CommandShaman())
                        .addRoleKey(RolesBase.SHAMAN.getKey())
                        .unsetAutoCompletion()
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(2));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.oracle.command", new CommandOracle())
                        .addRoleKey(RolesBase.ORACLE.getKey())
                        .setRequiredPower()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.menu.roles.command_1", new CommandRole())
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .setDescription("werewolf.menu.roles.description1")
                        .addArgNumbers(0));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.menu.rank.command", new CommandRank())
                        .addStateWW(StateGame.LOBBY)
                        .setDescription("werewolf.menu.rank.description")
                        .addArgNumbers(0));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.menu.global.command", new CommandRules())
                        .setDescription("werewolf.menu.global.description")
                        .addArgNumbers(0));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.menu.roles.command_2", new CommandCompo())
                        .setDescription("werewolf.menu.roles.description2")
                        .addArgNumbers(0));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.menu.scenarios.command", new CommandScenarios())
                        .setDescription("werewolf.menu.scenarios.description")
                        .addArgNumbers(0));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.menu.enchantments.command", new CommandEnchantment())
                        .setDescription("werewolf.menu.enchantments.description")
                        .addArgNumbers(0));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.menu.timers.command", new CommandTimer())
                        .setDescription("werewolf.menu.timers.description")
                        .addArgNumbers(0));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.menu.random_events.command", new CommandRandomEvents())
                        .setDescription("werewolf.menu.random_events.description")
                        .addArgNumbers(0));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.vote.command", new CommandVote())
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.help.command", new CommandHelp()));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.doc.command", new CommandDoc())
                        .setDescription("werewolf.commands.doc.description"));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.anonymous_chat.command", new CommandAnonymeChat())
                        .addStateWW(StateGame.GAME)
                        .addStateWW(StateGame.START)
                        .setDescription("werewolf.commands.admin.anonymous_chat.description")
                        .addStateAccess(StatePlayer.ALIVE));
        
        return commandsRegister;
    }
}
