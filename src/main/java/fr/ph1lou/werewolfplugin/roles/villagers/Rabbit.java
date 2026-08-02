package fr.ph1lou.werewolfplugin.roles.villagers;

import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.RequireAbility;
import fr.ph1lou.werewolfapi.annotations.RequireAlive;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.Day;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.enums.UniversalPotionEffectType;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.NightEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleImpl;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;


@Role(key = RoleBase.RABBIT,
        category = Category.VILLAGER,
        attribute = RoleAttribute.VILLAGER,
        configValues = {@IntValue(key = IntValueBase.RABBIT_LOW_HEALTH,
                defaultValue = 6, meetUpValue = 6, step = 1, item = UniversalMaterial.RED_DYE),
                @IntValue(key = IntValueBase.RABBIT_HIGH_HEALTH,
                        defaultValue = 12, meetUpValue = 12, step = 1, item = UniversalMaterial.GOLDEN_APPLE),
                @IntValue(key = IntValueBase.RABBIT_ALLY_RANGE,
                        defaultValue = 20, meetUpValue = 20, step = 1, item = UniversalMaterial.COMPASS),
                @IntValue(key = IntValueBase.RABBIT_ALLY_REDUCTION,
                        defaultValue = 10, meetUpValue = 10, step = 5, item = UniversalMaterial.SHIELD),
                @IntValue(key = IntValueBase.RABBIT_LOW_SPEED,
                        defaultValue = 120, meetUpValue = 120, step = 5, item = UniversalMaterial.SUGAR_CANE_BLOCK),
                @IntValue(key = IntValueBase.RABBIT_NORMAL_SPEED,
                        defaultValue = 110, meetUpValue = 110, step = 5, item = UniversalMaterial.SUGAR_CANE_BLOCK)})
public class Rabbit extends RoleImpl {

    private static final float defaultWalkSpeed = 0.2f;
    private boolean belowHearts = false;//pr éviter de reset la speed quand ya pas de changement de vie

    public Rabbit(WereWolfAPI game, IPlayerWW playerWW) {
        super(game, playerWW);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(this.game, this)
                .setDescription(this.game.translate("werewolf.roles.rabbit.description",
                        Formatter.number(this.game.getConfig().getValue(IntValueBase.RABBIT_HIGH_HEALTH)),
                        Formatter.format("&number1&", this.game.getConfig().getValue(IntValueBase.RABBIT_ALLY_RANGE)),
                        Formatter.format("&number2&", this.game.getConfig().getValue(IntValueBase.RABBIT_ALLY_REDUCTION))))
                .setEffects(this.game.translate("werewolf.roles.rabbit.effects"))
                .build();
    }

    @Override
    public void second() {

        if (!this.isAbilityEnabled()) return;

        Player player = Bukkit.getPlayer(this.getPlayerUUID());

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE) || player == null) return;

        boolean belowHearts = this.getPlayerWW().getHealth() < game.getConfig().getValue(IntValueBase.RABBIT_LOW_HEALTH);

        if (this.belowHearts == belowHearts) return;

        player.setWalkSpeed(defaultWalkSpeed * (this.belowHearts ? game.getConfig().getValue(IntValueBase.RABBIT_LOW_SPEED) / 100f : game.getConfig().getValue(IntValueBase.RABBIT_NORMAL_SPEED) / 100f));
        this.belowHearts = belowHearts;
    }

    @EventHandler
    @RequireAbility
    @RequireAlive
    protected void onCheckReduceDamage(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof Player)) return;

        if (this.getPlayerWW().getHealth() >= game.getConfig().getValue(IntValueBase.RABBIT_HIGH_HEALTH)) {
            return;
        }

        IPlayerWW victimWW = this.game.getPlayerWW(event.getEntity().getUniqueId()).orElse(null);

        if (victimWW == null || !victimWW.isState(StatePlayer.ALIVE)) {
            return;
        }

        if (victimWW.getRole().isNeutral() || victimWW.getRole().isWereWolf()) {
            return;
        }

        if (victimWW.distance(this.getPlayerWW()) > game.getConfig().getValue(IntValueBase.RABBIT_ALLY_RANGE)) {
            return;
        }

        if (victimWW.getPotionModifiers()
                .stream()
                .anyMatch(p -> p.getPotionEffectType() == UniversalPotionEffectType.RESISTANCE)) {
            return;
        }

        event.setDamage(event.getDamage() * (100 - game.getConfig().getValue(IntValueBase.RABBIT_ALLY_REDUCTION)) / 100.0);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    @RequireAbility
    public void onNight(NightEvent event) {

        this.getPlayerWW().addPotionModifier(PotionModifier.add(UniversalPotionEffectType.WEAKNESS, this.getKey()));
    }

    @EventHandler
    public void onDay(DayEvent event) {
        this.getPlayerWW().addPotionModifier(PotionModifier.remove(UniversalPotionEffectType.WEAKNESS, this.getKey(), 0));
    }

    @Override
    public void recoverPower() {

    }

    @Override
    public void recoverPotionEffect() {

        if (!this.isAbilityEnabled()) return;

        if (game.isDay(Day.DAY)) {
            return;
        }

        this.getPlayerWW().addPotionModifier(PotionModifier.add(UniversalPotionEffectType.WEAKNESS, this.getKey()));
    }
}