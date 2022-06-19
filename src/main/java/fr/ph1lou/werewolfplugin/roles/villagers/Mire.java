package fr.ph1lou.werewolfplugin.roles.villagers;

import fr.ph1lou.werewolfapi.enums.Camp;
import fr.ph1lou.werewolfapi.enums.Prefix;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
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

public class Mire extends RoleVillage {
    private IPlayerWW lowLife;
    private double life;

    public Mire(WereWolfAPI game, IPlayerWW playerWW, String key) {
        super(game, playerWW, key);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.role.mire.desc"))
                .build();
    }

    @EventHandler
    public void playerDamage(EntityDamageEvent event) {
        if(!(event.getEntity() instanceof Player)) return;
        Bukkit.broadcastMessage("C'est bien un joueur!");

        Player player = (Player) event.getEntity();
        IPlayerWW playerWW = game.getPlayerWW(player.getUniqueId()).orElse(null);

        assert playerWW != null;
        if(playerWW.getHealth() > 6) return;
        Bukkit.broadcastMessage("Il est en dessous de 3 coeurs.");

        if(playerWW == getPlayerWW()) {
            playerWW.addPotionModifier(
                    PotionModifier.add(PotionEffectType.ABSORPTION, 600, 0, "mire"));
            Bukkit.broadcastMessage("C'est le joueur qui détiens le role.");
        } else {
            if((getPlayerWW().getLocation().distance(playerWW.getLocation()) < 60)) {
                getPlayerWW().sendMessageWithKey(Prefix.ORANGE.getKey(), "werewolf.role.mire.warning_death");
            }
        }
    }

    @EventHandler
    public void eatGoldenApple(PlayerItemConsumeEvent event) {
        if (!event.getItem().getType().equals(Material.GOLDEN_APPLE)) return;

        List<IPlayerWW> playerWWS = Bukkit.getOnlinePlayers()
                .stream()
                .map(Entity::getUniqueId)
                .map(game::getPlayerWW)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .map(IPlayerWW::getRole)
                .filter(roles -> roles.isDisplayCamp(Camp.WEREWOLF.getKey()))
                .map(IRole::getPlayerWW)
                .collect(Collectors.toList());

        life = 100;
        playerWWS.forEach(iPlayerWW -> {
            double vie = iPlayerWW.getHealth() / iPlayerWW.getMaxHealth() * 100;
            if (vie > life) return;

            life = vie;
            lowLife = iPlayerWW;
        });

        getPlayerWW().sendMessageWithKey(Prefix.ORANGE.getKey(), "werewolf.role.mire.min_health", Formatter.player(lowLife.getName()));
    }

    @Override
    public void recoverPower() {

    }
}
