package fr.ph1lou.werewolfplugin.commands.roles.villager.bonesetter;

import fr.ph1lou.werewolfapi.annotations.Params;
import fr.ph1lou.werewolfapi.annotations.PlayerArg;
import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.bonesetter.BonesetterChooseEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfplugin.roles.villagers.Bonesetter;
import org.bukkit.Bukkit;

@RoleCommand(key = "werewolf.roles.bonesetter.command",
        roleKeys = RoleBase.BONESETTER,
        argNumbers = 1,
        args = @Params(players = @PlayerArg(index = 0, states = { StatePlayer.ALIVE }, excludeSelf = true)))
public class CommandBonesetter implements IParameterizedCommandRole {
    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {
        IRole role = playerWW.getRole();
        if (!(role instanceof Bonesetter)) {
            return;
        }
        Bonesetter bonesetter = (Bonesetter) role;

        if (bonesetter.getUse() == 3) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.bonesetter.too_many_players");
            return;
        }

        IPlayerWW targetWW = ctx.player(0);

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
        playerWW.sendMessageWithKey(Prefix.GREEN, "werewolf.roles.bonesetter.perform", Formatter.player(targetWW.getName()));
    }
}
