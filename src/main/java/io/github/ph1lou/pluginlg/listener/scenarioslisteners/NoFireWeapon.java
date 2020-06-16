package io.github.ph1lou.pluginlg.listener.scenarioslisteners;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.enumlg.ScenarioLG;
import io.github.ph1lou.pluginlgapi.events.EnchantmentEvent;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;

public class NoFireWeapon extends Scenarios {

    public NoFireWeapon(MainLG main, GameManager game, ScenarioLG scenario) {
        super(main, game, scenario);
    }

    @EventHandler
    public void onEnchant(EnchantmentEvent event){
        event.getFinalEnchants().remove(Enchantment.ARROW_FIRE);
        event.getFinalEnchants().remove(Enchantment.FIRE_ASPECT);
    }
}
