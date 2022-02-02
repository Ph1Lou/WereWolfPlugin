package io.github.ph1lou.werewolfplugin.commands.roles.villager.occultist;

import io.github.ph1lou.werewolfapi.Formatter;
import io.github.ph1lou.werewolfapi.ICommand;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Prefix;
import io.github.ph1lou.werewolfapi.events.roles.occultist.WishChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandOccultistWish implements ICommand {
    @Override
    public void execute(WereWolfAPI wereWolfAPI, Player player, String[] strings) {
        if(!wereWolfAPI.getPlayerWW(player.getUniqueId()).isPresent()) {
            return;
        }
        IPlayerWW playerWW = wereWolfAPI.getPlayerWW(player.getUniqueId()).get();
        StringBuilder builder = new StringBuilder();
        for(String string : strings) {
            builder.append(string);
        }
        WishChangeEvent wishChangeEvent = new WishChangeEvent(playerWW,
                builder.toString());
        Bukkit.getPluginManager().callEvent(wishChangeEvent);

        if(wishChangeEvent.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.ORANGE.getKey(), "werewolf.check.cancel");
        } else {
            playerWW.sendMessageWithKey(Prefix.LIGHT_BLUE.getKey(), "werewolf.role.occultist.changewish",
                    Formatter.format("&wish&", builder.toString()));
            playerWW.setWish(builder.toString());
        }

    }
}
