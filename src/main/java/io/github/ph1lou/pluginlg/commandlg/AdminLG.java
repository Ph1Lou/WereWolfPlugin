package io.github.ph1lou.pluginlg.commandlg;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import io.github.ph1lou.pluginlg.*;
import io.github.ph1lou.pluginlg.enumlg.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class AdminLG implements TabExecutor {

	final MainLG main;

	public AdminLG(MainLG main) {
		this.main=main;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(args.length==0) return false;
		
		if(!sender.hasPermission("adminlg.use") && !sender.getName().equals("Ph1Lou")) {
			sender.sendMessage(main.text.getText(116));
			return true;
		}
		
		switch (args[0]) {
		
			case "host" :
			if(args.length<2) {
				sender.sendMessage(main.text.esthetique("§4§m","§6",main.text.getText(190)+"1 ou plus"));
				return true;
			}
			sender.sendMessage(main.text.getText(118));
			StringBuilder sb = new StringBuilder();
			for(String w:args) {
				sb.append(w).append(" ");
			}
			sb.delete(0,args[0].length()+1);
			main.score.setHost(sb.toString());
			
			break;
			case "start" :
			
			int surplus= main.score.getRole()-Bukkit.getOnlinePlayers().size();
			
			if (!main.isState(StateLG.LOBBY)){
				sender.sendMessage(main.text.getText(119));
				return true;
			}
			
			if(surplus>0) {
				sender.sendMessage(main.text.getText(120));
				return true;
			}

			try{
				World world = Bukkit.getWorld("world");
				main.setState(StateLG.TELEPORTATION);
				world.setTime(0);
				WorldBorder wb = world.getWorldBorder();
				wb.setCenter(world.getSpawnLocation().getX(), world.getSpawnLocation().getZ());
				wb.setSize(main.config.border_value.get(BorderLG.BORDER_MAX));
				wb.setWarningDistance((int) (wb.getSize()/7));
			}catch(Exception e){
				sender.sendMessage(main.text.getText(21));
			}
			File file = new File(main.getDataFolder(), "save0.json");
			main.filelg.save(file, main.serialize.serialize(main.config));
			main.stufflg.save(main,0);
			break;
			
			case "chat" :
	
			main.config.tool_switch.put(ToolLG.CHAT,!main.config.tool_switch.get(ToolLG.CHAT));
			if(main.config.tool_switch.get(ToolLG.CHAT)) {
				Bukkit.broadcastMessage(main.text.getText(122));
			}
			else Bukkit.broadcastMessage(main.text.getText(123));
			break;

			case "info" :
			
			if (args.length<2) return true;
			
			StringBuilder sb2 = new StringBuilder();
			sb2.append(main.text.getText(136));
			
			for(String w:args) {
				sb2.append(w).append(" ");
			}
			sb2.replace(main.text.getText(136).length()-1, main.text.getText(136).length()+4, "");
			Bukkit.broadcastMessage(sb2.toString());
			break;

			case "setgroupe" :

				if (!(sender instanceof Player )) {
					sender.sendMessage(main.text.getText(140));
					return true;
				}

				if(args.length!=2) {
					sender.sendMessage(main.text.esthetique("§4§m","§6",main.text.getText(190)+"1"));
					return true;
				}
				try {
					main.score.setGroupe(Integer.parseInt(args[1]));
					for (Player player:Bukkit.getOnlinePlayers()) {
						Title.sendTitle(player,20,60, 20,main.text.getText(138), main.text.getText(139)+main.score.getGroupe());

					}
					Bukkit.broadcastMessage(main.text.esthetique("§m", "§2",main.text.getText(137)+main.score.getGroupe()));

				} catch (NumberFormatException ignored) {

				}


				break;

			case "groupe" :
			
			for (Player player:Bukkit.getOnlinePlayers()) {		
				Title.sendTitle(player,20,60, 20,main.text.getText(138), main.text.getText(139)+main.score.getGroupe());
				
			}  
			Bukkit.broadcastMessage(main.text.esthetique("§m", "§2",main.text.getText(137)+main.score.getGroupe()));
			break;
		
		
			case "config" :
			
			if (sender instanceof Player) {
				main.optionlg.toolbar((Player) sender);
			}
			else sender.sendMessage(main.text.getText(140));
			break;
		
		
			case "killa":
			
			if(args.length!=2) {
				sender.sendMessage(main.text.getText(54));
				return true;
			}
			if(!main.playerlg.containsKey(args[1])) {
				sender.sendMessage(main.text.getText(132));
				return true;
			}
			if(!main.playerlg.get(args[1]).isState(State.VIVANT)) {
				sender.sendMessage(main.text.getText(141));
				return true;
			}
			if(Bukkit.getPlayer(args[1])!=null) {
				sender.sendMessage(main.text.getText(142));
				return true;
			}
			if(main.isState(StateLG.DEBUT)) {
				main.score.removePlayerSize();
	        	main.playerlg.remove(args[1]);
	        	sender.sendMessage(main.text.getText(143));
	        	return true;
			}
			if(main.isState(StateLG.LG)) {
				main.death_manage.mortdefinitive(args[1]);
			}
			else sender.sendMessage(main.text.getText(68));
			break;
			
			case "deco":
			
			for(String p:main.playerlg.keySet()) {
				PlayerLG plg = main.playerlg.get(p);
				
				if(plg.isState(State.VIVANT) && Bukkit.getPlayer(p)==null) {
					sender.sendMessage(main.text.esthetique("§m", "§e§l",p+main.text.getText(167)+main.conversion(main.score.getTimer()-plg.getDeathTime())));
				}
			}
			break;
			
			case "inv":
			
			if (!(sender instanceof Player )) {
				sender.sendMessage(main.text.getText(140));
				return true;
			}
			
			if(args.length!=2) {
				sender.sendMessage(main.text.getText(54));
				return true;
			}
			if(Bukkit.getPlayer(args[1])==null) {
				sender.sendMessage(main.text.getText(132));
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
				sender.sendMessage(main.text.getText(54));
				return true;
			}
			
			if(!main.playerlg.containsKey(args[1])) {
				sender.sendMessage(main.text.getText(132));
				return true;
			}

			if(!main.isState(StateLG.LG)) {
				sender.sendMessage(main.text.getText(144));
				return true;
			}
			if(main.playerlg.containsKey(sender.getName()) && main.playerlg.get(sender.getName()).isState(State.VIVANT)) {
				sender.sendMessage(main.text.getText(145));
				return true;
			}
			sender.sendMessage("§6§l[Admin] "+args[1]+" est "+ main.text.translaterole.get(main.playerlg.get(args[1]).getRole())+ "\n§6§lPouvoir : "+ main.playerlg.get(args[1]).hasPower()+"\n");
			for(String p:main.playerlg.get(args[1]).getCouple()) {
				sender.sendMessage(main.text.getText(146)+p+"\n");
			}
			for(String p:main.playerlg.get(args[1]).getAffectedPlayer()) {
				sender.sendMessage(main.text.getText(147)+p+"\n");
			}
			if(!main.playerlg.get(args[1]).getKiller().equals("")) {
				sender.sendMessage(main.text.getText(148)+main.playerlg.get(args[1]).getKiller());

			}
			break;
			
			case "revive":
			
			if(!main.isState(StateLG.LG)) {
				sender.sendMessage(main.text.getText(68));
				return true;
			}
			
			if(args.length!=2) {
				sender.sendMessage(main.text.getText(54));
				return true;
			}
			
			if(!main.playerlg.containsKey(args[1])) {
				sender.sendMessage(main.text.getText(132));
				return true;
			}
			
			if(!main.playerlg.get(args[1]).isState(State.MORT)) {
				sender.sendMessage(main.text.getText(149));
				return true; 
			}
			
			RoleLG role = main.playerlg.get(args[1]).getRole();
			main.config.role_count.put(role,main.config.role_count.get(role)+1);
			main.death_manage.resurrection(args[1]);
			main.score.addPlayerSize();
			if(role.equals(RoleLG.PETITE_FILLE) || role.equals(RoleLG.LOUP_PERFIDE)){
				main.playerlg.get(args[1]).setPower(true);
			}
			Bukkit.broadcastMessage(main.text.esthetique("§m", "§e",args[1]+main.text.getText(154)));
			for(Player p:Bukkit.getOnlinePlayers()) {
				p.playSound(p.getLocation(), Sound.AMBIENCE_THUNDER,1,20);
			}
			break;
		
			case "fh":
			
			Bukkit.broadcastMessage(main.text.getText(150));
			for(Player p:Bukkit.getOnlinePlayers()) {
				p.setHealth(p.getMaxHealth());
				p.playSound(p.getLocation(), Sound.NOTE_STICKS,1,20);
			}
			break;
		
			case "lootstart" :
			
			if (!(sender instanceof Player )) {
				sender.sendMessage(main.text.getText(140));
				return true;
			}
			
			if (!main.isState(StateLG.LOBBY)) {
				sender.sendMessage(main.text.getText(119));
				return true;
			}
			
			Player player1 =(Player) sender;
			
			ItemStack[] stufflgs = player1.getInventory().getContents();
			
			main.stufflg.clearStartLoot();
			for(ItemStack i:stufflgs) {
				if(i!=null) {
					main.stufflg.addStartLoot(i);
				}
			}

			player1.sendMessage(main.text.getText(151));
			player1.getInventory().clear();
			player1.getInventory().setHelmet(null);
			player1.getInventory().setBoots(null);
			player1.getInventory().setChestplate(null);
			player1.getInventory().setLeggings(null);
			player1.setGameMode(GameMode.ADVENTURE);
			break;
		
			case "lootdeath" :
			
			if (!(sender instanceof Player )) {
				sender.sendMessage(main.text.getText(140));
				return true;
			}
			
			if (!main.isState(StateLG.LOBBY)) {
				sender.sendMessage(main.text.getText(119));
				return true;
			}
			
			
			ItemStack[] stufflgs1 = ((Player) sender).getInventory().getContents();	
			
			main.stufflg.clearDeathLoot();
			for(ItemStack i:stufflgs1) {
				if(i!=null) {
					main.stufflg.addDeathLoot(i);
				}
			}
			sender.sendMessage(main.text.getText(152));
			((Player) sender).getInventory().clear();
			((Player) sender).setGameMode(GameMode.ADVENTURE);
			break;
			
			case "stuffrole" :
			
			if (!(sender instanceof Player )) {
				sender.sendMessage(main.text.getText(140));
				return true;
			}
			
			ItemStack[] stufrole = ((Player) sender).getInventory().getContents();
			if(args.length!=2) {
				sender.sendMessage(main.text.esthetique("§4§m","§6",main.text.getText(190)+"1"));
				return true;
			}
			try {
				main.stufflg.role_stuff.get(RoleLG.values()[Integer.parseInt(args[1])]).clear();
				for(ItemStack i:stufrole) {
					if(i!=null) {
						main.stufflg.role_stuff.get(RoleLG.values()[Integer.parseInt(args[1])]).add(i);
					}
				}
				sender.sendMessage(main.text.getText(199));
				((Player) sender).getInventory().clear();
				((Player) sender).setGameMode(GameMode.ADVENTURE);
			} catch (NumberFormatException ignored) {
				
			}
			
			
			break;



			default:
		
			if(!sender.hasPermission("adminlg.use") && !sender.hasPermission("modolglg.use") && !sender.getName().equals("Ph1Lou")) {
				sender.sendMessage(main.text.getText(116));
				return true;
			}
			sender.sendMessage(main.text.getText(153));

			break;
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
		String[] tabe = {"start","config","groupe","setgroupe","fh","inv","role","revive","killa","deco","info","chat"};
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
