package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.ConfigsBase;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.WitchResurrectionEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandWitch implements Commands {


    private final Main main;

    public CommandWitch(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();
        UUID uuid = player.getUniqueId();
        PlayerWW playerWW = game.getPlayerWW(uuid);

        if (playerWW == null) return;

        Roles witch = playerWW.getRole();


        if (Bukkit.getPlayer(UUID.fromString(args[0])) == null) {
            playerWW.sendMessageWithKey("werewolf.check.offline_player");
            return;
        }
        UUID argUUID = UUID.fromString(args[0]);
        PlayerWW playerWW1 = game.getPlayerWW(argUUID);

        if (playerWW1 == null) {
            playerWW.sendMessageWithKey("werewolf.check.player_not_found");
            return;
        }

        if (!game.getConfig().isConfigActive(ConfigsBase.AUTO_REZ_WITCH.getKey()) && argUUID.equals(uuid)) {
            playerWW.sendMessageWithKey("werewolf.check.not_yourself");
            return;
        }

        if (!playerWW1.isState(StatePlayer.JUDGEMENT)) {
            playerWW.sendMessageWithKey("werewolf.check.not_in_judgement");
            return;
        }

        if (game.getScore().getTimer() - playerWW1.getDeathTime() < 7) {
            return;
        }

        ((Power) witch).setPower(false);
        WitchResurrectionEvent witchResurrectionEvent = new WitchResurrectionEvent(playerWW, playerWW1);
        Bukkit.getPluginManager().callEvent(witchResurrectionEvent);

        if (witchResurrectionEvent.isCancelled()) {
            playerWW.sendMessageWithKey("werewolf.check.cancel");
            return;
        }

        ((AffectedPlayers) witch).addAffectedPlayer(playerWW1);
        game.resurrection(playerWW1);
        playerWW.sendMessageWithKey("werewolf.role.witch.resuscitation_perform",
                playerWW1.getName());
    }
}
