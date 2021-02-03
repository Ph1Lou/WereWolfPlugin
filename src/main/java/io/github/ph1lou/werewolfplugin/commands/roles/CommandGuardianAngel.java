package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.AngelForm;
import io.github.ph1lou.werewolfapi.enums.RolesBase;
import io.github.ph1lou.werewolfapi.enums.TimersBase;
import io.github.ph1lou.werewolfapi.events.AngelChoiceEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AngelRole;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandGuardianAngel implements Commands {


    private final Main main;

    public CommandGuardianAngel(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();
        UUID uuid = player.getUniqueId();
        PlayerWW playerWW = game.getPlayerWW(uuid);

        if (playerWW == null) return;

        Roles angel = playerWW.getRole();

        if (!((AngelRole) angel).isChoice(AngelForm.ANGEL)) {
            playerWW.sendMessageWithKey("werewolf.check.power");
            return;
        }

        Bukkit.getPluginManager().callEvent(new AngelChoiceEvent(playerWW, AngelForm.GUARDIAN_ANGEL));
        ((AngelRole) angel).setChoice(AngelForm.GUARDIAN_ANGEL);
        playerWW.sendMessageWithKey("werewolf.role.angel.angle_choice_click",
                game.translate(RolesBase.GUARDIAN_ANGEL.getKey())
                , game.getScore().conversion(game.getConfig().getTimerValue(TimersBase.ANGEL_DURATION.getKey())));
    }
}
