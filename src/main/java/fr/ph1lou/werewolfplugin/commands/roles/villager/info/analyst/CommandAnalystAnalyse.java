package fr.ph1lou.werewolfplugin.commands.roles.villager.info.analyst;

import fr.ph1lou.werewolfapi.player.impl.AuraModifier;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Camp;
import fr.ph1lou.werewolfapi.enums.Prefix;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.analyst.AnalystExtraDetailsEvent;
import fr.ph1lou.werewolfplugin.roles.villagers.Analyst;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;
import java.util.stream.Collectors;

public class CommandAnalystAnalyse implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) return;

        if(!(playerWW.getRole() instanceof Analyst)){
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

        if(!analyst.getAffectedPlayers().contains(playerWW1)){
            playerWW.sendMessageWithKey(Prefix.ORANGE.getKey(),"werewolf.role.analyst.not_affected");
            return;
        }

        if(!analyst.isPower2()){
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.power");
            return;
        }

        analyst.setPower2(false);


        AnalystExtraDetailsEvent analystEvent = new AnalystExtraDetailsEvent(playerWW,playerWW1, playerWW1.getPotionModifiers()
                .stream()
                .filter(PotionModifier::isAdd)
                .map(PotionModifier::getPotionEffectType)
                .collect(Collectors.toSet()));

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

        analyst.addAuraModifier(new AuraModifier("analyst", Aura.DARK,1,false));
    }
}
