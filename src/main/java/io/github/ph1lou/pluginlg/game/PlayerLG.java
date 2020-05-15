package io.github.ph1lou.pluginlg.game;

import io.github.ph1lou.pluginlg.classesroles.PlayerLGI;
import io.github.ph1lou.pluginlg.classesroles.RolesImpl;
import io.github.ph1lou.pluginlg.classesroles.neutralroles.SerialKiller;
import io.github.ph1lou.pluginlg.classesroles.neutralroles.Thief;
import io.github.ph1lou.pluginlg.classesroles.villageroles.Elder;
import io.github.ph1lou.pluginlg.classesroles.villageroles.Villager;
import io.github.ph1lou.pluginlg.events.DayEvent;
import io.github.ph1lou.pluginlg.events.NightEvent;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


public class PlayerLG implements Listener, PlayerLGI, PlayerWW {

	private State state = State.ALIVE;
	private RolesImpl role ;
	private Boolean canBeInfect = false;
	private Boolean damn = false;
	private Boolean salvation = false;
	private final List<UUID> lovers = new ArrayList<>();
	private Boolean kit = false;
	private Boolean announceCursedLoversAFK = false;
	private Boolean announceLoversAFK = false;
	private Boolean thief = false;
	private transient Scoreboard board= Bukkit.getScoreboardManager().getNewScoreboard();
	private Boolean revealAmnesiacLover=false;
	private final List<ItemStack> itemsDeath = new ArrayList<>();
	private UUID cursedLovers=null;
	private UUID amnesiacLoverUUID=null;
	private transient Location spawn;
	private final transient GameManager game;
	private int deathTime = 0;
	private int vote = 0;
	private int lostHeart = 0;
	private int kill = 0;
	private final List<UUID> killer = new ArrayList<>();
	private UUID playerVote = null;
	private String name;
	private final UUID playerUUID;
	private Boolean infected = false;


	public PlayerLG(Player player, GameManager game) {
		this.spawn = player.getWorld().getSpawnLocation();
		this.playerUUID=player.getUniqueId();
		this.game=game;
		this.role=new Villager(game,this.playerUUID);
		this.name=player.getName();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onDay(DayEvent event) {

		if (!isState(State.ALIVE)) return;

		if(!event.getUuid().equals(game.getGameUUID())){
			return;
		}

		if(Bukkit.getPlayer(playerUUID)==null){
			return;
		}

		Player player = Bukkit.getPlayer(playerUUID);

		if (getLostHeart() > 0) {
			player.setMaxHealth(player.getMaxHealth() + getLostHeart());
			clearLostHeart();
		}

		if (hasDamn()) {
			setDamn(false);
			player.removePotionEffect(PotionEffectType.JUMP);
			player.sendMessage(game.translate("werewolf.role.raven.no_longer_curse"));
		}
		if (hasSalvation()) {
			setSalvation(false);
			if (!(getRole() instanceof Thief || (getRole() instanceof Elder) && ((Elder) getRole()).hasPower())) {
				player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
			}
			player.sendMessage(game.translate("werewolf.role.protector.no_longer_protected"));
		}
		if (getInfected() && !(getRole() instanceof SerialKiller && ((SerialKiller) getRole()).hasPower())) {
			player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
		}

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInfectedNight(NightEvent event) {

		if(!event.getUuid().equals(game.getGameUUID())){
			return;
		}

		if(!isState(State.ALIVE)){
			return;
		}

		if (!getInfected()) {
			return;
		}

		if(Bukkit.getPlayer(playerUUID)==null){
			return;
		}
		Player player = Bukkit.getPlayer(playerUUID);

		player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, -1, false, false));
	}

	public Scoreboard getScoreBoard() {
		return this.board;
	}

	public void setScoreBoard(Scoreboard board){
		this.board=board;
	}

	@Override
	public void setItemDeath(ItemStack[] itemsDeath) {
		this.itemsDeath.addAll(Arrays.asList(itemsDeath));
	}

	@Override
	public List<ItemStack> getItemDeath() {
		return this.itemsDeath;
	}

	@Override
	public void clearItemDeath() {
		this.itemsDeath.clear();
	}

	@Override
	public void setState(State state) {
		this.state=state;
	}

