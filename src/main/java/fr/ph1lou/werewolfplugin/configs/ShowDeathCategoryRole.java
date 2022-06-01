package fr.ph1lou.werewolfplugin.configs;

import fr.ph1lou.werewolfapi.annotations.Configuration;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import fr.ph1lou.werewolfapi.events.UpdatePlayerNameTagEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.AnnouncementDeathEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

@Configuration(key = ConfigBase.SHOW_ROLE_CATEGORY_TO_DEATH,
        incompatibleConfigs = {ConfigBase.SHOW_ROLE_TO_DEATH})
public class ShowDeathCategoryRole extends ListenerWerewolf {

    public ShowDeathCategoryRole(WereWolfAPI main) {
        super(main);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onAnnounceDeath(AnnouncementDeathEvent event) {

        event.setFormat("werewolf.announcement.death_message_with_camp");

        event.setRole(event.getPlayerWW().getRole().getCamp().getKey());
    }

    @EventHandler
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
                + game.translate(playerWW.getRole().getCamp().getKey()));
    }

    @Override
    public void register(boolean isActive) {
        super.register(isActive);
        Bukkit.getOnlinePlayers().forEach(player -> Bukkit.getPluginManager().callEvent(
                new UpdateNameTagEvent(player)));

    }
}
