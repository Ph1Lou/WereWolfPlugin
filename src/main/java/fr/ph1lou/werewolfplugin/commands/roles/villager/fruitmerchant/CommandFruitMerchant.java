package fr.ph1lou.werewolfplugin.commands.roles.villager.fruitmerchant;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.ICommandRole;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.events.roles.fruitmerchant.FruitMerchantCommandEvent;
import fr.ph1lou.werewolfapi.events.roles.fruitmerchant.FruitMerchantRecoverInformationEvent;
import fr.ph1lou.werewolfapi.events.roles.fruitmerchant.GoldenCount;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfapi.utils.Utils;
import fr.ph1lou.werewolfplugin.roles.villagers.FruitMerchant;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RoleCommand(key = "werewolf.role.fruit_merchant.command",
        roleKeys = RoleBase.FRUIT_MERCHANT,
        requiredPower = true,
        argNumbers = 0)
public class CommandFruitMerchant implements ICommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, String[] args) {

        FruitMerchant fruitMerchant = (FruitMerchant) playerWW.getRole();

        Set<IPlayerWW> players = Bukkit.getOnlinePlayers()
                .stream()
                .map(Entity::getUniqueId)
                .filter(uuid1 -> !uuid1.equals(playerWW.getUUID()))
                .map(game::getPlayerWW)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(playerWW1 -> playerWW1.isState(StatePlayer.ALIVE))
                .filter(iPlayerWW -> {
                    Location location = iPlayerWW.getLocation();
                    return location.getWorld() == playerWW.getLocation().getWorld() &&
                            location.distance(playerWW.getLocation()) <
                                    game.getConfig().getValue(FruitMerchant.DISTANCE);
                })
                .collect(Collectors.toSet());

        fruitMerchant.setPower(false);

        FruitMerchantCommandEvent fruitMerchantCommandEvent = new FruitMerchantCommandEvent(playerWW, players);

        Bukkit.getPluginManager().callEvent(fruitMerchantCommandEvent);

        if (fruitMerchantCommandEvent.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.cancel");
            return;
        }

        fruitMerchant.clearAffectedPlayer();

        fruitMerchantCommandEvent.getPlayerWWS().forEach(fruitMerchant::addAffectedPlayer);

        playerWW.sendMessageWithKey(Prefix.GREEN , "werewolf.role.fruit_merchant.perform",
                Formatter.format("&players&",
                        fruitMerchantCommandEvent.getPlayerWWS()
                                .stream()
                                .map(IPlayerWW::getName)
                                .collect(Collectors.joining(", "))),
                Formatter.timer(Utils.conversion(game.getConfig()
                        .getTimerValue(TimerBase.FRUIT_MERCHANT_COOL_DOWN)/2)));

        BukkitUtils.scheduleSyncDelayedTask(() -> {
            if(game.isState(StateGame.GAME)){
                if(playerWW.isState(StatePlayer.ALIVE)){
                    Map<IPlayerWW, GoldenCount> goldenCountMap = new HashMap<>();
                    fruitMerchant.getAffectedPlayers()
                            .forEach(playerWW1 -> goldenCountMap.put(playerWW1,new GoldenCount(fruitMerchant.getGoldenAppleNumber(playerWW1),
                                    Utils.countGoldenApple(playerWW1))));

                    fruitMerchant.clearAffectedPlayer();
                    FruitMerchantRecoverInformationEvent fruitMerchantRecoverInformationEvent = new FruitMerchantRecoverInformationEvent(playerWW,goldenCountMap);

                    Bukkit.getPluginManager().callEvent(fruitMerchantRecoverInformationEvent);

                    if(fruitMerchantRecoverInformationEvent.isCancelled()){
                        playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.cancel");
                        return;
                    }

                    playerWW.sendMessageWithKey(Prefix.LIGHT_BLUE,"werewolf.role.fruit_merchant.announce_info");
                    fruitMerchantRecoverInformationEvent.getPlayerWWS().forEach(playerWW1 -> playerWW.sendMessageWithKey(Prefix.YELLOW,"werewolf.role.fruit_merchant.info",
                            Formatter.player(playerWW1.getName()),
                            Formatter.number(fruitMerchantRecoverInformationEvent.getGoldenAppleCount(playerWW1).getOldCount()),
                            Formatter.format("&number2&", fruitMerchantRecoverInformationEvent.getGoldenAppleCount(playerWW1).getNewCount())));

                    BukkitUtils.scheduleSyncDelayedTask(() -> {
                        fruitMerchant.setPower(true);
                        playerWW.sendMessageWithKey(Prefix.GREEN,"werewolf.role.fruit_merchant.recover");
                    },game.getConfig().getTimerValue(TimerBase.FRUIT_MERCHANT_COOL_DOWN) / 2 * 20L);
                }
            }
        }, game.getConfig().getTimerValue(TimerBase.FRUIT_MERCHANT_COOL_DOWN) / 2 * 20L);
    }
}
