package io.github.ph1lou.pluginlg.enumlg;

public enum ScenarioLG {
    VANILLA_PLUS(true,"Vanilla+"),
    ROD_LESS(true,"RodLess"),
    HORSE_LESS(true,"HorseLess"),
    FIRE_LESS(true,"FireLess"),
    CUT_CLEAN(true,"CutClean"),
    DIAMOND_LIMIT(true,"DiamondLimit"),
    FAST_SMELTING(true,"FastMelting"),
    HASTEY_BOYS(true,"HasteyBoys"),
    NO_FALL(false,"NoFall"),
    NO_SNOWBALL(true,"NoSnowBall"),
    NO_POISON(true,"NoPoison"),
    XP_BOOST(true,"XPBoost"),
    COMPASS_TARGET_LAST_DEATH(true,"La boussole pointe sur le lieu de la dernière mort"),
    NO_CLEAN_UP(true,"NoCleanUp"),
    NO_NAME_TAG(false,"NoNameTag"),
    CAT_EYES(true,"CatEyes"),
    SLOW_BOW(false,"SlowBow");

    private final Boolean value;
    private final String appearance;

    ScenarioLG(Boolean value, String appearance) {
        this.value=value;
        this.appearance=appearance;
    }

    public Boolean getValue() {
        return this.value;
    }

    public String getAppearance() {
        return this.appearance;
    }
}



