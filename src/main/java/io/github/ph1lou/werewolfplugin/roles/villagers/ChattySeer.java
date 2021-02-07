package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import org.jetbrains.annotations.NotNull;

public class ChattySeer extends Seer {

    public ChattySeer(GetWereWolfAPI main, PlayerWW playerWW, String key) {
        super(main, playerWW, key);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(() -> game.translate("werewolf.role.chatty_seer.description"))
                .setItems(() -> game.translate("werewolf.role.seer.items"))
                .setEffects(() -> game.translate("werewolf.role.seer.effect"))
                .build();
    }


}
