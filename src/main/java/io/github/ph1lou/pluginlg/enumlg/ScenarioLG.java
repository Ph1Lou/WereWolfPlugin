package io.github.ph1lou.pluginlg.enumlg;

public enum ScenarioLG {
    VANILLA_PLUS(true),
    ROD_LESS(true),
    HORSE_LESS(true),
    FIRE_LESS(true),
    CUT_CLEAN(true),
    DIAMOND_LIMIT(true),
    FAST_SMELTING(true),
    HASTEY_BOYS(true),
    NO_FALL(false),
    NO_SNOWBALL(true),
    NO_POISON(true),
    XP_BOOST(true),
    COMPASS_TARGET_LAST_DEATH(false),
    NO_CLEAN_UP(true),
    NO_NAME_TAG(false),
    CAT_EYES(true),
    SLOW_BOW(false);

    private final Boolean value;


    ScenarioLG(Boolean value) {
        this.value=value;
    }

    public Boolean getValue() {
        return this.value;
    }

}



