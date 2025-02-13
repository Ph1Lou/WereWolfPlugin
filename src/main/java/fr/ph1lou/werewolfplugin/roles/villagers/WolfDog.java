package fr.ph1lou.werewolfplugin.roles.villagers;


import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Camp;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalPotionEffectType;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.NightEvent;
import fr.ph1lou.werewolfapi.events.game.timers.WereWolfListEvent;
import fr.ph1lou.werewolfapi.events.werewolf.AppearInWereWolfListEvent;
import fr.ph1lou.werewolfapi.events.werewolf.RequestSeeWereWolfListEvent;
import fr.ph1lou.werewolfapi.events.werewolf.WereWolfCanSpeakInChatEvent;
import fr.ph1lou.werewolfapi.events.werewolf.WereWolfChatEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.AuraModifier;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleImpl;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.role.interfaces.ITransformed;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

@Role(key = RoleBase.WOLF_DOG,
        defaultAura = Aura.DARK,
        category = Category.VILLAGER,
        auraDescriptionSpecialUseCase = "werewolf.roles.wolf_dog.aura",
        attribute = RoleAttribute.HYBRID)
public class WolfDog extends RoleImpl implements ITransformed, IPower {

    private boolean transformed = false;
    private boolean power = true;

    public WolfDog(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
    }

    @Override
    public @NotNull String getDescription() {

        return new DescriptionBuilder(game, this)
                .setDescription(power ?
                        game.translate("werewolf.roles.wolf_dog.description")
                        + '\n' + '\n' + game.translate("werewolf.roles.wolf_dog.description_2")
                        :
                        game.translate(this.transformed ? "werewolf.roles.wolf_dog.description_2"
                                :
                                "werewolf.roles.wolf_dog.description"))
                .build();

    }


    @Override
    public void recoverPower() {

        int timer = game.getConfig().getTimerValue(TimerBase.WEREWOLF_LIST);

        if (timer > 0) {
            this.getPlayerWW().sendMessageWithKey(Prefix.GREEN, "werewolf.roles.wolf_dog.transform",
                    Formatter.timer(game, TimerBase.WEREWOLF_LIST));
        }
    }

    @Override
    public boolean isWereWolf() {
        return super.isWereWolf() || this.transformed;
    }

    @EventHandler
    public void onWereWolfList(WereWolfListEvent event) {

        if (this.power) {
            this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.roles.wolf_dog.time_over");
        }
        this.power = false;
    }


    @EventHandler(priority = EventPriority.LOW)
    public void onWerewolfWHat(WereWolfChatEvent event) {

        if (!event.getTargetWW().equals(this.getPlayerWW())) {
            return;
        }

        event.setCancelled(this.transformed && !super.isWereWolf());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onNightForWereWolf(NightEvent event) {

        if (!super.isWereWolf() && this.transformed) {
            this.getPlayerWW().addPotionModifier(PotionModifier.remove(UniversalPotionEffectType.STRENGTH, RoleBase.WEREWOLF, 0));
        }

        if (!this.transformed) {
            this.getPlayerWW().addPotionModifier(PotionModifier.add(UniversalPotionEffectType.STRENGTH, RoleBase.WEREWOLF));
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDayForWereWolf(DayEvent event) {

        if (!this.transformed) {
            this.getPlayerWW().addPotionModifier(PotionModifier.remove(UniversalPotionEffectType.STRENGTH, RoleBase.WEREWOLF, 0));
        }
    }


    @EventHandler
    public void onChatSpeak(WereWolfCanSpeakInChatEvent event) {

        if (!event.getPlayerWW().equals(getPlayerWW())) return;

        event.setCanSpeak(!this.transformed || super.isWereWolf());
    }


    @EventHandler
    public void onAppearInWereWolfList(AppearInWereWolfListEvent event) {

        if (!getPlayerWW().equals(event.getTargetWW())) return;

        if (this.getPlayerWW().isState(StatePlayer.DEATH)) return;

        event.setAppear(!this.transformed || super.isWereWolf());
    }

    @EventHandler
    public void onRequestSeeWereWolfListEvent(RequestSeeWereWolfListEvent event) {

        if (!getPlayerWW().equals(event.getPlayerWW())) return;

        if (this.getPlayerWW().isState(StatePlayer.DEATH)) return;

        event.setAccept(this.transformed || super.isWereWolf());
    }

    @Override
    public String getDisplayCamp() {

        if (this.transformed) {
            return Camp.VILLAGER.getKey();
        }
        return Camp.WEREWOLF.getKey();
    }

    @Override
    public String getDisplayRole() {

        if (this.transformed) {
            return this.game.getAlivePlayersWW().stream()
                    .map(IPlayerWW::getRole)
                    .filter(roles -> roles.isCamp(Camp.VILLAGER))
                    .map(IRole::getKey)
                    .findFirst()
                    .orElse(this.getKey());
        }


        return this.game.getAlivePlayersWW().stream()
                .map(IPlayerWW::getRole)
                .filter(role -> role.isCamp(Camp.WEREWOLF))
                .map(IRole::getKey)
                .findFirst()
                .orElse(this.getKey());
    }

    @Override
    public boolean isTransformed() {
        return this.transformed;
    }

    @Override
    public void setTransformed(boolean transformed) {
        if (transformed) {
            this.addAuraModifier(new AuraModifier(this.getKey(), Aura.LIGHT, 1, false));
        }
        this.transformed = transformed;
    }

    @Override
    public void setPower(boolean power) {
        this.power = power;
    }

    @Override
    public boolean hasPower() {
        return power;
    }
}
