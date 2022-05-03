package fr.ph1lou.werewolfplugin.roles.villagers;


import fr.minuskube.inv.ClickableItem;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.game.IConfiguration;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.Prefix;
import fr.ph1lou.werewolfapi.enums.RolesBase;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.TimerBase;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.game.day_cycle.NightEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.twin.AutoTwinEvent;
import fr.ph1lou.werewolfapi.events.roles.twin.TwinListEvent;
import fr.ph1lou.werewolfapi.events.roles.twin.TwinRevealEvent;
import fr.ph1lou.werewolfapi.events.roles.twin.TwinRoleEvent;
import fr.ph1lou.werewolfapi.role.impl.RoleVillage;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import fr.ph1lou.werewolfapi.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class Twin extends RoleVillage {

    private List<IPlayerWW> twinList;

    public Twin(WereWolfAPI api, IPlayerWW playerWW, String key) {
        super(api, playerWW, key);
    }


    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.role.twin.description",
                        Formatter.number(game.getConfig().getDistanceTwin()),
                        Formatter.format("&number2&",game.getConfig().getDistanceTwin()*2)))
                .setEffects(game.translate("werewolf.role.twin.effects",Formatter.number(game.getConfig().getDistanceTwin())))
                .setPower(this.twinList == null ?
                        game.translate("werewolf.role.twin.timer", Formatter.timer(Utils.conversion(game.getConfig().getTimerValue(TimerBase.TWIN_DURATION.getKey()))))
                        : game.translate("werewolf.role.twin.twin_list", Formatter.format("&role&", this.twinList
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
                .filter(playerWW -> !playerWW.getRole().isKey(RolesBase.TWIN.getKey()))
                .collect(Collectors.toList());
        Collections.shuffle(playerWWS, game.getRandom());

        if(playerWWS.size() < 2){
            return;
        }

        this.twinList = new ArrayList<>(Arrays.asList(playerWWS.get(0), playerWWS.get(1)));
        twinList.addAll(game.getPlayersWW()
                .stream()
                .filter(playerWW -> playerWW.getRole().isKey(RolesBase.TWIN.getKey()))
                .filter(playerWW -> !playerWW.equals(this.getPlayerWW()))
                .collect(Collectors.toList()));

        Collections.shuffle(this.twinList, game.getRandom());

        TwinRevealEvent twinRevealEvent = new TwinRevealEvent(this.getPlayerWW(), new HashSet<>(this.twinList));

        Bukkit.getPluginManager().callEvent(twinRevealEvent);

        if(twinRevealEvent.isCancelled()){
            this.getPlayerWW().sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.cancel");
            return;
        }

        this.getPlayerWW().sendMessageWithKey(Prefix.BLUE.getKey(),"werewolf.role.twin.twin_list",
                Formatter.format("&list&", this.twinList
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

        if(!event.getPlayerWW().getRole().isKey(RolesBase.TWIN.getKey())){
            return;
        }

        this.getPlayerWW().sendMessageWithKey(Prefix.RED.getKey(),"werewolf.role.twin.death", Formatter.player(event.getPlayerWW().getName()));

        this.getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.INCREASE_DAMAGE,6000,0,"twin"));

        this.getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.SPEED,6000,0,"twin"));

    }

    @EventHandler
    public void onNight(NightEvent event){

        if(!this.getPlayerWW().isState(StatePlayer.ALIVE)){
            return;
        }

        if(this.twinList == null){
            return;
        }

        this.twinList.stream().filter(playerWW -> playerWW.getRole().isKey(RolesBase.TWIN.getKey()))
                .findFirst()
                .ifPresent(playerWW -> {
                    Location twinLocation = playerWW.getLocation();
                    Location playerLocation = this.getPlayerWW().getLocation();

                    if(twinLocation.getWorld() != playerLocation.getWorld()){
                        return;
                    }

                    if(twinLocation.distance(playerLocation) < game.getConfig().getDistanceTwin() * 2){
                        this.getPlayerWW().sendMessageWithKey(Prefix.RED.getKey(),"werewolf.role.twin.too_near");
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
                                        twinLocation.distance(location) < game.getConfig().getDistanceTwin();

                            })
                            .collect(Collectors.toList());

                    if(game.getRandom().nextBoolean()){

                        TwinListEvent twinListEvent = new TwinListEvent(this.getPlayerWW(),new HashSet<>(nearPlayers));

                        Bukkit.getPluginManager().callEvent(twinListEvent);

                        if(twinListEvent.isCancelled()){
                            this.getPlayerWW().sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.cancel");
                            return;
                        }

                        this.getPlayerWW().sendMessageWithKey(Prefix.ORANGE.getKey(),"werewolf.role.twin.list_near",
                        Formatter.number(game.getConfig().getDistanceTwin()),
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
                            this.getPlayerWW().sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.cancel");
                            return;
                        }

                        this.getPlayerWW().sendMessageWithKey(Prefix.ORANGE.getKey(),"werewolf.role.twin.role_near",
                                Formatter.number(game.getConfig().getDistanceTwin()),
                                Formatter.role(game.translate(twinRoleEvent.getTargetWW().getRole().getKey())));
                    }
                });
    }

    public static ClickableItem config(WereWolfAPI game) {
        List<String> lore = Arrays.asList(game.translate("werewolf.menu.left"),
                game.translate("werewolf.menu.right"));
        IConfiguration config = game.getConfig();

        return ClickableItem.of((
                new ItemBuilder(UniversalMaterial.GREEN_WOOL.getStack())
                        .setDisplayName(game.translate("werewolf.menu.advanced_tool.twin",
                                Formatter.number(config.getDistanceTwin())))
                        .setLore(lore).build()), e -> {

            if (e.isLeftClick()) {
                config.setDistanceTwin((config.getDistanceTwin() + 2));
            } else if (config.getDistanceTwin() - 2 > 0) {
                config.setDistanceTwin(config.getDistanceTwin() - 2);
            }


            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setLore(lore)
                    .setDisplayName(game.translate("werewolf.menu.advanced_tool.twin",
                            Formatter.number(config.getDistanceTwin())))
                    .build());

        });
    }

}
