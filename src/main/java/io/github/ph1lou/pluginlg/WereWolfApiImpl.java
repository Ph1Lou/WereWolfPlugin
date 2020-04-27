package io.github.ph1lou.pluginlg;

import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.worldloader.WorldFillTask;
import io.github.ph1lou.pluginlgapi.WereWolfAPI;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.UUID;

public class WereWolfApiImpl implements WereWolfAPI {

    final MainLG main;

    public WereWolfApiImpl(MainLG main){
        this.main=main;
    }

    @Override
    public UUID launchGame(String name, List<UUID> hostsUUIDs, List<UUID> moderatorsUUIDs, List<String> whiteListedPlayers, int playerMax) {
        GameManager game =new GameManager(main,name, hostsUUIDs, moderatorsUUIDs,whiteListedPlayers,playerMax);
        return game.getGameUUID();
    }

    @Override
    public void addPlayerOnWhiteList(UUID uuid, String s) {
        try{
            GameManager game=main.listGames.get(uuid);
            game.addWhiteListedPlayer(s);
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void removePlayerOnWhiteList(UUID uuid, String s) {
        try{
            GameManager game=main.listGames.get(uuid);
            game.removeWhiteListedPlayer(s);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void addHost(UUID uuid, UUID uuid1) {
        try{
            GameManager game=main.listGames.get(uuid);
            game.addHost(uuid1);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void removeHost(UUID uuid, UUID uuid1) {
        try{
            GameManager game=main.listGames.get(uuid);
            game.removeHost(uuid1);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void addModerator(UUID uuid, UUID uuid1) {
        try{
            GameManager game=main.listGames.get(uuid);
            game.addModerator(uuid1);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void removeModerator(UUID uuid, UUID uuid1) {
        try{
            GameManager game=main.listGames.get(uuid);
            game.removeModerator(uuid1);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void generateMap(UUID uuid, int i) {
        try{
            GameManager game=main.listGames.get(uuid);
            if (game.wft == null || game.wft.getPercentageCompleted()==100) {
                game.wft = new WorldFillTask(game, 20, i);
                game.wft.setTaskID(Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(main, game.wft, 1, 1));
                Bukkit.getConsoleSender().sendMessage(game.text.getText(269));
            } else Bukkit.getConsoleSender().sendMessage(game.text.getText(11));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void stopGame(UUID uuid) {
        try{
            GameManager game=main.listGames.get(uuid);
            game.deleteGame();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
