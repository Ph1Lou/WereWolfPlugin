package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.Formatter;
import io.github.ph1lou.werewolfapi.ICommand;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Prefix;
import io.github.ph1lou.werewolfapi.enums.RolesBase;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.roles.librarian.LibrarianGiveBackEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import io.github.ph1lou.werewolfplugin.roles.villagers.Librarian;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class CommandSendToLibrarian implements ICommand {


    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) return;


        if (args.length == 0) {
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.parameters",
                    Formatter.format("&number&",1));
            return;
        }

        AtomicBoolean find = new AtomicBoolean(false);

        StringBuilder sb2 = new StringBuilder();

        for (String w : args) {
            sb2.append(w).append(" ");
        }
        game.getPlayersWW()
                .stream()
                .filter(playerWW1 -> playerWW1.isState(StatePlayer.ALIVE))
                .map(IPlayerWW::getRole)
                .filter(role -> role.isKey(RolesBase.LIBRARIAN.getKey()))
                .filter(roles -> ((IAffectedPlayers) roles).getAffectedPlayers().contains(playerWW))
                .forEach(roles -> {

                    ((IAffectedPlayers) roles).removeAffectedPlayer(playerWW);
                    LibrarianGiveBackEvent librarianGiveBackEvent =
                            new LibrarianGiveBackEvent(playerWW,
                                    roles.getPlayerWW(),
                                    sb2.toString());

                    Bukkit.getPluginManager().callEvent(librarianGiveBackEvent);

                    if (librarianGiveBackEvent.isCancelled()) {
                        playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.cancel");
                        return;
                    }

                    ((Librarian) roles).addStorage(sb2.toString());

                    playerWW.sendMessageWithKey(Prefix.GREEN.getKey() , "werewolf.role.librarian.contribute");
                    find.set(true);
                    librarianGiveBackEvent.getTargetWW().sendMessageWithKey(
                            Prefix.GREEN.getKey() , "werewolf.role.librarian.contribution",
                            Formatter.format("&player&",player.getName()),
                            Formatter.format("message",librarianGiveBackEvent.getInfo()));
                });


        if (!find.get()) {
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.role.librarian.prohibit");
        }


    }
}
