package fr.ph1lou.werewolfplugin.guis.elections;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.enums.ElectionState;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfplugin.configs.Elections;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ElectionGUI implements InventoryProvider {

    public ElectionGUI(Player player) {

    }

    public static SmartInventory getInventory(Player player) {
        return SmartInventory.builder()
                .id("election")
                .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
                .provider(new ElectionGUI(player))
                .size(6, 9)
                .title(JavaPlugin.getPlugin(Main.class).getWereWolfAPI().translate("werewolf.elections.election.menu_title"))
                .closeable(true)
                .build();
    }


    @Override
    public void init(Player player, InventoryContents contents) {

        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();

        IPlayerWW playerWW = game.getPlayerWW(player.getUniqueId()).orElse(null);


        if (playerWW == null) return;

        contents.fillBorders(ClickableItem.empty(UniversalMaterial.ORANGE_STAINED_GLASS_PANE.getStack()));

        AtomicInteger i = new AtomicInteger(10);

        game.getListenersManager().getConfiguration(ConfigBase.ELECTIONS)
                .ifPresent(electionManager1 -> {
                    Elections electionManager = (Elections) electionManager1;
                    electionManager.getCandidates().forEach(candidateWW -> {

                        if (!electionManager.isState(ElectionState.ELECTION)) return;

                        contents.set(i.get() / 9, i.get() % 9,
                                ClickableItem.of(new ItemBuilder(UniversalMaterial.PLAYER_HEAD.getStack())
                                        .setHead(candidateWW.getName(),
                                                Bukkit.getOfflinePlayer(candidateWW.getReviewUUID()))
                                        .setLore(electionManager.getPlayerMessage(candidateWW).orElse(""))
                                        .build(), event -> {
                                    if (electionManager.isState(ElectionState.ELECTION)) {
                                        electionManager.addVote(playerWW, candidateWW);
                                    }
                                }));
                        i.set(i.get() + 2);
                    });
                });

    }

    @Override
    public void update(Player player, InventoryContents contents) {

        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();

        IPlayerWW playerWW = game.getPlayerWW(player.getUniqueId()).orElse(null);

        AtomicInteger i = new AtomicInteger(10);

        if (playerWW == null) return;


        game.getListenersManager().getConfiguration(ConfigBase.ELECTIONS)
                .ifPresent(electionManager1 -> {

                    Elections electionManager = (Elections) electionManager1;

                    electionManager.getCandidates().forEach(candidateWW -> {

                        if (!electionManager.isState(ElectionState.ELECTION)) return;


                        List<String> lore = electionManager.getVoters(candidateWW)
                                .stream()
                                .map(IPlayerWW::getName)
                                .collect(Collectors.toList());

                        lore.add(0, game.translate("werewolf.elections.election.application",
                                Formatter.format("&application&", electionManager.getPlayerMessage(candidateWW).orElse(""))));


                        contents.get(i.get() / 9, i.get() % 9).ifPresent(clickableItem -> {
                            ItemBuilder item = new ItemBuilder(clickableItem.getItem()).setLore(lore);
                            contents.set(i.get() / 9, i.get() % 9, ClickableItem.of(item.build(), event -> {
                                if (electionManager.isState(ElectionState.ELECTION)) {
                                    electionManager.addVote(playerWW, candidateWW);
                                }
                            }));
                        });
                        i.set(i.get() + 2);
                    });
                });


    }

}

