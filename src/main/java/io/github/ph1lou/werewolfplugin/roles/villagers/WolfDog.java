package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Camp;
import io.github.ph1lou.werewolfapi.enums.ConfigsBase;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.TimersBase;
import io.github.ph1lou.werewolfapi.events.game.day_cycle.NightEvent;
import io.github.ph1lou.werewolfapi.events.game.timers.WereWolfListEvent;
import io.github.ph1lou.werewolfapi.events.werewolf.AppearInWereWolfListEvent;
import io.github.ph1lou.werewolfapi.events.werewolf.WereWolfCanSpeakInChatEvent;
import io.github.ph1lou.werewolfapi.events.werewolf.WereWolfChatEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IPower;
import io.github.ph1lou.werewolfapi.rolesattributs.IRole;
import io.github.ph1lou.werewolfapi.rolesattributs.ITransformed;
import io.github.ph1lou.werewolfapi.rolesattributs.RoleVillage;
import io.github.ph1lou.werewolfapi.utils.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class WolfDog extends RoleVillage implements ITransformed, IPower {

    private boolean transformed = false;
    private boolean power = true;

    public WolfDog(WereWolfAPI api, IPlayerWW playerWW, String key) {
        super(api, playerWW, key);
    }

    @Override
    public @NotNull String getDescription() {

        return new DescriptionBuilder(game, this)
                .setDescription(() -> power ?
                        game.translate("werewolf.role.wolf_dog.description")
                                + '\n' + game.translate("werewolf.role.wolf_dog.description_2")
                        :
                        game.translate(this.transformed ? "werewolf.role.wolf_dog.description_2"
                                :
                                "werewolf.role.wolf_dog.description"))
                .build();

    }


    @Override
    public void recoverPower() {
        this.getPlayerWW().sendMessageWithKey("werewolf.role.wolf_dog.transform",
                Utils.conversion(game.getConfig().getTimerValue(TimersBase.WEREWOLF_LIST.getKey())));
    }

    @Override
    public boolean isWereWolf() {
        return super.isWereWolf() || this.transformed;
    }

    @EventHandler
    public void onWereWolfList(WereWolfListEvent event) {

        if (this.power) {
            this.getPlayerWW().sendMessageWithKey("werewolf.role.wolf_dog.time_over");
        }
        this.power = false;
    }

    @Override
    @EventHandler
    public void onWWChat(WereWolfChatEvent event) {

        if (event.isCancelled()) return;

        if (this.transformed && !super.isWereWolf()) {
            event.setCancelled(true);
            return;
        }

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) return;

        getPlayerWW().sendMessageWithKey("werewolf.commands.admin.ww_chat.prefix", event.getMessage());

    }

    @Override
    @EventHandler
    public void onNightForWereWolf(NightEvent event) {

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (super.isWereWolf()) {
            getPlayerWW().addPotionEffect(PotionEffectType.INCREASE_DAMAGE);
        }

        if (transformed || !super.isWereWolf()) return;


        if (!game.getConfig().isConfigActive(ConfigsBase.WEREWOLF_CHAT.getKey())) return;

        openWereWolfChat();

    }

    @Override
    @EventHandler
    public void onChatSpeak(WereWolfCanSpeakInChatEvent event) {

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (!event.getPlayerWW().equals(getPlayerWW())) return;

        if (this.transformed && !super.isWereWolf()) return;

        event.setCanSpeak(true);
    }

    @Override
    @EventHandler
    public void onAppearInWereWolfList(AppearInWereWolfListEvent event) {

        if (!getPlayerUUID().equals(event.getPlayerUUID())) return;

        if (getPlayerWW().isState(StatePlayer.DEATH)) return;

        event.setAppear(!this.transformed || super.isWereWolf());
    }

    @Override
    public boolean isDisplayCamp(String camp) {
        return getDisplayCamp().equals(camp);
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
            return this.game.getPlayerWW().stream()
                    .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                    .map(IPlayerWW::getRole)
                    .filter(roles -> roles.isCamp(Camp.VILLAGER))
                    .map(IRole::getKey)
                    .findFirst()
                    .orElse(this.getKey());
        }


        return this.game.getPlayerWW().stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .map(IPlayerWW::getRole)
                .filter(role -> role.isDisplayCamp(Camp.WEREWOLF.getKey()))
                .map(IRole::getKey)
                .findFirst()
                .orElse(this.getKey());
    }

    @Override
    public boolean getTransformed() {
        return this.transformed;
    }

    @Override
    public void setTransformed(boolean transformed) {
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
