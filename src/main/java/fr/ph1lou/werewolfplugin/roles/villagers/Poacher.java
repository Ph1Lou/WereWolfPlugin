package fr.ph1lou.werewolfplugin.roles.villagers;


import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.ActionBarEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.AnnouncementDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.poacher.PoacherRecoverFurEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleVillage;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.utils.Utils;
import fr.ph1lou.werewolfplugin.Register;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Role(key = RoleBase.POACHER, category = Category.VILLAGER,
        attributes = RoleAttribute.VILLAGER,
        timers = {@Timer(key = TimerBase.POACHER_PROGRESS, defaultValue = 10, meetUpValue = 10)},
        configValues = {
                @IntValue(key = IntValueBase.POACHER_DISTANCE, defaultValue = 10, meetUpValue = 10, step = 1, item = UniversalMaterial.ARROW)
})
public class Poacher extends RoleVillage {

    private int furNumbers = 0;
    private final List<IPlayerWW> affectedPlayer = new ArrayList<>();
    private int progress = 0;
    @Nullable
    private IPlayerWW playerWW;

    public Poacher(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeathByFox(AnnouncementDeathEvent event) {

        if(!event.getTargetPlayer().equals(this.getPlayerWW())){
            return;
        }

        Register.get().getCategory(event.getRole()).ifPresent(category -> {
            if(event.getPlayerWW().getRole().isWereWolf() || category == Category.WEREWOLF){
                this.affectedPlayer.add(event.getPlayerWW());
            }
        });
    }

    @Override
    public Aura getDefaultAura() {
        return Aura.NEUTRAL;
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.poacher.description",
                        Formatter.number(game.getConfig().getValue(IntValueBase.POACHER_DISTANCE)),
                                Formatter.timer(game, TimerBase.POACHER_PROGRESS)))
                        .setEffects(game.translate("werewolf.roles.poacher.effects"))
                .setPower(game.translate("werewolf.roles.poacher.fur_numbers",
                        Formatter.number(this.furNumbers)))
                .build();
    }


    @Override
    public void recoverPower() {

    }

    @Override
    public String getDisplayCamp() {

        if(this.furNumbers > 0 || this.isWereWolf()){
            return Category.WEREWOLF.getKey();
        }
        return Category.VILLAGER.getKey();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onActionBarGameLoverEvent(ActionBarEvent event) {

        if(this.playerWW == null){
            return;
        }

        UUID uuid = event.getPlayerUUID();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (!this.getPlayerWW().equals(playerWW)) return;

        if(!this.getPlayerWW().isState(StatePlayer.ALIVE)){
            return;
        }

        StringBuilder sb = new StringBuilder(event.getActionBar());

        sb.insert(0, game.translate("werewolf.roles.poacher.actionbar"));
        sb.append(game.translate("werewolf.roles.poacher.actionbar"));
        event.setActionBar(sb.toString());
    }

    @Override
    public void second() {

        if(!this.getPlayerWW().isState(StatePlayer.ALIVE)){
            return;
        }

        if(this.affectedPlayer.isEmpty()){
            return;
        }

        if(this.affectedPlayer
                .stream()
                .anyMatch(playerWW -> {
                    if(Utils.compareDistance(game, playerWW.getDeathLocation(),
                            this.getPlayerWW().getLocation(),
                            IntValueBase.POACHER_DISTANCE)){
                        this.playerWW = playerWW;
                        return true;
                    }
                    return false;
                })){
            this.progress++;
        }
        else{
            this.progress = 0;
            this.playerWW = null;
        }

        if(this.progress == game.getConfig().getTimerValue(TimerBase.POACHER_PROGRESS)){
            this.progress = 0;
            if(this.playerWW != null){
                this.affectedPlayer.remove(this.playerWW);

                PoacherRecoverFurEvent event = new PoacherRecoverFurEvent(this.getPlayerWW(),this.furNumbers+1);

                Bukkit.getPluginManager().callEvent(event);

                if(event.isCancelled()){
                    this.playerWW = null;
                    this.getPlayerWW().sendMessageWithKey(Prefix.RED , "werewolf.check.cancel");
                    return;
                }

                this.getPlayerWW().sendMessageWithKey(Prefix.GREEN, "werewolf.roles.poacher.recover",
                        Formatter.player(this.playerWW.getName()));

                this.furNumbers++;
                this.recoverPotionEffect();
                this.playerWW = null;

            }
        }
    }

    @Override
    public void recoverPotionEffect() {

        if(this.furNumbers > 0){
            this.getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.DAMAGE_RESISTANCE,
                    this.getKey()));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPatchPotion(EntityDamageByEntityEvent event) {

        if(this.furNumbers <= 1) return;

        if (!(event.getEntity() instanceof Player)) return;

        if (!(event.getDamager() instanceof Player)) return;

        Player player = (Player) event.getEntity();

        if (player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
            event.setDamage(event.getDamage() * (1 - 0.03d * (this.furNumbers - 1)));
        }
    }
}
