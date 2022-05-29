package fr.ph1lou.werewolfplugin.commands.utilities;

import fr.ph1lou.werewolfapi.annotations.PlayerCommand;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.registers.IRegisterManager;
import fr.ph1lou.werewolfapi.utils.Wrapper;
import fr.ph1lou.werewolfplugin.Register;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

@PlayerCommand(key = "werewolf.commands.player.help.command",
        descriptionKey = "")
public class CommandHelp implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        IRegisterManager registerManager = Register.get();

        TextComponent textComponent1 = new TextComponent(game.translate(Prefix.GREEN , "werewolf.commands.admin.help.help"));

        for (Wrapper<ICommand, PlayerCommand> command : registerManager.getPlayerCommandsRegister()) {
            if (!command.getMetaDatas().descriptionKey().isEmpty()) {

                TextComponent textComponent = new TextComponent(
                        String.format("/ww §b%s ",
                                game.translate(command.getMetaDatas().key())));

                textComponent.setHoverEvent(
                        new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                new ComponentBuilder(
                                        game.translate(command.getMetaDatas().key()))
                                        .create()));
                textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,String.format("/ww %s ",
                        game.translate(command.getMetaDatas().key()))));
                textComponent1.addExtra(textComponent);
            }

        }

        player.spigot().sendMessage(textComponent1);
    }
}
