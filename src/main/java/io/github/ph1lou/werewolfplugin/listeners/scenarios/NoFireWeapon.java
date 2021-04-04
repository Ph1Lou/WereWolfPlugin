package io.github.ph1lou.werewolfplugin.listeners.scenarios;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.ListenerManager;
import io.github.ph1lou.werewolfapi.events.game.utils.EnchantmentEvent;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;

public class NoFireWeapon extends ListenerManager {

    public NoFireWeapon(GetWereWolfAPI main) {
        super(main);
    }

    @EventHandler
    public void onEnchant(EnchantmentEvent event) {
        event.getFinalEnchants().remove(Enchantment.ARROW_FIRE);
        event.getFinalEnchants().remove(Enchantment.FIRE_ASPECT);
    }
}
