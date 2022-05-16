package fr.ph1lou.werewolfplugin.commands.utilities;

import fr.ph1lou.werewolfapi.annotations.PlayerCommand;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.occultist.WishChangeEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;

import fr.ph1lou.werewolfapi.player.utils.Formatter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@PlayerCommand(key = "werewolf.wish.command",
        descriptionKey = "werewolf.wish.command_description",
        statesPlayer = StatePlayer.ALIVE,
        statesGame = StateGame.GAME)
public class CommandWish implements ICommand {

    @Override
    public void execute(WereWolfAPI wereWolfAPI, Player player, String[] strings) {

        IPlayerWW playerWW = wereWolfAPI.getPlayerWW(player.getUniqueId()).orElse(null);

        if(playerWW == null) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        for(String string : strings) {
            builder.append(string).append(" ");
        }
        WishChangeEvent wishChangeEvent = new WishChangeEvent(playerWW,
                builder.toString());
        Bukkit.getPluginManager().callEvent(wishChangeEvent);
        if(wishChangeEvent.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.ORANGE, "werewolf.check.cancel");
        } else {
            playerWW.sendMessageWithKey(Prefix.LIGHT_BLUE, "werewolf.wish.change_wish",
                    Formatter.format("&wish&", builder.toString()));
            playerWW.setWish(builder.toString());
        }

    }
}