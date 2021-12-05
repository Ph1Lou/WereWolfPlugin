package io.github.ph1lou.werewolfplugin.roles.werewolfs;

import fr.minuskube.inv.ClickableItem;
import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.Formatter;
import io.github.ph1lou.werewolfapi.IConfiguration;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.PotionModifier;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Day;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.UniversalMaterial;
import io.github.ph1lou.werewolfapi.events.UpdatePlayerNameTagEvent;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.AnnouncementDeathEvent;
import io.github.ph1lou.werewolfapi.events.game.vote.VoteEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.RoleWereWolf;
import io.github.ph1lou.werewolfapi.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class FearFulWerewolf extends RoleWereWolf {
    public FearFulWerewolf(WereWolfAPI game, IPlayerWW playerWW, String key) {
        super(game, playerWW, key);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game,this)
                .setDescription(game.translate("werewolf.role.fearful_werewolf.description",
                        Formatter.number(game.getConfig().getDistanceFearfulWerewolf())))
                .setEffects(game.translate("werewolf.role.fearful_werewolf.effects"))
                .build();
    }

    @Override
    public void recoverPower() {

    }

    @EventHandler
    public void onVote(VoteEvent event){
        if(!event.getPlayerWW().equals(this.getPlayerWW())){
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onDeath(AnnouncementDeathEvent event){
        if(!event.getPlayerWW().equals(this.getPlayerWW())){
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onUpdate(UpdatePlayerNameTagEvent event) {

        if(!event.getPlayerUUID().equals(this.getPlayerUUID())){
            return;
        }

        if(!this.getPlayerWW().isState(StatePlayer.DEATH)){
            return;
        }

        event.setTabVisibility(false);
    }

    @Override
    public void second() {

        if(!this.isAbilityEnabled()){
            return;
        }

        if(!this.getPlayerWW().isState(StatePlayer.ALIVE)){
            return;
        }

        long number = Bukkit.getOnlinePlayers()
                .stream()
                .map(Entity::getUniqueId)
                .filter(uuid -> !this.getPlayerUUID().equals(uuid))
                .map(game::getPlayerWW)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(playerWW -> playerWW.getRole().isWereWolf())
                .filter(iPlayerWW -> {
                    Location fearful = this.getPlayerWW().getLocation();
                    Location player = iPlayerWW.getLocation();
                    return (fearful.getWorld() == player.getWorld() &&
                    fearful.distance(player) < game.getConfig().getDistanceFearfulWerewolf());
                })
                .count();

        if(number == 0){
            this.getPlayerWW()
                    .addPotionModifier(PotionModifier.add(PotionEffectType.SPEED,
                            "fearful"));
        }
        else{
            this.getPlayerWW()
                    .addPotionModifier(PotionModifier.remove(PotionEffectType.SPEED,
                            "fearful",0));
        }

        if(number >= 4){
            this.getPlayerWW()
                    .addPotionModifier(PotionModifier.add(PotionEffectType.WEAKNESS,
                            "fearful"));
        }
        else{
            this.getPlayerWW()
                    .addPotionModifier(PotionModifier.remove(PotionEffectType.WEAKNESS,
                            "fearful",0));
        }

        if(number <= 2){
            if(game.isDay(Day.DAY)){
                this.getPlayerWW()
                        .addPotionModifier(PotionModifier.add(PotionEffectType.DAMAGE_RESISTANCE,
                                "fearful"));
                this.getPlayerWW()
                        .addPotionModifier(PotionModifier.remove(PotionEffectType.INCREASE_DAMAGE,
                                "werewolf"));
            }
            else{
                this.getPlayerWW()
                        .addPotionModifier(PotionModifier.add(PotionEffectType.INCREASE_DAMAGE,
                                "werewolf"));
                this.getPlayerWW()
                        .addPotionModifier(PotionModifier.remove(PotionEffectType.DAMAGE_RESISTANCE,
                                "fearful",0));
            }
        }
        else{
            this.getPlayerWW()
                    .addPotionModifier(PotionModifier.remove(PotionEffectType.INCREASE_DAMAGE,
                            "werewolf"));
            this.getPlayerWW()
                    .addPotionModifier(PotionModifier.remove(PotionEffectType.DAMAGE_RESISTANCE,
                            "fearful",0));
        }
    }

    public static ClickableItem config(WereWolfAPI game) {

        List<String> lore = Arrays.asList(game.translate("werewolf.menu.left"),
                game.translate("werewolf.menu.right"));
        IConfiguration config = game.getConfig();

        return ClickableItem.of((new ItemBuilder(
                UniversalMaterial.MAGENTA_WOOL.getStack())
                .setDisplayName(game.translate("werewolf.menu.advanced_tool.fearful_werewolf",
                        Formatter.number(config.getDistanceFearfulWerewolf())))
                .setLore(lore).build()), e -> {
            if (e.isLeftClick()) {
                config.setDistanceFearfulWerewolf((config.getDistanceFearfulWerewolf() + 2));
            } else if (config.getDistanceFearfulWerewolf() - 2 > 0) {
                config.setDistanceFearfulWerewolf(config.getDistanceFearfulWerewolf() - 2);
            }


            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setLore(lore)
                    .setDisplayName(game.translate("werewolf.menu.advanced_tool.fearful_werewolf",
                            Formatter.number(config.getDistanceFearfulWerewolf())))
                    .build());

        });
    }
}
