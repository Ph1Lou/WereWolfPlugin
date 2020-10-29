package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enumlg.Sounds;
import io.github.ph1lou.werewolfapi.enumlg.State;
import io.github.ph1lou.werewolfapi.enumlg.StateLG;
import io.github.ph1lou.werewolfapi.events.DonEvent;
import io.github.ph1lou.werewolfapi.versions.VersionUtils;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandLovers implements Commands {


    private final Main main;

    public CommandLovers(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        GameManager game = main.getCurrentGame();
        String playername = player.getName();
        UUID uuid = player.getUniqueId();

        if (!game.getPlayersWW().containsKey(uuid)) {
            player.sendMessage(game.translate("werewolf.check.not_in_game"));
            return;
        }

        PlayerWW plg = game.getPlayersWW().get(uuid);


        if (!game.isState(StateLG.GAME)) {
            player.sendMessage(game.translate("werewolf.check.game_not_in_progress"));
            return;
        }

        if (plg.getLovers().isEmpty() && (plg.getAmnesiacLoverUUID()==null || !plg.getRevealAmnesiacLover())) {
            player.sendMessage(game.translate("werewolf.role.lover.not_in_pairs"));
            return;
        }
        if (args.length != 1 && args.length != 2) {
            player.sendMessage(game.translate("werewolf.check.parameters",1));
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

        if(args.length==1){

            if (plg.getLovers().size() > heart) {
                player.sendMessage(game.translate("werewolf.role.lover.not_enough_heart_send"));
                return;
            }

            player.setHealth(life-heart);
            int temp=heart;
            if(!plg.getLovers().isEmpty()){

                int don = heart / plg.getLovers().size();
                for (UUID uuid1 : plg.getLovers()) {
                    if(game.getPlayersWW().get(uuid1).isState(State.ALIVE)) {
                        Player playerCouple = Bukkit.getPlayer(uuid1);

                        if (playerCouple != null) {

                            if (VersionUtils.getVersionUtils().getPlayerMaxHealth(playerCouple) - playerCouple.getHealth() >= don) {
                                DonEvent donEvent = new DonEvent(uuid, uuid1, don);
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
            }
            else {

                Player playerCouple = Bukkit.getPlayer(plg.getAmnesiacLoverUUID());

                if (plg.getAmnesiacLoverUUID() != null && playerCouple != null) {

                    if (game.getPlayersWW().get(plg.getAmnesiacLoverUUID()).isState(State.ALIVE)) {
                        if (VersionUtils.getVersionUtils().getPlayerMaxHealth(playerCouple) - playerCouple.getHealth() >= heart) {
                            DonEvent donEvent = new DonEvent(uuid, plg.getAmnesiacLoverUUID(), heart);
                            Bukkit.getPluginManager().callEvent(donEvent);

                            if (!donEvent.isCancelled()) {
                                playerCouple.setHealth(playerCouple.getHealth() + heart);
                                playerCouple.sendMessage(game.translate("werewolf.role.lover.received", heart, playername));
                                player.sendMessage((game.translate("werewolf.role.lover.complete", heart, playerCouple.getName())));
                                Sounds.PORTAL.play(playerCouple);
                                temp -= heart;
                            } else player.sendMessage(game.translate("werewolf.check.cancel"));
                        } else
                            player.sendMessage(game.translate("werewolf.role.lover.too_many_heart", playerCouple.getName()));
                    }
                    else player.sendMessage(game.translate("werewolf.check.offline_player"));
                }
            }

            player.setHealth(player.getHealth()+temp);
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

            if (!game.getPlayersWW().get(argUUID).isState(State.ALIVE)) {
                player.sendMessage(game.translate("werewolf.check.offline_player"));
                return;
            }

            if (!plg.getLovers().contains(argUUID) && (plg.getAmnesiacLoverUUID()==null || !plg.getAmnesiacLoverUUID().equals(argUUID) || !plg.getRevealAmnesiacLover())) {
                player.sendMessage(game.translate("werewolf.role.lover.not_lover"));
                return;
            }
            player.setHealth(life-heart);

            if (VersionUtils.getVersionUtils().getPlayerMaxHealth(playerCouple) - playerCouple.getHealth() >= heart) {

                DonEvent donEvent = new DonEvent(uuid, argUUID, heart);
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
        }
    }
}
