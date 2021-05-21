package io.github.ph1lou.werewolfplugin.game;


import io.github.ph1lou.werewolfapi.ILover;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.MessageAction;
import io.github.ph1lou.werewolfapi.PotionAction;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.RolesBase;
import io.github.ph1lou.werewolfapi.enums.Sound;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.rolesattributs.IRole;
import io.github.ph1lou.werewolfapi.versions.VersionUtils;
import io.github.ph1lou.werewolfplugin.roles.villagers.Villager;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.enginehub.squirrelid.Profile;
import org.enginehub.squirrelid.resolver.HttpRepositoryService;
import org.enginehub.squirrelid.resolver.ProfileService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


public class PlayerWW implements IPlayerWW {

    private StatePlayer state = StatePlayer.ALIVE;
    private final List<ILover> lovers = new ArrayList<>();
    private final List<PotionAction> disconnectedPotionActions = new ArrayList<>();
    private final List<MessageAction> disconnectedMessages = new ArrayList<>();
    private final List<ItemStack> decoItems = new ArrayList<>();
    private int maxHealth = 20;
    private Location disconnectedLocation = null;
    private int disconnectedChangeHealth = 0;
    @Nullable
    private UUID mojangUUID = null;
    private final List<IPlayerWW> killer = new ArrayList<>();
    private final UUID uuid;
    private IRole role;
    private int disconnectedChangeMaxHealth = 0;
    private final List<ItemStack> itemsDeath = new ArrayList<>();
    private transient Location spawn;
    private int deathTime = 0;
    private int disconnectedTime = 0;
    private int kill = 0;
    private String name;
    private final WereWolfAPI game;


