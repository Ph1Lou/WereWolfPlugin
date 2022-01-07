package io.github.ph1lou.werewolfplugin.statistiks;

import com.google.common.collect.Sets;
import io.github.ph1lou.werewolfapi.ILover;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.LoverType;
import io.github.ph1lou.werewolfapi.enums.Prefix;
import io.github.ph1lou.werewolfapi.enums.StateGame;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.CustomEvent;
import io.github.ph1lou.werewolfapi.events.TrollEvent;
import io.github.ph1lou.werewolfapi.events.TrollLoverEvent;
import io.github.ph1lou.werewolfapi.events.game.configs.LoneWolfEvent;
import io.github.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import io.github.ph1lou.werewolfapi.events.game.day_cycle.NightEvent;
import io.github.ph1lou.werewolfapi.events.game.game_cycle.StartEvent;
import io.github.ph1lou.werewolfapi.events.game.game_cycle.StopEvent;
import io.github.ph1lou.werewolfapi.events.game.game_cycle.WinEvent;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.ResurrectionEvent;
import io.github.ph1lou.werewolfapi.events.game.timers.BorderStartEvent;
import io.github.ph1lou.werewolfapi.events.game.timers.BorderStopEvent;
import io.github.ph1lou.werewolfapi.events.game.timers.DiggingEndEvent;
import io.github.ph1lou.werewolfapi.events.game.timers.InvulnerabilityEvent;
import io.github.ph1lou.werewolfapi.events.game.timers.PVPEvent;
import io.github.ph1lou.werewolfapi.events.game.timers.RepartitionEvent;
import io.github.ph1lou.werewolfapi.events.game.timers.WereWolfListEvent;
import io.github.ph1lou.werewolfapi.events.game.vote.CancelVoteEvent;
import io.github.ph1lou.werewolfapi.events.game.vote.VoteEvent;
import io.github.ph1lou.werewolfapi.events.game.vote.VoteResultEvent;
import io.github.ph1lou.werewolfapi.events.lovers.AmnesiacLoverDeathEvent;
import io.github.ph1lou.werewolfapi.events.lovers.CupidLoversEvent;
import io.github.ph1lou.werewolfapi.events.lovers.CursedLoverDeathEvent;
import io.github.ph1lou.werewolfapi.events.lovers.DonEvent;
import io.github.ph1lou.werewolfapi.events.lovers.LoverDeathEvent;
import io.github.ph1lou.werewolfapi.events.lovers.RevealAmnesiacLoversEvent;
import io.github.ph1lou.werewolfapi.events.lovers.RevealLoversEvent;
import io.github.ph1lou.werewolfapi.events.random_events.AmnesicEvent;
import io.github.ph1lou.werewolfapi.events.random_events.AmnesicTransformEvent;
import io.github.ph1lou.werewolfapi.events.random_events.BearingRitualEvent;
import io.github.ph1lou.werewolfapi.events.random_events.DrunkenWereWolfEvent;
import io.github.ph1lou.werewolfapi.events.random_events.ExposedEvent;
import io.github.ph1lou.werewolfapi.events.random_events.FindAllLootBoxEvent;
import io.github.ph1lou.werewolfapi.events.random_events.GodMiracleEvent;
import io.github.ph1lou.werewolfapi.events.random_events.InfectionRandomEvent;
import io.github.ph1lou.werewolfapi.events.random_events.LootBoxEvent;
import io.github.ph1lou.werewolfapi.events.random_events.PutrefactionEvent;
import io.github.ph1lou.werewolfapi.events.random_events.SwapEvent;
import io.github.ph1lou.werewolfapi.events.random_events.TroupleEvent;
import io.github.ph1lou.werewolfapi.events.roles.InvisibleEvent;
import io.github.ph1lou.werewolfapi.events.roles.StealEvent;
import io.github.ph1lou.werewolfapi.events.roles.amnesiac.AmnesiacTransformationEvent;
import io.github.ph1lou.werewolfapi.events.roles.analyst.AnalystEvent;
import io.github.ph1lou.werewolfapi.events.roles.analyst.AnalystExtraDetailsEvent;
import io.github.ph1lou.werewolfapi.events.roles.angel.AngelChoiceEvent;
import io.github.ph1lou.werewolfapi.events.roles.angel.AngelTargetDeathEvent;
import io.github.ph1lou.werewolfapi.events.roles.angel.AngelTargetEvent;
import io.github.ph1lou.werewolfapi.events.roles.angel.FallenAngelTargetDeathEvent;
import io.github.ph1lou.werewolfapi.events.roles.angel.RegenerationEvent;
import io.github.ph1lou.werewolfapi.events.roles.avenger_werewolf.DeathAvengerListEvent;
import io.github.ph1lou.werewolfapi.events.roles.avenger_werewolf.RegisterAvengerListEvent;
import io.github.ph1lou.werewolfapi.events.roles.bear_trainer.GrowlEvent;
import io.github.ph1lou.werewolfapi.events.roles.charmer.CharmedDeathEvent;
import io.github.ph1lou.werewolfapi.events.roles.charmer.CharmerEvent;
import io.github.ph1lou.werewolfapi.events.roles.charmer.CharmerGetEffectDeathEvent;
import io.github.ph1lou.werewolfapi.events.roles.comedian.UseMaskEvent;
import io.github.ph1lou.werewolfapi.events.roles.detective.InvestigateEvent;
import io.github.ph1lou.werewolfapi.events.roles.druid.DruidUsePowerEvent;
import io.github.ph1lou.werewolfapi.events.roles.elder.ElderResurrectionEvent;
import io.github.ph1lou.werewolfapi.events.roles.falsifier_werewolf.NewDisplayRole;
import io.github.ph1lou.werewolfapi.events.roles.flute_player.AllPlayerEnchantedEvent;
import io.github.ph1lou.werewolfapi.events.roles.flute_player.EnchantedEvent;
import io.github.ph1lou.werewolfapi.events.roles.flute_player.FindFluteEvent;
import io.github.ph1lou.werewolfapi.events.roles.flute_player.GiveFluteEvent;
import io.github.ph1lou.werewolfapi.events.roles.fox.BeginSniffEvent;
import io.github.ph1lou.werewolfapi.events.roles.fox.SniffEvent;
import io.github.ph1lou.werewolfapi.events.roles.grim_werewolf.GrimEvent;
import io.github.ph1lou.werewolfapi.events.roles.guard.GuardEvent;
import io.github.ph1lou.werewolfapi.events.roles.guard.GuardResurrectionEvent;
import io.github.ph1lou.werewolfapi.events.roles.howling_werewolf.HowlEvent;
import io.github.ph1lou.werewolfapi.events.roles.infect_father_of_the_wolves.InfectionEvent;
import io.github.ph1lou.werewolfapi.events.roles.librarian.LibrarianDeathEvent;
import io.github.ph1lou.werewolfapi.events.roles.librarian.LibrarianGiveBackEvent;
import io.github.ph1lou.werewolfapi.events.roles.librarian.LibrarianRequestEvent;
import io.github.ph1lou.werewolfapi.events.roles.mystical_werewolf.MysticalWerewolfRevelationEvent;
import io.github.ph1lou.werewolfapi.events.roles.oracle.OracleEvent;
import io.github.ph1lou.werewolfapi.events.roles.priestess.PriestessEvent;
import io.github.ph1lou.werewolfapi.events.roles.protector.ProtectionEvent;
import io.github.ph1lou.werewolfapi.events.roles.raven.CurseEvent;
import io.github.ph1lou.werewolfapi.events.roles.rival.RivalAnnouncementEvent;
import io.github.ph1lou.werewolfapi.events.roles.rival.RivalLoverDeathEvent;
import io.github.ph1lou.werewolfapi.events.roles.rival.RivalLoverEvent;
import io.github.ph1lou.werewolfapi.events.roles.seer.SeeVoteEvent;
import io.github.ph1lou.werewolfapi.events.roles.seer.SeerEvent;
import io.github.ph1lou.werewolfapi.events.roles.serial_killer.SerialKillerEvent;
import io.github.ph1lou.werewolfapi.events.roles.shaman.ShamanEvent;
import io.github.ph1lou.werewolfapi.events.roles.sister.SisterSeeNameEvent;
import io.github.ph1lou.werewolfapi.events.roles.sister.SisterSeeRoleEvent;
import io.github.ph1lou.werewolfapi.events.roles.stud.StudLoverEvent;
import io.github.ph1lou.werewolfapi.events.roles.succubus.BeginCharmEvent;
import io.github.ph1lou.werewolfapi.events.roles.succubus.CharmEvent;
import io.github.ph1lou.werewolfapi.events.roles.succubus.SuccubusResurrectionEvent;
import io.github.ph1lou.werewolfapi.events.roles.trapper.TrackEvent;
import io.github.ph1lou.werewolfapi.events.roles.trouble_maker.TroubleMakerDeathEvent;
import io.github.ph1lou.werewolfapi.events.roles.trouble_maker.TroubleMakerEvent;
import io.github.ph1lou.werewolfapi.events.roles.twin.TwinListEvent;
import io.github.ph1lou.werewolfapi.events.roles.twin.TwinRevealEvent;
import io.github.ph1lou.werewolfapi.events.roles.twin.TwinRoleEvent;
import io.github.ph1lou.werewolfapi.events.roles.village_idiot.VillageIdiotEvent;
import io.github.ph1lou.werewolfapi.events.roles.villager.VillagerKitEvent;
import io.github.ph1lou.werewolfapi.events.roles.wild_child.ModelEvent;
import io.github.ph1lou.werewolfapi.events.roles.wild_child.WildChildTransformationEvent;
import io.github.ph1lou.werewolfapi.events.roles.will_o_the_wisp.WillOTheWispRecoverRoleEvent;
import io.github.ph1lou.werewolfapi.events.roles.will_o_the_wisp.WillOTheWispTeleportEvent;
import io.github.ph1lou.werewolfapi.events.roles.witch.WitchResurrectionEvent;
import io.github.ph1lou.werewolfapi.events.roles.wolf_dog.WolfDogChooseWereWolfForm;
import io.github.ph1lou.werewolfapi.events.werewolf.NewWereWolfEvent;
import io.github.ph1lou.werewolfapi.events.werewolf.WereWolfChatEvent;
import io.github.ph1lou.werewolfapi.statistics.GameReview;
import io.github.ph1lou.werewolfapi.statistics.RegisteredAction;
import io.github.ph1lou.werewolfapi.utils.BukkitUtils;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import io.github.ph1lou.werewolfplugin.save.FileUtils_;
import io.github.ph1lou.werewolfplugin.save.Serializer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffectType;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class Events implements Listener {

    private final Main main;

    public Events(Main main) {
        this.main = main;
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onGameEnd(StopEvent event) {

        if (main.getCurrentGameReview().getWinnerCampKey() == null) return;

        String jsonInputString = Serializer.serialize(main.getCurrentGameReview());
        File file = new File(main.getDataFolder() + File.separator + "statistiks", main.getCurrentGameReview().getGameUUID() + ".json");

        FileUtils_.save(file, jsonInputString);


        if (main.getCurrentGameReview().getPlayerSize() < 17) {
            Bukkit.getLogger().warning("[WereWolfPlugin] Statistiks no send because player size < 17");
            return;
        }

        if (event.getWereWolfAPI().getTimer() < 3600) {
            Bukkit.getLogger().warning("[WereWolfPlugin] Statistiks no send because game duration < 1h");
            return;
        }

        try {

            URL url = new URL("http://ph1lou.fr:15000/infos2");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("accept", "application/json");
            con.setDoOutput(true);


            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            } catch (Exception ignored) {
            }

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                TextComponent msg = new TextComponent(main.getWereWolfAPI().translate(Prefix.YELLOW.getKey() , "werewolf.statistics.message"));
                msg.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
                        String.format("https://ph1lou.fr/werewolfstat/detail.php?id=%s",
                                response.toString().replaceAll("\"", ""))));
                BukkitUtils.scheduleSyncDelayedTask(() -> Bukkit.getOnlinePlayers()
                        .forEach(player -> player.spigot().sendMessage(msg)), 100);
            } catch (Exception ignored) {

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(((GameManager) event.getWereWolfAPI()).isCrack()){
            Bukkit.getLogger().warning("[WereWolfPlugin] Statistiks no send because Server Crack");
            return;
        }

        try {

            URL url = new URL("https://api.ph1lou.fr/create");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input);
            } catch (Exception ignored) {
            }

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                Bukkit.getLogger().warning(response.toString());
            } catch (Exception ignored) {
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWin(WinEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("win",
                event.getPlayers(), api.getTimer(), event.getRole()));
        main.getCurrentGameReview().end(event.getRole(), event.getPlayers());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGameStart(StartEvent event) {
        UUID serverUUID;
        try{
            serverUUID = UUID.fromString(main.getConfig().getString("server_uuid"));
        }
        catch (Exception e){
            serverUUID = UUID.randomUUID();
            main.getConfig().set("server_uuid", serverUUID);
        }

        main.setCurrentGameReview(new GameReview(main.getWereWolfAPI(), serverUUID));

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event) {

        Player player = event.getEntity();
        WereWolfAPI api = main.getWereWolfAPI();
        UUID playerUUID = player.getUniqueId();
        IPlayerWW playerWW = api.getPlayerWW(playerUUID).orElse(null);

        if (playerWW == null) return;

        Player killer = player.getKiller();
        if (killer == null) return;

        UUID killerUUID = killer.getUniqueId();
        IPlayerWW killerWW = api.getPlayerWW(killerUUID).orElse(null);

        if (main.getCurrentGameReview() == null) return;

        if (!api.isState(StateGame.GAME)) return;

        if (playerWW.isState(StatePlayer.DEATH)) return;

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("kill",
                playerWW, killerWW, api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onFinalDeath(FinalDeathEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("final_kill",
                playerWW, event.getPlayerWW().getLastKiller().isPresent() ?
                event.getPlayerWW().getLastKiller().get() : null, api.getTimer()));
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onCustom(CustomEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction(event.getEvent(), event.getPlayerWW(), event.getPlayerWWS(), api.getTimer(), event.getExtraInfo(), event.getExtraInt()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInfection(InfectionEvent event) {

        if (event.isCancelled()) return;


        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("infection",
                event.getPlayerWW(), event.getTargetWW(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGrim(GrimEvent event) {

        if (event.isCancelled()) return;


        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("grim",
                event.getPlayerWW(), event.getTargetWW(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onReviveElder(ElderResurrectionEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("elder_revive",
                playerWW, api.getTimer(),
                event.isKillerAVillager() ? "elder_kill_by_villager" : "elder_not_kill_by_villager"));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRevive(ResurrectionEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview()
                .addRegisteredAction(
                        new RegisteredAction("revive",
                                playerWW, api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWitchRevive(WitchResurrectionEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview()
                .addRegisteredAction(
                        new RegisteredAction("witch_revive",
                                event.getPlayerWW(), event.getTargetWW(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLoverDeath(LoverDeathEvent event) {

        List<? extends IPlayerWW> lovers = event.getLover().getLovers();
        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview()
                .addRegisteredAction(
                        new RegisteredAction("lover_death", new HashSet<>(lovers), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAmnesiacLoverDeath(AmnesiacLoverDeathEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview()
                .addRegisteredAction(
                        new RegisteredAction("amnesiac_lover_death",
                                event.getPlayerWW1(), event.getPlayerWW2(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCursedLoverDeath(CursedLoverDeathEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview()
                .addRegisteredAction(
                        new RegisteredAction("cursed_lover_death",
                                event.getPlayerWW1(), event.getPlayerWW2(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onProtection(ProtectionEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW targetWW = event.getTargetWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("protection",
                playerWW, targetWW, api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onModel(ModelEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW targetWW = event.getTargetWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("model",
                playerWW, targetWW, api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCharmed(CharmEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW targetWW = event.getTargetWW();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("charmed",
                playerWW, targetWW, api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEnchanted(EnchantedEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("enchanted",
                event.getPlayerWW(), event.getTargetWW(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEnchantedAll(AllPlayerEnchantedEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("all_enchanted",
                event.getPlayerWW(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onFindFlute(FindFluteEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("find_flute",
                event.getPlayerWW(), event.getTargetWW(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGiveFlute(GiveFluteEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("give_flute", event.getPlayerWW(), event.getTargetWW(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCursed(CurseEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW targetWW = event.getTargetWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("cursed", playerWW, targetWW, api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDesignedLover(CupidLoversEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        Set<IPlayerWW> lovers = event.getPlayerWWS();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("designed_lover", playerWW, lovers, api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSteal(StealEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW thiefWW = event.getThiefWW();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("steal", thiefWW, playerWW, api.getTimer(), event.getRole()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGrowl(GrowlEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();
        Set<IPlayerWW> growled = event.getPlayerWWS();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("growl", playerWW, growled, api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBeginSniff(BeginSniffEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW targetWW = event.getTargetWW();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("begin_smell", playerWW, targetWW, api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBeginCharm(BeginCharmEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW targetWW = event.getTargetWW();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("begin_charm", playerWW, targetWW, api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSniff(SniffEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW targetWW = event.getTargetWW();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("sniff", playerWW, targetWW, api.getTimer(), event.isWereWolf() ? "werewolf.role.fox.werewolf" : "werewolf.role.fox.not_werewolf"));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSee(SeerEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW targetWW = event.getTargetWW();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("see", playerWW, targetWW, api.getTimer(), event.getCamp()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTrack(TrackEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW targetWW = event.getTargetWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("track", playerWW, targetWW, api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTrouble(TroubleMakerEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW targetWW = event.getTargetWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("trouble", playerWW, targetWW, api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGiveBook(LibrarianRequestEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW targetWW = event.getTargetWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("give_book", playerWW, targetWW, api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLibrarianDeath(LibrarianDeathEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("librarian_death", playerWW, api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEnquire(InvestigateEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();
        Set<IPlayerWW> IPlayerWWS = event.getPlayerWWs();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("enquire", playerWW, IPlayerWWS, api.getTimer(), event.isSameCamp() ? "werewolf.role.detective.same_camp" : "werewolf.role.detective.opposing_camp"));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTroubleDeath(TroubleMakerDeathEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("trouble_maker_death", playerWW, api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInvisible(InvisibleEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("invisible", playerWW, api.getTimer(), event.isInvisible() ? "invisible" : "visible"));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onVoteSee(SeeVoteEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("see_vote", playerWW, api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onVoteCancel(CancelVoteEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("cancel_vote", playerWW, event.getVoteWW(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onVote(VoteEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW targetWW = event.getTargetWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("vote", playerWW, targetWW, api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onNewWereWolf(NewWereWolfEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("new_werewolf", playerWW, api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onUseMask(UseMaskEvent event) {

        if (event.isCancelled()) return;
        String[] masks = {"mask_strength", "mask_speed", "mask_resistance"};
        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("mask", playerWW, api.getTimer(), masks[event.getMask()]));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWildChildTransformation(WildChildTransformationEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW modelWW = event.getModel();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("wild_child_transformation", playerWW, modelWW, api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAmnesiacTransformation(AmnesiacTransformationEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW villager = event.getVillager();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("amnesiac_transformation", playerWW, villager, api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onVoteResult(VoteResultEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("vote_result", playerWW, api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSuccubusResurrection(SuccubusResurrectionEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW targetWW = event.getTargetWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("succubus_resurrection", playerWW, targetWW, api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSisterDeathName(SisterSeeNameEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("sister_see_name", event.getPlayerWW(), event.getTargetWW(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSisterDeathRole(SisterSeeRoleEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("sister_role_name", event.getPlayerWW(), event.getTargetWW(), api.getTimer(), event.getTargetWW() == null ? "pve" : event.getTargetWW().getRole().getKey()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDay(DayEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("day", api.getTimer(), event.getNumber()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onNight(NightEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("night", api.getTimer(), event.getNumber()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLoverReveal(RevealLoversEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();
        for (ILover lover : event.getLovers()) {
            main.getCurrentGameReview().addRegisteredAction(new RegisteredAction(lover.isKey(LoverType.CURSED_LOVER.getKey()) ? "cursed_lover_revelation" : "lover_revelation", Sets.newHashSet(lover.getLovers()), api.getTimer()));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onStudLoverReveal(StudLoverEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("stud_lover", event.getPlayerWW(), event.getTargetWW(), api.getTimer()));

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAmnesiacLoverReveal(RevealAmnesiacLoversEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("amnesiac_lover_revelation", event.getPlayerWWS(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onNewDisplayRole(NewDisplayRole event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("new_display_role", event.getPlayerWW(), api.getTimer(), event.getNewDisplayRole()));

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTarget(AngelTargetEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW targetWW = event.getTargetWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("angel_target", playerWW, targetWW, api.getTimer()));
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onTargetDeath(AngelTargetDeathEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW targetWW = event.getTargetWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("angel_target_death", playerWW, targetWW, api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSKKill(SerialKillerEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW targetWW = event.getTargetWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("sk_target_death", playerWW, targetWW, api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onFallenAngelKill(FallenAngelTargetDeathEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW targetWW = event.getTargetWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("fallen_angel_kill_target", playerWW, targetWW, api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAngelChoice(AngelChoiceEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("angel_choice", playerWW, api.getTimer(), event.getChoice().toString()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWereWolfList(WereWolfListEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf_list", api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRepartition(RepartitionEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("repartition", api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTroll(TrollEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("troll", api.getTimer(), api.getConfig().getTrollKey()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTrollLover(TrollLoverEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("troll_lover", api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPVP(PVPEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("pvp", api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInvulnerability(InvulnerabilityEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("invulnerability", api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBorderStart(BorderStartEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("border_start", api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBorderStop(BorderStopEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("border_stop", api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDiggingEnd(DiggingEndEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("digging_end", api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDon(DonEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW receiverWW = event.getReceiverWW();

        main.getCurrentGameReview().addRegisteredAction(
                new RegisteredAction("don", playerWW,
                        receiverWW, api.getTimer(), event.getDon()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGiveBack(LibrarianGiveBackEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW targetWW = event.getTargetWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("give_back_book", playerWW, targetWW, api.getTimer(), event.getInfo()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAngelRegeneration(RegenerationEvent event) {
        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();

        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW targetWW = event.getTargetWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("angel_regeneration", playerWW, targetWW, api.getTimer()));

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDogTransformation(WolfDogChooseWereWolfForm event) {
        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();

        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("wolf_dog_choose_wolf", playerWW, api.getTimer()));

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRivalAnnouncement(RivalAnnouncementEvent event) {
        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();


        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("rival_announcement", event.getPlayerWW(), new HashSet<>(event.getPlayerWWs()), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLoverRivalDeath(RivalLoverDeathEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("rival_lover_death", event.getPlayerWW(), new HashSet<>(event.getPlayerWWs()), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRivalLover(RivalLoverEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("rival_lover", event.getPlayerWW(), event.getTargetWW(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onVillageIdiotResurrection(VillageIdiotEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("idiot_village", event.getPlayerWW(), event.getTargetWW(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMysticalReveal(MysticalWerewolfRevelationEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("mystical_reveal", event.getPlayerWW(), event.getTargetWW(), api.getTimer(), event.getTargetWW().getRole().getKey()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWereWolfChat(WereWolfChatEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf_message", event.getPlayerWW(), api.getTimer(), event.getMessage()));

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPriestessSpec(PriestessEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("priestess_spec", event.getPlayerWW(), event.getTargetWW(), api.getTimer(), event.getCamp()));

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onVillagerKit(VillagerKitEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("villager_kit",
                event.getPlayerWW(), api.getTimer(), event.getKit()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onExposed(ExposedEvent event) {
        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("exposed",
                event.getPlayerWW(), new HashSet<>(event.getRoles()), api.getTimer()));

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLootBox_(LootBoxEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("event_loot_box",
                event.getPlayerWW(), api.getTimer(), event.getChestNumbers()));

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onFindAllLootBox(FindAllLootBoxEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("find_all_loot_box",
                api.getTimer()));

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRandomInfection(InfectionRandomEvent event) {
        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("random_infection",
                event.getPlayerWW(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBearingRitual(BearingRitualEvent event) {
        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("bearing_ritual_event",
                api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPutrefaction(PutrefactionEvent event) {
        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("putrefaction_event",
                api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGodMiracle(GodMiracleEvent event) {
        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("god_miracle_event",
                event.getPlayerWW(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSwap(SwapEvent event) {
        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("swap_event",
                event.getPlayerWW1(), event.getPlayerWW2(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLoneWolf(LoneWolfEvent event) {
        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("lone_wolf",
                event.getPlayerWW(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGuard(GuardEvent event) {
        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("guard_event",
                event.getPlayerWW(), event.getTargetWW(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGuardResurrection(GuardResurrectionEvent event) {
        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("guard_resurrection",
                event.getPlayerWW(), event.getTargetWW(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTrouple(TroupleEvent event) {
        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("trouple_event",
                event.getPlayerWW(), event.getPlayerWWs(), api.getTimer()));
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onRegisterAvenger(RegisterAvengerListEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("avenger_list_register",
                playerWW, event.getTargetWW(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeathListAvenger(DeathAvengerListEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("avenger_list_death",
                playerWW, event.getTargetWW(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDrunkenWerewolf(DrunkenWereWolfEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("drunken_werewolf",
                event.getPlayerWW(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAmnesic(AmnesicEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("amnesic_design",
                event.getPlayerWW(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAmnesicReveal(AmnesicTransformEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("amnesic_transform",
                event.getPlayerWW(), event.getTargetWW(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onShaman(ShamanEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("shaman",
                event.getPlayerWW(), event.getTargetWW(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onOracle(OracleEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("oracle_see",
                event.getPlayerWW(), event.getTargetWW(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCharmedDeath(CharmedDeathEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("charmed_death_event",
                event.getPlayerWW(), event.getTargetWW(),api.getTimer(), event.isBeforeCountDown()?"before_count_down":"after_count_down"));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCharmerGetEffect(CharmerGetEffectDeathEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("charmer_get_effect",
                event.getPlayerWW(),new HashSet<>(event.getLover().getLovers()), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCharmerCharmed(CharmerEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("charmer_charmed",
                event.getPlayerWW(),event.getTargetWW(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWispTeleport(WillOTheWispTeleportEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("will_o_the_wisp_teleport",
                event.getPlayerWW(),api.getTimer(), event.getNumberUse()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWispTeleport(WillOTheWispRecoverRoleEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("will_o_the_wisp_recover_role",
                event.getPlayerWW(),event.getTargetWW(),api.getTimer(),event.getRoleKey()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTwin(TwinRevealEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("twin_reveal",
                event.getPlayerWW(),event.getPlayerWWS(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTwinList(TwinListEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("twin_list",
                event.getPlayerWW(),event.getPlayerWWS(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTwinRole(TwinRoleEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("twin_role",
                event.getPlayerWW(),event.getTargetWW(),api.getTimer(),event.getTargetWW().getRole().getKey()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWerewolfHowler(HowlEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf_howler",
                event.getPlayerWW(),event.getPlayerWWS(),event.getNotWerewolfSize(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAnalystAnalyst(AnalystExtraDetailsEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("analyst_analyst",
                event.getPlayerWW(),event.getTargetWW(),api.getTimer(),event.getPotions().stream().map(PotionEffectType::getName).collect(Collectors.joining(", "))));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAnalystSee(AnalystEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("analyst_see",
                event.getPlayerWW(),event.getTargetWW(),api.getTimer(),event.hasEffect()?"werewolf.role.analyst.has_effects":"werewolf.role.analyst.no_effects"));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDruidInfo(DruidUsePowerEvent event) {

        if (event.isCancelled()) return;

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("druid_power",
                event.getPlayerWW(),api.getTimer(),event.getDarkAura()));
    }

}
