package fr.ph1lou.werewolfplugin.commands.roles.neutral.willothewisp;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.commands.ICommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.will_o_the_wisp.WillOTheWispIncendiaryMadnessEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfapi.utils.Utils;
import fr.ph1lou.werewolfplugin.roles.neutrals.WillOTheWisp;
import org.bukkit.Bukkit;
import org.bukkit.potion.PotionEffectType;

@RoleCommand(key = "werewolf.roles.will_o_the_wisp.command_incendiary",
        roleKeys = RoleBase.WILL_O_THE_WISP,
        argNumbers = 0,
        requiredPower = true)
public class CommandWillOTheWisp implements ICommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, String[] args) {

        WillOTheWisp willOTheWisp = (WillOTheWisp) playerWW.getRole();

        willOTheWisp.setPower(false);

        BukkitUtils.scheduleSyncDelayedTask(game, () -> {
            willOTheWisp.setPower(true);
            if(playerWW.isState(StatePlayer.ALIVE)){
                playerWW.sendMessageWithKey(Prefix.GREEN, "werewolf.roles.will_o_the_wisp.colldown_end");
            }
        }, game.getConfig().getTimerValue(TimerBase.WILL_O_THE_WISP_COOLDOWN_INCENDIARY_MADNESS)* 20L);

        WillOTheWispIncendiaryMadnessEvent willOTheWispIncendiaryMadnessEvent = new WillOTheWispIncendiaryMadnessEvent(playerWW);

        Bukkit.getPluginManager().callEvent(willOTheWispIncendiaryMadnessEvent);

        if(willOTheWispIncendiaryMadnessEvent.isCancelled()){
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }

        playerWW.sendMessageWithKey(Prefix.GREEN, "werewolf.roles.will_o_the_wisp.perform_madness",
                Formatter.timer(Utils.conversion(game.getConfig().getTimerValue(TimerBase.WILL_O_THE_WISP_DURATION_INCENDIARY_MADNESS))));
        playerWW.addPotionModifier(PotionModifier.add(PotionEffectType.SPEED,
                game.getConfig().getTimerValue(TimerBase.WILL_O_THE_WISP_DURATION_INCENDIARY_MADNESS) * 20,
                0, WillOTheWisp.INCENDIARY_MADNESS));
    }
}
