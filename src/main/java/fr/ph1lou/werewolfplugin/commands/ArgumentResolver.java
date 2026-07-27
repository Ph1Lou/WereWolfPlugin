package fr.ph1lou.werewolfplugin.commands;

import fr.ph1lou.werewolfapi.annotations.ChoiceArg;
import fr.ph1lou.werewolfapi.annotations.EnumArg;
import fr.ph1lou.werewolfapi.annotations.IntArg;
import fr.ph1lou.werewolfapi.annotations.Params;
import fr.ph1lou.werewolfapi.annotations.PlayerArg;
import fr.ph1lou.werewolfapi.annotations.PlayerBukkitArg;
import fr.ph1lou.werewolfapi.annotations.StringArg;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Valide et résout les paramètres d'entrée d'une commande décrits par {@link Params}.
 * Chaque type d'argument a sa propre annotation (et ses champs pertinents uniquement) ;
 * les cinq tableaux sont fusionnés par {@code index}.
 *
 * <p>{@code senderWW} (joueur exécuteur) est nécessaire pour les vérifications
 * {@code excludeSelf} et {@code maxDistanceKey} ; {@code null} pour les exécutants hors
 * partie (commandes admin), auquel cas ces deux vérifications sont ignorées.</p>
 */
public class ArgumentResolver {

    /** Exception interne : levée dès qu'une validation échoue (message déjà envoyé). */
    private static final class ResolveFailedException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

    /**
     * Résout tous les paramètres décrits dans {@code params} contre {@code args}.
     *
     * @param senderWW joueur exécuteur (pour excludeSelf / distance), {@code null} si inconnu/hors partie.
     * @return le contexte résolu, ou {@code empty} si un paramètre requis est invalide/absent.
     */
    public Optional<CommandContext> resolve(WereWolfAPI game,
                                            IPlayerWW senderWW,
                                            MessageSender sender,
                                            Params params,
                                            String[] args) {
        Map<Integer, Object> values = new HashMap<>();
        try {
            for (PlayerArg spec : params.players()) {
                if (shouldSkip(spec, args.length, sender)) {
                    continue;
                }
                values.put(spec.index(), resolvePlayer(game, senderWW, sender, spec, args[spec.index()]));
            }
            for (PlayerBukkitArg spec : params.bukkitPlayers()) {
                if (shouldSkip(spec, args.length, sender)) {
                    continue;
                }
                values.put(spec.index(), resolveBukkitPlayer(sender, spec, args[spec.index()]));
            }
            for (IntArg spec : params.integers()) {
                if (shouldSkip(spec, args.length, sender)) {
                    continue;
                }
                values.put(spec.index(), resolveInt(sender, spec, args[spec.index()]));
            }
            for (StringArg spec : params.strings()) {
                if (shouldSkip(spec, args.length, sender)) {
                    continue;
                }
                values.put(spec.index(), resolveString(sender, spec, args[spec.index()]));
            }
            for (EnumArg spec : params.enums()) {
                if (shouldSkip(spec, args.length, sender)) {
                    continue;
                }
                values.put(spec.index(), resolveEnum(sender, spec, args[spec.index()]));
            }
            for (ChoiceArg spec : params.choices()) {
                if (shouldSkip(spec, args.length, sender)) {
                    continue;
                }
                values.put(spec.index(), resolveChoice(sender, spec, args[spec.index()]));
            }
        } catch (ResolveFailedException e) {
            return Optional.empty();
        }
        return Optional.of(new CommandContextImpl(args, values));
    }

    private boolean shouldSkip(PlayerArg spec, int argsLength, MessageSender sender) {
        return shouldSkip(spec.index(), spec.required(), argsLength, sender);
    }

    private boolean shouldSkip(PlayerBukkitArg spec, int argsLength, MessageSender sender) {
        return shouldSkip(spec.index(), spec.required(), argsLength, sender);
    }

    private boolean shouldSkip(IntArg spec, int argsLength, MessageSender sender) {
        return shouldSkip(spec.index(), spec.required(), argsLength, sender);
    }

    private boolean shouldSkip(StringArg spec, int argsLength, MessageSender sender) {
        return shouldSkip(spec.index(), spec.required(), argsLength, sender);
    }

    private boolean shouldSkip(EnumArg spec, int argsLength, MessageSender sender) {
        return shouldSkip(spec.index(), spec.required(), argsLength, sender);
    }

    private boolean shouldSkip(ChoiceArg spec, int argsLength, MessageSender sender) {
        return shouldSkip(spec.index(), spec.required(), argsLength, sender);
    }

    private boolean shouldSkip(int index, boolean required, int argsLength, MessageSender sender) {
        if (index >= argsLength) {
            if (required) {
                fail(sender, "werewolf.check.parameters", Formatter.number(index + 1));
            }
            return true;
        }
        return false;
    }

