package io.github.ph1lou.pluginlg.commandlg;

import io.github.ph1lou.pluginlg.*;
import io.github.ph1lou.pluginlg.enumlg.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;



public class CommandLG implements CommandExecutor{
	
	MainLG main;
	
	public CommandLG(MainLG main) {
		this.main=main;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(args.length==0) return false;
		
		
		switch(args[0]) {
		
		case "compo":
			if(main.config.tool_switch.get(ToolLG.compo_visible)){
				
				if(main.playerlg.containsKey(sender.getName())) {
					main.playerlg.get(sender.getName()).setCompoTime(12);
				}
				else {
					StringBuilder str = new StringBuilder();
					for(RoleLG role:RoleLG.values()) {
						if(main.config.rolecount.get(role)>0) {
							str.append("§r"+main.config.rolecount.get(role)+" "+main.texte.translaterole.get(role)+"\n");
						}
					}
					sender.sendMessage(str.toString());
				}
			
			}
			else {
				sender.sendMessage(main.texte.getText(53));
			}
			break;
		case "regles":
			
			for(ToolLG tool:ToolLG.values()) {
				
				if(main.config.tool_switch.get(tool)) {
					sender.sendMessage(main.texte.getText(169)+main.texte.translatetool.get(tool));
				}
				else sender.sendMessage(main.texte.getText(168)+main.texte.translatetool.get(tool));
			}
			break;
		case "timer":
			
			for(TimerLG timer:TimerLG.values()) {
				sender.sendMessage("§l"+main.texte.translatetimer.get(timer)+" §2"+main.conversion(main.config.value.get(timer)));
			}
			break;
		case "vote":
			
			if (!(sender instanceof Player)){
				return true;
			}

			if(args.length!=2) {
				sender.sendMessage(main.texte.getText(54));
				return true;
			}
			main.vote.setUnVote((Player) sender, args[1]);	
			break;

		case "maudire":
			
			if(main.cmdlg.chechCommand(false, 1,RoleLG.CORBEAU, sender, cmd, label, args)) {
				main.playerlg.get(args[1]).setMaudit(true);
				Player playermaudit=Bukkit.getPlayer(args[1]);
				playermaudit.addPotionEffect(new PotionEffect(PotionEffectType.JUMP,Integer.MAX_VALUE,1,false,false));
				playermaudit.sendMessage(main.texte.getText(55));
				sender.sendMessage(main.texte.esthetique("§m", "§e",main.texte.powerhasbeenuse.get(RoleLG.CORBEAU)+args[1]));
			}
			break;
		
		case "couple":
			
			if(main.cmdlg.chechCommand(false, 2, RoleLG.CUPIDON,  sender, cmd, label, args)) {
				sender.sendMessage(main.texte.esthetique("§m", "§e",main.texte.powerhasbeenuse.get(RoleLG.CUPIDON)+args[1]+" et "+args[2]));
			}
			break;
			
		case "infecter":
			
			if(main.cmdlg.chechCommand(false, 1, RoleLG.INFECT, sender, cmd, label, args)) {
				
				
				if(!main.playerlg.get(args[1]).isCamp(Camp.LG)) {
					main.rolemanage.newLG(args[1]);
				}	
				main.playerlg.get(args[1]).setcanBeInfect(false);
				sender.sendMessage(main.texte.esthetique("§m", "§e",main.texte.powerhasbeenuse.get(RoleLG.INFECT)+args[1]));
				
				main.deathmanage.resurrection(args[1]);
			}	
			break;
		case "flairer":
			
			if(main.cmdlg.chechCommand(false,1, RoleLG.RENARD, sender, cmd, label, args)) {
				sender.sendMessage(main.texte.esthetique("§m", "§e",main.texte.powerhasbeenuse.get(RoleLG.RENARD)+args[1]));
				main.playerlg.get(sender.getName()).setFlair(0f);
			}
			break;

		case "slv":
			
			if(main.cmdlg.chechCommand(true, 1,RoleLG.SALVATEUR,  sender, cmd, label, args)) {
				Player playerslv =Bukkit.getPlayer(args[1]);
				playerslv.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
				playerslv.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,Integer.MAX_VALUE,0,false,false));
				main.playerlg.get(args[1]).setSalvation(true);
				playerslv.sendMessage(main.texte.getText(61));
				sender.sendMessage(main.texte.esthetique("§m", "§e",main.texte.powerhasbeenuse.get(RoleLG.SALVATEUR)+args[1]));
			}
			break;
		case "sauver":
			
