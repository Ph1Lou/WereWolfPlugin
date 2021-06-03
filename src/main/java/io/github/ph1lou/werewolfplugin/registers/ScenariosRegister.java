package io.github.ph1lou.werewolfplugin.registers;

import io.github.ph1lou.werewolfapi.enums.ScenariosBase;
import io.github.ph1lou.werewolfapi.registers.ScenarioRegister;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.listeners.scenarios.CatEyes;
import io.github.ph1lou.werewolfplugin.listeners.scenarios.CompassMiddle;
import io.github.ph1lou.werewolfplugin.listeners.scenarios.CompassTargetLastDeath;
import io.github.ph1lou.werewolfplugin.listeners.scenarios.CutClean;
import io.github.ph1lou.werewolfplugin.listeners.scenarios.DiamondLimit;
import io.github.ph1lou.werewolfplugin.listeners.scenarios.DoubleJump;
import io.github.ph1lou.werewolfplugin.listeners.scenarios.FastSmelting;
import io.github.ph1lou.werewolfplugin.listeners.scenarios.FireLess;
import io.github.ph1lou.werewolfplugin.listeners.scenarios.HasteyBoys;
import io.github.ph1lou.werewolfplugin.listeners.scenarios.HorseLess;
import io.github.ph1lou.werewolfplugin.listeners.scenarios.NoCleanUp;
import io.github.ph1lou.werewolfplugin.listeners.scenarios.NoEggSnowBall;
import io.github.ph1lou.werewolfplugin.listeners.scenarios.NoEnd;
import io.github.ph1lou.werewolfplugin.listeners.scenarios.NoFall;
import io.github.ph1lou.werewolfplugin.listeners.scenarios.NoFireWeapon;
import io.github.ph1lou.werewolfplugin.listeners.scenarios.NoNameTag;
import io.github.ph1lou.werewolfplugin.listeners.scenarios.NoNether;
import io.github.ph1lou.werewolfplugin.listeners.scenarios.NoPoison;
import io.github.ph1lou.werewolfplugin.listeners.scenarios.RodLess;
import io.github.ph1lou.werewolfplugin.listeners.scenarios.SlowBow;
import io.github.ph1lou.werewolfplugin.listeners.scenarios.Timber;
import io.github.ph1lou.werewolfplugin.listeners.scenarios.VanillaPlus;
import io.github.ph1lou.werewolfplugin.listeners.scenarios.XpBoost;

import java.util.ArrayList;
import java.util.List;

public class ScenariosRegister {

    public static List<ScenarioRegister> registerScenarios(Main main) {

        List<ScenarioRegister> scenariosRegister = new ArrayList<>();

        scenariosRegister
                .add(new ScenarioRegister("werewolf.name",
                        ScenariosBase.CAT_EYES.getKey(),
                        new CatEyes(main)));
        scenariosRegister
                .add(new ScenarioRegister("werewolf.name",
                        ScenariosBase.COMPASS_TARGET_LAST_DEATH.getKey(),
                        new CompassTargetLastDeath(main))
                        .addIncompatibleScenario(ScenariosBase.COMPASS_MIDDLE.getKey()));
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

        scenariosRegister
                .add(new ScenarioRegister("werewolf.name",
                        ScenariosBase.COMPASS_MIDDLE.getKey(),
                        new CompassMiddle(main))
                        .setDefaultValue()
                        .addIncompatibleScenario(ScenariosBase.COMPASS_TARGET_LAST_DEATH.getKey())
                );

        scenariosRegister
                .add(new ScenarioRegister("werewolf.name",
                        ScenariosBase.NO_NETHER.getKey(),
                        new NoNether(main))
                        .setDefaultValue()
                );
        scenariosRegister
                .add(new ScenarioRegister("werewolf.name",
                        ScenariosBase.NO_END.getKey(),
                        new NoEnd(main))
                        .setDefaultValue()
                );

        return scenariosRegister;

    }
}
