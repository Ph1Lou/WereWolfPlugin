package io.github.ph1lou.werewolfplugin;

import io.github.ph1lou.werewolfapi.*;
import io.github.ph1lou.werewolfapi.enumlg.Scenarios;
import io.github.ph1lou.werewolfapi.enumlg.*;
import io.github.ph1lou.werewolfplugin.commands.admin.CommandChange;
import io.github.ph1lou.werewolfplugin.commands.admin.CommandGeneration;
import io.github.ph1lou.werewolfplugin.commands.admin.CommandSize;
import io.github.ph1lou.werewolfplugin.commands.admin.CommandStop;
import io.github.ph1lou.werewolfplugin.commands.admin.ingame.*;
import io.github.ph1lou.werewolfplugin.commands.roles.*;
import io.github.ph1lou.werewolfplugin.commands.utilities.*;
import io.github.ph1lou.werewolfplugin.listeners.scenarioslisteners.*;
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
        registerScenarios();
        registerTimers();
        registerConfigs();
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
                        .addStateWW(StateGame.START));
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
                        .addRoleKey("werewolf.role.seer.display")
                        .addRoleKey("werewolf.role.seer.display")
                        .setRoleOnly()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.cupid.command", new CommandCupid(main))
                        .addRoleKey("werewolf.role.cupid.display")
                        .setRoleOnly()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(2));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.detective.command", new CommandDetective(main))
                        .addRoleKey("werewolf.role.detective.display")
                        .setRoleOnly()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(2));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.angel.command_2", new CommandFallenAngel(main))
                        .addRoleKey("werewolf.role.angel.display")
                        .setRoleOnly()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.angel.command_1", new CommandGuardianAngel(main))
                        .addRoleKey("werewolf.role.angel.display")
                        .setRoleOnly()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.infect_father_of_the_wolves.command", new CommandInfect(main))
                        .addRoleKey("werewolf.role.infect_father_of_the_wolves.display")
                        .setRoleOnly().unsetAutoCompletion()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.fox.command", new CommandFox(main))
                        .addRoleKey("werewolf.role.fox.display")
                        .setRoleOnly()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.lover.command", new CommandLovers(main))
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(2)
                        .addArgNumbers(3));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.protector.command", new CommandProtector(main))
                        .addRoleKey("werewolf.role.protector.display")
                        .setRoleOnly()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.raven.command", new CommandRaven(main))
                        .addRoleKey("werewolf.role.raven.display")
                        .setRoleOnly()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.citizen.command_2", new CommandCitizenCancelVote(main))
                        .addRoleKey("werewolf.role.citizen.display")
                        .setRoleOnly().addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME).addArgNumbers(0));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.citizen.command_1", new CommandCitizenSeeVote(main))
                        .addRoleKey("werewolf.role.citizen.display")
                        .setRoleOnly()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(0));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.troublemaker.command", new CommandTroubleMaker(main))
                        .addRoleKey("werewolf.role.troublemaker.display")
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
                        .addRoleKey("werewolf.role.wild_child.display")
                        .setRoleOnly()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.comedian.command", new CommandComedian(main))
                        .addRoleKey("werewolf.role.comedian.display")
                        .setRoleOnly()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.witch.command", new CommandWitch(main))
                        .addRoleKey("werewolf.role.witch.display")
                        .setRoleOnly().unsetAutoCompletion()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.trapper.command", new CommandTrapper(main))
                        .addRoleKey("werewolf.role.trapper.display")
                        .setRoleOnly()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.guardian_angel.command", new CommandAngelRegen(main))
                        .addRoleKey("werewolf.role.angel.display")
                        .addRoleKey("werewolf.role.guardian_angel.display")
                        .setRoleOnly().addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(0));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.succubus.command", new CommandSuccubus(main))
                        .addRoleKey("werewolf.role.succubus.display")
                        .setRoleOnly()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.flute_player.command", new CommandFlutePlayer(main))
                        .addRoleKey("werewolf.role.flute_player.display")
                        .setRoleOnly()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1)
                        .addArgNumbers(2));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.librarian.command", new CommandLibrarian(main))
                        .addRoleKey("werewolf.role.librarian.display")
                        .setRoleOnly()
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME)
                        .addArgNumbers(1));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.role.librarian.request_command", new CommandSendToLibrarian(main))
                        .addStateAccess(StatePlayer.ALIVE)
                        .addStateWW(StateGame.GAME));
        commandsRegister
                .add(new CommandRegister("werewolf.name",
                        "werewolf.menu.roles.command_1", new CommandRole(main))
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
                    Roles.CUPID.getKey(),Cupid.class)
                            .addCategory(Category.VILLAGER));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                    Roles.WEREWOLF.getKey(),WereWolf.class)
                            .addCategory(Category.WEREWOLF));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                    Roles.FALSIFIER_WEREWOLF.getKey(),FalsifierWereWolf.class)
                            .addCategory(Category.WEREWOLF));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            Roles.INFECT.getKey(),InfectFatherOfTheWolves.class)
                            .addCategory(Category.WEREWOLF));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            Roles.WITCH.getKey(),Witch.class)
                            .addCategory(Category.VILLAGER));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            Roles.ELDER.getKey(),Elder.class)
                            .addCategory(Category.VILLAGER));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            Roles.NAUGHTY_LITTLE_WOLF.getKey(),NaughtyLittleWolf.class)
                            .addCategory(Category.WEREWOLF));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            Roles.FOX.getKey(),Fox.class)
                            .addCategory(Category.VILLAGER));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            Roles.MISCHIEVOUS_WEREWOLF.getKey(),MischievousWereWolf.class)
                            .addCategory(Category.WEREWOLF));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            Roles.LITTLE_GIRL.getKey(),LittleGirl.class)
                            .addCategory(Category.VILLAGER));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            Roles.WILD_CHILD.getKey(),WildChild.class)
                            .addCategory(Category.VILLAGER));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            Roles.CITIZEN.getKey(),Citizen.class)
                            .addCategory(Category.VILLAGER));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            Roles.COMEDIAN.getKey(),Comedian.class)
                            .addCategory(Category.VILLAGER));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            Roles.MINER.getKey(),Miner.class)
                            .addCategory(Category.VILLAGER));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            Roles.SISTER.getKey(),Sister.class)
                            .addCategory(Category.VILLAGER));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            Roles.SIAMESE_TWIN.getKey(),SiameseTwin.class)
                            .addCategory(Category.VILLAGER));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            Roles.RAVEN.getKey(),Raven.class)
                            .addCategory(Category.VILLAGER));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            Roles.PROTECTOR.getKey(),Protector.class)
                            .addCategory(Category.VILLAGER));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            Roles.TRAPPER.getKey(),Trapper.class)
                            .addCategory(Category.VILLAGER));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            Roles.TROUBLEMAKER.getKey(),Troublemaker.class)
                            .addCategory(Category.VILLAGER));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            Roles.BEAR_TRAINER.getKey(),BearTrainer.class)
                            .addCategory(Category.VILLAGER));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            Roles.SEER.getKey(),Seer.class)
                            .addCategory(Category.VILLAGER));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            Roles.CHATTY_SEER.getKey(),ChattySeer.class)
                            .addCategory(Category.VILLAGER));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            Roles.DETECTIVE.getKey(),Detective.class)
                            .addCategory(Category.VILLAGER));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            Roles.SUCCUBUS.getKey(),Succubus.class)
                            .addCategory(Category.NEUTRAL));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            Roles.ANGEL.getKey(),Angel.class)
                            .addCategory(Category.NEUTRAL));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            Roles.FALLEN_ANGEL.getKey(),FallenAngel.class)
                            .addCategory(Category.NEUTRAL));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            Roles.GUARDIAN_ANGEL.getKey(),GuardianAngel.class)
                            .addCategory(Category.NEUTRAL));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            Roles.ASSASSIN.getKey(),Assassin.class)
                            .addCategory(Category.NEUTRAL));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            Roles.SERIAL_KILLER.getKey(),SerialKiller.class)
                            .addCategory(Category.NEUTRAL));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            Roles.AMNESIAC_WEREWOLF.getKey(),AmnesicWerewolf.class)
                            .addCategory(Category.NEUTRAL));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            Roles.WHITE_WEREWOLF.getKey(),WhiteWereWolf.class)
                            .addCategory(Category.NEUTRAL));

            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            Roles.THIEF.getKey(),Thief.class)
                            .addCategory(Category.NEUTRAL));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            Roles.FLUTE_PLAYER.getKey(),FlutePlayer.class)
                            .addCategory(Category.NEUTRAL));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            Roles.LIBRARIAN.getKey(),Librarian.class)
                            .addCategory(Category.VILLAGER));
            rolesRegister
                    .add(new RoleRegister("werewolf.name",
                            Roles.VILLAGER.getKey(),
                            Villager.class)
                            .addCategory(Category.VILLAGER));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    private void registerScenarios() {
        try {
            scenariosRegister
                    .add(new ScenarioRegister("werewolf.name",
                            Scenarios.CAT_EYES.getKey(),
                            CatEyes.class));
            scenariosRegister
                    .add(new ScenarioRegister("werewolf.name",
                            Scenarios.COMPASS_TARGET_LAST_DEATH.getKey(),
                            CompassTargetLastDeath.class));
            scenariosRegister
                    .add(new ScenarioRegister("werewolf.name",
                            Scenarios.CUT_CLEAN.getKey(),
                            CutClean.class)
                            .setDefaultValue());
            scenariosRegister
                    .add(new ScenarioRegister("werewolf.name",
                            Scenarios.DIAMOND_LIMIT.getKey(),
                            DiamondLimit.class).setDefaultValue());
            scenariosRegister
                    .add(new ScenarioRegister("werewolf.name",
                            Scenarios.DOUBLE_JUMP.getKey(),
                            DoubleJump.class));
            scenariosRegister
                    .add(new ScenarioRegister("werewolf.name",
                            Scenarios.FAST_SMELTING.getKey(),
                            FastSmelting.class)
                            .setDefaultValue());
            scenariosRegister
                    .add(new ScenarioRegister("werewolf.name",
                            Scenarios.FIRE_LESS.getKey(),
                            FireLess.class)
                            .setDefaultValue());
            scenariosRegister
                    .add(new ScenarioRegister("werewolf.name",
                            Scenarios.HASTEY_BOYS.getKey(),
                            HasteyBoys.class)
                            .setDefaultValue());
            scenariosRegister
                    .add(new ScenarioRegister("werewolf.name",
                            Scenarios.HORSE_LESS.getKey(),
                            HorseLess.class)
                            .setDefaultValue());
            scenariosRegister
                    .add(new ScenarioRegister("werewolf.name",
                            Scenarios.NO_CLEAN_UP.getKey(),
                            NoCleanUp.class));
            scenariosRegister
                    .add(new ScenarioRegister("werewolf.name",
                            Scenarios.NO_EGG_SNOWBALL.getKey(),
                            NoEggSnowBall.class)
                            .setDefaultValue());
            scenariosRegister
                    .add(new ScenarioRegister("werewolf.name",
                            Scenarios.NO_FALL.getKey(),
                            NoFall.class));
            scenariosRegister
                    .add(new ScenarioRegister("werewolf.name",
                            Scenarios.NO_FIRE_WEAPONS.getKey(),
                            NoFireWeapon.class)
                            .setDefaultValue());
            scenariosRegister
                    .add(new ScenarioRegister("werewolf.name",
                            Scenarios.NO_NAME_TAG.getKey(),
                            NoNameTag.class));
            scenariosRegister
                    .add(new ScenarioRegister("werewolf.name",
                            Scenarios.NO_POISON.getKey(),
                            NoPoison.class)
                            .setDefaultValue());
            scenariosRegister
                    .add(new ScenarioRegister("werewolf.name",
                            Scenarios.ROD_LESS.getKey(),
                            RodLess.class)
                            .setDefaultValue());
            scenariosRegister
                    .add(new ScenarioRegister("werewolf.name",
                            Scenarios.SLOW_BOW.getKey(),
                            SlowBow.class));
            scenariosRegister
                    .add(new ScenarioRegister("werewolf.name",
                            Scenarios.TIMBER.getKey(),
                            Timber.class));
            scenariosRegister
                    .add(new ScenarioRegister("werewolf.name",
                            Scenarios.XP_BOOST.getKey(),
                            XpBoost.class).setDefaultValue());
            scenariosRegister
                    .add(new ScenarioRegister("werewolf.name",
                            Scenarios.VANILLA_PLUS.getKey(),
                            VanillaPlus.class)
                            .setDefaultValue());

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    private void registerConfigs() {

        configsRegister
                .add(new ConfigRegister("werewolf.name",
                        Configs.VICTORY_LOVERS.getKey()));
        configsRegister
                .add(new ConfigRegister("werewolf.name",
                        Configs.EVENT_SEER_DEATH.getKey())
                        .setDefaultValue());
        configsRegister
                .add(new ConfigRegister("werewolf.name",
                        Configs.CHAT.getKey())
                        .setDefaultValue());
        configsRegister
                .add(new ConfigRegister("werewolf.name",
                        Configs.COMPASS_MIDDLE.getKey())
                        .setDefaultValue());
        configsRegister
                .add(new ConfigRegister("werewolf.name",
                        Configs.SHOW_ROLE_TO_DEATH.getKey()).setDefaultValue());
        configsRegister
                .add(new ConfigRegister("werewolf.name",
                        Configs.AUTO_REZ_INFECT.getKey()));
        configsRegister
                .add(new ConfigRegister("werewolf.name",
                        Configs.AUTO_REZ_WITCH.getKey()));
        configsRegister
                .add(new ConfigRegister("werewolf.name",
                        Configs.POLYGAMY.getKey()));
        configsRegister
                .add(new ConfigRegister("werewolf.name",
                        Configs.VOTE.getKey())
                        .setDefaultValue());
        configsRegister
                .add(new ConfigRegister("werewolf.name",
                        Configs.HIDE_COMPOSITION.getKey()));
        configsRegister
                .add(new ConfigRegister("werewolf.name",
                        Configs.RED_NAME_TAG.getKey())
                        .setDefaultValue());
        configsRegister
                .add(new ConfigRegister("werewolf.name",
                        Configs.SEER_EVERY_OTHER_DAY.getKey())
                        .setDefaultValue());
        configsRegister
                .add(new ConfigRegister("werewolf.name",
                        Configs.PROXIMITY_CHAT.getKey()));
    }

    private void registerTimers() {

        timersRegister
                .add(new TimerRegister("werewolf.name",
                        Timers.INVULNERABILITY.getKey())
                        .setDefaultValue(30));
        timersRegister
                .add(new TimerRegister("werewolf.name",
                        Timers.ROLE_DURATION.getKey())
                        .setDefaultValue(1200));
        timersRegister
                .add(new TimerRegister("werewolf.name",
                        Timers.PVP.getKey())
                        .setDefaultValue(1500));
        timersRegister
                .add(new TimerRegister("werewolf.name",
                        Timers.WEREWOLF_LIST.getKey())
                        .setDefaultValue(600));

        timersRegister
                .add(new TimerRegister("werewolf.name",
                        Timers.VOTE_BEGIN.getKey())
                        .setDefaultValue(2400));
        timersRegister
                .add(new TimerRegister("werewolf.name",
                        Timers.BORDER_BEGIN.getKey())
                        .setDefaultValue(3600));

        timersRegister
                .add(new TimerRegister("werewolf.name",
                        Timers.DIGGING.getKey())
                        .setDefaultValue(4200));

        timersRegister
                .add(new TimerRegister("werewolf.name",
                        Timers.BORDER_DURATION.getKey())
                        .setDefaultValue(280));

        timersRegister
                .add(new TimerRegister("werewolf.name",
                        Timers.VOTE_DURATION.getKey())
                        .setDefaultValue(180));
        timersRegister
                .add(new TimerRegister("werewolf.name",
                        Timers.CITIZEN_DURATION.getKey())
                        .setDefaultValue(60));
        timersRegister
                .add(new TimerRegister("werewolf.name",
                        Timers.MODEL_DURATION.getKey())
                        .setDefaultValue(240));
        timersRegister
                .add(new TimerRegister("werewolf.name",
                        Timers.LOVER_DURATION.getKey())
                        .setDefaultValue(240));
        timersRegister
                .add(new TimerRegister("werewolf.name",
                        Timers.ANGEL_DURATION.getKey())
                        .setDefaultValue(240));
        timersRegister
                .add(new TimerRegister("werewolf.name",
                        Timers.POWER_DURATION.getKey())
                        .setDefaultValue(240));
        timersRegister
                .add(new TimerRegister("werewolf.name",
                        Timers.FOX_SMELL_DURATION.getKey())
                        .setDefaultValue(120));

        timersRegister
                .add(new TimerRegister("werewolf.name",
                        Timers.SUCCUBUS_DURATION.getKey())
                        .setDefaultValue(180));

        timersRegister
                .add(new TimerRegister("werewolf.name",
                        Timers.DAY_DURATION.getKey())
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
