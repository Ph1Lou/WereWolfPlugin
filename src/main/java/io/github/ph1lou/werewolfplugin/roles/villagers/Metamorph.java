package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enums.ConfigsBase;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.TimersBase;
import io.github.ph1lou.werewolfapi.events.AppearInWereWolfListEvent;
import io.github.ph1lou.werewolfapi.events.NightEvent;
import io.github.ph1lou.werewolfapi.events.WereWolfCanSpeakInChatEvent;
import io.github.ph1lou.werewolfapi.events.WereWolfChatEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesVillage;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Metamorph extends RolesVillage {

    public Metamorph(GetWereWolfAPI main, PlayerWW playerWW, String key) {
        super(main, playerWW, key);
    }


    @Override
    public @NotNull String getDescription() {
        return super.getDescription() +
                game.translate("werewolf.description.description", game.translate("werewolf.role.metamorph.description")) +
                game.translate("werewolf.description.power", game.translate("werewolf.role.metamorph.chat")) +
                game.translate("werewolf.description._");
    }


    @Override
    public void recoverPower() {

    }

    @EventHandler
    public void onWWChatMeta(WereWolfChatEvent event) {

        if (event.isCancelled()) return;

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (isWereWolf()) { //pour éviter qu'elle ait le message en double
            return;
        }

        getPlayerWW().sendMessageWithKey("werewolf.commands.admin.ww_chat.prefix", event.getMessage());

    }

    @EventHandler
    public void onNightAnnounceWereWOlfChat(NightEvent event) {

        if (!game.getConfig().isConfigActive(ConfigsBase.WEREWOLF_CHAT.getKey())) return;

        getPlayerWW().sendMessageWithKey("werewolf.commands.admin.ww_chat.announce", game.getScore().conversion(game.getConfig().getTimerValue(TimersBase.WEREWOLF_CHAT_DURATION.getKey())), game.getConfig().getWereWolfChatMaxMessage());

    }

    @EventHandler
    public void onRequestAccessWereWolfChat(WereWolfCanSpeakInChatEvent event) {

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (!event.getPlayerWW().equals(getPlayerWW())) return;

        event.setCanSpeak(true);
    }

    @EventHandler
    public void onRequestOnWereWolfList(AppearInWereWolfListEvent event) {

        if (!getPlayerUUID().equals(event.getPlayerUUID())) return;

        if (Objects.requireNonNull(game.getPlayerWW(getPlayerUUID()))
                .isState(StatePlayer.DEATH)) return;

        event.setAppear(true);
    }

}