    private Object resolvePlayer(WereWolfAPI game, IPlayerWW senderWW, MessageSender sender, PlayerArg spec, String raw) {
        UUID uuid;
        if (spec.byUuid()) {
            try {
                uuid = UUID.fromString(raw);
            } catch (IllegalArgumentException e) {
                fail(sender, "werewolf.check.not_in_game_player");
                return null; // unreachable
            }
        } else {
            Player bukkitPlayer = Bukkit.getPlayer(raw);
            if (bukkitPlayer == null) {
                fail(sender, "werewolf.check.offline_player");
            }
            uuid = bukkitPlayer.getUniqueId();
        }
        IPlayerWW target = game.getPlayerWW(uuid).orElse(null);
        if (target == null) {
            fail(sender, "werewolf.check.not_in_game_player");
        }
        if (spec.excludeSelf() && senderWW != null && senderWW.getUUID().equals(uuid)) {
            fail(sender, "werewolf.check.not_yourself");
        }
        StatePlayer[] states = spec.states();
        if (states.length > 0) {
            boolean ok = false;
            for (StatePlayer state : states) {
                if (target.isState(state)) {
                    ok = true;
                    break;
                }
            }
            if (!ok) {
                fail(sender, spec.stateMessageKey().isEmpty() ? "werewolf.check.state_player" : spec.stateMessageKey());
            }
        }
        if (spec.rejectIfAlreadyAffected() && senderWW != null) {
            IRole role = senderWW.getRole();
            if (role instanceof IAffectedPlayers
                    && ((IAffectedPlayers) role).getAffectedPlayers().contains(target)) {
                fail(sender, spec.alreadyAffectedMessageKey().isEmpty()
                        ? "werewolf.check.already_get_power" : spec.alreadyAffectedMessageKey());
            }
        }
        if (!spec.maxDistanceKey().isEmpty() && senderWW != null) {
            int maxDistance = game.getConfig().getValue(spec.maxDistanceKey());
            if (senderWW.distance(target) > maxDistance) {
                fail(sender, spec.maxDistanceMessageKey().isEmpty() ? "werewolf.check.distance" : spec.maxDistanceMessageKey(),
                        Formatter.number(maxDistance));
            }
        }
        return target;
    }

    private Object resolveBukkitPlayer(MessageSender sender, PlayerBukkitArg spec, String raw) {
        Player bukkitPlayer = Bukkit.getPlayer(raw);
        if (bukkitPlayer == null) {
            fail(sender, "werewolf.check.offline_player");
        }
        return bukkitPlayer;
    }

    private Object resolveInt(MessageSender sender, IntArg spec, String raw) {
        int value;
        try {
            value = Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            fail(sender, "werewolf.check.number_required");
            return null; // unreachable
        }
        if (value < spec.min() || value > spec.max()) {
            fail(sender, "werewolf.check.number_required");
        }
        return value;
    }

    private Object resolveString(MessageSender sender, StringArg spec, String raw) {
        if (spec.maxLength() >= 0 && raw.length() > spec.maxLength()) {
            fail(sender, "werewolf.check.parameters", Formatter.number(spec.maxLength()));
        }
        if (spec.choices().length > 0) {
            for (String choice : spec.choices()) {
                if (choice.equalsIgnoreCase(raw)) {
                    return raw;
                }
            }
            fail(sender, "werewolf.check.parameters", Formatter.number(spec.index() + 1));
        }
        return raw;
    }

    private Object resolveChoice(MessageSender sender, ChoiceArg spec, String raw) {
        for (String choice : spec.choices()) {
            if (choice.equalsIgnoreCase(raw)) {
                return raw;
            }
        }
        fail(sender, "werewolf.check.parameters", Formatter.number(spec.index() + 1));
        return null; // unreachable
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Object resolveEnum(MessageSender sender, EnumArg spec, String raw) {
        try {
            Class enumType = (Class) spec.enumClass();
            Enum.valueOf(enumType, raw);
            return raw;
        } catch (IllegalArgumentException e) {
            fail(sender, "werewolf.check.parameters", Formatter.number(spec.index() + 1));
            return null; // unreachable
        }
    }

    private void fail(MessageSender sender, String key, Formatter... formatters) {
        sender.send(Prefix.RED, key, formatters);
        throw new ResolveFailedException();
    }

    /**
     * Suggestions d'autocomplétion pour le paramètre à {@code index} (PLAYER → noms des
     * joueurs dans un état autorisé ; CHOICE/ENUM → valeurs autorisées ; sinon rien).
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public List<String> complete(WereWolfAPI game, Params params, int index, String partial) {
        String lower = partial.toLowerCase();
        for (PlayerArg spec : params.players()) {
            if (spec.index() == index) {
                if (spec.byUuid()) {
                    return Collections.emptyList();
                }
                StatePlayer[] states = spec.states();
                return game.getPlayersWW().stream()
                        .filter(p -> states.length == 0 || Arrays.stream(states).anyMatch(p::isState))
                        .map(IPlayerWW::getName)
                        .filter(n -> n.toLowerCase().startsWith(lower))
                        .collect(Collectors.toList());
            }
        }
        for (PlayerBukkitArg spec : params.bukkitPlayers()) {
            if (spec.index() == index) {
                return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(n -> n.toLowerCase().startsWith(lower))
                        .collect(Collectors.toList());
            }
        }
        for (ChoiceArg spec : params.choices()) {
            if (spec.index() == index) {
                return Arrays.stream(spec.choices())
                        .filter(c -> c.toLowerCase().startsWith(lower))
                        .collect(Collectors.toList());
            }
        }
        for (EnumArg spec : params.enums()) {
            if (spec.index() == index) {
                Object[] constants = spec.enumClass().getEnumConstants();
                if (constants == null) {
                    return Collections.emptyList();
                }
                return Arrays.stream(constants)
                        .map(Object::toString)
                        .filter(s -> s.toLowerCase().startsWith(lower))
                        .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }
}
