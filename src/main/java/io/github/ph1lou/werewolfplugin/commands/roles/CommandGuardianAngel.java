package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.ICommands;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.AngelForm;
import io.github.ph1lou.werewolfapi.enums.RolesBase;
import io.github.ph1lou.werewolfapi.enums.TimersBase;
import io.github.ph1lou.werewolfapi.events.roles.angel.AngelChoiceEvent;
import io.github.ph1lou.werewolfapi.utils.Utils;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.roles.neutrals.Angel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandGuardianAngel implements ICommands {


    private final Main main;

    public CommandGuardianAngel(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();
        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid);

        if (playerWW == null) return;

        Angel angel = (Angel) playerWW.getRole();

        if (!angel.isChoice(AngelForm.ANGEL)) {
            playerWW.sendMessageWithKey("werewolf.check.power");
            return;
        }

        Bukkit.getPluginManager().callEvent(new AngelChoiceEvent(playerWW, AngelForm.GUARDIAN_ANGEL));
        angel.setChoice(AngelForm.GUARDIAN_ANGEL);
        playerWW.sendMessageWithKey("werewolf.role.angel.angle_choice_click",
                game.translate(RolesBase.GUARDIAN_ANGEL.getKey())
                , Utils.conversion(game.getConfig().getTimerValue(TimersBase.ANGEL_DURATION.getKey())));
    }
}
