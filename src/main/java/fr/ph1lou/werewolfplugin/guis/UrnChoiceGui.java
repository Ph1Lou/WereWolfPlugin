package fr.ph1lou.werewolfplugin.guis;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfplugin.buildings.UrnVoteManager;
import fr.ph1lou.werewolfplugin.buildings.VoteOffice;
import fr.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class UrnChoiceGui implements InventoryProvider {

    private final VoteOffice office;

    public UrnChoiceGui(VoteOffice office) {
        this.office = office;
    }

    public static SmartInventory getInventory(VoteOffice office) {
        return SmartInventory.builder()
                .id("urn-choice")
                .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
                .provider(new UrnChoiceGui(office))
                .size(3, 9)
                .title(JavaPlugin.getPlugin(Main.class).getWereWolfAPI().translate("werewolf.configurations.buildings.urn_menu"))
                .closeable(true)
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        GameManager game = (GameManager) JavaPlugin.getPlugin(Main.class).getWereWolfAPI();
        UrnVoteManager urn = game.getBuildingsManager().getUrnVoteManager();
        UUID uuid = player.getUniqueId();

        if (office.isFull()) {
            contents.set(1, 4, ClickableItem.empty(new ItemBuilder(UniversalMaterial.BARRIER.getStack())
                    .setDisplayName(game.translate("werewolf.configurations.buildings.urn_full"))
                    .build()));
            return;
        }

        if (urn.hasVotedAgainst(uuid)) {
            contents.set(1, 4, ClickableItem.empty(new ItemBuilder(UniversalMaterial.BARRIER.getStack())
                    .setDisplayName(game.translate("werewolf.configurations.buildings.urn_already_voted_against"))
                    .build()));
            return;
        }

        if (urn.hasVotedBlankOnce(uuid)) {
            contents.set(1, 2, ClickableItem.of(new ItemBuilder(UniversalMaterial.BARRIER.getStack())
                            .setDisplayName(game.translate("werewolf.configurations.buildings.urn_nothing"))
                            .build(),
                    e -> player.closeInventory()));

            if (!urn.hasBlankVotedUrn(uuid, office.getUrnLocation())) {
                contents.set(1, 6, ClickableItem.of(new ItemBuilder(UniversalMaterial.WHITE_WOOL.getStack())
                                .setDisplayName(game.translate("werewolf.configurations.buildings.urn_vote_blank_again"))
                                .build(),
                        e -> {
                            UrnVoteGuiHelper.recordBlank(game, player, office, true);
                            player.closeInventory();
                        }));
            }
            return;
        }

        contents.set(1, 2, ClickableItem.of(new ItemBuilder(UniversalMaterial.PLAYER_HEAD.getStack())
                        .setDisplayName(game.translate("werewolf.configurations.buildings.urn_vote_against"))
                        .build(),
                e -> UrnTargetGui.getInventory(office).open(player)));

        contents.set(1, 6, ClickableItem.of(new ItemBuilder(UniversalMaterial.WHITE_WOOL.getStack())
                        .setDisplayName(game.translate("werewolf.configurations.buildings.urn_vote_blank"))
                        .build(),
                e -> {
                    UrnVoteGuiHelper.recordBlank(game, player, office, false);
                    player.closeInventory();
                }));
    }

    @Override
    public void update(Player player, InventoryContents contents) {
    }
}
