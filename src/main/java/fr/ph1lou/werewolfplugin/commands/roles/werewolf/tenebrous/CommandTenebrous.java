package fr.ph1lou.werewolfplugin.commands.roles.werewolf.tenebrous;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.commands.ICommandRole;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Day;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.tenebrous_werewolf.TenebrousEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.AuraModifier;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfplugin.roles.werewolfs.TenebrousWerewolf;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import fr.ph1lou.werewolfapi.enums.UniversalPotionEffectType;

import java.util.List;
import java.util.stream.Collectors;

@RoleCommand(key = "werewolf.roles.tenebrous_werewolf.command",
        roleKeys = RoleBase.TENEBROUS_WEREWOLF,
        requiredPower = true,
        argNumbers = 0)
public class CommandTenebrous implements ICommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, String[] strings) {

        if (game.isDay(Day.DAY)) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.tenebrous_werewolf.not_night");
            return;
        }

        Location location = playerWW.getLocation();

        int range = game.getConfig().getValue(IntValueBase.TENEBROUS_WEREWOLF_DISTANCE);

        List<IPlayerWW> affectedPlayers = game.getPlayersWW().stream()
                .filter(player1 -> player1.isState(StatePlayer.ALIVE))
                .filter(player1 -> !player1.getRole().isWereWolf())
                .filter(player1 -> player1.getLocation().distance(location) < range)
                .collect(Collectors.toList());

        TenebrousEvent event = new TenebrousEvent(playerWW, affectedPlayers);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }

        TenebrousWerewolf role = (TenebrousWerewolf) playerWW.getRole();
        role.setPower(false);

        affectedPlayers = event.getAffectedPlayers();

        for (IPlayerWW p : affectedPlayers) {
            role.addAffectedPlayer(p);
            p.addPotionModifier(PotionModifier.add(UniversalPotionEffectType.BLINDNESS, game.getConfig()
                    .getTimerValue(TimerBase.WEREWOLF_TENEBROUS_DURATION) * 20, 1, playerWW.getRole().getKey()));
            p.sendMessageWithKey(Prefix.RED, "werewolf.roles.tenebrous_werewolf.darkness");
        }

        List<IPlayerWW> werewolves = game.getPlayersWW().stream()
                .filter(player1 -> player1.isState(StatePlayer.ALIVE))
                .filter(player1 -> player1.getRole().isWereWolf()).collect(Collectors.toList());

        playerWW.getRole().addAuraModifier(new AuraModifier(playerWW.getRole().getKey(), Aura.DARK, 1, false));

        for (IPlayerWW ww : werewolves) {
            ww.sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.tenebrous_werewolf.darkness_wolves");
        }

    }
}