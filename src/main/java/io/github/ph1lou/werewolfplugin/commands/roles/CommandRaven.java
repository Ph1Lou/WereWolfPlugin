package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.ICommands;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.roles.raven.CurseEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.IPower;
import io.github.ph1lou.werewolfapi.rolesattributs.IRole;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class CommandRaven implements ICommands {


    private final Main main;

    public CommandRaven(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();
        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid);

        if (playerWW == null) return;

        IRole raven = playerWW.getRole();
        Player playerArg = Bukkit.getPlayer(args[0]);

        if (playerArg == null) {
            playerWW.sendMessageWithKey("werewolf.check.offline_player");
            return;
        }
        UUID argUUID = playerArg.getUniqueId();
        IPlayerWW playerWW1 = game.getPlayerWW(argUUID);

        if (playerWW1 == null || !playerWW1.isState(StatePlayer.ALIVE)) {
            playerWW.sendMessageWithKey("werewolf.check.player_not_found");
            return;
        }

        if (((IAffectedPlayers) raven).getAffectedPlayers().contains(playerWW1)) {
            playerWW.sendMessageWithKey("werewolf.check.already_get_power");
            return;
        }

        CurseEvent curseEvent = new CurseEvent(playerWW, playerWW1);
        ((IPower) raven).setPower(false);
        Bukkit.getPluginManager().callEvent(curseEvent);

        if (curseEvent.isCancelled()) {
            playerWW.sendMessageWithKey("werewolf.check.cancel");
            return;
        }

        ((IAffectedPlayers) raven).clearAffectedPlayer();
        ((IAffectedPlayers) raven).addAffectedPlayer(playerWW1);
        playerWW1.addPotionEffect(PotionEffectType.JUMP);
        playerWW1.sendMessageWithKey("werewolf.role.raven.get_curse");
        playerWW.sendMessageWithKey("werewolf.role.raven.curse_perform", playerArg.getName());
    }
}
