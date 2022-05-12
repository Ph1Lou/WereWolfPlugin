package fr.ph1lou.werewolfplugin.commands.roles.neutral.willothewisp;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.events.roles.will_o_the_wisp.WillOTheWispTeleportEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.role.interfaces.IInvisible;
import fr.ph1lou.werewolfapi.role.interfaces.ILimitedUse;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfplugin.roles.neutrals.WillOTheWisp;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.UUID;

@RoleCommand(key = "werewolf.role.will_o_the_wisp.command",
        roleKeys = RoleBase.WILL_O_THE_WISP,
        argNumbers = 0)
public class CommandWillOTheWisp implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) return;

        IRole willOTheWisp = playerWW.getRole();


        if (!(willOTheWisp instanceof ILimitedUse) || ((ILimitedUse) willOTheWisp).getUse() >= 2) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.power");
            return;
        }

        if(!(willOTheWisp instanceof IInvisible) || !((IInvisible)willOTheWisp).isInvisible()){
            playerWW.sendMessageWithKey(Prefix.RED,"werewolf.role.will_o_the_wisp.should_be_invisible");
            return;
        }

        ((ILimitedUse) willOTheWisp).setUse(((ILimitedUse) willOTheWisp).getUse() + 1);

        WillOTheWispTeleportEvent willOTheWispTeleportEvent = new WillOTheWispTeleportEvent(playerWW, ((ILimitedUse) willOTheWisp).getUse());
        Bukkit.getPluginManager().callEvent(willOTheWispTeleportEvent);

        if (willOTheWispTeleportEvent.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.cancel");
            return;
        }

        Vector vector = player.getEyeLocation().getDirection();
        vector
                .normalize()
                .multiply(game.getConfig().getValue(RoleBase.WILL_O_THE_WISP, WillOTheWisp.DISTANCE))
                .setY(player.getWorld().getHighestBlockYAt(player.getLocation()) - player.getLocation().getBlockY() + 10);

        playerWW.teleport(playerWW.getLocation().add(vector));
        playerWW.addPotionModifier(PotionModifier.add(PotionEffectType.WITHER,
                400,
                0,
                "no_fall"));

    }
}
