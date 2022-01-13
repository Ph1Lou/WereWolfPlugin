package io.github.ph1lou.werewolfplugin.commands.roles.villager.occultist;

import io.github.ph1lou.werewolfapi.ICommand;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.events.roles.occultist.WishChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandOccultistWish implements ICommand {
    @Override
    public void execute(WereWolfAPI wereWolfAPI, Player player, String[] strings) {
        StringBuilder builder = new StringBuilder();
        for(String string : strings){
            builder.append(string);
        }
        wereWolfAPI.getPlayerWW(player.getUniqueId()).get().setWish(builder.toString());
        player.sendMessage("Votre dernière volonté est désormais: " + builder.toString());
        WishChangeEvent wishChangeEvent = new WishChangeEvent(wereWolfAPI.getPlayerWW(player.getUniqueId()).get());
        wishChangeEvent.setWish(builder.toString());

        Bukkit.getPluginManager().callEvent(wishChangeEvent);
    }
}
