package fr.ph1lou.werewolfplugin.roles.villagers;

import fr.ph1lou.werewolfapi.annotations.Configuration;
import fr.ph1lou.werewolfapi.annotations.ConfigurationBasic;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.werewolf.WereWolfKillEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleVillage;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

/**
 * @author havwila
 */
@Role(key = RoleBase.HUNTER,
        category = Category.VILLAGER,
        attributes = RoleAttribute.VILLAGER,
        configurations = @Configuration(config = @ConfigurationBasic(key = ConfigBase.HUNTER_CAN_SHOOT, meetUpValue = true)))
public class Hunter extends RoleVillage implements IPower {

    private boolean power = false;
    private double damageBonus = 0;

    public Hunter(WereWolfAPI game, IPlayerWW playerWW) {
        super(game, playerWW);
    }

    @Override
    public @NotNull String getDescription() {
        DescriptionBuilder descBuilder = new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.hunter.description"))
                .setItems(game.translate("werewolf.roles.hunter.items"))
                .setEffects(game.translate("werewolf.roles.hunter.effect", Formatter.format("&number&", 0.5 + damageBonus)));
        if (game.getConfig().isConfigActive(ConfigBase.HUNTER_CAN_SHOOT)) {
            descBuilder = descBuilder.addExtraLines(game.translate("werewolf.roles.hunter.description_shoot"));
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
            if (game.getConfig().isConfigActive(ConfigBase.HUNTER_CAN_SHOOT)) {
                this.setPower(true);
                getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.hunter.perform");
                BukkitUtils.scheduleSyncDelayedTask(game, () -> {
                    getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.check.end_selection");
                    setPower(false);
                }, 20 * 30);
            }
            return;
        }

        if (!playerWW.getRole().isWereWolf()) return;

        if (playerWW.getLastKiller().isPresent() && playerWW.getLastKiller().get().equals(getPlayerWW())) {
            damageBonus += 0.1;
            getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.hunter.werewolf_slain");
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
        if (!getPlayerWW().equals(damagerWW)) return;

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

    @Override
    public void setPower(boolean b) {
        this.power = b;
    }

    @Override
    public boolean hasPower() {
        return power;
    }
}
