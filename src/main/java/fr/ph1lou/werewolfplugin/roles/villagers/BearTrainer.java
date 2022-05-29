package fr.ph1lou.werewolfplugin.roles.villagers;

import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Camp;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Sound;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.roles.bear_trainer.GrowlEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.AuraModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleVillage;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Role(key = RoleBase.BEAR_TRAINER,
        category = Category.VILLAGER,
        attributes = {RoleAttribute.VILLAGER,
                RoleAttribute.INFORMATION},
        configValues = {@IntValue(key = IntValueBase.BEAR_TRAINER_DISTANCE,
                defaultValue = 50,
                meetUpValue = 50,
                step = 5,
                item = UniversalMaterial.BROWN_WOOL)})
public class BearTrainer extends RoleVillage {

    public BearTrainer(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
    }

    @EventHandler
    public void onDay(DayEvent event) {

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }
        if (player == null) return;

        if (!isAbilityEnabled()) return;

        Location oursLocation = player.getLocation();
        Set<IPlayerWW> growled = Bukkit.getOnlinePlayers()
                .stream()
                .filter(player1 -> player1.getWorld().equals(oursLocation.getWorld())
                        && oursLocation.distance(player1.getLocation())
                        < game.getConfig().getValue(IntValueBase.BEAR_TRAINER_DISTANCE))
                .map(Entity::getUniqueId)
                .map(game::getPlayerWW)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .map(IPlayerWW::getRole)
                .filter(roles -> roles.isDisplayCamp(Camp.WEREWOLF.getKey()) || (roles.getDisplayCamp().equals(roles.getCamp().getKey()) && roles.isWereWolf()))
                .map(IRole::getPlayerWW)
                .collect(Collectors.toSet());

        GrowlEvent growlEvent = new GrowlEvent(this.getPlayerWW(), growled);
        Bukkit.getPluginManager().callEvent(growlEvent);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onGrowl(GrowlEvent event) {

        if (event.getPlayerWWS().isEmpty()) {
            event.setCancelled(true);
            return;
        }

        if (!event.getPlayerWW().equals(getPlayerWW())) return;

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) return;

        if (event.isCancelled()) {
            player.sendMessage(game.translate(Prefix.RED , "werewolf.check.cancel"));
            return;
        }

        String builder = event.getPlayerWWS().stream().map(ignored ->
                game.translate("werewolf.roles.bear_trainer.growling"))
                .collect(Collectors.joining());

        Bukkit.getOnlinePlayers()
                .forEach(Sound.WOLF_GROWL::play);

        Bukkit.broadcastMessage(game.translate(Prefix.YELLOW , "werewolf.roles.bear_trainer.growling_message",
                Formatter.format("&growling&",builder)));

        int growl = event.getPlayerWWS().size();

        this.removeAuraModifier(this.getKey());

        if(growl == 0){
            this.addAuraModifier(new AuraModifier(this.getKey(), Aura.LIGHT,1,true));
        }
        else if(growl == 1){
            this.addAuraModifier(new AuraModifier(this.getKey(), Aura.NEUTRAL,1,true));
        }
        else{
            this.addAuraModifier(new AuraModifier(this.getKey(), Aura.DARK,1,true));
        }

    }

    @Override
    public @NotNull String getDescription() {

        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.bear_trainer.description",
                                Formatter.number(game.getConfig().getValue(IntValueBase.BEAR_TRAINER_DISTANCE))))
                .build();
    }


    @Override
    public void recoverPower() {

    }
}
