package fr.ph1lou.werewolfplugin.roles.lovers;

import fr.ph1lou.werewolfapi.annotations.Lover;
import fr.ph1lou.werewolfapi.basekeys.LoverBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;

import java.util.List;

@Lover(key = LoverBase.FAKE_LOVER, color = LoverImpl.COLOR)
public class FakeLoverCharmer extends FakeLover {

    private IPlayerWW charmer;

    public FakeLoverCharmer(WereWolfAPI game, List<IPlayerWW> lovers, IPlayerWW charmer) {
        super(game, lovers);
        this.charmer = charmer;
    }

    @Override
    public void announceLovers(IPlayerWW playerWW) {
        if (playerWW.equals(this.charmer)) {
            this.charmer
                    .sendMessageWithKey(Prefix.YELLOW,
                            "werewolf.roles.charmer.announcement",
                            Formatter.player(this.getLovers().stream()
                                    .filter(playerWW1 -> !playerWW1.equals(playerWW))
                                    .map(IPlayerWW::getName)
                                    .findFirst()
                                    .orElse(null)));
        } else {
            super.announceLovers(playerWW);
        }
    }

    @Override
    public boolean swap(IPlayerWW playerWW, IPlayerWW playerWW1) {
        if (playerWW.equals(this.charmer)) {
            this.charmer = playerWW1;
        }
        return super.swap(playerWW, playerWW1);
    }

    public IPlayerWW getCharmer() {
        return charmer;
    }
}
