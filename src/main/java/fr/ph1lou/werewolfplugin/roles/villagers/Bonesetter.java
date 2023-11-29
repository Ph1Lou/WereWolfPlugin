package fr.ph1lou.werewolfplugin.roles.villagers;

import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.bonesetter.BonesetterHealEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.role.impl.RoleVillage;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.ILimitedUse;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Role(key = RoleBase.BONESETTER,
        category = Category.VILLAGER,
        attributes = RoleAttribute.VILLAGER)
public class Bonesetter extends RoleVillage implements IAffectedPlayers, ILimitedUse {
    private final List<IPlayerWW> affectedPlayers = new ArrayList<>();
    private final List<IPlayerWW> alreadyUsed = new ArrayList<>();
    private int use = 0;

    public Bonesetter(WereWolfAPI game, IPlayerWW playerWW) {
        super(game, playerWW);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(this.game, this)
                .setDescription(this.game.translate("werewolf.roles.bonesetter.description"))
                .setCommand(game.translate("werewolf.roles.bonesetter.command_description"))
                .build();
    }

    @Override
    public void recoverPower() {

    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (!isAbilityEnabled()) return;

        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();

        IPlayerWW playerWW = game.getPlayerWW(player.getUniqueId()).orElse(null);

        if (playerWW == null) return;

        if (!affectedPlayers.contains(playerWW)) return;

        if (!game.isState(StateGame.GAME)) {
            return;
        }

        if (!playerWW.isState(StatePlayer.ALIVE)) return;

        if (playerWW.getHealth() - event.getFinalDamage() > 8) return;

        BonesetterHealEvent bonesetterHealEvent = new BonesetterHealEvent(getPlayerWW(), playerWW);
        Bukkit.getPluginManager().callEvent(bonesetterHealEvent);
        if (!bonesetterHealEvent.isCancelled()) {
            //25 ticks = 1/2 heart
            playerWW.addPotionModifier(PotionModifier.add(PotionEffectType.REGENERATION, 6 * 25, 1, getKey()));
            playerWW.sendMessageWithKey(Prefix.GREEN, "werewolf.roles.bonesetter.receive_heal");
            getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.bonesetter.activate");
            affectedPlayers.remove(playerWW);
        }
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getPlayerUUID())) return;

        if (event.getItem().getType().equals(Material.GOLDEN_APPLE)) {
            getPlayerWW().addPlayerHealth(2);
        }
    }

    @Override
    public Aura getDefaultAura() {
        return Aura.NEUTRAL;
    }

    @Override
    public void addAffectedPlayer(IPlayerWW iPlayerWW) {
        this.affectedPlayers.add(iPlayerWW);
        this.alreadyUsed.add(iPlayerWW);
    }

    @Override
    public void removeAffectedPlayer(IPlayerWW iPlayerWW) {
        this.affectedPlayers.remove(iPlayerWW);
    }

    @Override
    public void clearAffectedPlayer() {
        this.affectedPlayers.clear();
    }

    @Override
    public List<? extends IPlayerWW> getAffectedPlayers() {
        return this.affectedPlayers;
    }

    @Override
    public int getUse() {
        return use;
    }

    @Override
    public void setUse(int i) {
        use = i;
    }

    public List<IPlayerWW> getAlreadyUsed() {
        return alreadyUsed;
    }
}
