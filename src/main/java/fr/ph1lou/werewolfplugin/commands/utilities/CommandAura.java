package fr.ph1lou.werewolfplugin.commands.utilities;

import fr.ph1lou.werewolfapi.annotations.PlayerCommand;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfplugin.Register;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@PlayerCommand(key = "werewolf.commands.player.aura.command",
        descriptionKey = "werewolf.commands.player.aura.description",
        argNumbers = 0)
public class CommandAura implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        List<String> extraUseCaseAura = game.translateArray("werewolf.commands.player.aura.specific_use_cases");
        String sb = game.translate(Prefix.BLUE, "werewolf.commands.player.aura.prefix") +
                    "\n" +
                    getAura(game, Aura.LIGHT, extraUseCaseAura) +
                    getAura(game, Aura.DARK, extraUseCaseAura) +
                    getAura(game, Aura.NEUTRAL, extraUseCaseAura);

        player.sendMessage(sb);

        extraUseCaseAura.forEach(player::sendMessage);
    }

    public String getAura(WereWolfAPI game, Aura aura, List<String> extraUseCaseAura) {

        StringBuilder sb = new StringBuilder(aura.getChatColor() + game.translate(aura.getKey()));
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        sb.append("§f : ");

        Register.get().getRolesRegister().stream()
                .filter(roleRegister -> roleRegister.getMetaDatas()
                                                .defaultAura() == aura)
                .forEach(roleRegister -> {
                    String key = roleRegister.getMetaDatas().key();

                    if (game.getConfig().isConfigActive(ConfigBase.HIDE_COMPOSITION) || game.getConfig().getRoleCount(key) > 0) {
                        String auraDescriptionSpecialUseCase = roleRegister.getMetaDatas().auraDescriptionSpecialUseCase();
                        if(!auraDescriptionSpecialUseCase.isEmpty()){
                            extraUseCaseAura.add(game.translate(auraDescriptionSpecialUseCase));
                        }
                        sb.append(game.translate(roleRegister.getMetaDatas().key()))
                                .append(", ");
                        atomicBoolean.set(true);
                    }
                });
        sb.replace(sb.length() - 2, sb.length(), "");
        sb.append("\n");
        if (!atomicBoolean.get()) {
            return "";
        }
        return sb.toString();
    }
}
