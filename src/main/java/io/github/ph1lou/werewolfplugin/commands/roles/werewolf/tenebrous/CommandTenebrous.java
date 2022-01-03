package io.github.ph1lou.werewolfplugin.commands.roles.werewolf.tenebrous;

import io.github.ph1lou.werewolfapi.ICommand;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.PotionModifier;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Day;
import io.github.ph1lou.werewolfapi.enums.Prefix;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.roles.tenebrous_werewolf.TenebrousEvent;
import io.github.ph1lou.werewolfplugin.roles.werewolfs.TenebrousWerewolf;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CommandTenebrous implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] strings) {

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) return;

        if (game.isDay(Day.DAY)) {
            playerWW.sendMessageWithKey(Prefix.RED.getKey(),"werewolf.role.tenebrous_werewolf.not_night");
            return;
        }

        Location location = playerWW.getLocation();

        List<IPlayerWW> affectedPlayers = game.getPlayersWW().stream()
                .filter(player1 -> player1.isState(StatePlayer.ALIVE))
                .filter(player1 -> !player1.getRole().isWereWolf())
                .filter(player1 -> player1.getLocation().distance(location) < 50)
                .collect(Collectors.toList());

        TenebrousEvent event = new TenebrousEvent(affectedPlayers);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED.getKey(),"werewolf.check.cancel");
            return;
        }

        TenebrousWerewolf role = (TenebrousWerewolf) playerWW.getRole();
        role.setPower(false);

        affectedPlayers = event.getAffectedPlayers();

        for (IPlayerWW p : affectedPlayers) {
            role.addAffectedPlayer(p);
            p.addPotionModifier(PotionModifier.add(PotionEffectType.BLINDNESS, 600, 1, "tenebrous"));
            p.sendMessageWithKey(Prefix.RED.getKey() ,"werewolf.role.tenebrous_werewolf.darkness");
        }

        List<IPlayerWW> werewolves =  game.getPlayersWW().stream()
                .filter(player1->player1.isState(StatePlayer.ALIVE))
                .filter(player1 -> player1.getRole().isWereWolf()).collect(Collectors.toList());

        for (IPlayerWW ww :  werewolves) {
            ww.sendMessageWithKey(Prefix.YELLOW.getKey(), "werewolf.role.tenebrous_werewolf.darkness_wolves");
        }

    }
}
