package fr.ph1lou.werewolfplugin.roles.neutrals;


import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.Sound;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalEnchantment;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.flute_player.AllPlayerEnchantedEvent;
import fr.ph1lou.werewolfapi.events.roles.flute_player.EnchantedEvent;
import fr.ph1lou.werewolfapi.events.roles.flute_player.FindFluteEvent;
import fr.ph1lou.werewolfapi.events.roles.flute_player.GiveFluteEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleNeutral;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import fr.ph1lou.werewolfapi.versions.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
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
import fr.ph1lou.werewolfapi.enums.UniversalPotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@Role(key = RoleBase.FLUTE_PLAYER,
        category = Category.NEUTRAL,
        attribute = RoleAttribute.NEUTRAL,
        timers = @Timer(key = TimerBase.FLUTE_PLAYER_PROGRESS, defaultValue = 6, meetUpValue = 3, step = 1),
        configValues = @IntValue(key = IntValueBase.FLUTE_PLAYER_DISTANCE,
                defaultValue = 20, meetUpValue = 20, step = 4, item = UniversalMaterial.LIGHT_BLUE_WOOL))

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

    public FlutePlayer(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
        this.registerCustomCraft();
    }

    public void registerCustomCraft() {

        if (flute != null) {
            return;
        }

        flute = new ItemBuilder(Material.STICK)
                .addItemFlag(ItemFlag.HIDE_ENCHANTS)
                .setDisplayName(game.translate("werewolf.roles.flute_player.item"))
                .addEnchant(UniversalEnchantment.FLAME.getEnchantment(), 1).build();

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
                .setDescription(game.translate("werewolf.roles.flute_player.description",
                        Formatter.number(game.getConfig().getValue(IntValueBase.FLUTE_PLAYER_DISTANCE)
                        ), Formatter.timer(game, TimerBase.FLUTE_PLAYER_PROGRESS)))
                .setPower(game.translate("werewolf.roles.flute_player.power"))
                .setItems(game.translate("werewolf.roles.flute_player.craft_description"))
                .setEffects(game.translate("werewolf.roles.flute_player.effect"))
                .addExtraLines(game.translate("werewolf.roles.flute_player.affected",
                        Formatter.format("&list&", affectedPlayer.isEmpty() ? "" : enchantedList())))
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
            this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.flute_player.perform",
                    Formatter.number(game.getConfig().getValue(IntValueBase.FLUTE_PLAYER_DISTANCE)));
        } else {
            this.fluteInStore++;
            this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.flute_player.craft",
                    Formatter.number(this.fluteInStore));
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

        BukkitUtils.scheduleSyncDelayedTask(game, () -> event.getWhoClicked().closeInventory());

    }

    @EventHandler
    public void onFluteCraft(PrepareItemCraftEvent event) {

        if (!flute.equals(event.getInventory().getResult())) {
            return;
        }

        IPlayerWW playerWW = game.getPlayerWW(event.getView()
                .getPlayer().getUniqueId()).orElse(null);

        if (playerWW == null) {
            return;
        }

        if (!playerWW.getRole().isKey(RoleBase.FLUTE_PLAYER)) {
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

        this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.flute_player.day",
                Formatter.number(this.fluteInStore));
    }

    @Override
    public void second() {

        if (!this.hasOwnFlute) {
            return;
        }

        this.timer++;
        this.timer %= game.getConfig().getTimerValue(TimerBase.FLUTE_PLAYER_PROGRESS);

        if (this.timer != 0) {
            return;
        }

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        AtomicBoolean recoverResistance = new AtomicBoolean(false);

        Bukkit.getOnlinePlayers().stream()
                .filter(player1 -> !this.getPlayerUUID().equals(player1.getUniqueId()))
                .filter(player1 -> this.checkDistance(this.getPlayerWW().getLocation(), player1) || this.checkDistance(player1))
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
                                this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.flute_player.enchanted",
                                        Formatter.player(playerWW.getName()));
                                this.checkStrength();
                            }
                        }
                    }
                });

        if (recoverResistance.get()) {
            this.getPlayerWW()
                    .addPotionModifier(
                            PotionModifier
                                    .add(UniversalPotionEffectType.RESISTANCE,
                                            240,
                                            0,
                                            this.getKey()));
        } else {
            this.getPlayerWW()
                    .addPotionModifier(
                            PotionModifier
                                    .remove(UniversalPotionEffectType.RESISTANCE,
                                            this.getKey(),
                                            0));
        }
    }

    private boolean checkDistance(Player player1) {
        return this.flutedPlayer.stream()
                .anyMatch(player -> checkDistance(player.getLocation(), player1));
    }

    private boolean checkDistance(Location player, Player player1) {
        return player.getWorld() == player1.getWorld() &&
                player.distance(player1.getLocation())
                        < game.getConfig().getValue(IntValueBase.FLUTE_PLAYER_DISTANCE);
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
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.flute_player.find");
            Sound.ANVIL_BREAK.play(playerWW);
            this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.roles.flute_player.find_flute",
                    Formatter.player(playerWW.getName()));
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
            this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.flute_player.wait");
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
                this.getPlayerWW().sendMessageWithKey(Prefix.GREEN, "werewolf.roles.flute_player.transmit",
                        Formatter.player(clickedPlayerWW.getName()),
                        Formatter.number(this.fluteInStore));
                return;
            }
        }

        this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.roles.flute_player.full");
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
        if (this.all) {
            this.getPlayerWW().addPotionModifier(PotionModifier.add(UniversalPotionEffectType.STRENGTH, this.getKey()));
        }
    }

    private void checkStrength() {

        if (!this.all && this.affectedPlayer
                .stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE)).count() + 1
                == game.getPlayersCount()) {
            this.all = true;
            Bukkit.getPluginManager().callEvent(new AllPlayerEnchantedEvent(this.getPlayerWW()));
            this.recoverPotionEffect();
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

}
