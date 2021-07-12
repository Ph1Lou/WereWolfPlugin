package io.github.ph1lou.werewolfplugin.roles.villagers;


import fr.minuskube.inv.ClickableItem;
import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.IConfiguration;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.PotionModifier;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Camp;
import io.github.ph1lou.werewolfapi.enums.Sound;
import io.github.ph1lou.werewolfapi.enums.StateGame;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.TimerBase;
import io.github.ph1lou.werewolfapi.enums.UniversalMaterial;
import io.github.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import io.github.ph1lou.werewolfapi.events.roles.fox.SniffEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.ILimitedUse;
import io.github.ph1lou.werewolfapi.rolesattributs.IPower;
import io.github.ph1lou.werewolfapi.rolesattributs.IProgress;
import io.github.ph1lou.werewolfapi.rolesattributs.RoleVillage;
import io.github.ph1lou.werewolfapi.utils.BukkitUtils;
import io.github.ph1lou.werewolfapi.utils.ItemBuilder;
import io.github.ph1lou.werewolfapi.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Fox extends RoleVillage implements IProgress, ILimitedUse, IAffectedPlayers, IPower {

    private float progress = 0;
    private int use = 0;
    private boolean power = false;
    private final List<IPlayerWW> affectedPlayer = new ArrayList<>();

    public Fox(WereWolfAPI api, IPlayerWW playerWW, String key) {
        super(api, playerWW, key);
    }

    @Override
    public void setPower(boolean power) {
        this.power = power;
    }

    @Override
    public boolean hasPower() {
        return (this.power);
    }

    @Override
    public void addAffectedPlayer(IPlayerWW playerWW) {
        this.affectedPlayer.add(playerWW);
    }

    @Override
    public void removeAffectedPlayer(IPlayerWW playerWW) {
        this.affectedPlayer.remove(playerWW);
    }

    @Override
    public void clearAffectedPlayer() {
        this.affectedPlayer.clear();
    }

    @Override
    public List<IPlayerWW> getAffectedPlayers() {
        return (this.affectedPlayer);
    }

    @Override
    public int getUse() {
        return use;
    }

    @Override
    public void setUse(int use) {
        this.use = use;
    }

    @Override
    public float getProgress() {
        return (this.progress);
    }

    @Override
    public void setProgress(float progress) {
        this.progress = progress;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeathByFox(PlayerDeathEvent event) {

        if (event.getEntity().getKiller() == null) return;

        Player killer = event.getEntity().getKiller();

        if (!getPlayerUUID().equals(killer.getUniqueId())) return;

        this.getPlayerWW().addPotionModifier(PotionModifier.add(
                PotionEffectType.SPEED,
                3600,
                0,
                "fox"));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDay(DayEvent event) {

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (getUse() >= game.getConfig().getUseOfFlair()) {
            return;
        }

        setPower(true);
        this.getPlayerWW().sendMessageWithKey("werewolf.role.fox.smell_message",
                game.getConfig().getUseOfFlair() - getUse());
    }


    @Override
    public @NotNull String getDescription() {

        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.role.fox.description",
                        game.getConfig().getDistanceFox(),
                        Utils.conversion(game.getConfig()
                                .getTimerValue(TimerBase.FOX_SMELL_DURATION.getKey())),
                        game.getConfig().getUseOfFlair() - use))
                .setEffects(game.translate("werewolf.role.fox.effect"))
                .build();
    }


    @Override
    public void recoverPower() {

    }

    @Override
    public void second() {

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) {
            return;
        }
        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }
        if (getAffectedPlayers().isEmpty()) {
            return;
        }

        IPlayerWW playerWW = getAffectedPlayers().get(0);
        Player flair = Bukkit.getPlayer(playerWW.getUUID());

        if (!playerWW.isState(StatePlayer.ALIVE)) {
            return;
        }

        if (flair == null) {
            return;
        }

        Location renardLocation = player.getLocation();
        Location playerLocation = flair.getLocation();

        if (!player.getWorld().equals(flair.getWorld())) {
            return;
        }

        if (renardLocation.distance(playerLocation) >
                game.getConfig().getDistanceFox()) {
            return;
        }

        float temp = getProgress() + 100f /
                (game.getConfig().getTimerValue(TimerBase.FOX_SMELL_DURATION.getKey()) + 1);

        setProgress(temp);

        if (temp % 10 > 0 && temp % 10 <= 100f /
                (game.getConfig().getTimerValue(TimerBase.FOX_SMELL_DURATION.getKey()) + 1)) {
            player.sendMessage(game.translate("werewolf.role.fox.progress",
                    Math.min(100, Math.floor(temp))));
        }

        if (temp >= 100) {

            boolean isWereWolf = playerWW.getRole().isDisplayCamp(Camp.WEREWOLF.getKey());

            SniffEvent sniffEvent = new SniffEvent(this.getPlayerWW(),
                    playerWW, isWereWolf);

            Bukkit.getPluginManager().callEvent(sniffEvent);

            if (!sniffEvent.isCancelled()) {
                if (sniffEvent.isWereWolf()) {
                    player.sendMessage(game.translate(
                            "werewolf.role.fox.werewolf",
                            playerWW.getName()));
                    player.sendMessage(game.translate("werewolf.role.fox.warn"));
                } else {
                    player.sendMessage(game.translate(
                            "werewolf.role.fox.not_werewolf",
                            playerWW.getName()));
                }


                if (playerWW.getRole().isWereWolf()) {
                    BukkitUtils.scheduleSyncDelayedTask(() -> {
                        if (game.isState(StateGame.GAME)) {
                            playerWW.sendMessageWithKey("werewolf.role.fox.smell", Sound.DONKEY_ANGRY);
                        }
                    }, 20 * 60 * 5);
                }

            } else {
                player.sendMessage(game.translate("werewolf.check.cancel"));
            }

            clearAffectedPlayer();
            setProgress(0f);
        }
    }

    public static ClickableItem config1(WereWolfAPI game) {
        List<String> lore = Arrays.asList(game.translate("werewolf.menu.left"),
                game.translate("werewolf.menu.right"));
        IConfiguration config = game.getConfig();

        return ClickableItem.of(
                new ItemBuilder(UniversalMaterial.CARROT.getType())
                        .setLore(lore)
                        .setDisplayName(game.translate("werewolf.menu.advanced_tool.fox_smell_number",
                                config.getUseOfFlair()))
                        .build(), e -> {
                    if (e.isLeftClick()) {
                        config.setUseOfFlair(config.getUseOfFlair() + 1);
                    } else if (config.getUseOfFlair() > 0) {
                        config.setUseOfFlair(config.getUseOfFlair() - 1);
                    }


                    e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                            .setDisplayName(game.translate("werewolf.menu.advanced_tool.fox_smell_number",
                                    config.getUseOfFlair()))
                            .build());

                });
    }

    public static ClickableItem config2(WereWolfAPI game) {

        List<String> lore = Arrays.asList(game.translate("werewolf.menu.left"),
                game.translate("werewolf.menu.right"));
        IConfiguration config = game.getConfig();

        return ClickableItem.of((
                new ItemBuilder(UniversalMaterial.ORANGE_WOOL.getStack())
                        .setDisplayName(game.translate("werewolf.menu.advanced_tool.fox",
                                config.getDistanceFox()))
                        .setLore(lore).build()), e -> {

            if (e.isLeftClick()) {
                config.setDistanceFox((config.getDistanceFox() + 5));
            } else if (config.getDistanceFox() - 5 > 0) {
                config.setDistanceFox(config.getDistanceFox() - 5);
            }


            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setLore(lore)
                    .setDisplayName(game.translate("werewolf.menu.advanced_tool.fox",
                            config.getDistanceFox()))
                    .build());

        });
    }
}
