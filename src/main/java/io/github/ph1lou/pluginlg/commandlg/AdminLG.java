package io.github.ph1lou.pluginlg.commandlg;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.PlayerLG;
import io.github.ph1lou.pluginlg.Title;
import io.github.ph1lou.pluginlg.WorldLoader;
import io.github.ph1lou.pluginlg.enumlg.*;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdminLG implements TabExecutor {

	final MainLG main;

	public AdminLG(MainLG main) {
		this.main=main;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(args.length==0) return false;

		if(!sender.hasPermission("adminlg.use")) {
			sender.sendMessage(main.text.getText(116));
			return true;
		}

		switch (args[0]) {

			case "host" :
				if(args.length<2) {
					sender.sendMessage(String.format(main.text.getText(190),1));
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
					main.setState(StateLG.TRANSPORTATION);
					world.setTime(0);
					WorldBorder wb = world.getWorldBorder();
					wb.setCenter(world.getSpawnLocation().getX(),world.getSpawnLocation().getZ());
					wb.setSize(main.config.border_value.get(BorderLG.BORDER_MAX));
					wb.setWarningDistance((int) (wb.getSize()/7));
				}catch(Exception e){
					sender.sendMessage(main.text.getText(21));
				}
				File file = new File(main.getDataFolder()+File.separator+"configs"+File.separator, "saveCurrent.json");
				main.filelg.save(file, main.serialize.serialize(main.config));
				main.stufflg.save("saveCurrent");
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

				for(String w:args) {
					sb2.append(w).append(" ");
				}
				sb2.replace(0, 4, "");
				Bukkit.broadcastMessage(String.format(main.text.getText(136),sb2.toString()));
				break;
			case "pregen" :
				World world = Bukkit.getWorld("world");
				WorldLoader worldloader = new WorldLoader(world, main.config.border_value.get(BorderLG.BORDER_MAX)/2,main);
				Bukkit.getScheduler().scheduleSyncRepeatingTask(main, worldloader, 0L, 145L);
				break;
			case "setgroup" :

				if (!(sender instanceof Player )) {
					sender.sendMessage(main.text.getText(140));
					return true;
				}

				if(args.length!=2) {
					sender.sendMessage(String.format(main.text.getText(190),1));
					return true;
				}
				try {
					main.score.setGroup(Integer.parseInt(args[1]));
					for (Player player:Bukkit.getOnlinePlayers()) {
						Title.sendTitle(player,20,60, 20,main.text.getText(138), String.format(main.text.getText(139),main.score.getGroup()));

					}
					Bukkit.broadcastMessage(String.format(main.text.getText(137),main.score.getGroup()));

				} catch (NumberFormatException ignored) {

				}
				break;

			case "group" :

				for (Player player:Bukkit.getOnlinePlayers()) {
					Title.sendTitle(player,20,60, 20,main.text.getText(138), String.format(main.text.getText(139),main.score.getGroup()));
				}
				Bukkit.broadcastMessage(String.format(main.text.getText(137),main.score.getGroup()));
				break;
		
		
			case "config" :
			
			if (sender instanceof Player) {
				main.optionlg.toolBar((Player) sender);
			}
			else sender.sendMessage(main.text.getText(140));
			break;
		
		
			case "killa":

				if(args.length!=2) {
					sender.sendMessage(main.text.getText(54));
					return true;
				}
				if(!main.playerLG.containsKey(args[1])) {
					sender.sendMessage(main.text.getText(132));
					return true;
				}
				if(!main.playerLG.get(args[1]).isState(State.LIVING)) {
					sender.sendMessage(main.text.getText(141));
					return true;
				}
				if(main.isState(StateLG.DEBUT)) {
					main.score.removePlayerSize();
					main.playerLG.remove(args[1]);
					sender.sendMessage(main.text.getText(143));
					return true;
				}
				if(Bukkit.getPlayer(args[1])!=null) {
					sender.sendMessage(main.text.getText(142));
					return true;
				}
				if(main.isState(StateLG.LG)) {
					main.death_manage.death(args[1]);
				}
				else sender.sendMessage(main.text.getText(68));
				break;

			case "disc":

				for(String p:main.playerLG.keySet()) {
					PlayerLG plg = main.playerLG.get(p);

					if(plg.isState(State.LIVING) && Bukkit.getPlayer(p)==null) {
						sender.sendMessage(String.format(main.text.getText(167),p,main.score.conversion(main.score.getTimer()-plg.getDeathTime())));
					}
				}
				break;
			/*case "sendHost":

				if (!(sender instanceof Player )) {
					sender.sendMessage(main.text.getText(140));
					return true;
				}

				if(args.length!=2) {
					sender.sendMessage(String.format(main.text.getText(190),2));
					return true;
				}
				if(main.score.getHost().equals("")) {
					sender.sendMessage("Configure Host");
					return true;
				}
				main.host.sendHostToDiscord(sender.getName(),args[1]);
				break;*/
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

			case "tpgroup" :

				if(args.length!=2 && args.length!=3) {
					sender.sendMessage(main.text.getText(54));
					return true;
				}

				if(!main.playerLG.containsKey(args[1]) || Bukkit.getPlayer(args[1])==null) {
					sender.sendMessage(main.text.getText(132));
					return true;
				}

				if(!main.isState(StateLG.LG)) {
					sender.sendMessage(main.text.getText(144));
					return true;
				}

				if(!main.playerLG.get(args[1]).isState(State.LIVING)){
					return true;
				}
				int d=20;
				int size = main.score.getGroup();
				double r= Math.random()*Bukkit.getOnlinePlayers().size();
				Player player = Bukkit.getPlayer(args[1]);
				Location location = player.getLocation();

				try {
					if(args.length==3){
						d=Integer.parseInt(args[2]);
					}
				} catch (NumberFormatException ignored) {
				}
				for (Player p:Bukkit.getOnlinePlayers()) {
					if(size>0 && main.playerLG.containsKey(p.getName()) && main.playerLG.get(p.getName()).isState(State.LIVING)) {
						if(p.getLocation().distance(location)<=d){
							size--;
							main.death_manage.transportation(p.getName(),r,main.text.getText(93));
						}
					}
				}
				break;

			case "role":

				if(args.length!=2) {
					sender.sendMessage(main.text.getText(54));
					return true;
				}

				if(!main.playerLG.containsKey(args[1])) {
					sender.sendMessage(main.text.getText(132));
					return true;
				}

				if(!main.isState(StateLG.LG)) {
					sender.sendMessage(main.text.getText(144));
					return true;
				}
				if(main.playerLG.containsKey(sender.getName()) && main.playerLG.get(sender.getName()).isState(State.LIVING)) {
					sender.sendMessage(main.text.getText(145));
					return true;
				}
				sender.sendMessage(String.format(main.text.getText(92),args[1],main.text.translateRole.get(main.playerLG.get(args[1]).getRole()))+ String.format(main.text.getText(91),main.playerLG.get(args[1]).hasPower()));
				for(String p:main.playerLG.get(args[1]).getCouple()) {
					sender.sendMessage(String.format(main.text.getText(146),p));
				}
				for(String p:main.playerLG.get(args[1]).getAffectedPlayer()) {
					sender.sendMessage(String.format(main.text.getText(147),p));
				}
				if(!main.playerLG.get(args[1]).getKiller().equals("")) {
					sender.sendMessage(String.format(main.text.getText(148),main.playerLG.get(args[1]).getKiller()));

				}
			break;

			case "revive":

				if(args.length!=2) {
					sender.sendMessage(main.text.getText(54));
					return true;
				}

				if(!main.isState(StateLG.LG)) {
					sender.sendMessage(main.text.getText(68));
					return true;
				}

				if(!main.playerLG.containsKey(args[1])) {
					sender.sendMessage(main.text.getText(132));
					return true;
				}

				if(!main.playerLG.get(args[1]).isState(State.MORT)) {
					sender.sendMessage(main.text.getText(149));
					return true;
				}

				RoleLG role = main.playerLG.get(args[1]).getRole();
				main.config.role_count.put(role,main.config.role_count.get(role)+1);
				main.death_manage.resurrection(args[1]);
				main.score.addPlayerSize();
				if(role.equals(RoleLG.PETITE_FILLE) || role.equals(RoleLG.LOUP_PERFIDE)){
					main.playerLG.get(args[1]).setPower(true);
				}
				Bukkit.broadcastMessage(String.format(main.text.getText(154),args[1]));
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

				main.stufflg.clearStartLoot();
				for(ItemStack i:((Player) sender).getInventory().getContents()) {
					if(i!=null) {
						main.stufflg.addStartLoot(i);
					}
				}

				sender.sendMessage(main.text.getText(151));
				((Player) sender).getInventory().clear();
				((Player) sender).setGameMode(GameMode.ADVENTURE);
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

				main.stufflg.clearDeathLoot();
				for(ItemStack i:((Player) sender).getInventory().getContents()) {
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

				if(args.length!=2) {
					sender.sendMessage(String.format(main.text.getText(190),1));
					return true;
				}
				try {
					main.stufflg.role_stuff.get(RoleLG.values()[Integer.parseInt(args[1])]).clear();
					for(ItemStack i:((Player) sender).getInventory().getContents()) {
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
				sender.sendMessage(main.text.getText(153));
				break;
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
		String[] tabe = {"pregen","start","tpgroup","config","host","group","setgroup","fh","inv","role","revive","killa","disc","info","chat"};
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
