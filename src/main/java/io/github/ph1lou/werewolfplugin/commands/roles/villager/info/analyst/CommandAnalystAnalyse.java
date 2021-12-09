package io.github.ph1lou.werewolfplugin.commands.roles.villager.info.analyst;

import io.github.ph1lou.werewolfapi.Formatter;
import io.github.ph1lou.werewolfapi.ICommand;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.PotionModifier;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Camp;
import io.github.ph1lou.werewolfapi.enums.Prefix;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.roles.analyst.AnalystExtraDetailsEvent;
import io.github.ph1lou.werewolfplugin.roles.villagers.Analyst;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CommandAnalystAnalyse implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) return;

        if(playerWW.getRole() instanceof Analyst){
            return;
        }

        Analyst analyst = (Analyst) playerWW.getRole();

        Player playerArg = Bukkit.getPlayer(args[0]);

        if (playerArg == null) {
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.offline_player");
            return;
        }
        UUID argUUID = playerArg.getUniqueId();
        IPlayerWW playerWW1 = game.getPlayerWW(argUUID).orElse(null);

        if (playerWW1 == null || !playerWW1.isState(StatePlayer.ALIVE)) {
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.player_not_found");
            return;
        }

        List<PotionEffectType> effects = Arrays.asList(PotionEffectType.INCREASE_DAMAGE,
                PotionEffectType.DAMAGE_RESISTANCE, PotionEffectType.WEAKNESS, PotionEffectType.SPEED, PotionEffectType.INVISIBILITY, PotionEffectType.ABSORPTION);

        if(!analyst.isPower2()){
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.power");
            return;
        }


        AnalystExtraDetailsEvent analystEvent = new AnalystExtraDetailsEvent(playerWW,playerWW1, playerWW1.getPotionModifiers()
                .stream()
                .filter(PotionModifier::isAdd)
                .map(PotionModifier::getPotionEffectType)
                .collect(Collectors.toList()));

        Bukkit.getPluginManager().callEvent(analystEvent);

        if(analystEvent.isCancelled()){
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.cancel");
            return;
        }

        playerWW.sendMessageWithKey(Prefix.GREEN.getKey(),"werewolf.role.analyst.effects",
                Formatter.player(playerWW1.getName()),
                Formatter.format("&list&",analystEvent.getPotions()
                        .stream()
                        .map(PotionEffectType::getName)
                        .collect(Collectors.joining(", "))));

        if(playerWW1.getRole().isCamp(Camp.VILLAGER)){
            playerWW1.sendMessageWithKey(Prefix.RED.getKey(),"werewolf.role.analyst.call_back");
        }
        else{
            playerWW1.sendMessageWithKey(Prefix.RED.getKey(),"werewolf.role.analyst.call_back_no_villager",
                    Formatter.player(playerWW.getName()));
        }
    }
}
