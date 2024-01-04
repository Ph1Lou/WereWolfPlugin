package fr.ph1lou.werewolfplugin.roles.neutrals;

import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Camp;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.ActionBarEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.NightEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.game.timers.WereWolfListEvent;
import fr.ph1lou.werewolfapi.events.game.utils.WinConditionsCheckEvent;
import fr.ph1lou.werewolfapi.events.random_events.SwapEvent;
import fr.ph1lou.werewolfapi.events.roles.StealEvent;
import fr.ph1lou.werewolfapi.events.roles.remulusremus.MotherDeathEvent;
import fr.ph1lou.werewolfapi.events.werewolf.AppearInWereWolfListEvent;
import fr.ph1lou.werewolfapi.events.werewolf.NewWereWolfEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleImpl;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.ITransformed;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfapi.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Role(key = RoleBase.ROMULUS_REMUS,
        defaultAura = Aura.NEUTRAL,
        category = Category.NEUTRAL,
        attributes = RoleAttribute.HYBRID,
        requireDouble = true,
configValues = @IntValue(key = IntValueBase.ROMULUS_REMUS_DISTANCE_BROTHER, defaultValue = 50, meetUpValue = 40, step = 5, item = UniversalMaterial.BOOK))
public class RomulusRemus extends RoleImpl implements IAffectedPlayers, ITransformed {
    
    private final List<IPlayerWW> affectedPlayers = new ArrayList<>();
    private boolean transformed;
    private boolean romulus;
    private boolean isInitialized;
    private boolean killedBrother;
    private boolean metMother;
    private int counter = 0;

    public RomulusRemus(@NotNull WereWolfAPI game, @NotNull IPlayerWW playerWW) {
        super(game, playerWW);
        this.setDisplayRole(RoleBase.VILLAGER);
        this.setDisplayCamp(Camp.VILLAGER.getKey());
        transformed = false;
        isInitialized = false;
        metMother = false;
    }

    @Override
    public boolean isNeutral() {
        return !this.transformed;
    }

    @Override
    public @NotNull String getDescription() {

        if (!transformed) {
            DescriptionBuilder descriptionBuilder = new DescriptionBuilder(game, this)
                    .setDescription(game.translate("werewolf.roles.romulus_remus.description_naive"))
                    .setPower(game.translate("werewolf.roles.romulus_remus.power_naive",
                            Formatter.number(game.getConfig().getValue(IntValueBase.ROMULUS_REMUS_DISTANCE_BROTHER))));
            getBrother().ifPresent(brother -> descriptionBuilder.addExtraLines(
                    game.translate("werewolf.roles.romulus_remus.brother_name",
                            Formatter.format("&name&", brother.getName()))));
            getMother().ifPresent(mother -> descriptionBuilder.addExtraLines(game.translate("werewolf.roles.romulus_remus.mother_role",
                    Formatter.format("&role&", game.translate(mother.getRole().getKey())))));
            return descriptionBuilder.build();
        }

        DescriptionBuilder descriptionBuilder;
        if (romulus) {
            descriptionBuilder = new DescriptionBuilder(game, this)
                    .setDescription(game.translate("werewolf.roles.romulus_remus.description_romulus"));
        } else {
            descriptionBuilder = new DescriptionBuilder(game, this)
                    .setDescription(game.translate("werewolf.roles.romulus_remus.description_remus"));
        }
        if (killedBrother) {
            descriptionBuilder.addExtraLines(game.translate("werewolf.roles.romulus_remus.strength"));
        }
        return descriptionBuilder.build();

    }

    @Override
    public void recoverPower() {

    }

    @Override
    public void addAffectedPlayer(IPlayerWW iPlayerWW) {
        affectedPlayers.add(iPlayerWW);
    }

    @Override
    public void removeAffectedPlayer(IPlayerWW iPlayerWW) {
        affectedPlayers.remove(iPlayerWW);
    }

    @Override
    public void clearAffectedPlayer() {
        affectedPlayers.clear();
    }

    @Override
    public boolean isWereWolf() {
        return super.isWereWolf() || (this.transformed && !romulus);
    }

    @Override
    public List<? extends IPlayerWW> getAffectedPlayers() {
        return affectedPlayers;
    }

    @EventHandler
    public void onWerewolfList(WereWolfListEvent event) {
        initialize();
    }

