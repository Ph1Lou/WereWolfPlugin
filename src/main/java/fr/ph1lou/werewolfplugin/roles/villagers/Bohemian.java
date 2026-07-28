package fr.ph1lou.werewolfplugin.roles.villagers;

import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.game.spy.SpyInfoEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleImpl;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Role(key = RoleBase.BOHEMIAN,
        category = Category.VILLAGER,
        attribute = RoleAttribute.INFORMATION,
        defaultAura = Aura.LIGHT)
public class Bohemian extends RoleImpl {

    private static final int COOLDOWN = 10 * 60;

    private final Map<UUID, Integer> targetCounts = new HashMap<>();
    private final Map<UUID, String> lastNature = new HashMap<>();
    private int lastUseTimer = -COOLDOWN;
    private boolean initialized = false;

    public Bohemian(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
    }

    @Override
    public void recoverPower() {
        if (!this.initialized) {
            this.getPlayerWW().modifyHonor(1 - this.getPlayerWW().getHonor());
            this.initialized = true;
        }
    }
    

    @EventHandler
    public void onDay(DayEvent event) {
        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }
        this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.bohemian.honor_level",
                Formatter.number(this.getPlayerWW().getHonor()));
    }

    @EventHandler
    public void onSpyInfo(SpyInfoEvent event) {
        IPlayerWW target = event.getTargetWW();
        this.targetCounts.merge(target.getUUID(), 1, Integer::sum);
        this.lastNature.put(target.getUUID(), event.getEvent());
    }

    public boolean canSacrifice() {
        return game.getTimer() - this.lastUseTimer >= COOLDOWN;
    }

    public void onSacrificed() {
        this.lastUseTimer = game.getTimer();
    }

    public int getCurrentHonor() {
        return this.getPlayerWW().getHonor();
    }

    public void sacrificeHonor(int amount) {
        this.getPlayerWW().modifyHonor(-amount);
    }

    public int getTargetCount(UUID targetUUID) {
        return this.targetCounts.getOrDefault(targetUUID, 0);
    }

    @Nullable
    public String getTargetNature(UUID targetUUID) {
        return this.lastNature.get(targetUUID);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.bohemian.description",
                        Formatter.number(this.getPlayerWW().getHonor())))
                .build();
    }
}