	@Override
	public boolean isState(State state) {
		return(this.state==state);
	}

	@Override
	public void addOneKill() {
		this.kill +=1;
	}

	@Override
	public int getNbKill() {
		return(this.kill);
	}

	@Override
	public void addKLostHeart(int k) {
		this.lostHeart +=k;
	}

	@Override
	public int getLostHeart() {
		return(this.lostHeart);
	}

	@Override
	public void setVote(UUID vote) {
		this.playerVote =vote;
	}

	@Override
	public UUID getVotedPlayer() {
		return(this.playerVote);
	}

	@Override
	public void setKit(Boolean kit) {
		this.kit=kit;
	}

	@Override
	public Boolean hasKit() {		
		return(this.kit);
	}

	@Override
	public void incVote() {
		this.vote+=1;
	}

	@Override
	public void resetVote() {
		this.vote=0;
	}

	@Override
	public int getVote() {
		return(this.vote);
	}

	@Override
	public void setRole(RolesImpl role) {
		this.role = role;
	}

	@Override
	public RolesImpl getRole() {
		return (this.role);
	}

	@Override
	public Boolean isRole(RolesImpl role) {
		return (this.role.equals(role));
	}

	@Override
	public void setSpawn(Location spawn) {
		this.spawn=spawn;
	}

	@Override
	public Location getSpawn() {
		return(this.spawn);
	}

	@Override
	public void setDamn(Boolean damn) {
		this.damn = damn;
	}

	@Override
	public Boolean hasDamn() {
		return(this.damn);
	}

	@Override
	public void setSalvation(Boolean salvation) {
		this.salvation = salvation;
	}

	@Override
	public Boolean hasSalvation() {
		return(this.salvation);
	}

	@Override
	public void addLover(UUID uuid) {
		this.lovers.add(uuid);
	}

	@Override
	public void clearLovers() {
		this.lovers.clear();
	}

	@Override
	public List<UUID> getLovers() {
		return (this.lovers);
	}

	@Override
	public void addKiller(UUID killerUUID) {
		this.killer.add(killerUUID);
	}

	@Override
	public List<UUID> getKillers() {
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

	@Override
	public void setCanBeInfect(Boolean b) {
		this.canBeInfect =b;
	}

	@Override
	public Boolean canBeInfect() {
		return(this.canBeInfect);
	}

	@Override
	public void clearLostHeart() {
		this.lostHeart = 0;
	}

	@Override
	public void removeLover(UUID uuid) {
		this.lovers.remove(uuid);
	}

	@Override
	public UUID getCursedLovers() {
		return cursedLovers;
	}

	@Override
	public void setCursedLover(UUID uuid) {
		this.cursedLovers = uuid;
	}

	@Override
	public Boolean getAnnounceCursedLoversAFK() {
		return announceCursedLoversAFK;
	}

	@Override
	public void setAnnounceCursedLoversAFK(Boolean announceCursedLoversAFK) {
		this.announceCursedLoversAFK = announceCursedLoversAFK;
	}

	@Override
	public Boolean getAnnounceLoversAFK() {
		return announceLoversAFK;
	}

	@Override
	public void setAnnounceLoversAFK(Boolean announceLoversAFK) {
		this.announceLoversAFK = announceLoversAFK;
	}

	@Override
    public UUID getLastKiller() {
		return this.killer.isEmpty()?null:this.killer.get(this.killer.size()-1);
    }

	@Override
	public void setThief(Boolean thief) {
		this.thief = thief;
	}

	@Override
	public Boolean isThief() {
		return(this.thief);
	}

	@Override
	public UUID getAmnesiacLoverUUID() {
		return amnesiacLoverUUID;
	}

	@Override
	public void setAmnesiacLoverUUID(UUID uuid) {
		this.amnesiacLoverUUID = uuid;
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
	public Boolean getRevealAmnesiacLover() {
		return revealAmnesiacLover;
	}

	@Override
	public void setRevealAmnesiacLover(Boolean revealAmnesiacLover) {
		this.revealAmnesiacLover = revealAmnesiacLover;
	}

	@Override
	public Boolean getInfected() {
		return infected;
	}

	@Override
	public void setInfected(Boolean infected) {
		this.infected = infected;
	}
}

