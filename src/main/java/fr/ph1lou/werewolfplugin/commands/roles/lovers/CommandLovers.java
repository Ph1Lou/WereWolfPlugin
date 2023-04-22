package fr.ph1lou.werewolfplugin.commands.roles.lovers;

import fr.ph1lou.werewolfapi.annotations.PlayerCommand;
import fr.ph1lou.werewolfapi.basekeys.LoverBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.enums.Sound;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.lovers.DonEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.lovers.ILover;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfplugin.roles.lovers.AmnesiacLover;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@PlayerCommand(key = "werewolf.lovers.lover.command", descriptionKey = "",
        argNumbers = {1, 2},
        statesGame = StateGame.GAME,
        statesPlayer = StatePlayer.ALIVE)
public class CommandLovers implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        String playerName = player.getName();
        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) return;

        if (playerWW.getLovers().isEmpty()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.lovers.lover.not_in_pairs");
            return;
        }

        int heart;

        try {
            heart = Integer.parseInt(args[0]);
        } catch (NumberFormatException ignored) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.number_required");
            return;
        }

        if (heart >= 100) {
            playerWW.sendMessageWithKey(Prefix.GREEN, "werewolf.lovers.lover.100");
            return;
        }

        if (args.length == 1) {

            List<ILover> lovers = playerWW.getLovers().stream()
                    .filter(loverAPI1 -> !loverAPI1.isKey(LoverBase.CURSED_LOVER))
                    .filter(loverAPI1 -> !loverAPI1.isKey(LoverBase.AMNESIAC_LOVER) || ((AmnesiacLover) loverAPI1).isRevealed())
                    .collect(Collectors.toList());

            if (lovers.isEmpty()) {
                playerWW.sendMessageWithKey(Prefix.RED, "werewolf.lovers.lover.not_in_pairs");
                return;
            }

            lovers.forEach(loverAPI1 -> {
                double health = player.getHealth() * heart / 100f;
                AtomicReference<Double> temp = new AtomicReference<>((double) 0);

                double don = health / (float) (loverAPI1.getLovers().size() - 1);

                List<IPlayerWW> lovers2 = loverAPI1.getLovers()
                        .stream()
                        .filter(playerWW1 -> !playerWW.equals(playerWW1))
                        .filter(playerWW1 -> playerWW1.isState(StatePlayer.ALIVE))
                        .collect(Collectors.toList());

                if (lovers2.isEmpty()) {
                    playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.player_not_found");
                    return;
                }
                lovers2.forEach(playerWW1 -> {
                    Player playerCouple = Bukkit.getPlayer(playerWW1.getUUID());

                    if (playerCouple != null) {

                        if (playerWW1.getMaxHealth() - playerCouple.getHealth() >= don) {
                            DonEvent donEvent = new DonEvent(playerWW, playerWW1, heart);
                            Bukkit.getPluginManager().callEvent(donEvent);

                            if (!donEvent.isCancelled()) {
                                playerCouple.setHealth(playerCouple.getHealth() + don);
                                temp.updateAndGet(v -> v + don);
                                playerWW1.sendMessageWithKey(Prefix.YELLOW, "werewolf.lovers.lover.received",
                                        Formatter.number(heart),
                                        Formatter.player(playerName));
                                playerWW.sendMessageWithKey(Prefix.GREEN, "werewolf.lovers.lover.complete",
                                        Formatter.number(heart),
                                        Formatter.player(playerCouple.getName()));
                                playerWW.sendSound(Sound.PORTAL);
                            } else {
                                playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
                            }
                        } else {
                            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.lovers.lover.too_many_heart",
                                    Formatter.player(playerCouple.getName()));
                        }
                    }
                });

                player.setHealth(player.getHealth() - temp.get());
            });
        } else {
            if (args[1].equals(playerName)) {
                playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.not_yourself");
                return;
            }
            Player playerCouple = Bukkit.getPlayer(args[1]);

            if (playerCouple == null) {
                playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.offline_player");
                return;
            }

            UUID argUUID = playerCouple.getUniqueId();
            IPlayerWW playerWW1 = game.getPlayerWW(argUUID).orElse(null);

            if (playerWW1 == null) return;

            if (!playerWW1.isState(StatePlayer.ALIVE)) {
                playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.player_not_found");
                return;
            }

            double don = player.getHealth() * heart / 100f;

            Optional<? extends ILover> iLover = playerWW.getLovers().stream()
                    .filter(loverAPI1 -> !loverAPI1.isKey(LoverBase.CURSED_LOVER))
                    .filter(loverAPI1 -> loverAPI1.getLovers().contains(playerWW1))
                    .filter(loverAPI1 -> !loverAPI1.isKey(LoverBase.AMNESIAC_LOVER) || ((AmnesiacLover) loverAPI1).isRevealed())
                    .findFirst();

            if (iLover.isPresent()) {
                iLover.ifPresent(loverAPI1 -> {

                    if (playerWW1.getMaxHealth() - playerCouple.getHealth() >= heart) {

                        DonEvent donEvent = new DonEvent(playerWW, playerWW1, heart);
                        Bukkit.getPluginManager().callEvent(donEvent);

                        if (!donEvent.isCancelled()) {
                            playerCouple.setHealth(playerCouple.getHealth() + don);
                            player.setHealth(player.getHealth() - don);
                            playerWW1.sendMessageWithKey(Prefix.YELLOW, "werewolf.lovers.lover.received",
                                    Formatter.number(heart),
                                    Formatter.player(playerName));
                            playerWW.sendMessageWithKey(Prefix.GREEN, "werewolf.lovers.lover.complete",
                                    Formatter.number(heart),
                                    Formatter.player(playerCouple.getName()));
                            playerWW.sendSound(Sound.PORTAL);
                        } else {
                            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
                        }
                    } else {
                        playerWW.sendMessageWithKey(Prefix.RED, "werewolf.lovers.lover.too_many_heart",
                                Formatter.player(playerCouple.getName()));
                    }
                });
            } else {

                Optional<? extends ILover> iLover2 = playerWW.getLovers().stream()
                        .filter(loverAPI1 -> !loverAPI1.isKey(LoverBase.CURSED_LOVER))
                        .filter(loverAPI1 -> !loverAPI1.isKey(LoverBase.AMNESIAC_LOVER) || ((AmnesiacLover) loverAPI1).isRevealed())
                        .findFirst();

                if (iLover2.isPresent()) {
                    playerWW.sendMessageWithKey(Prefix.RED, "werewolf.lovers.lover.not_lover");
                } else {
                    playerWW.sendMessageWithKey(Prefix.RED, "werewolf.lovers.lover.not_in_pairs");
                }
            }

        }
    }
}
