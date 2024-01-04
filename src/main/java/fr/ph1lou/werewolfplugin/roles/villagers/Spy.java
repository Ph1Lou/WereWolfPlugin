package fr.ph1lou.werewolfplugin.roles.villagers;

import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.game.actionablestory.ActionableStoryEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.roles.spy.SpyResultEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleImpl;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

@Role(key = RoleBase.SPY,
        category = Category.VILLAGER,
        attributes = {RoleAttribute.VILLAGER, RoleAttribute.MINOR_INFORMATION},
        configValues = @IntValue(key = IntValueBase.SPY_DAY, defaultValue = 5, meetUpValue = 2, step = 1, item = UniversalMaterial.ANVIL))
public class Spy extends RoleImpl implements IAffectedPlayers, IPower {

    @Nullable
    private IPlayerWW playerWW;
    private int count = 0;
    private boolean power = false;

    public Spy(WereWolfAPI game, IPlayerWW playerWW) {
        super(game, playerWW);
    }

    @Override
    public void addAffectedPlayer(IPlayerWW iPlayerWW) {
        this.playerWW = iPlayerWW;
    }

    @Override
    public void removeAffectedPlayer(IPlayerWW iPlayerWW) {
        if (iPlayerWW.equals(this.playerWW)) {
            this.playerWW = null;
        }
    }

    @Override
    public void clearAffectedPlayer() {
        this.playerWW = null;
    }

    @Override
    public List<? extends IPlayerWW> getAffectedPlayers() {
        if (this.playerWW == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(this.playerWW);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.spy.description",
                        Formatter.number(game.getConfig().getValue(IntValueBase.SPY_DAY))))
                .build();
    }

    @Override
    public void recoverPower() {

    }

    @EventHandler
    public void onActionableStory(ActionableStoryEvent event) {
        game.getPlayerWW(event.getPlayer())
                .ifPresent(iPlayerWW -> {
                    if (iPlayerWW.equals(this.playerWW)) {
                        this.count++;
                    }
                });
    }

    @EventHandler
    public void onDay(DayEvent event) {

        if (event.getNumber() >= game.getConfig().getValue(IntValueBase.SPY_DAY)) {
            this.power = true;
        }

        if (this.getPlayerWW().isState(StatePlayer.ALIVE)) {

            if (this.isAbilityEnabled()) {
                if (this.playerWW != null) {

                    SpyResultEvent spyResultEvent = new SpyResultEvent(this.getPlayerWW(),
                            this.playerWW,
                            this.count);

                    Bukkit.getPluginManager().callEvent(spyResultEvent);

                    if (spyResultEvent.isCancelled()) {
                        this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
                    } else {
                        this.getPlayerWW().sendMessageWithKey(Prefix.GREEN,
                                "werewolf.roles.spy.result",
                                Formatter.player(this.playerWW.getName()),
                                Formatter.number(this.count));
                    }
                }
            }

            this.getPlayerWW().sendMessageWithKey(Prefix.GREEN, "werewolf.roles.spy.use_power");
        }

        this.count = 0;
        this.playerWW = null;
    }

    @Override
    public void setPower(boolean power) {
        this.power = power;
    }

    @Override
    public boolean hasPower() {
        return this.power;
    }
}
