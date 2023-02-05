package fr.ph1lou.werewolfplugin.guis;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.ph1lou.werewolfapi.enums.Camp;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.game.guess.GuessEvent;
import fr.ph1lou.werewolfapi.game.IConfiguration;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IGuesser;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfplugin.Register;
import fr.ph1lou.werewolfplugin.utils.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class GuessInventory implements InventoryProvider {

    private Category category;
    private final Set<Category> categories;
    private final IPlayerWW targetWW;

    public GuessInventory(IPlayerWW targetWW, Set<Category> categories) {
        this.targetWW = targetWW;
        this.categories = categories;
        if (categories.contains(Category.VILLAGER)) {
            category = Category.VILLAGER;
        }
        else if (categories.contains(Category.WEREWOLF)) {
            category = Category.WEREWOLF;
        } else if (categories.contains(Category.NEUTRAL)) {
            category = Category.NEUTRAL;
        } else {
            category = Category.ADDONS;
        }
    }

    public static SmartInventory getInventory(IPlayerWW targetWW, Set<Category> categories) {
        return SmartInventory.builder()
                .id("guess")
                .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
                .provider(new GuessInventory(targetWW, categories))
                .size(6, 9)
                .title(JavaPlugin.getPlugin(Main.class).getWereWolfAPI().translate("werewolf.commands.player.guess.title",
                        Formatter.format("&player&", targetWW.getName())))
                .closeable(true)
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
    }

    @Override
    public void update(Player player, InventoryContents contents) {
        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();
        IConfiguration config = game.getConfig();
        Pagination pagination = contents.pagination();

        if (categories.contains(Category.WEREWOLF)) {
            contents.set(5, 1, ClickableItem.of(
                    new ItemBuilder(
                            Category.WEREWOLF == this.category ?
                                    Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK)
                            .setDisplayName(game.translate(Camp.WEREWOLF.getKey()))
                            .build(), e -> this.category = Category.WEREWOLF));
        }

        if (categories.contains(Category.VILLAGER)) {
            contents.set(5, 3, ClickableItem.of((
                    new ItemBuilder(
                            Category.VILLAGER == this.category ?
                                    Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK)
                            .setDisplayName(game.translate(Camp.VILLAGER.getKey()))
                            .build()), e -> this.category = Category.VILLAGER));
        }

        if (categories.contains(Category.NEUTRAL)) {
            contents.set(5, 5, ClickableItem.of((
                    new ItemBuilder(
                            Category.NEUTRAL == this.category ?
                                    Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK)
                            .setDisplayName(game.translate(Camp.NEUTRAL.getKey()))
                            .build()), e -> this.category = Category.NEUTRAL));
        }

        if (categories.contains(Category.ADDONS)) {
            contents.set(5, 7, ClickableItem.of((
                    new ItemBuilder(
                            Category.ADDONS == this.category ?
                                    Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK)
                            .setDisplayName(game.translate("werewolf.categories.addons"))
                            .build()), e -> this.category = Category.ADDONS));
        }

        List<ClickableItem> items = new ArrayList<>();

        main.getRegisterManager().getRolesRegister()
                .stream()
                .sorted((o1, o2) -> game.translate(o1.getMetaDatas().key())
                        .compareToIgnoreCase(game.translate(o2.getMetaDatas().key())))
                .forEach(roleRegister -> {

                    String key = roleRegister.getMetaDatas().key();
                    Optional<String> addonKey = Register.get().getModuleKey(key);

                    if (roleRegister.getMetaDatas().category() == this.category
                        ||
                        (addonKey.isPresent() &&
                         !addonKey.get().equals(Main.KEY) &&
                         this.category == Category.ADDONS)) {


                        List<String> lore = Arrays.stream(roleRegister.getMetaDatas().loreKey())
                                .map(game::translate)
                                .collect(Collectors.toList());

                        if (config.getTrollKey().equals(key)) {
                            items.add(ClickableItem.empty(new ItemBuilder(UniversalMaterial.GREEN_TERRACOTTA.getStack()).setLore(lore)
                                    .setDisplayName(game.translate(key)).build()));
                        } else {
                            items.add(ClickableItem.of((new ItemBuilder(UniversalMaterial.RED_TERRACOTTA.getStack())
                                    .setLore(lore)
                                    .setDisplayName(game.translate(key)).build()), event -> {

                                UUID uuid = player.getUniqueId();
                                game.getPlayerWW(uuid).ifPresent(iPlayerWW -> {
                                    if ((iPlayerWW.getRole() instanceof IGuesser)) {
                                        IGuesser role = (IGuesser) iPlayerWW.getRole();

                                        role.resolveGuess(key, targetWW);

                                        Bukkit.getPluginManager().callEvent(new GuessEvent(iPlayerWW, targetWW, key));
                                    }
                                });
                                player.closeInventory();
                            }));
                        }

                    }
                });
        InventoryUtils.fillInventory(game, items, pagination, contents, () -> getInventory(targetWW, this.categories), 36);
    }
}
