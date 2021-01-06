package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enums.StateGame;
import io.github.ph1lou.werewolfapi.enums.UniversalMaterial;
import io.github.ph1lou.werewolfapi.enums.VillagerKit;
import io.github.ph1lou.werewolfapi.events.EndPlayerMessageEvent;
import io.github.ph1lou.werewolfapi.events.VillagerKitEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesVillage;
import io.github.ph1lou.werewolfapi.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

public class Villager extends RolesVillage {

    private VillagerKit villagerKit;

    public Villager(GetWereWolfAPI main, PlayerWW playerWW, String key) {
        super(main, playerWW, key);
        if (game.isState(StateGame.LOBBY)) return;
        villagerKit = VillagerKit.values()[(int) Math.floor(game.getRandom().nextFloat() * VillagerKit.values().length)];
        Bukkit.getPluginManager().callEvent(new VillagerKitEvent(getPlayerWW(), villagerKit.getKey()));
    }


    @Override
    public @NotNull String getDescription() {
        return super.getDescription() +
                game.translate("werewolf.description.equipment", game.translate(villagerKit.getDescription()));
    }


    @Override
    public void recoverPower() {

        if (game.getConfig().isTrollSV()) return;

        switch (villagerKit) {
            case GOLEM:
                getPlayerWW().addItem(UniversalMaterial.IRON_INGOT.getStack(15));
                getPlayerWW().addItem(new ItemBuilder(UniversalMaterial.ENCHANTED_BOOK.getStack()).addEnchant(Enchantment.DURABILITY, 3).setAmount(3).build());
                break;
            case MINER:
                getPlayerWW().addItem(new ItemBuilder(UniversalMaterial.DIAMOND_PICKAXE.getStack()).addEnchant(Enchantment.DIG_SPEED, 2).build());
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
