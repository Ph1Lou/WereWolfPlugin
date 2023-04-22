package fr.ph1lou.werewolfplugin.commands.roles.villager.bonesetter;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.ICommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.bonesetter.BonesetterChooseEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfplugin.roles.villagers.Bonesetter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@RoleCommand(key = "werewolf.roles.bonesetter.command",
        roleKeys = RoleBase.BONESETTER,
        argNumbers = 1)
public class CommandBonesetter implements ICommandRole {
    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, String[] args) {
        IRole role = playerWW.getRole();
        if (!(role instanceof Bonesetter)) {
            return;
        }
        Bonesetter bonesetter = (Bonesetter) role;

        if (bonesetter.getUse() == 3) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.bonesetter.too_many_players");
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.offline_player");
            return;
        }

        IPlayerWW targetWW = game.getPlayerWW(target.getUniqueId()).orElse(null);

        if (targetWW == null || !targetWW.isState(StatePlayer.ALIVE)) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.player_not_found");
            return;
        }

        if (playerWW.getUUID().equals(targetWW.getUUID())) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.not_yourself");
            return;
        }

        if (bonesetter.getAlreadyUsed().contains(targetWW)) {
            playerWW.sendMessageWithKey(Prefix.RED,
                    "werewolf.roles.benefactor.already_use_on_player");
            return;
        }

        BonesetterChooseEvent bonesetterChooseEvent = new BonesetterChooseEvent(playerWW, targetWW);
        Bukkit.getPluginManager().callEvent(bonesetterChooseEvent);

        if (bonesetterChooseEvent.isCancelled()) return;

        bonesetter.setUse(bonesetter.getUse() + 1);
        bonesetter.addAffectedPlayer(targetWW);
        targetWW.sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.bonesetter.target_message");
        playerWW.sendMessageWithKey(Prefix.GREEN, "werewolf.roles.bonesetter.perform", Formatter.player(targetWW.getName()));
    }
}
