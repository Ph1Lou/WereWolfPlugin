package io.github.ph1lou.pluginlg.enumlg;

import io.github.ph1lou.pluginlg.listener.gamelisteners.scenarioslisteners.*;

public enum ScenarioLG {
    VANILLA_PLUS(true, VanillaPlus.class),
    ROD_LESS(true, RodLess.class),
    HORSE_LESS(true, HorseLess.class),
    FIRE_LESS(true, FireLess.class),
    CUT_CLEAN(true, CutClean.class),
    DIAMOND_LIMIT(true, DiamondLimit.class),
    FAST_SMELTING(true, FastSmelting.class),
    HASTEY_BOYS(true, HasteyBoys.class),
    NO_FALL(false, NoFall.class),
    NO_SNOWBALL(true, NoSnowBall.class),
    NO_POISON(true, NoPoison.class),
    XP_BOOST(true, XpBoost.class),
    COMPASS_TARGET_LAST_DEATH(false, CompassTargetLastDeath.class),
    NO_CLEAN_UP(true, NoCleanUp.class),
    NO_NAME_TAG(false, null),
    CAT_EYES(true, null),
    NO_FIRE_WEAPONS(true, null),
    TIMBER(false, Timber.class),
    SLOW_BOW(false, SlowBow.class);

    private final Boolean value;
    private final Class<? extends Scenarios> scenario;

    ScenarioLG(Boolean value, Class<? extends Scenarios> scenario) {
        this.value = value;
        this.scenario = scenario;
    }

    public Boolean getValue() {
        return this.value;
    }

    public Class<? extends Scenarios> getScenario() {
        return scenario;
    }
}



