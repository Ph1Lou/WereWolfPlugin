package io.github.ph1lou.werewolfplugin.listeners.configs;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.ListenerManager;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.StatePlayer;
import io.github.ph1lou.werewolfapi.events.AnnouncementDeathEvent;
import io.github.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import io.github.ph1lou.werewolfapi.events.UpdatePlayerNameTag;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class ShowDeathRole extends ListenerManager {


    public ShowDeathRole(GetWereWolfAPI main) {
        super(main);
    }

    @EventHandler
    public void onAnnounceDeath(AnnouncementDeathEvent event) {

        WereWolfAPI game = main.getWereWolfAPI();

        event.setFormat(game.translate("werewolf.announcement.death_message_with_role"));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onUpdate(UpdatePlayerNameTag event) {

        WereWolfAPI game = main.getWereWolfAPI();

        PlayerWW playerWW = game.getPlayerWW(event.getPlayerUUID());

        if (playerWW == null) {
            return;
        }

        if (!playerWW.isState(StatePlayer.DEATH)) return;

        event.setSuffix(event.getSuffix()
                .replace(game.translate("werewolf.score_board.death"),
                        "")
                + game.translate(playerWW.getRole().getKey()));
    }

    @Override
    public void register(boolean isActive) {
        super.register(isActive);
        Bukkit.getPluginManager().callEvent(
                new UpdateNameTagEvent(Bukkit.getOnlinePlayers()));
    }


}
