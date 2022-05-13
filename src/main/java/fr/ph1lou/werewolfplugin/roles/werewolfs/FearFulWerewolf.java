package fr.ph1lou.werewolfplugin.roles.werewolfs;

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
import fr.ph1lou.werewolfapi.role.impl.RoleWereWolf;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@Role(key = RoleBase.FEARFUL_WEREWOLF, 
        category = Category.WEREWOLF, 
        attributes = RoleAttribute.WEREWOLF,
        configValues = @IntValue(key = FearFulWerewolf.DISTANCE,
        defaultValue = 20, meetUpValue = 20, step = 4, item = UniversalMaterial.MAGENTA_WOOL))
public class FearFulWerewolf extends RoleWereWolf {
    public FearFulWerewolf(WereWolfAPI game, IPlayerWW playerWW) {
        super(game, playerWW);
    }

    public static final String DISTANCE = "werewolf.role.fearful_werewolf.distance";

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game,this)
                .setDescription(game.translate("werewolf.role.fearful_werewolf.description",
                        Formatter.number(game.getConfig().getValue(DISTANCE))))
                .setEffects(game.translate("werewolf.role.fearful_werewolf.effects"))
                .build();
    }

    @Override
    public void recoverPower() {

    }

    @EventHandler
    public void onUpdateCompo(UpdateCompositionEvent event){

        if(event.getReason() != UpdateCompositionReason.DEATH){
            return;
        }

        if(!event.getKey().equals(RoleBase.FEARFUL_WEREWOLF)){
            return;
        }

        event.setCancelled(true);
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

    @EventHandler
    public void onLoverDeathMessage(AnnouncementLoverDeathEvent event){
        if(event.getPlayerWW().equals(this.getPlayerWW())){
            event.setCancelled(true);
        }
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
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .filter(playerWW -> playerWW.getRole().isWereWolf())
                .filter(iPlayerWW -> {
                    Location fearful = this.getPlayerWW().getLocation();
                    Location player = iPlayerWW.getLocation();
                    return (fearful.getWorld() == player.getWorld() &&
                    fearful.distance(player) < game.getConfig().getValue(DISTANCE));
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
                                "werewolf",0));
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
                            "werewolf",0));
            this.getPlayerWW()
                    .addPotionModifier(PotionModifier.remove(PotionEffectType.DAMAGE_RESISTANCE,
                            "fearful",0));
        }
    }

    @Override
    public void disableAbilitiesRole() {

        this.getPlayerWW()
                .addPotionModifier(PotionModifier.remove(PotionEffectType.SPEED,
                        "fearful",0));
        this.getPlayerWW()
                .addPotionModifier(PotionModifier.remove(PotionEffectType.DAMAGE_RESISTANCE,
                        "fearful",0));
        this.getPlayerWW()
                .addPotionModifier(PotionModifier.remove(PotionEffectType.WEAKNESS,
                        "fearful",0));
    }
}
