package fr.ph1lou.werewolfplugin.roles.neutrals;


import fr.ph1lou.werewolfapi.annotations.Configuration;
import fr.ph1lou.werewolfapi.annotations.ConfigurationBasic;
import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.RequireAbility;
import fr.ph1lou.werewolfapi.annotations.RequireAlive;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.Sound;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalEnchantment;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.enums.UniversalPotionEffectType;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.flute_player.AllPlayerEnchantedEvent;
import fr.ph1lou.werewolfapi.events.roles.flute_player.EnchantedEvent;
import fr.ph1lou.werewolfapi.events.roles.flute_player.GiveFluteEvent;
import fr.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import fr.ph1lou.werewolfapi.events.UpdatePlayerNameTagEvent;
import fr.ph1lou.werewolfapi.events.werewolf.RequestStrengthRateEvent;
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
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Arrays;
import java.util.Objects;

@Role(key = RoleBase.FLUTE_PLAYER,
        category = Category.NEUTRAL,
        attribute = RoleAttribute.NEUTRAL,
        defaultAura = Aura.LIGHT,
        sharpnessDiamondModifier = 1,
        sharpnessIronModifier = 1,
        timers = @Timer(key = TimerBase.FLUTE_PLAYER_PROGRESS, defaultValue = 6, meetUpValue = 3, step = 1),
        configValues = @IntValue(key = IntValueBase.FLUTE_PLAYER_DISTANCE,
                defaultValue = 20, meetUpValue = 20, step = 4, item = UniversalMaterial.LIGHT_BLUE_WOOL),
        configurations = {@Configuration(config = @ConfigurationBasic(key = "werewolf.roles.flute_player.configurations.flute_player_every_other_day", defaultValue = true))})

public class FlutePlayer extends RoleNeutral implements IPower, IAffectedPlayers {

    private static ItemStack flute;
    private final List<IPlayerWW> flutedPlayer = new ArrayList<>();
    private final Map<IPlayerWW, Integer> progress = new HashMap<>();
    private final List<IPlayerWW> affectedPlayer = new ArrayList<>();
    private final Set<IPlayerWW> hitPlayers = new HashSet<>();
    private final Set<IPlayerWW> pendingNotify = new HashSet<>();
    private boolean power = true;
    private boolean hasOwnFlute = false;
    private int timer = 0;
    private int flutedCycle = 0;
    private int fluteInStore = 0;
    private boolean bonus3Heart = false;
    private boolean bonus12Heart = false;
    private boolean all = false;
    private int dayNumber = -8;

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

        if (!flute.equals(event.getCurrentItem())) {
            return;
        }

        if (!event.getWhoClicked().getUniqueId().equals(this.getPlayerUUID())) {
            IPlayerWW clickerWW = game.getPlayerWW(event.getWhoClicked().getUniqueId()).orElse(null);
            if (clickerWW == null || !clickerWW.getRole().isKey(RoleBase.FLUTE_PLAYER)) {
                event.setCancelled(true);
            }
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
    @RequireAlive
    public void onDay(DayEvent event) {

        if (this.hasOwnFlute) {
            if (!(game.getConfig().isConfigActive("werewolf.roles.flute_player.configurations.flute_player_every_other_day")
                    && event.getNumber() == dayNumber + 1)) {
                dayNumber = event.getNumber();
                this.power = true;
                this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.flute_player.day",
                        Formatter.number(this.fluteInStore));
            }
        }

        new ArrayList<>(pendingNotify).forEach(playerWW -> {
            Player player = Bukkit.getPlayer(playerWW.getUUID());
            if (player != null) {
                PlayerInventory inventory = player.getInventory();
                for (int j = 9; j < 40; j++) {
                    if (inventory.getItem(j) == null) {
                        inventory.setItem(j, flute);
                        break;
                    }
                }
            }
            this.flutedPlayer.add(playerWW);
            playerWW.sendMessageWithKey(Prefix.ORANGE, "werewolf.roles.flute_player.fluted_notify");
        });
        pendingNotify.clear();
    }

