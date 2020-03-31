package io.github.ph1lou.pluginlg.commandlg;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.roles.*;
import io.github.ph1lou.pluginlg.commandlg.utilities.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CommandLG implements TabExecutor {
	
	final MainLG main;
	private final List<Commands> listCommands=new ArrayList<>();
	private final List<String> tab = new ArrayList<>();

	public CommandLG(MainLG main) {
		this.main=main;
		listCommands.add(new CommandSeer(main,main.text.getText(230)));
		listCommands.add(new CommandCupid(main,main.text.getText(231)));
		listCommands.add(new CommandDetective(main,main.text.getText(232)));
		listCommands.add(new CommandFallenAngel(main,main.text.getText(233)));
		listCommands.add(new CommandFox(main,main.text.getText(234)));
		listCommands.add(new CommandGuardianAngel(main,main.text.getText(235)));
		listCommands.add(new CommandInfect(main,main.text.getText(236)));
		listCommands.add(new CommandLovers(main,main.text.getText(237)));
		listCommands.add(new CommandProtector(main,main.text.getText(238)));
		listCommands.add(new CommandRaven(main,main.text.getText(239)));
		listCommands.add(new CommandCitizenCancelVote(main,main.text.getText(240)));
		listCommands.add(new CommandCitizenSeeVote(main,main.text.getText(241)));
		listCommands.add(new CommandTroubleMaker(main,main.text.getText(242)));
		listCommands.add(new CommandWereWolf(main,main.text.getText(243)));
		listCommands.add(new CommandWildChild(main,main.text.getText(244)));
		listCommands.add(new CommandCompo(main,main.text.getText(245)));
		listCommands.add(new CommandHelp(main,main.text.getText(246)));
		listCommands.add(new CommandRole(main,main.text.getText(247)));
		listCommands.add(new CommandRules(main,main.text.getText(248)));
		listCommands.add(new CommandScenarios(main,main.text.getText(249)));
		listCommands.add(new CommandStuff(main,main.text.getText(250)));
		listCommands.add(new CommandTimer(main,main.text.getText(251)));
		listCommands.add(new CommandVote(main,main.text.getText(252)));
		listCommands.add(new CommandWitch(main,main.text.getText(253)));
		for(Commands c:listCommands){
			tab.add(c.getName());
		}
	}


	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(args.length==0) return true;
		
		for(Commands c:listCommands){
			if(c.getName().equals(args[0])){
				c.execute(sender,Arrays.copyOfRange(args,1,args.length));
				return true;
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {

		List<String> temp = new ArrayList<>(tab);
		
		if(args.length==0){
			return temp;
		}
		else if(args.length==1){

			for(int i=0;i<temp.size();i++){
				for(int j=0;j<temp.get(i).length() && j<args[0].length();j++){
					if(temp.get(i).charAt(j)!=args[0].charAt(j)){
						temp.remove(i);
						i--;
						break;
					}
				}
			}
			return temp;
		}
		return null;
	}
}
