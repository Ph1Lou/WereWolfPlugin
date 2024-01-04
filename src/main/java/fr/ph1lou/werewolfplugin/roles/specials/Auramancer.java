package fr.ph1lou.werewolfplugin.roles.specials;

import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FirstDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.auramancer.AuramancerChangeAuraEvent;
import fr.ph1lou.werewolfapi.events.roles.auramancer.AuramancerEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.AuraModifier;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IAuraModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleImpl;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfplugin.listeners.DeathListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Role(key = RoleBase.AURAMANCER,
        defaultAura = Aura.NEUTRAL,
        category = Category.VILLAGER,
        attributes = RoleAttribute.HYBRID,
configValues = @IntValue(key = IntValueBase.AURAMANCER_DISTANCE,
        defaultValue = 50,
        meetUpValue = 25,
        step = 2,
        item = UniversalMaterial.BED))
public class Auramancer extends RoleImpl {

    private boolean auraLocked = false;
    private IPlayerWW knownPlayer = null;

    public Auramancer(@NotNull WereWolfAPI game, @NotNull IPlayerWW playerWW) {
        super(game, playerWW);
    }

    @Override
    public @NotNull String getDescription() {

        DescriptionBuilder builder = new DescriptionBuilder(game, this).setDescription(game.translate("werewolf.roles.auramancer.description",
                Formatter.format("&light&", Aura.LIGHT.getChatColor() + game.translate(Aura.LIGHT.getKey())),
                        Formatter.format("&dark&", Aura.DARK.getChatColor() + game.translate(Aura.DARK.getKey()))));
        switch (this.getAura()) {
            case LIGHT:
                builder.addExtraLines(game.translate("werewolf.roles.auramancer.passive_light",
                        Formatter.format("&light&", Aura.LIGHT.getChatColor() + game.translate(Aura.LIGHT.getKey()))));
                break;
            case NEUTRAL:
                builder.addExtraLines(game.translate("werewolf.roles.auramancer.passive_neutral"));
                break;
            case DARK:
                builder.addExtraLines(game.translate("werewolf.roles.auramancer.passive_dark",
                        Formatter.format("&light&", Aura.LIGHT.getChatColor() + game.translate(Aura.LIGHT.getKey()))));
        }
        if (this.getAura().equals(Aura.DARK)) {
            if (auraLocked) {
                builder.addExtraLines(game.translate("werewolf.roles.auramancer.no_light",
                        Formatter.format("&light&", Aura.LIGHT.getChatColor() + game.translate(Aura.LIGHT.getKey()))));
            } else if (knownPlayer != null) {
                if (!knownPlayer.getRole().getAura().equals(Aura.LIGHT)) {
                    knownPlayer = null;
                } else {
                    builder.addExtraLines(game.translate("werewolf.roles.auramancer.light_player",
                            Formatter.format("&light&", Aura.LIGHT.getChatColor() + game.translate(Aura.LIGHT.getKey())),
                            Formatter.format("&player&", knownPlayer.getName())));
                }
            }
            if (knownPlayer == null) {
                List<IPlayerWW> lightPlayers = game.getPlayersWW()
                        .stream()
                        .filter(playerWW2 -> playerWW2.isState(StatePlayer.ALIVE))
                        .filter(playerWW2 -> !playerWW2.equals(getPlayerWW()))
                        .filter(playerWW2 -> playerWW2.getRole().getAura().equals(Aura.LIGHT))
                        .collect(Collectors.toList());

                if (lightPlayers.isEmpty()) {
                    auraLocked = true;
                    getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.roles.auramancer.aura_locked",
                            Formatter.format("&aura&", this.getAura().getChatColor() + game.translate(this.getAura().getKey())),
                            Formatter.format("&aura_light&", Aura.LIGHT.getChatColor() + game.translate(Aura.LIGHT.getKey())));
                    if (isAbilityEnabled()) {
                        getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.SPEED, this.getKey()));
                    }
                } else {
                    knownPlayer = lightPlayers.get(game.getRandom().nextInt(lightPlayers.size()));
                    getPlayerWW().sendMessageWithKey("werewolf.roles.auramancer.light_player",
                            Formatter.format("&light&", Aura.LIGHT.getChatColor() + game.translate(Aura.LIGHT.getKey())),
                            Formatter.format("&player&", knownPlayer.getName()));
                }
            }
        }
        return builder.build();
    }

    @Override
    public void recoverPower() {
        if (auraLocked && isAbilityEnabled()) {
            getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.SPEED, this.getKey()));
        }
    }



    @Override
    public void addAuraModifier(IAuraModifier auraModifier) {

        if (auraLocked) return;

        if(this.getAura() == auraModifier.getAura()) return;

        if (auraModifier.getName().equals(DeathListener.KILLER)) return;

        super.addAuraModifier(new AuraModifier(this.getKey(), auraModifier.getAura(), 1, false));

        Bukkit.getPluginManager().callEvent(new AuramancerChangeAuraEvent(this.getPlayerWW(), this.getAura()));

        getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.auramancer.aura_change",
                Formatter.format("&aura&", this.getAura().getChatColor() + game.translate(this.getAura().getKey())));
    }

    @Override
    public void removeAuraModifier(IAuraModifier auraModifier) {
    }

    @Override
    public void removeAuraModifier(String modifierName) {
    }

    @Override
    public void removeTemporaryAuras() {
    }

    @Override
    public List<IAuraModifier> getAuraModifiers() {
        return new ArrayList<>();
    }

    @Override
    public boolean isNeutral() {
        return super.isNeutral() || (this.getAura() == Aura.DARK && !super.isWereWolf());
    }

    @EventHandler
    public void onFirstDeathEvent(FirstDeathEvent event) {

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (!isAbilityEnabled()) return;

        IPlayerWW playerWW = event.getPlayerWW();
        playerWW.getLastKiller().ifPresent(killerWW -> {

            if (this.getPlayerWW().equals(killerWW)) {
                if (auraLocked) return;

                Aura auraDead = playerWW.getRole().getAura();

                if (auraDead == Aura.DARK) {
                    if (this.getAura() == Aura.DARK) {
                        this.addAuraModifier(new AuraModifier(this.getKey(), Aura.NEUTRAL, 1, false));
                    }
                    else if (this.getAura() == Aura.NEUTRAL) {
                        this.addAuraModifier(new AuraModifier(this.getKey(), Aura.LIGHT, 1, false));
                    }
                } else {
                    this.addAuraModifier(new AuraModifier(this.getKey(), Aura.DARK, 1, false));
                }

                Bukkit.getPluginManager().callEvent(new AuramancerChangeAuraEvent(this.getPlayerWW(), this.getAura()));
                getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.roles.auramancer.aura_kill",
                        Formatter.format("&aura_dead&", auraDead.getChatColor() + game.translate(auraDead.getKey())),
                        Formatter.format("&aura_new&", this.getAura().getChatColor() + game.translate(this.getAura().getKey())));

            }
            else if (this.getAura() == Aura.NEUTRAL &&
                    killerWW.getLocation().distance(this.getPlayerWW().getLocation()) <
                    game.getConfig().getValue(IntValueBase.AURAMANCER_DISTANCE)) {

                //Compute the Aura the killer had before the kill
                Aura auraKiller = killerWW.getRole().getAura();
                if (killerWW.getPlayersKills().size() == 1) {
                    List<IAuraModifier> modifiers = killerWW.getRole().getAuraModifiers();
                    modifiers.removeAll(modifiers.stream()
                            .filter(a -> a.getName().equals(DeathListener.KILLER))
                            .collect(Collectors.toList()));
                    auraKiller = modifiers.size() == 0 ? killerWW.getRole().getDefaultAura() : modifiers.get(modifiers.size()-1).getAura();
                }

                Aura auraVictim = playerWW.getRole().getAura();

                Bukkit.getPluginManager().callEvent(new AuramancerEvent(getPlayerWW(), playerWW, auraVictim));
                Bukkit.getPluginManager().callEvent(new AuramancerEvent(getPlayerWW(), killerWW, auraKiller));

                this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.roles.auramancer.aura_sense",
                        Formatter.format("&auraVictim&", auraVictim.getChatColor() + game.translate(auraVictim.getKey())),
                        Formatter.format("&auraKiller&", auraKiller.getChatColor() + game.translate(auraKiller.getKey())));
            }
        });
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof Player)) return;

        if (!(event.getDamager() instanceof Player)) return;

        Player damager = (Player) event.getDamager();
        IPlayerWW damagerWW = game.getPlayerWW(damager.getUniqueId()).orElse(null);

        if (this.getAura() == Aura.DARK) {

            if (damagerWW == null || !damagerWW.equals(getPlayerWW())) return;

            Player target = (Player) event.getEntity();
            IPlayerWW targetWW = game.getPlayerWW(target.getUniqueId()).orElse(null);
            if (targetWW == null) return;

            if (targetWW.getRole().getAura() == Aura.LIGHT) {
                event.setDamage(event.getDamage() * (1 + game.getConfig().getStrengthRate() / 100f));
            }
        } else if (this.getAura() == Aura.LIGHT) {

            if (!isAbilityEnabled()) return;

            Set<IPlayerWW> nearbyPlayers = game.getPlayersWW()
                    .stream()
                    .filter(playerWW2 -> playerWW2.isState(StatePlayer.ALIVE))
                    .filter(playerWW2 -> !playerWW2.equals(getPlayerWW()))
                    .filter(playerWW2 -> playerWW2.getRole().getAura().equals(Aura.LIGHT))
                    .filter(playerWW2 -> playerWW2.getLocation().distance(getPlayerWW().getLocation()) < game.getConfig().getValue(IntValueBase.AURAMANCER_DISTANCE))
                    .collect(Collectors.toSet());

            if (nearbyPlayers.contains(damagerWW)) {
                event.setDamage(event.getDamage() * (1 + game.getConfig().getStrengthRate() / 200f));
            }
        }


    }

    @EventHandler
    public void onFinalDeath(FinalDeathEvent event) {

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (auraLocked) return;

        if (!this.getAura().equals(Aura.DARK)) return;

        long lightAuras = game.getPlayersWW()
                .stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .map(IPlayerWW::getRole)
                .filter(roles -> !roles.equals(this))
                .filter(roles -> roles.getAura().equals(Aura.LIGHT)).count();

        if (lightAuras == 0) {
            auraLocked = true;
            getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.roles.auramancer.aura_locked",
                    Formatter.format("&aura&", this.getAura().getChatColor() + game.translate(this.getAura().getKey())),
                            Formatter.format("&aura_light&", Aura.LIGHT.getChatColor() + game.translate(Aura.LIGHT.getKey())));
            if (isAbilityEnabled()) {
                getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.SPEED, this.getKey()));
            }
        }
    }
}
