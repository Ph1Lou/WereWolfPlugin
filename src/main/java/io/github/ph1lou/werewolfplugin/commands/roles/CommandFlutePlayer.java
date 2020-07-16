package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.State;
import io.github.ph1lou.werewolfapi.enumlg.StateLG;
import io.github.ph1lou.werewolfapi.events.EnchantedEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommandFlutePlayer implements Commands {

    final GetWereWolfAPI api;

    public CommandFlutePlayer(GetWereWolfAPI api) {
        this.api=api;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

            WereWolfAPI game = api.getWereWolfAPI();

            if (!(sender instanceof Player)) {
                sender.sendMessage(game.translate("werewolf.check.console"));
                return ;
            }

            Player player = (Player) sender;
            UUID uuid = player.getUniqueId();

            if(!game.getPlayersWW().containsKey(uuid)) {
                player.sendMessage(game.translate("werewolf.check.not_in_game"));
                return ;
            }

            PlayerWW plg = game.getPlayersWW().get(uuid);


            if (!game.isState(StateLG.GAME)) {
                player.sendMessage(game.translate("werewolf.check.game_not_in_progress"));
                return ;
            }

            if (!(plg.getRole().isDisplay("werewolf.role.flute_player.display"))){
                player.sendMessage(game.translate("werewolf.check.role", game.translate("werewolf.role.flute_player.display")));
                return ;
            }

            Roles flutePlayer = plg.getRole();

            if (args.length!=2 && args.length!=1) {
                player.sendMessage(game.translate("werewolf.check.parameters",2));
                return ;
            }

            if(!plg.isState(State.ALIVE)){
                player.sendMessage(game.translate("werewolf.check.death"));
                return ;
            }

            if(!((Power)flutePlayer).hasPower()) {
                player.sendMessage(game.translate("werewolf.check.power"));
                return ;
            }

            if(args.length==2 && args[0].equals(args[1])) {
                player.sendMessage(game.translate("werewolf.check.two_distinct_player"));
                return ;
            }


            for(String p:args) {

                Player playerArg = Bukkit.getPlayer(p);

                if (playerArg == null) {
                    player.sendMessage(game.translate("werewolf.check.offline_player"));
                    return;
                }

                UUID playerUUID = playerArg.getUniqueId();

                if (!game.getPlayersWW().containsKey(playerUUID) || game.getPlayersWW().get(playerUUID).isState(State.DEATH)) {
                    player.sendMessage(game.translate("werewolf.check.player_not_found"));
                    return;
                }

                if(p.equals(plg.getName())) {
                    player.sendMessage(game.translate("werewolf.check.not_yourself"));
                    return ;
                }

                if (!game.getPlayersWW().containsKey(playerUUID)) {
                    player.sendMessage(game.translate("werewolf.check.not_in_game_player"));
                    return;
                }

                if (((AffectedPlayers) flutePlayer).getAffectedPlayers().contains(playerUUID)) {
                    player.sendMessage(game.translate("werewolf.role.flute_player.already_enchant", playerArg.getName()));
                    return;
                }

                if (player.getLocation().distance(playerArg.getLocation()) > 100) {
                    player.sendMessage(game.translate("werewolf.role.flute_player.distance", playerArg.getName()));
                    return;
                }
            }

            List<UUID> listUUIDs = new ArrayList<>();

            for(String p:args) {
                Player enchanted =Bukkit.getPlayer(p);
                if (enchanted == null) return;
                UUID uuid1=enchanted.getUniqueId();
                listUUIDs.add(uuid1);
                ((AffectedPlayers) flutePlayer).addAffectedPlayer(uuid1);
                enchanted.sendMessage(game.translate("werewolf.role.flute_player.enchanted"));
                player.sendMessage(game.translate("werewolf.role.flute_player.perform",enchanted.getName()));
            }

            ((Power) flutePlayer).setPower(false);

            Bukkit.getPluginManager().callEvent(new EnchantedEvent(uuid,listUUIDs));

            game.checkVictory();
    }
}
