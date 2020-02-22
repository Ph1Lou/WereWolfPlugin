package io.github.ph1lou.pluginlg.commandlg;

import java.io.File;

import io.github.ph1lou.pluginlg.*;
import io.github.ph1lou.pluginlg.enumlg.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import com.connorlinfoot.titleapi.TitleAPI;


public class AdminLG implements CommandExecutor{

	MainLG main;
	
	public AdminLG(MainLG main) {
		this.main=main;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(args.length==0) return false;
		
		if(!sender.hasPermission("adminlg.use") && !sender.getName().equals("Ph1Lou")) {
			sender.sendMessage(main.texte.getText(116));	
			return true;
		}
		
		switch (args[0]) {
		
		case "host" :
			if(args.length<2) {
				sender.sendMessage(main.texte.esthetique("§4§m","§6",main.texte.getText(190)+"1 ou plus"));
				return true;
			}
			sender.sendMessage(main.texte.getText(118));	
			StringBuilder sb = new StringBuilder();
			for(String w:args) {
				sb.append(w+" ");
			}
			sb.delete(0,args[0].length()+1);
			main.score.setHost(sb.toString());
			
			break;
		case "start" :
			
			int surplus= main.score.getRole()-Bukkit.getOnlinePlayers().size();
			
			if (!main.isState(StateLG.LOBBY)){
				sender.sendMessage(main.texte.getText(119));
				return true;
			}
			
			if(surplus>0) {
				sender.sendMessage(main.texte.getText(120));
				return true;
			}
			
			main.config.rolecount.put(RoleLG.VILLAGEOIS,-surplus + main.config.rolecount.get(RoleLG.VILLAGEOIS));
			main.setState(StateLG.TELEPORTATION);
			main.setDay(Day.DAY);
			World world = Bukkit.getWorld("world");
			WorldBorder wb =world.getWorldBorder();
			wb.setSize(main.config.bordurevalue.get(BordureLG.borduremax));
			wb.setWarningDistance((int) (wb.getSize()/7));
			wb.setCenter(world.getSpawnLocation().getX(), world.getSpawnLocation().getZ());	
			File file = new File(main.getDataFolder(), "savelg0.json");
			main.filelg.save(file, main.serialize.serialize(main.config));
			main.stufflg.save(main,0);
			break;
			
		case "chat" :
	
			main.config.tool_switch.put(ToolLG.chat,!main.config.tool_switch.get(ToolLG.chat));
			if(main.config.tool_switch.get(ToolLG.chat)) {
				Bukkit.broadcastMessage(main.texte.getText(122));
			}
			else Bukkit.broadcastMessage(main.texte.getText(123));
			break;
		
		case "info" :
			
			if (args.length<2) return true;
			
			StringBuilder sb2 = new StringBuilder();
			sb2.append(main.texte.getText(136));
			
			for(String w:args) {
				sb2.append(w+" ");
			}
			sb2.replace(main.texte.getText(136).length()-1, main.texte.getText(136).length()+4, "");
			Bukkit.broadcastMessage(sb2.toString());
			break;
			
		
		case "groupe" :
			
			for (Player player:Bukkit.getOnlinePlayers()) {		
				TitleAPI.sendTitle(player,20,60, 20,main.texte.getText(138), main.texte.getText(139)+main.score.getGroupe());
				
			}  
			Bukkit.broadcastMessage(main.texte.esthetique("§m", "§2",main.texte.getText(137)+main.score.getGroupe()));
			break;
		
		
		case "config" :
			
			if (sender instanceof Player) {
				main.optionlg.toolbar((Player) sender);
			}
			else sender.sendMessage(main.texte.getText(140));
			break;
		
		
		case "killa":
			
			if(args.length!=2) {
				sender.sendMessage(main.texte.getText(54));	
				return true;
			}
			if(!main.playerlg.containsKey(args[1])) {
				sender.sendMessage(main.texte.getText(132));	
				return true;
			}
			if(!main.playerlg.get(args[1]).isState(State.VIVANT)) {
				sender.sendMessage(main.texte.getText(141));	
				return true;
			}
			if(Bukkit.getPlayer(args[1])!=null) {
				sender.sendMessage(main.texte.getText(142));	
				return true;
			}
			if(main.isState(StateLG.DEBUT)) {
				main.score.removePlayerSize();
	        	main.playerlg.remove(args[1]);
	        	sender.sendMessage(main.texte.getText(143));	
	        	return true;
			}
			if(main.isState(StateLG.LG)) {
				main.deathmanage.mortdefinitive(args[1]);
			}
			else sender.sendMessage(main.texte.getText(68));	
			break;
			
		case "deco":
			
			for(String p:main.playerlg.keySet()) {
				PlayerLG plg = main.playerlg.get(p);
				
				if(plg.isState(State.VIVANT) && Bukkit.getPlayer(p)==null) {
					sender.sendMessage(main.texte.esthetique("§m", "§e§l",p+main.texte.getText(167)+main.conversion(main.score.getTimer()-plg.getDeathTime())));
				}
			}
			break;
			
		case "inv":
			
			if (!(sender instanceof Player )) {
				sender.sendMessage(main.texte.getText(140));
				return true;
			}
			
			if(args.length!=2) {
				sender.sendMessage(main.texte.getText(54));	
				return true;
			}
			if(Bukkit.getPlayer(args[1])==null) {
				sender.sendMessage(main.texte.getText(132));	
				return true;
			}
			Player pinv = Bukkit.getPlayer(args[1]);
			Inventory inv = Bukkit.createInventory(null, 45,args[1]);
			
			for(ItemStack i:pinv.getInventory()) {
				if(i!=null) {
					inv.addItem(i);
				}
			}
			if(pinv.getInventory().getHelmet()!=null) {
				inv.addItem(pinv.getInventory().getHelmet());
			}
			if(pinv.getInventory().getChestplate()!=null) {
				inv.addItem(pinv.getInventory().getChestplate());
			}
			if(pinv.getInventory().getLeggings()!=null) {
				inv.addItem(pinv.getInventory().getLeggings());
			}
			if(pinv.getInventory().getBoots()!=null) {
				inv.addItem(pinv.getInventory().getBoots());
			}
			
			((Player) sender).openInventory(inv);
			break;
			
		case "role":
			
			if(args.length!=2) {
				sender.sendMessage(main.texte.getText(54));	
				return true;
			}
			
			if(!main.playerlg.containsKey(args[1])) {
				sender.sendMessage(main.texte.getText(132));	
				return true;
			}

			if(!main.isState(StateLG.LG)) {
				sender.sendMessage(main.texte.getText(144));	
				return true;
			}
			if(main.playerlg.containsKey(sender.getName()) && main.playerlg.get(sender.getName()).isState(State.VIVANT)) {
				sender.sendMessage(main.texte.getText(145));	
				return true;
			}
			sender.sendMessage("§6§l[Admin] "+args[1]+" est "+ main.texte.translaterole.get(main.playerlg.get(args[1]).getRole())+ "\n§6§lPouvoir : "+ main.playerlg.get(args[1]).hasPower()+"\n");
			for(String p:main.playerlg.get(args[1]).getCouple()) {
				sender.sendMessage(main.texte.getText(146)+p+"\n");
			}
			for(String p:main.playerlg.get(args[1]).getAffectedPlayer()) {
				sender.sendMessage(main.texte.getText(147)+p+"\n");
			}
			if(!main.playerlg.get(args[1]).getKiller().equals("")) {
				sender.sendMessage(main.texte.getText(148)+main.playerlg.get(args[1]).getKiller());

			}
			break;
			
		case "revive":
			
			if(!main.isState(StateLG.LG)) {
				sender.sendMessage(main.texte.getText(68));
				return true;
			}
			
			if(args.length!=2) {
				sender.sendMessage(main.texte.getText(54));	
				return true;
			}
			
			if(!main.playerlg.containsKey(args[1])) {
				sender.sendMessage(main.texte.getText(132));	
				return true;
			}
			
			if(!main.playerlg.get(args[1]).isState(State.MORT)) {
				sender.sendMessage(main.texte.getText(149));	
				return true; 
			}
			
			RoleLG role = main.playerlg.get(args[1]).getRole();
			main.config.rolecount.put(role,main.config.rolecount.get(role)+1);
			main.deathmanage.resurrection(args[1]);
			main.score.addPlayerSize();
			Bukkit.broadcastMessage(main.texte.esthetique("§m", "§e",args[1]+main.texte.getText(154)));
			for(Player p:Bukkit.getOnlinePlayers()) {
				p.playSound(p.getLocation(), Sound.AMBIENCE_THUNDER,1,20);
			}
			break;
		
		case "fh":
			
			Bukkit.broadcastMessage(main.texte.getText(150));
			for(Player p:Bukkit.getOnlinePlayers()) {
				p.setHealth(p.getMaxHealth());
				p.playSound(p.getLocation(), Sound.PORTAL_TRAVEL,1,2);
			}
			break;
		
		case "lootstart" :
			
			if (!(sender instanceof Player )) {
				sender.sendMessage(main.texte.getText(140));
				return true;
			}
			
			if (!main.isState(StateLG.LOBBY)) {
				sender.sendMessage(main.texte.getText(119));
				return true;
			}
			
			Player player1 =(Player) sender;
			
			ItemStack[] stufflgs = player1.getInventory().getContents();
			
			main.stufflg.clearstartloot();
			for(ItemStack i:stufflgs) {
				if(i!=null) {
					main.stufflg.addstartloot(i);
				}
			}

			player1.sendMessage(main.texte.getText(151));
			player1.getInventory().clear();
			player1.getInventory().setHelmet(null);
			player1.getInventory().setBoots(null);
			player1.getInventory().setChestplate(null);
			player1.getInventory().setLeggings(null);
			player1.setGameMode(GameMode.ADVENTURE);
			break;
		
		case "lootdeath" :
			
			if (!(sender instanceof Player )) {
				sender.sendMessage(main.texte.getText(140));
				return true;
			}
			
			if (!main.isState(StateLG.LOBBY)) {
				sender.sendMessage(main.texte.getText(119));
				return true;
			}
			
			
			ItemStack[] stufflgs1 = ((Player) sender).getInventory().getContents();	
			
			main.stufflg.cleardeathloot();
			for(ItemStack i:stufflgs1) {
				if(i!=null) {
					main.stufflg.adddeathloot(i);
				}
			}
			((Player) sender).sendMessage(main.texte.getText(152));
			((Player) sender).getInventory().clear();
			((Player) sender).setGameMode(GameMode.ADVENTURE);
			break;
			
		case "stuffrole" :
			
			if (!(sender instanceof Player )) {
				sender.sendMessage(main.texte.getText(140));
				return true;
			}
			
			ItemStack[] stufrole = ((Player) sender).getInventory().getContents();
			if(args.length!=2) {
				sender.sendMessage(main.texte.esthetique("§4§m","§6",main.texte.getText(190)+"1"));
				return true;
			}
			try {
				main.stufflg.rolestuff.get(RoleLG.values()[Integer.parseInt(args[1])]).clear();
				for(ItemStack i:stufrole) {
					if(i!=null) {
						main.stufflg.rolestuff.get(RoleLG.values()[Integer.parseInt(args[1])]).add(i);
					}
				}
				((Player) sender).sendMessage(main.texte.getText(199));
				((Player) sender).getInventory().clear();
				((Player) sender).setGameMode(GameMode.ADVENTURE);
			} catch (NumberFormatException nfe) {
				
			}
			
			
			break;
		default:
		
			if(!sender.hasPermission("adminlg.use") && !sender.hasPermission("modolglg.use") && !sender.getName().equals("Ph1Lou")) {
				sender.sendMessage(main.texte.getText(116));	
				return true;
			}
			sender.sendMessage(main.texte.getText(153));

			break;
		}
		return true;
	}

}
