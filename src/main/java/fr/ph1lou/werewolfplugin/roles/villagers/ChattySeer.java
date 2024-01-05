package fr.ph1lou.werewolfplugin.roles.villagers;


import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.jetbrains.annotations.NotNull;

@Role(key = RoleBase.CHATTY_SEER,
        category = Category.VILLAGER,
        attribute = RoleAttribute.INFORMATION)
public class ChattySeer extends Seer {

    public ChattySeer(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.chatty_seer.description"))
                .setItems(game.translate("werewolf.roles.seer.items"))
                .setEffects(game.translate("werewolf.roles.seer.effect"))
                .build();
    }


}
