package fr.ph1lou.werewolfplugin.scenarios;

import fr.ph1lou.werewolfapi.annotations.Scenario;
import fr.ph1lou.werewolfapi.basekeys.ScenarioBase;
import fr.ph1lou.werewolfapi.enums.UniversalEnchantment;
import fr.ph1lou.werewolfapi.events.game.utils.EnchantmentEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import org.bukkit.event.EventHandler;

@Scenario(key = ScenarioBase.NO_FIRE_WEAPONS, defaultValue = true, meetUpValue = true)
public class NoFireWeapon extends ListenerWerewolf {

    public NoFireWeapon(WereWolfAPI main) {
        super(main);
    }

    @EventHandler
    public void onEnchant(EnchantmentEvent event) {
        event.getFinalEnchants().remove(UniversalEnchantment.FLAME);
        event.getFinalEnchants().remove(UniversalEnchantment.FIRE_ASPECT);
    }
}
