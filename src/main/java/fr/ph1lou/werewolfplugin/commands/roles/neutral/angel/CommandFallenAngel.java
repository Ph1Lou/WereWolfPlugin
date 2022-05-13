package fr.ph1lou.werewolfplugin.commands.roles.neutral.angel;


import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.enums.AngelForm;
import fr.ph1lou.werewolfapi.enums.Day;
import fr.ph1lou.werewolfapi.events.roles.angel.AngelChoiceEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.utils.Utils;
import fr.ph1lou.werewolfplugin.roles.neutrals.Angel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

@RoleCommand(key = "werewolf.role.angel.command_2",
        roleKeys = RoleBase.ANGEL,
        autoCompletion = false,
        argNumbers = 0)
public class CommandFallenAngel implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) return;

        Angel role = (Angel) playerWW.getRole();

        if (!role.isChoice(AngelForm.ANGEL)) {
            player.sendMessage(game.translate(Prefix.RED , "werewolf.check.power"));
            return;
        }

        role.setChoice(AngelForm.FALLEN_ANGEL);
        Bukkit.getPluginManager().callEvent(new AngelChoiceEvent(playerWW, AngelForm.FALLEN_ANGEL));
        player.sendMessage(game.translate(Prefix.YELLOW , "werewolf.role.angel.angle_choice_click",
                Formatter.format("&form&",game.translate(RoleBase.FALLEN_ANGEL)),
                Formatter.timer(Utils.conversion(game.getConfig().getTimerValue(TimerBase.ANGEL_DURATION)))));

        if (game.isDay(Day.NIGHT)) {
            playerWW.addPotionModifier(PotionModifier.remove(PotionEffectType.DAMAGE_RESISTANCE,"fallen_angel",0));

        }
    }
}
