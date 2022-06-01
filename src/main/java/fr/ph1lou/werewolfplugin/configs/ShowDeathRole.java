package fr.ph1lou.werewolfplugin.configs;

import fr.ph1lou.werewolfapi.annotations.Configuration;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import fr.ph1lou.werewolfapi.events.UpdatePlayerNameTagEvent;
import fr.ph1lou.werewolfapi.events.game.game_cycle.UpdateCompositionEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.AnnouncementDeathEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

@Configuration(key = ConfigBase.SHOW_ROLE_TO_DEATH, defaultValue = true,
        meetUpValue = true,
        incompatibleConfigs = {ConfigBase.SHOW_ROLE_CATEGORY_TO_DEATH})
public class ShowDeathRole extends ListenerWerewolf {

    public ShowDeathRole(WereWolfAPI main) {
        super(main);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onAnnounceDeath(AnnouncementDeathEvent event) {

        event.setFormat("werewolf.announcement.death_message_with_role");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCompositionUpdate(UpdateCompositionEvent event) {
        event.setCancelled(false);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onUpdate(UpdatePlayerNameTagEvent event) {

        WereWolfAPI game = this.getGame();

        IPlayerWW playerWW = game.getPlayerWW(event.getPlayerUUID()).orElse(null);

        if (playerWW == null) {
            return;
        }

        if (!playerWW.isState(StatePlayer.DEATH)) return;

        event.setSuffix(event.getSuffix()
                .replace(game.translate("werewolf.score_board.death"),
                        "")
                + game.translate(playerWW.getDeathRole()));
    }


    @Override
    public void register(boolean isActive) {
        super.register(isActive);
        Bukkit.getOnlinePlayers().forEach(player -> Bukkit.getPluginManager().callEvent(
                new UpdateNameTagEvent(player)));
    }


}
