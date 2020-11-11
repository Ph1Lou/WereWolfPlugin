package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.Camp;
import io.github.ph1lou.werewolfapi.enumlg.RolesBase;
import io.github.ph1lou.werewolfapi.enumlg.StatePlayer;
import io.github.ph1lou.werewolfapi.events.SeerEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.Display;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfapi.versions.VersionUtils;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandSeer implements Commands {

    private final Main main;

    public CommandSeer(Main main) {
        this.main = main;
    }


    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();
        UUID uuid = player.getUniqueId();
        PlayerWW plg = game.getPlayersWW().get(uuid);
        Roles seer = plg.getRole();

        if (args.length != 1) {
            player.sendMessage(game.translate("werewolf.check.player_input"));
            return;
        }

        if(!((Power)seer).hasPower()) {
            player.sendMessage(game.translate("werewolf.check.power"));
            return;
        }
        Player playerArg = Bukkit.getPlayer(args[0]);

        if (playerArg == null) {
            player.sendMessage(game.translate("werewolf.check.offline_player"));
            return;
        }
        UUID argUUID = playerArg.getUniqueId();

        if(!game.getPlayersWW().containsKey(argUUID) || !game.getPlayersWW().get(argUUID).isState(StatePlayer.ALIVE)){
            player.sendMessage(game.translate("werewolf.check.player_not_found"));
            return;
        }

        double life = VersionUtils.getVersionUtils().getPlayerMaxHealth(player);

        if (life<7) {
            player.sendMessage(game.translate("werewolf.role.seer.not_enough_life"));
        }
        else {
            PlayerWW plg1 = game.getPlayersWW().get(argUUID);
            Roles role1 = plg1.getRole();

            String camp = "werewolf.categories.villager";

            if ((role1 instanceof Display && ((Display) role1).isDisplayCamp(Camp.NEUTRAL)) || role1.isNeutral()) {
                camp = "werewolf.categories.neutral";
            } else if ((role1 instanceof Display && ((Display) role1).isDisplayCamp(Camp.WEREWOLF)) || (!(role1 instanceof Display) && role1.isWereWolf())) {
                camp = "werewolf.categories.werewolf";
            }


            SeerEvent seerEvent = new SeerEvent(uuid, argUUID, camp);
            ((Power) seer).setPower(false);
            Bukkit.getPluginManager().callEvent(seerEvent);

            if (seerEvent.isCancelled()) {
                player.sendMessage(game.translate("werewolf.check.cancel"));
                return;
            }

            ((AffectedPlayers) seer).addAffectedPlayer(argUUID);

            if (seerEvent.getCamp().equals("werewolf.categories.villager")) {
                VersionUtils.getVersionUtils().setPlayerMaxHealth(player, life - 6);
                if (player.getHealth() > life - 6) {
                    player.setHealth(life - 6);
                }
                player.sendMessage(game.translate("werewolf.role.seer.see_villager"));
                if (seer.isKey(RolesBase.CHATTY_SEER.getKey())) {
                    Bukkit.broadcastMessage(game.translate("werewolf.role.chatty_seer.see_perform", game.translate("werewolf.categories.villager")));
                }
                plg.addKLostHeart(6);
            } else if (seerEvent.getCamp().equals("werewolf.categories.werewolf")) {
                player.sendMessage(game.translate("werewolf.role.seer.see_perform", game.translate("werewolf.categories.werewolf")));
                if (seer.isKey(RolesBase.CHATTY_SEER.getKey())) {
                    Bukkit.broadcastMessage(game.translate("werewolf.role.chatty_seer.see_perform", game.translate("werewolf.categories.werewolf")));
                }
            } else {
                player.sendMessage(game.translate("werewolf.role.seer.see_perform", game.translate("werewolf.categories.neutral")));
                if (seer.isKey(RolesBase.CHATTY_SEER.getKey())) {
                    Bukkit.broadcastMessage(game.translate("werewolf.role.chatty_seer.see_perform", game.translate("werewolf.categories.neutral")));
                }
            }
        }
    }
}
