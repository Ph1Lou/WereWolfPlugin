package fr.ph1lou.werewolfplugin.game;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.lovers.ILover;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfplugin.utils.MessageAction;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.enums.RolesBase;
import fr.ph1lou.werewolfapi.enums.Sound;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfapi.versions.VersionUtils;
import fr.ph1lou.werewolfplugin.roles.villagers.Villager;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;


public class PlayerWW implements IPlayerWW {

    private StatePlayer state = StatePlayer.ALIVE;
    private final List<ILover> lovers = new ArrayList<>();
    private final Map<PotionModifier,Integer> potionModifiers = new HashMap<>();
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
    private boolean tpWhenDisconnected = false;
    private String name;
    private final GameManager game;
    private final List<IPlayerWW> playersKilled = new ArrayList<>();
    private final List<IPlayerWW> lastMinutesDamagedPlayer = new ArrayList<>();
    @Nullable private String lastWish;
    @Nullable
    private Location deathLocation;
    private final List<String> deathRoles = new ArrayList<>();

    public PlayerWW(GameManager api, Player player) {
        this.spawn = player.getWorld().getSpawnLocation();
        this.uuid = player.getUniqueId();
        this.name = player.getName();
        this.role = new Villager(api, this,
                RolesBase.VILLAGER.getKey());
        this.game = api;
        try {
            this.mojangUUID = getUUID(name);
        } catch (Exception ignored) {
            this.game.setCrack();
        }
    }

    private static UUID getUUID(String name) throws IOException {
        String uuid;
        BufferedReader in = new BufferedReader(new InputStreamReader(new URL("https://api.mojang.com/users/profiles/minecraft/" + name).openStream()));
        uuid = (((JsonObject)new JsonParser().parse(in)).get("id")).toString().replaceAll("\"", "");
        uuid = uuid.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5");
        in.close();
        return UUID.fromString(uuid);
    }

    @Override
    public void addPlayerHealth(int health) {

        Player player = Bukkit.getPlayer(this.uuid);

        if (player != null) {
            player.setHealth(Math.min(player.getHealth() + health, VersionUtils.getVersionUtils().getPlayerMaxHealth(player)));
            return;
        }

        this.disconnectedChangeHealth += health;
    }

    @Override
    public void removePlayerHealth(int health) {

        Player player = Bukkit.getPlayer(this.uuid);

        if (player != null) {
            player.setHealth(player.getHealth() - health);
            return;
        }

        this.disconnectedChangeHealth -= health;

    }

    @Override
    public void addPlayerMaxHealth(int health) {

        Player player = Bukkit.getPlayer(uuid);

        this.maxHealth += health;

        if (player != null) {
            VersionUtils.getVersionUtils().addPlayerMaxHealth(player, health);
            return;
        }

        this.disconnectedChangeMaxHealth += health;
    }

    @Override
    public void removePlayerMaxHealth(int health) {

        Player player = Bukkit.getPlayer(uuid);

        this.maxHealth -= health;

        if (player != null) {
            VersionUtils.getVersionUtils().removePlayerMaxHealth(player, health);

            if (player.getHealth() > VersionUtils.getVersionUtils().getPlayerMaxHealth(player)) {
                player.setHealth(VersionUtils.getVersionUtils().getPlayerMaxHealth(player));
            }
            return;
        }

        this.disconnectedChangeMaxHealth -= health;
    }

    @Override
    public void sendMessageWithKey(@NotNull String key, Formatter... formatters) {
        this.sendMessageWithKey("", key, formatters);
    }

    @Override
    public void sendMessageWithKey(@NotNull String prefixKey,@NotNull String key, Formatter... formatters) {
        String message = this.game.translate(prefixKey,key,formatters);

        Player player = Bukkit.getPlayer(this.uuid);

        if (player != null) {
            player.sendMessage(message);
            return;
        }

        this.disconnectedMessages.add(new MessageAction(message));
    }

    @Override
    public void sendMessage(@NotNull TextComponent textComponent) {
        Player player = Bukkit.getPlayer(uuid);

        if (player != null) {
            player.spigot().sendMessage(textComponent);
            return;
        }

        this.disconnectedMessages.add(new MessageAction(textComponent));
    }

    @Override
    public void sendSound(@NotNull Sound sound) {
        Player player = Bukkit.getPlayer(uuid);
        if(player != null){
            sound.play(player);
        }
    }

