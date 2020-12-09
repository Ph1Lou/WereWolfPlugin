package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.LoverAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.RolesBase;
import io.github.ph1lou.werewolfapi.enums.Sounds;
import io.github.ph1lou.werewolfapi.enums.StateGame;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.DonEvent;
import io.github.ph1lou.werewolfapi.versions.VersionUtils;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.roles.lovers.AmnesiacLover;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public class CommandLovers implements Commands {


    private final Main main;

    public CommandLovers(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();
        String playername = player.getName();
        UUID uuid = player.getUniqueId();
        PlayerWW playerWW = game.getPlayerWW(uuid);

        if (playerWW == null) return;

        if (!game.isState(StateGame.GAME)) {
            player.sendMessage(game.translate("werewolf.check.game_not_in_progress"));
            return;
        }

        if (playerWW.getLovers().isEmpty()) {
            player.sendMessage(game.translate("werewolf.role.lover.not_in_pairs"));
            return;
        }
        if (args.length != 1 && args.length != 2) {
            player.sendMessage(game.translate("werewolf.check.parameters", 1));
            return;
        }
        int heart;
        double life = player.getHealth();
        try {
            heart = Integer.parseInt(args[0]);
        } catch (NumberFormatException ignored) {
            player.sendMessage(game.translate("werewolf.check.number_required"));
            return;
        }
        if (life<=heart) {
            player.sendMessage(game.translate("werewolf.role.lover.not_enough_heart"));
            return;
        }

        if(args.length==1) {

            playerWW.getLovers().stream()
                    .filter(loverAPI1 -> !loverAPI1.isKey(RolesBase.CURSED_LOVER.getKey()))
                    .filter(loverAPI1 -> !loverAPI1.isKey(RolesBase.AMNESIAC_LOVER.getKey()) || ((AmnesiacLover) loverAPI1).isRevealed())
                    .forEach(loverAPI1 -> {

                        if (!loverAPI1.isKey(RolesBase.CURSED_LOVER.getKey())) {
                            if (loverAPI1.getLovers().size() > heart) {
                                player.sendMessage(game.translate("werewolf.role.lover.not_enough_heart_send"));
                                return;
                            }

                            player.setHealth(life - heart);
                            int temp = heart;
                            int don = heart / (loverAPI1.getLovers().size()-1);

                            for (PlayerWW playerWW1 : loverAPI1.getLovers()) {

                                if (playerWW1.isState(StatePlayer.ALIVE) && !playerWW1.equals(playerWW)) {

                                    Player playerCouple = Bukkit.getPlayer(playerWW1.getUUID());

                                    if (playerCouple != null) {

                                        if (VersionUtils.getVersionUtils().getPlayerMaxHealth(playerCouple) - playerCouple.getHealth() >= don) {
                                            DonEvent donEvent = new DonEvent(playerWW, playerWW1, don);
                                            Bukkit.getPluginManager().callEvent(donEvent);

                                            if (!donEvent.isCancelled()) {
                                                playerCouple.setHealth(playerCouple.getHealth() + don);
                                                playerCouple.sendMessage(game.translate("werewolf.role.lover.received", don, playername));
                                                player.sendMessage((game.translate("werewolf.role.lover.complete", don, playerCouple.getName())));
                                                Sounds.PORTAL.play(playerCouple);
                                                temp -= don;
                                            } else player.sendMessage(game.translate("werewolf.check.cancel"));
                                        } else
                                            player.sendMessage(game.translate("werewolf.role.lover.too_many_heart", playerCouple.getName()));
                                    }
                                }
                            }

                            player.setHealth(player.getHealth() + temp);
                        }
                    });


        }
        else {
            if (args[1].equals(playername)) {
                player.sendMessage(game.translate("werewolf.check.not_yourself"));
                return;
            }
            Player playerCouple = Bukkit.getPlayer(args[1]);

            if (playerCouple == null) {
                player.sendMessage(game.translate("werewolf.check.offline_player"));
                return;
            }

            UUID argUUID = playerCouple.getUniqueId();
            PlayerWW playerWW1 = game.getPlayerWW(argUUID);

            if (playerWW1 == null) return;

            if (!playerWW1.isState(StatePlayer.ALIVE)) {
                player.sendMessage(game.translate("werewolf.check.offline_player"));
                return;
            }

            Optional<LoverAPI> loverAPI = playerWW.getLovers().stream()
                    .filter(loverAPI1 -> !loverAPI1.isKey(RolesBase.CURSED_LOVER.getKey()))
                    .filter(loverAPI1 -> loverAPI1.getLovers().contains(playerWW1))
                    .filter(loverAPI1 -> !loverAPI1.isKey(RolesBase.AMNESIAC_LOVER.getKey()) || ((AmnesiacLover) loverAPI1).isRevealed())
                    .findFirst();

            if (loverAPI.isPresent()) {
                loverAPI.ifPresent(loverAPI1 -> {

                    player.setHealth(life - heart);

                    if (VersionUtils.getVersionUtils().getPlayerMaxHealth(playerCouple) - playerCouple.getHealth() >= heart) {

                        DonEvent donEvent = new DonEvent(playerWW, playerWW1, heart);
                        Bukkit.getPluginManager().callEvent(donEvent);

                        if (!donEvent.isCancelled()) {
                            playerCouple.setHealth(playerCouple.getHealth() + heart);
                            playerCouple.sendMessage(game.translate("werewolf.role.lover.received", heart, playername));
                            player.sendMessage((game.translate("werewolf.role.lover.complete", heart, args[1])));
                        } else {
                            player.sendMessage(game.translate("werewolf.check.cancel"));
                            player.setHealth(player.getHealth() + heart);
                        }
                    } else {
                        player.sendMessage(game.translate("werewolf.role.lover.too_many_heart", args[1]));
                        player.setHealth(player.getHealth() + heart);
                    }
                });
            } else {
                player.sendMessage(game.translate("werewolf.role.lover.not_lover"));
            }

        }
    }
}
