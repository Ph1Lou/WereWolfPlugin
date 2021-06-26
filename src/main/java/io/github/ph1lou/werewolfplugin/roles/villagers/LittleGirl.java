package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.PotionModifier;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Day;
import io.github.ph1lou.werewolfapi.enums.StateGame;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import io.github.ph1lou.werewolfapi.events.UpdatePlayerNameTagEvent;
import io.github.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import io.github.ph1lou.werewolfapi.events.game.day_cycle.DayWillComeEvent;
import io.github.ph1lou.werewolfapi.events.game.day_cycle.NightEvent;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.ResurrectionEvent;
import io.github.ph1lou.werewolfapi.events.game.utils.EnchantmentEvent;
import io.github.ph1lou.werewolfapi.events.game.utils.GoldenAppleParticleEvent;
import io.github.ph1lou.werewolfapi.events.roles.InvisibleEvent;
import io.github.ph1lou.werewolfapi.events.roles.StealEvent;
import io.github.ph1lou.werewolfapi.events.werewolf.WereWolfChatEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IInvisible;
import io.github.ph1lou.werewolfapi.rolesattributs.IRole;
import io.github.ph1lou.werewolfapi.rolesattributs.RoleVillage;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.javatuples.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class LittleGirl extends RoleVillage implements IInvisible {

    private boolean invisible = false;

    public LittleGirl(WereWolfAPI api, IPlayerWW playerWW, String key) {
        super(api, playerWW, key);
    }


    @EventHandler
    public void onNight(NightEvent event) {

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        this.getPlayerWW().sendMessageWithKey(
                "werewolf.role.little_girl.remove_armor");
    }

    @EventHandler
    public void onDay(DayEvent event) {

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }
        if (!this.isInvisible()) return;

        this.getPlayerWW().addPotionModifier(PotionModifier.remove(PotionEffectType.INVISIBILITY,"little_girl"));

        this.setInvisible(false);
        Bukkit.getPluginManager().callEvent(
                new InvisibleEvent(this.getPlayerWW(),
                        false));
        Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(this.getPlayerWW()));
        this.getPlayerWW().sendMessageWithKey("werewolf.role.little_girl.visible");
    }

    @EventHandler
    public void onUpdateNameTag(UpdatePlayerNameTagEvent event) {

        if (!event.getPlayerUUID().equals(this.getPlayerUUID())) return;

        if (event.isVisibility()) {
            event.setVisibility(!this.invisible);
        }
    }

    @EventHandler
    public void onGoldenAppleEat(GoldenAppleParticleEvent event) {

        if (!event.getPlayerWW().equals(this.getPlayerWW())) return;

        if (!this.isInvisible()) return;

        if (this.game.isDay(Day.DAY)) return;

        event.setCancelled(true);
    }


    @EventHandler
    public void onDayWillCome(DayWillComeEvent event) {


        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        this.getPlayerWW().sendMessageWithKey(
                "werewolf.role.little_girl.soon_to_be_day");
    }

    @Override
    public void second() {

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) {
            return;
        }

        if (!this.game.isDay(Day.NIGHT)) {
            return;
        }

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (!this.isInvisible()) {
            return;
        }

        game.getPlayersWW()
                .stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .map(IPlayerWW::getRole)
                .filter(roles -> !roles.equals(this))
                .filter(roles -> roles instanceof IInvisible)
                .map(roles -> (IInvisible) roles)
                .filter(IInvisible::isInvisible)
                .map(IInvisible -> {
                    if (((IRole) IInvisible).isWereWolf()) {
                        return new Pair<>(Material.REDSTONE_BLOCK,
                                ((IRole) IInvisible).getPlayerUUID());
                    } else return new Pair<>(Material.LAPIS_BLOCK,
                            ((IRole) IInvisible).getPlayerUUID());
                })
                .map(objects -> new Pair<>(objects.getValue0(),
                        Bukkit.getPlayer(objects.getValue1())))
                .filter(objects -> objects.getValue1() != null)
                .map(objects -> new Pair<>(objects.getValue0(),
                        objects.getValue1().getLocation()))
                .forEach(objects -> player.playEffect(objects.getValue1(),
                        Effect.STEP_SOUND, objects.getValue0()));

    }


    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.role.little_girl.description"))
                .setItems(game.translate("werewolf.role.little_girl.item"))
                .setEffects(game.translate("werewolf.role.little_girl.effect"))
                .build();
    }


    @EventHandler
    public void onStealEvent(StealEvent event) {

        if (!event.getThiefWW().equals(getPlayerWW())) return;

        this.setInvisible(false);
    }

    @Override
    public void recoverPower() {

    }

    @Override
    public boolean isInvisible() {
        return this.invisible;
    }

    @Override
    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
    }

    @Override
    public void recoverPotionEffect() {

        this.getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.NIGHT_VISION,"little_girl"));
    }

    @EventHandler
    public void onFinalDeath(FinalDeathEvent event) {

        if (!event.getPlayerWW().equals(this.getPlayerWW())) return;

        this.setInvisible(false);

        if (this.game.getConfig().getLimitKnockBack() == 2) return;

        for (ItemStack i : this.getPlayerWW().getItemDeath()) {
            if (i != null) {
                i.removeEnchantment(Enchantment.KNOCKBACK);
            }
        }
    }

    @EventHandler
    public void onEnchantment(EnchantmentEvent event){

        if (!event.getPlayerWW().equals(getPlayerWW())) return;

        if (event.getEnchants().containsKey(Enchantment.KNOCKBACK)) {
            event.getFinalEnchants().put(Enchantment.KNOCKBACK,
                    Math.min(event.getEnchants().get(Enchantment.KNOCKBACK),
                            game.getConfig().getLimitKnockBack()));
        }
    }

    @EventHandler
    private void onCloseInvent(InventoryCloseEvent event) {

        Player player = (Player) event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!uuid.equals(this.getPlayerUUID())) return;

        Inventory inventory = player.getInventory();

        if (!this.game.isState(StateGame.GAME)) return;
        if (!this.game.isDay(Day.NIGHT)) return;

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (inventory.getItem(36) == null &&
                inventory.getItem(37) == null &&
                inventory.getItem(38) == null &&
                inventory.getItem(39) == null) {
            if (!this.isInvisible()) {
                player.sendMessage(game.translate("werewolf.role.little_girl.remove_armor_perform"));
                this.getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.INVISIBILITY,"little_girl"));

                if (getInfected()) {
                    this.getPlayerWW().addPotionModifier(PotionModifier.remove(PotionEffectType.INCREASE_DAMAGE,"werewolf"));

                }
                this.setInvisible(true);
                Bukkit.getPluginManager().callEvent(new InvisibleEvent(this.getPlayerWW(), true));
                Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(this.getPlayerWW()));
            }
        } else if (this.isInvisible()) {
            player.sendMessage(game.translate(
                    "werewolf.role.little_girl.visible"));
            if (this.getInfected()) {
                this.getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.INCREASE_DAMAGE,"werewolf"));
            }
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
            this.setInvisible(false);
            Bukkit.getPluginManager().callEvent(
                    new InvisibleEvent(this.getPlayerWW(), false));
            Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(this.getPlayerWW()));
        }
    }

    @EventHandler
    public void onWWChatLittleGirl(WereWolfChatEvent event) {

        if (event.isCancelled()) return;

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (isWereWolf()) { //pour éviter qu'elle ait le message en double
            return;
        }

        event.sendMessage(this.getPlayerWW());

    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onResurrection(ResurrectionEvent event) {

        if (!event.getPlayerWW().equals(getPlayerWW())) return;

        this.setInvisible(false);
    }

}
