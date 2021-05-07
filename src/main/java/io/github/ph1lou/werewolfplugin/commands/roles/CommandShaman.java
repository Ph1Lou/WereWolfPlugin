package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.ICommands;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.events.roles.shaman.ShamanEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.IDisplay;
import io.github.ph1lou.werewolfapi.rolesattributs.IRole;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
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

        UUID argUUID = UUID.fromString(args[0]);
        IPlayerWW pVictim = game.getPlayerWW(argUUID);

        if (game.getScore().getTimer() - pVictim.getDeathTime() > 30 ||
                ((IAffectedPlayers) playerWW.getRole()).getAffectedPlayers().contains(pVictim)) {
            playerWW.sendMessageWithKey("werewolf.role.shaman.cannot_use");
            return;
        }

        Optional<IPlayerWW> pKiller = pVictim.getLastKiller();
        ((IAffectedPlayers) playerWW.getRole()).addAffectedPlayer(pVictim);

        ShamanEvent shamanEvent = new ShamanEvent(pVictim, pKiller);

        if (shamanEvent.isCancelled()) {
            playerWW.sendMessageWithKey("werewolf.role.shaman.killer_no_info", pVictim.getName());
            return;
        }

        if (!pKiller.isPresent()) {
            playerWW.sendMessageWithKey("werewolf.role.shaman.killer_pve", pVictim.getName());
            return;
        }

        playerWW.removePlayerMaxHealth(2);

        if (game.getRandom().nextBoolean()) {
            playerWW.sendMessageWithKey("werewolf.role.shaman.killer_name", pVictim.getName(), pKiller.get().getName());
        } else {
            IRole role = pKiller.get().getRole();
            playerWW.sendMessageWithKey("werewolf.role.shaman.killer_role", pVictim.getName(),
                    role instanceof IDisplay ? game.translate(((IDisplay) role).getDisplayRole())
                            : game.translate(role.getKey()));
        }

    }
}
