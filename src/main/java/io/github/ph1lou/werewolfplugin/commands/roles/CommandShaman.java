package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.ICommands;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.events.roles.shaman.ShamanEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.IDisplay;
import io.github.ph1lou.werewolfapi.rolesattributs.IRole;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public class CommandShaman implements ICommands {

    private final Main main;

    public CommandShaman(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();
        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid);

        if (playerWW == null) {
            return;
        }

        UUID argUUID = UUID.fromString(args[0]);
        IPlayerWW playerWW1 = game.getPlayerWW(argUUID);

        if (playerWW1 == null) {
            return;
        }

        if (game.getScore().getTimer() - playerWW1.getDeathTime() > 30 ||
                ((IAffectedPlayers) playerWW.getRole()).getAffectedPlayers().contains(playerWW1)) {
            playerWW.sendMessageWithKey("werewolf.role.shaman.cannot_use");
            return;
        }

        Optional<IPlayerWW> pKiller = playerWW1.getLastKiller();
        ((IAffectedPlayers) playerWW.getRole()).addAffectedPlayer(playerWW1);

        ShamanEvent shamanEvent = new ShamanEvent(playerWW1, pKiller.orElse(null));

        if (shamanEvent.isCancelled()) {
            playerWW.sendMessageWithKey("werewolf.role.shaman.killer_no_info", playerWW1.getName());
            return;
        }

        if (!pKiller.isPresent()) {
            playerWW.sendMessageWithKey("werewolf.role.shaman.killer_pve", playerWW1.getName());
            return;
        }

        playerWW.removePlayerMaxHealth(2);

        if (game.getRandom().nextBoolean()) {
            playerWW.sendMessageWithKey("werewolf.role.shaman.killer_name", playerWW1.getName(), pKiller.get().getName());
        } else {
            IRole role = pKiller.get().getRole();
            playerWW.sendMessageWithKey("werewolf.role.shaman.killer_role", playerWW1.getName(),
                    role instanceof IDisplay ? game.translate(((IDisplay) role).getDisplayRole())
                            : game.translate(role.getKey()));
        }

    }
}
