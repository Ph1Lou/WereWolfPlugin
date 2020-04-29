package io.github.ph1lou.pluginlg;

import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.worldloader.WorldFillTask;
import io.github.ph1lou.pluginlgapi.WereWolfAPI;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.UUID;

public class WereWolfApiImpl implements WereWolfAPI {

    final MainLG main;

    public WereWolfApiImpl(MainLG main) {
        this.main = main;
    }


    @Override
    public void setGameName(String name) {
        try {
            GameManager game = main.currentGame;
            game.setGameName(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setHosts(List<UUID> hostsUUIDs) {
        try {
            GameManager game = main.currentGame;
            game.setHosts(hostsUUIDs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setModerators(List<UUID> moderatorsUUIDs) {
        try {
            GameManager game = main.currentGame;
            game.setModerators(moderatorsUUIDs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setWhiteListedPlayers(List<UUID> whiteListedPlayers) {
        try {
            GameManager game = main.currentGame;
            game.setWhiteListedPlayer(whiteListedPlayers);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setPlayerMax(int playerMax) {
        try {
            GameManager game = main.currentGame;
            game.setPlayerMax(playerMax);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addPlayerOnWhiteList(UUID uuid) {
        try {
            GameManager game = main.currentGame;
            game.addWhiteListedPlayer(uuid);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void removePlayerOnWhiteList(UUID uuid) {
        try {
            GameManager game = main.currentGame;
            game.removeWhiteListedPlayer(uuid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addHost(UUID uuid) {
        try {
            GameManager game = main.currentGame;
            game.addHost(uuid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeHost(UUID uuid) {
        try {
            GameManager game = main.currentGame;
            game.removeHost(uuid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addModerator(UUID uuid) {
        try {
            GameManager game = main.currentGame;
            game.addModerator(uuid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeModerator(UUID uuid) {
        try {
            GameManager game = main.currentGame;
            game.removeModerator(uuid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void generateMap(int i) {
        try {
            GameManager game = main.currentGame;
            if (game.wft == null || game.wft.getPercentageCompleted() == 100) {
                game.wft = new WorldFillTask(game, 20, i);
                game.wft.setTaskID(Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(main, game.wft, 1, 1));
                Bukkit.getConsoleSender().sendMessage(game.text.getText(269));
            } else Bukkit.getConsoleSender().sendMessage(game.text.getText(11));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopGame() {
        try {
            GameManager game = main.currentGame;
            game.deleteGame();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
