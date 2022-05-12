package fr.ph1lou.werewolfplugin.roles.villagers;

import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.Day;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.NightEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.role.impl.RoleVillage;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;


@Role(key = RoleBase.RABBIT,
        category = Category.VILLAGER,
        attributes = {RoleAttribute.VILLAGER})
public class Rabbit extends RoleVillage {

    private static final float defaultWalkSpeed = 0.2f;
    private boolean belowHearts = false;//pr éviter de reset la speed quand ya pas de changement de vie

    public Rabbit(WereWolfAPI game, IPlayerWW playerWW) {
        super(game, playerWW);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(this.game, this)
                .setDescription(this.game.translate("werewolf.role.rabbit.description"))
                .setEffects(this.game.translate("werewolf.role.rabbit.effects"))
                .build();
    }

    @Override
    public void second() {

        if(!this.isAbilityEnabled()) return;

        Player player = Bukkit.getPlayer(this.getPlayerUUID());

        if(!this.getPlayerWW().isState(StatePlayer.ALIVE) || player == null) return;

        boolean belowHearts = player.getHealth() < 6;

        if(this.belowHearts == belowHearts) return;

        player.setWalkSpeed(defaultWalkSpeed * (this.belowHearts ? 1.2f : 1.1f));
        this.belowHearts = belowHearts;
    }

    @EventHandler
    protected void onCheckReduceDamage(EntityDamageByEntityEvent event) {

        if(!this.isAbilityEnabled()) return;

        if(!(event.getEntity() instanceof Player)) return;

        Player player = Bukkit.getPlayer(this.getPlayerUUID());

        if(!this.getPlayerWW().isState(StatePlayer.ALIVE) || player == null) {
            return;
        }

        if(player.getHealth() >= 12) {
            return;
        }

        IPlayerWW victimWW = this.game.getPlayerWW(event.getEntity().getUniqueId()).orElse(null);

        if(victimWW == null || !victimWW.isState(StatePlayer.ALIVE)) {
            return;
        }

        if(victimWW.getRole().isNeutral() || victimWW.getRole().isWereWolf()){
            return;
        }

        if(victimWW.getLocation().getWorld() != this.getPlayerWW().getLocation().getWorld()) {
            return;
        }

        if(victimWW.getLocation().distance(this.getPlayerWW().getLocation()) > 20) {
            return;
        }

        if(victimWW.getPotionModifiers()
                .stream()
                .anyMatch(p -> p.getPotionEffectType() == PotionEffectType.DAMAGE_RESISTANCE)) {
            return;
        }

        event.setDamage(event.getDamage() * 0.9);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onNight(NightEvent event) {

        if (!this.isAbilityEnabled()) return;

        this.getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.WEAKNESS,"rabbit"));
    }

    @EventHandler
    public void onDay(DayEvent event) {
        this.getPlayerWW().addPotionModifier(PotionModifier.remove(PotionEffectType.WEAKNESS,"rabbit", 0));
    }

    @Override
    public void recoverPower() {

    }

    @Override
    public void recoverPotionEffect() {

        if (!this.isAbilityEnabled()) return;

        if(game.isDay(Day.DAY)){
            return;
        }

        this.getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.WEAKNESS,"rabbit"));
    }
}