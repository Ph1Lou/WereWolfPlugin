package fr.ph1lou.werewolfplugin.roles.villagers;

import fr.minuskube.inv.ClickableItem;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Camp;
import fr.ph1lou.werewolfapi.enums.Prefix;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.werewolf.WereWolfKillEvent;
import fr.ph1lou.werewolfapi.game.IConfiguration;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleVillage;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

/**
 * @author havwila
 */

public class Hunter extends RoleVillage implements IPower {

    private boolean power = false;
    private double damageBonus = 0;

    public Hunter(WereWolfAPI game, IPlayerWW playerWW, String key) {
        super(game, playerWW, key);
    }

    @Override
    public @NotNull String getDescription() {
        DescriptionBuilder descBuilder = new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.role.hunter.description"))
                .setItems(game.translate("werewolf.role.hunter.items"))
                .setEffects(game.translate("werewolf.role.hunter.effect", Formatter.format("&number&", 0.5 + damageBonus)));
        if (game.getConfig().isConfigActive("werewolf.role.hunter.can_shoot")) {
            descBuilder = descBuilder.addExtraLines(game.translate("werewolf.role.hunter.description_shoot"));
        }
        return descBuilder.build();
    }

    @Override
    public void recoverPower() {

    }

    @Override
    public Aura getDefaultAura() {
        return Aura.NEUTRAL;
    }

    @EventHandler
    public void onFinalDeathEvent(FinalDeathEvent event) {
        IPlayerWW playerWW = event.getPlayerWW();

        if (playerWW.equals(this.getPlayerWW())) {
            if (game.getConfig().isConfigActive("werewolf.role.hunter.can_shoot")) {
                this.setPower(true);
                getPlayerWW().sendMessageWithKey(Prefix.YELLOW.getKey(), "werewolf.role.hunter.perform");
                BukkitUtils.scheduleSyncDelayedTask(() -> {
                    getPlayerWW().sendMessageWithKey(Prefix.YELLOW.getKey(), "werewolf.check.end_selection");
                    setPower(false);
                }, 20 * 30);
            }
            return;
        }

        if (!playerWW.getRole().isWereWolf()) return;

        if (playerWW.getLastKiller().isPresent() && playerWW.getLastKiller().get().equals(getPlayerWW())) {
            damageBonus += 0.1;
            getPlayerWW().sendMessageWithKey(Prefix.YELLOW.getKey(), "werewolf.role.hunter.werewolf_slain");
        }
    }

    @EventHandler
    public void onPlayerdamage(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof Player)) return;

        if (!(event.getDamager() instanceof Player)) return;

        if (!isAbilityEnabled()) return;

        Player damager = (Player) event.getDamager();
        IPlayerWW damagerWW = game.getPlayerWW(damager.getUniqueId()).orElse(null);
        //also handles case damagerWW == null
        if (!damagerWW.equals(getPlayerWW())) return;

        if (damager.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) return;

        Player target = (Player) event.getEntity();
        IPlayerWW targetWW = game.getPlayerWW(target.getUniqueId()).orElse(null);
        if (targetWW == null || !targetWW.getRole().isWereWolf()) return;

        event.setDamage(event.getDamage() * (1 + (game.getConfig().getStrengthRate()/100f) * (0.5 + damageBonus)));
    }

    @EventHandler
    public void onWerewolfKillEvent(WereWolfKillEvent event) {
        if (event.getVictimWW().equals(getPlayerWW())) {
            event.setCancelled(true);
        }
    }

    public static ClickableItem configCanShoot(WereWolfAPI game) {
        IConfiguration config = game.getConfig();

        return ClickableItem.of(new ItemBuilder(Material.BOW)
                .setLore(game.translate(
                        config.isConfigActive("werewolf.role.hunter.can_shoot") ? "werewolf.utils.enable" : "werewolf.utils.disable"))
                .setDisplayName(game.translate("werewolf.role.hunter.can_shoot"))
                .build(), e -> {
            config.setConfig("werewolf.role.hunter.can_shoot", !config.isConfigActive("werewolf.role.hunter.can_shoot"));

            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setLore(game.translate(config.isConfigActive("werewolf.role.hunter.can_shoot") ? "werewolf.utils.enable" : "werewolf.utils.disable"))
                    .build());
        });
    }

    @Override
    public void setPower(boolean b) {
        this.power = b;
    }

    @Override
    public boolean hasPower() {
        return power;
    }
}
