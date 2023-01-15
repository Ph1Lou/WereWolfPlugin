package fr.ph1lou.werewolfplugin.scenarios;

import fr.ph1lou.werewolfapi.annotations.Scenario;
import fr.ph1lou.werewolfapi.basekeys.ScenarioBase;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Scenario(key = ScenarioBase.BETA_ZOMBIES)
public class BetaZombies extends ListenerWerewolf {

    public BetaZombies(WereWolfAPI main) {
        super(main);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {


        List<ItemStack> loots = event.getDrops();

        for (int i = loots.size() - 1; i >= 0; --i) {
            ItemStack is = loots.get(i);
            if (is == null) {
                return;
            }

            if (is.getType() == Material.ROTTEN_FLESH) {
                loots.remove(i);
                loots.add(new ItemStack(UniversalMaterial.FEATHER.getStack()));
            }
        }
    }
}
