package io.github.ph1lou.pluginlg.commandlg.roles;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.classesroles.neutralroles.Angel;
import io.github.ph1lou.pluginlg.classesroles.neutralroles.GuardianAngel;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.game.PlayerLG;
import io.github.ph1lou.pluginlgapi.enumlg.RoleLG;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import io.github.ph1lou.pluginlgapi.enumlg.StateLG;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class CommandAngelRegen extends Commands {


    public CommandAngelRegen(MainLG main) {
        super(main);
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

        if(!game.playerLG.containsKey(uuid)) {
            player.sendMessage(game.translate("werewolf.check.not_in_game"));
            return;
        }

        PlayerLG plg = game.playerLG.get(uuid);


        if (!game.isState(StateLG.GAME)) {
            player.sendMessage(game.translate("werewolf.check.game_not_in_progress"));
            return;
        }

        if (!(plg.getRole() instanceof Angel) || !((Angel) plg.getRole()).getChoice().equals(RoleLG.GUARDIAN_ANGEL)) {
            player.sendMessage(game.translate("werewolf.check.role",game.translate("werewolf.role.guardian_angel.display")));
            return;
        }

        GuardianAngel guardianAngel = (GuardianAngel) plg.getRole();

        if (!plg.isState(State.ALIVE)) {
            player.sendMessage(game.translate("werewolf.check.death"));
            return;
        }

        if (guardianAngel.getUse() >= 3) {
            player.sendMessage(game.translate("werewolf.check.power"));
            return;
        }

        if (guardianAngel.getAffectedPlayers().isEmpty()) {
            player.sendMessage(game.translate("werewolf.role.guardian_angel.no_protege"));
            return;
        }

        if (Bukkit.getPlayer(guardianAngel.getAffectedPlayers().get(0)) == null) {
            player.sendMessage(game.translate("werewolf.role.guardian_angel.disconnected_protege"));
            return;
        }

        guardianAngel.setUse(guardianAngel.getUse()+1);

        Player playerProtected = Bukkit.getPlayer(guardianAngel.getAffectedPlayers().get(0));
        playerProtected.removePotionEffect(PotionEffectType.REGENERATION);
        playerProtected.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 400, 0, false, false));
        playerProtected.sendMessage(game.translate("werewolf.role.guardian_angel.get_regeneration"));
    }
}
