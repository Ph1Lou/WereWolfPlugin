package fr.ph1lou.werewolfplugin.configs;

import fr.ph1lou.werewolfapi.annotations.Configuration;
import fr.ph1lou.werewolfapi.annotations.ConfigurationBasic;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Camp;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.UpdatePlayerNameTagEvent;
import fr.ph1lou.werewolfapi.events.game.game_cycle.UpdateCompositionEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.AnnouncementDeathEvent;
import fr.ph1lou.werewolfapi.events.game.timers.RepartitionEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Configuration(config = @ConfigurationBasic(key = ConfigBase.HIDDEN_ROLES,
        loreKey = "werewolf.configurations.hidden_roles.description", incompatibleConfigs = ConfigBase.HONOR))
public class HiddenRoles extends ListenerWerewolf {

    @Nullable
    private IPlayerWW playerWW;

    public HiddenRoles(WereWolfAPI main) {
        super(main);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onRole(RepartitionEvent event) {

        if (this.getGame().getPlayersWW()
                .stream()
                .anyMatch(playerWW1 -> playerWW1.getRole().isKey(RoleBase.PRIESTESS))) {
            return;
        }

        List<IPlayerWW> playerWWs = this.getGame().getAlivePlayersWW()
                .stream()
                .filter(playerWW1 -> playerWW1.getRole().isCamp(Camp.VILLAGER))
                .collect(Collectors.toList());

        if (playerWWs.isEmpty()) {
            return;
        }

        Collections.shuffle(playerWWs, getGame().getRandom());

        this.playerWW = playerWWs.get(0);
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void sendDeathMessage(AnnouncementDeathEvent event) {

        if (this.playerWW == null) {
            return;
        }

        if (event.getTargetPlayer().equals(this.playerWW)) {
            return; //le rôle pivot voit les vrais rôles
        }

        if (event.getTargetPlayer().equals(event.getPlayerWW())) {
            return; //le mort voit son vrai rôle
        }

        IPlayerWW playerWW = event.getTargetPlayer();

        if (playerWW.getRole().isNeutral()) {
            if (getGame().getRandom().nextFloat() > 0.95) {
                event.setRole("werewolf.configurations.hidden_roles.magic");
            }
        } else if (getGame().getRandom().nextFloat() < 0.8) {

            if (this.playerWW.isState(StatePlayer.ALIVE)) {
                if (playerWW.getRole().isWereWolf()) {
                    event.setRole("werewolf.configurations.hidden_roles.magic");
                }

            } else {
                if (!playerWW.getRole().isWereWolf()) {
                    event.setRole("werewolf.configurations.hidden_roles.magic");
                }
            }
        } else {
            if (this.playerWW.isState(StatePlayer.ALIVE)) {
                if (!playerWW.getRole().isWereWolf()) {
                    event.setRole("werewolf.configurations.hidden_roles.magic");
                }
            } else {
                if (playerWW.getRole().isWereWolf()) {
                    event.setRole("werewolf.configurations.hidden_roles.magic");
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCompositionUpdate(UpdateCompositionEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onUpdate(UpdatePlayerNameTagEvent event) {

        IPlayerWW playerWW = this.getGame().getPlayerWW(event.getPlayerUUID()).orElse(null);

        if (playerWW == null) {
            return;
        }

        if (!playerWW.isState(StatePlayer.DEATH)) return;

        event.setSuffix("");
    }
}
