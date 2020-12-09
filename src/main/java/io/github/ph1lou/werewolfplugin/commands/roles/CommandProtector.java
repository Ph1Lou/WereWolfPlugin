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
import org.bukkit.potion.PotionEffect;
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
            player.sendMessage(game.translate("werewolf.check.offline_player"));
            return;
        }
        UUID argUUID = playerArg.getUniqueId();
        PlayerWW playerWW1 = game.getPlayerWW(argUUID);

        if (playerWW1 == null || !playerWW1.isState(StatePlayer.ALIVE)) {
            player.sendMessage(game.translate("werewolf.check.player_not_found"));
            return;
        }

        if (((AffectedPlayers) protector).getAffectedPlayers().contains(playerWW1)) {
            player.sendMessage(game.translate("werewolf.check.already_get_power"));
            return;
        }


        ((Power) protector).setPower(false);

        ProtectionEvent protectionEvent = new ProtectionEvent(playerWW, playerWW1);

        Bukkit.getPluginManager().callEvent(protectionEvent);

        if (protectionEvent.isCancelled()) {
            player.sendMessage(game.translate("werewolf.check.cancel"));
            return;
        }

        ((AffectedPlayers) protector).clearAffectedPlayer();
        ((AffectedPlayers) protector).addAffectedPlayer(playerWW1);

        Player playerProtected = Bukkit.getPlayer(args[0]);
        if (playerProtected == null) return;
        playerProtected.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
        playerProtected.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,Integer.MAX_VALUE,0,false,false));
        playerProtected.sendMessage(game.translate("werewolf.role.protector.get_protection"));
        player.sendMessage(game.translate("werewolf.role.protector.protection_perform", playerArg.getName()));
    }
}
