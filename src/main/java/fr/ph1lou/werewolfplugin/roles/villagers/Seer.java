package fr.ph1lou.werewolfplugin.roles.villagers;


import fr.ph1lou.werewolfapi.annotations.Configuration;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleWithLimitedSelectionDuration;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.utils.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


@Role(key = RoleBase.SEER,
        category = Category.VILLAGER,
        attributes = {RoleAttribute.VILLAGER,
                RoleAttribute.INFORMATION},
        configurations = {@Configuration(key = ConfigBase.SEER_EVERY_OTHER_DAY)})
public class Seer extends RoleWithLimitedSelectionDuration implements IAffectedPlayers {

    private int dayNumber = -8;
    private final List<IPlayerWW> affectedPlayer = new ArrayList<>();

    private boolean disablePower = false;

    public Seer(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
        setPower(false);
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

    @EventHandler
    public void onDay(DayEvent event) {

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (game.getConfig().isConfigActive(ConfigBase.SEER_EVERY_OTHER_DAY) &&
                event.getNumber() == dayNumber + 1) {
            return;
        }

        dayNumber = event.getNumber();

        if (disablePower) {
            disablePower = false;
            this.getPlayerWW().sendMessageWithKey(Prefix.RED , "werewolf.role.seer.disable");
            return;
        }

        setPower(true);

        this.getPlayerWW().sendMessageWithKey(
                Prefix.YELLOW , "werewolf.role.seer.see_camp_message",
                Formatter.timer(Utils.conversion(
                        game.getConfig()
                                .getTimerValue(TimerBase.POWER_DURATION))));
    }


    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.role.seer.description"))
                .setItems(game.translate("werewolf.role.seer.items"))
                .setEffects(game.translate("werewolf.role.seer.effect"))
                .build();
    }


    @Override
    public void recoverPower() {

    }


    @Override
    public void recoverPotionEffect() {

        this.getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.NIGHT_VISION,"seer"));
    }

    public void setDisablePower() {
        this.disablePower = true;
    }
}
