package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.RolesBase;
import io.github.ph1lou.werewolfapi.enumlg.StateGame;
import io.github.ph1lou.werewolfapi.enumlg.StatePlayer;
import io.github.ph1lou.werewolfapi.events.LibrarianGiveBackEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.Storage;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class CommandSendToLibrarian implements Commands {

    private final Main main;

    public CommandSendToLibrarian(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();
        UUID uuid = player.getUniqueId();

        if (!game.isState(StateGame.GAME)) {
            player.sendMessage(game.translate("werewolf.check.game_not_in_progress"));
            return;
        }

        if (args.length == 0) {
            player.sendMessage(game.translate("werewolf.check.parameters",1));
            return;
        }

        AtomicBoolean find = new AtomicBoolean(false);

        StringBuilder sb2 = new StringBuilder();

        for (String w : args) {
            sb2.append(w).append(" ");
        }
        game.getPlayersWW().values()
                .stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .filter(playerWW -> playerWW.getRole().isKey(RolesBase.LIBRARIAN.getKey()))
                .map(PlayerWW::getRole)
                .filter(roles -> ((AffectedPlayers) roles).getAffectedPlayers().contains(uuid))
                .forEach(roles -> {

                    ((AffectedPlayers) roles).removeAffectedPlayer(uuid);
                    LibrarianGiveBackEvent librarianGiveBackEvent =
                            new LibrarianGiveBackEvent(uuid,
                                    roles.getPlayerUUID(),
                                    sb2.toString());

                    Bukkit.getPluginManager().callEvent(librarianGiveBackEvent);

                    if (librarianGiveBackEvent.isCancelled()) {
                        player.sendMessage(game.translate("werewolf.check.cancel"));
                        return;
                    }

                    ((Storage) roles).getStorage().add(sb2.toString());

                    player.sendMessage(game.translate("werewolf.role.librarian.contribute"));
                    find.set(true);
                    Player librarian = Bukkit.getPlayer(librarianGiveBackEvent.getTargetUUID());
                    if (librarian != null) {
                        librarian.sendMessage(game.translate(
                                "werewolf.role.librarian.contribution",
                                player.getName(),
                                librarianGiveBackEvent.getInfo()));
                    }
                });


        if (!find.get()) {
            player.sendMessage(game.translate("werewolf.role.librarian.prohibit"));
        }


    }
}
