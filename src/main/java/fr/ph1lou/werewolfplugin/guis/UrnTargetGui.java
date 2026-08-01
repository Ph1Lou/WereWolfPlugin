package fr.ph1lou.werewolfplugin.guis;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.buildings.IVoteOffice;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfplugin.buildings.UrnVoteManager;
import fr.ph1lou.werewolfplugin.game.GameManager;
import fr.ph1lou.werewolfplugin.roles.werewolfs.MesquinWereWolf;
import fr.ph1lou.werewolfplugin.utils.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UrnTargetGui implements InventoryProvider {

    private final IVoteOffice office;

    public UrnTargetGui(IVoteOffice office) {
        this.office = office;
    }

    public static SmartInventory getInventory(IVoteOffice office) {
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

        Optional<IPlayerWW> voterWW = game.getPlayerWW(player.getUniqueId());

        if (!voterWW.isPresent()) {
            return;
        }

        List<ClickableItem> items = new ArrayList<>();

        game.getAlivePlayersWW().forEach(targetWW -> {
            if (targetWW.getUUID().equals(player.getUniqueId())) {
                return;
            }
            if (!targetWW.isState(StatePlayer.ALIVE) && !targetWW.isState(StatePlayer.JUDGEMENT)) {
                return;
            }
            if (!voterWW.get().getRole().getPlayersMet().contains(targetWW)) {
                return;
            }
            if (game.getBuildingsManager().getUrnVoteManager().isElected(targetWW)) {
                return;
            }
            items.add(ClickableItem.of(new ItemBuilder(UniversalMaterial.PLAYER_HEAD.getStack())
                            .setHead(targetWW.getName(), Bukkit.getOfflinePlayer(targetWW.getReviewUUID()))
                            .setDisplayName(game.translate("werewolf.configurations.buildings.urn_target",
                                    fr.ph1lou.werewolfapi.player.utils.Formatter.player(targetWW.getName())))
                            .build(),
                    e -> {
                        recordVoteAgainst(game, voterWW.get(), office, targetWW);
                        player.closeInventory();
                    }));
        });

        InventoryUtils.fillInventory(game, items, pagination, contents, () -> getInventory(office), 36);
    }

    @Override
    public void update(Player player, InventoryContents contents) {
    }

    private void recordVoteAgainst(GameManager game, IPlayerWW playerWW, IVoteOffice office, IPlayerWW target) {
        UrnVoteManager urn = game.getBuildingsManager().getUrnVoteManager();

        if (!urn.isActive()) {
            return;
        }
        if (office.isFull()) {
            return;
        }
        if (urn.hasVotedAgainst(playerWW)) {
            return;
        }
        if (urn.isElected(target)) {
            return;
        }

        office.recordVoteAgainst(target);
        urn.markVotedAgainst(playerWW);

        if (!MesquinWereWolf.isAnyUrnRiggedAt(game, office.getUrnLocation())) {
            playerWW.modifyHonor(1);
        }

        urn.fireCastEvent(playerWW, target, false, office);

        playerWW.sendMessageWithKey(Prefix.YELLOW, "werewolf.configurations.buildings.urn_voted_against",
                Formatter.player(target.getName()));
    }
}
