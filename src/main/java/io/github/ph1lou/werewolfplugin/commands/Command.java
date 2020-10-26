package io.github.ph1lou.werewolfplugin.commands;


import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.commands.roles.*;
import io.github.ph1lou.werewolfplugin.commands.utilities.*;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.*;


public class Command implements TabExecutor {

    private final Map<String, Commands> listCommands = new HashMap<>();

    public Command(Main main, GameManager game) {

        listCommands.put(game.translate("werewolf.role.seer.command"), new CommandSeer(main));
        listCommands.put(game.translate("werewolf.role.cupid.command"), new CommandCupid(main));
        listCommands.put(game.translate("werewolf.role.detective.command"), new CommandDetective(main));
        listCommands.put(game.translate("werewolf.role.angel.command_2"), new CommandFallenAngel(main));
        listCommands.put(game.translate("werewolf.role.fox.command"), new CommandFox(main));
        listCommands.put(game.translate("werewolf.role.angel.command_1"), new CommandGuardianAngel(main));
        listCommands.put(game.translate("werewolf.role.infect_father_of_the_wolves.command"), new CommandInfect(main));
        listCommands.put(game.translate("werewolf.role.lover.command"), new CommandLovers(main));
        listCommands.put(game.translate("werewolf.role.protector.command"), new CommandProtector(main));
        listCommands.put(game.translate("werewolf.role.raven.command"), new CommandRaven(main));
        listCommands.put(game.translate("werewolf.role.citizen.command_2"), new CommandCitizenCancelVote(main));
        listCommands.put(game.translate("werewolf.role.citizen.command_1"), new CommandCitizenSeeVote(main));
        listCommands.put(game.translate("werewolf.role.troublemaker.command"), new CommandTroubleMaker(main));
        listCommands.put(game.translate("werewolf.role.werewolf.command"), new CommandWereWolf(main));
        listCommands.put(game.translate("werewolf.role.wild_child.command"), new CommandWildChild(main));
        listCommands.put(game.translate("werewolf.menu.roles.command_2"), new CommandCompo(main));
        listCommands.put(game.translate("werewolf.role.comedian.command"), new CommandComedian(main));
        listCommands.put("h", new CommandHelp(main));
        listCommands.put("?", new CommandAnonymeChat(main));
        listCommands.put(game.translate("werewolf.menu.roles.command_1"), new CommandRole(main));
        listCommands.put(game.translate("werewolf.menu.global.command"), new CommandRules(main));
        listCommands.put(game.translate("werewolf.menu.scenarios.command"), new CommandScenarios(main));
        listCommands.put(game.translate("werewolf.menu.enchantments.command"), new CommandEnchantment(main));
        listCommands.put(game.translate("werewolf.menu.timers.command"), new CommandTimer(main));
        listCommands.put(game.translate("werewolf.vote.command"), new CommandVote(main));
        listCommands.put(game.translate("werewolf.role.witch.command"), new CommandWitch(main));
        listCommands.put(game.translate("werewolf.role.trapper.command"), new CommandTrapper(main));
        listCommands.put(game.translate("werewolf.role.guardian_angel.command"), new CommandAngelRegen(main));
        listCommands.put(game.translate("werewolf.role.succubus.command"), new CommandSuccubus(main));
        listCommands.put(game.translate("werewolf.role.flute_player.command"), new CommandFlutePlayer(main));
        listCommands.put(game.translate("werewolf.role.librarian.command"), new CommandLibrarian(main));
        listCommands.put(game.translate("werewolf.role.librarian.request_command"), new CommandSendToLibrarian(main));
        listCommands.put(game.translate("werewolf.menu.rank.command"), new CommandRank(main));

        listCommands.putAll(main.getListCommands());
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command cmd, @NotNull String label, String[] args) {

        if (args.length == 0) {
            this.listCommands.get("h").execute(sender, null);
        } else
            this.listCommands.getOrDefault(args[0], this.listCommands.get("h")).execute(sender, Arrays.copyOfRange(args, 1, args.length));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull org.bukkit.command.Command command, @NotNull String s, String[] args) {

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
