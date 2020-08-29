package io.github.ph1lou.werewolfplugin.listeners.scenarioslisteners;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.Scenarios;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.events.EnchantmentEvent;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;

public class NoFireWeapon extends Scenarios {

    public NoFireWeapon(GetWereWolfAPI main, WereWolfAPI game, String key) {
        super(main, game, key);
    }

    @EventHandler
    public void onEnchant(EnchantmentEvent event){
        event.getFinalEnchants().remove(Enchantment.ARROW_FIRE);
        event.getFinalEnchants().remove(Enchantment.FIRE_ASPECT);
    }
}
