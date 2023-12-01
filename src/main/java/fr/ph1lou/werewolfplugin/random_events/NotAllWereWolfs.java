package fr.ph1lou.werewolfplugin.random_events;

import fr.ph1lou.werewolfapi.annotations.RandomEvent;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.EventBase;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Camp;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UpdateCompositionReason;
import fr.ph1lou.werewolfapi.events.UpdatePlayerNameTagEvent;
import fr.ph1lou.werewolfapi.events.game.game_cycle.UpdateCompositionEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.AnnouncementDeathEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.game.timers.RepartitionEvent;
import fr.ph1lou.werewolfapi.events.random_events.NotAllWerewolfEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfplugin.Register;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RandomEvent(key = EventBase.NOT_ALL_WEREWOLFS, loreKey = "werewolf.random_events.not_all_werewolfs.description")
public class NotAllWereWolfs extends ListenerWerewolf {

    public NotAllWereWolfs(WereWolfAPI game) {
        super(game);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onRoles(RepartitionEvent event) {

        NotAllWerewolfEvent notAllWerewolfEvent = new NotAllWerewolfEvent();

        Bukkit.getPluginManager().callEvent(notAllWerewolfEvent);

        if (notAllWerewolfEvent.isCancelled()) {
            return;
        }

        List<IRole> defaultWereWolfs = this.getGame().getPlayersWW().stream()
                .filter(this::isDefaultWereWolf)
                .map(IPlayerWW::getRole)
                .collect(Collectors.toList());

        defaultWereWolfs.forEach(r -> this.getGame().getConfig().removeOneRole(r.getKey()));
        defaultWereWolfs.forEach(r -> this.getGame().getConfig().addOneRole(RoleBase.WEREWOLF));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDeathAnnounce(AnnouncementDeathEvent event) {
        if (!this.isDefaultWereWolf(event.getRole())) return;

        event.setRole(RoleBase.WEREWOLF);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAnnounceDeath(FinalDeathEvent event) {
        if (!this.isDefaultWereWolf(event.getPlayerWW())) return;
        this.getGame().getConfig().removeOneRole(RoleBase.WEREWOLF);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCompositionUpdate(UpdateCompositionEvent event) {
        if (!this.isDefaultWereWolf(event.getKey())) return;
        if (event.getReason() != UpdateCompositionReason.DEATH) return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onUpdatePlayerNameTag(UpdatePlayerNameTagEvent event) {
        IPlayerWW playerWW = this.getGame().getPlayerWW(event.getPlayerUUID()).orElse(null);

        if (playerWW == null) return;

        if (!playerWW.isState(StatePlayer.DEATH)) return;

        if (!this.isDefaultWereWolf(playerWW)) return;

        if (this.getGame().getConfig().isConfigActive(ConfigBase.SHOW_ROLE_TO_DEATH)) {
            event.setSuffix(event.getSuffix()
                    .replace(this.getGame().translate(playerWW.getRole().getKey()),
                            "")
                    + this.getGame().translate(RoleBase.WEREWOLF));
        } else if (this.getGame().getConfig().isConfigActive(ConfigBase.SHOW_ROLE_CATEGORY_TO_DEATH)) {
            event.setSuffix(event.getSuffix()
                    .replace(this.getGame().translate(playerWW.getRole().getCamp().getKey()),
                            "")
                    + this.getGame().translate(Camp.WEREWOLF.getKey()));
        }
    }

    private boolean isDefaultWereWolf(IPlayerWW playerWW) {
        return this.isDefaultWereWolf(playerWW.getRole());
    }

    private boolean isDefaultWereWolf(IRole role) {
        return this.isDefaultWereWolf(role.getKey());
    }

    private boolean isDefaultWereWolf(String key) {
        return Register.get().getRolesRegister().stream().filter(r -> r.getMetaDatas().key().equalsIgnoreCase(key))
                .anyMatch(r -> r.getMetaDatas().category() == Category.WEREWOLF ||
                               Objects.equals(r.getMetaDatas().key(), RoleBase.WHITE_WEREWOLF));
    }
}
