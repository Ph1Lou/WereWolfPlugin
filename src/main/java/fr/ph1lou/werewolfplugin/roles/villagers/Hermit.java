package fr.ph1lou.werewolfplugin.roles.villagers;

import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.Day;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.enums.UpdateCompositionReason;
import fr.ph1lou.werewolfapi.events.UpdatePlayerNameTagEvent;
import fr.ph1lou.werewolfapi.events.game.game_cycle.UpdateCompositionEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.AnnouncementDeathEvent;
import fr.ph1lou.werewolfapi.events.game.vote.VoteEvent;
import fr.ph1lou.werewolfapi.events.lovers.AnnouncementLoverDeathEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleVillage;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;


@Role(key = RoleBase.HERMIT, 
        category = Category.VILLAGER, attributes = RoleAttribute.VILLAGER,
        intValues = @IntValue(key = Hermit.DISTANCE, defaultValue = 20,
                meetUpValue = 20, step = 4, item = UniversalMaterial.WHITE_WOOL))
public class Hermit extends RoleVillage {

    public static final String DISTANCE = "werewolf.role.hermit.distance";
    public static final String POTION = "hermit";

    public Hermit(WereWolfAPI game, IPlayerWW playerWW) {
        super(game, playerWW);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game,this)
                .setDescription(game.translate("werewolf.role.hermit.description",
                        Formatter.number(game.getConfig().getValue(RoleBase.HERMIT, DISTANCE))))
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
                        POTION,0));
        this.getPlayerWW()
                .addPotionModifier(PotionModifier.remove(PotionEffectType.INCREASE_DAMAGE,
                        POTION,0));
        this.getPlayerWW()
                .addPotionModifier(PotionModifier.remove(PotionEffectType.DAMAGE_RESISTANCE,
                        POTION,0));
        this.getPlayerWW()
                .addPotionModifier(PotionModifier.remove(PotionEffectType.WEAKNESS,
                        POTION,0));
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

        if(!event.getKey().equals(RoleBase.HERMIT)){
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
                    hermit.distance(player) < game.getConfig().getValue(RoleBase.HERMIT, DISTANCE));
                })
                .count();

        if(number == 0){
            this.getPlayerWW()
                    .addPotionModifier(PotionModifier.add(PotionEffectType.SPEED,
                            POTION));
        }
        else{
            this.getPlayerWW()
                    .addPotionModifier(PotionModifier.remove(PotionEffectType.SPEED,
                            POTION,0));
        }

        if(number >= 5){
            this.getPlayerWW()
                    .addPotionModifier(PotionModifier.add(PotionEffectType.WEAKNESS,
                            POTION));

            this.getPlayerWW()
                    .addPotionModifier(PotionModifier.remove(PotionEffectType.INCREASE_DAMAGE,
                            POTION,0));
            this.getPlayerWW()
                    .addPotionModifier(PotionModifier.remove(PotionEffectType.DAMAGE_RESISTANCE,
                            POTION,0));
        }
        else{
            this.getPlayerWW()
                    .addPotionModifier(PotionModifier.remove(PotionEffectType.WEAKNESS,
                            POTION,0));
            
            if(game.isDay(Day.DAY)){
                this.getPlayerWW()
                        .addPotionModifier(PotionModifier.remove(PotionEffectType.DAMAGE_RESISTANCE,
                                POTION,0));
                this.getPlayerWW()
                        .addPotionModifier(PotionModifier.add(PotionEffectType.INCREASE_DAMAGE,
                                POTION));
            }
            else{
                this.getPlayerWW()
                        .addPotionModifier(PotionModifier.remove(PotionEffectType.INCREASE_DAMAGE,
                                POTION,0));
                this.getPlayerWW()
                        .addPotionModifier(PotionModifier.add(PotionEffectType.DAMAGE_RESISTANCE,
                                POTION));
            }
        }
    }
}
