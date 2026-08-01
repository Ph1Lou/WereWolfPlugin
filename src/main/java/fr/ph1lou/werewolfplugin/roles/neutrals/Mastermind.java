package fr.ph1lou.werewolfplugin.roles.neutrals;

import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleNeutral;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IGuesser;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Role(key = RoleBase.MASTERMIND, defaultAura = Aura.NEUTRAL, category = Category.NEUTRAL, attribute = RoleAttribute.NEUTRAL,
        configValues = {@IntValue(key = IntValueBase.MASTERMIND_CORRECT_BONUS,
                defaultValue = 2, meetUpValue = 2, step = 1, item = UniversalMaterial.GOLDEN_APPLE),
                @IntValue(key = IntValueBase.MASTERMIND_WRONG_PENALTY,
                        defaultValue = 2, meetUpValue = 2, step = 1, item = UniversalMaterial.RED_DYE)})
public class Mastermind extends RoleNeutral implements IGuesser, IAffectedPlayers, IPower {

    private final List<IPlayerWW> guessedPlayers = new ArrayList<>();
    private final List<IPlayerWW> failedPlayers = new ArrayList<>();
    private boolean power = true;

    public Mastermind(WereWolfAPI game, IPlayerWW playerWW) {
        super(game, playerWW);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.mastermind.description"))
                .setCommand(game.translate("werewolf.roles.mastermind.command"))
                .build();
    }

    @Override
    public void recoverPower() {

    }

    @Override
    public boolean canGuess(IPlayerWW targetWW) {

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) {
            getPlayerWW().sendMessageWithKey("werewolf.check.death");
            return false;
        }

        if (targetWW.equals(this.getPlayerWW())) {
            getPlayerWW().sendMessageWithKey("werewolf.check.not_yourself");
            return false;
        }

        if (guessedPlayers.contains(targetWW) || failedPlayers.contains(targetWW)) {
            getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.mastermind.repeat_target");
            return false;
        }
        return true;
    }

    @Override
    public void resolveGuess(String key, IPlayerWW targetWW) {


        if (targetWW.getRole().getKey().equals(key)) {
            guessedPlayers.add(targetWW);
            getPlayerWW().sendMessageWithKey(Prefix.GREEN, "werewolf.roles.mastermind.guess_success", Formatter.format("&player&", targetWW.getName()));
            getPlayerWW().addPlayerMaxHealth(game.getConfig().getValue(IntValueBase.MASTERMIND_CORRECT_BONUS));
        } else {
            failedPlayers.add(targetWW);
            getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.roles.mastermind.guess_fail");
            getPlayerWW().removePlayerMaxHealth(game.getConfig().getValue(IntValueBase.MASTERMIND_WRONG_PENALTY));
        }
    }

    @Override
    public Set<Category> getAvailableCategories() {
        return new HashSet<>();
    }

    @Override
    public void addAffectedPlayer(IPlayerWW playerWW) {
        guessedPlayers.add(playerWW);
    }

    @Override
    public void removeAffectedPlayer(IPlayerWW playerWW) {
        guessedPlayers.remove(playerWW);
    }

    @Override
    public void clearAffectedPlayer() {
        guessedPlayers.clear();
    }

    @Override
    public List<? extends IPlayerWW> getAffectedPlayers() {
        return guessedPlayers;
    }

    @Override
    public void setPower(boolean b) {
        power = b;
    }

    @Override
    public boolean hasPower() {
        return power;
    }
}
