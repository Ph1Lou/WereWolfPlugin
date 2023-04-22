package fr.ph1lou.werewolfplugin.roles.villagers;


import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Camp;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.Sound;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.roles.fox.SniffEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.AuraModifier;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleVillage;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.ILimitedUse;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.interfaces.IProgress;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Role(key = RoleBase.FOX, category = Category.VILLAGER,
        attributes = {RoleAttribute.VILLAGER,
                RoleAttribute.INFORMATION},
        timers = {@Timer(key = TimerBase.FOX_SMELL_DURATION, defaultValue = 90, meetUpValue = 30)},
        configValues = {
                @IntValue(key = IntValueBase.FOX_SMELL_NUMBER, defaultValue = 3, meetUpValue = 3, step = 1, item = UniversalMaterial.CARROT),
                @IntValue(key = IntValueBase.FOX_DISTANCE, defaultValue = 20, meetUpValue = 20, step = 5, item = UniversalMaterial.ORANGE_WOOL)})
public class Fox extends RoleVillage implements IProgress, ILimitedUse, IAffectedPlayers, IPower {

    private final List<IPlayerWW> affectedPlayer = new ArrayList<>();
    private float progress = 0;
    private int use = 0;
    private boolean power = false;

    public Fox(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
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
                this.getKey()));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDay(DayEvent event) {

        if (getUse() >= game.getConfig().getValue(IntValueBase.FOX_SMELL_NUMBER)) {
            return;
        }

        setPower(true);

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.fox.smell_message",
                Formatter.number(game.getConfig().getValue(IntValueBase.FOX_SMELL_NUMBER) - getUse()));
    }


    @Override
    public @NotNull String getDescription() {

        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.fox.description",
                        Formatter.number(game.getConfig().getValue(IntValueBase.FOX_DISTANCE)),
                        Formatter.timer(game, TimerBase.FOX_SMELL_DURATION),
                        Formatter.format("&number1&", game.getConfig().getValue(IntValueBase.FOX_SMELL_NUMBER) - use)))
                .setEffects(game.translate("werewolf.roles.fox.effect"))
                .setPower(game.translate("werewolf.roles.fox.progress",
                        Formatter.format("&progress&", Math.min(100, Math.floor(this.getProgress())))))
                .build();
    }


    @Override
    public void recoverPower() {

    }

    @Override
    public void second() {

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }
        if (getAffectedPlayers().isEmpty()) {
            return;
        }

        IPlayerWW playerWW = getAffectedPlayers().get(0);

        if (!playerWW.isState(StatePlayer.ALIVE)) {
            return;
        }

        Location renardLocation = this.getPlayerWW().getLocation();
        Location playerLocation = playerWW.getLocation();

        if (renardLocation.getWorld() != playerLocation.getWorld()) {
            return;
        }

        if (renardLocation.distance(playerLocation) >
                game.getConfig().getValue(IntValueBase.FOX_DISTANCE)) {
            return;
        }

        float temp = getProgress() + 100f /
                (game.getConfig().getTimerValue(TimerBase.FOX_SMELL_DURATION) + 1);

        this.setProgress(temp);

        if (temp % 10 > 0 && temp % 10 <= 100f /
                (game.getConfig().getTimerValue(TimerBase.FOX_SMELL_DURATION) + 1)) {
            this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.fox.progress",
                    Formatter.format("&progress&", Math.min(100, Math.floor(temp))));
        }

        if (temp >= 100) {

            boolean isWereWolf = playerWW.getRole().isDisplayCamp(Camp.WEREWOLF.getKey()) ||
                    (playerWW.getRole().getDisplayCamp().equals(playerWW.getRole().getCamp().getKey()) &&
                            playerWW.getRole().isWereWolf());

            SniffEvent sniffEvent = new SniffEvent(this.getPlayerWW(),
                    playerWW, isWereWolf);

            Bukkit.getPluginManager().callEvent(sniffEvent);

            if (!sniffEvent.isCancelled()) {
                if (sniffEvent.isWereWolf()) {
                    this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.fox.werewolf",
                            Formatter.player(playerWW.getName()));
                    this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.fox.warn");

                    this.addAuraModifier(new AuraModifier(this.getKey(), Aura.DARK, 1, false));

                } else {
                    this.getPlayerWW().sendMessageWithKey(
                            Prefix.YELLOW, "werewolf.roles.fox.not_werewolf",
                            Formatter.player(playerWW.getName()));
                }


                if (sniffEvent.isWereWolf()) {
                    BukkitUtils.scheduleSyncDelayedTask(game, () -> {
                        playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.fox.smell");
                        playerWW.sendSound(Sound.DONKEY_ANGRY);
                    }, 20 * 60 * 5);
                }

            } else {
                this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            }

            clearAffectedPlayer();
            setProgress(0f);
        }
    }
}
