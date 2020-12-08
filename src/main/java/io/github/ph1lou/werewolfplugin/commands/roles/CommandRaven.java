package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.StatePlayer;
import io.github.ph1lou.werewolfapi.events.CurseEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class CommandRaven implements Commands {


    private final Main main;

    public CommandRaven(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();
        UUID uuid = player.getUniqueId();
        PlayerWW playerWW = game.getPlayerWW(uuid);

        if (playerWW == null) return;

        Roles raven = playerWW.getRole();

        if (args.length != 1) {
            player.sendMessage(game.translate("werewolf.check.player_input"));
            return;
        }

        if (!((Power) raven).hasPower()) {
            player.sendMessage(game.translate("werewolf.check.power"));
            return;
        }

        Player playerArg = Bukkit.getPlayer(args[0]);

        if (playerArg == null) {
            player.sendMessage(game.translate("werewolf.check.offline_player"));
            return;
        }
        UUID argUUID = playerArg.getUniqueId();
        PlayerWW playerWW1 = game.getPlayerWW(argUUID);

        if (playerWW1 == null || !playerWW1.isState(StatePlayer.ALIVE)) {
            player.sendMessage(game.translate("werewolf.check.player_not_found"));
            return;
        }

        if (((AffectedPlayers) raven).getAffectedPlayers().contains(playerWW1)) {
            player.sendMessage(game.translate("werewolf.check.already_get_power"));
            return;
        }

        CurseEvent curseEvent = new CurseEvent(playerWW, playerWW1);
        ((Power) raven).setPower(false);
        Bukkit.getPluginManager().callEvent(curseEvent);

        if (curseEvent.isCancelled()) {
            player.sendMessage(game.translate("werewolf.check.cancel"));
            return;
        }

        ((AffectedPlayers) raven).clearAffectedPlayer();
        ((AffectedPlayers) raven).addAffectedPlayer(playerWW1);
        playerArg.removePotionEffect(PotionEffectType.JUMP);
        playerArg.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 1, false, false));
        playerArg.sendMessage(game.translate("werewolf.role.raven.get_curse"));
        player.sendMessage(game.translate("werewolf.role.raven.curse_perform", playerArg.getName()));
    }
}
