package fr.ph1lou.werewolfplugin.roles.neutrals;


import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Day;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.NightEvent;
import fr.ph1lou.werewolfapi.events.game.utils.EnchantmentEvent;
import fr.ph1lou.werewolfapi.role.impl.RoleNeutral;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;


@Role(key = RoleBase.ASSASSIN,
        category = Category.NEUTRAL,
        attributes = RoleAttribute.NEUTRAL)
public class Assassin extends RoleNeutral {

    public Assassin(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onNight(NightEvent event) {

        this.getPlayerWW()
                .addPotionModifier(PotionModifier.remove(PotionEffectType.INCREASE_DAMAGE,"assassin",0));

    }

    @EventHandler
    public void onDay(DayEvent event) {

        if (!isAbilityEnabled()) return;

        this.getPlayerWW()
                .addPotionModifier(PotionModifier.add(PotionEffectType.INCREASE_DAMAGE,"assassin"));

    }

    @EventHandler
    public void onEnchantment(EnchantmentEvent event){

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (!event.getPlayerWW().equals(getPlayerWW())) return;

        ItemStack item = event.getItem();

        if (event.getEnchants().containsKey(Enchantment.PROTECTION_ENVIRONMENTAL)) {

            if (item.getType().equals(Material.DIAMOND_BOOTS) ||
                    item.getType().equals(Material.DIAMOND_LEGGINGS) ||
                    item.getType().equals(Material.DIAMOND_HELMET) ||
                    item.getType().equals(Material.DIAMOND_CHESTPLATE)) {
                event.getFinalEnchants().put(Enchantment.PROTECTION_ENVIRONMENTAL,
                        Math.min(event.getEnchants().get(Enchantment.PROTECTION_ENVIRONMENTAL),
                                game.getConfig().getLimitProtectionDiamond() + 1));
            } else {
                event.getFinalEnchants().put(
                        Enchantment.PROTECTION_ENVIRONMENTAL,
                        Math.min(event.getEnchants()
                                        .get(Enchantment.PROTECTION_ENVIRONMENTAL),
                                game.getConfig().getLimitProtectionIron() + 1));
            }
        }
        if(event.getEnchants().containsKey(Enchantment.DAMAGE_ALL)){
            if (item.getType().equals(Material.DIAMOND_SWORD)) {
                event.getFinalEnchants().put(Enchantment.DAMAGE_ALL,
                        Math.min(event.getEnchants().get(Enchantment.DAMAGE_ALL),
                                game.getConfig().getLimitSharpnessDiamond() + 1));
            }
            else {
                event.getFinalEnchants().put(Enchantment.DAMAGE_ALL,
                        Math.min(event.getEnchants().get(Enchantment.DAMAGE_ALL),
                                game.getConfig().getLimitSharpnessIron() + 1));
            }
        }
        if(event.getEnchants().containsKey(Enchantment.ARROW_DAMAGE)) {
            event.getFinalEnchants().put(Enchantment.ARROW_DAMAGE,
                    Math.min(event.getEnchants().get(Enchantment.ARROW_DAMAGE),
                            game.getConfig().getLimitPowerBow() + 1));
        }
    }

    @Override
    public @NotNull String getDescription() {

        return new DescriptionBuilder(game, this)
                .setEquipments(game.translate("werewolf.role.assassin.limit"))
                .setItems(game.translate("werewolf.role.assassin.items"))
                .setEffects(game.translate("werewolf.role.assassin.effect"))
                .build();
    }


    @Override
    public void recoverPower() {

    }


    @Override
    public void recoverPotionEffect() {

        if (game.isDay(Day.NIGHT)) return;

        if (!isAbilityEnabled()) return;

        this.getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.INCREASE_DAMAGE,"assassin"));
    }

    @Override
    public Aura getDefaultAura() {
        return Aura.DARK;
    }

    @Override
    public void disableAbilitiesRole() {

        this.getPlayerWW().addPotionModifier(PotionModifier.remove(PotionEffectType.INCREASE_DAMAGE,"assassin",0));
    }
}
