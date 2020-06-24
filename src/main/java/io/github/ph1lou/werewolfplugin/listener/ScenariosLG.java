package io.github.ph1lou.werewolfplugin.listener;

import io.github.ph1lou.pluginlgapi.enumlg.ScenarioLG;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import io.github.ph1lou.werewolfplugin.listener.scenarioslisteners.*;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredListener;

import java.util.ArrayList;
import java.util.List;

public class ScenariosLG {

    final Main main;
    final GameManager game;
    final List<Scenarios> scenariosRegister = new ArrayList<>();

    public ScenariosLG(Main main, GameManager game) {
        this.main = main;
        this.game=game;
    }

    public void init() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new PlayerListener(main,game), main);
        pm.registerEvents(game.eventslg,main);
        pm.registerEvents(new MenuListener(main,game), main);
        pm.registerEvents(new SmallFeaturesListener(main,game), main);
        pm.registerEvents(new EnchantmentListener(game), main);
        pm.registerEvents(new ChatListener(game), main);
        pm.registerEvents(new PatchPotions(game), main);
        pm.registerEvents(new CycleListener(main,game), main);
        scenariosRegister.add(new VanillaPlus(main,game,ScenarioLG.VANILLA_PLUS));
        scenariosRegister.add(new RodLess(main,game,ScenarioLG.ROD_LESS));
        scenariosRegister.add(new CompassTargetLastDeath(main,game,ScenarioLG.COMPASS_TARGET_LAST_DEATH));
        scenariosRegister.add(new CutClean(main,game,ScenarioLG.CUT_CLEAN));
        scenariosRegister.add(new NoFireWeapon(main,game,ScenarioLG.NO_FIRE_WEAPONS));
        scenariosRegister.add(new DiamondLimit(main,game, ScenarioLG.DIAMOND_LIMIT));
        scenariosRegister.add(new FastSmelting(main,game,ScenarioLG.FAST_SMELTING));
        scenariosRegister.add(new FireLess(main,game,ScenarioLG.FIRE_LESS));
        scenariosRegister.add(new HasteyBoys(main,game,ScenarioLG.HASTEY_BOYS));
        scenariosRegister.add(new HorseLess(main,game,ScenarioLG.HORSE_LESS));
        scenariosRegister.add(new NoCleanUp(main,game,ScenarioLG.NO_CLEAN_UP));
        scenariosRegister.add(new NoFall(main,game,ScenarioLG.NO_FALL));
        scenariosRegister.add(new NoPoison(main,game,ScenarioLG.NO_POISON));
        scenariosRegister.add(new NoEggSnowBall(main,game,ScenarioLG.NO_EGG_SNOWBALL));
        scenariosRegister.add(new SlowBow(main,game,ScenarioLG.SLOW_BOW));
        scenariosRegister.add(new Timber(main,game,ScenarioLG.TIMBER));
        scenariosRegister.add(new XpBoost(main,game,ScenarioLG.XP_BOOST));
        scenariosRegister.add(new DoubleJump(main,game,ScenarioLG.DOUBLE_JUMP));
        update();
    }

    public void delete() {

        for (RegisteredListener event : HandlerList.getRegisteredListeners(main)) {
            HandlerList.unregisterAll(event.getListener());
        }

    }

    public void update() {
        for (Scenarios scenario : this.scenariosRegister) {
            scenario.register();
        }
    }
}
