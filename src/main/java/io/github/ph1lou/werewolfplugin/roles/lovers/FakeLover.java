package io.github.ph1lou.werewolfplugin.roles.lovers;

import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.LoverType;

import java.util.List;

public class FakeLover extends AbstractLover {

    public FakeLover(WereWolfAPI game, List<IPlayerWW> lovers) {
       super(game, lovers);
    }

    @Override
    public LoverType getLoverType() {
        return LoverType.FAKE_LOVER;
    }

    @Override
    public boolean swap(IPlayerWW iPlayerWW, IPlayerWW iPlayerWW1) {
        return false;
    }
}
