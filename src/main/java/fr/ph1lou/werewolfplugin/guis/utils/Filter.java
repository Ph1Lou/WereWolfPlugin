package fr.ph1lou.werewolfplugin.guis.utils;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Camp;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import fr.ph1lou.werewolfapi.utils.Wrapper;
import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfplugin.Register;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public abstract class Filter<T>{

    public static final String CATEGORY_FILTER = "werewolf.menus.roles.filters.category";
    public static final String AURA_FILTER = "werewolf.menus.roles.filters.aura";
    public static final String ATTRIBUTE_FILTER = "werewolf.menus.roles.filters.attribute";

    public static Filter<?> getNextFilter(String currentFilter, boolean next){

        switch (currentFilter){
            case CATEGORY_FILTER:
                if(next){
                    return getAttributeFilter(RoleAttribute.HYBRID);
                }
                return getAuraFilter(Aura.LIGHT);
            case ATTRIBUTE_FILTER:
                if(next){
                    return getAuraFilter(Aura.LIGHT);
                }
                return getCategoryFilter(Category.WEREWOLF);
            case AURA_FILTER:
                if(next){
                    return getCategoryFilter(Category.WEREWOLF);
                }
                return getAttributeFilter(RoleAttribute.HYBRID);
        }
        return null;
    }

    public static Filter<Category> getCategoryFilter(Category category) {
        return new Filter<Category>(category, CATEGORY_FILTER) {

            @Override
            public boolean filter(Role role) {
                Optional<String> addonKey = Register.get().getModuleKey(role.key());

                if(this.initializer == null){
                    return addonKey.isPresent() && !addonKey.get().equals(Main.KEY);
                }
                return role.category() == this.initializer;
            }

            @Override
            public void setColumnFilterOverride(InventoryContents contents, WereWolfAPI game) {

                contents.set(5, 1, getItemBuilder(Category.WEREWOLF, game.translate(Camp.WEREWOLF.getKey()), game));
                contents.set(5, 3, getItemBuilder(Category.VILLAGER, game.translate(Camp.VILLAGER.getKey()), game));
                contents.set(5, 5, getItemBuilder(Category.NEUTRAL, game.translate(Camp.NEUTRAL.getKey()), game));
                contents.set(5, 7, ClickableItem.of(new ItemBuilder(this.initializer == null ?
                        new ItemStack(Material.STICK) :
                        UniversalMaterial.ORANGE_STAINED_GLASS_PANE.getStack())
                        .setDisplayName(game.translate("werewolf.categories.addons"))
                        .build(), (e) -> this.initializer = null));
            }

            @Override
            public int count(WereWolfAPI game, Category category) {
                int i = 0;
                for (Wrapper<IRole, Role> roleRegister : Register.get().getRolesRegister()) {
                    if (roleRegister.getMetaDatas().category() == category) {
                        i += game.getConfig().getRoleCount(roleRegister.getMetaDatas().key());
                    }

                }
                return i;
            }
        };

    }

    public static Filter<Aura> getAuraFilter(Aura aura) {

        return new Filter<Aura>(aura, AURA_FILTER) {

            @Override
            public boolean filter(Role role) {
                return role.defaultAura() == this.initializer;
            }

            @Override
            public void setColumnFilterOverride(InventoryContents contents, WereWolfAPI game) {

                contents.set(5, 2, getItemBuilder(Aura.LIGHT, game.translate(Aura.LIGHT.getKey()), game));
                contents.set(5, 4, getItemBuilder(Aura.DARK, game.translate(Aura.DARK.getKey()), game));
                contents.set(5, 6, getItemBuilder(Aura.NEUTRAL, game.translate(Aura.NEUTRAL.getKey()), game));
            }

            @Override
            public int count(WereWolfAPI game, Aura aura1) {
                int i = 0;
                for (Wrapper<IRole, Role> roleRegister : Register.get().getRolesRegister()) {
                    if (roleRegister.getMetaDatas().defaultAura() == aura1) {
                        i += game.getConfig().getRoleCount(roleRegister.getMetaDatas().key());
                    }

                }
                return i;
            }
        };

    }

    public static Filter<RoleAttribute> getAttributeFilter(RoleAttribute aura) {

        return new Filter<RoleAttribute>(aura, ATTRIBUTE_FILTER) {

            @Override
            public boolean filter(Role role) {
                return role.attribute() == this.initializer;
            }

            @Override
            public void setColumnFilterOverride(InventoryContents contents, WereWolfAPI game) {

                contents.set(5, 1, getItemBuilder(RoleAttribute.HYBRID, game.translate(RoleAttribute.HYBRID.getKey()), game));
                contents.set(5, 2, getItemBuilder(RoleAttribute.INFORMATION, game.translate(RoleAttribute.INFORMATION.getKey()), game));
                contents.set(5, 3, getItemBuilder(RoleAttribute.MINOR_INFORMATION, game.translate(RoleAttribute.MINOR_INFORMATION.getKey()), game));
                contents.set(5, 4, getItemBuilder(RoleAttribute.VILLAGER, game.translate(RoleAttribute.VILLAGER.getKey()), game));
                contents.set(5, 5, getItemBuilder(RoleAttribute.WEREWOLF, game.translate(RoleAttribute.WEREWOLF.getKey()), game));
                contents.set(5, 6, getItemBuilder(RoleAttribute.NEUTRAL, game.translate(RoleAttribute.NEUTRAL.getKey()), game));
            }

            @Override
            public int count(WereWolfAPI game, RoleAttribute aura1) {
                int i = 0;
                for (Wrapper<IRole, Role> roleRegister : Register.get().getRolesRegister()) {
                    if (roleRegister.getMetaDatas().attribute() == aura1) {
                        i += game.getConfig().getRoleCount(roleRegister.getMetaDatas().key());
                    }

                }
                return i;
            }
        };

    }

    protected T initializer;

    private final String key;

    public Filter(T initializer, String key){
        this.initializer = initializer;
        this.key = key;
    }

    public abstract boolean filter(Role role);

    public abstract int count(WereWolfAPI game, T t);


    public final void setColumnFilter(InventoryContents inventoryContents, WereWolfAPI game){
        inventoryContents.set(5, 0, null);
        inventoryContents.set(5, 1, null);
        inventoryContents.set(5, 2, null);
        inventoryContents.set(5, 3, null);
        inventoryContents.set(5, 4, null);
        inventoryContents.set(5, 5, null);
        inventoryContents.set(5, 6, null);
        inventoryContents.set(5, 7, null);

        this.setColumnFilterOverride(inventoryContents, game);
    }

    public abstract void setColumnFilterOverride(InventoryContents inventoryContents, WereWolfAPI game);

    public ClickableItem getItemBuilder(T selector, String name, WereWolfAPI game){

        ItemBuilder itemBuilder;
        int number = count(game, selector);

        if(selector == this.initializer){
            itemBuilder = new ItemBuilder(Material.STICK);
        }
        else if(number > 0){
            itemBuilder = new ItemBuilder(UniversalMaterial.GREEN_STAINED_GLASS_PANE.getStack());
        }
        else{
            itemBuilder = new ItemBuilder(UniversalMaterial.RED_STAINED_GLASS_PANE.getStack());
        }

        return ClickableItem.of(itemBuilder.setDisplayName(name)
                .setAmount(Math.max(1, number))
                .build(), e -> this.initializer = selector);

    }

    public String getKey(){
        return this.key;
    }
}
