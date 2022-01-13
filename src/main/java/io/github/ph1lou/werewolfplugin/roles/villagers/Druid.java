package io.github.ph1lou.werewolfplugin.roles.villagers;

import fr.minuskube.inv.ClickableItem;
import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.Formatter;
import io.github.ph1lou.werewolfapi.IConfiguration;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Aura;
import io.github.ph1lou.werewolfapi.enums.Day;
import io.github.ph1lou.werewolfapi.enums.Prefix;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.UniversalMaterial;
import io.github.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import io.github.ph1lou.werewolfapi.events.roles.druid.DruidUsePowerEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IPower;
import io.github.ph1lou.werewolfapi.rolesattributs.RoleVillage;
import io.github.ph1lou.werewolfapi.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Druid extends RoleVillage implements IPower {

    private boolean power = true;

    public Druid(WereWolfAPI game, IPlayerWW playerWW, String key) {
        super(game, playerWW, key);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.role.druid.description",
                        Formatter.number(game.getConfig().getDistanceDruid())))
                .build();
    }

    @Override
    public void recoverPower() {

    }

    @EventHandler
    public void onDay(DayEvent event){
        if(!this.getPlayerWW().isState(StatePlayer.ALIVE)){
            return;
        }
        this.setPower(true);

        this.getPlayerWW().sendMessageWithKey(Prefix.GREEN.getKey(),"werewolf.role.druid.day");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAppleEat(PlayerItemConsumeEvent event) {

        Player player = event.getPlayer();
        IPlayerWW playerWW = game.getPlayerWW(player.getUniqueId()).orElse(null);

        if(!this.getPlayerWW().isState(StatePlayer.ALIVE)){
            return;
        }

        if (!this.getPlayerWW().equals(playerWW)) return;

        if(game.isDay(Day.DAY)){
            return;
        }

        if (!event.getItem().getType().equals(Material.GOLDEN_APPLE)) {
            return;
        }

        if (!player.isSneaking()) {
            return;
        }

        if (!this.hasPower()) {
            return;
        }

        this.setPower(false);

        World world = this.getPlayerWW().getLocation().getWorld();

        int darkAura = (int) Bukkit.getOnlinePlayers()
                .stream().map(Entity::getUniqueId)
                .map(game::getPlayerWW)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(playerWW1 -> {
                    Location location= playerWW1.getLocation();
                    return location.getWorld() == world &&
                            location.distance(this.getPlayerWW().getLocation()) < game.getConfig().getDistanceDruid();
                })
                .filter(playerWW1 -> playerWW1.getRole().getAura() == Aura.DARK)
                .count();

        DruidUsePowerEvent druidUsePowerEvent = new DruidUsePowerEvent(this.getPlayerWW(),darkAura);

        Bukkit.getPluginManager().callEvent(druidUsePowerEvent);

        if(druidUsePowerEvent.isCancelled()){
            this.getPlayerWW().sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.cancel");
            return;
        }

        this.getPlayerWW().sendMessageWithKey(Prefix.BLUE.getKey(),"werewolf.role.druid.perform",
                Formatter.number(druidUsePowerEvent.getDarkAura()),
                Formatter.format("&blocks&",game.getConfig().getDistanceDruid()));

    }

    @Override
    public void setPower(boolean power) {
        this.power = power;
    }

    @Override
    public boolean hasPower() {
        return this.power;
    }

    public static ClickableItem config(WereWolfAPI game) {
        List<String> lore = Arrays.asList(game.translate("werewolf.menu.left"),
                game.translate("werewolf.menu.right"));
        IConfiguration config = game.getConfig();

        return ClickableItem.of((
                new ItemBuilder(UniversalMaterial.CYAN_WOOL.getStack())
                        .setDisplayName(game.translate("werewolf.menu.advanced_tool.druid",
                                Formatter.number(config.getDistanceDruid())))
                        .setLore(lore).build()), e -> {

            if (e.isLeftClick()) {
                config.setDistanceDruid((config.getDistanceDruid() + 2));
            } else if (config.getDistanceDruid() - 2 > 0) {
                config.setDistanceDruid(config.getDistanceDruid() - 2);
            }


            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setLore(lore)
                    .setDisplayName(game.translate("werewolf.menu.advanced_tool.druid",
                            Formatter.number(config.getDistanceDruid())))
                    .build());

        });
    }

    @Override
    public Aura getDefaultAura() {
        return Aura.DARK;
    }
}
