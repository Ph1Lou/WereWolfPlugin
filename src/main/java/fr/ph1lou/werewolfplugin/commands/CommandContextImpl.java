package fr.ph1lou.werewolfplugin.commands;

import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * Implémentation prérésolue de {@link CommandContext}. Toutes les valeurs des
 * paramètres déclarés via {@link fr.ph1lou.werewolfapi.annotations.Params} sont
 * calculées à l'avance par {@link ArgumentResolver} et stockées dans une map
 * indexée par la position du paramètre.
 */
public class CommandContextImpl implements CommandContext {

    private final String[] args;
    private final Map<Integer, Object> values;

    public CommandContextImpl(String[] args, Map<Integer, Object> values) {
        this.args = args;
        this.values = values;
    }

    @Override
    public String[] args() {
        return args;
    }

    @Override
    public boolean has(int index) {
        return values.containsKey(index);
    }

    @Override
    public IPlayerWW player(int index) {
        return (IPlayerWW) values.get(index);
    }

    @Override
    public Player bukkitPlayer(int index) {
        return (Player) values.get(index);
    }

    @Override
    public int intValue(int index) {
        return (Integer) values.get(index);
    }

    @Override
    public String string(int index) {
        return (String) values.get(index);
    }

    @Override
    public <E extends Enum<E>> E enumValue(int index, Class<E> enumClass) {
        return Enum.valueOf(enumClass, (String) values.get(index));
    }
}
