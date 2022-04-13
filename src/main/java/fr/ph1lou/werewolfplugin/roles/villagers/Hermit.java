package fr.ph1lou.werewolfplugin.roles.villagers;

import fr.minuskube.inv.ClickableItem;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.game.IConfiguration;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.Day;
import fr.ph1lou.werewolfapi.enums.RolesBase;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.enums.UpdateCompositionReason;
import fr.ph1lou.werewolfapi.events.UpdatePlayerNameTagEvent;
import fr.ph1lou.werewolfapi.events.game.game_cycle.UpdateCompositionEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.AnnouncementDeathEvent;
import fr.ph1lou.werewolfapi.events.game.vote.VoteEvent;
import fr.ph1lou.werewolfapi.events.lovers.AnnouncementLoverDeathEvent;
import fr.ph1lou.werewolfapi.role.impl.RoleVillage;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Hermit extends RoleVillage {
    public Hermit(WereWolfAPI game, IPlayerWW playerWW, String key) {
        super(game, playerWW, key);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game,this)
                .setDescription(game.translate("werewolf.role.hermit.description",
                        Formatter.number(game.getConfig().getDistanceHermit())))
                .setEffects(game.translate("werewolf.role.hermit.effects"))
                .build();
    }

    @Override
    public void recoverPower() {

    }

    @Override
    public void disableAbilitiesRole() {

        this.getPlayerWW()
                .addPotionModifier(PotionModifier.remove(PotionEffectType.SPEED,
                        "hermit",0));
        this.getPlayerWW()
                .addPotionModifier(PotionModifier.remove(PotionEffectType.INCREASE_DAMAGE,
                        "hermit",0));
        this.getPlayerWW()
                .addPotionModifier(PotionModifier.remove(PotionEffectType.DAMAGE_RESISTANCE,
                        "hermit",0));
        this.getPlayerWW()
                .addPotionModifier(PotionModifier.remove(PotionEffectType.WEAKNESS,
                        "hermit",0));
    }

    @EventHandler
    public void onVote(VoteEvent event){
        if(!event.getPlayerWW().equals(this.getPlayerWW())){
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onLoverDeathMessage(AnnouncementLoverDeathEvent event){
        if(event.getPlayerWW().equals(this.getPlayerWW())){
            event.setCancelled(true);
        }
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

    @EventHandler
    public void onDeath(AnnouncementDeathEvent event){
        if(!event.getPlayerWW().equals(this.getPlayerWW())){
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onUpdateCompo(UpdateCompositionEvent event){

        if(event.getReason() != UpdateCompositionReason.DEATH){
            return;
        }

        if(!event.getKey().equals(RolesBase.HERMIT.getKey())){
            return;
        }

        event.setCancelled(true);
    }

    @Override
    public void second() {
        if(!this.getPlayerWW().isState(StatePlayer.ALIVE)){
            return;
        }
        if(!this.isAbilityEnabled()){
            return;
        }

        long number = Bukkit.getOnlinePlayers()
                .stream()
                .map(Entity::getUniqueId)
                .filter(uuid -> !this.getPlayerUUID().equals(uuid))
                .map(game::getPlayerWW)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .filter(iPlayerWW -> {
                    Location hermit = this.getPlayerWW().getLocation();
                    Location player = iPlayerWW.getLocation();
                    return (hermit.getWorld() == player.getWorld() &&
                    hermit.distance(player) < game.getConfig().getDistanceHermit());
                })
                .count();

        if(number == 0){
            this.getPlayerWW()
                    .addPotionModifier(PotionModifier.add(PotionEffectType.SPEED,
                            "hermit"));
        }
        else{
            this.getPlayerWW()
                    .addPotionModifier(PotionModifier.remove(PotionEffectType.SPEED,
                            "hermit",0));
        }

        if(number >= 5){
            this.getPlayerWW()
                    .addPotionModifier(PotionModifier.add(PotionEffectType.WEAKNESS,
                            "hermit"));

            this.getPlayerWW()
                    .addPotionModifier(PotionModifier.remove(PotionEffectType.INCREASE_DAMAGE,
                            "hermit",0));
            this.getPlayerWW()
                    .addPotionModifier(PotionModifier.remove(PotionEffectType.DAMAGE_RESISTANCE,
                            "hermit",0));
        }
        else{
            this.getPlayerWW()
                    .addPotionModifier(PotionModifier.remove(PotionEffectType.WEAKNESS,
                            "hermit",0));
            
            if(game.isDay(Day.DAY)){
                this.getPlayerWW()
                        .addPotionModifier(PotionModifier.remove(PotionEffectType.DAMAGE_RESISTANCE,
                                "hermit",0));
                this.getPlayerWW()
                        .addPotionModifier(PotionModifier.add(PotionEffectType.INCREASE_DAMAGE,
                                "hermit"));
            }
            else{
                this.getPlayerWW()
                        .addPotionModifier(PotionModifier.remove(PotionEffectType.INCREASE_DAMAGE,
                                "hermit",0));
                this.getPlayerWW()
                        .addPotionModifier(PotionModifier.add(PotionEffectType.DAMAGE_RESISTANCE,
                                "hermit"));
            }
        }
    }

    public static ClickableItem config(WereWolfAPI game) {

        List<String> lore = Arrays.asList(game.translate("werewolf.menu.left"),
                game.translate("werewolf.menu.right"));
        IConfiguration config = game.getConfig();

        return ClickableItem.of((new ItemBuilder(
                UniversalMaterial.WHITE_WOOL.getStack())
                .setDisplayName(game.translate("werewolf.menu.advanced_tool.hermit",
                        Formatter.number(config.getDistanceHermit())))
                .setLore(lore).build()), e -> {
            if (e.isLeftClick()) {
                config.setDistanceHermit((config.getDistanceHermit() + 2));
            } else if (config.getDistanceHermit() - 2 > 0) {
                config.setDistanceHermit(config.getDistanceHermit() - 2);
            }


            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setLore(lore)
                    .setDisplayName(game.translate("werewolf.menu.advanced_tool.hermit",
                            Formatter.number(config.getDistanceHermit())))
                    .build());

        });
    }
}