    @Override
    public void second() {

        if (!this.isAbilityEnabled()) return;
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

        this.flutedCycle++;
        boolean flutedTick = (this.flutedCycle % 3 == 0);

        game.getAlivePlayersWW()
                .stream()
                .filter(player1 -> !this.getPlayerWW().equals(player1))
                .filter(player1 -> !this.flutedPlayer.contains(player1))
                .filter(player1 -> !this.affectedPlayer.contains(player1))
                .forEach(playerWW -> {
                    boolean nearFlutePlayer = this.checkDistance(this.getPlayerWW(), playerWW);
                    boolean nearFluted = !nearFlutePlayer && this.checkDistance(playerWW);

                    if (nearFlutePlayer) {
                        this.progress.merge(playerWW, 1, Integer::sum);
                    } else if (nearFluted && flutedTick) {
                        this.progress.merge(playerWW, 1, Integer::sum);
                    } else {
                        return;
                    }

                    this.tryEnchant(playerWW);
                });
        this.refreshTab();
    }

    @EventHandler
    @RequireAbility
    @RequireAlive
    public void onHit(EntityDamageByEntityEvent event) {

        if (!this.hasOwnFlute) return;

        if (!(event.getDamager() instanceof Player)) return;
        if (!event.getDamager().getUniqueId().equals(this.getPlayerUUID())) return;

        if (!(event.getEntity() instanceof Player)) return;
        Player victim = (Player) event.getEntity();
        IPlayerWW victimWW = game.getPlayerWW(victim.getUniqueId()).orElse(null);
        if (victimWW == null) return;
        if (!victimWW.isState(StatePlayer.ALIVE)) return;
        if (this.affectedPlayer.contains(victimWW)) return;
        if (this.hitPlayers.contains(victimWW)) return;

        this.hitPlayers.add(victimWW);
        this.progress.merge(victimWW, 10, Integer::sum);

        this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW,
                "werewolf.roles.flute_player.hit_progress",
                Formatter.player(victimWW.getName()));

