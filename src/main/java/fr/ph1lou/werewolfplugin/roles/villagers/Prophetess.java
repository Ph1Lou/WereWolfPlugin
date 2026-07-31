package fr.ph1lou.werewolfplugin.roles.villagers;

import fr.ph1lou.werewolfapi.annotations.Configuration;
import fr.ph1lou.werewolfapi.annotations.ConfigurationBasic;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleImpl;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Role(key = RoleBase.PROPHETESS,
        category = Category.VILLAGER,
        attribute = RoleAttribute.MINOR_INFORMATION,
        defaultAura = Aura.NEUTRAL,
        configurations = {@Configuration(config = @ConfigurationBasic(key = "werewolf.roles.prophetess.configurations.prophetess_every_other_day", defaultValue = true))})
public class Prophetess extends RoleImpl {

    private final Map<UUID, Aura> previousAuras = new HashMap<>();
    private boolean initialized = false;
    private int dayNumber = -8;

    public Prophetess(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDay(DayEvent event) {
        if (!isAbilityEnabled()) return;

        if (game.getConfig().isConfigActive("werewolf.roles.prophetess.configurations.prophetess_every_other_day")
                && event.getNumber() == dayNumber + 1) {
            return;
        }
        dayNumber = event.getNumber();

        if (!initialized) {
            initialized = true;
            snapshotAuras();
            return;
        }

        if (this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            List<String> changedPlayers = new ArrayList<>();

            for (IPlayerWW playerWW : game.getAlivePlayersWW()) {
                Aura previousAura = this.previousAuras.get(playerWW.getUUID());
                if (previousAura == null) continue;
                Aura currentAura = playerWW.getRole().getAura();
                if (previousAura != currentAura) {
                    changedPlayers.add(playerWW.getName());
                }
            }

            if (changedPlayers.isEmpty()) {
                this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW,
                        "werewolf.roles.prophetess.no_change");
            } else {
                this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW,
                        "werewolf.roles.prophetess.list",
                        Formatter.format("&list&", String.join(", ", changedPlayers)));
            }
        }

        snapshotAuras();
    }

    private void snapshotAuras() {
        this.previousAuras.clear();
        for (IPlayerWW playerWW : game.getAlivePlayersWW()) {
            this.previousAuras.put(playerWW.getUUID(), playerWW.getRole().getAura());
        }
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.prophetess.description"))
                .build();
    }

    @Override
    public void recoverPower() {
    }
}
