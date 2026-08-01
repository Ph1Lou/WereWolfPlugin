package fr.ph1lou.werewolfplugin.guis;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.buildings.IVoteOffice;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfplugin.buildings.UrnVoteManager;
import fr.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;
import java.util.UUID;

public class UrnChoiceGui implements InventoryProvider {

    private final IVoteOffice office;

    public UrnChoiceGui(IVoteOffice office) {
        this.office = office;
    }

    public static SmartInventory getInventory(IVoteOffice office) {
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

        Optional<IPlayerWW> playerWW = game.getPlayerWW(uuid);

        if (!playerWW.isPresent()) {
            return;
        }

        if (office.isFull()) {
            contents.set(1, 4, ClickableItem.empty(new ItemBuilder(UniversalMaterial.BARRIER.getStack())
                    .setDisplayName(game.translate("werewolf.configurations.buildings.urn_full"))
                    .build()));
            return;
        }

        if (urn.hasVotedAgainst(playerWW.get())) {
            contents.set(1, 4, ClickableItem.empty(new ItemBuilder(UniversalMaterial.BARRIER.getStack())
                    .setDisplayName(game.translate("werewolf.configurations.buildings.urn_already_voted_against"))
                    .build()));
            return;
        }

        if (urn.hasVotedBlankOnce(playerWW.get())) {
            contents.set(1, 2, ClickableItem.of(new ItemBuilder(UniversalMaterial.BARRIER.getStack())
                            .setDisplayName(game.translate("werewolf.configurations.buildings.urn_nothing"))
                            .build(),
                    e -> player.closeInventory()));

            if (!urn.hasBlankVotedUrn(playerWW.get(), office.getUrnLocation())) {
                contents.set(1, 6, ClickableItem.of(new ItemBuilder(UniversalMaterial.WHITE_WOOL.getStack())
                                .setDisplayName(game.translate("werewolf.configurations.buildings.urn_vote_blank_again"))
                                .build(),
                        e -> {
                            recordBlank(game, playerWW.get(), office, true);
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
                    recordBlank(game, playerWW.get(), office, false);
                    player.closeInventory();
                }));
    }

    @Override
    public void update(Player player, InventoryContents contents) {
    }

    private void recordBlank(GameManager game, IPlayerWW playerWW, IVoteOffice office, boolean additional) {
        UrnVoteManager urn = game.getBuildingsManager().getUrnVoteManager();

        if (!urn.isActive()) {
            return;
        }
        if (office.isFull()) {
            return;
        }
        if (additional && urn.hasBlankVotedUrn(playerWW, office.getUrnLocation())) {
            return;
        }

        office.recordBlank(playerWW);
        urn.markBlankVotedUrn(playerWW, office.getUrnLocation());
        if (!additional) {
            urn.markVotedBlankOnce(playerWW);
        }

        if (additional) {
            playerWW.modifyHonor(-1);
        }

        urn.fireCastEvent(playerWW, null, true, office);

        playerWW.sendMessageWithKey(Prefix.YELLOW, "werewolf.configurations.buildings.urn_voted_blank");
    }
}
