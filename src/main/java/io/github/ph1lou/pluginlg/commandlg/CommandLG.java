package io.github.ph1lou.pluginlg.commandlg;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.PlayerLG;
import io.github.ph1lou.pluginlg.enumlg.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CommandLG implements TabExecutor {
	
	final MainLG main;
	
	public CommandLG(MainLG main) {
		this.main=main;
	}


	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(args.length==0) return false;
		
		
		switch(args[0]) {

			case "compo":

				if(main.config.tool_switch.get(ToolLG.COMPO_VISIBLE)){

					StringBuilder sb = new StringBuilder();
					for(RoleLG role:RoleLG.values()) {
						if(main.config.role_count.get(role)>0) {
							sb.append("§3").append(main.config.role_count.get(role)).append("§r ").append(main.text.translaterole.get(role)).append("\n");
						}
					}
					sender.sendMessage(sb.toString());

				}
				else {
					sender.sendMessage(main.text.getText(53));
				}
				break;
			case "regles":

				for(ToolLG tool:ToolLG.values()) {

					if(main.config.tool_switch.get(tool)) {
						sender.sendMessage(String.format(main.text.getText(169),main.text.translatetool.get(tool)));
					}
					else sender.sendMessage(String.format(main.text.getText(168),main.text.translatetool.get(tool)));
				}
				break;
			case "scenarios":

				for(ScenarioLG scenario:ScenarioLG.values()) {

					if(main.config.scenario.get(scenario)) {
						sender.sendMessage(String.format(main.text.getText(169),main.text.translatescenario.get(scenario)));
					}
					else sender.sendMessage(String.format(main.text.getText(168),main.text.translatescenario.get(scenario)));
				}
				break;
			case "timer":

				for(TimerLG timer:TimerLG.values()) {
					String time =main.score.conversion(main.config.value.get(timer));
					if(time.charAt(0)!='-'){
						sender.sendMessage(String.format(main.text.translatetimer.get(timer),time));
					}
				}
				break;
			case "vote":

				if (!(sender instanceof Player)){
					return true;
				}

				if(args.length!=2) {
					sender.sendMessage(main.text.getText(54));
					return true;
				}
				main.vote.setUnVote((Player) sender, args[1]);
				break;

			case "maudire":

				if(main.cmdlg.chechCommand(false, true,true,1,RoleLG.CORBEAU, sender, args)) {
					main.playerlg.get(args[1]).setDamn(true);
					Player playermaudit=Bukkit.getPlayer(args[1]);
					playermaudit.addPotionEffect(new PotionEffect(PotionEffectType.JUMP,Integer.MAX_VALUE,1,false,false));
					playermaudit.sendMessage(main.text.getText(55));
					sender.sendMessage(String.format(main.text.powerhasbeenuse.get(RoleLG.CORBEAU),args[1]));
				}
				break;

			case "couple":

				if(main.cmdlg.chechCommand(false, true,true,2, RoleLG.CUPIDON,  sender, args)) {
					sender.sendMessage(String.format(main.text.powerhasbeenuse.get(RoleLG.CUPIDON),args[1],args[2]));
				}
				break;

			case "infecter":

				if(main.cmdlg.chechCommand(false, true,true,1, RoleLG.INFECT, sender, args)) {


					if(!main.playerlg.get(args[1]).isCamp(Camp.LG)) {
						main.role_manage.newLG(args[1]);
					}
					main.playerlg.get(args[1]).setCanBeInfect(false);
					sender.sendMessage(String.format(main.text.powerhasbeenuse.get(RoleLG.INFECT),args[1]));

					main.death_manage.resurrection(args[1]);
				}
				break;

			case "flairer":

				if(main.cmdlg.chechCommand(false,true,true,1, RoleLG.RENARD, sender, args)) {
					sender.sendMessage(String.format(main.text.powerhasbeenuse.get(RoleLG.RENARD),args[1]));
					main.playerlg.get(sender.getName()).setFlair(0f);
				}
				break;

			case "slv":

				if(main.cmdlg.chechCommand(true, true,true,1,RoleLG.SALVATEUR,  sender, args)) {
					Player playerslv =Bukkit.getPlayer(args[1]);
					playerslv.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
					playerslv.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,Integer.MAX_VALUE,0,false,false));
					main.playerlg.get(args[1]).setSalvation(true);
					playerslv.sendMessage(main.text.getText(61));
					sender.sendMessage(String.format(main.text.powerhasbeenuse.get(RoleLG.SALVATEUR),args[1]));
				}
				break;
			case "sauver":

				if(main.cmdlg.chechCommand(false,true,true,1, RoleLG.SORCIERE, sender, args)) {
					main.death_manage.resurrection(args[1]);
					sender.sendMessage(String.format(main.text.powerhasbeenuse.get(RoleLG.SORCIERE),args[1]));
				}
				break;

			case "maitre":

				if(main.cmdlg.chechCommand(false,true,true,1, RoleLG.ENFANT_SAUVAGE, sender, args)) {
					main.playerlg.get(args[1]).addDisciple(sender.getName());
					sender.sendMessage(String.format(main.text.powerhasbeenuse.get(RoleLG.ENFANT_SAUVAGE),args[1]));
				}
				break;

			case "switch":

				if (main.cmdlg.chechCommand(true, true,true,1, RoleLG.TRUBLION, sender, args)) {

					main.death_manage.transportation(args[1], Math.random()*Bukkit.getOnlinePlayers().size(),main.text.getText(202));
					sender.sendMessage(String.format(main.text.powerhasbeenuse.get(RoleLG.TRUBLION),args[1]));
				}
				break;

			case "voir":

				if(main.playerlg.containsKey(sender.getName()) && main.playerlg.get(sender.getName()).isRole(RoleLG.VOYANTE_BAVARDE)) {
					main.cmdlg.chechCommand(true, true,true,1, RoleLG.VOYANTE_BAVARDE, sender, args) ;
				}
				else {
					main.cmdlg.chechCommand(true, true,true,1, RoleLG.VOYANTE, sender, args) ;
				}
				break;

			case "inspecter":

				if(main.cmdlg.chechCommand(false, true,true,2, RoleLG.DETECTIVE, sender, args)) {
					sender.sendMessage(String.format(main.text.getText(71),args[1],args[2]));
				}
				break;


			case "role":

				if(main.cmdlg.chechCommand(false, false,false,0,null , sender, args)) {

					PlayerLG plg =main.playerlg.get(sender.getName());
					sender.sendMessage(main.text.description.get(plg.getRole()));

					if(plg.isRole(RoleLG.SOEUR)) {
						StringBuilder strb =new StringBuilder();
						for(String soeur:main.playerlg.keySet()) {
							if(main.playerlg.get(soeur).isState(State.LIVING) && main.playerlg.get(soeur).isRole(RoleLG.SOEUR)) {
								strb.append(soeur).append(" ");
							}
						}
						sender.sendMessage(String.format(main.text.getText(22),strb.toString()));
					}
					else if(plg.isRole(RoleLG.FRERE_SIAMOIS)) {
						StringBuilder strb =new StringBuilder();
						for(String frere:main.playerlg.keySet()) {
							if(main.playerlg.get(frere).isState(State.LIVING) && main.playerlg.get(frere).isRole(RoleLG.FRERE_SIAMOIS)) {
								strb.append(frere).append(" ");
							}
						}
						sender.sendMessage(String.format(main.text.getText(23),strb.toString()));
					}
				}
				break;

			case "lg":

				if(main.cmdlg.chechCommand(false, false,false,0,null , sender, args)) {

					PlayerLG plg = main.playerlg.get(sender.getName());

					if(!plg.isCamp(Camp.LG) && !plg.isRole(RoleLG.LOUP_GAROU_BLANC)) {
						sender.sendMessage(main.text.getText(98));
						return true;
					}
					if(!main.config.tool_switch.get(ToolLG.LG_LIST)){
						sender.sendMessage(main.text.getText(99));
						return true;
					}
					if(main.config.value.get(TimerLG.LG_LIST)>0) {
						sender.sendMessage(main.text.getText(100));
						return true;
					}

					StringBuilder strb =new StringBuilder();

					for(String loup:main.playerlg.keySet()) {
						if(main.playerlg.get(loup).isState(State.LIVING) && (main.playerlg.get(loup).isCamp(Camp.LG) || main.playerlg.get(loup).isRole(RoleLG.LOUP_GAROU_BLANC))) {
							strb.append(loup).append(" ");
						}
					}
					sender.sendMessage(String.format(main.text.getText(101),strb.toString()));
				}

				break;
			case "dechu":

				if(main.cmdlg.chechCommand(false, true,true,0,RoleLG.ANGE , sender, args)) {
					PlayerLG plg =main.playerlg.get(sender.getName());
					plg.setRole(RoleLG.ANGE_DECHU);
					plg.setPower(false);
					sender.sendMessage(String.format(main.text.powerhasbeenuse.get(RoleLG.ANGE),main.text.translaterole.get(RoleLG.ANGE_DECHU)));
				}
				break;
			case "gardien":

				if(main.cmdlg.chechCommand(false, true,true,0,RoleLG.ANGE , sender, args)) {
					PlayerLG plg =main.playerlg.get(sender.getName());
					plg.setRole(RoleLG.ANGE_GARDIEN);
					plg.setPower(false);
					sender.sendMessage(String.format(main.text.powerhasbeenuse.get(RoleLG.ANGE),main.text.translaterole.get(RoleLG.ANGE_GARDIEN)));
				}
				break;
			case "depouiller":

				if(main.cmdlg.chechCommand(false, true,true,0,RoleLG.CITOYEN , sender, args)) {
					main.vote.seeVote((Player) sender);
					main.playerlg.get(sender.getName()).setPower(false);
				}
				break;
			case "cancelvote":

				if(main.cmdlg.chechCommand(false, false,true,0,RoleLG.CITOYEN , sender, args)) {
					PlayerLG plg =main.playerlg.get(sender.getName());
					if(plg.getAffectedPlayer().isEmpty()){
						String pvote=main.vote.getResult();
						sender.sendMessage(String.format(main.text.powerhasbeenuse.get(RoleLG.CITOYEN),pvote));
						plg.addAffectedPlayer(pvote);
						Bukkit.broadcastMessage(main.text.getText(94));
						main.vote.resetVote();
					}
					else sender.sendMessage(main.text.getText(103));
				}
				break;
			default:
				sender.sendMessage(main.text.getText(102));
		}

		return true;
		
	}
	
	public boolean chechCommand(boolean autochoice,boolean needpower, boolean needrole,int nbargs, RoleLG role, CommandSender sender, String[] args) {
		
		if (!(sender instanceof Player)){
			return false;
		}
		
		Player player =(Player) sender;
		String playername = player.getName();
		
		if(!main.playerlg.containsKey(playername)) {
			player.sendMessage(main.text.getText(67));
			return false;
		}
		
		PlayerLG plg = main.playerlg.get(playername);
		
		if(!main.isState(StateLG.LG)) {
			player.sendMessage(main.text.getText(68));
			return false;
		}
		
		if (needrole && !plg.isRole(role)){
			player.sendMessage(String.format(main.text.getText(189),main.text.translaterole.get(role)));
			return false;
		}
		
		if (args.length!=nbargs+1) {
			player.sendMessage(String.format(main.text.getText(190),nbargs));
			return false;
		}
		
		if(!plg.isState(State.LIVING)){
			player.sendMessage(main.text.getText(97));
			return false;
		}
		
		if(needpower && !plg.hasPower()) {
			player.sendMessage(main.text.getText(103));
			return false;
		}
		if(nbargs==0) {
			return true;
		}
		
		if(nbargs==2 && args[1].equals(args[2])) {
			player.sendMessage(main.text.getText(104));
			return false;
		}
		
		if (!autochoice ) {
			for(String p:args) {
				if(p.equals(playername)) {
					player.sendMessage(main.text.getText(105));
					return false;
				}
			}
			
		}
		
		for(String p:args) {
			
			if(!p.equals(args[0]) && (Bukkit.getPlayer(p)==null || !main.playerlg.containsKey(p) || main.playerlg.get(p).isState(State.MORT))) {
				player.sendMessage(main.text.getText(106));
				return false;
			}
		}
		
		if (role.equals(RoleLG.SALVATEUR) || role.equals(RoleLG.CORBEAU)) {
			
			if(!plg.getAffectedPlayer().isEmpty() && plg.getAffectedPlayer().get(0).equals(args[1])){
				player.sendMessage(main.text.getText(107));
				return false;
			}
		}
		
		if (role.equals(RoleLG.INFECT)) {

			PlayerLG plg1 = main.playerlg.get(args[1]);

			if (!plg1.isState(State.JUDGEMENT)) {
				player.sendMessage(main.text.getText(108));
				return false;
			}
			
			if (!plg1.canBeInfect()) {
				player.sendMessage(main.text.getText(109));
				return false;
			}
		}
		
		if (role.equals(RoleLG.SORCIERE)) {

			PlayerLG plg1 = main.playerlg.get(args[1]);

			if (!plg1.isState(State.JUDGEMENT)) {
				player.sendMessage(main.text.getText(108));
				return false;
			}
			
			if (plg1.canBeInfect()) {
				return false;
			}
		}
		
		if (role.equals(RoleLG.RENARD)) {
			
			Player pflair = Bukkit.getPlayer(args[1]);
			Location renardlocation = player.getLocation();
			Location pflairlocation = pflair.getLocation();
				
			if(renardlocation.distance(pflairlocation)>20) {
				player.sendMessage(main.text.getText(111));
				return false;
			}	
		}
		
		if(role.equals(RoleLG.VOYANTE) || role.equals(RoleLG.VOYANTE_BAVARDE)) {
			
			double life =player.getMaxHealth();
			player.setMaxHealth(life);
			
			if (life<7) {
				player.sendMessage(main.text.getText(112));
				return false;
			}
			else {
				PlayerLG plg1 = main.playerlg.get(args[1]);
				plg.setPower(false);
				plg.clearAffectedPlayer();
				plg.addAffectedPlayer(args[1]);
				
				if((plg1.isRole(RoleLG.LOUP_FEUTRE) && plg1.isPosterCamp(Camp.VILLAGE)) || plg1.isCamp(Camp.VILLAGE)) {
					player.setMaxHealth(life-6);
					if(player.getHealth()>life-6) {
						player.setHealth(life-6);
					}
					player.sendMessage(main.text.getText(113));
					plg.addKLostHeart(6);
					return false;
				}	
				if((plg1.isRole(RoleLG.LOUP_FEUTRE) && plg1.isPosterCamp(Camp.LG)) || (!plg1.isRole(RoleLG.LOUP_FEUTRE) && plg1.isCamp(Camp.LG))) {
					player.sendMessage(String.format(main.text.powerhasbeenuse.get(RoleLG.VOYANTE),main.text.translaterole.get(RoleLG.LOUP_GAROU)));
					if(role.equals(RoleLG.VOYANTE_BAVARDE)) {
						Bukkit.broadcastMessage(String.format(main.text.powerhasbeenuse.get(RoleLG.VOYANTE_BAVARDE),main.text.translaterole.get(RoleLG.LOUP_GAROU)));
					}
					return true;
				}
				if((plg1.isRole(RoleLG.LOUP_FEUTRE) && plg1.isPosterCamp(Camp.NEUTRAL)) || plg1.isCamp(Camp.NEUTRAL)) {
					player.sendMessage(String.format(main.text.powerhasbeenuse.get(RoleLG.VOYANTE),main.text.getText(201)));
					if(role.equals(RoleLG.VOYANTE_BAVARDE)) {
						Bukkit.broadcastMessage(String.format(main.text.powerhasbeenuse.get(RoleLG.VOYANTE_BAVARDE),main.text.getText(201)));
					}
					return true;
				}		
			}
		}
		
		if(role.equals(RoleLG.DETECTIVE)) {
			
			if(!plg.getAffectedPlayer().isEmpty() && (plg.getAffectedPlayer().contains(args[1]) || plg.getAffectedPlayer().contains(args[2]))){
				player.sendMessage(main.text.getText(114));
				return false;
			}
			PlayerLG plg1 = main.playerlg.get(args[1]);
			plg.addAffectedPlayer(args[1]);
			plg.addAffectedPlayer(args[2]);
			
			Camp islg1=main.playerlg.get(args[2]).getCamp();
			Camp islg2=plg1.getCamp();
			
			if(main.playerlg.get(args[2]).isRole(RoleLG.LOUP_FEUTRE)) {
				islg1=main.playerlg.get(args[2]).getPosterCamp();
			}
			if(plg1.isRole(RoleLG.LOUP_FEUTRE)) {
				islg2=plg1.getPosterCamp();
			}
			plg.setPower(false);
			
			if(islg1!=islg2) {
				player.sendMessage(String.format(main.text.getText(72),args[2],args[1]));
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

	@Override
	public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
		String[] tabe = {"compo","regles","timer","vote","scenarios","maudire","couple","flairer","slv","maitre","switch","voir","inspecter","role","lg","dechu","gardien","depouiller","cancelvote"};
		List<String> tab = new ArrayList<>(Arrays.asList(tabe));
		if(args.length==0){
			return tab;
		}
		else if(args.length==1){

			for(int i=0;i<tab.size();i++){
				for(int j=0;j<tab.get(i).length() && j<args[0].length();j++){
					if(tab.get(i).charAt(j)!=args[0].charAt(j)){
						tab.remove(i);
						i--;
						break;
					}
				}
			}
			return tab;
		}
		return null;
	}
}
