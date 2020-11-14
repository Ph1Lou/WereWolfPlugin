package io.github.ph1lou.werewolfplugin;

import io.github.ph1lou.werewolfapi.*;
import io.github.ph1lou.werewolfapi.enumlg.*;
import io.github.ph1lou.werewolfplugin.commands.admin.CommandChange;
import io.github.ph1lou.werewolfplugin.commands.admin.CommandGeneration;
import io.github.ph1lou.werewolfplugin.commands.admin.CommandSize;
import io.github.ph1lou.werewolfplugin.commands.admin.CommandStop;
import io.github.ph1lou.werewolfplugin.commands.admin.ingame.*;
import io.github.ph1lou.werewolfplugin.commands.roles.*;
import io.github.ph1lou.werewolfplugin.commands.utilities.*;
import io.github.ph1lou.werewolfplugin.listeners.configs.CompassMiddle;
import io.github.ph1lou.werewolfplugin.listeners.configs.RedNameTag;
import io.github.ph1lou.werewolfplugin.listeners.configs.SeerEvent;
import io.github.ph1lou.werewolfplugin.listeners.configs.ShowDeathRole;
import io.github.ph1lou.werewolfplugin.listeners.scenarios.*;
import io.github.ph1lou.werewolfplugin.roles.neutrals.*;
import io.github.ph1lou.werewolfplugin.roles.villagers.*;
import io.github.ph1lou.werewolfplugin.roles.werewolfs.*;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Register implements RegisterManager {

    private final List<RoleRegister> rolesRegister = new ArrayList<>();
    private final List<ScenarioRegister> scenariosRegister = new ArrayList<>();
    private final List<ConfigRegister> configsRegister = new ArrayList<>();
    private final List<TimerRegister> timersRegister = new ArrayList<>();
    private final List<CommandRegister> commandsRegister = new ArrayList<>();
    private final List<CommandRegister> adminCommandsRegister = new ArrayList<>();
    private final List<AddonRegister> addonsRegister = new ArrayList<>();

    public Register(Main main) {
        registerRoles();
        registerScenarios(main);
        registerTimers();
        registerConfigs(main);
        registerCommands(main);
        registerAdminCommands(main);
    }

    private void registerAdminCommands(Main main) {

        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.set_game_name.command", new CommandName(main))
                        .setHostAccess());

        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.start.command", new CommandStart(main))
                        .setHostAccess()
                        .addStateWW(StateGame.LOBBY)
                        .addArgNumbers(0));
        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.chat.command", new CommandChat(main))
                        .setHostAccess()
                        .setModeratorAccess()
                        .addArgNumbers(0));
        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.info.command", new CommandInfo(main))
                        .setHostAccess()
                        .setModeratorAccess());
        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.generation.command", new CommandGeneration(main))
                        .setHostAccess().setModeratorAccess()
                        .addStateWW(StateGame.LOBBY)
                        .addArgNumbers(0));
        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.group.command_2", new CommandSetGroup(main))
                        .setHostAccess()
                        .setModeratorAccess()
                        .addArgNumbers(1));
        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.group.command", new CommandGroup(main))
                        .setHostAccess()
                        .setModeratorAccess()
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(0));
        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.menu.command", new CommandConfig())
                        .setHostAccess()
                        .setModeratorAccess()
                        .addArgNumbers(0));
        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.kill.command", new CommandKill(main))
                        .setHostAccess()
                        .setModeratorAccess()
                        .addStateWW(StateGame.GAME)
                        .addStateWW(StateGame.START)
                        .addArgNumbers(1));
        adminCommandsRegister.
                add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.disconnected.command", new CommandDisconnected(main))
                        .setHostAccess()
                        .setModeratorAccess()
                        .addStateWW(StateGame.START)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(0));
        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.inventory.command", new CommandInventory(main))
                        .setHostAccess()
                        .setModeratorAccess()
                        .addArgNumbers(1));
        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.tp_group.command", new CommandTPGroup(main))
                        .setHostAccess()
                        .setModeratorAccess()
                        .addArgNumbers(1)
                        .addArgNumbers(2)
                        .addStateWW(StateGame.GAME));
        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.role.command", new CommandAdminRole(main))
                        .setHostAccess()
                        .setModeratorAccess()
                        .addStateWW(StateGame.GAME)
                        .addStateWW(StateGame.END)
                        .addArgNumbers(0)
                        .addArgNumbers(1));
        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.revive.command", new CommandRevive(main))
                        .setHostAccess()
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1));
        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.final_heal.command", new CommandFinalHeal(main))
                        .setHostAccess()
                        .setModeratorAccess()
                        .addStateWW(StateGame.GAME)
                        .addStateWW(StateGame.START)
                        .addArgNumbers(0));
        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.stuff_start.command", new CommandLootStart(main))
                        .setHostAccess()
                        .setModeratorAccess()
                        .unsetAutoCompletion()
                        .addStateWW(StateGame.LOBBY)
                        .addArgNumbers(0));
        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.loot_death.command", new CommandLootDeath(main))
                        .setHostAccess()
                        .setModeratorAccess()
                        .unsetAutoCompletion()
                        .addArgNumbers(0));
        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.loot_role.command", new CommandStuffRole(main))
                        .setHostAccess()
                        .setModeratorAccess()
                        .unsetAutoCompletion()
                        .addArgNumbers(1)
                        .addStateWW(StateGame.LOBBY)
                        .addStateWW(StateGame.GAME)
                        .addStateWW(StateGame.TRANSPORTATION));
        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.help.command", new CommandAdminHelp(main))
                        .setHostAccess()
                        .setModeratorAccess());
        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.stop.command", new CommandStop(main))
                        .setHostAccess()
                        .addArgNumbers(0)
                        .addStateWW(StateGame.GAME)
                        .addStateWW(StateGame.START)
                        .addStateWW(StateGame.TRANSPORTATION)
                        .addStateWW(StateGame.END));
        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.whitelist.command", new CommandWhitelist(main))
                        .setHostAccess()
                        .setModeratorAccess()
                        .addStateWW(StateGame.LOBBY)
                        .addArgNumbers(1));
        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.moderator.command", new CommandModerator(main))
                        .setHostAccess()
                        .addArgNumbers(1));
        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.host.command", new CommandHost(main))
                        .setHostAccess()
                        .addArgNumbers(1));
        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.gamemode.command", new CommandGamemode(main))
                        .setHostAccess()
                        .setModeratorAccess()
                        .addArgNumbers(1));
        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.teleportation.command", new CommandTP(main))
                        .setHostAccess()
                        .setModeratorAccess()
                        .addArgNumbers(1)
                        .addArgNumbers(2));
        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.size.command", new CommandSize(main))
                        .setHostAccess()
                        .setModeratorAccess()
                        .addStateWW(StateGame.LOBBY)
                        .addArgNumbers(0));
        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.change.command", new CommandChange(main))
                        .setHostAccess()
                        .addStateWW(StateGame.LOBBY)
                        .unsetAutoCompletion()
                        .addArgNumbers(0));
        adminCommandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.preview.command", new CommandPreview(main))
                        .setHostAccess()
                        .setModeratorAccess()
                        .addStateWW(StateGame.LOBBY)
                        .addArgNumbers(0));
    }

    private void registerCommands(Main main) {

        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.seer.command", new CommandSeer(main))
                        .addRoleKey(RolesBase.SEER.getKey())
                        .addRoleKey(RolesBase.CHATTY_SEER.getKey())
                        .setRoleOnly()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.cupid.command", new CommandCupid(main))
                        .addRoleKey(RolesBase.CUPID.getKey())
                        .setRoleOnly()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(2));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.detective.command", new CommandDetective(main))
                        .addRoleKey(RolesBase.DETECTIVE.getKey())
                        .setRoleOnly()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(2));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.angel.command_2", new CommandFallenAngel(main))
                        .addRoleKey(RolesBase.ANGEL.getKey())
                        .setRoleOnly()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .unsetAutoCompletion()
                        .addArgNumbers(0));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.angel.command_1", new CommandGuardianAngel(main))
                        .addRoleKey(RolesBase.ANGEL.getKey())
                        .setRoleOnly()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .unsetAutoCompletion()
                        .addArgNumbers(0));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.infect_father_of_the_wolves.command", new CommandInfect(main))
                        .addRoleKey(RolesBase.INFECT.getKey())
                        .setRoleOnly().unsetAutoCompletion()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.fox.command", new CommandFox(main))
                        .addRoleKey(RolesBase.FOX.getKey())
                        .setRoleOnly()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.lover.command", new CommandLovers(main))
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1)
                        .addArgNumbers(2));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.protector.command", new CommandProtector(main))
                        .addRoleKey(RolesBase.PROTECTOR.getKey())
                        .setRoleOnly()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.raven.command", new CommandRaven(main))
                        .addRoleKey(RolesBase.RAVEN.getKey())
                        .setRoleOnly()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.citizen.command_2", new CommandCitizenCancelVote(main))
                        .addRoleKey(RolesBase.CITIZEN.getKey())
                        .setRoleOnly().addStateAccess(StatePlayer.ALIVE)
                        .unsetAutoCompletion()
                        .addStateWW(StateGame.GAME).addArgNumbers(0));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.citizen.command_1", new CommandCitizenSeeVote(main))
                        .addRoleKey(RolesBase.CITIZEN.getKey())
                        .setRoleOnly()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .unsetAutoCompletion()
                        .addArgNumbers(0));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.troublemaker.command", new CommandTroubleMaker(main))
                        .addRoleKey(RolesBase.TROUBLEMAKER.getKey())
                        .setRoleOnly()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.werewolf.command", new CommandWereWolf(main))
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(0));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.wild_child.command", new CommandWildChild(main))
                        .addRoleKey(RolesBase.WILD_CHILD.getKey())
                        .setRoleOnly()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.comedian.command", new CommandComedian(main))
                        .addRoleKey(RolesBase.COMEDIAN.getKey())
                        .setRoleOnly()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.witch.command", new CommandWitch(main))
                        .addRoleKey(RolesBase.WITCH.getKey())
                        .setRoleOnly().unsetAutoCompletion()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.trapper.command", new CommandTrapper(main))
                        .addRoleKey(RolesBase.TRAPPER.getKey())
                        .setRoleOnly()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.guardian_angel.command", new CommandAngelRegen(main))
                        .addRoleKey(RolesBase.ANGEL.getKey())
                        .addRoleKey(RolesBase.GUARDIAN_ANGEL.getKey())
                        .setRoleOnly().addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(0));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.succubus.command", new CommandSuccubus(main))
                        .addRoleKey(RolesBase.SUCCUBUS.getKey())
                        .setRoleOnly()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.flute_player.command", new CommandFlutePlayer(main))
                        .addRoleKey(RolesBase.FLUTE_PLAYER.getKey())
                        .setRoleOnly()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1)
                        .addArgNumbers(2));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.librarian.command", new CommandLibrarian(main))
                        .addRoleKey(RolesBase.LIBRARIAN.getKey())
                        .setRoleOnly()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.librarian.request_command", new CommandSendToLibrarian(main))
                        .setRequiredPlayerInGame()
                        .addStateAccess(StatePlayer.ALIVE)
                        .unsetAutoCompletion()
                        .addStateWW(StateGame.GAME));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.menu.roles.command_1", new CommandRole(main))
                        .setRequiredPlayerInGame()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(0));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.menu.rank.command", new CommandRank(main))
                        .addStateWW(StateGame.LOBBY)
                        .addArgNumbers(0));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.menu.global.command", new CommandRules(main))
                        .addArgNumbers(0));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.menu.roles.command_2", new CommandCompo(main))
                        .addArgNumbers(0));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.menu.scenarios.command", new CommandScenarios(main))
                        .addArgNumbers(0));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.menu.enchantments.command", new CommandEnchantment(main))
                        .addArgNumbers(0));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.menu.timers.command", new CommandTimer(main))
                        .addArgNumbers(0));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.vote.command", new CommandVote(main))
                        .setRequiredPlayerInGame()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.help.command", new CommandHelp(main)));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.commands.admin.anonymous_chat.command", new CommandAnonymeChat(main))
                        .addStateWW(StateGame.GAME)
                        .addStateWW(StateGame.START)
                        .setRequiredPlayerInGame()
                        .addStateAccess(StatePlayer.ALIVE));
    }

    private void registerRoles() {
        try {
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.CUPID.getKey(), Cupid.class)
                            .addCategory(Category.VILLAGER));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.WEREWOLF.getKey(), WereWolf.class)
                            .addCategory(Category.WEREWOLF));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.FALSIFIER_WEREWOLF.getKey(), FalsifierWereWolf.class)
                            .addCategory(Category.WEREWOLF));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.INFECT.getKey(), InfectFatherOfTheWolves.class)
                            .addCategory(Category.WEREWOLF));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.WITCH.getKey(), Witch.class)
                            .addCategory(Category.VILLAGER));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.ELDER.getKey(), Elder.class)
                            .addCategory(Category.VILLAGER));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.NAUGHTY_LITTLE_WOLF.getKey(), NaughtyLittleWolf.class)
                            .addCategory(Category.WEREWOLF));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.FOX.getKey(), Fox.class)
                            .addCategory(Category.VILLAGER));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.MISCHIEVOUS_WEREWOLF.getKey(), MischievousWereWolf.class)
                            .addCategory(Category.WEREWOLF));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.LITTLE_GIRL.getKey(), LittleGirl.class)
                            .addCategory(Category.VILLAGER));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.WILD_CHILD.getKey(), WildChild.class)
                            .addCategory(Category.VILLAGER));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.CITIZEN.getKey(), Citizen.class)
                            .addCategory(Category.VILLAGER));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.COMEDIAN.getKey(), Comedian.class)
                            .addCategory(Category.VILLAGER));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.MINER.getKey(), Miner.class)
                            .addCategory(Category.VILLAGER));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.SISTER.getKey(), Sister.class)
                            .addCategory(Category.VILLAGER));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.SIAMESE_TWIN.getKey(), SiameseTwin.class)
                            .addCategory(Category.VILLAGER));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.RAVEN.getKey(), Raven.class)
                            .addCategory(Category.VILLAGER));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.PROTECTOR.getKey(), Protector.class)
                            .addCategory(Category.VILLAGER));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.TRAPPER.getKey(), Trapper.class)
                            .addCategory(Category.VILLAGER));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.TROUBLEMAKER.getKey(), Troublemaker.class)
                            .addCategory(Category.VILLAGER));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.BEAR_TRAINER.getKey(), BearTrainer.class)
                            .addCategory(Category.VILLAGER));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.SEER.getKey(), Seer.class)
                            .addCategory(Category.VILLAGER));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.CHATTY_SEER.getKey(), ChattySeer.class)
                            .addCategory(Category.VILLAGER));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.DETECTIVE.getKey(), Detective.class)
                            .addCategory(Category.VILLAGER));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.SUCCUBUS.getKey(), Succubus.class)
                            .addCategory(Category.NEUTRAL));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.ANGEL.getKey(), Angel.class)
                            .addCategory(Category.NEUTRAL));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.FALLEN_ANGEL.getKey(), FallenAngel.class)
                            .addCategory(Category.NEUTRAL));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.GUARDIAN_ANGEL.getKey(), GuardianAngel.class)
                            .addCategory(Category.NEUTRAL));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.ASSASSIN.getKey(), Assassin.class)
                            .addCategory(Category.NEUTRAL));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.SERIAL_KILLER.getKey(), SerialKiller.class)
                            .addCategory(Category.NEUTRAL));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.AMNESIAC_WEREWOLF.getKey(), AmnesicWerewolf.class)
                            .addCategory(Category.NEUTRAL));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.WHITE_WEREWOLF.getKey(), WhiteWereWolf.class)
                            .addCategory(Category.NEUTRAL));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.THIEF.getKey(), Thief.class)
                            .addCategory(Category.NEUTRAL));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.FLUTE_PLAYER.getKey(), FlutePlayer.class)
                            .addCategory(Category.NEUTRAL));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.LIBRARIAN.getKey(), Librarian.class)
                            .addCategory(Category.VILLAGER));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.VILLAGER.getKey(),
                            Villager.class)
                            .addCategory(Category.VILLAGER));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            RolesBase.METAMORPH.getKey(),
                            Metamorph.class)
                            .addCategory(Category.VILLAGER));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    private void registerScenarios(Main main) {
        scenariosRegister
                .add(new ScenarioRegister("werewolf.name",
                        ScenariosBase.CAT_EYES.getKey(),
                        new CatEyes(main)));
        scenariosRegister
                .add(new ScenarioRegister("werewolf.name",
                        ScenariosBase.COMPASS_TARGET_LAST_DEATH.getKey(),
                        new CompassTargetLastDeath(main)));
        scenariosRegister
                .add(new ScenarioRegister("werewolf.name",
                        ScenariosBase.CUT_CLEAN.getKey(),
                        new CutClean(main))
                        .setDefaultValue());
        scenariosRegister
                .add(new ScenarioRegister("werewolf.name",
                        ScenariosBase.DIAMOND_LIMIT.getKey(),
                        new DiamondLimit(main)).setDefaultValue());
        scenariosRegister
                .add(new ScenarioRegister("werewolf.name",
                        ScenariosBase.DOUBLE_JUMP.getKey(),
                        new DoubleJump(main)));
        scenariosRegister
                .add(new ScenarioRegister("werewolf.name",
                        ScenariosBase.FAST_SMELTING.getKey(),
                        new FastSmelting(main))
                        .setDefaultValue());
        scenariosRegister
                .add(new ScenarioRegister("werewolf.name",
                        ScenariosBase.FIRE_LESS.getKey(),
                        new FireLess(main))
                        .setDefaultValue());
        scenariosRegister
                .add(new ScenarioRegister("werewolf.name",
                        ScenariosBase.HASTEY_BOYS.getKey(),
                        new HasteyBoys(main))
                        .setDefaultValue());
        scenariosRegister
                .add(new ScenarioRegister("werewolf.name",
                        ScenariosBase.HORSE_LESS.getKey(),
                        new HorseLess(main))
                        .setDefaultValue());
        scenariosRegister
                .add(new ScenarioRegister("werewolf.name",
                        ScenariosBase.NO_CLEAN_UP.getKey(),
                        new NoCleanUp(main)));
        scenariosRegister
                .add(new ScenarioRegister("werewolf.name",
                        ScenariosBase.NO_EGG_SNOWBALL.getKey(),
                        new NoEggSnowBall(main))
                        .setDefaultValue());
        scenariosRegister
                .add(new ScenarioRegister("werewolf.name",
                        ScenariosBase.NO_FALL.getKey(),
                        new NoFall(main)));
        scenariosRegister
                .add(new ScenarioRegister("werewolf.name",
                        ScenariosBase.NO_FIRE_WEAPONS.getKey(),
                        new NoFireWeapon(main))
                        .setDefaultValue());
        scenariosRegister
                .add(new ScenarioRegister("werewolf.name",
                        ScenariosBase.NO_NAME_TAG.getKey(),
                        new NoNameTag(main)));
        scenariosRegister
                .add(new ScenarioRegister("werewolf.name",
                        ScenariosBase.NO_POISON.getKey(),
                        new NoPoison(main))
                        .setDefaultValue());
        scenariosRegister
                .add(new ScenarioRegister("werewolf.name",
                        ScenariosBase.ROD_LESS.getKey(),
                        new RodLess(main))
                        .setDefaultValue());
        scenariosRegister
                .add(new ScenarioRegister("werewolf.name",
                        ScenariosBase.SLOW_BOW.getKey(),
                        new SlowBow(main)));
        scenariosRegister
                .add(new ScenarioRegister("werewolf.name",
                        ScenariosBase.TIMBER.getKey(),
                        new Timber(main)));
        scenariosRegister
                .add(new ScenarioRegister("werewolf.name",
                        ScenariosBase.XP_BOOST.getKey(),
                        new XpBoost(main)).setDefaultValue());
        scenariosRegister
                .add(new ScenarioRegister("werewolf.name",
                        ScenariosBase.VANILLA_PLUS.getKey(),
                        new VanillaPlus(main))
                        .setDefaultValue());

    }

    private void registerConfigs(Main main) {

        configsRegister
                .add(new ConfigRegister("werewolf.name",
                        ConfigsBase.VICTORY_LOVERS.getKey()));
        configsRegister
                .add(new ConfigRegister("werewolf.name",
                        ConfigsBase.EVENT_SEER_DEATH.getKey())
                        .setDefaultValue()
                        .addConfig(new SeerEvent(main)));
        configsRegister
                .add(new ConfigRegister("werewolf.name",
                        ConfigsBase.CHAT.getKey())
                        .setDefaultValue()
                        .unSetAppearInMenu());
        configsRegister
                .add(new ConfigRegister("werewolf.name",
                        ConfigsBase.COMPASS_MIDDLE.getKey())
                        .setDefaultValue()
                        .addConfig(new CompassMiddle(main)));
        configsRegister
                .add(new ConfigRegister("werewolf.name",
                        ConfigsBase.SHOW_ROLE_TO_DEATH.getKey())
                        .setDefaultValue()
                        .addConfig(new ShowDeathRole(main)));
        configsRegister
                .add(new ConfigRegister("werewolf.name",
                        ConfigsBase.AUTO_REZ_INFECT.getKey()));
        configsRegister
                .add(new ConfigRegister("werewolf.name",
                        ConfigsBase.AUTO_REZ_WITCH.getKey()));
        configsRegister
                .add(new ConfigRegister("werewolf.name",
                        ConfigsBase.POLYGAMY.getKey()));
        configsRegister
                .add(new ConfigRegister("werewolf.name",
                        ConfigsBase.VOTE.getKey())
                        .setDefaultValue()
                        .unSetAppearInMenu());
        configsRegister
                .add(new ConfigRegister("werewolf.name",
                        ConfigsBase.HIDE_COMPOSITION.getKey()));
        configsRegister
                .add(new ConfigRegister("werewolf.name",
                        ConfigsBase.RED_NAME_TAG.getKey())
                        .setDefaultValue()
                        .addConfig(new RedNameTag(main)));

        configsRegister
                .add(new ConfigRegister("werewolf.name",
                        ConfigsBase.SWEET_ANGEL.getKey()));
        configsRegister
                .add(new ConfigRegister("werewolf.name",
                        ConfigsBase.SEER_EVERY_OTHER_DAY.getKey())
                        .setDefaultValue());
        configsRegister
                .add(new ConfigRegister("werewolf.name",
                        ConfigsBase.PROXIMITY_CHAT.getKey()));
    }

    private void registerTimers() {

        timersRegister
                .add(new TimerRegister("werewolf.name",
                        TimersBase.INVULNERABILITY.getKey())
                        .setDefaultValue(30));
        timersRegister
                .add(new TimerRegister("werewolf.name",
                        TimersBase.ROLE_DURATION.getKey())
                        .setDefaultValue(1200));
        timersRegister
                .add(new TimerRegister("werewolf.name",
                        TimersBase.PVP.getKey())
                        .setDefaultValue(1500));
        timersRegister
                .add(new TimerRegister("werewolf.name",
                        TimersBase.WEREWOLF_LIST.getKey())
                        .setDefaultValue(600));

        timersRegister
                .add(new TimerRegister("werewolf.name",
                        TimersBase.VOTE_BEGIN.getKey())
                        .setDefaultValue(2400));
        timersRegister
                .add(new TimerRegister("werewolf.name",
                        TimersBase.BORDER_BEGIN.getKey())
                        .setDefaultValue(3600));

        timersRegister
                .add(new TimerRegister("werewolf.name",
                        TimersBase.DIGGING.getKey())
                        .setDefaultValue(4200));

        timersRegister
                .add(new TimerRegister("werewolf.name",
                        TimersBase.BORDER_DURATION.getKey())
                        .setDefaultValue(280));

        timersRegister
                .add(new TimerRegister("werewolf.name",
                        TimersBase.VOTE_DURATION.getKey())
                        .setDefaultValue(180));
        timersRegister
                .add(new TimerRegister("werewolf.name",
                        TimersBase.CITIZEN_DURATION.getKey())
                        .setDefaultValue(60));
        timersRegister
                .add(new TimerRegister("werewolf.name",
                        TimersBase.MODEL_DURATION.getKey())
                        .setDefaultValue(240));
        timersRegister
                .add(new TimerRegister("werewolf.name",
                        TimersBase.LOVER_DURATION.getKey())
                        .setDefaultValue(240));
        timersRegister
                .add(new TimerRegister("werewolf.name",
                        TimersBase.ANGEL_DURATION.getKey())
                        .setDefaultValue(240));
        timersRegister
                .add(new TimerRegister("werewolf.name",
                        TimersBase.POWER_DURATION.getKey())
                        .setDefaultValue(240));
        timersRegister
                .add(new TimerRegister("werewolf.name",
                        TimersBase.FOX_SMELL_DURATION.getKey())
                        .setDefaultValue(120));

        timersRegister
                .add(new TimerRegister("werewolf.name",
                        TimersBase.SUCCUBUS_DURATION.getKey())
                        .setDefaultValue(180));

        timersRegister
                .add(new TimerRegister("werewolf.name",
                        TimersBase.DAY_DURATION.getKey())
                        .setDefaultValue(300));

    }

    @Override
    public List<? extends RoleRegister> getRolesRegister() {
        return rolesRegister;
    }

    @Override
    public List<? extends ScenarioRegister> getScenariosRegister() {
        return scenariosRegister;
    }

    @Override
    public List<? extends ConfigRegister> getConfigsRegister() {
        return configsRegister;
    }

    @Override
    public List<? extends TimerRegister> getTimersRegister() {
        return timersRegister;
    }

    @Override
    public List<? extends CommandRegister> getCommandsRegister() {
        return commandsRegister;
    }

    @Override
    public List<? extends CommandRegister> getAdminCommandsRegister() {
        return adminCommandsRegister;
    }

    @Override
    public List<? extends AddonRegister> getAddonsRegister() {
        return addonsRegister;
    }

    @Override
    public void registerAddon(AddonRegister addonRegister) {
       register(addonRegister,addonsRegister);
    }

    @Override
    public void registerRole(RoleRegister roleRegister) {
       register(roleRegister,rolesRegister);
    }

    @Override
    public void registerScenario(ScenarioRegister scenarioRegister) {
        register(scenarioRegister,scenariosRegister);
    }

    @Override
    public void registerConfig(ConfigRegister configRegister) {
        register(configRegister,configsRegister);
    }

    @Override
    public void registerTimer(TimerRegister timerRegister) {
        register(timerRegister,timersRegister);
    }

    @Override
    public void registerCommands(CommandRegister commandRegister) {
        register(commandRegister,commandsRegister);
    }

    @Override
    public void registerAdminCommands(CommandRegister commandRegister) {
        register(commandRegister,adminCommandsRegister);
    }

    private <A extends RegisterAPI> void register(A register, List<A> registers ){
        if(registers.removeAll(registers.stream()
                .filter(register1 -> register1.getKey().equalsIgnoreCase(register.getKey()))
                .collect(Collectors.toList()))){
            Bukkit.getLogger().warning(String.format("[WereWolfPlugin] L'élément %s a été écrasé par l'addon %s",register.getKey(),register.getAddonKey()));
        }
        registers.add(register);
    }


}
