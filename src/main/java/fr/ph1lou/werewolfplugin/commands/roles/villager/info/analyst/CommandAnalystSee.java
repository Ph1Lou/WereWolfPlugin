package fr.ph1lou.werewolfplugin.commands.roles.villager.info.analyst;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.ICommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.analyst.AnalystEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.ILimitedUse;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RoleCommand(key = "werewolf.roles.analyst.command_see",
        roleKeys = RoleBase.ANALYST,
        requiredPower = true,
        argNumbers = 1)
public class CommandAnalystSee implements ICommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, String[] args) {

        IRole analyst = playerWW.getRole();

        Player playerArg = Bukkit.getPlayer(args[0]);

        if (playerArg == null) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.offline_player");
            return;
        }
        UUID argUUID = playerArg.getUniqueId();
        IPlayerWW playerWW1 = game.getPlayerWW(argUUID).orElse(null);

        if (playerWW1 == null || !playerWW1.isState(StatePlayer.ALIVE)) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.player_not_found");
            return;
        }

        List<PotionEffectType> effects = Arrays.asList(PotionEffectType.INCREASE_DAMAGE,
                PotionEffectType.DAMAGE_RESISTANCE, PotionEffectType.WEAKNESS, PotionEffectType.SPEED, PotionEffectType.INVISIBILITY);

        if(analyst instanceof ILimitedUse){
            if(((ILimitedUse)analyst).getUse() >= 5){
                playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.power");
                return;
            }
            ((ILimitedUse)analyst).setUse(((ILimitedUse)analyst).getUse()+1);
        }

        if(analyst instanceof IPower){
            ((IPower)analyst).setPower(false);
        }

        AnalystEvent analystEvent = new AnalystEvent(playerWW,playerWW1, playerWW1.getPotionModifiers()
                .stream()
                .filter(PotionModifier::isAdd)
                .map(PotionModifier::getPotionEffectType).anyMatch(effects::contains));

        Bukkit.getPluginManager().callEvent(analystEvent);

        if(analystEvent.isCancelled()){
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.cancel");
            return;
        }

        if(analyst instanceof IAffectedPlayers){
            ((IAffectedPlayers)analyst).addAffectedPlayer(playerWW1);
        }

        if(analystEvent.hasEffect()){
            playerWW.sendMessageWithKey(Prefix.GREEN,"werewolf.roles.analyst.has_effects",
                    Formatter.player(playerWW1.getName()));
        }
        else{
            playerWW.sendMessageWithKey(Prefix.RED,"werewolf.roles.analyst.no_effects",
                    Formatter.player(playerWW1.getName()));
        }
    }
}