    public PlayerWW(WereWolfAPI api, Player player) {
        this.spawn = player.getWorld().getSpawnLocation();
        this.uuid = player.getUniqueId();
        this.name = player.getName();
        this.role = new Villager(api, this,
                RolesBase.VILLAGER.getKey());
        this.game = api;
        ProfileService resolver = HttpRepositoryService.forMinecraft();
        try {
            Profile profile = resolver.findByName(this.name); // May be null
            if (profile != null) {
                this.mojangUUID = profile.getUniqueId();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addPlayerHealth(int health) {

        Player player = Bukkit.getPlayer(uuid);

        if (player != null) {
            player.setHealth(Math.min(player.getHealth() + health, VersionUtils.getVersionUtils().getPlayerMaxHealth(player)));
            return;
        }

        disconnectedChangeHealth += health;
    }

    @Override
    public void removePlayerHealth(int health) {

        Player player = Bukkit.getPlayer(uuid);

        if (player != null) {
            player.setHealth(player.getHealth() - health);
            return;
        }

        disconnectedChangeHealth -= health;

    }

    @Override
    public void addPlayerMaxHealth(int health) {

        Player player = Bukkit.getPlayer(uuid);

        maxHealth += health;

        if (player != null) {
            VersionUtils.getVersionUtils().addPlayerMaxHealth(player, health);
            return;
        }

        disconnectedChangeMaxHealth += health;
    }

    @Override
    public void removePlayerMaxHealth(int health) {

        Player player = Bukkit.getPlayer(uuid);

        maxHealth -= health;

        if (player != null) {
            VersionUtils.getVersionUtils().removePlayerMaxHealth(player, health);

            if (player.getHealth() > VersionUtils.getVersionUtils().getPlayerMaxHealth(player)) {
                player.setHealth(VersionUtils.getVersionUtils().getPlayerMaxHealth(player));
            }
            return;
        }

        disconnectedChangeMaxHealth -= health;
    }

    @Override
    public void sendMessage(String message) {
        this.sendMessage(new TextComponent(message));
    }

    @Override
    public void sendMessageWithKey(String key, Object... args) {
        this.sendMessage(game.translate(key, args));
    }

    @Override
    public void sendMessageWithKey(String key, Sound sound, Object... args) {
        this.sendMessage(new TextComponent(game.translate(key, args)), sound);
    }

    @Override
    public void sendMessage(TextComponent textComponent) {
        this.sendMessage(textComponent, null);
    }

    @Override
    public void sendMessage(TextComponent textComponent, Sound sound) {

        Player player = Bukkit.getPlayer(uuid);

        if (player != null) {
            player.spigot().sendMessage(textComponent);
            if (sound != null) {
                sound.play(player);
            }
            return;
        }

        disconnectedMessages.add(new MessageAction(textComponent, sound));
    }

    @Override
    public void addPotionEffect(PotionEffectType potionEffectType, int duration, int amplifier) {

        if (potionEffectType.equals(PotionEffectType.INCREASE_DAMAGE)) {
            amplifier = -1;
        }

        PotionEffect potionEffect = new PotionEffect(potionEffectType,
                duration,
                amplifier,
                false,
                false);

        Player player = Bukkit.getPlayer(uuid);

        if (player != null) {
            player.addPotionEffect(potionEffect);
            return;
        }

        disconnectedPotionActions.add(new PotionAction(potionEffectType, duration, amplifier));

    }

    @Override
    public void addPotionEffect(PotionEffectType potionEffectType) {
        removePotionEffect(potionEffectType);
        addPotionEffect(potionEffectType, Integer.MAX_VALUE, 0);
    }

    @Override
    public void removePotionEffect(PotionEffectType potionEffectType) {

        Player player = Bukkit.getPlayer(uuid);

        if (player != null) {
            player.removePotionEffect(potionEffectType);
            return;
        }

        disconnectedPotionActions.add(new PotionAction(potionEffectType, false));
    }

    @Override
    public List<? extends PotionEffectType> getPotionEffects() {
        List<PotionEffectType> potionEffectTypes = disconnectedPotionActions.stream().filter(PotionAction::isAdd)
                .map(PotionAction::getPotionEffectType)
                .collect(Collectors.toList());

        Player player = Bukkit.getPlayer(this.uuid);
        if (player != null) {
            potionEffectTypes.addAll(player.getActivePotionEffects().stream().map(PotionEffect::getType).collect(Collectors.toSet()));
        }
        return potionEffectTypes;
    }

    @Override
    public void teleport(Location location) {

        Player player = Bukkit.getPlayer(uuid);

        if (player != null) {
            player.teleport(location);
            return;
        }

        disconnectedLocation = location.clone();

    }

    @Override
    public List<ItemStack> getItemDeath() {
        return this.itemsDeath;
    }

    @Override
    public void setItemDeath(ItemStack[] itemStacks) {
        itemsDeath.clear();
        itemsDeath.addAll(new ArrayList<>(Arrays.asList(itemStacks)));
    }

    @Override
    public void clearItemDeath() {
        this.itemsDeath.clear();
    }

    @Override
    public void setState(StatePlayer state) {
        this.state = state;
    }

	@Override
	public boolean isState(StatePlayer state) {
		return(this.state==state);
	}

    @Override
    public void addOneKill() {
        this.kill += 1;
    }

    @Override
    public int getNbKill() {
        return (this.kill);
    }

    @Override
    public UUID getMojangUUID() {

        if (this.mojangUUID != null) {
            return this.mojangUUID;
        }
        ProfileService resolver = HttpRepositoryService.forMinecraft();
        try {
            Profile profile = resolver.findByName(this.name); // May be null
            if (profile != null) {
                return this.mojangUUID = profile.getUniqueId();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return this.uuid;
    }

    @Override
    public IRole getRole() {
        return (this.role);
    }

    @Override
    public void setRole(IRole role) {
        this.role = role;
        this.role.setPlayerWW(this);
    }

    @Override
    public boolean isKey(String role) {
        return (role.equals(this.role.getKey()));
    }

    @Override
	public void setSpawn(Location spawn) {
		this.spawn=spawn;
	}

    @Override
    public Location getSpawn() {
        return (this.spawn);
    }

    @Override
    public List<ILover> getLovers() {
        return (this.lovers);
    }

    @Override
    public void addLover(ILover lover) {
        lovers.add(lover);
    }

    @Override
    public void removeLover(ILover lover) {
        lovers.remove(lover);
    }

    @Override
    public void addKiller(IPlayerWW killerUUID) {
        this.killer.add(killerUUID);
    }

    @Override
    public List<? extends IPlayerWW> getKillers() {
        return (this.killer);
    }

	@Override
	public void setDeathTime(Integer deathTime) {
		this.deathTime =deathTime;
	}

	@Override
	public int getDeathTime() {
		return(this.deathTime);
	}

    @Nullable
    @Override
    public Optional<IPlayerWW> getLastKiller() {
        return this.killer.isEmpty() ?
                Optional.empty() :
                this.killer.get(this.killer.size() - 1) == null ?
                        Optional.empty() :
                        Optional.of(this.killer.get(this.killer.size() - 1));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public @NotNull StatePlayer getState() {
        return state;
    }

    @Override
    public int getDisconnectedTime() {
        return disconnectedTime;
    }

    @Override
    public void setDisconnectedTime(int disconnectedTime) {
        this.disconnectedTime = disconnectedTime;
    }

    @Override
    public @NotNull UUID getUUID() {
        return uuid;
    }


    @Override
    public void updateAfterReconnect(Player player) {

        for (ItemStack item : decoItems) {
            addItem(item);
        }
        addPlayerMaxHealth(disconnectedChangeMaxHealth);
        addPlayerHealth(disconnectedChangeHealth);

        for (PotionAction potionAction : disconnectedPotionActions) {
            potionAction.executePotionAction(player);
        }
        for (MessageAction message : disconnectedMessages) {
            player.spigot().sendMessage(message.getMessage());
            if (message.getSound().isPresent()) {
                message.getSound().get().play(player);
            }
        }
        player.teleport(disconnectedLocation);

        disconnectedChangeHealth = 0;
        disconnectedChangeMaxHealth = 0;
        disconnectedPotionActions.clear();
        disconnectedMessages.clear();
        decoItems.clear();
    }

    @Override
    public void setDisconnectedLocation(Location location) {
        disconnectedLocation = location;
    }

    @Override
    public void addItem(ItemStack itemStack) {

        if (itemStack == null) return;

        Player player = Bukkit.getPlayer(uuid);

        if (player != null) {
            if (player.getInventory().firstEmpty() == -1) {
                player.getWorld().dropItem(player.getLocation(), itemStack);
            } else {
                player.getInventory().addItem(itemStack);
                player.updateInventory();
            }
            return;
        }

        decoItems.add(itemStack);
    }

    @Override
    public int getMaxHealth() {
        return maxHealth;
    }

    @Override
    public @NotNull Location getLocation() {

        Player player = Bukkit.getPlayer(uuid);

        if (player == null) return disconnectedLocation;

        return player.getLocation().clone();
    }


}

