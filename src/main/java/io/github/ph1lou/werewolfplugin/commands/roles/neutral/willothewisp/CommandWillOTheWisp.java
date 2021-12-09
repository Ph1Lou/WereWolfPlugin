package io.github.ph1lou.werewolfplugin.commands.roles.neutral.willothewisp;

import io.github.ph1lou.werewolfapi.ICommand;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.PotionModifier;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Prefix;
import io.github.ph1lou.werewolfapi.events.roles.will_o_the_wisp.WillOTheWispTeleportEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IInvisible;
import io.github.ph1lou.werewolfapi.rolesattributs.ILimitedUse;
import io.github.ph1lou.werewolfapi.rolesattributs.IRole;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.UUID;

public class CommandWillOTheWisp implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) return;

        IRole willOTheWisp = playerWW.getRole();


        if (!(willOTheWisp instanceof ILimitedUse) || ((ILimitedUse) willOTheWisp).getUse() >= 2) {
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.power");
            return;
        }

        if(!(willOTheWisp instanceof IInvisible) || !((IInvisible)willOTheWisp).isInvisible()){
            playerWW.sendMessageWithKey(Prefix.RED.getKey(),"werewolf.role.will_o_the_wisp.should_be_invisible");
            return;
        }

        ((ILimitedUse) willOTheWisp).setUse(((ILimitedUse) willOTheWisp).getUse() + 1);

        WillOTheWispTeleportEvent willOTheWispTeleportEvent = new WillOTheWispTeleportEvent(playerWW, ((ILimitedUse) willOTheWisp).getUse());
        Bukkit.getPluginManager().callEvent(willOTheWispTeleportEvent);

        if (willOTheWispTeleportEvent.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.cancel");
            return;
        }

        Vector vector = player.getEyeLocation().getDirection();
        vector
                .normalize()
                .multiply(game.getConfig().getDistanceWillOTheWisp())
                .setY(player.getWorld().getHighestBlockYAt(player.getLocation()) - player.getLocation().getBlockY() + 10);

        playerWW.teleport(playerWW.getLocation().add(vector));
        playerWW.addPotionModifier(PotionModifier.add(PotionEffectType.WITHER,
                400,
                0,
                "no_fall"));

    }
}