    @Override
    public void addPotionModifier(PotionModifier potionModifier) {

        Player player = Bukkit.getPlayer(this.uuid);

        if(!potionModifier.isAdd()){
            new ArrayList<>(this.potionModifiers.keySet())
                    .stream()
                    .filter(potionModifier1 -> potionModifier1.getIdentifier()
                            .equals(potionModifier.getIdentifier()))
                    .filter(potionModifier1 -> potionModifier1.getPotionEffectType()
                            .equals(potionModifier.getPotionEffectType()))
                    .filter(potionModifier1 -> potionModifier1.getAmplifier() == potionModifier.getAmplifier())
                    .forEach(potionModifier1 -> {
                        int id = this.potionModifiers.remove(potionModifier1);

                        if(id != -1){
                            Bukkit.getScheduler().cancelTask(id);
                        }

                        if(player!=null){
                            player.removePotionEffect(potionModifier1.getPotionEffectType());

                            int maxAmplifier = this.potionModifiers.keySet().stream()
                                    .filter(potionModifier2 -> potionModifier2.getPotionEffectType().equals(potionModifier1.getPotionEffectType()))
                                    .max(Comparator.comparing(PotionModifier::getAmplifier))
                                    .map(PotionModifier::getAmplifier)
                                    .orElse(-1);

                            this.potionModifiers.keySet().stream()
                                    .filter(potionModifier2 -> potionModifier2.getPotionEffectType().equals(potionModifier1.getPotionEffectType()))
                                    .filter(potionModifier2 -> potionModifier2.getAmplifier() == maxAmplifier)
                                    .max(Comparator.comparing(potionModifier2 -> 20 * (potionModifier2.getTimer() - game.getTimer()) + potionModifier2.getDuration()))
                                    .ifPresent(potionModifier2 -> player.addPotionEffect(
                                            new PotionEffect(
                                                    potionModifier2.getPotionEffectType(),
                                                    20 * (potionModifier2.getTimer() - game.getTimer()) + potionModifier2.getDuration(),
                                                    potionModifier2.getAmplifier(),
                                                    false,
                                                    false)));
                        }
                    });

            return;
        }
        AtomicBoolean find = new AtomicBoolean(false);
        new ArrayList<>(this.potionModifiers.keySet())
                .stream()
                .filter(potionModifier1 -> potionModifier1.getPotionEffectType()
                        .equals(potionModifier.getPotionEffectType()))
                .forEach(potionModifier1 -> {
                    if((20 * (potionModifier1.getTimer() - game.getTimer()) +
                            potionModifier1.getDuration()  < potionModifier.getDuration() &&
                    potionModifier1.getAmplifier() == potionModifier.getAmplifier()) ||
                            potionModifier1.getAmplifier() < potionModifier.getAmplifier()){

                        if(potionModifier1.getIdentifier().equals(potionModifier.getIdentifier()) &&
                                potionModifier1.getAmplifier() == potionModifier.getAmplifier()){
                            int id = this.potionModifiers.remove(potionModifier1);
                            if(id != -1){
                                Bukkit.getScheduler().cancelTask(id);
                            }
                        }
                        if(player!=null){
                            player.removePotionEffect(potionModifier.getPotionEffectType());
                        }
                    }
                    else if(potionModifier1.getIdentifier().equals(potionModifier.getIdentifier())){
                        find.set(true);
                    }
                });

        if(find.get()){
            return;
        }

        if(potionModifier.getDuration()<1000000000){
            potionModifier.setTimer(game.getTimer());
            this.potionModifiers.put(potionModifier,BukkitUtils.scheduleSyncDelayedTask(() -> {
                if(!this.game.isState(StateGame.END)){
                    this.addPotionModifier(PotionModifier.remove(potionModifier.getPotionEffectType(),
                            potionModifier.getIdentifier(),
                            potionModifier.getAmplifier()));
                }
            },potionModifier.getDuration()));
        }
        else {
            this.potionModifiers.put(potionModifier,-1);
        }

        if(player!=null){
            player.addPotionEffect(new PotionEffect(potionModifier.getPotionEffectType(),
                    potionModifier.getDuration(),
                    potionModifier.getAmplifier(),
                    false,
                    false));
        }

    }

    @Override
    public void clearPotionEffects() {
        this.potionModifiers.clear();

        Player player = Bukkit.getPlayer(this.uuid);

        if(player!=null){
            player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        }
    }

    @Override
    public Set<? extends PotionModifier> getPotionModifiers() {
        return this.potionModifiers.keySet();
    }

