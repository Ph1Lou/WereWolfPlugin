package fr.ph1lou.werewolfplugin.roles.villagers;


import fr.ph1lou.werewolfapi.annotations.RequireAbility;
import fr.ph1lou.werewolfapi.annotations.RequireAlive;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.UniversalEnchantment;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.game.timers.WereWolfListEvent;
import fr.ph1lou.werewolfapi.events.game.utils.EnchantmentEvent;
import fr.ph1lou.werewolfapi.events.roles.trouble_maker.TroubleMakerDeathEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.role.impl.RoleImpl;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPowers;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.role.utils.Powers;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Role(key = RoleBase.TROUBLEMAKER,
        defaultAura = Aura.NEUTRAL,
        category = Category.VILLAGER,
        attribute = RoleAttribute.VILLAGER)
public class Troublemaker extends RoleImpl implements IAffectedPlayers, IPowers {

    // Clés des pouvoirs nommés (servent aussi de clés de traduction du nom affiché
    // dans la commande admin role). Toujours garder en minuscules (cf. LanguageLoader).
    public static final String POWER_SWITCH = "werewolf.roles.troublemaker.powers.switch";
    public static final String POWER_TPALL = "werewolf.roles.troublemaker.powers.tpall";
    public static final String POWER_TP = "werewolf.roles.troublemaker.powers.tp";

    private final List<IPlayerWW> affectedPlayer = new ArrayList<>();
    private final Powers powers = new Powers();

    // Fenêtre du /lg switch : fermée à la révélation de la liste des loups (état de partie, pas un pouvoir).
    private boolean wwListRevealed = false;
    // Garde-fou : la condition de victoire se décide une seule fois à l'annonce.
    private boolean soloAssigned = false;

    public Troublemaker(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
        powers.add(POWER_SWITCH).add(POWER_TPALL).add(POWER_TP);
    }

    @EventHandler
    @RequireAbility
    public void onFinalDeath(FinalDeathEvent event) {

        if (!event.getPlayerWW().equals(getPlayerWW())) return;

        Bukkit.getPluginManager().callEvent(new TroubleMakerDeathEvent(getPlayerWW()));

        // Dispersion automatique à la mort uniquement si le /lg tpall n'a pas été utilisé.
        if (!hasPower(POWER_TPALL)) return;

        Bukkit.broadcastMessage(game.translate(Prefix.YELLOW, "werewolf.roles.troublemaker.troublemaker_death"));

        AtomicInteger i = new AtomicInteger();

        game.getAlivePlayersWW()
                .forEach(playerWW -> {
                    game.getMapManager().transportation(playerWW,
                            i.get() * 2 * Math.PI / game.getPlayersCount());
                    i.getAndIncrement();
                });
    }

    /**
     * Décision aléatoire de la condition de victoire à l'annonce des rôles :
     * gagner avec le Village (par défaut) ou seul (solitaire).
     */
    @Override
    public void recoverPower() {

        if (this.soloAssigned) return;
        this.soloAssigned = true;

        boolean solo = game.getRandom().nextBoolean();
        this.setSolitary(solo);

        if (solo) {
            getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.troublemaker.solo");
        } else {
            getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.troublemaker.village");
        }
    }

    /**
     * Ferme la fenêtre du /lg switch une fois la liste des loups révélée.
     */
    @EventHandler
    @RequireAbility
    public void onWereWolfList(WereWolfListEvent event) {
        this.wwListRevealed = true;
    }

    /**
     * Mode solitaire : +1 Sharpness sur l'épée enchantée (table/enclume),
     * via le système {@link EnchantmentEvent} (cf. rôle Fencer).
     */
    @EventHandler
    @RequireAlive
    public void onEnchantment(EnchantmentEvent event) {

        if (!this.isSolitary()) return;
        if (!event.getPlayerWW().equals(getPlayerWW())) return;
        if (!event.getEnchants().containsKey(UniversalEnchantment.SHARPNESS)) return;

        int rolled = event.getEnchants().get(UniversalEnchantment.SHARPNESS);
        Material type = event.getItem().getType();
        int limit = type.equals(Material.DIAMOND_SWORD)
                ? game.getConfig().getLimitSharpnessDiamond()
                : game.getConfig().getLimitSharpnessIron();

        event.getFinalEnchants().put(UniversalEnchantment.SHARPNESS, Math.min(rolled, limit + 1));
    }

    @Override
    public void addAffectedPlayer(IPlayerWW playerWW) {
        this.affectedPlayer.add(playerWW);
    }

    @Override
    public void removeAffectedPlayer(IPlayerWW playerWW) {
        this.affectedPlayer.remove(playerWW);
    }

    @Override
    public void clearAffectedPlayer() {
        this.affectedPlayer.clear();
    }

    @Override
    public List<IPlayerWW> getAffectedPlayers() {
        return (this.affectedPlayer);
    }

    // --- Pouvoirs nommés (cf. IPowers) ---

    @Override
    public Powers getPowers() {
        return powers;
    }

    public boolean isWwListRevealed() {
        return wwListRevealed;
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.troublemaker.description"))
                .setPower(game.translate("werewolf.roles.troublemaker.chat"))
                .addExtraLines(() -> game.translate("werewolf.roles.troublemaker.power_solo"), this.isSolitary())
                .build();
    }
}
