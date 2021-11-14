package io.github.ph1lou.werewolfplugin.roles.neutrals;


import fr.minuskube.inv.ClickableItem;
import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.Formatter;
import io.github.ph1lou.werewolfapi.IConfiguration;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.PotionModifier;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Aura;
import io.github.ph1lou.werewolfapi.enums.Prefix;
import io.github.ph1lou.werewolfapi.enums.RolesBase;
import io.github.ph1lou.werewolfapi.enums.Sound;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.UniversalMaterial;
import io.github.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import io.github.ph1lou.werewolfapi.events.roles.flute_player.AllPlayerEnchantedEvent;
import io.github.ph1lou.werewolfapi.events.roles.flute_player.EnchantedEvent;
import io.github.ph1lou.werewolfapi.events.roles.flute_player.FindFluteEvent;
import io.github.ph1lou.werewolfapi.events.roles.flute_player.GiveFluteEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.IPower;
import io.github.ph1lou.werewolfapi.rolesattributs.RoleNeutral;
import io.github.ph1lou.werewolfapi.utils.BukkitUtils;
import io.github.ph1lou.werewolfapi.utils.ItemBuilder;
import io.github.ph1lou.werewolfapi.versions.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class FlutePlayer extends RoleNeutral implements IPower, IAffectedPlayers {

    private static ItemStack flute;
    private final List<IPlayerWW> flutedPlayer = new ArrayList<>();
    private final Map<IPlayerWW, Integer> progress = new HashMap<>();
    private final List<IPlayerWW> affectedPlayer = new ArrayList<>();
    private boolean power = true;
    private boolean hasOwnFlute = false;
    private int timer = 0; //pour faire les calculs une fois toutes les 6 secondes

    private boolean all = false;

    private int fluteInStore = 0;

    public FlutePlayer(WereWolfAPI api, IPlayerWW playerWW, String key) {
        super(api, playerWW, key);
        this.registerCustomCraft();
    }

    public void registerCustomCraft() {

        if(flute != null){
            return;
        }

        flute = new ItemBuilder(Material.STICK)
                .addItemFlag(ItemFlag.HIDE_ENCHANTS)
                .setDisplayName(game.translate("werewolf.role.flute_player.item"))
                .addEnchant(Enchantment.ARROW_FIRE, 1).build();

        ShapedRecipe recipe = VersionUtils.getVersionUtils().registerCraft(flute, "recipe_flute_player");

        recipe.shape("OOO", "OSO", "OOO");

        recipe.setIngredient('O', Material.GOLD_INGOT);
        recipe.setIngredient('S', Material.STICK);
        Bukkit.addRecipe(recipe);
    }

    @Override
    public void addAffectedPlayer(IPlayerWW playerWW) {
        this.affectedPlayer.add(playerWW);
    }

    @Override
    public void removeAffectedPlayer(IPlayerWW playerWW) {
        this.affectedPlayer.remove(playerWW);
    }

    @Override
    public void clearAffectedPlayer() {
        this.affectedPlayer.clear();
    }

    @Override
    public List<IPlayerWW> getAffectedPlayers() {
        return (this.affectedPlayer);
    }

    @Override
    public @NotNull String getDescription() {

        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.role.flute_player.description",
                                Formatter.format("&number&",game.getConfig().getDistanceFlutePlayer())))
                .setPower(game.translate("werewolf.role.flute_player.power"))
                .setItems(game.translate("werewolf.role.flute_player.craft_description"))
                .setEffects(game.translate("werewolf.role.flute_player.effect"))
                .addExtraLines(game.translate("werewolf.role.flute_player.affected",
                        Formatter.format("&list&",affectedPlayer.isEmpty() ? "" : enchantedList())))
                .build();
    }

    @EventHandler
    public void onInventoryClick(CraftItemEvent event) {

        if (event.getSlotType() != InventoryType.SlotType.RESULT) {
            return;
        }

        if (!event.getWhoClicked().getUniqueId().equals(this.getPlayerUUID())) {
            return;
        }

        if (!flute.equals(event.getCurrentItem())) {
            return;
        }

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (!this.hasOwnFlute) {
            this.hasOwnFlute = true;
            this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW.getKey() , "werewolf.role.flute_player.perform",
                    Formatter.format("&number&",game.getConfig().getDistanceFlutePlayer()));
        } else {
            this.fluteInStore++;
            this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW.getKey() , "werewolf.role.flute_player.craft",
                    Formatter.format("&number&",this.fluteInStore));
        }
        event.setResult(Event.Result.ALLOW);
        event.getInventory().setResult(null);

        ItemStack[] inventories = event.getInventory().getMatrix();

        Arrays.stream(inventories)
                .filter(Objects::nonNull)
                .filter(itemStack -> itemStack.getAmount() > 1)
                .forEach(itemStack -> {
                    ItemStack itemStack1 = itemStack.clone();
                    itemStack1.setAmount(itemStack.getAmount() - 1);
                    this.getPlayerWW().addItem(itemStack1);
                });

        event.getInventory().clear();

        BukkitUtils.scheduleSyncDelayedTask(() -> event.getWhoClicked().closeInventory());

    }

    @EventHandler
    public void onFluteCraft(PrepareItemCraftEvent event) {

        if (!flute.equals(event.getInventory().getResult())) {
            return;
        }

        IPlayerWW playerWW = game.getPlayerWW(event.getView()
                .getPlayer().getUniqueId()).orElse(null);

        if(playerWW==null){
            return;
        }

        if(!playerWW.getRole().isKey(RolesBase.FLUTE_PLAYER.getKey())){
            event.getInventory().setResult(new ItemStack(Material.AIR));
        }
    }

    @EventHandler
    public void onDay(DayEvent event) {

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (!this.hasOwnFlute) {
            return;
        }

        this.power = true;

        this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW.getKey() , "werewolf.role.flute_player.day",
                Formatter.format("&number&",this.fluteInStore));
    }

    @Override
    public void second() {

        if (!this.hasOwnFlute) {
            return;
        }

        this.timer++;
        this.timer %= 6;

        if (this.timer != 0) {
            return;
        }

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) {
            return;
        }
        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        AtomicBoolean recoverResistance = new AtomicBoolean(false);

        Bukkit.getOnlinePlayers().stream()
                .filter(player1 -> !player.equals(player1))
                .filter(player1 -> this.checkDistance(player, player1) || this.checkDistance(player1))
                .map(Entity::getUniqueId)
                .map(game::getPlayerWW)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .peek(playerWW -> {
                    if (this.affectedPlayer.contains(playerWW)) {
                        recoverResistance.set(true);
                    }
                })
                .filter(playerWW -> !this.affectedPlayer.contains(playerWW))
                .forEach(playerWW -> {

                    if (isAbilityEnabled()) {
                        this.progress.merge(playerWW, 1, Integer::sum);

                        if (this.progress.get(playerWW) > 100) {

                            EnchantedEvent event = new EnchantedEvent(this.getPlayerWW(), playerWW);

                            Bukkit.getPluginManager().callEvent(event);

                            if (!event.isCancelled()) {
                                this.affectedPlayer.add(playerWW);
                                this.progress.remove(playerWW);
                                this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW.getKey() , "werewolf.role.flute_player.enchanted",
                                        Formatter.format("&player&",playerWW.getName()));
                                this.checkStrength();
                            }
                        }
                    }
                });

        if (recoverResistance.get()) {
            this.getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.DAMAGE_RESISTANCE, 240, 0,"pied_piper"));
        }
    }

    private boolean checkDistance(Player player1) {
        return this.flutedPlayer.stream()
                .map(IPlayerWW::getUUID)
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .anyMatch(player -> checkDistance(player, player1));
    }

    private boolean checkDistance(Player player, Player player1) {
        return player.getWorld().equals(player1.getWorld()) &&
                player.getLocation().distance(player1.getLocation())
                        < game.getConfig().getDistanceFlutePlayer();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (!this.hasOwnFlute) {
            return;
        }

        if (!flute.equals(event.getCurrentItem())) {
            return;
        }

        IPlayerWW playerWW = this.flutedPlayer.stream()
                .filter(playerWW1 -> playerWW1.getUUID().equals(event.getWhoClicked().getUniqueId()))
                .findFirst().orElse(null);
        if (playerWW != null) {
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.role.flute_player.find");
            Sound.ANVIL_BREAK.play(playerWW);
            this.getPlayerWW().sendMessageWithKey(Prefix.RED.getKey() , "werewolf.role.flute_player.find_flute",
                    Formatter.format("&player&",playerWW.getName()));
            event.setCurrentItem(null);
            this.flutedPlayer.remove(playerWW);
            event.getWhoClicked().closeInventory();
            Bukkit.getPluginManager().callEvent(new FindFluteEvent(playerWW, this.getPlayerWW()));
        }
    }

    @EventHandler
    public void onInteraction(PlayerInteractAtEntityEvent event) {

        if (!this.hasOwnFlute) {
            return;
        }

        if (this.fluteInStore <= 0) {
            return;
        }

        if (!event.getPlayer().getUniqueId().equals(this.getPlayerUUID())) {
            return;
        }

        if (!(event.getRightClicked() instanceof Player)) {
            return;
        }
        Player clickedPlayer = (Player) event.getRightClicked();

        IPlayerWW clickedPlayerWW = game.getPlayerWW(clickedPlayer.getUniqueId()).orElse(null);

        if (clickedPlayerWW == null) {
            return;
        }

        if (this.flutedPlayer.contains(clickedPlayerWW)) {
            return;
        }

        if (!this.power) {
            this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW.getKey() , "werewolf.role.flute_player.wait");
            return;
        }

        PlayerInventory inventory = clickedPlayer.getInventory();
        for (int j = 9; j < 40; j++) {
            if (inventory.getItem(j) == null) {
                inventory.setItem(j, flute);
                Bukkit.getPluginManager().callEvent(new GiveFluteEvent(clickedPlayerWW, this.getPlayerWW()));
                this.flutedPlayer.add(clickedPlayerWW);
                this.power = false;
                this.fluteInStore--;
                this.getPlayerWW().sendMessageWithKey(Prefix.GREEN.getKey() , "werewolf.role.flute_player.transmit",
                        Formatter.format("&player&",clickedPlayerWW.getName()),
                        Formatter.format("&number&",this.fluteInStore));
                return;
            }
        }

        this.getPlayerWW().sendMessageWithKey(Prefix.RED.getKey() , "werewolf.role.flute_player.full");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFinalDeath(FinalDeathEvent event) {

        if (event.getPlayerWW().equals(this.getPlayerWW())) {
            return;
        }

        this.checkStrength();
    }


    public String enchantedList() {
        StringBuilder sb = new StringBuilder();

        for (IPlayerWW playerWW : affectedPlayer) {
            if (playerWW.isState(StatePlayer.ALIVE)) {
                sb.append(playerWW.getName()).append(" ");
            }
        }
        return sb.toString();
    }

    @Override
    public void recoverPower() {

    }

    @Override
    public void recoverPotionEffect() {
        this.checkStrength();
    }

    @Override
    public Aura getDefaultAura() {
        return Aura.LIGHT;
    }

    private void checkStrength() {

        if (this.all) {
            this.getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.INCREASE_DAMAGE,"flute_player"));
            return;
        }

        if (this.affectedPlayer.stream().filter(playerWW -> playerWW.isState(StatePlayer.ALIVE)).count() + 1
                == game.getPlayerSize()) {
            this.all = true;
            Bukkit.getPluginManager().callEvent(new AllPlayerEnchantedEvent(this.getPlayerWW()));
        }
    }

    @Override
    public void setPower(boolean power) {
        this.power = power;
    }

    @Override
    public boolean hasPower() {
        return this.power;
    }

    public static ClickableItem config(WereWolfAPI game) {

        List<String> lore = Arrays.asList(game.translate("werewolf.menu.left"),
                game.translate("werewolf.menu.right"));
        IConfiguration config = game.getConfig();

        return ClickableItem.of((new ItemBuilder(
                UniversalMaterial.LIGHT_BLUE_WOOL.getStack())
                .setDisplayName(game.translate("werewolf.menu.advanced_tool.flute_player",
                                Formatter.format("&number&",config.getDistanceFlutePlayer())))
                .setLore(lore).build()), e -> {
            if (e.isLeftClick()) {
                config.setDistanceFlutePlayer((config.getDistanceFlutePlayer() + 2));
            } else if (config.getDistanceFlutePlayer() - 2 > 0) {
                config.setDistanceFlutePlayer(config.getDistanceFlutePlayer() - 2);
            }


            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setLore(lore)
                    .setDisplayName(game.translate("werewolf.menu.advanced_tool.flute_player",
                                    Formatter.format("&number&",config.getDistanceFlutePlayer())))
                    .build());

        });
    }
}
