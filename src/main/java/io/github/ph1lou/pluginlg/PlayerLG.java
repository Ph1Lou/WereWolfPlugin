package io.github.ph1lou.pluginlg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import io.github.ph1lou.pluginlg.enumlg.Camp;
import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import io.github.ph1lou.pluginlg.enumlg.State;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;


public class PlayerLG{

	private State state = State.VIVANT;
	private Camp camp = Camp.VILLAGE;
	private Camp campfeutre = Camp.LG;
	private RoleLG role=RoleLG.VILLAGEOIS;
	private RoleLG rolefeutre=RoleLG.LOUP_FEUTRE;
	private Boolean power=true;
	private Boolean canbeinfect=false;
	private Boolean maudit=false;
	private Boolean salvationner=false;
	private Boolean hasbeenstolen=false;
	private Boolean voleur=false;
	private	Boolean kit=false;
	Scoreboard board;
	private final List<String> affected_player = new ArrayList<>();
	private final List<String> disciple = new ArrayList<>();
	private final List<String> cibleof = new ArrayList<>();
	private final List<String> couple = new ArrayList<>();
	private final List<ItemStack> itemsdeath= new ArrayList<>();
	private Location spawn;
	private int deathtime=0;
	private int vote=0;
	private int diamondlimit=17;
	private int lostheart=0;
	private int nbkill=0;
	private float progflair = 0;
	private int timecompo = 0;
	private String killer="";
	private String avote="";

	public PlayerLG() {
		board = Bukkit.getScoreboardManager().getNewScoreboard();
	}

	public Scoreboard getScoreBoard(){
		return this.board;
	}

	public void setScoreBoard(Scoreboard board){
		this.board=board;
	}

	public void setItemDeath(ItemStack[] itemsdeath) {
		this.itemsdeath.addAll(Arrays.asList(itemsdeath));
	}

	public List<ItemStack> getItemDeath() {
		return this.itemsdeath;
	}

	public void addItemDeath(ItemStack itemsdeath) {
		this.itemsdeath.add(itemsdeath);
	}

	public void clearItemDeath() {
		this.itemsdeath.clear();
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
		this.nbkill+=1;
	}

	public int getNbKill() {
		return(this.nbkill);
	}
	
	public void addKLostHeart(int k) {
		this.lostheart+=k;
	}

	public int getLostHeart() {
		return(this.lostheart);
	}
	
	public void setVote(String vote) {
		this.avote=vote;
	}
	
	public String getVotedPlayer() {
		return(this.avote);
	}
	
	public void setKit(Boolean kit) {
		this.kit=kit;
	}
	
	public Boolean hasKit() {		
		return(this.kit);
	}
	
	public void setCampFeutre(Camp camp) {
		this.campfeutre=camp;
	}

	public boolean isCampFeutre(Camp camp) {
		return(this.campfeutre.equals(camp));
	}
	
	public Camp getCampFeutre() {
		return(this.campfeutre);
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

	public void setStolen(Boolean hasbeenstolen) {
		this.hasbeenstolen=hasbeenstolen;
	}
	
	public Boolean hasBeenStolen() {
		return(this.hasbeenstolen);
	}

	public void setRole(RoleLG role) {
		this.role=role;
	}

	public void setFlair(Float flair) {
		this.progflair=flair;
	}

	public float getFlair() {
		return(this.progflair);
	}

	public RoleLG getRole() {
		return(this.role);
	}

	public Boolean isRole(RoleLG role) {
		return(this.role.equals(role));
	}

	public Boolean isRoleFeutre(RoleLG role) {
		return(this.rolefeutre.equals(role));
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
	public void setMaudit(Boolean maudit) {
		this.maudit= maudit;
	}

	public Boolean hasMaudit() {
		return(this.maudit);
	}
	public void setSalvation(Boolean salvation) {
		this.salvationner= salvation;
	}
	
	public Boolean hasSalvation() {
		return(this.salvationner);
	}

	public void setVoleur(Boolean voleur) {
		this.voleur= voleur;
	}
	
	public Boolean isVoleur() {
		return(this.voleur);
	}
	
	public void addAffectedPlayer(String player) {
		this.affected_player.add(player);
	}
	
	public void clearAffectedPlayer() {
		this.affected_player.clear();
	}
	
	public List<String> getAffectedPlayer() {
		return(this.affected_player);
	}
	
	public void addTargetOf(String player) {
		this.cibleof.add(player);
	}
	
	public List<String> getTargetOf() {
		return(this.cibleof);
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

	public void setRoleFeutre(RoleLG roleLG) {
		this.rolefeutre=roleLG;
	}
	
	public RoleLG getRoleFeutre() {
		return(this.rolefeutre);
	}
	
	public void setDeathTime(Integer deathtime) {
		this.deathtime=deathtime;
	}
	
	public int getDeathTime() {
		return(this.deathtime);
	}
	
	public void setCompoTime(Integer compotime) {
		this.timecompo=compotime;
	}
	
	public int getCompoTime() {
		return(this.timecompo);
	}
	
	public void decDiamondLimit() {
		this.diamondlimit-=1;
	}
	
	public int getDiamondLimit() {
		return(this.diamondlimit);
	}
	public void setDiamondLimit(int diamond) {
		this.diamondlimit=diamond;
	}

	public void setCanBeInfect(Boolean b) {
		this.canbeinfect=b;
	}
	
	public Boolean canBeInfect() {
		return(this.canbeinfect);
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
		this.affected_player.remove(playername);
	}

	public void clearLostHeart() {
		this.lostheart=0;
	}

	public void removeCouple(String playername) {
		this.couple.remove(playername);
	}
}

