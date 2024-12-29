package fr.ph1lou.werewolfplugin.roles.villagers;

import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.Day;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.roles.druid.DruidUsePowerEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleImpl;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;


@Role(key = RoleBase.DRUID,
        defaultAura = Aura.DARK,
        category = Category.VILLAGER,
        attribute = RoleAttribute.MINOR_INFORMATION,
        configValues = { @IntValue(key = IntValueBase.DRUID_DISTANCE,
                defaultValue = 50, meetUpValue = 50, step = 5, item = UniversalMaterial.CYAN_WOOL) })
public class Druid extends RoleImpl implements IPower {

    private boolean power = true;

    public Druid(WereWolfAPI game, IPlayerWW playerWW) {
        super(game, playerWW);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.druid.description",
                        Formatter.number(game.getConfig().getValue(IntValueBase.DRUID_DISTANCE))))
                .build();
    }

    @Override
    public void recoverPower() {

    }

    @EventHandler
    public void onDay(DayEvent event) {
        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }
        this.setPower(true);

        this.getPlayerWW().sendMessageWithKey(Prefix.GREEN, "werewolf.roles.druid.day");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAppleEat(PlayerItemConsumeEvent event) {

        Player player = event.getPlayer();
        IPlayerWW playerWW = game.getPlayerWW(player.getUniqueId()).orElse(null);

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (!this.getPlayerWW().equals(playerWW)) return;

        if (game.isDay(Day.DAY)) {
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

        List<IPlayerWW> playerWWS = game.getPlayersWW()
                .stream()
                .filter(playerWW1 -> playerWW1.isState(StatePlayer.ALIVE))
                .filter(playerWW1 -> !playerWW1.equals(this.getPlayerWW()))
                .filter(playerWW1 -> playerWW1.distance(this.getPlayerWW()) < game.getConfig().getValue(IntValueBase.DRUID_DISTANCE))
                .filter(playerWW1 -> playerWW1.getRole().getAura() == Aura.DARK)
                .collect(Collectors.toList());

        DruidUsePowerEvent druidUsePowerEvent = new DruidUsePowerEvent(this.getPlayerWW(), playerWWS.size(), playerWWS);

        Bukkit.getPluginManager().callEvent(druidUsePowerEvent);

        if (druidUsePowerEvent.isCancelled()) {
            this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }

        this.getPlayerWW().sendMessageWithKey(Prefix.BLUE, "werewolf.roles.druid.perform",
                Formatter.number(druidUsePowerEvent.getDarkAura()),
                Formatter.format("&blocks&", game.getConfig().getValue(IntValueBase.DRUID_DISTANCE)));

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
