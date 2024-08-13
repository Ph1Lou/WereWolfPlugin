package fr.ph1lou.werewolfplugin.listeners;

import fr.ph1lou.werewolfapi.enums.UniversalEnchantment;
import fr.ph1lou.werewolfapi.events.game.life_cycle.DeathItemsEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.ResurrectionEvent;
import fr.ph1lou.werewolfapi.events.game.utils.EnchantmentEvent;
import fr.ph1lou.werewolfapi.events.game.utils.GoldenAppleParticleEvent;
import fr.ph1lou.werewolfapi.events.roles.InvisibleEvent;
import fr.ph1lou.werewolfapi.events.roles.StealEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.role.interfaces.IInvisible;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import fr.ph1lou.werewolfapi.enums.UniversalPotionEffectType;

public class InvisibleListener implements Listener {


    private final WereWolfAPI game;

    public InvisibleListener(WereWolfAPI game) {
        this.game = game;
    }

    @EventHandler
    public void onEnchantment(EnchantmentEvent event) {

        if (!(event.getPlayerWW().getRole() instanceof IInvisible)) {
            return;
        }

        if (event.getEnchants().containsKey(UniversalEnchantment.KNOCKBACK)) {
            event.getFinalEnchants().put(UniversalEnchantment.KNOCKBACK,
                    Math.min(event.getEnchants().get(UniversalEnchantment.KNOCKBACK),
                            game.getConfig().getLimitKnockBack()));
        }
    }

    //Remove golden particle when invisible role become invisible and config set to 1
    @EventHandler
    public void onInvisibleRemoveGoldenParticle(InvisibleEvent event) {

        if (game.getConfig().getGoldenAppleParticles() != 1) {
            return;
        }

        Player player = Bukkit.getPlayer(event.getPlayerWW().getUUID());

        if (player == null) {
            return;
        }

        event.getPlayerWW().getPotionModifiers()
                .forEach(potionModifier -> {
                    if (potionModifier.getPotionEffectType() == UniversalPotionEffectType.ABSORPTION) {
                        if (event.isInvisible()) {
                            player.removePotionEffect(UniversalPotionEffectType.ABSORPTION.getPotionEffectType());
                            player.addPotionEffect(new PotionEffect(UniversalPotionEffectType.ABSORPTION.getPotionEffectType(),
                                    potionModifier.getDuration() - (game.getTimer() - potionModifier.getTimer()) * 20,
                                    potionModifier.getAmplifier(), false, false));
                        } else {
                            player.removePotionEffect(UniversalPotionEffectType.ABSORPTION.getPotionEffectType());
                            player.addPotionEffect(new PotionEffect(UniversalPotionEffectType.ABSORPTION.getPotionEffectType(),
                                    potionModifier.getDuration() - (game.getTimer() - potionModifier.getTimer()) * 20,
                                    potionModifier.getAmplifier()));
                        }
                    }
                });


    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onResurrection(ResurrectionEvent event) {

        if (!(event.getPlayerWW().getRole() instanceof IInvisible)) {
            return;
        }

        if (event.getPlayerWW().getPotionModifiers()
                .stream()
                .noneMatch(potionModifier -> potionModifier.getPotionEffectType() == UniversalPotionEffectType.INVISIBILITY)) {
            return;
        }

        event.getPlayerWW().addPotionModifier(PotionModifier.remove(UniversalPotionEffectType.INVISIBILITY, event.getPlayerWW().getRole().getKey(), 0));

        ((IInvisible) event.getPlayerWW().getRole()).setInvisible(false);
    }

    @EventHandler
    public void onFinalDeath(DeathItemsEvent event) {

        if (!(event.getPlayerWW().getRole() instanceof IInvisible)) {
            return;
        }

        ((IInvisible) event.getPlayerWW().getRole()).setInvisible(false);

        if (!this.game.getConfig().isKnockBackForInvisibleRoleOnly()) return;

        for (ItemStack i : event.getItems()) {
            if (i != null) {
                i.removeEnchantment(UniversalEnchantment.KNOCKBACK.getEnchantment());
            }
        }
    }

    @EventHandler
    public void onStealEvent(StealEvent event) {

        if (!(event.getPlayerWW().getRole() instanceof IInvisible)) {
            return;
        }

        ((IInvisible) event.getPlayerWW().getRole()).setInvisible(false);
    }

    @EventHandler
    public void onGoldenAppleEat(GoldenAppleParticleEvent event) {

        if (!(event.getPlayerWW().getRole() instanceof IInvisible)) {
            return;
        }

        if (!((IInvisible) event.getPlayerWW().getRole()).isInvisible()) return;

        event.setCancelled(true);
    }
}
