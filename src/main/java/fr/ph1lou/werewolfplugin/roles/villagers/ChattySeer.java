package fr.ph1lou.werewolfplugin.roles.villagers;


import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
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
