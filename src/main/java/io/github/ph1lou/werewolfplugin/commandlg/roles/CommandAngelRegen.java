package io.github.ph1lou.werewolfplugin.commandlg.roles;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enumlg.AngelForm;
import io.github.ph1lou.werewolfapi.enumlg.State;
import io.github.ph1lou.werewolfapi.enumlg.StateLG;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.AngelRole;
import io.github.ph1lou.werewolfapi.rolesattributs.LimitedUse;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class CommandAngelRegen implements Commands {

    private final Main main;

    public CommandAngelRegen(Main main) {
        this.main = main;
    }


    @Override
    public void execute(CommandSender sender, String[] args) {

        GameManager game = main.currentGame;

        if (!(sender instanceof Player)) {
            sender.sendMessage(game.translate("werewolf.check.console"));
            return;
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        if(!game.getPlayersWW().containsKey(uuid)) {
            player.sendMessage(game.translate("werewolf.check.not_in_game"));
            return;
        }

        PlayerWW plg = game.getPlayersWW().get(uuid);


        if (!game.isState(StateLG.GAME)) {
            player.sendMessage(game.translate("werewolf.check.game_not_in_progress"));
            return;
        }

        if (!(plg.getRole() instanceof AngelRole) || !((AngelRole) plg.getRole()).getChoice().equals(AngelForm.GUARDIAN_ANGEL)) {
            player.sendMessage(game.translate("werewolf.check.role",game.translate("werewolf.role.guardian_angel.display")));
            return;
        }

        Roles guardianAngel = plg.getRole();

        if (!plg.isState(State.ALIVE)) {
            player.sendMessage(game.translate("werewolf.check.death"));
            return;
        }

        if (((LimitedUse)guardianAngel).getUse() >= 3) {
            player.sendMessage(game.translate("werewolf.check.power"));
            return;
        }

        if (((AffectedPlayers)guardianAngel).getAffectedPlayers().isEmpty()) {
            player.sendMessage(game.translate("werewolf.role.guardian_angel.no_protege"));
            return;
        }

        if (Bukkit.getPlayer(((AffectedPlayers) guardianAngel).getAffectedPlayers().get(0)) == null) {
            player.sendMessage(game.translate("werewolf.role.guardian_angel.disconnected_protege"));
            return;
        }

        ((LimitedUse) guardianAngel).setUse(((LimitedUse) guardianAngel).getUse()+1);

        Player playerProtected = Bukkit.getPlayer(((AffectedPlayers) guardianAngel).getAffectedPlayers().get(0));
        playerProtected.removePotionEffect(PotionEffectType.REGENERATION);
        playerProtected.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 400, 0, false, false));
        playerProtected.sendMessage(game.translate("werewolf.role.guardian_angel.get_regeneration"));
        player.sendMessage(game.translate("werewolf.role.guardian_angel.perform",3-((LimitedUse) guardianAngel).getUse()));
    }
}
