package fr.ph1lou.werewolfplugin.roles.lovers;

import fr.ph1lou.werewolfapi.annotations.Lover;
import fr.ph1lou.werewolfapi.basekeys.LoverBase;
import fr.ph1lou.werewolfapi.enums.LoverType;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;

import java.util.List;

@Lover(key = LoverBase.FAKE_LOVER)
public class FakeLover extends AbstractLover {

    public FakeLover(WereWolfAPI game, List<IPlayerWW> lovers) {
        super(game, lovers);
    }

    @Override
    public LoverType getLoverType() {
        return LoverType.FAKE_LOVER;
    }

    @Override
    public boolean swap(IPlayerWW playerWW, IPlayerWW playerWW1) {

        if (playerWW.equals(playerWW1)) return false;

        if (this.getLovers().contains(playerWW1)) return false;

        if (this.death) return false;

        this.lovers.remove(playerWW);
        this.lovers.add(playerWW1);

        this.lovers.forEach(this::announceLovers);

        return true;
    }
}
