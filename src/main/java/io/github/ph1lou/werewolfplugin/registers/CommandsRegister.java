package io.github.ph1lou.werewolfplugin.registers;

import io.github.ph1lou.werewolfapi.enums.RolesBase;
import io.github.ph1lou.werewolfapi.enums.StateGame;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.registers.CommandRegister;
import io.github.ph1lou.werewolfplugin.commands.roles.hybrid.wildchild.CommandWildChild;
import io.github.ph1lou.werewolfplugin.commands.roles.hybrid.wolfdog.CommandWolfDog;
import io.github.ph1lou.werewolfplugin.commands.roles.lovers.CommandLovers;
import io.github.ph1lou.werewolfplugin.commands.roles.neutral.angel.CommandAngelRegen;
import io.github.ph1lou.werewolfplugin.commands.roles.neutral.angel.CommandFallenAngel;
import io.github.ph1lou.werewolfplugin.commands.roles.neutral.angel.CommandGuardianAngel;
import io.github.ph1lou.werewolfplugin.commands.roles.neutral.charmer.CommandCharmer;
import io.github.ph1lou.werewolfplugin.commands.roles.neutral.succubus.CommandSuccubus;
import io.github.ph1lou.werewolfplugin.commands.roles.neutral.willothewisp.CommandWillOTheWisp;
import io.github.ph1lou.werewolfplugin.commands.roles.villager.citizen.CommandCitizenCancelVote;
import io.github.ph1lou.werewolfplugin.commands.roles.villager.citizen.CommandCitizenSeeVote;
import io.github.ph1lou.werewolfplugin.commands.roles.villager.comedian.CommandComedian;
import io.github.ph1lou.werewolfplugin.commands.roles.villager.cupid.CommandCupid;
import io.github.ph1lou.werewolfplugin.commands.roles.villager.guard.CommandGuard;
import io.github.ph1lou.werewolfplugin.commands.roles.villager.info.analyst.CommandAnalystAnalyse;
import io.github.ph1lou.werewolfplugin.commands.roles.villager.info.analyst.CommandAnalystSee;
import io.github.ph1lou.werewolfplugin.commands.roles.villager.info.detective.CommandDetective;
import io.github.ph1lou.werewolfplugin.commands.roles.villager.info.fox.CommandFox;
import io.github.ph1lou.werewolfplugin.commands.roles.villager.info.librarian.CommandLibrarian;
import io.github.ph1lou.werewolfplugin.commands.roles.villager.info.librarian.CommandSendToLibrarian;
import io.github.ph1lou.werewolfplugin.commands.roles.villager.info.oracle.CommandOracle;
import io.github.ph1lou.werewolfplugin.commands.roles.villager.info.seer.CommandSeer;
import io.github.ph1lou.werewolfplugin.commands.roles.villager.priestess.CommandPriestess;
import io.github.ph1lou.werewolfplugin.commands.roles.villager.protector.CommandProtector;
import io.github.ph1lou.werewolfplugin.commands.roles.villager.raven.CommandRaven;
import io.github.ph1lou.werewolfplugin.commands.roles.villager.shaman.CommandShaman;
import io.github.ph1lou.werewolfplugin.commands.roles.villager.sister.CommandSisterSeeName;
import io.github.ph1lou.werewolfplugin.commands.roles.villager.sister.CommandSisterSeeRole;
import io.github.ph1lou.werewolfplugin.commands.roles.villager.trapper.CommandTrapper;
import io.github.ph1lou.werewolfplugin.commands.roles.villager.troublemaker.CommandTroubleMaker;
import io.github.ph1lou.werewolfplugin.commands.roles.villager.witch.CommandWitch;
import io.github.ph1lou.werewolfplugin.commands.roles.werewolf.CommandWereWolf;
import io.github.ph1lou.werewolfplugin.commands.roles.werewolf.CommandWereWolfChat;
import io.github.ph1lou.werewolfplugin.commands.roles.werewolf.infect.CommandInfect;
import io.github.ph1lou.werewolfplugin.commands.utilities.CommandAnonymeChat;
import io.github.ph1lou.werewolfplugin.commands.utilities.CommandAura;
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
                        "werewolf.role.will_o_the_wisp.command", new CommandWillOTheWisp())
                        .addStateAccess(StatePlayer.ALIVE)
                        .setRequiredAbilityEnabled()
                        .addRoleKey(RolesBase.WILL_O_THE_WISP.getKey())
                        .addStateWW(StateGame.GAME));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.ww_chat.name", new CommandWereWolfChat())
                        .addStateAccess(StatePlayer.ALIVE)
                        .setRequiredAbilityEnabled()
                        .addStateWW(StateGame.GAME));


        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.seer.command", new CommandSeer())
                        .addRoleKey(RolesBase.SEER.getKey())
                        .addRoleKey(RolesBase.CHATTY_SEER.getKey())
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .setRequiredPower()
                        .setRequiredAbilityEnabled()
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
                        .setRequiredAbilityEnabled()
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
                        .setRequiredAbilityEnabled()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.fox.command", new CommandFox())
                        .addRoleKey(RolesBase.FOX.getKey())
                        .setRequiredPower()
                        .setRequiredAbilityEnabled()
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
                        .setRequiredAbilityEnabled()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.analyst.command_see", new CommandAnalystSee())
                        .addRoleKey(RolesBase.ANALYST.getKey())
                        .setRequiredPower()
                        .setRequiredAbilityEnabled()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.analyst.command_analyse", new CommandAnalystAnalyse())
                        .addRoleKey(RolesBase.ANALYST.getKey())
                        .setRequiredAbilityEnabled()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.sister.command_name", new CommandSisterSeeName())
                        .addRoleKey(RolesBase.SISTER.getKey())
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .setRequiredAbilityEnabled()
                        .unsetAutoCompletion()
                        .addArgNumbers(1));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.sister.command_role", new CommandSisterSeeRole())
                        .addRoleKey(RolesBase.SISTER.getKey())
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .setRequiredAbilityEnabled()
                        .unsetAutoCompletion()
                        .addArgNumbers(1));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.raven.command", new CommandRaven())
                        .addRoleKey(RolesBase.RAVEN.getKey())
                        .setRequiredPower()
                        .setRequiredAbilityEnabled()
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
                        .setRequiredAbilityEnabled()
                        .addStateWW(StateGame.GAME).addArgNumbers(0));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.citizen.command_1", new CommandCitizenSeeVote())
                        .addRoleKey(RolesBase.CITIZEN.getKey())
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .unsetAutoCompletion()
                        .setRequiredAbilityEnabled()
                        .addArgNumbers(0));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.guard.command", new CommandGuard())
                        .addRoleKey(RolesBase.GUARD.getKey())
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .setRequiredPower()
                        .setRequiredAbilityEnabled()
                        .addArgNumbers(1));


        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.troublemaker.command", new CommandTroubleMaker())
                        .addRoleKey(RolesBase.TROUBLEMAKER.getKey())
                        .setRequiredPower()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .setRequiredAbilityEnabled()
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
                        .setRequiredAbilityEnabled()
                        .addArgNumbers(1));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.witch.command", new CommandWitch())
                        .addRoleKey(RolesBase.WITCH.getKey())
                        .unsetAutoCompletion()
                        .setRequiredPower()
                        .setRequiredAbilityEnabled()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.trapper.command", new CommandTrapper())
                        .addRoleKey(RolesBase.TRAPPER.getKey())
                        .setRequiredPower()
                        .setRequiredAbilityEnabled()
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
                        .setRequiredAbilityEnabled()
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
                        .setRequiredAbilityEnabled()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.priestess.command", new CommandPriestess())
                        .addRoleKey(RolesBase.PRIESTESS.getKey())
                        .setRequiredPower()
                        .setRequiredAbilityEnabled()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.librarian.command", new CommandLibrarian())
                        .addRoleKey(RolesBase.LIBRARIAN.getKey())
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .setRequiredAbilityEnabled()
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
                        .setRequiredAbilityEnabled()
                        .addArgNumbers(2));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.oracle.command", new CommandOracle())
                        .addRoleKey(RolesBase.ORACLE.getKey())
                        .setRequiredPower()
                        .setRequiredAbilityEnabled()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1));

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.charmer.command", new CommandCharmer())
                        .addRoleKey(RolesBase.CHARMER.getKey())
                        .setRequiredPower()
                        .setRequiredAbilityEnabled()
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
                        "werewolf.commands.aura.command", new CommandAura())
                        .setDescription("werewolf.menu.aura.description")
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
