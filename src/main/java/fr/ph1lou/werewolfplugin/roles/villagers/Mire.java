package fr.ph1lou.werewolfplugin.roles.villagers;

import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.*;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleVillage;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Role(key = RoleBase.MIRE,
        category = Category.VILLAGER,
        attributes = {RoleAttribute.VILLAGER, RoleAttribute.MINOR_INFORMATION},
        configValues = @IntValue(
                key = IntValueBase.MIRE_DISTANCE,
                defaultValue = 60,
                meetUpValue = 60,
                step = 5,
                item = UniversalMaterial.SKELETON_SKULL
        )
)
public class Mire extends RoleVillage {
    private IPlayerWW lowLife;
    private double life;

    public Mire(WereWolfAPI game, IPlayerWW playerWW) {
        super(game, playerWW);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.mire.desc"))
                .build();
    }

    @EventHandler
    public void playerDamage(EntityDamageEvent event) {
        if(!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        IPlayerWW playerWW = game.getPlayerWW(player.getUniqueId()).orElse(null);

        if(playerWW == null) return;

        if(!playerWW.isState(StatePlayer.ALIVE)) return;

        if(playerWW.getHealth() > 6) return;

        if(playerWW.equals(getPlayerWW())) {
            playerWW.addPotionModifier(
                    //TODO : FAIRE CONFIG POUR AJOUTER QU'UN COEUR
                    PotionModifier.add(PotionEffectType.ABSORPTION, 600, 0, "mire"));
        } else {
            if((getPlayerWW().getLocation().distance(playerWW.getLocation()) < game.getConfig().getValue(IntValueBase.MIRE_DISTANCE))) {
                getPlayerWW().sendMessageWithKey(Prefix.ORANGE, "werewolf.roles.mire.warning_death");
            }
        }
    }

    @EventHandler
    public void eatGoldenApple(PlayerItemConsumeEvent event) {
        if (!event.getItem().getType().equals(Material.GOLDEN_APPLE)) return;

        if(!event.getPlayer().getUniqueId().equals(getPlayerUUID())) return;

        List<IPlayerWW> playerWWS = Bukkit.getOnlinePlayers()
                .stream()
                .map(Entity::getUniqueId)
                .map(game::getPlayerWW)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .map(IPlayerWW::getRole)
                .map(IRole::getPlayerWW)
                .collect(Collectors.toList());

        lowLife = null;
        life = 100;
        playerWWS.forEach(iPlayerWW -> {
            double vie = iPlayerWW.getHealth() / iPlayerWW.getMaxHealth() * 100;
            if (vie > life) return;

            life = vie;
            lowLife = iPlayerWW;
        });

        if(lowLife == null)
            getPlayerWW().sendMessageWithKey(Prefix.ORANGE, "werewolf.roles.mire.all_full");
        else
            getPlayerWW().sendMessageWithKey(Prefix.ORANGE, "werewolf.roles.mire.min_health", Formatter.player(lowLife.getName()));
    }

    @Override
    public void recoverPower() {

    }
}
