package fr.ph1lou.werewolfplugin.commands.roles.villager.info.librarian;

import fr.ph1lou.werewolfapi.annotations.PlayerCommand;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.librarian.LibrarianGiveBackEvent;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfplugin.roles.villagers.Librarian;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@PlayerCommand(key = "werewolf.role.librarian.request_command",
        descriptionKey = "",
        autoCompletion = false)
public class CommandSendToLibrarian implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) return;


        if (args.length == 0) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.parameters",
                    Formatter.number(1));
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
                .filter(role -> role.isKey(RoleBase.LIBRARIAN))
                .filter(roles -> ((IAffectedPlayers) roles).getAffectedPlayers().contains(playerWW))
                .forEach(roles -> {

                    ((IAffectedPlayers) roles).removeAffectedPlayer(playerWW);
                    LibrarianGiveBackEvent librarianGiveBackEvent =
                            new LibrarianGiveBackEvent(playerWW,
                                    roles.getPlayerWW(),
                                    sb2.toString());

                    Bukkit.getPluginManager().callEvent(librarianGiveBackEvent);

                    if (librarianGiveBackEvent.isCancelled()) {
                        playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.cancel");
                        return;
                    }

                    ((Librarian) roles).addStorage(sb2.toString());

                    playerWW.sendMessageWithKey(Prefix.GREEN , "werewolf.role.librarian.contribute");
                    find.set(true);
                    librarianGiveBackEvent.getTargetWW().sendMessageWithKey(
                            Prefix.GREEN , "werewolf.role.librarian.contribution",
                            Formatter.player(player.getName()),
                            Formatter.format("message",librarianGiveBackEvent.getInfo()));
                });


        if (!find.get()) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.role.librarian.prohibit");
        }


    }
}
