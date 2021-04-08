package io.github.ph1lou.werewolfplugin.statistiks;

import com.google.common.collect.Sets;
import io.github.ph1lou.werewolfapi.ILover;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.enums.LoverType;
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
import io.github.ph1lou.werewolfapi.events.random_events.BearingRitualEvent;
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
import io.github.ph1lou.werewolfapi.events.roles.angel.AngelChoiceEvent;
import io.github.ph1lou.werewolfapi.events.roles.angel.AngelTargetDeathEvent;
import io.github.ph1lou.werewolfapi.events.roles.angel.AngelTargetEvent;
import io.github.ph1lou.werewolfapi.events.roles.angel.FallenAngelTargetDeathEvent;
import io.github.ph1lou.werewolfapi.events.roles.angel.RegenerationEvent;
import io.github.ph1lou.werewolfapi.events.roles.avenger_werewolf.DeathAvengerListEvent;
import io.github.ph1lou.werewolfapi.events.roles.avenger_werewolf.RegisterAvengerListEvent;
import io.github.ph1lou.werewolfapi.events.roles.bear_trainer.GrowlEvent;
import io.github.ph1lou.werewolfapi.events.roles.comedian.UseMaskEvent;
import io.github.ph1lou.werewolfapi.events.roles.detective.InvestigateEvent;
import io.github.ph1lou.werewolfapi.events.roles.elder.ElderResurrectionEvent;
import io.github.ph1lou.werewolfapi.events.roles.falsifier_werewolf.NewDisplayRole;
import io.github.ph1lou.werewolfapi.events.roles.flute_player.EnchantedEvent;
import io.github.ph1lou.werewolfapi.events.roles.fox.BeginSniffEvent;
import io.github.ph1lou.werewolfapi.events.roles.fox.SniffEvent;
import io.github.ph1lou.werewolfapi.events.roles.grim_werewolf.GrimEvent;
import io.github.ph1lou.werewolfapi.events.roles.guard.GuardEvent;
import io.github.ph1lou.werewolfapi.events.roles.guard.GuardResurrectionEvent;
import io.github.ph1lou.werewolfapi.events.roles.infect_father_of_the_wolves.InfectionEvent;
import io.github.ph1lou.werewolfapi.events.roles.librarian.LibrarianDeathEvent;
import io.github.ph1lou.werewolfapi.events.roles.librarian.LibrarianGiveBackEvent;
import io.github.ph1lou.werewolfapi.events.roles.librarian.LibrarianRequestEvent;
import io.github.ph1lou.werewolfapi.events.roles.mystical_werewolf.MysticalWerewolfRevelationEvent;
import io.github.ph1lou.werewolfapi.events.roles.priestess.PriestessEvent;
import io.github.ph1lou.werewolfapi.events.roles.protector.ProtectionEvent;
import io.github.ph1lou.werewolfapi.events.roles.raven.CurseEvent;
import io.github.ph1lou.werewolfapi.events.roles.rival.RivalAnnouncementEvent;
import io.github.ph1lou.werewolfapi.events.roles.rival.RivalLoverDeathEvent;
import io.github.ph1lou.werewolfapi.events.roles.rival.RivalLoverEvent;
import io.github.ph1lou.werewolfapi.events.roles.seer.SeeVoteEvent;
import io.github.ph1lou.werewolfapi.events.roles.seer.SeerEvent;
import io.github.ph1lou.werewolfapi.events.roles.serial_killer.SerialKillerEvent;
import io.github.ph1lou.werewolfapi.events.roles.sister.SisterSeeNameEvent;
import io.github.ph1lou.werewolfapi.events.roles.sister.SisterSeeRoleEvent;
import io.github.ph1lou.werewolfapi.events.roles.stud.StudLoverEvent;
import io.github.ph1lou.werewolfapi.events.roles.succubus.BeginCharmEvent;
import io.github.ph1lou.werewolfapi.events.roles.succubus.CharmEvent;
import io.github.ph1lou.werewolfapi.events.roles.succubus.SuccubusResurrectionEvent;
import io.github.ph1lou.werewolfapi.events.roles.trapper.TrackEvent;
import io.github.ph1lou.werewolfapi.events.roles.trouble_maker.TroubleMakerDeathEvent;
import io.github.ph1lou.werewolfapi.events.roles.trouble_maker.TroubleMakerEvent;
import io.github.ph1lou.werewolfapi.events.roles.village_idiot.VillageIdiotEvent;
import io.github.ph1lou.werewolfapi.events.roles.villager.VillagerKitEvent;
import io.github.ph1lou.werewolfapi.events.roles.wild_child.ModelEvent;
import io.github.ph1lou.werewolfapi.events.roles.wild_child.WildChildTransformationEvent;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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

        if (!((GameManager) main.getWereWolfAPI()).isDebug()) {
            if (main.getCurrentGameReview().getPlayerSize() < 17) {
                Bukkit.getLogger().warning("[WereWolfPlugin] Statistiks no send because player size < 17");
                return;
            }

            if (event.getWereWolfAPI().getScore().getTimer() < 3600) {
                Bukkit.getLogger().warning("[WereWolfPlugin] Statistiks no send because game duration < 1h");
                return;
            }
        }

        try {

            URL url = new URL("http://ph1lou.fr:15000/infos2");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
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

                TextComponent msg = new TextComponent(main.getWereWolfAPI().translate("werewolf.statistics.message"));
                msg.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
                        String.format("https://ph1lou.fr/werewolfstat/detail.php?id=%s", response.toString().replaceAll("\"", ""))));
                BukkitUtils.scheduleSyncDelayedTask(() -> Bukkit.getOnlinePlayers()
                        .forEach(player -> player.spigot().sendMessage(msg)), 100);
            } catch (Exception ignored) {

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWin(WinEvent event) {
        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("win", event.getPlayers(), api.getScore().getTimer(), event.getRole()));
        main.getCurrentGameReview().end(event.getRole(), event.getPlayers());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGameStart(StartEvent event) {
        main.setCurrentGameReview(new GameReview(main.getWereWolfAPI()));
        String serverUUID = main.getConfig().getString("server_uuid");
        if (serverUUID != null) {
            try {
                main.getCurrentGameReview().setServerUUID(UUID.fromString(serverUUID));
            } catch (Exception ignored) {
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event) {

        Player player = event.getEntity();
        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        UUID playerUUID = player.getUniqueId();
        IPlayerWW playerWW = api.getPlayerWW(playerUUID);

        if (playerWW == null) return;

        Player killer = player.getKiller();
        if (killer == null) return;

        UUID killerUUID = killer.getUniqueId();
        IPlayerWW killerWW = api.getPlayerWW(killerUUID);

        if (main.getCurrentGameReview() == null) return;

        if (!api.isState(StateGame.GAME)) return;

        if (playerWW.isState(StatePlayer.DEATH)) return;

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("kill", playerWW, killerWW, api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onFinalDeath(FinalDeathEvent event) {
        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("final_kill", playerWW, event.getPlayerWW().getLastKiller().isPresent() ?
                event.getPlayerWW().getLastKiller().get() : null, api.getScore().getTimer()));
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onCustom(CustomEvent event) {

        if (event.isCancelled()) return;

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction(event.getEvent(), event.getPlayerWW(), event.getPlayerWWS(), api.getScore().getTimer(), event.getExtraInfo(), event.getExtraInt()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInfection(InfectionEvent event) {

        if (event.isCancelled()) return;


        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("infection", event.getPlayerWW(), event.getTargetWW(), api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGrim(GrimEvent event) {

        if (event.isCancelled()) return;


        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("grim", event.getPlayerWW(), event.getTargetWW(), api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onReviveElder(ElderResurrectionEvent event) {

        if (event.isCancelled()) return;

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("elder_revive", playerWW, api.getScore().getTimer(), event.isKillerAVillager() ? "elder_kill_by_villager" : "elder_not_kill_by_villager"));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRevive(ResurrectionEvent event) {

        if (event.isCancelled()) return;

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview()
                .addRegisteredAction(
                        new RegisteredAction("revive", playerWW, api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWitchRevive(WitchResurrectionEvent event) {

        if (event.isCancelled()) return;

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview()
                .addRegisteredAction(
                        new RegisteredAction("witch_revive", event.getPlayerWW(), event.getTargetWW(), api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLoverDeath(LoverDeathEvent event) {

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview()
                .addRegisteredAction(
                        new RegisteredAction("lover_death", event.getPlayerWW1(), event.getPlayerWW2(), api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAmnesiacLoverDeath(AmnesiacLoverDeathEvent event) {

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview()
                .addRegisteredAction(
                        new RegisteredAction("amnesiac_lover_death", event.getPlayerWW1(), event.getPlayerWW2(), api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCursedLoverDeath(CursedLoverDeathEvent event) {

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview()
                .addRegisteredAction(
                        new RegisteredAction("cursed_lover_death", event.getPlayerWW1(), event.getPlayerWW2(), api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onProtection(ProtectionEvent event) {

        if (event.isCancelled()) return;

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW targetWW = event.getTargetWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("protection", playerWW, targetWW, api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onModel(ModelEvent event) {

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW targetWW = event.getTargetWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("model", playerWW, targetWW, api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCharmed(CharmEvent event) {

        if (event.isCancelled()) return;

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW targetWW = event.getTargetWW();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("charmed", playerWW, targetWW, api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEnchanted(EnchantedEvent event) {

        if (event.isCancelled()) return;

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        Set<IPlayerWW> enchanted = event.getPlayerWWS();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("enchanted", playerWW, enchanted, api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCursed(CurseEvent event) {

        if (event.isCancelled()) return;

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW targetWW = event.getTargetWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("cursed", playerWW, targetWW, api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDesignedLover(CupidLoversEvent event) {

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        Set<IPlayerWW> lovers = event.getPlayerWWS();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("designed_lover", playerWW, lovers, api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSteal(StealEvent event) {

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW thiefWW = event.getThiefWW();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("steal", thiefWW, playerWW, api.getScore().getTimer(), event.getRole()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGrowl(GrowlEvent event) {

        if (event.isCancelled()) return;

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        Set<IPlayerWW> growled = event.getPlayerWWS();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("growl", playerWW, growled, api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBeginSniff(BeginSniffEvent event) {

        if (event.isCancelled()) return;

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW targetWW = event.getTargetWW();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("begin_smell", playerWW, targetWW, api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBeginCharm(BeginCharmEvent event) {

        if (event.isCancelled()) return;

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW targetWW = event.getTargetWW();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("begin_charm", playerWW, targetWW, api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSniff(SniffEvent event) {

        if (event.isCancelled()) return;

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW targetWW = event.getTargetWW();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("sniff", playerWW, targetWW, api.getScore().getTimer(), event.isWereWolf() ? "werewolf.role.fox.werewolf" : "werewolf.role.fox.not_werewolf"));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSee(SeerEvent event) {

        if (event.isCancelled()) return;

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW targetWW = event.getTargetWW();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("see", playerWW, targetWW, api.getScore().getTimer(), event.getCamp()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTrack(TrackEvent event) {

        if (event.isCancelled()) return;

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW targetWW = event.getTargetWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("track", playerWW, targetWW, api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTrouble(TroubleMakerEvent event) {

        if (event.isCancelled()) return;

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW targetWW = event.getTargetWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("trouble", playerWW, targetWW, api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGiveBook(LibrarianRequestEvent event) {

        if (event.isCancelled()) return;

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW targetWW = event.getTargetWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("give_book", playerWW, targetWW, api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLibrarianDeath(LibrarianDeathEvent event) {

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("librarian_death", playerWW, api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEnquire(InvestigateEvent event) {

        if (event.isCancelled()) return;

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        Set<IPlayerWW> IPlayerWWS = event.getPlayerWWs();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("enquire", playerWW, IPlayerWWS, api.getScore().getTimer(), event.isSameCamp() ? "werewolf.role.detective.same_camp" : "werewolf.role.detective.opposing_camp"));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTroubleDeath(TroubleMakerDeathEvent event) {
        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("trouble_maker_death", playerWW, api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInvisible(InvisibleEvent event) {
        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("invisible", playerWW, api.getScore().getTimer(), event.isInvisible() ? "invisible" : "visible"));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onVoteSee(SeeVoteEvent event) {

        if (event.isCancelled()) return;

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("see_vote", playerWW, api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onVoteCancel(CancelVoteEvent event) {

        if (event.isCancelled()) return;

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("cancel_vote", playerWW, event.getVoteWW(), api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onVote(VoteEvent event) {

        if (event.isCancelled()) return;

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW targetWW = event.getTargetWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("vote", playerWW, targetWW, api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onNewWereWolf(NewWereWolfEvent event) {

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("new_werewolf", playerWW, api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onUseMask(UseMaskEvent event) {

        if (event.isCancelled()) return;
        String[] masks = {"mask_strength", "mask_speed", "mask_resistance"};
        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("mask", playerWW, api.getScore().getTimer(), masks[event.getMask()]));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWildChildTransformation(WildChildTransformationEvent event) {

        if (event.isCancelled()) return;

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW modelWW = event.getModel();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("wild_child_transformation", playerWW, modelWW, api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAmnesiacTransformation(AmnesiacTransformationEvent event) {

        if (event.isCancelled()) return;

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW villager = event.getVillager();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("amnesiac_transformation", playerWW, villager, api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onVoteResult(VoteResultEvent event) {

        if (event.isCancelled()) return;

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("vote_result", playerWW, api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSuccubusResurrection(SuccubusResurrectionEvent event) {

        if (event.isCancelled()) return;

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW targetWW = event.getTargetWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("succubus_resurrection", playerWW, targetWW, api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSisterDeathName(SisterSeeNameEvent event) {

        if (event.isCancelled()) return;

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("sister_see_name", event.getPlayerWW(), event.getTargetWW(), api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSisterDeathRole(SisterSeeRoleEvent event) {

        if (event.isCancelled()) return;

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("sister_role_name", event.getPlayerWW(), event.getTargetWW(), api.getScore().getTimer(), event.getTargetWW() == null ? "pve" : event.getTargetWW().getRole().getKey()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDay(DayEvent event) {

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("day", api.getScore().getTimer(), event.getNumber()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onNight(NightEvent event) {
        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("night", api.getScore().getTimer(), event.getNumber()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLoverReveal(RevealLoversEvent event) {
        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        for (ILover lover : event.getLovers()) {
            main.getCurrentGameReview().addRegisteredAction(new RegisteredAction(lover.isKey(LoverType.CURSED_LOVER.getKey()) ? "cursed_lover_revelation" : "lover_revelation", Sets.newHashSet(lover.getLovers()), api.getScore().getTimer()));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onStudLoverReveal(StudLoverEvent event) {
        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("stud_lover", event.getPlayerWW(), event.getTargetWW(), api.getScore().getTimer()));

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAmnesiacLoverReveal(RevealAmnesiacLoversEvent event) {
        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("amnesiac_lover_revelation", event.getPlayerWWS(), api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onNewDisplayRole(NewDisplayRole event) {

        if (event.isCancelled()) return;

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("new_display_role", event.getPlayerWW(), api.getScore().getTimer(), event.getNewDisplayRole()));

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTarget(AngelTargetEvent event) {

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW targetWW = event.getTargetWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("angel_target", playerWW, targetWW, api.getScore().getTimer()));
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onTargetDeath(AngelTargetDeathEvent event) {
        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW targetWW = event.getTargetWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("angel_target_death", playerWW, targetWW, api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSKKill(SerialKillerEvent event) {
        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW targetWW = event.getTargetWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("sk_target_death", playerWW, targetWW, api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onFallenAngelKill(FallenAngelTargetDeathEvent event) {
        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW targetWW = event.getTargetWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("fallen_angel_kill_target", playerWW, targetWW, api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAngelChoice(AngelChoiceEvent event) {
        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("angel_choice", playerWW, api.getScore().getTimer(), event.getChoice().toString()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWereWolfList(WereWolfListEvent event) {
        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf_list", api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRepartition(RepartitionEvent event) {
        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("repartition", api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTroll(TrollEvent event) {
        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("troll", api.getScore().getTimer(), api.getConfig().getTrollKey()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTrollLover(TrollLoverEvent event) {
        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("troll_lover", api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPVP(PVPEvent event) {
        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("pvp", api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInvulnerability(InvulnerabilityEvent event) {
        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("invulnerability", api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBorderStart(BorderStartEvent event) {
        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("border_start", api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBorderStop(BorderStopEvent event) {
        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("border_stop", api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDiggingEnd(DiggingEndEvent event) {
        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("digging_end", api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDon(DonEvent event) {

        if (event.isCancelled()) return;

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW receiverWW = event.getReceiverWW();

        main.getCurrentGameReview().addRegisteredAction(
                new RegisteredAction("don", playerWW,
                        receiverWW, api.getScore().getTimer(), event.getDon()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGiveBack(LibrarianGiveBackEvent event) {

        if (event.isCancelled()) return;

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW targetWW = event.getTargetWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("give_back_book", playerWW, targetWW, api.getScore().getTimer(), event.getInfo()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAngelRegeneration(RegenerationEvent event) {
        if (event.isCancelled()) return;

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();

        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW targetWW = event.getTargetWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("angel_regeneration", playerWW, targetWW, api.getScore().getTimer()));

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDogTransformation(WolfDogChooseWereWolfForm event) {
        if (event.isCancelled()) return;

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();

        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("wolf_dog_choose_wolf", playerWW, api.getScore().getTimer()));

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRivalAnnouncement(RivalAnnouncementEvent event) {
        if (event.isCancelled()) return;

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();


        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("rival_announcement", event.getPlayerWW(), new HashSet<>(event.getPlayerWWs()), api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLoverRivalDeath(RivalLoverDeathEvent event) {

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("rival_lover_death", event.getPlayerWW(), new HashSet<>(event.getPlayerWWs()), api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRivalLover(RivalLoverEvent event) {

        if (event.isCancelled()) return;

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("rival_lover", event.getPlayerWW(), event.getTargetWW(), api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onVillageIdiotResurrection(VillageIdiotEvent event) {

        if (event.isCancelled()) return;

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("idiot_village", event.getPlayerWW(), event.getTargetWW(), api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMysticalReveal(MysticalWerewolfRevelationEvent event) {

        if (event.isCancelled()) return;

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("mystical_reveal", event.getPlayerWW(), event.getTargetWW(), api.getScore().getTimer(), event.getTargetWW().getRole().getKey()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWereWolfChat(WereWolfChatEvent event) {

        if (event.isCancelled()) return;

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf_message", event.getPlayerWW(), api.getScore().getTimer(), event.getMessage()));

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPriestessSpec(PriestessEvent event) {

        if (event.isCancelled()) return;

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("priestess_spec", event.getPlayerWW(), event.getTargetWW(), api.getScore().getTimer(), event.getCamp()));

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onVillagerKit(VillagerKitEvent event) {

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("villager_kit", event.getPlayerWW(), api.getScore().getTimer(), event.getKit()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onExposed(ExposedEvent event) {
        if (event.isCancelled()) return;

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("exposed", event.getPlayerWW(), new HashSet<>(event.getRoles()), api.getScore().getTimer()));

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLootBox_(LootBoxEvent event) {

        if (event.isCancelled()) return;

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("event_loot_box", event.getPlayerWW(), api.getScore().getTimer(), event.getChestNumbers()));

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onFindAllLootBox(FindAllLootBoxEvent event) {

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("find_all_loot_box", api.getScore().getTimer()));

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRandomInfection(InfectionRandomEvent event) {
        if (event.isCancelled()) return;

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("random_infection", event.getPlayerWW(), api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBearingRitual(BearingRitualEvent event) {
        if (event.isCancelled()) return;

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("bearing_ritual_event", api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPutrefaction(PutrefactionEvent event) {
        if (event.isCancelled()) return;

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("putrefaction_event", api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGodMiracle(GodMiracleEvent event) {
        if (event.isCancelled()) return;

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("god_miracle_event", event.getPlayerWW(), api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSwap(SwapEvent event) {
        if (event.isCancelled()) return;

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("swap_event", event.getPlayerWW1(), event.getPlayerWW2(), api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLoneWolf(LoneWolfEvent event) {
        if (event.isCancelled()) return;

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("lone_wolf", event.getPlayerWW(), api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGuard(GuardEvent event) {
        if (event.isCancelled()) return;

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("guard_event", event.getPlayerWW(), event.getTargetWW(), api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGuardResurrection(GuardResurrectionEvent event) {
        if (event.isCancelled()) return;

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("guard_resurrection", event.getPlayerWW(), event.getTargetWW(), api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTrouple(TroupleEvent event) {
        if (event.isCancelled()) return;

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("trouple_event", event.getPlayerWW(), event.getPlayerWWs(), api.getScore().getTimer()));
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onRegisterAvenger(RegisterAvengerListEvent event) {

        if (event.isCancelled()) return;

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("avenger_list_register", playerWW, event.getTargetWW(), api.getScore().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeathListAvenger(DeathAvengerListEvent event) {

        if (event.isCancelled()) return;

        io.github.ph1lou.werewolfapi.WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("avenger_list_death", playerWW, event.getTargetWW(), api.getScore().getTimer()));
    }
}
