package io.github.ph1lou.pluginlg;

import io.github.ph1lou.pluginlg.enumlg.Camp;
import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import io.github.ph1lou.pluginlg.enumlg.State;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PlayerLG{

	private State state = State.LIVING;
	private Camp camp = Camp.VILLAGE;
	private Camp posterCamp = Camp.LG;
	private RoleLG role=RoleLG.VILLAGEOIS;
	private RoleLG posterRole =RoleLG.LOUP_FEUTRE;
	private Boolean power=true;
	private Boolean canBeInfect =false;
	private Boolean damn =false;
	private Boolean salvation =false;
	private Boolean hasBeenStolen =false;
	private Boolean thief =false;
	private Boolean kit=false;
	private transient Scoreboard board;
	private final List<String> affectedPlayer = new ArrayList<>();
	private final List<String> disciple = new ArrayList<>();
	private final List<String> targetOf = new ArrayList<>();
	private final List<String> couple = new ArrayList<>();
	private final List<ItemStack> itemsDeath = new ArrayList<>();
	private transient Location spawn;
	private int deathTime =0;
	private int use=0;
	private int vote=0;
	private int diamondLimit =0;
	private int lostHeart =0;
	private int kill =0;
	private float flair = 0;
	private String killer="";
	private String playerVote ="";

	public PlayerLG(Player player) {
		board = Bukkit.getScoreboardManager().getNewScoreboard();
		clearPlayer(player);
		this.spawn=player.getWorld().getSpawnLocation();
	}

	public void clearPlayer(Player player){
		player.setMaxHealth(20);
		player.setHealth(20);
		player.setExp(0);
		player.setLevel(0);
		player.getInventory().clear();
		player.getInventory().clear();
		player.getInventory().setHelmet(null);
		player.getInventory().setChestplate(null);
		player.getInventory().setLeggings(null);
		player.getInventory().setBoots(null);
		for(PotionEffect po:player.getActivePotionEffects()) {
			player.removePotionEffect(po.getType());
		}
	}

	public Scoreboard getScoreBoard(){
		return this.board;
	}

	public void setScoreBoard(Scoreboard board){
		this.board=board;
	}

	public void setItemDeath(ItemStack[] itemsDeath) {
		this.itemsDeath.addAll(Arrays.asList(itemsDeath));
	}

	public List<ItemStack> getItemDeath() {
		return this.itemsDeath;
	}

	public void addItemDeath(ItemStack itemsDeath) {
		this.itemsDeath.add(itemsDeath);
	}

	public void clearItemDeath() {
		this.itemsDeath.clear();
	}
	
	public void setState(State state) {
		this.state=state;
	}

	public boolean isState(State state) {
		return(this.state==state);
	}
	
	public void setCamp(Camp camp) {
		this.camp=camp;
	}
	
	public boolean isCamp(Camp camp) {
		return(this.camp.equals(camp));
	}
	
	public Camp getCamp() {
		return(this.camp);
	}
	
	public void addOneKill() {
		this.kill +=1;
	}

	public int getNbKill() {
		return(this.kill);
	}
	
	public void addKLostHeart(int k) {
		this.lostHeart +=k;
	}

	public int getLostHeart() {
		return(this.lostHeart);
	}
	
	public void setVote(String vote) {
		this.playerVote =vote;
	}
	
	public String getVotedPlayer() {
		return(this.playerVote);
	}
	
	public void setKit(Boolean kit) {
		this.kit=kit;
	}
	
	public Boolean hasKit() {		
		return(this.kit);
	}
	
	public void setPosterCamp(Camp camp) {
		this.posterCamp =camp;
	}

	public boolean isPosterCamp(Camp camp) {
		return(this.posterCamp.equals(camp));
	}
	
	public Camp getPosterCamp() {
		return(this.posterCamp);
	}
	
	public void incVote() {
		this.vote+=1;
	}

	public void resetVote() {
		this.vote=0;
	}

	public int getVote() {
		return(this.vote);
	}

	public void setStolen(Boolean stolen) {
		this.hasBeenStolen =stolen;
	}
	
	public Boolean hasBeenStolen() {
		return(this.hasBeenStolen);
	}

	public void setRole(RoleLG role) {
		this.role=role;
	}

	public void setFlair(Float flair) {
		this.flair =flair;
	}

	public float getFlair() {
		return(this.flair);
	}

	public RoleLG getRole() {
		return(this.role);
	}

	public Boolean isRole(RoleLG role) {
		return(this.role.equals(role));
	}

	public Boolean isPosterRole(RoleLG role) {
		return(this.posterRole.equals(role));
	}

	public void setPower(Boolean power) {
		this.power=power;
	}
	
	public Boolean hasPower() {
		return(this.power);
	}
	public void setSpawn(Location spawn) {
		this.spawn=spawn;
	}

	public Location getSpawn() {
		return(this.spawn);
	}
	public void setDamn(Boolean damn) {
		this.damn = damn;
	}

	public Boolean hasDamn() {
		return(this.damn);
	}
	public void setSalvation(Boolean salvation) {
		this.salvation = salvation;
	}
	
	public Boolean hasSalvation() {
		return(this.salvation);
	}

	public void setThief(Boolean thief) {
		this.thief = thief;
	}
	
	public Boolean isThief() {
		return(this.thief);
	}
	
	public void addAffectedPlayer(String player) {
		this.affectedPlayer.add(player);
	}
	
	public void clearAffectedPlayer() {
		this.affectedPlayer.clear();
	}
	
	public List<String> getAffectedPlayer() {
		return(this.affectedPlayer);
	}
	
	public void addTargetOf(String player) {
		this.targetOf.add(player);
	}
	
	public List<String> getTargetOf() {
		return(this.targetOf);
	}
	
	public void addCouple(String c) {
		this.couple.add(c);
	}

	public void clearCouple() {
		this.couple.clear();
	}
	
	public List<String> getCouple() {
		return(this.couple);
	}
	public void setKiller(String killer) {
		this.killer=killer;
	}
	
	public String getKiller() {
		return(this.killer);
	}

	public void setPosterRole(RoleLG roleLG) {
		this.posterRole =roleLG;
	}
	
	public RoleLG getPosterRole() {
		return(this.posterRole);
	}
	
	public void setDeathTime(Integer deathTime) {
		this.deathTime =deathTime;
	}
	
	public int getDeathTime() {
		return(this.deathTime);
	}
	
	public void incDiamondLimit() {
		this.diamondLimit +=1;
	}
	
	public int getDiamondLimit() {
		return(this.diamondLimit);
	}

	public void setCanBeInfect(Boolean b) {
		this.canBeInfect =b;
	}
	
	public Boolean canBeInfect() {
		return(this.canBeInfect);
	}
	public void addDisciple(String disciple) {
		this.disciple.add(disciple);
	}
	
	public void removeDisciple(String disciple) {
		this.disciple.remove(disciple);
	}
	
	public List<String> getDisciple() {
		return(this.disciple);
	}

	public void removeAffectedPlayer(String playername) {
		this.affectedPlayer.remove(playername);
	}

	public void clearLostHeart() {
		this.lostHeart =0;
	}

	public void removeCouple(String playername) {
		this.couple.remove(playername);
	}

	public int getUse() {
		return use;
	}

	public void setUse(int use) {
		this.use = use;
	}
}

