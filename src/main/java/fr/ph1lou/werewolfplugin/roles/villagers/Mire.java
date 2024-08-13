package fr.ph1lou.werewolfplugin.roles.villagers;

import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.roles.mire.MireNearbyPlayerUnderThreeHeartEvent;
import fr.ph1lou.werewolfapi.events.roles.mire.MireUnderThreeHeartsEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleImpl;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import fr.ph1lou.werewolfapi.enums.UniversalPotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Role(key = RoleBase.MIRE,
        category = Category.VILLAGER,
        attribute = RoleAttribute.MINOR_INFORMATION,
        configValues = @IntValue(
                key = IntValueBase.MIRE_DISTANCE,
                defaultValue = 60,
                meetUpValue = 60,
                step = 5,
                item = UniversalMaterial.SKELETON_SKULL
        )
)
public class Mire extends RoleImpl {

    public Mire(WereWolfAPI game, IPlayerWW playerWW) {
        super(game, playerWW);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.mire.description",
                        Formatter.number(game.getConfig().getValue(IntValueBase.MIRE_DISTANCE))))
                .setEffects(game.translate("werewolf.roles.mire.effect"))
                .build();
    }

    @EventHandler
    public void playerDamage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();

        IPlayerWW playerWW = game.getPlayerWW(player.getUniqueId()).orElse(null);

        if (playerWW == null) return;

        if (!this.game.isState(StateGame.GAME)) {
            return;
        }

        if (!playerWW.isState(StatePlayer.ALIVE)) return;

        if (playerWW.getHealth() >= 6) return;

        if (playerWW.equals(getPlayerWW())) {

            MireUnderThreeHeartsEvent event1 = new MireUnderThreeHeartsEvent(this.getPlayerWW());

            Bukkit.getPluginManager().callEvent(event1);

            if (!event1.isCancelled()) {
                playerWW.addPotionModifier(
                        //TODO : FAIRE CONFIG POUR AJOUTER QU'UN COEUR
                        PotionModifier.add(UniversalPotionEffectType.ABSORPTION, 600, 0, this.getKey()));
            } else {
                this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            }

        } else {

            if (getPlayerWW().getLocation().getWorld() == playerWW.getLocation().getWorld() &&
                    getPlayerWW().getLocation().distance(playerWW.getLocation()) <
                            game.getConfig().getValue(IntValueBase.MIRE_DISTANCE)) {

                MireNearbyPlayerUnderThreeHeartEvent event1 = new MireNearbyPlayerUnderThreeHeartEvent(this.getPlayerWW(), playerWW);

                Bukkit.getPluginManager().callEvent(event1);

                if (!event1.isCancelled()) {
                    getPlayerWW().sendMessageWithKey(Prefix.ORANGE, "werewolf.roles.mire.warning_death",
                            Formatter.number(game.getConfig().getValue(IntValueBase.MIRE_DISTANCE)));
                } else {
                    this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
                }
            }
        }
    }

    @EventHandler
    public void eatGoldenApple(PlayerItemConsumeEvent event) {
        if (!event.getItem().getType().equals(Material.GOLDEN_APPLE)) return;

        if (!event.getPlayer().getUniqueId().equals(getPlayerUUID())) return;

        List<IPlayerWW> playerWWS = Bukkit.getOnlinePlayers()
                .stream()
                .map(Entity::getUniqueId)
                .map(game::getPlayerWW)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .sorted(Comparator.comparingDouble(value -> value.getHealth() / value.getMaxHealth()))
                .collect(Collectors.toList());

        if (playerWWS.isEmpty()) {
            getPlayerWW().sendMessageWithKey(Prefix.ORANGE, "werewolf.roles.mire.all_full",
                    Formatter.number(game.getConfig().getValue(IntValueBase.MIRE_DISTANCE)));
        } else {
            IPlayerWW playerWW = playerWWS.get(0);
            MireNearbyPlayerUnderThreeHeartEvent event1 =
                    new MireNearbyPlayerUnderThreeHeartEvent(this.getPlayerWW(), playerWW);

            Bukkit.getPluginManager().callEvent(event1);

            if (!event1.isCancelled()) {
                getPlayerWW().sendMessageWithKey(Prefix.ORANGE, "werewolf.roles.mire.min_health",
                        Formatter.player(playerWW.getName()));
            }
        }
    }

    @Override
    public void recoverPower() {

    }
}