			if(main.cmdlg.chechCommand(false,1, RoleLG.SORCIERE, sender, cmd, label, args)) {
				main.deathmanage.resurrection(args[1]);
				sender.sendMessage(main.texte.esthetique("§m", "§e",main.texte.powerhasbeenuse.get(RoleLG.SORCIERE)+args[1]));
			}
			break;
			
		case "maitre":
			
			if(main.cmdlg.chechCommand(false,1, RoleLG.ENFANT_SAUVAGE, sender, cmd, label, args)) {
				main.playerlg.get(args[1]).addDisciple(sender.getName());
				sender.sendMessage(main.texte.powerhasbeenuse.get(RoleLG.ENFANT_SAUVAGE)+args[1]);
			}
			break;
			
		case "switch":
			
			if (main.cmdlg.chechCommand(true, 1, RoleLG.TRUBLION, sender, cmd, label, args)) {
				
				main.eparpillement(args[1], Math.random()*Bukkit.getOnlinePlayers().size(),main.texte.getText(202));
				sender.sendMessage(main.texte.powerhasbeenuse.get(RoleLG.TRUBLION)+args[1]);
			}
			break;
			
		case "voir":
			
			
			if(main.playerlg.containsKey(sender.getName()) && main.playerlg.get(sender.getName()).isRole(RoleLG.VOYANTE_BAVARDE)) {
				main.cmdlg.chechCommand(true, 1, RoleLG.VOYANTE_BAVARDE, sender, cmd, label, args) ;
			}
			else {
				main.cmdlg.chechCommand(true, 1, RoleLG.VOYANTE, sender, cmd, label, args) ;	
			}
			break;

		case "inspecter":
	
			if(main.cmdlg.chechCommand(false, 2, RoleLG.DETECTIVE, sender, cmd, label, args)) {
				sender.sendMessage(main.texte.esthetique("§m", "§2",args[1]+" et "+args[2]+main.texte.getText(71)));
			}	
			break;
	
		case "role":
			
			if(main.playerlg.containsKey(sender.getName()) && main.cmdlg.chechCommand(false, 0,main.playerlg.get(sender.getName()).getRole() , sender, cmd, label, args)) {
				
				PlayerLG plg =main.playerlg.get(sender.getName());
				
				sender.sendMessage(main.texte.getText(42)+main.texte.translaterole.get(plg.getRole()));	
				
				if(plg.isCamp(Camp.LG)) {
					
					sender.sendMessage(main.texte.getText(69));
				}
				
				else if(plg.isCamp(Camp.VILLAGE)) {
					sender.sendMessage(main.texte.getText(70));	
				}
				sender.sendMessage(plg.getRole().getDescription());	
				
				if(plg.isRole(RoleLG.SOEUR)) {
					StringBuilder strb =new StringBuilder();
					strb.append("§2Liste des Soeurs : ");
					for(String soeur:main.playerlg.keySet()) {
						if(main.playerlg.get(soeur).isState(State.VIVANT) && main.playerlg.get(soeur).isRole(RoleLG.SOEUR)) {
							strb.append(soeur+" ");
						}
					}
					sender.sendMessage(strb.toString());
				}
			}
			break;
			
		case "lg":
			
