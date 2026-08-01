package fr.ph1lou.werewolfplugin.roles.villagers;


import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.RequireAbility;
import fr.ph1lou.werewolfapi.annotations.RequireAlive;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Camp;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.Day;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.enums.UniversalPotionEffectType;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.NightEvent;
import fr.ph1lou.werewolfapi.events.roles.fox.SniffEvent;
import fr.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import fr.ph1lou.werewolfapi.events.UpdatePlayerNameTagEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.AuraModifier;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleImpl;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.ILimitedUse;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.interfaces.IProgress;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Role(key = RoleBase.FOX, category = Category.VILLAGER,
        defaultAura = Aura.LIGHT,
        auraDescriptionSpecialUseCase = "werewolf.roles.fox.aura",
        attribute = RoleAttribute.MINOR_INFORMATION,
        timers = { @Timer(key = TimerBase.FOX_SMELL_DURATION, defaultValue = 90, meetUpValue = 30) },
        configValues = {
                @IntValue(key = IntValueBase.FOX_SMELL_NUMBER, defaultValue = 3, meetUpValue = 3, step = 1, item = UniversalMaterial.CARROT),
                @IntValue(key = IntValueBase.FOX_DISTANCE, defaultValue = 15, meetUpValue = 15, step = 5, item = UniversalMaterial.ORANGE_WOOL) })
public class Fox extends RoleImpl implements IProgress, ILimitedUse, IAffectedPlayers, IPower {

    private final List<IPlayerWW> affectedPlayer = new ArrayList<>();
    private final List<IPlayerWW> sniffedPlayers = new ArrayList<>();
    private float progress = 0;
    private int use = 0;
    private boolean power = false;
    private int lastDisplayedPct = -1;

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

    @EventHandler(priority = EventPriority.HIGH)
    public void onNight(NightEvent event) {

        if (getUse() >= game.getConfig().getValue(IntValueBase.FOX_SMELL_NUMBER)) {
            return;
        }

        setPower(true);

        if (this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            this.getPlayerWW().addPotionModifier(PotionModifier.add(UniversalPotionEffectType.SPEED, this.getKey()));
            this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.fox.smell_message",
                    Formatter.number(game.getConfig().getValue(IntValueBase.FOX_SMELL_NUMBER) - getUse()));
        }
    }

    @EventHandler
    public void onDay(DayEvent event) {

        this.getPlayerWW().addPotionModifier(PotionModifier.remove(UniversalPotionEffectType.SPEED, this.getKey(), 0));

        IPlayerWW former = getAffectedPlayers().isEmpty() ? null : getAffectedPlayers().get(0);
        clearAffectedPlayer();
        setProgress(0f);
        this.lastDisplayedPct = -1;
        if (former != null) {
            Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(former));
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    @RequireAbility
    @RequireAlive
    public void onUpdatePlayerNameTag(UpdatePlayerNameTagEvent event) {

        if (!event.getTargetUUID().equals(this.getPlayerUUID())) return;
        if (getAffectedPlayers().isEmpty()) return;

        IPlayerWW viewed = game.getPlayerWW(event.getPlayerUUID()).orElse(null);
        if (viewed == null) return;
        if (!viewed.equals(getAffectedPlayers().get(0))) return;

        int pct = (int) Math.min(100, Math.floor(this.getProgress()));
        if (pct <= 0) return;

        String color = pct >= 100 ? "§c" : pct >= 50 ? "§e" : "§a";
        event.setSuffix(event.getSuffix() + " " + color + pct + "%");
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
    public void recoverPotionEffect() {

        if (game.isDay(Day.NIGHT) && this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            this.getPlayerWW().addPotionModifier(PotionModifier.add(UniversalPotionEffectType.SPEED, this.getKey()));
        }
    }

    @Override
    public void disableAbilitiesRole() {

        this.getPlayerWW().addPotionModifier(PotionModifier.remove(UniversalPotionEffectType.SPEED, this.getKey(), 0));
    }

    @Override
    public void second() {

        if (!isAbilityEnabled()) return;
        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (!game.isDay(Day.NIGHT)) {
            return;
        }

        if (getAffectedPlayers().isEmpty()) {
            return;
        }

        IPlayerWW playerWW = getAffectedPlayers().get(0);

        if (!playerWW.isState(StatePlayer.ALIVE)) {
            return;
        }

        if (playerWW.distance(getPlayerWW()) >
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

        int pct = (int) Math.min(100, Math.floor(temp));
        if (pct != this.lastDisplayedPct) {
            this.lastDisplayedPct = pct;
            Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(playerWW));
        }

        if (temp >= 100) {

            boolean isWereWolf = playerWW.getRole().isDisplayCamp(Camp.WEREWOLF.getKey()) ||
                                 (playerWW.getRole().getDisplayCamp().equals(playerWW.getRole().getCamp().getKey()) &&
                                  playerWW.getRole().isWereWolf());

            SniffEvent sniffEvent = new SniffEvent(this.getPlayerWW(),
                    playerWW, isWereWolf);

            Bukkit.getPluginManager().callEvent(sniffEvent);

            if (!sniffEvent.isCancelled()) {

                this.sniffedPlayers.add(playerWW);
                this.use++;

                this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.fox.smell_perform",
                        Formatter.player(playerWW.getName()));

                if (this.use >= game.getConfig().getValue(IntValueBase.FOX_SMELL_NUMBER)) {

                    long wolfCount = this.sniffedPlayers.stream()
                            .filter(p -> p.getRole().isDisplayCamp(Camp.WEREWOLF.getKey()) ||
                                    (p.getRole().getDisplayCamp().equals(p.getRole().getCamp().getKey()) &&
                                            p.getRole().isWereWolf()))
                            .count();

                    this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.fox.result",
                            Formatter.number((int) wolfCount));

                    if (wolfCount > 0) {
                        this.addAuraModifier(new AuraModifier(this.getKey(), Aura.DARK, 1, false));
                    }
                }

            } else {
                this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            }

            clearAffectedPlayer();
            setProgress(0f);
            this.lastDisplayedPct = -1;
            Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(playerWW));
        }
    }
}
