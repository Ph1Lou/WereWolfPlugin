package fr.ph1lou.werewolfplugin.registers;

import fr.ph1lou.werewolfplugin.listeners.scenarios.BetaZombies;
import fr.ph1lou.werewolfplugin.listeners.scenarios.CatEyes;
import fr.ph1lou.werewolfplugin.listeners.scenarios.CompassMiddle;
import fr.ph1lou.werewolfplugin.listeners.scenarios.CompassTargetLastDeath;
import fr.ph1lou.werewolfplugin.listeners.scenarios.DiamondLimit;
import fr.ph1lou.werewolfplugin.listeners.scenarios.DoubleJump;
import fr.ph1lou.werewolfplugin.listeners.scenarios.FastSmelting;
import fr.ph1lou.werewolfplugin.listeners.scenarios.FinalHeal;
import fr.ph1lou.werewolfplugin.listeners.scenarios.FireLess;
import fr.ph1lou.werewolfplugin.listeners.scenarios.HasteyBabies;
import fr.ph1lou.werewolfplugin.listeners.scenarios.HasteyBoys;
import fr.ph1lou.werewolfplugin.listeners.scenarios.HorseLess;
import fr.ph1lou.werewolfplugin.listeners.scenarios.NoCleanUp;
import fr.ph1lou.werewolfplugin.listeners.scenarios.NoEggSnowBall;
import fr.ph1lou.werewolfplugin.listeners.scenarios.NoEnd;
import fr.ph1lou.werewolfplugin.listeners.scenarios.NoExtraStones;
import fr.ph1lou.werewolfplugin.listeners.scenarios.NoFall;
import fr.ph1lou.werewolfplugin.listeners.scenarios.NoFireWeapon;
import fr.ph1lou.werewolfplugin.listeners.scenarios.NoNameTag;
import fr.ph1lou.werewolfplugin.listeners.scenarios.NoNether;
import fr.ph1lou.werewolfplugin.listeners.scenarios.NoPoison;
import fr.ph1lou.werewolfplugin.listeners.scenarios.RodLess;
import fr.ph1lou.werewolfplugin.listeners.scenarios.SafeMiner;
import fr.ph1lou.werewolfplugin.listeners.scenarios.SlowBow;
import fr.ph1lou.werewolfplugin.listeners.scenarios.Timber;
import fr.ph1lou.werewolfplugin.listeners.scenarios.VanillaPlus;
import fr.ph1lou.werewolfplugin.listeners.scenarios.XpBoost;
import fr.ph1lou.werewolfapi.enums.ScenariosBase;
import fr.ph1lou.werewolfapi.registers.impl.ScenarioRegister;
import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfplugin.listeners.scenarios.CutClean;

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
                        .setDefaultValue()
                        .addIncompatibleScenario(ScenariosBase.HASTEY_BABIES.getKey()));
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

        scenariosRegister
                .add(new ScenarioRegister("werewolf.name",
                        ScenariosBase.NO_EXTRA_STONES.getKey(),
                        new NoExtraStones(main))
                        .setDefaultValue());
        scenariosRegister
                .add(new ScenarioRegister("werewolf.name",
                        ScenariosBase.FINAL_HEAL.getKey(),
                        new FinalHeal(main))
                        .setDefaultValue());

        scenariosRegister
                .add(new ScenarioRegister("werewolf.name",
                        ScenariosBase.SAFE_MINER.getKey(),
                        new SafeMiner(main)));

        scenariosRegister
                .add(new ScenarioRegister("werewolf.name",
                        ScenariosBase.BETA_ZOMBIES.getKey(),
                        new BetaZombies(main)));

        scenariosRegister
                .add(new ScenarioRegister("werewolf.name",
                        ScenariosBase.HASTEY_BABIES.getKey(),
                        new HasteyBabies(main))
                        .addIncompatibleScenario(ScenariosBase.HASTEY_BOYS.getKey()));

        return scenariosRegister;

    }
}
