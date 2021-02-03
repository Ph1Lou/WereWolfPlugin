package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.ProtectionEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class CommandProtector implements Commands {


    private final Main main;

    public CommandProtector(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();
        UUID uuid = player.getUniqueId();
        PlayerWW playerWW = game.getPlayerWW(uuid);

        if (playerWW == null) return;

        Roles protector = playerWW.getRole();

        Player playerArg = Bukkit.getPlayer(args[0]);

        if (playerArg == null) {
            playerWW.sendMessageWithKey("werewolf.check.offline_player");
            return;
        }
        UUID argUUID = playerArg.getUniqueId();
        PlayerWW playerWW1 = game.getPlayerWW(argUUID);

        if (playerWW1 == null || !playerWW1.isState(StatePlayer.ALIVE)) {
            playerWW.sendMessageWithKey("werewolf.check.player_not_found");
            return;
        }

        if (((AffectedPlayers) protector).getAffectedPlayers().contains(playerWW1)) {
            playerWW.sendMessageWithKey("werewolf.check.already_get_power");
            return;
        }


        ((Power) protector).setPower(false);

        ProtectionEvent protectionEvent = new ProtectionEvent(playerWW, playerWW1);

        Bukkit.getPluginManager().callEvent(protectionEvent);

        if (protectionEvent.isCancelled()) {
            playerWW.sendMessageWithKey("werewolf.check.cancel");
            return;
        }

        ((AffectedPlayers) protector).clearAffectedPlayer();
        ((AffectedPlayers) protector).addAffectedPlayer(playerWW1);

        playerWW1.addPotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
        playerWW1.sendMessageWithKey("werewolf.role.protector.get_protection");
        playerWW.sendMessageWithKey("werewolf.role.protector.protection_perform", playerArg.getName());
    }
}
