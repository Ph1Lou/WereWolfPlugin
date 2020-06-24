package io.github.ph1lou.werewolfplugin.listener.scenarioslisteners;

import io.github.ph1lou.pluginlgapi.enumlg.ScenarioLG;
import io.github.ph1lou.pluginlgapi.events.EnchantmentEvent;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;

public class NoFireWeapon extends Scenarios {

    public NoFireWeapon(Main main, GameManager game, ScenarioLG scenario) {
        super(main, game, scenario);
    }

    @EventHandler
    public void onEnchant(EnchantmentEvent event){
        event.getFinalEnchants().remove(Enchantment.ARROW_FIRE);
        event.getFinalEnchants().remove(Enchantment.FIRE_ASPECT);
    }
}
