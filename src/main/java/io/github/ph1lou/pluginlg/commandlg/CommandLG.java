package io.github.ph1lou.pluginlg.commandlg;


import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.roles.*;
import io.github.ph1lou.pluginlg.commandlg.utilities.*;
import io.github.ph1lou.pluginlg.savelg.TextLG;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.*;


public class CommandLG implements TabExecutor {
    
    private final Map<String, Commands> listCommands = new HashMap<>();

    public CommandLG(MainLG main, TextLG text) {
        
        
        listCommands.put(text.getText(230), new CommandSeer(main));
        listCommands.put(text.getText(231), new CommandCupid(main));
        listCommands.put(text.getText(232), new CommandDetective(main));
        listCommands.put(text.getText(233), new CommandFallenAngel(main));
        listCommands.put(text.getText(234), new CommandFox(main));
        listCommands.put(text.getText(235), new CommandGuardianAngel(main));
        listCommands.put(text.getText(236), new CommandInfect(main));
        listCommands.put(text.getText(237), new CommandLovers(main));
        listCommands.put(text.getText(238), new CommandProtector(main));
        listCommands.put(text.getText(239), new CommandRaven(main));
        listCommands.put(text.getText(240), new CommandCitizenCancelVote(main));
        listCommands.put(text.getText(241), new CommandCitizenSeeVote(main));
        listCommands.put(text.getText(242), new CommandTroubleMaker(main));
        listCommands.put(text.getText(243), new CommandWereWolf(main));
        listCommands.put(text.getText(244), new CommandWildChild(main));
        listCommands.put(text.getText(245), new CommandCompo(main));
        listCommands.put(text.getText(246), new CommandComedian(main));
        listCommands.put("h", new CommandHelp(main));
        listCommands.put(text.getText(247), new CommandRole(main));
        listCommands.put(text.getText(248), new CommandRules(main));
        listCommands.put(text.getText(249), new CommandScenarios(main));
        listCommands.put(text.getText(250), new CommandStuff(main));
        listCommands.put(text.getText(251), new CommandTimer(main));
        listCommands.put(text.getText(252), new CommandVote(main));
        listCommands.put(text.getText(253), new CommandWitch(main));
        listCommands.put(text.getText(160), new CommandTrapper(main));
        listCommands.put(text.getText(110), new CommandAngelRegen(main));
        listCommands.put("join", new CommandJoin(main));
        listCommands.put("leave", new CommandLeave(main));
        listCommands.put("find", new CommandFind(main));
        listCommands.put(text.getText(65), new CommandSuccubus(main));
    }


	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (args.length == 0) return true;
        this.listCommands.getOrDefault(args[0], this.listCommands.get("h")).execute(sender, Arrays.copyOfRange(args, 1, args.length));
        return true;
    }

	@Override
	public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {

        List<String> temp = new ArrayList<>(this.listCommands.keySet());

        if (args.length == 0) {
            return temp;
        } else if (args.length == 1) {

            for (int i = 0; i < temp.size(); i++) {
                for (int j = 0; j < temp.get(i).length() && j < args[0].length(); j++) {
                    if (temp.get(i).charAt(j) != args[0].charAt(j)) {
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
