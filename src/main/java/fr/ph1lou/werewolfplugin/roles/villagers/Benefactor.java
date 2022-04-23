package fr.ph1lou.werewolfplugin.roles.villagers;

import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.role.impl.RoleVillage;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Benefactor extends RoleVillage implements IAffectedPlayers {
    private final List<IPlayerWW> affectedPlayers = new ArrayList<>();

    public Benefactor(WereWolfAPI game, IPlayerWW playerWW, String key) {
        super(game, playerWW, key);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(this.game, this)
                .setDescription(this.game.translate("werewolf.role.benefactor.description"))
                .setItems(this.game.translate("werewolf.role.benefactor.items"))
                .setCommand(this.game.translate("werewolf.role.benefactor.command_description"))
                .setEffects(game.translate("werewolf.role.benefactor.effect"))
                .build();
    }

    @Override
    public void second() {
        super.second();
        if(!this.isAbilityEnabled()) return;
        if(this.affectedPlayers.size() < 3) return;
        if(this.game.getTimer() % (3*60) != 0) return;
        this.getPlayerWW().addPlayerHealth(2);
    }

    @Override
    public void recoverPower() {

    }

    @Override
    public void addAffectedPlayer(IPlayerWW iPlayerWW) {
        this.affectedPlayers.add(iPlayerWW);
    }

    @Override
    public void removeAffectedPlayer(IPlayerWW iPlayerWW) {
        this.affectedPlayers.remove(iPlayerWW);
    }

    @Override
    public void clearAffectedPlayer() {
        this.affectedPlayers.clear();
    }

    @Override
    public List<? extends IPlayerWW> getAffectedPlayers() {
        return this.affectedPlayers;
    }
}