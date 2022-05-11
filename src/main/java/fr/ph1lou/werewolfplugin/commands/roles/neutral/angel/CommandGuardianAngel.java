package fr.ph1lou.werewolfplugin.commands.roles.neutral.angel;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.enums.AngelForm;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.events.roles.angel.AngelChoiceEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.utils.Utils;
import fr.ph1lou.werewolfplugin.roles.neutrals.Angel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@RoleCommand(key = "werewolf.role.angel.command_1",
        roleKeys = RoleBase.ANGEL,
        autoCompletion = false,
        argNumbers = 0)
public class CommandGuardianAngel implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) return;

        Angel angel = (Angel) playerWW.getRole();

        if (!angel.isChoice(AngelForm.ANGEL)) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.power");
            return;
        }

        Bukkit.getPluginManager().callEvent(new AngelChoiceEvent(playerWW, AngelForm.GUARDIAN_ANGEL));
        angel.setChoice(AngelForm.GUARDIAN_ANGEL);
        playerWW.sendMessageWithKey(Prefix.YELLOW , "werewolf.role.angel.angle_choice_click",
                Formatter.format("&form&",game.translate(RoleBase.GUARDIAN_ANGEL)),
                Formatter.timer(Utils.conversion(game.getConfig().getTimerValue(TimerBase.ANGEL_DURATION))));
    }
}