        this.tryEnchant(victimWW);
        this.refreshTab();
    }

    private void tryEnchant(IPlayerWW target) {

        if (this.progress.getOrDefault(target, 0) <= 100) return;

        EnchantedEvent event = new EnchantedEvent(this.getPlayerWW(), target);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) return;

        this.affectedPlayer.add(target);
        this.progress.remove(target);
        this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.flute_player.enchanted",
                Formatter.player(target.getName()));
        this.applyTierEffects();
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onUpdatePlayerNameTag(UpdatePlayerNameTagEvent event) {
        if (!this.hasOwnFlute) return;
        if (!event.getTargetUUID().equals(this.getPlayerUUID())) return;
        IPlayerWW viewed = game.getPlayerWW(event.getPlayerUUID()).orElse(null);
        if (viewed == null || viewed.equals(this.getPlayerWW())) return;
        if (!viewed.isState(StatePlayer.ALIVE)) return;

        String add;
        if (this.affectedPlayer.contains(viewed)) {
            add = " §d♥";
        } else {
            int pct = Math.min(100, this.progress.getOrDefault(viewed, 0));
            if (pct <= 0) return;
            String color = pct >= 100 ? "§c" : pct >= 50 ? "§e" : "§a";
            add = " " + color + pct + "%";
        }
        event.setSuffix(event.getSuffix() + add);
    }

    private void refreshTab() {
        Set<IPlayerWW> targets = new HashSet<>(this.progress.keySet());
        targets.addAll(this.affectedPlayer);
        targets.forEach(p -> {
            if (p.isState(StatePlayer.ALIVE)) {
                Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(p));
            }
        });
    }

    private void applyTierEffects() {

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;

        long count = this.affectedPlayer.stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .count();

        if (count >= 3 && !this.bonus3Heart) {
            this.bonus3Heart = true;
            this.getPlayerWW().addPlayerMaxHealth(4);
        }
        if (count >= 12 && !this.bonus12Heart) {
            this.bonus12Heart = true;
            this.getPlayerWW().addPlayerMaxHealth(2);
        }

        if (count >= 6) {
            this.getPlayerWW().addPotionModifier(PotionModifier.add(UniversalPotionEffectType.STRENGTH, this.getKey()));
        } else {
            this.getPlayerWW().addPotionModifier(PotionModifier.remove(UniversalPotionEffectType.STRENGTH, this.getKey(), 0));
        }

        if (count >= 9) {
            this.getPlayerWW().addPotionModifier(PotionModifier.add(UniversalPotionEffectType.SPEED, this.getKey()));
        } else {
            this.getPlayerWW().addPotionModifier(PotionModifier.remove(UniversalPotionEffectType.SPEED, this.getKey(), 0));
        }

        boolean allCharmed = count >= 6 && count + 1 >= game.getPlayersCount();
        if (allCharmed) {
            this.getPlayerWW().addPotionModifier(PotionModifier.add(UniversalPotionEffectType.RESISTANCE, this.getKey()));
            if (!this.all) {
                this.all = true;
                Bukkit.getPluginManager().callEvent(new AllPlayerEnchantedEvent(this.getPlayerWW()));
            }
        } else {
            this.getPlayerWW().addPotionModifier(PotionModifier.remove(UniversalPotionEffectType.RESISTANCE, this.getKey(), 0));
            this.all = false;
        }
    }

    @EventHandler
    @RequireAbility
    @RequireAlive
    public void onRequestStrengthRate(RequestStrengthRateEvent event) {

        if (!event.getPlayerWW().equals(this.getPlayerWW())) return;

        long count = this.affectedPlayer.stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .count();

        if (count >= 9) {
            return;
        }
        if (count >= 6) {
            event.setStrengthRate(event.getStrengthRate() / 2);
        }
    }

    private boolean checkDistance(IPlayerWW player1) {
        return this.flutedPlayer.stream()
                .anyMatch(player -> checkDistance(player, player1));
    }

    private boolean checkDistance(IPlayerWW player, IPlayerWW player1) {
        return player.distance(player1)
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

        IPlayerWW clicker = this.flutedPlayer.stream()
                .filter(playerWW1 -> playerWW1.getUUID().equals(event.getWhoClicked().getUniqueId()))
                .findFirst().orElse(null);

        if (clicker != null) {
            event.setCancelled(true);
            BukkitUtils.scheduleSyncDelayedTask(game, () -> event.getWhoClicked().closeInventory());
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {

        if (!this.hasOwnFlute) {
            return;
        }

        if (!flute.equals(event.getItemDrop().getItemStack())) {
            return;
        }

        if (this.flutedPlayer.stream().anyMatch(playerWW -> playerWW.getUUID().equals(event.getPlayer().getUniqueId()))) {
            event.setCancelled(true);
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

        if (this.flutedPlayer.contains(clickedPlayerWW) || this.pendingNotify.contains(clickedPlayerWW)) {
            return;
        }

        if (!this.power) {
            this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.flute_player.wait");
            return;
        }

        Bukkit.getPluginManager().callEvent(new GiveFluteEvent(clickedPlayerWW, this.getPlayerWW()));
        this.pendingNotify.add(clickedPlayerWW);
        this.power = false;
        this.fluteInStore--;
        this.getPlayerWW().sendMessageWithKey(Prefix.GREEN, "werewolf.roles.flute_player.transmit",
                Formatter.player(clickedPlayerWW.getName()),
                Formatter.number(this.fluteInStore));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFinalDeath(FinalDeathEvent event) {

        if (event.getPlayerWW().equals(this.getPlayerWW())) {
            return;
        }

        IPlayerWW dead = event.getPlayerWW();
        this.flutedPlayer.remove(dead);
        this.pendingNotify.remove(dead);
        this.hitPlayers.remove(dead);
        this.progress.remove(dead);

        this.applyTierEffects();
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
        this.applyTierEffects();
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