    @Override
    public void teleport(Location location) {

        Player player = Bukkit.getPlayer(uuid);

        if (player != null) {
            player.teleport(location);
            return;
        }
        this.tpWhenDisconnected = true;
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
    public void addOneKill(IPlayerWW playerWW) {
        this.playersKilled.add(playerWW);
    }

    @Override
    public List<? extends IPlayerWW> getPlayersKills() {
        return this.playersKilled;
    }

    @Override
    public Optional<UUID> getMojangUUID() {
        return Optional.ofNullable(this.mojangUUID);
    }

    @Override
    public UUID getReviewUUID(){
        return this.getMojangUUID().isPresent() ? this.getMojangUUID().get() : this.getUUID();
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
        this.lovers.add(lover);
    }

    @Override
    public void removeLover(ILover lover) {
        this.lovers.remove(lover);
    }

    @Override
    public void clearLover() {
        this.lovers.clear();
    }

    @Override
    public void addKiller(IPlayerWW killerUUID) {
        this.killer.add(killerUUID);
    }

    @Override
    public List<? extends IPlayerWW> getKillers() {
        return (this.killer);
    }

	public void setDeathTime(int deathTime) {
		this.deathTime =deathTime;
	}

	@Override
	public int getDeathTime() {
		return this.deathTime;
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
    public List<IPlayerWW> getLastMinutesDamagedPlayer() {
        return this.lastMinutesDamagedPlayer;
    }

    public void addLastMinutesDamagedPlayer(IPlayerWW playerWW){
        this.lastMinutesDamagedPlayer.add(playerWW);
    }

    public void removeLastMinutesDamagedPlayer(IPlayerWW playerWW){
        this.lastMinutesDamagedPlayer.remove(playerWW);
    }

    @Override
    public String getName() {
        return name;
    }

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

    public void setDisconnectedTime(int disconnectedTime) {
        this.disconnectedTime = disconnectedTime;
    }

    @Override
    public @NotNull UUID getUUID() {
        return uuid;
    }

    public void updateAfterReconnect(Player player) {

        this.decoItems.forEach(this::addItem);

        addPlayerMaxHealth(this.disconnectedChangeMaxHealth);
        addPlayerHealth(this.disconnectedChangeHealth);

        this.updatePotionEffects(player);

        this.disconnectedMessages.forEach(messageAction -> {
            if(messageAction.isMessageComponent()){
                player.spigot().sendMessage(messageAction.getMessageComponent());
            }
            else{
                player.sendMessage(messageAction.getMessageString());
            }

        });
        if(this.tpWhenDisconnected){
            this.tpWhenDisconnected=false;
            this.addPotionModifier(PotionModifier.add(PotionEffectType.WITHER,400,0,"no_fall"));
        }
        player.teleport(this.disconnectedLocation);

        this.disconnectedChangeHealth = 0;
        this.disconnectedChangeMaxHealth = 0;
        this.disconnectedMessages.clear();
        this.decoItems.clear();
    }

    public void updatePotionEffects(Player player) {
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));

        this.potionModifiers.keySet()
                .stream()
                .filter(PotionModifier::isAdd)
                .forEach(potionModifier -> {
                    int duration = potionModifier.getDuration();
                    if(duration<1000000000){
                        duration-=(this.game.getTimer()-potionModifier.getTimer())*20;
                    }
                    player.addPotionEffect(
                            new PotionEffect(
                                    potionModifier.getPotionEffectType(),
                                    duration,
                                    potionModifier.getAmplifier(),
                                    false,
                                    false));
                });
    }

    public void setDisconnectedLocation(Location location) {
        this.disconnectedLocation = location;
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

    @Override
    public Optional<String> getWish() {
        return Optional.ofNullable(lastWish);
    }

    @Override
    public void setWish(String wish) {
        this.lastWish = wish;
    }

    @Override
    public Location getDeathLocation() {
        if(this.deathLocation!=null){
            return this.deathLocation;
        }
        return this.getLocation();
    }

    @Override
    public void setDeathLocation(@Nullable Location location) {
        this.deathLocation=location;
    }

    @Override
    public List<? extends String> getDeathRoles() {
        List<String> deathRolesTemp = new ArrayList<>(this.deathRoles);
        deathRolesTemp.add(this.getRole().getKey());
        return deathRolesTemp;
    }

    @Override
    public void addDeathRole(String role) {
        this.deathRoles.add(role);
    }

    @Override
    public String getDeathRole() {
        if(this.deathRoles.isEmpty()){
            return this.role.getKey();
        }
        return this.deathRoles.get(0);
    }


}

