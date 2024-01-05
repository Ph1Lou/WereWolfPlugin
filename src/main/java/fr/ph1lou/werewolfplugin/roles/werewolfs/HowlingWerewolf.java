package fr.ph1lou.werewolfplugin.roles.werewolfs;

import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.Sound;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.game.day_cycle.NightEvent;
import fr.ph1lou.werewolfapi.events.roles.howling_werewolf.HowlEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleWereWolf;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfapi.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Role(key = RoleBase.HOWLING_WEREWOLF, defaultAura = Aura.DARK, category = Category.WEREWOLF,
        attribute = RoleAttribute.WEREWOLF,
        configValues = @IntValue(key = IntValueBase.HOWLING_WEREWOLF_DISTANCE,
                defaultValue = 80, meetUpValue = 80, step = 5, item = UniversalMaterial.LIGHT_GRAY_WOOL))
public class HowlingWerewolf extends RoleWereWolf {

    public HowlingWerewolf(WereWolfAPI game, IPlayerWW playerWW) {
        super(game, playerWW);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.howling_werewolf.description",
                        Formatter.number(game.getConfig().getValue(IntValueBase.HOWLING_WEREWOLF_DISTANCE)),
                        Formatter.timer(Utils.conversion(game.getConfig().getTimerValue(TimerBase.DAY_DURATION)))))
                .setEffects(game.translate("werewolf.description.werewolf"))
                .build();
    }

    @Override
    public void recoverPower() {

    }

    @EventHandler
    public void onNight(NightEvent event) {

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        Set<IPlayerWW> playerWWS = Bukkit.getOnlinePlayers()
                .stream()
                .map(Entity::getUniqueId)
                .filter(uuid -> !this.getPlayerUUID().equals(uuid))
                .map(game::getPlayerWW)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(iPlayerWW -> iPlayerWW.isState(StatePlayer.ALIVE))
                .filter(playerWW -> {
                    Location location = playerWW.getLocation();
                    Location playerLocation = this.getPlayerWW().getLocation();
                    return location.getWorld() == playerLocation.getWorld() &&
                            location.distance(playerLocation) < game.getConfig().getValue(IntValueBase.HOWLING_WEREWOLF_DISTANCE);
                })
                .collect(Collectors.toSet());

        if (playerWWS.size() < 5) {
            return;
        }

        HowlEvent howlEvent = new HowlEvent(this.getPlayerWW(), playerWWS, (int) playerWWS
                .stream()
                .map(playerWW -> !playerWW.getRole().isWereWolf())
                .count());

        Bukkit.getPluginManager().callEvent(howlEvent);

        if (howlEvent.isCancelled()) {
            this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }

        playerWWS.forEach(playerWW -> playerWW.sendSound(Sound.WOLF_HOWL));

        int heart = 0;

        if (howlEvent.getNotWerewolfSize() > 1) {
            heart = 1;
            if (howlEvent.getNotWerewolfSize() > 3) {
                heart++;
            }
            if (howlEvent.getNotWerewolfSize() > 5) {
                heart++;
            }
        }

        if (heart == 0) {
            return;
        }

        int finalHeart = heart * 2;

        this.getPlayerWW().addPlayerMaxHealth(finalHeart);

        this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.howling_werewolf.message",
                Formatter.number(howlEvent.getNotWerewolfSize()),
                Formatter.format("&heart&", heart),
                Formatter.timer(Utils.conversion(game.getConfig().getTimerValue(TimerBase.DAY_DURATION))));

        BukkitUtils.scheduleSyncDelayedTask(game, () -> this.getPlayerWW().removePlayerMaxHealth(finalHeart),
                game.getConfig().getTimerValue(TimerBase.DAY_DURATION) * 20L);
    }
}