    @EventHandler
    public void onFinalDeath(FinalDeathEvent event) {

        IPlayerWW playerWW = event.getPlayerWW();

        if (playerWW == null) {
            return;
        }

        if (!getAffectedPlayers().contains(playerWW)) {
            return;
        }
        if (!getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }
        if (playerWW.equals(getBrother().orElse(null))) {

            playerWW.getLastKiller().ifPresent(iPlayerWW -> {
                if (getPlayerWW().equals(iPlayerWW)) {
                    killedBrother = true;
                    if (transformed) {
                        if (isAbilityEnabled()) {
                            getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.INCREASE_DAMAGE, "romulus_remus_strength"));
                        }
                        getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.roles.romulus_remus.killed_brother_strength");
                        if (!romulus) {
                            if (!super.isWereWolf()) {
                                Bukkit.getPluginManager().callEvent(new NewWereWolfEvent(getPlayerWW()));
                            }
                        }
                    }
                }
            });

        }
        if (playerWW.equals(getMother().orElse(null))) {
            setTransformed(true);
            this.getPlayerWW().addPotionModifier(PotionModifier.remove(PotionEffectType.DAMAGE_RESISTANCE, getKey(), 0));
            getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.roles.romulus_remus.mother_dead");

            Bukkit.getPluginManager().callEvent(new MotherDeathEvent(getMother().orElse(null), playerWW.getLastKiller().orElse(null)));
        }
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    private void initialize() {

        if (!affectedPlayers.isEmpty() || isInitialized) {
            return;
        }

        List<IPlayerWW> brothers = game.getPlayersWW()
                .stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .filter(playerWW -> !playerWW.getRole().equals(this))
                .filter(playerWW -> playerWW.getRole().isKey(RoleBase.ROMULUS_REMUS))
                .filter(playerWW -> playerWW.getRole() instanceof RomulusRemus && !((RomulusRemus) playerWW.getRole()).isInitialized())
                .collect(Collectors.toList());

        if (brothers.isEmpty()) {
            return;
        }

        IPlayerWW brother = brothers.get(game.getRandom().nextInt(brothers.size()));
        romulus = game.getRandom().nextBoolean();

        List<IPlayerWW> wolves = game.getPlayersWW()
                .stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .filter(playerWW -> !playerWW.getRole().getKey().equals(this.getKey()))
                .filter(playerWW -> playerWW.getRole().isWereWolf())
                .collect(Collectors.toList());

        if (wolves.isEmpty()) {
            wolves = game.getPlayersWW().stream()
                    .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                    .filter(playerWW -> !playerWW.getRole().getKey().equals(this.getKey()))
                    .collect(Collectors.toList());
        }

        if (wolves.isEmpty()) {
            return;
        }

        IPlayerWW mother = wolves.get(game.getRandom().nextInt(wolves.size()));

        if (!(brother.getRole() instanceof RomulusRemus)) {
            return;
        }
        ((RomulusRemus) brother.getRole()).initialize(getPlayerWW(), mother, !romulus);

        initialize(brother, mother, romulus);

    }

    public void initialize(IPlayerWW brother, IPlayerWW mother, boolean isRomulus) {

        this.romulus = isRomulus;
        addAffectedPlayer(brother);
        addAffectedPlayer(mother);

        isInitialized = true;
        getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.romulus_remus.family_init");
    }

    public Optional<IPlayerWW> getBrother() {
        return affectedPlayers.isEmpty() ? Optional.empty() : Optional.of(affectedPlayers.get(0));
    }

    public Optional<IPlayerWW> getMother() {
        return affectedPlayers.isEmpty() ? Optional.empty() : Optional.of(affectedPlayers.get(1));
    }

    @Override
    public boolean isTransformed() {
        return transformed;
    }

    @Override
    public void setTransformed(boolean b) {
        transformed = b;
    }

    @Override
    public void recoverPotionEffect() {

        if (transformed && killedBrother && isAbilityEnabled()) {
            getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.INCREASE_DAMAGE, this.getKey()));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onNight(NightEvent event) {

        if (!this.transformed) {
            return;
        }

        if(!this.isWereWolf()){
            return;
        }

        this.getPlayerWW().addPotionModifier(PotionModifier.remove(PotionEffectType.INCREASE_DAMAGE, RoleBase.WEREWOLF, 0));
    }


    @EventHandler
    public void onActionBarRequest(ActionBarEvent event) {

        if (!getPlayerUUID().equals(event.getPlayerUUID())) {
            return;
        }

        if (this.transformed) {
            return;
        }

        if(this.killedBrother){
            return;
        }

        if (!isAbilityEnabled()) return;

        StringBuilder stringBuilder = new StringBuilder(event.getActionBar());

        Player player = Bukkit.getPlayer(event.getPlayerUUID());

        if (player == null) return;

        IPlayerWW brother = getBrother().orElse(null);

        if(brother == null) return;

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (!brother.isState(StatePlayer.ALIVE)) return;

        stringBuilder.append("§b ")
                .append(brother.getName())
                .append(" ")
                .append(Utils.updateArrow(player, brother.getLocation()));

        event.setActionBar(stringBuilder.toString());
    }

    @EventHandler
    public void onDetectVictoryWithFamily(WinConditionsCheckEvent event) {

        if (event.isWin()) return;

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (transformed || affectedPlayers.isEmpty()) return;

        IPlayerWW brother = getBrother().orElse(null);

        if (brother == null || !brother.isState(StatePlayer.ALIVE)) return;

        IPlayerWW mother = getMother().orElse(null);

        if (mother == null || !mother.isState(StatePlayer.ALIVE)) return;

        if (game.getPlayersWW()
                    .stream()
                    .filter(iPlayerWW -> iPlayerWW.isState(StatePlayer.ALIVE)).count() == 3) {
            event.setWin();
            event.setVictoryTeam(RoleBase.ROMULUS_REMUS);
        }
    }

    @EventHandler
    public void onAppearInWerewolfList(AppearInWereWolfListEvent event) {

        if(!event.getTargetWW().equals(this.getPlayerWW()) &&
           !event.getPlayerWW().equals(this.getPlayerWW())){
            return;
        }

        if(this.isWereWolf() && !this.romulus && !this.killedBrother){
            event.setAppear(false);
        }
    }


    @Override
    public void second() {


        if(this.transformed){
            return;
        }

        counter++;
        if (counter % 6 != 0) return;
        counter = 0;

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        IPlayerWW brother = getBrother().orElse(null);
        IPlayerWW mother = getMother().orElse(null);

        Location location = this.getPlayerWW().getLocation();

        if (brother != null &&  brother.isState(StatePlayer.ALIVE) && isAbilityEnabled()) {
            boolean recoverResistance = brother.getLocation().distance(location) > game.getConfig().getValue(IntValueBase.ROMULUS_REMUS_DISTANCE_BROTHER);

            this.getPlayerWW().addPotionModifier(PotionModifier.remove(PotionEffectType.DAMAGE_RESISTANCE, getKey(), 0));
            if (recoverResistance) {
                this.getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.DAMAGE_RESISTANCE, 100, 0, getKey()));
            }
        }

        if (mother == null || metMother || !mother.isState(StatePlayer.ALIVE)) {
            return;
        }

        if (mother.getLocation().distance(location) < 20) {
            BukkitUtils.scheduleSyncDelayedTask(game, () -> mother.sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.romulus_remus.mother_message"), 20 * 60 * 2);
            metMother = true;
        }
    }

    @Override
    public void disableAbilitiesRole() {

        if(!this.getPlayerWW().isState(StatePlayer.ALIVE)){
            return;
        }

        this.getPlayerWW().addPotionModifier(PotionModifier.remove(PotionEffectType.DAMAGE_RESISTANCE, getKey(), 0));
    }

    @EventHandler
    public void onSwap(SwapEvent event) {
        if (!isInitialized) return;

        IPlayerWW playerWW1 = event.getPlayerWW1();
        IPlayerWW playerWW2 = event.getPlayerWW2();
        boolean change = false;

        if(getMother().isPresent()){
            if (getMother().get().equals(playerWW1)) {
                affectedPlayers.set(1, playerWW2);
                change = true;
            } else if (getMother().get().equals(playerWW2)) {
                affectedPlayers.set(1, playerWW1);
                change = true;
            }  
        }

        if(getBrother().isPresent()){
            if (getBrother().get().equals(playerWW1)) {
                affectedPlayers.set(0, playerWW2);
                change = true;
            } else if (getBrother().get().equals(playerWW2)) {
                affectedPlayers.set(0, playerWW1);
                change = true;
            }
        }

        if (change) {
            getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.romulus_remus.family_change");
        }
    }

    @EventHandler
    public void onFamilyIsStolen(StealEvent event) {

        IPlayerWW playerWW = event.getTargetWW();
        IPlayerWW thiefWW = event.getPlayerWW();

        if (!getAffectedPlayers().contains(playerWW)) return;

        if (this.getPlayerWW().isState(StatePlayer.DEATH)) return;

        int i = affectedPlayers.indexOf(playerWW);
        affectedPlayers.set(i, thiefWW);

        getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.romulus_remus.family_change");
    }
}
