package fr.ph1lou.werewolfplugin.configs;

import fr.ph1lou.werewolfapi.annotations.Configuration;
import fr.ph1lou.werewolfapi.annotations.ConfigurationBasic;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.enums.Sound;
import fr.ph1lou.werewolfapi.events.ActionBarEvent;
import fr.ph1lou.werewolfapi.events.werewolf.WereWolfCanHowlingEvent;
import fr.ph1lou.werewolfapi.events.werewolf.WereWolfHowlingEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfapi.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Configuration(config = @ConfigurationBasic(key = ConfigBase.WEREWOLF_HOWLING))
public class WerewolfHowling extends ListenerWerewolf {

    private final Set<IPlayerWW> playerWhoHaveHowling = new HashSet<>();
    private final Map<UUID, List<Location>> howlingLocation = new HashMap<>();


    public WerewolfHowling(WereWolfAPI game) {
        super(game);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onWerewolfRequestHowling(WereWolfCanHowlingEvent event) {

        if (!event.getPlayerWW().getRole().isWereWolf()) {
            return;
        }

        if (Math.abs(event.getPlayerWW().getHonor()) <= 1) {
            return;
        }

        if (playerWhoHaveHowling.contains(event.getPlayerWW())) {
            return;
        }

        event.setCanHowling(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWerewolfRequestHowlingFinal(WereWolfCanHowlingEvent event) {

        if (getGame().getConfig().isConfigActive(ConfigBase.WEREWOLF_HOWLING)) {
            return;
        }

        event.setCanHowling(false);
        event.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.configurations.werewolf_howling.disabled");

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onWerewolfHowlingFirstCheck(WereWolfHowlingEvent event) {

        event.setCancelled(!event.getTargetWW().getRole().isWereWolf());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onWerewolfHowling(WereWolfHowlingEvent event) {

        playerWhoHaveHowling.add(event.getPlayerWW());

        UUID targetUUID = event.getTargetWW().getUUID();

        event.getTargetWW().sendSound(Sound.WOLF_HOWL);
        event.getTargetWW().sendMessageWithKey(Prefix.RED, "werewolf.configurations.werewolf_howling.warn");

        List<Location> locationList = howlingLocation.getOrDefault(targetUUID, new ArrayList<>());
        locationList.add(event.getLocation());
        howlingLocation.put(targetUUID, locationList);
        BukkitUtils.scheduleSyncDelayedTask(getGame(),
                () -> howlingLocation.get(targetUUID).remove(event.getLocation()),
                event.getDuration());
    }

    @EventHandler
    public void onActionBarEvent(ActionBarEvent event) {

        List<Location> locationList = howlingLocation.get(event.getPlayerUUID());
        if (locationList == null || locationList.isEmpty()) {
            return;
        }

        Player player = Bukkit.getPlayer(event.getPlayerUUID());

        if (player == null) {
            return;
        }

        StringBuilder sb = event.getActionBar()
                .append(" ").append(getGame().translate("werewolf.configurations.werewolf_howling.action_bar"));

        locationList.forEach(location -> sb.append(" ").append(Utils.getArrow(player, location)));
    }


}
