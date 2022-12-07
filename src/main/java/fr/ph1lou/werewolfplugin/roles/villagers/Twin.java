package fr.ph1lou.werewolfplugin.roles.villagers;


import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.game.day_cycle.NightEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.twin.AutoTwinEvent;
import fr.ph1lou.werewolfapi.events.roles.twin.TwinListEvent;
import fr.ph1lou.werewolfapi.events.roles.twin.TwinRevealEvent;
import fr.ph1lou.werewolfapi.events.roles.twin.TwinRoleEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleVillage;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Role(key = RoleBase.TWIN, 
        category = Category.VILLAGER,
        attributes = RoleAttribute.VILLAGER,
        requireDouble = true,
        timers = {@Timer(key = TimerBase.TWIN_DURATION,
                defaultValue = 1800, meetUpValue = 1800,
        decrementAfterRole = true,
        onZero = AutoTwinEvent.class)},
        configValues = {@IntValue(key = IntValueBase.TWIN_DISTANCE,
                defaultValue = 50,
                meetUpValue = 50, step = 5, item = UniversalMaterial.GREEN_WOOL)})
public class Twin extends RoleVillage {

    @Nullable
    private List<IPlayerWW> twinList;

    public Twin(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
    }


    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.twin.description",
                        Formatter.number(game.getConfig().getValue(IntValueBase.TWIN_DISTANCE)),
                        Formatter.format("&number2&",game.getConfig().getValue(IntValueBase.TWIN_DISTANCE)*2)))
                .setEffects(game.translate("werewolf.roles.twin.effects",Formatter.number(game.getConfig().getValue(IntValueBase.TWIN_DISTANCE))))
                .setPower(this.twinList == null ?
                        game.translate("werewolf.roles.twin.timer", Formatter.timer(game, TimerBase.TWIN_DURATION))
                        : game.translate("werewolf.roles.twin.twin_list", Formatter.format("&list&", this.twinList
                                .stream()
                                .map(IPlayerWW::getName)
                                .collect(Collectors.joining(", "))))
                        )
                .build();
    }

    @EventHandler
    public void onTwinReveal(AutoTwinEvent event){

        List<IPlayerWW> playerWWS = game.getPlayersWW().stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .filter(playerWW -> !playerWW.getRole().isKey(RoleBase.TWIN))
                .collect(Collectors.toList());
        Collections.shuffle(playerWWS, game.getRandom());

        if(playerWWS.size() < 2){
            return;
        }

        List<IPlayerWW> players = new ArrayList<>(Arrays.asList(playerWWS.get(0), playerWWS.get(1)));

        this.twinList = game.getPlayersWW()
                .stream()
                .filter(playerWW -> playerWW.getRole().isKey(RoleBase.TWIN))
                .filter(playerWW -> !playerWW.equals(this.getPlayerWW()))
                .collect(Collectors.toList());

        players.addAll(this.twinList);

        Collections.shuffle(players, game.getRandom());

        TwinRevealEvent twinRevealEvent = new TwinRevealEvent(this.getPlayerWW(), new HashSet<>(players));

        Bukkit.getPluginManager().callEvent(twinRevealEvent);

        if(twinRevealEvent.isCancelled()){
            this.getPlayerWW().sendMessageWithKey(Prefix.RED , "werewolf.check.cancel");
            return;
        }

        this.getPlayerWW().sendMessageWithKey(Prefix.BLUE,"werewolf.roles.twin.twin_list",
                Formatter.format("&list&", players
                        .stream()
                        .map(IPlayerWW::getName)
                        .collect(Collectors.joining(", "))));
    }

    @Override
    public void recoverPower() {
    }

    @EventHandler
    public void onTwinDeath(FinalDeathEvent event){

        if(!this.getPlayerWW().isState(StatePlayer.ALIVE)){
            return;
        }

        if(event.getPlayerWW().equals(this.getPlayerWW())){
            return;
        }

        if(!event.getPlayerWW().getRole().isKey(RoleBase.TWIN)){
            return;
        }

        this.getPlayerWW().sendMessageWithKey(Prefix.RED,"werewolf.roles.twin.death", Formatter.player(event.getPlayerWW().getName()));

        this.getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.INCREASE_DAMAGE,6000,0,this.getKey()));

        this.getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.SPEED,6000,0,this.getKey()));

    }

    @EventHandler
    public void onNight(NightEvent event){

        if(!this.getPlayerWW().isState(StatePlayer.ALIVE)){
            return;
        }

        if(this.twinList == null){
            return;
        }

        this.twinList.stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .findFirst()
                .ifPresent(playerWW -> {
                    Location twinLocation = playerWW.getLocation();
                    Location playerLocation = this.getPlayerWW().getLocation();

                    if(twinLocation.getWorld() != playerLocation.getWorld()){
                        return;
                    }

                    if(twinLocation.distance(playerLocation) < game.getConfig().getValue(IntValueBase.TWIN_DISTANCE) * 2){
                        this.getPlayerWW().sendMessageWithKey(Prefix.RED,"werewolf.roles.twin.too_near");
                        return;
                    }

                    List<IPlayerWW> nearPlayers = game.getPlayersWW()
                            .stream()
                            .filter(playerWW1 -> playerWW1.isState(StatePlayer.ALIVE))
                            .filter(playerWW1 -> !playerWW1.equals(playerWW))
                            .filter(playerWW1 -> !playerWW1.equals(this.getPlayerWW()))
                            .filter(playerWW1 -> {
                                Location location = playerWW1.getLocation();

                                return twinLocation.getWorld() == location.getWorld() &&
                                        twinLocation.distance(location) < game.getConfig().getValue(IntValueBase.TWIN_DISTANCE);

                            })
                            .collect(Collectors.toList());

                    if(game.getRandom().nextBoolean()){

                        TwinListEvent twinListEvent = new TwinListEvent(this.getPlayerWW(),new HashSet<>(nearPlayers));

                        Bukkit.getPluginManager().callEvent(twinListEvent);

                        if(twinListEvent.isCancelled()){
                            this.getPlayerWW().sendMessageWithKey(Prefix.RED , "werewolf.check.cancel");
                            return;
                        }

                        this.getPlayerWW().sendMessageWithKey(Prefix.ORANGE,"werewolf.roles.twin.list_near",
                        Formatter.number(game.getConfig().getValue(IntValueBase.TWIN_DISTANCE)),
                                Formatter.format("&list&",twinListEvent.getPlayerWWS().stream().map(IPlayerWW::getName).collect(Collectors.joining(", "))));
                    }
                    else{
                        Collections.shuffle(nearPlayers, game.getRandom());

                        if(nearPlayers.isEmpty()){
                            return;
                        }

                        TwinRoleEvent twinRoleEvent = new TwinRoleEvent(this.getPlayerWW(),nearPlayers.get(0));

                        Bukkit.getPluginManager().callEvent(twinRoleEvent);

                        if(twinRoleEvent.isCancelled()){
                            this.getPlayerWW().sendMessageWithKey(Prefix.RED , "werewolf.check.cancel");
                            return;
                        }

                        this.getPlayerWW().sendMessageWithKey(Prefix.ORANGE,"werewolf.roles.twin.role_near",
                                Formatter.number(game.getConfig().getValue(IntValueBase.TWIN_DISTANCE)),
                                Formatter.role(game.translate(twinRoleEvent.getTargetWW().getRole().getKey())));
                    }
                });
    }

}