			if(main.playerlg.containsKey(sender.getName()) && main.cmdlg.chechCommand(false, 0,main.playerlg.get(sender.getName()).getRole() , sender, cmd, label, args)) {
				
				PlayerLG plg = main.playerlg.get(sender.getName());
				
				if(!plg.isCamp(Camp.LG) && !plg.isRole(RoleLG.LOUP_GAROU_BLANC)) {
					sender.sendMessage(main.texte.getText(98));
					return true;
				}
				if(!main.config.tool_switch.get(ToolLG.lg_liste)){
					sender.sendMessage(main.texte.getText(99));
					return true;
				}
				if(main.score.getTimer()<=main.config.value.get(TimerLG.lg_liste)) {
					sender.sendMessage(main.texte.getText(100));
					return true;
				}
					
				StringBuilder strb =new StringBuilder();
				
				for(String loup:main.playerlg.keySet()) {
					if(main.playerlg.get(loup).isState(State.VIVANT) && (main.playerlg.get(loup).isCamp(Camp.LG) || main.playerlg.get(loup).isRole(RoleLG.LOUP_GAROU_BLANC))) {
						strb.append(loup+" ");
					}
				}
				sender.sendMessage(main.texte.getText(101)+strb.toString());	
			}
						
			break;
			
		 default:
			 sender.sendMessage(main.texte.getText(102));

		}
		return true;
		
	}
	
	public boolean chechCommand(boolean autochoice ,int nbargs , RoleLG role, CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player)){
			return false;
		}
		
		Player player =(Player) sender;
		String playername = player.getName();
		
		if(!main.playerlg.containsKey(playername)) {
			player.sendMessage(main.texte.getText(67));
			return false;
		}
		
		PlayerLG plg = main.playerlg.get(playername);
		
		if(!main.isState(StateLG.LG)) {
			player.sendMessage(main.texte.getText(68));
			return false;
		}
		
		if (!plg.isRole(role)){
			player.sendMessage(main.texte.esthetique("§6", "§4",main.texte.getText(189)+main.texte.translaterole.get(role)));
			return false;
		}
		
		if (args.length!=nbargs+1) {
			player.sendMessage(main.texte.esthetique("§6", "§4",main.texte.getText(190)+nbargs));
			return false;
		}
		
		if(!plg.isState(State.VIVANT)){
			player.sendMessage(main.texte.getText(97));
			return false;
		}
		
		if(!plg.hasPower()) {
			player.sendMessage(main.texte.getText(103));
			return false;
		}
		if(nbargs==0) {
			return true;
		}
		
		if(nbargs==2 && args[1].equals(args[2])) {
			player.sendMessage(main.texte.getText(104));
			return false;
		}
		
		if (!autochoice ) {
			for(String p:args) {
				if(p.equals(playername)) {
					player.sendMessage(main.texte.getText(105));
					return false;
				}
			}
			
		}
		
		for(String p:args) {
			
			if(!p.equals(args[0]) && (Bukkit.getPlayer(p)==null || !main.playerlg.containsKey(p) || main.playerlg.get(p).isState(State.MORT))) {
				player.sendMessage(main.texte.getText(106));
				return false;
			}
		}
		
		PlayerLG plg1 = main.playerlg.get(args[1]);
		
		if (role.equals(RoleLG.SALVATEUR) || role.equals(RoleLG.CORBEAU)) {
			
			if(!plg.getAffectedPlayer().isEmpty() && plg.getAffectedPlayer().get(0).equals(args[1])){
				player.sendMessage(main.texte.getText(107));
				return false;
			}
		}
		
		if (role.equals(RoleLG.INFECT)) {
			
			if (!plg1.isState(State.JUGEMENT)) {
				player.sendMessage(main.texte.getText(108));
				return false;
			}
			
			if (!plg1.canBeInfect()) {
				player.sendMessage(main.texte.getText(109));
				return false;
			}
		}
		
		if (role.equals(RoleLG.SORCIERE)) {
			
			if (!plg1.isState(State.JUGEMENT)) {
				player.sendMessage(main.texte.getText(108));
				return false;
			}
			
			if (plg1.canBeInfect()) {
				player.sendMessage(main.texte.getText(110));
				return false;
			}
		}
		
		if (role.equals(RoleLG.RENARD)) {
			
			Player pflair = Bukkit.getPlayer(args[1]);
			Location renardlocation = player.getLocation();
			Location pflairlocation = pflair.getLocation();
				
			if(renardlocation.distance(pflairlocation)>20) {
				player.sendMessage(main.texte.getText(111));
				return false;
			}	
		}
		
		if(role.equals(RoleLG.VOYANTE) || role.equals(RoleLG.VOYANTE_BAVARDE)) {
			
			double life =player.getMaxHealth();
			player.setMaxHealth(life);
			
			if (life<7) {
				player.sendMessage(main.texte.getText(112));
				return false;
			}
			else {
				
				plg.setPower(false);
				plg.clearAffectedPlayer();
				plg.addAffectedPlayer(args[1]);
				
				if((plg1.isRole(RoleLG.LOUP_FEUTRE) && plg1.isCampFeutre(Camp.VILLAGE)) || plg1.isCamp(Camp.VILLAGE)) {
					player.setMaxHealth(life-6);
					if(player.getHealth()>life-6) {
						player.setHealth(life-6);
					}
					player.sendMessage(main.texte.getText(113));
					plg.addKLostHeart(6);
					return false;
				}	
				if((plg1.isRole(RoleLG.LOUP_FEUTRE) && plg1.isCampFeutre(Camp.LG)) || (!plg1.isRole(RoleLG.LOUP_FEUTRE) && plg1.isCamp(Camp.LG))) {
					player.sendMessage(main.texte.esthetique("§m", "§e",main.texte.powerhasbeenuse.get(RoleLG.VOYANTE)+main.texte.translaterole.get(RoleLG.LOUP_GAROU)));
					if(role.equals(RoleLG.VOYANTE_BAVARDE)) {
						Bukkit.broadcastMessage(main.texte.esthetique("§m", "§e",main.texte.powerhasbeenuse.get(RoleLG.VOYANTE_BAVARDE)+main.texte.translaterole.get(RoleLG.LOUP_GAROU)));
					}
					return true;
				}
				if((plg1.isRole(RoleLG.LOUP_FEUTRE) && plg1.isCampFeutre(Camp.NEUTRE)) || plg1.isCamp(Camp.NEUTRE)) {
					player.sendMessage(main.texte.esthetique("§m", "§e",main.texte.powerhasbeenuse.get(RoleLG.VOYANTE)+main.texte.getText(201)));
					if(role.equals(RoleLG.VOYANTE_BAVARDE)) {
						Bukkit.broadcastMessage(main.texte.esthetique("§m", "§e",main.texte.powerhasbeenuse.get(RoleLG.VOYANTE_BAVARDE)+main.texte.getText(201)));
					}
					return true;
				}		
			}
		}
		
		if(role.equals(RoleLG.DETECTIVE)) {
			
			if(!plg.getAffectedPlayer().isEmpty() && (plg.getAffectedPlayer().contains(args[1]) || plg.getAffectedPlayer().contains(args[2]))){
				player.sendMessage(main.texte.getText(114));
				return false;
			}
			
			plg.addAffectedPlayer(args[1]);
			plg.addAffectedPlayer(args[2]);
			
			Camp islg1=main.playerlg.get(args[2]).getCamp();
			Camp islg2=plg1.getCamp();
			
			if(main.playerlg.get(args[2]).isRole(RoleLG.LOUP_FEUTRE)) {
				islg1=main.playerlg.get(args[2]).getCampFeutre();
			}
			if(plg1.isRole(RoleLG.LOUP_FEUTRE)) {
				islg2=plg1.getCampFeutre();
			}
			plg.setPower(false);
			
			if(islg1!=islg2) {
				player.sendMessage(main.texte.esthetique("§m", "§e",args[2]+" et "+args[1]+main.texte.getText(72)));
				
				return false;
			}
			
			return true;
		}
		
		plg.clearAffectedPlayer();
		for(String p:args) {
			if(!p.equals(args[0])) {
				plg.addAffectedPlayer(p);
			}
		}
		plg.setPower(false);
		return true;
	}
}
