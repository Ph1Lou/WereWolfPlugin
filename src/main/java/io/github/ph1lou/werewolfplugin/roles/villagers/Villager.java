package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.StateGame;
import io.github.ph1lou.werewolfapi.enums.UniversalMaterial;
import io.github.ph1lou.werewolfapi.enums.VillagerKit;
import io.github.ph1lou.werewolfapi.events.game.utils.EndPlayerMessageEvent;
import io.github.ph1lou.werewolfapi.events.roles.villager.VillagerKitEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.RoleVillage;
import io.github.ph1lou.werewolfapi.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class Villager extends RoleVillage {

    private VillagerKit villagerKit;

    public Villager(WereWolfAPI api, IPlayerWW playerWW, String key) {
        super(api, playerWW, key);
        if (!game.isState(StateGame.GAME)) return;
        villagerKit = VillagerKit.values()[(int) Math.floor(game.getRandom().nextFloat() * VillagerKit.values().length)];
        if (game.getConfig().isTrollSV()) return;
        Bukkit.getPluginManager().callEvent(new VillagerKitEvent(getPlayerWW(), villagerKit.getKey()));
    }


    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setEquipments(() -> game.translate(villagerKit.getDescription()))
                .build();
    }


    @Override
    public void recoverPower() {

        if (game.getConfig().isTrollSV()) return;

        switch (villagerKit) {
            case GOLEM:
                ItemStack book = new ItemBuilder(UniversalMaterial.ENCHANTED_BOOK.getStack())
                        .addEnchant(Enchantment.DURABILITY, 3).build();
                getPlayerWW().addItem(UniversalMaterial.IRON_INGOT.getStack(15));
                getPlayerWW().addItem(book);
                getPlayerWW().addItem(book);
                getPlayerWW().addItem(book);
                break;
            case MINER:
                getPlayerWW().addItem(new ItemBuilder(UniversalMaterial.DIAMOND_PICKAXE.getStack())
                        .addEnchant(Enchantment.DIG_SPEED, 2).build());
                break;
            case ARCHER:
                getPlayerWW().addItem(UniversalMaterial.ARROW.getStack(64));
                getPlayerWW().addItem(UniversalMaterial.STRING.getStack(6));
                break;
            case PRIEST:
                getPlayerWW().addItem(UniversalMaterial.GOLDEN_APPLE.getStack(3));
                break;
            case BLACK_SMITH:
                getPlayerWW().addItem(UniversalMaterial.ANVIL.getStack());
                getPlayerWW().addItem(UniversalMaterial.EXPERIENCE_BOTTLE.getStack(10));
                break;
            case BOOK_SELLER:
                getPlayerWW().addItem(UniversalMaterial.BOOK.getStack(8));
                getPlayerWW().addItem(UniversalMaterial.EXPERIENCE_BOTTLE.getStack(10));
                break;
            default:
                break;
        }

    }

    @EventHandler
    public void onEndPlayerMessage(EndPlayerMessageEvent event) {

        if (!event.getPlayerWW().equals(getPlayerWW())) return;

        StringBuilder sb = event.getEndMessage();

        sb.append(game.translate("werewolf.role.villager.kit", game.translate(villagerKit.getKey())));

    }

}
