package io.github.ph1lou.werewolfplugin;

import io.github.ph1lou.werewolfapi.ConfigRegister;
import io.github.ph1lou.werewolfapi.RoleRegister;
import io.github.ph1lou.werewolfapi.ScenarioRegister;
import io.github.ph1lou.werewolfapi.TimerRegister;
import io.github.ph1lou.werewolfapi.enumlg.Category;
import io.github.ph1lou.werewolfplugin.listeners.scenarioslisteners.*;
import io.github.ph1lou.werewolfplugin.roles.neutrals.*;
import io.github.ph1lou.werewolfplugin.roles.villagers.*;
import io.github.ph1lou.werewolfplugin.roles.werewolfs.*;

import java.util.ArrayList;
import java.util.List;

public class Register {

    private final Main main;
    private final List<RoleRegister> rolesRegister = new ArrayList<>();
    private final List<ScenarioRegister> scenariosRegister = new ArrayList<>();
    private final List<ConfigRegister> configsRegister = new ArrayList<>();
    private final List<TimerRegister> timersRegister = new ArrayList<>();

    public Register(Main main) {
        this.main = main;
    }

    public void init() {
        registerRole();
        registerScenario();
        registerTimers();
        registerConfig();
    }

    private void registerRole() {
        try {
            new RoleRegister(main, main, "werewolf.role.cupid.display").registerRole(Cupid.class).addCategory(Category.VILLAGER).create();

            new RoleRegister(main, main, "werewolf.role.werewolf.display").registerRole(WereWolf.class).addCategory(Category.WEREWOLF).create();
            new RoleRegister(main, main, "werewolf.role.falsifier_werewolf.display").registerRole(FalsifierWereWolf.class).addCategory(Category.WEREWOLF).create();

            new RoleRegister(main, main, "werewolf.role.infect_father_of_the_wolves.display").registerRole(InfectFatherOfTheWolves.class).addCategory(Category.WEREWOLF).create();
            new RoleRegister(main, main, "werewolf.role.witch.display").registerRole(Witch.class).addCategory(Category.VILLAGER).create();
            new RoleRegister(main, main, "werewolf.role.elder.display").registerRole(Elder.class).addCategory(Category.VILLAGER).create();

            new RoleRegister(main, main, "werewolf.role.naughty_little_wolf.display").registerRole(NaughtyLittleWolf.class).addCategory(Category.WEREWOLF).create();
            new RoleRegister(main, main, "werewolf.role.fox.display").registerRole(Fox.class).addCategory(Category.VILLAGER).create();

            new RoleRegister(main, main, "werewolf.role.mischievous_werewolf.display").registerRole(MischievousWereWolf.class).addCategory(Category.WEREWOLF).create();
            new RoleRegister(main, main, "werewolf.role.little_girl.display").registerRole(LittleGirl.class).addCategory(Category.VILLAGER).create();

            new RoleRegister(main, main, "werewolf.role.wild_child.display").registerRole(WildChild.class).addCategory(Category.VILLAGER).create();
            new RoleRegister(main, main, "werewolf.role.citizen.display").registerRole(Citizen.class).addCategory(Category.VILLAGER).create();
            new RoleRegister(main, main, "werewolf.role.comedian.display").registerRole(Comedian.class).addCategory(Category.VILLAGER).create();
            new RoleRegister(main, main, "werewolf.role.miner.display").registerRole(Miner.class).addCategory(Category.VILLAGER).create();

            new RoleRegister(main, main, "werewolf.role.sister.display").registerRole(Sister.class).addCategory(Category.VILLAGER).create();
            new RoleRegister(main, main, "werewolf.role.siamese_twin.display").registerRole(SiameseTwin.class).addCategory(Category.VILLAGER).create();

            new RoleRegister(main, main, "werewolf.role.raven.display").registerRole(Raven.class).addCategory(Category.VILLAGER).create();
            new RoleRegister(main, main, "werewolf.role.protector.display").registerRole(Protector.class).addCategory(Category.VILLAGER).create();
            new RoleRegister(main, main, "werewolf.role.trapper.display").registerRole(Trapper.class).addCategory(Category.VILLAGER).create();
            new RoleRegister(main, main, "werewolf.role.troublemaker.display").registerRole(Troublemaker.class).addCategory(Category.VILLAGER).create();

            new RoleRegister(main, main, "werewolf.role.bear_trainer.display").registerRole(BearTrainer.class).addCategory(Category.VILLAGER).create();
            new RoleRegister(main, main, "werewolf.role.seer.display").registerRole(Seer.class).addCategory(Category.VILLAGER).create();
            new RoleRegister(main, main, "werewolf.role.chatty_seer.display").registerRole(ChattySeer.class).addCategory(Category.VILLAGER).create();
            new RoleRegister(main, main, "werewolf.role.detective.display").registerRole(Detective.class).addCategory(Category.VILLAGER).create();

            new RoleRegister(main, main, "werewolf.role.succubus.display").registerRole(Succubus.class).addCategory(Category.NEUTRAL).create();
            new RoleRegister(main, main, "werewolf.role.angel.display").registerRole(Angel.class).addCategory(Category.NEUTRAL).create();
            new RoleRegister(main, main, "werewolf.role.fallen_angel.display").registerRole(FallenAngel.class).addCategory(Category.NEUTRAL).create();
            new RoleRegister(main, main, "werewolf.role.guardian_angel.display").registerRole(GuardianAngel.class).addCategory(Category.NEUTRAL).create();
            new RoleRegister(main, main, "werewolf.role.assassin.display").registerRole(Assassin.class).addCategory(Category.NEUTRAL).create();
            new RoleRegister(main, main, "werewolf.role.serial_killer.display").registerRole(SerialKiller.class).addCategory(Category.NEUTRAL).create();

            new RoleRegister(main, main, "werewolf.role.amnesiac_werewolf.display").registerRole(AmnesicWerewolf.class).addCategory(Category.NEUTRAL).create();
            new RoleRegister(main, main, "werewolf.role.white_werewolf.display").registerRole(WhiteWereWolf.class).addCategory(Category.NEUTRAL).create();

            new RoleRegister(main, main, "werewolf.role.thief.display").registerRole(Thief.class).addCategory(Category.NEUTRAL).create();
            new RoleRegister(main, main, "werewolf.role.flute_player.display").registerRole(FlutePlayer.class).addCategory(Category.NEUTRAL).create();
            new RoleRegister(main, main, "werewolf.role.librarian.display").registerRole(Librarian.class).addCategory(Category.VILLAGER).create();
            new RoleRegister(main, main, "werewolf.role.villager.display").registerRole(Villager.class).addCategory(Category.VILLAGER).create();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    private void registerScenario() {
        try {
            new ScenarioRegister(main, main, "werewolf.menu.scenarios.cat_eyes").registerScenario(CatEyes.class).create();
            new ScenarioRegister(main, main, "werewolf.menu.scenarios.compass_target_last_death").registerScenario(CompassTargetLastDeath.class).create();
            new ScenarioRegister(main, main, "werewolf.menu.scenarios.cut_clean").registerScenario(CutClean.class).setDefaultValue(true).create();
            new ScenarioRegister(main, main, "werewolf.menu.scenarios.diamond_limit").registerScenario(DiamondLimit.class).setDefaultValue(true).create();
            new ScenarioRegister(main, main, "werewolf.menu.scenarios.double_jump").registerScenario(DoubleJump.class).create();
            new ScenarioRegister(main, main, "werewolf.menu.scenarios.fast_smelting").registerScenario(FastSmelting.class).setDefaultValue(true).create();
            new ScenarioRegister(main, main, "werewolf.menu.scenarios.fire_less").registerScenario(FireLess.class).setDefaultValue(true).create();
            new ScenarioRegister(main, main, "werewolf.menu.scenarios.hastey_boys").registerScenario(HasteyBoys.class).setDefaultValue(true).create();
            new ScenarioRegister(main, main, "werewolf.menu.scenarios.horse_less").registerScenario(HorseLess.class).setDefaultValue(true).create();
            new ScenarioRegister(main, main, "werewolf.menu.scenarios.no_clean_up").registerScenario(NoCleanUp.class).create();
            new ScenarioRegister(main, main, "werewolf.menu.scenarios.no_egg_snowball").registerScenario(NoEggSnowBall.class).setDefaultValue(true).create();
            new ScenarioRegister(main, main, "werewolf.menu.scenarios.no_fall").registerScenario(NoFall.class).create();
            new ScenarioRegister(main, main, "werewolf.menu.scenarios.no_fire_weapons").registerScenario(NoFireWeapon.class).setDefaultValue(true).create();
            new ScenarioRegister(main, main, "werewolf.menu.scenarios.no_name_tag").registerScenario(NoNameTag.class).create();
            new ScenarioRegister(main, main, "werewolf.menu.scenarios.no_poison").registerScenario(NoPoison.class).setDefaultValue(true).create();
            new ScenarioRegister(main, main, "werewolf.menu.scenarios.rod_less").registerScenario(RodLess.class).setDefaultValue(true).create();
            new ScenarioRegister(main, main, "werewolf.menu.scenarios.slow_bow").registerScenario(SlowBow.class).create();
            new ScenarioRegister(main, main, "werewolf.menu.scenarios.timber").registerScenario(Timber.class).create();
            new ScenarioRegister(main, main, "werewolf.menu.scenarios.xp_boost").registerScenario(XpBoost.class).setDefaultValue(true).create();
            new ScenarioRegister(main, main, "werewolf.menu.scenarios.vanilla_plus").registerScenario(VanillaPlus.class).setDefaultValue(true).create();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    private void registerConfig() {
        new ConfigRegister(main, main, "werewolf.menu.global.victory_couple").setDefaultValue(false).create();
        new ConfigRegister(main, main, "werewolf.menu.global.event_seer_death").setDefaultValue(true).create();
        new ConfigRegister(main, main, "werewolf.menu.global.chat").setDefaultValue(true).create();
        new ConfigRegister(main, main, "werewolf.menu.global.compass_middle").setDefaultValue(true).create();
        new ConfigRegister(main, main, "werewolf.menu.global.show_role_to_death").setDefaultValue(true).create();
        new ConfigRegister(main, main, "werewolf.menu.global.auto_rez_infect").setDefaultValue(false).create();
        new ConfigRegister(main, main, "werewolf.menu.global.auto_rez_witch").setDefaultValue(false).create();
        new ConfigRegister(main, main, "werewolf.menu.global.polygamy").setDefaultValue(false).create();
        new ConfigRegister(main, main, "werewolf.menu.global.vote").setDefaultValue(true).create();
        new ConfigRegister(main, main, "werewolf.menu.global.hide_composition").setDefaultValue(false).create();
        new ConfigRegister(main, main, "werewolf.menu.global.red_name_tag").setDefaultValue(true).create();
        new ConfigRegister(main, main, "werewolf.menu.global.seer_every_other_day").setDefaultValue(true).create();
        new ConfigRegister(main, main, "werewolf.menu.global.proximity_chat").setDefaultValue(false).create();
    }

    private void registerTimers() {

        new TimerRegister(main, main, "werewolf.menu.timers.invulnerability").setDefaultValue(30).create();
        new TimerRegister(main, main, "werewolf.menu.timers.role_duration").setDefaultValue(1200).create();
        new TimerRegister(main, main, "werewolf.menu.timers.pvp").setDefaultValue(1500).create();
        new TimerRegister(main, main, "werewolf.menu.timers.werewolf_list").setDefaultValue(600).create();

        new TimerRegister(main, main, "werewolf.menu.timers.vote_begin").setDefaultValue(2400).create();
        new TimerRegister(main, main, "werewolf.menu.timers.border_begin").setDefaultValue(3600).create();

        new TimerRegister(main, main, "werewolf.menu.timers.digging_end").setDefaultValue(4200).create();

        new TimerRegister(main, main, "werewolf.menu.timers.border_duration").setDefaultValue(280).create();

        new TimerRegister(main, main, "werewolf.menu.timers.vote_duration").setDefaultValue(180).create();
        new TimerRegister(main, main, "werewolf.menu.timers.citizen_duration").setDefaultValue(60).create();
        new TimerRegister(main, main, "werewolf.menu.timers.model_duration").setDefaultValue(240).create();
        new TimerRegister(main, main, "werewolf.menu.timers.lover_duration").setDefaultValue(240).create();
        new TimerRegister(main, main, "werewolf.menu.timers.angel_duration").setDefaultValue(240).create();
        new TimerRegister(main, main, "werewolf.menu.timers.power_duration").setDefaultValue(240).create();
        new TimerRegister(main, main, "werewolf.menu.timers.fox_smell_duration").setDefaultValue(120).create();

        new TimerRegister(main, main, "werewolf.menu.timers.succubus_duration").setDefaultValue(180).create();

        new TimerRegister(main, main, "werewolf.menu.timers.day_duration").setDefaultValue(300).create();

    }

    public List<RoleRegister> getRolesRegister() {
        return rolesRegister;
    }

    public List<ScenarioRegister> getScenariosRegister() {
        return scenariosRegister;
    }

    public List<ConfigRegister> getConfigsRegister() {
        return configsRegister;
    }

    public List<TimerRegister> getTimersRegister() {
        return timersRegister;
    }
}
