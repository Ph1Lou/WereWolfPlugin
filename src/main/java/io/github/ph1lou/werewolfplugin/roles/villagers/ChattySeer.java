package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import org.jetbrains.annotations.NotNull;

public class ChattySeer extends Seer {

    public ChattySeer(WereWolfAPI api, IPlayerWW playerWW, String key) {
        super(api, playerWW, key);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.role.chatty_seer.description"))
                .setItems(game.translate("werewolf.role.seer.items"))
                .setEffects(game.translate("werewolf.role.seer.effect"))
                .build();
    }


}
