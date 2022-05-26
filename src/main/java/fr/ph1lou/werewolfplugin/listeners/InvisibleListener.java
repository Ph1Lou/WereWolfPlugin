package fr.ph1lou.werewolfplugin.listeners;

import fr.ph1lou.werewolfapi.events.game.life_cycle.DeathItemsEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.ResurrectionEvent;
import fr.ph1lou.werewolfapi.events.game.utils.EnchantmentEvent;
import fr.ph1lou.werewolfapi.events.game.utils.GoldenAppleParticleEvent;
import fr.ph1lou.werewolfapi.events.roles.StealEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.role.interfaces.IInvisible;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class InvisibleListener implements Listener {


    private final WereWolfAPI game;

    //todo à tester

    public InvisibleListener(WereWolfAPI game) {
        this.game=game;
    }

    @EventHandler
    public void onEnchantment(EnchantmentEvent event){

        if(!(event.getPlayerWW().getRole() instanceof IInvisible)){
            return;
        }

        if (event.getEnchants().containsKey(Enchantment.KNOCKBACK)) {
            event.getFinalEnchants().put(Enchantment.KNOCKBACK,
                    Math.min(event.getEnchants().get(Enchantment.KNOCKBACK),
                            game.getConfig().getLimitKnockBack()));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onResurrection(ResurrectionEvent event) {

        if(!(event.getPlayerWW().getRole() instanceof IInvisible)){
            return;
        }

        ((IInvisible)event.getPlayerWW().getRole()).setInvisible(false);
    }

    @EventHandler
    public void onFinalDeath(DeathItemsEvent event) {

        if(!(event.getPlayerWW().getRole() instanceof IInvisible)){
            return;
        }

        ((IInvisible)event.getPlayerWW().getRole()).setInvisible(false);

        if (!this.game.getConfig().isKnockBackForInvisibleRoleOnly()) return;

        for (ItemStack i : event.getItems()) {
            if (i != null) {
                i.removeEnchantment(Enchantment.KNOCKBACK);
            }
        }
    }

    @EventHandler
    public void onStealEvent(StealEvent event) {

        if(!(event.getThiefWW().getRole() instanceof IInvisible)){
            return;
        }

        ((IInvisible)event.getThiefWW().getRole()).setInvisible(false);
    }

    @EventHandler
    public void onGoldenAppleEat(GoldenAppleParticleEvent event) {

        if(!(event.getPlayerWW().getRole() instanceof IInvisible)){
            return;
        }

        if (!((IInvisible)event.getPlayerWW().getRole()).isInvisible()) return;

        event.setCancelled(true);
    }
}
