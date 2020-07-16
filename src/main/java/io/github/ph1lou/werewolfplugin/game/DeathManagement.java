package io.github.ph1lou.werewolfplugin.game;

import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enumlg.Sounds;
import io.github.ph1lou.werewolfapi.enumlg.State;
import io.github.ph1lou.werewolfapi.enumlg.ToolLG;
import io.github.ph1lou.werewolfapi.events.FinalDeathEvent;
import io.github.ph1lou.werewolfapi.events.ResurrectionEvent;
import io.github.ph1lou.werewolfapi.events.SecondDeathEvent;
import io.github.ph1lou.werewolfapi.events.ThirdDeathEvent;
import io.github.ph1lou.werewolfplugin.Main;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class DeathManagement {

    private final Main main;

    public DeathManagement(Main main) {
        this.main = main;
    }


    public void deathStep1(UUID uuid) {

        GameManager game = main.getCurrentGame();
        PlayerWW plg = game.getPlayersWW().get(uuid);
        SecondDeathEvent secondDeathEvent = new SecondDeathEvent(uuid);
        Bukkit.getPluginManager().callEvent(secondDeathEvent);
        Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
            if(plg.isState(State.JUDGEMENT) && !secondDeathEvent.isCancelled()){
                plg.setCanBeInfect(false);
                deathStep2(uuid);
            }
        },7*20);
    }

    private void deathStep2(UUID uuid) {
        GameManager game = main.getCurrentGame();
        PlayerWW plg = game.getPlayersWW().get(uuid);
        ThirdDeathEvent thirdDeathEvent = new ThirdDeathEvent(uuid);
        Bukkit.getPluginManager().callEvent(thirdDeathEvent);
        Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
            if(plg.isState(State.JUDGEMENT) && !thirdDeathEvent.isCancelled()){
                death(uuid);
            }
        },7*20);
	}

    public void death(UUID playerUUID) {
        GameManager game = main.getCurrentGame();
        World world = game.getWorld();
        PlayerWW plg = game.getPlayersWW().get(playerUUID);
        Player player = Bukkit.getPlayer(playerUUID);

        plg.setDeathTime((int) TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        String roleLG = plg.getRole().getDisplay();

        if (plg.isThief()) {
            roleLG= "werewolf.role.thief.display";
        }

        game.getConfig().getRoleCount().put(roleLG, game.getConfig().getRoleCount().get(roleLG) - 1);

        if (game.getConfig().getConfigValues().get(ToolLG.SHOW_ROLE_TO_DEATH)) {
            Bukkit.broadcastMessage(game.translate("werewolf.announcement.death_message_with_role", plg.getName(), game.translate(roleLG)));
        } else Bukkit.broadcastMessage(game.translate("werewolf.announcement.death_message", plg.getName()));

        plg.setState(State.DEATH);
        game.getScore().removePlayerSize();
        Bukkit.getPluginManager().callEvent(new FinalDeathEvent(playerUUID));

        for (ItemStack i : Stream.concat(plg.getItemDeath().stream(), game.getStuffs().getDeathLoot().stream()).collect(Collectors.toList())) {
            if (i != null) {
                world.dropItem(plg.getSpawn(), i);
            }
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            Sounds.AMBIENCE_THUNDER.play(p);
        }

        if (!plg.getLovers().isEmpty()) {
            game.getLoversManage().checkLovers(playerUUID);
        }
        if (plg.getCursedLovers() != null) {
            game.getLoversManage().checkCursedLovers(playerUUID);
        }
        if (plg.getAmnesiacLoverUUID() != null) {
            game.getLoversManage().checkAmnesiacLovers(playerUUID);
        }

        if (player != null) {

            player.setGameMode(GameMode.SPECTATOR);
            TextComponent msg = new TextComponent(game.translate("werewolf.bug"));
            msg.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/GXXCVUA"));
            player.spigot().sendMessage(msg);
            if (game.getSpectatorMode() == 0) {
                player.kickPlayer(game.translate("werewolf.check.death_spectator"));
            }
        }
        game.checkVictory();
    }




    public void resurrection(UUID playerUUID) {
        GameManager game = main.getCurrentGame();
        PlayerWW plg = game.getPlayersWW().get(playerUUID);
        Bukkit.getPluginManager().callEvent(new ResurrectionEvent(playerUUID));

        if (Bukkit.getPlayer(playerUUID) != null) {
            Player player = Bukkit.getPlayer(playerUUID);
            plg.getRole().recoverPotionEffect(player);
        }
        game.transportation(playerUUID, Math.random() * Math.PI * 2, game.translate("werewolf.announcement.resurrection"));
        plg.setState(State.ALIVE);

        game.checkVictory();
    }







}
