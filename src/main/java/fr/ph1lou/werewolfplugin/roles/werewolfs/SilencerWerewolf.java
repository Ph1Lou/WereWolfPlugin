package fr.ph1lou.werewolfplugin.roles.werewolfs;

import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Camp;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.game.day_cycle.NightEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleWereWolf;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IGuesser;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Role(key = RoleBase.SILENCER_WEREWOLF,
        defaultAura = Aura.DARK,
        category = Category.WEREWOLF,
        attributes = RoleAttribute.WEREWOLF)
public class SilencerWerewolf extends RoleWereWolf implements IPower, IGuesser, IAffectedPlayers {

    private final List<IPlayerWW> affectedPlayers = new ArrayList<>();
    private boolean power = false;

    public SilencerWerewolf(WereWolfAPI game, IPlayerWW playerWW) {
        super(game, playerWW);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.silencer_werewolf.description"))
                .setCommand(game.translate("werewolf.roles.silencer_werewolf.desc_command"))
                .setPower(game.translate("werewolf.description.werewolf"))
                .build();
    }

    @Override
    public void recoverPower() {

    }

    @Override
    public void addAffectedPlayer(IPlayerWW iPlayerWW) {
        affectedPlayers.add(iPlayerWW);
    }

    @Override
    public void removeAffectedPlayer(IPlayerWW iPlayerWW) {
        affectedPlayers.remove(iPlayerWW);
    }

    @Override
    public void clearAffectedPlayer() {
        affectedPlayers.clear();
    }

    @Override
    public List<? extends IPlayerWW> getAffectedPlayers() {
        return affectedPlayers;
    }

    @Override
    public void setPower(boolean b) {
        power = b;
    }

    @Override
    public boolean hasPower() {
        return power;
    }

    @Override
    public boolean canGuess(IPlayerWW targetWW) {
        if (!getPlayerWW().isState(StatePlayer.ALIVE)) {
            getPlayerWW().sendMessageWithKey("werewolf.check.state_player");
            return false;
        }

        if (targetWW.equals(this.getPlayerWW())) {
            getPlayerWW().sendMessageWithKey("werewolf.check.not_yourself");
            return false;
        }

        if (!power || !isAbilityEnabled() ) {
            getPlayerWW().sendMessageWithKey("werewolf.check.power");
            return false;
        }

        long nVillagers = game.getPlayersWW().stream()
                .filter(playerWW->playerWW.isState(StatePlayer.ALIVE))
                .filter(playerWW->playerWW.getRole().getCamp() == Camp.VILLAGER)
                .count();

        long nWW = game.getPlayersWW().stream()
                .filter(playerWW->playerWW.isState(StatePlayer.ALIVE))
                .filter(playerWW->playerWW.getRole().getCamp() == Camp.WEREWOLF)
                .count();
        if (nWW >= nVillagers) {
            getPlayerWW().sendMessageWithKey(Prefix.BLUE, "werewolf.roles.silencer_werewolf.more_wolves");
            return false;
        }
        return true;
    }

    @Override
    public void resolveGuess(String key, IPlayerWW targetWW) {
        power = false;
        if (targetWW.getRole().getKey().equals(key)) {
            affectedPlayers.add(targetWW);
            targetWW.getRole().disableAbilities();
            getPlayerWW().sendMessageWithKey(Prefix.GREEN, "werewolf.roles.silencer_werewolf.guess_success", Formatter.format("&player&", targetWW.getName()));
            targetWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.silencer_werewolf.disable_target");
        } else {
            getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.roles.silencer_werewolf.guess_fail");
            this.disableAbilities();
        }
    }

    @Override
    public Set<Category> getAvailableCategories() {
        Set<Category> category = new HashSet<>();
        category.add(Category.VILLAGER);
        return category;
    }

    @EventHandler
    public void onNight(NightEvent event) {
        boolean failedGuess = false;
        if (!failedGuess && isAbilityEnabled()) {
            power = true;
            getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.silencer_werewolf.power");
        }
    }
}
