package fr.ph1lou.werewolfplugin.roles.villagers;


import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.enums.VillagerKit;
import fr.ph1lou.werewolfapi.events.game.utils.EndPlayerMessageEvent;
import fr.ph1lou.werewolfapi.events.roles.villager.VillagerKitEvent;
import fr.ph1lou.werewolfapi.role.impl.RoleVillage;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;


@Role(key = RoleBase.VILLAGER,
        category = Category.VILLAGER,
        attributes = RoleAttribute.VILLAGER)
public class Villager extends RoleVillage {

    private VillagerKit villagerKit;

    public Villager(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
        if (!game.isState(StateGame.GAME)) return;
        
        villagerKit = VillagerKit.values()[(int) Math.floor(game.getRandom().nextFloat() * VillagerKit.values().length)];
        
        if (game.getConfig().isConfigActive(ConfigBase.TROLL_ROLE)) return;
        
        Bukkit.getPluginManager().callEvent(new VillagerKitEvent(this.getPlayerWW(), villagerKit.getKey()));
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setEquipments(game.translate(villagerKit.getDescription()))
                .build();
    }


    @Override
    public void recoverPower() {

        if (game.getConfig().isConfigActive(ConfigBase.TROLL_ROLE)) return;

        if(!this.isAbilityEnabled()) return;

        switch (villagerKit) {
            case GOLEM:
                ItemStack book = new ItemBuilder(UniversalMaterial.ENCHANTED_BOOK.getStack())
                        .addEnchant(Enchantment.DURABILITY, 3).build();
                this.getPlayerWW().addItem(UniversalMaterial.IRON_INGOT.getStack(15));
                this.getPlayerWW().addItem(book);
                this.getPlayerWW().addItem(book);
                this.getPlayerWW().addItem(book);
                break;
            case MINER:
                this.getPlayerWW().addItem(new ItemBuilder(UniversalMaterial.DIAMOND_PICKAXE.getStack())
                        .addEnchant(Enchantment.DIG_SPEED, 2).build());
                break;
            case ARCHER:
                this.getPlayerWW().addItem(UniversalMaterial.ARROW.getStack(64));
                this.getPlayerWW().addItem(UniversalMaterial.STRING.getStack(6));
                break;
            case PRIEST:
                this.getPlayerWW().addItem(UniversalMaterial.GOLDEN_APPLE.getStack(3));
                break;
            case BLACK_SMITH:
                this.getPlayerWW().addItem(UniversalMaterial.ANVIL.getStack());
                this.getPlayerWW().addItem(UniversalMaterial.EXPERIENCE_BOTTLE.getStack(10));
                break;
            case BOOK_SELLER:
                this.getPlayerWW().addItem(UniversalMaterial.BOOK.getStack(8));
                this.getPlayerWW().addItem(UniversalMaterial.EXPERIENCE_BOTTLE.getStack(10));
                break;
            default:
                break;
        }

    }

    @EventHandler
    public void onEndPlayerMessage(EndPlayerMessageEvent event) {

        if (!event.getPlayerWW().equals(getPlayerWW())) return;

        StringBuilder sb = event.getEndMessage();

        sb.append(game.translate("werewolf.roles.villager.kit",
                Formatter.format("&kit&",game.translate(villagerKit.getKey()))));

    }

}
