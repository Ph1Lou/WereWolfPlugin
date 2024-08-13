package fr.ph1lou.werewolfplugin.roles.neutrals;


import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.Day;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalEnchantment;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.NightEvent;
import fr.ph1lou.werewolfapi.events.game.utils.EnchantmentEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.role.impl.RoleNeutral;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import fr.ph1lou.werewolfapi.enums.UniversalPotionEffectType;
import org.jetbrains.annotations.NotNull;


@Role(key = RoleBase.ASSASSIN,
        defaultAura = Aura.DARK,
        category = Category.NEUTRAL,
        attribute = RoleAttribute.NEUTRAL)
public class Assassin extends RoleNeutral {

    public Assassin(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onNight(NightEvent event) {

        this.getPlayerWW()
                .addPotionModifier(PotionModifier.remove(UniversalPotionEffectType.STRENGTH, this.getKey(), 0));

    }

    @EventHandler
    public void onDay(DayEvent event) {

        if (!isAbilityEnabled()) return;

        this.getPlayerWW()
                .addPotionModifier(PotionModifier.add(UniversalPotionEffectType.STRENGTH, this.getKey()));

    }

    @EventHandler
    public void onEnchantment(EnchantmentEvent event) {

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (!event.getPlayerWW().equals(getPlayerWW())) return;

        ItemStack item = event.getItem();

        if (event.getEnchants().containsKey(UniversalEnchantment.PROTECTION)) {

            if (item.getType().equals(Material.DIAMOND_BOOTS) ||
                    item.getType().equals(Material.DIAMOND_LEGGINGS) ||
                    item.getType().equals(Material.DIAMOND_HELMET) ||
                    item.getType().equals(Material.DIAMOND_CHESTPLATE)) {
                event.getFinalEnchants().put(UniversalEnchantment.PROTECTION,
                        Math.min(event.getEnchants().get(UniversalEnchantment.PROTECTION),
                                game.getConfig().getLimitProtectionDiamond() + 1));
            } else {
                event.getFinalEnchants().put(
                        UniversalEnchantment.PROTECTION,
                        Math.min(event.getEnchants()
                                        .get(UniversalEnchantment.PROTECTION),
                                game.getConfig().getLimitProtectionIron() + 1));
            }
        }
        if (event.getEnchants().containsKey(UniversalEnchantment.SHARPNESS)) {
            if (item.getType().equals(Material.DIAMOND_SWORD)) {
                event.getFinalEnchants().put(UniversalEnchantment.SHARPNESS,
                        Math.min(event.getEnchants().get(UniversalEnchantment.SHARPNESS),
                                game.getConfig().getLimitSharpnessDiamond() + 1));
            } else {
                event.getFinalEnchants().put(UniversalEnchantment.SHARPNESS,
                        Math.min(event.getEnchants().get(UniversalEnchantment.SHARPNESS),
                                game.getConfig().getLimitSharpnessIron() + 1));
            }
        }
        if (event.getEnchants().containsKey(UniversalEnchantment.POWER)) {
            event.getFinalEnchants().put(UniversalEnchantment.POWER,
                    Math.min(event.getEnchants().get(UniversalEnchantment.POWER),
                            game.getConfig().getLimitPowerBow() + 1));
        }
    }

    @Override
    public @NotNull String getDescription() {

        return new DescriptionBuilder(game, this)
                .setEquipments(game.translate("werewolf.roles.assassin.limit"))
                .setItems(game.translate("werewolf.roles.assassin.items"))
                .setEffects(game.translate("werewolf.roles.assassin.effect"))
                .build();
    }


    @Override
    public void recoverPower() {

    }


    @Override
    public void recoverPotionEffect() {

        if (game.isDay(Day.NIGHT)) return;

        if (!isAbilityEnabled()) return;

        this.getPlayerWW().addPotionModifier(PotionModifier.add(UniversalPotionEffectType.STRENGTH, this.getKey()));
    }

    @Override
    public void disableAbilitiesRole() {

        this.getPlayerWW().addPotionModifier(PotionModifier.remove(UniversalPotionEffectType.STRENGTH, this.getKey(), 0));
    }

    @EventHandler
    public void onPlayerDeathByAssassin(PlayerDeathEvent event) {

        if (event.getEntity().getKiller() == null) return;

        Player killer = event.getEntity().getKiller();

        if(!killer.getUniqueId().equals(this.getPlayerUUID())){
            return;
        }

        getPlayerWW().addPotionModifier(PotionModifier.add(UniversalPotionEffectType.SPEED, 1200, 0, this.getKey()));
        getPlayerWW().addPotionModifier(PotionModifier.add(UniversalPotionEffectType.ABSORPTION, 1200, 0, this.getKey()));
    }
}
