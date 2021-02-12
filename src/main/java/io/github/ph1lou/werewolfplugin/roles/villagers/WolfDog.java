package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enums.Camp;
import io.github.ph1lou.werewolfapi.enums.ConfigsBase;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.TimersBase;
import io.github.ph1lou.werewolfapi.events.AppearInWereWolfListEvent;
import io.github.ph1lou.werewolfapi.events.NightEvent;
import io.github.ph1lou.werewolfapi.events.WereWolfCanSpeakInChatEvent;
import io.github.ph1lou.werewolfapi.events.WereWolfChatEvent;
import io.github.ph1lou.werewolfapi.events.WereWolfListEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.Display;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesVillage;
import io.github.ph1lou.werewolfapi.rolesattributs.Transformed;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class WolfDog extends RolesVillage implements Display, Transformed, Power {

    private boolean transformed = false;
    private boolean power = true;

    public WolfDog(GetWereWolfAPI main, PlayerWW playerWW, String key) {
        super(main, playerWW, key);
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
        getPlayerWW().sendMessageWithKey("werewolf.role.wolf_dog.transform",
                game.getScore().conversion(game.getConfig().getTimerValue(TimersBase.WEREWOLF_LIST.getKey())));
    }

    @Override
    public boolean isWereWolf() {
        return super.isWereWolf() || this.transformed;
    }

    @EventHandler
    public void onWereWolfList(WereWolfListEvent event) {

        if (this.power) {
            getPlayerWW().sendMessageWithKey("werewolf.role.wolf_dog.time_over");
        }
        this.power = false;
    }

    @Override
    @EventHandler
    public void onWWChat(WereWolfChatEvent event) {

        if (event.isCancelled()) return;

        if (transformed && !super.isWereWolf()) {
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

        if (transformed) {
            return Camp.VILLAGER.getKey();
        }
        return Camp.WEREWOLF.getKey();
    }

    @Override
    public void setDisplayCamp(String camp) {
    }

    @Override
    public String getDisplayRole() {

        if (transformed) {
            return game.getPlayerWW().stream()
                    .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                    .map(PlayerWW::getRole)
                    .filter(roles -> roles.isCamp(Camp.VILLAGER))
                    .map(Roles::getKey)
                    .findFirst()
                    .orElse(this.getKey());
        }


        return game.getPlayerWW().stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .map(PlayerWW::getRole)
                .filter(roles -> roles.isWereWolf() || roles instanceof Display)
                .filter(roles -> !(roles instanceof Display) ||
                        ((Display) roles).isDisplayCamp(Camp.WEREWOLF.getKey()))
                .map(Roles::getKey)
                .findFirst()
                .orElse(this.getKey());
    }

    @Override
    public void setDisplayRole(String s) {
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
