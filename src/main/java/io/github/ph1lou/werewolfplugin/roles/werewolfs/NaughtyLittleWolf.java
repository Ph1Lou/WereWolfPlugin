package io.github.ph1lou.werewolfplugin.roles.werewolfs;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesWereWolf;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class NaughtyLittleWolf extends RolesWereWolf {

    public NaughtyLittleWolf(GetWereWolfAPI main, PlayerWW playerWW, String key) {
        super(main, playerWW, key);
    }


    @Override
    public @NotNull String getDescription() {
        return super.getDescription().replace(game.translate("werewolf.description.werewolf"), "") +
                game.translate("werewolf.role.naughty_little_wolf.effect");
    }


    @Override
    public void recoverPower() {

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeathByNaughtyWereWolf(PlayerDeathEvent event) {

        if (!isWereWolf()) return;

        if (event.getEntity().getKiller() == null) return;

        Player killer = event.getEntity().getKiller();

        if (!getPlayerUUID().equals(killer.getUniqueId())) return;

        killer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 3600, 0, false, false));
    }
}
