package io.github.ph1lou.werewolfplugin.roles.lovers;

import io.github.ph1lou.werewolfapi.Formatter;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Prefix;

import java.util.List;

public class FakeLoverCharmer extends FakeLover{

    private IPlayerWW charmer;

    public FakeLoverCharmer(WereWolfAPI game, List<IPlayerWW> lovers, IPlayerWW charmer) {
        super(game, lovers);
        this.charmer=charmer;
    }

    @Override
    public void announceLovers(IPlayerWW playerWW) {
        if(playerWW.equals(this.charmer)){
            this.charmer
                    .sendMessageWithKey(Prefix.YELLOW.getKey(),
                            "werewolf.role.charmer.announcement",
                            Formatter.player(this.getLovers().stream()
                                    .filter(playerWW1 -> !playerWW1.equals(playerWW))
                                    .map(IPlayerWW::getName)
                                    .findFirst()
                                    .orElse(null)));
        }
        else{
            super.announceLovers(playerWW);
        }
    }

    @Override
    public boolean swap(IPlayerWW playerWW, IPlayerWW playerWW1) {
        if(playerWW.equals(this.charmer)){
            this.charmer = playerWW1;
        }
        return super.swap(playerWW, playerWW1);
    }

    public IPlayerWW getCharmer() {
        return charmer;
    }
}
