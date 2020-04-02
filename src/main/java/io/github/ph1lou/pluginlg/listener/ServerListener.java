package io.github.ph1lou.pluginlg.listener;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerListener implements Listener {

    final MainLG main;

    public ServerListener(MainLG main){
        this.main=main;
    }

    @EventHandler
    public void onPing(ServerListPingEvent event){

        if(main.isState(StateLG.LOBBY)){
            event.setMotd(main.text.getText(265));
        }
        else event.setMotd(main.text.getText(266));
    }
}
