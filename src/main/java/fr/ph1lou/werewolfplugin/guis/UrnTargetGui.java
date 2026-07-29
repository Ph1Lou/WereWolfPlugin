package fr.ph1lou.werewolfplugin.guis;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfplugin.buildings.VoteOffice;
import fr.ph1lou.werewolfplugin.game.GameManager;
import fr.ph1lou.werewolfplugin.utils.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class UrnTargetGui implements InventoryProvider {

    private final VoteOffice office;

    public UrnTargetGui(VoteOffice office) {
        this.office = office;
    }

    public static SmartInventory getInventory(VoteOffice office) {
        return SmartInventory.builder()
                .id("urn-target")
                .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
                .provider(new UrnTargetGui(office))
                .size(5, 9)
                .title(JavaPlugin.getPlugin(Main.class).getWereWolfAPI().translate("werewolf.configurations.buildings.urn_target_menu"))
                .closeable(true)
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        GameManager game = (GameManager) JavaPlugin.getPlugin(Main.class).getWereWolfAPI();
        Pagination pagination = contents.pagination();

        fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW voterWW =
                game.getPlayerWW(player.getUniqueId()).orElse(null);

        List<ClickableItem> items = new ArrayList<>();

        game.getAlivePlayersWW().forEach(targetWW -> {
            if (targetWW.getUUID().equals(player.getUniqueId())) {
                return;
            }
            if (!targetWW.isState(StatePlayer.ALIVE) && !targetWW.isState(StatePlayer.JUDGEMENT)) {
                return;
            }
            if (voterWW != null && !voterWW.getRole().getPlayersMet().contains(targetWW)) {
                return;
            }
            items.add(ClickableItem.of(new ItemBuilder(UniversalMaterial.PLAYER_HEAD.getStack())
                            .setHead(targetWW.getName(), Bukkit.getOfflinePlayer(targetWW.getReviewUUID()))
                            .setDisplayName(game.translate("werewolf.configurations.buildings.urn_target",
                                    fr.ph1lou.werewolfapi.player.utils.Formatter.player(targetWW.getName())))
                            .build(),
                    e -> {
                        UrnVoteGuiHelper.recordVoteAgainst(game, player, office, targetWW);
                        player.closeInventory();
                    }));
        });

        InventoryUtils.fillInventory(game, items, pagination, contents, () -> getInventory(office), 36);
    }

    @Override
    public void update(Player player, InventoryContents contents) {
    }
}
