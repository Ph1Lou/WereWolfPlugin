package io.github.ph1lou.werewolfplugin.commands.roles.villager.fruitmerchant;

import io.github.ph1lou.werewolfapi.Formatter;
import io.github.ph1lou.werewolfapi.ICommand;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Prefix;
import io.github.ph1lou.werewolfapi.enums.StateGame;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.TimerBase;
import io.github.ph1lou.werewolfapi.events.roles.fruitmerchant.FruitMerchantCommandEvent;
import io.github.ph1lou.werewolfapi.utils.BukkitUtils;
import io.github.ph1lou.werewolfapi.utils.Utils;
import io.github.ph1lou.werewolfplugin.roles.villagers.FruitMerchant;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class CommandFruitMerchant implements ICommand {


    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) return;

        FruitMerchant fruitMerchant = (FruitMerchant) playerWW.getRole();

        Set<IPlayerWW> players = Bukkit.getOnlinePlayers()
                .stream()
                .map(Entity::getUniqueId)
                .map(game::getPlayerWW)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(playerWW1 -> playerWW1.isState(StatePlayer.ALIVE))
                .filter(iPlayerWW -> {
                    Location location = iPlayerWW.getLocation();
                    return location.getWorld() == player.getWorld() &&
                            location.distance(player.getLocation()) < game.getConfig().getDistanceFruitMerchant();
                })
                .collect(Collectors.toSet());

        fruitMerchant.setPower(false);

        FruitMerchantCommandEvent fruitMerchantCommandEvent = new FruitMerchantCommandEvent(playerWW, players);

        Bukkit.getPluginManager().callEvent(fruitMerchantCommandEvent);

        if (fruitMerchantCommandEvent.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.cancel");
            return;
        }

        fruitMerchant.clearAffectedPlayer();

        fruitMerchantCommandEvent.getPlayerWWS().forEach(fruitMerchant::addAffectedPlayer);

        playerWW.sendMessageWithKey(Prefix.GREEN.getKey() , "werewolf.role.fruit_merchant.perform",
                Formatter.format("&players&",
                        fruitMerchantCommandEvent.getPlayerWWS()
                                .stream()
                                .map(IPlayerWW::getName)
                                .collect(Collectors.joining(", "))),
                Formatter.timer(Utils.conversion(game.getConfig()
                        .getTimerValue(TimerBase.FRUIT_MERCHANT_COOL_DOWN.getKey())/2)));

        BukkitUtils.scheduleSyncDelayedTask(() -> {
            if(game.isState(StateGame.GAME)){
                if(playerWW.isState(StatePlayer.ALIVE)){
                    playerWW.sendMessageWithKey(Prefix.LIGHT_BLUE.getKey(),"werewolf.role.fruit_merchant.announce_info");
                    fruitMerchant.getAffectedPlayers().forEach(playerWW1 -> playerWW.sendMessageWithKey(Prefix.YELLOW.getKey(),"werewolf.role.fruit_merchant.info",
                            Formatter.player(playerWW1.getName()),
                            Formatter.number(fruitMerchant.getGoldenAppleNumber(playerWW1)),
                            Formatter.format("&number2&", Utils.countGoldenApple(playerWW1))));
                    BukkitUtils.scheduleSyncDelayedTask(() -> {
                        fruitMerchant.setPower(true);
                        playerWW.sendMessageWithKey(Prefix.GREEN.getKey(),"werewolf.role.fruit_merchant.recover");
                    },game.getConfig().getTimerValue(TimerBase.FRUIT_MERCHANT_COOL_DOWN.getKey()) / 2 * 20L);
                }
            }
        }, game.getConfig().getTimerValue(TimerBase.FRUIT_MERCHANT_COOL_DOWN.getKey()) / 2 * 20L);
    }
}
