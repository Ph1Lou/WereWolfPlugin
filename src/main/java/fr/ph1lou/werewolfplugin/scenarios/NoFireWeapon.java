package fr.ph1lou.werewolfplugin.scenarios;

import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.annotations.Scenario;
import fr.ph1lou.werewolfapi.basekeys.ScenarioBase;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import fr.ph1lou.werewolfapi.events.game.utils.EnchantmentEvent;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;

@Scenario(key = ScenarioBase.NO_FIRE_WEAPONS, defaultValue = true, meetUpValue = true)
public class NoFireWeapon extends ListenerWerewolf {

    public NoFireWeapon(WereWolfAPI main) {
        super(main);
    }

    @EventHandler
    public void onEnchant(EnchantmentEvent event) {
        event.getFinalEnchants().remove(Enchantment.ARROW_FIRE);
        event.getFinalEnchants().remove(Enchantment.FIRE_ASPECT);
    }
}
