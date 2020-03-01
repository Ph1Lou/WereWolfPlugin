package io.github.ph1lou.pluginlg.enumlg;

public enum ScenarioLG {
    VANILLA_PLUS(true,"Vanilla+"),
    ROD_LESS(true,"Rodless"),
    HORSE_LESS(true,"Horseless"),
    FIRE_LESS(true,"Fireless"),
    CUT_CLEAN(true,"Cutclean"),
    DIAMOND_LIMIT(true,"Diamond Limit"),
    FAST_SMELTING(true,"FastMelting"),
    HASTEY_BOYS(true,"HasteyBoys"),
    NO_FALL(false,"Nofall"),
    SNOWBALL(false,"Boules de Neige"),
    POISON(false,"Poison"),
    XP_BOOST(true,"Boost d'XP"),
    COMPASS_TARGET_LAST_DEATH(true,"La boussole pointe sur le lieu de la dernière mort"),
    NO_CLEAN_UP(true,"No Clean Up"),
    NO_NAME_TAG(false,"No Name Tag"),
    CAT_EYES(true,"Cat Eyes");
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



