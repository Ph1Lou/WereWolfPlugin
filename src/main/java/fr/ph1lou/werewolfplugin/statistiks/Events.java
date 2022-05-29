package fr.ph1lou.werewolfplugin.statistiks;

import com.google.common.collect.Sets;
import fr.ph1lou.werewolfapi.basekeys.LoverBase;
import fr.ph1lou.werewolfapi.events.CustomEvent;
import fr.ph1lou.werewolfapi.events.TrollEvent;
import fr.ph1lou.werewolfapi.events.TrollLoverEvent;
import fr.ph1lou.werewolfapi.events.game.configs.LoneWolfEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.NightEvent;
import fr.ph1lou.werewolfapi.events.game.game_cycle.StartEvent;
import fr.ph1lou.werewolfapi.events.game.game_cycle.StopEvent;
import fr.ph1lou.werewolfapi.events.game.game_cycle.WinEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.PlayerWWDeathEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.ResurrectionEvent;
import fr.ph1lou.werewolfapi.events.game.timers.BorderStartEvent;
import fr.ph1lou.werewolfapi.events.game.timers.BorderStopEvent;
import fr.ph1lou.werewolfapi.events.game.timers.DiggingEndEvent;
import fr.ph1lou.werewolfapi.events.game.timers.InvulnerabilityEvent;
import fr.ph1lou.werewolfapi.events.game.timers.PVPEvent;
import fr.ph1lou.werewolfapi.events.game.timers.RepartitionEvent;
import fr.ph1lou.werewolfapi.events.game.timers.WereWolfListEvent;
import fr.ph1lou.werewolfapi.events.game.vote.NewVoteResultEvent;
import fr.ph1lou.werewolfapi.events.game.vote.VoteEvent;
import fr.ph1lou.werewolfapi.events.game.vote.VoteResultEvent;
import fr.ph1lou.werewolfapi.events.lovers.AmnesiacLoverDeathEvent;
import fr.ph1lou.werewolfapi.events.lovers.CupidLoversEvent;
import fr.ph1lou.werewolfapi.events.lovers.CursedLoverDeathEvent;
import fr.ph1lou.werewolfapi.events.lovers.DonEvent;
import fr.ph1lou.werewolfapi.events.lovers.LoverDeathEvent;
import fr.ph1lou.werewolfapi.events.lovers.RevealAmnesiacLoversEvent;
import fr.ph1lou.werewolfapi.events.lovers.RevealLoversEvent;
import fr.ph1lou.werewolfapi.events.random_events.AmnesicEvent;
import fr.ph1lou.werewolfapi.events.random_events.AmnesicTransformEvent;
import fr.ph1lou.werewolfapi.events.random_events.BearingRitualEvent;
import fr.ph1lou.werewolfapi.events.random_events.DrunkenWereWolfEvent;
import fr.ph1lou.werewolfapi.events.random_events.ExposedEvent;
import fr.ph1lou.werewolfapi.events.random_events.FindAllLootBoxEvent;
import fr.ph1lou.werewolfapi.events.random_events.GodMiracleEvent;
import fr.ph1lou.werewolfapi.events.random_events.InfectionRandomEvent;
import fr.ph1lou.werewolfapi.events.random_events.LootBoxEvent;
import fr.ph1lou.werewolfapi.events.random_events.MysanthropeSisterEvent;
import fr.ph1lou.werewolfapi.events.random_events.PutrefactionEvent;
import fr.ph1lou.werewolfapi.events.random_events.RumorsEvent;
import fr.ph1lou.werewolfapi.events.random_events.RumorsWriteEvent;
import fr.ph1lou.werewolfapi.events.random_events.SwapEvent;
import fr.ph1lou.werewolfapi.events.random_events.TroupleEvent;
import fr.ph1lou.werewolfapi.events.roles.InvisibleEvent;
import fr.ph1lou.werewolfapi.events.roles.StealEvent;
import fr.ph1lou.werewolfapi.events.roles.amnesiac.AmnesiacTransformationEvent;
import fr.ph1lou.werewolfapi.events.roles.analyst.AnalystEvent;
import fr.ph1lou.werewolfapi.events.roles.analyst.AnalystExtraDetailsEvent;
import fr.ph1lou.werewolfapi.events.roles.angel.AngelChoiceEvent;
import fr.ph1lou.werewolfapi.events.roles.angel.AngelTargetDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.angel.AngelTargetEvent;
import fr.ph1lou.werewolfapi.events.roles.angel.FallenAngelTargetDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.angel.RegenerationEvent;
import fr.ph1lou.werewolfapi.events.roles.avenger_werewolf.DeathAvengerListEvent;
import fr.ph1lou.werewolfapi.events.roles.avenger_werewolf.RegisterAvengerListEvent;
import fr.ph1lou.werewolfapi.events.roles.barbarian.BarbarianEvent;
import fr.ph1lou.werewolfapi.events.roles.bear_trainer.GrowlEvent;
import fr.ph1lou.werewolfapi.events.roles.benefactor.BenefactorGiveHeartEvent;
import fr.ph1lou.werewolfapi.events.roles.charmer.CharmedDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.charmer.CharmerEvent;
import fr.ph1lou.werewolfapi.events.roles.charmer.CharmerGetEffectDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.citizen.CitizenCancelVoteEvent;
import fr.ph1lou.werewolfapi.events.roles.citizen.CitizenSeeVoteEvent;
import fr.ph1lou.werewolfapi.events.roles.citizen.CitizenSeeWerewolfVoteEvent;
import fr.ph1lou.werewolfapi.events.roles.comedian.UseMaskEvent;
import fr.ph1lou.werewolfapi.events.roles.detective.InvestigateEvent;
import fr.ph1lou.werewolfapi.events.roles.devoted_servant.DevotedServantEvent;
import fr.ph1lou.werewolfapi.events.roles.druid.DruidUsePowerEvent;
import fr.ph1lou.werewolfapi.events.roles.elder.ElderResurrectionEvent;
import fr.ph1lou.werewolfapi.events.roles.falsifier_werewolf.NewDisplayRole;
import fr.ph1lou.werewolfapi.events.roles.flute_player.AllPlayerEnchantedEvent;
import fr.ph1lou.werewolfapi.events.roles.flute_player.EnchantedEvent;
import fr.ph1lou.werewolfapi.events.roles.flute_player.FindFluteEvent;
import fr.ph1lou.werewolfapi.events.roles.flute_player.GiveFluteEvent;
import fr.ph1lou.werewolfapi.events.roles.fox.BeginSniffEvent;
import fr.ph1lou.werewolfapi.events.roles.fox.SniffEvent;
import fr.ph1lou.werewolfapi.events.roles.fruitmerchant.FruitMerchantCommandEvent;
import fr.ph1lou.werewolfapi.events.roles.fruitmerchant.FruitMerchantDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.fruitmerchant.FruitMerchantRecoverInformationEvent;
import fr.ph1lou.werewolfapi.events.roles.gravedigger.TriggerGravediggerClueEvent;
import fr.ph1lou.werewolfapi.events.roles.grim_werewolf.GrimEvent;
import fr.ph1lou.werewolfapi.events.roles.guard.GuardEvent;
import fr.ph1lou.werewolfapi.events.roles.guard.GuardResurrectionEvent;
import fr.ph1lou.werewolfapi.events.roles.howling_werewolf.HowlEvent;
import fr.ph1lou.werewolfapi.events.roles.hunter.HunterShotEvent;
import fr.ph1lou.werewolfapi.events.roles.illusionist.IllusionistActivatePowerEvent;
import fr.ph1lou.werewolfapi.events.roles.illusionist.IllusionistAddPlayerOnWerewolfListEvent;
import fr.ph1lou.werewolfapi.events.roles.illusionist.IllusionistGetNamesEvent;
import fr.ph1lou.werewolfapi.events.roles.infect_father_of_the_wolves.InfectionEvent;
import fr.ph1lou.werewolfapi.events.roles.interpreter.InterpreterEvent;
import fr.ph1lou.werewolfapi.events.roles.librarian.LibrarianDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.librarian.LibrarianGiveBackEvent;
import fr.ph1lou.werewolfapi.events.roles.librarian.LibrarianRequestEvent;
import fr.ph1lou.werewolfapi.events.roles.mystical_werewolf.MysticalWerewolfRevelationEvent;
import fr.ph1lou.werewolfapi.events.roles.necromancer.NecromancerResurrectionEvent;
import fr.ph1lou.werewolfapi.events.roles.occultist.OccultistRevealWishes;
import fr.ph1lou.werewolfapi.events.roles.occultist.WishChangeEvent;
import fr.ph1lou.werewolfapi.events.roles.oracle.OracleEvent;
import fr.ph1lou.werewolfapi.events.roles.priestess.PriestessEvent;
import fr.ph1lou.werewolfapi.events.roles.protector.ProtectionEvent;
import fr.ph1lou.werewolfapi.events.roles.raven.CurseEvent;
import fr.ph1lou.werewolfapi.events.roles.rival.RivalAnnouncementEvent;
import fr.ph1lou.werewolfapi.events.roles.rival.RivalLoverDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.rival.RivalLoverEvent;
import fr.ph1lou.werewolfapi.events.roles.scammer.ScamEvent;
import fr.ph1lou.werewolfapi.events.roles.seer.SeerEvent;
import fr.ph1lou.werewolfapi.events.roles.serial_killer.SerialKillerEvent;
import fr.ph1lou.werewolfapi.events.roles.servitor.ServitorDefinitiveMasterEvent;
import fr.ph1lou.werewolfapi.events.roles.servitor.ServitorMasterChosenEvent;
import fr.ph1lou.werewolfapi.events.roles.shaman.ShamanEvent;
import fr.ph1lou.werewolfapi.events.roles.sister.SisterSeeNameEvent;
import fr.ph1lou.werewolfapi.events.roles.sister.SisterSeeRoleEvent;
import fr.ph1lou.werewolfapi.events.roles.spy.SpyChoseEvent;
import fr.ph1lou.werewolfapi.events.roles.spy.SpyResultEvent;
import fr.ph1lou.werewolfapi.events.roles.storyteller.StoryTellerEvent;
import fr.ph1lou.werewolfapi.events.roles.stud.StudLoverEvent;
import fr.ph1lou.werewolfapi.events.roles.succubus.BeginCharmEvent;
import fr.ph1lou.werewolfapi.events.roles.succubus.CharmEvent;
import fr.ph1lou.werewolfapi.events.roles.succubus.SuccubusResurrectionEvent;
import fr.ph1lou.werewolfapi.events.roles.tenebrous_werewolf.TenebrousEvent;
import fr.ph1lou.werewolfapi.events.roles.thug.ThugEvent;
import fr.ph1lou.werewolfapi.events.roles.thug.ThugRecoverGoldenAppleEvent;
import fr.ph1lou.werewolfapi.events.roles.thug.ThugRevealEvent;
import fr.ph1lou.werewolfapi.events.roles.trapper.TrackEvent;
import fr.ph1lou.werewolfapi.events.roles.trouble_maker.TroubleMakerDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.trouble_maker.TroubleMakerEvent;
import fr.ph1lou.werewolfapi.events.roles.twin.TwinListEvent;
import fr.ph1lou.werewolfapi.events.roles.twin.TwinRevealEvent;
import fr.ph1lou.werewolfapi.events.roles.twin.TwinRoleEvent;
import fr.ph1lou.werewolfapi.events.roles.village_idiot.VillageIdiotEvent;
import fr.ph1lou.werewolfapi.events.roles.villager.VillagerKitEvent;
import fr.ph1lou.werewolfapi.events.roles.wild_child.ModelEvent;
import fr.ph1lou.werewolfapi.events.roles.wild_child.WildChildTransformationEvent;
import fr.ph1lou.werewolfapi.events.roles.will_o_the_wisp.WillOTheWispRecoverRoleEvent;
import fr.ph1lou.werewolfapi.events.roles.will_o_the_wisp.WillOTheWispTeleportEvent;
import fr.ph1lou.werewolfapi.events.roles.wise_elder.WiseElderRevealAuraAmountEvent;
import fr.ph1lou.werewolfapi.events.roles.witch.WitchResurrectionEvent;
import fr.ph1lou.werewolfapi.events.roles.wolf_dog.WolfDogChooseWereWolfForm;
import fr.ph1lou.werewolfapi.events.werewolf.NewWereWolfEvent;
import fr.ph1lou.werewolfapi.events.werewolf.WereWolfChatEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.lovers.ILover;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.statistics.impl.GameReview;
import fr.ph1lou.werewolfapi.statistics.impl.RegisteredAction;
import fr.ph1lou.werewolfplugin.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class Events implements Listener {

    private final Main main;

    public Events(Main main) {
        this.main = main;
    }


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onGameEnd(StopEvent event) {
      StatistiksUtils.postGame(main, main.getCurrentGameReview());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWin(WinEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.win",
                event.getPlayers(), api.getTimer(), event.getRole()));
        main.getCurrentGameReview().end(event.getRole(), event.getPlayers());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onGameStart(StartEvent event) {
        UUID serverUUID;
        try{
            serverUUID = UUID.fromString(Objects.requireNonNull(main.getConfig().getString("server_uuid")));
        }
        catch (Exception e){
            serverUUID = UUID.randomUUID();
            main.getConfig().set("server_uuid", serverUUID);
        }

        main.setCurrentGameReview(new GameReview(main.getWereWolfAPI(), serverUUID));

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(PlayerWWDeathEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.kill",
                event.getPlayerWW(), event.getTargetWW(), api.getTimer())
                .setActionableStory(true));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFinalDeath(FinalDeathEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.final_kill",
                playerWW, event.getPlayerWW().getLastKiller().isPresent() ?
                event.getPlayerWW().getLastKiller().get() : null, api.getTimer()));
    }


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCustom(CustomEvent event) {

                if(event.getEvent().startsWith("werewolf.") || event.getEvent().split("\\.").length < 2){
            return;
        }

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview()
                .addRegisteredAction(new RegisteredAction(event.getEvent(),
                        event.getPlayerWW(),
                        event.getPlayerWWS(),
                        api.getTimer(),
                        event.getExtraInfo(),
                        event.getExtraInt()).setActionableStory(event.isActionableStory()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInfection(InfectionEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.infection",
                event.getPlayerWW(), event.getTargetWW(), api.getTimer())
                .setActionableStory(true));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onGrim(GrimEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.grim",
                event.getPlayerWW(), event.getTargetWW(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onReviveElder(ElderResurrectionEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.elder_revive",
                playerWW, api.getTimer(),
                event.isKillerAVillager() ? "werewolf.elder_kill_by_villager" : "werewolf.elder_not_kill_by_villager"));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRevive(ResurrectionEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview()
                .addRegisteredAction(
                        new RegisteredAction("werewolf.revive",
                                playerWW, api.getTimer()).setActionableStory(true));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWitchRevive(WitchResurrectionEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview()
                .addRegisteredAction(
                        new RegisteredAction("werewolf.witch_revive",
                                event.getPlayerWW(), event.getTargetWW(), api.getTimer())
                                .setActionableStory(true));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLoverDeath(LoverDeathEvent event) {

        List<? extends IPlayerWW> lovers = event.getLover().getLovers();
        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview()
                .addRegisteredAction(
                        new RegisteredAction("werewolf.lover_death", new HashSet<>(lovers), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAmnesiacLoverDeath(AmnesiacLoverDeathEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview()
                .addRegisteredAction(
                        new RegisteredAction("werewolf.amnesiac_lover_death",
                                event.getPlayerWW1(), event.getPlayerWW2(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCursedLoverDeath(CursedLoverDeathEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview()
                .addRegisteredAction(
                        new RegisteredAction("werewolf.cursed_lover_death",
                                event.getPlayerWW1(), event.getPlayerWW2(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onProtection(ProtectionEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW targetWW = event.getTargetWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.protection",
                playerWW, targetWW, api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onModel(ModelEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW targetWW = event.getTargetWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.model",
                playerWW, targetWW, api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCharmed(CharmEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW targetWW = event.getTargetWW();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.charmed",
                playerWW, targetWW, api.getTimer()).setActionableStory(true));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEnchanted(EnchantedEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.enchanted",
                event.getPlayerWW(), event.getTargetWW(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEnchantedAll(AllPlayerEnchantedEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.all_enchanted",
                event.getPlayerWW(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFindFlute(FindFluteEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.find_flute",
                event.getPlayerWW(), event.getTargetWW(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onGiveFlute(GiveFluteEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.give_flute",
                event.getPlayerWW(), event.getTargetWW(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCursed(CurseEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW targetWW = event.getTargetWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.cursed",
                playerWW, targetWW, api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDesignedLover(CupidLoversEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        Set<IPlayerWW> lovers = event.getPlayerWWS();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.designed_lover",
                playerWW, lovers, api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSteal(StealEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW thiefWW = event.getThiefWW();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.steal", thiefWW,
                playerWW, api.getTimer(), event.getRole()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onGrowl(GrowlEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        Set<IPlayerWW> growled = event.getPlayerWWS();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.growl", playerWW,
                growled, api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBeginSniff(BeginSniffEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW targetWW = event.getTargetWW();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.begin_smell",
                playerWW, targetWW, api.getTimer()).setActionableStory(true));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBeginCharm(BeginCharmEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW targetWW = event.getTargetWW();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.begin_charm",
                playerWW, targetWW, api.getTimer()).setActionableStory(true));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSniff(SniffEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW targetWW = event.getTargetWW();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.sniff", playerWW,
                targetWW, api.getTimer(),
                event.isWereWolf() ? "werewolf.roles.fox.werewolf" : "werewolf.roles.fox.not_werewolf"));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSee(SeerEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW targetWW = event.getTargetWW();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.see",
                playerWW, targetWW, api.getTimer(), event.getCamp()).setActionableStory(true));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTrack(TrackEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW targetWW = event.getTargetWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.track",
                playerWW, targetWW, api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTrouble(TroubleMakerEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW targetWW = event.getTargetWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.trouble",
                playerWW, targetWW, api.getTimer()).setActionableStory(true));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onGiveBook(LibrarianRequestEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW targetWW = event.getTargetWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.give_book",
                playerWW, targetWW, api.getTimer()).setActionableStory(true));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLibrarianDeath(LibrarianDeathEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.librarian_death",
                playerWW, api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEnquire(InvestigateEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        Set<IPlayerWW> IPlayerWWS = event.getPlayerWWs();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.enquire",
                playerWW, IPlayerWWS, api.getTimer(),
                event.isSameCamp() ? "werewolf.roles.detective.same_camp" :
                        "werewolf.roles.detective.opposing_camp"));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTroubleDeath(TroubleMakerDeathEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.trouble_maker_death",
                playerWW, api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInvisible(InvisibleEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.invisible",
                playerWW, api.getTimer(), event.isInvisible() ? "invisible" : "visible"));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onVoteSee(CitizenSeeVoteEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.see_vote",
                playerWW, api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onVoteCancel(CitizenCancelVoteEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.cancel_vote",
                playerWW, event.getVoteWW(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onVoteSeeWerewolf(CitizenSeeWerewolfVoteEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.see_werewolf.configurations.vote",
                playerWW, event.getTargetWW(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onVote(VoteEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW targetWW = event.getTargetWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.configurations.vote",
                playerWW, targetWW, api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNewWereWolf(NewWereWolfEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.new_werewolf",
                playerWW, api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onUseMask(UseMaskEvent event) {

        String[] masks = {"werewolf.mask_strength", "werewolf.mask_speed", "werewolf.mask_resistance"};
        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.mask",
                playerWW, api.getTimer(), masks[event.getMask()]).setActionableStory(true));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWildChildTransformation(WildChildTransformationEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW modelWW = event.getModel();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.wild_child_transformation",
                playerWW, modelWW, api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAmnesiacTransformation(AmnesiacTransformationEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW villager = event.getVillager();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.amnesiac_transformation",
                playerWW, villager, api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onVoteResult(VoteResultEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.configurations.vote_result",
                playerWW, api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSuccubusResurrection(SuccubusResurrectionEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW targetWW = event.getTargetWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.succubus_resurrection",
                playerWW, targetWW, api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSisterDeathName(SisterSeeNameEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.sister_see_name",
                event.getPlayerWW(), event.getTargetWW(), api.getTimer())
                .setActionableStory(true));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSisterDeathRole(SisterSeeRoleEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.sister_role_name",
                event.getPlayerWW(), event.getTargetWW(),
                api.getTimer(), event.getTargetWW() == null ? "pve" : event.getTargetWW().getRole().getKey())
                .setActionableStory(true));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDay(DayEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.day",
                api.getTimer(), event.getNumber()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNight(NightEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.night",
                api.getTimer(), event.getNumber()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLoverReveal(RevealLoversEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();
        for (ILover lover : event.getLovers()) {
            main.getCurrentGameReview().addRegisteredAction(new RegisteredAction(
                    lover.isKey(LoverBase.CURSED_LOVER) ? "werewolf.cursed_lover_revelation" :
                            "werewolf.lover_revelation", Sets.newHashSet(lover.getLovers()), api.getTimer()));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onStudLoverReveal(StudLoverEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.stud_lover",
                event.getPlayerWW(), event.getTargetWW(), api.getTimer()));

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAmnesiacLoverReveal(RevealAmnesiacLoversEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.amnesiac_lover_revelation",
                event.getPlayerWWS(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNewDisplayRole(NewDisplayRole event) {

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.new_display_role",
                event.getPlayerWW(), api.getTimer(), event.getNewDisplayRole()));

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTarget(AngelTargetEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW targetWW = event.getTargetWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.angel_target",
                playerWW, targetWW, api.getTimer()));
    }


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTargetDeath(AngelTargetDeathEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW targetWW = event.getTargetWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.angel_target_death",
                playerWW, targetWW, api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSKKill(SerialKillerEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW targetWW = event.getTargetWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.sk_target_death",
                playerWW, targetWW, api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFallenAngelKill(FallenAngelTargetDeathEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW targetWW = event.getTargetWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.fallen_angel_kill_target",
                playerWW, targetWW, api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAngelChoice(AngelChoiceEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.angel_choice",
                playerWW, api.getTimer(), event.getChoice().toString()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWereWolfList(WereWolfListEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.werewolf_list",
                api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRepartition(RepartitionEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.repartition",
                api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTroll(TrollEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.troll",
                api.getTimer(), api.getConfig().getTrollKey()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTrollLover(TrollLoverEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.troll_lover",
                api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPVP(PVPEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.pvp",
                api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInvulnerability(InvulnerabilityEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.invulnerability",
                api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBorderStart(BorderStartEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.border_start",
                api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBorderStop(BorderStopEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.border_stop",
                api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDiggingEnd(DiggingEndEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.digging_end",
                api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDon(DonEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW receiverWW = event.getReceiverWW();

        main.getCurrentGameReview().addRegisteredAction(
                new RegisteredAction("werewolf.don", playerWW,
                        receiverWW, api.getTimer(), event.getDon()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onGiveBack(LibrarianGiveBackEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW targetWW = event.getTargetWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.give_back_book",
                playerWW, targetWW, api.getTimer(), event.getInfo()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAngelRegeneration(RegenerationEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();

        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW targetWW = event.getTargetWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.angel_regeneration",
                playerWW, targetWW, api.getTimer())
                .setActionableStory(true));

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDogTransformation(WolfDogChooseWereWolfForm event) {
        WereWolfAPI api = main.getWereWolfAPI();

        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.wolf_dog_choose_wolf",
                playerWW, api.getTimer()));

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRivalAnnouncement(RivalAnnouncementEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();


        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.rival_announcement",
                event.getPlayerWW(), new HashSet<>(event.getPlayerWWs()), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLoverRivalDeath(RivalLoverDeathEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.rival_lover_death",
                event.getPlayerWW(), new HashSet<>(event.getPlayerWWs()), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRivalLover(RivalLoverEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.rival_lover",
                event.getPlayerWW(), event.getTargetWW(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onVillageIdiotResurrection(VillageIdiotEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.idiot_village",
                event.getPlayerWW(), event.getTargetWW(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMysticalReveal(MysticalWerewolfRevelationEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.mystical_reveal",
                event.getPlayerWW(), event.getTargetWW(), api.getTimer(), event.getTargetWW().getRole().getKey()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWereWolfChat(WereWolfChatEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.werewolf_message",
                event.getPlayerWW(), api.getTimer(), event.getMessage()));

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPriestessSpec(PriestessEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.priestess_spec",
                event.getPlayerWW(), event.getTargetWW(), api.getTimer(), event.getCamp())
                .setActionableStory(true));

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onVillagerKit(VillagerKitEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.villager_kit",
                event.getPlayerWW(), api.getTimer(), event.getKit()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onExposed(ExposedEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.exposed",
                event.getPlayerWW(), new HashSet<>(event.getRoles()), api.getTimer()));

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLootBox_(LootBoxEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.event_loot_box",
                event.getPlayerWW(), api.getTimer(), event.getChestNumbers()));

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFindAllLootBox(FindAllLootBoxEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.find_all_loot_box",
                api.getTimer()));

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRandomInfection(InfectionRandomEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.random_infection",
                event.getPlayerWW(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBearingRitual(BearingRitualEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.bearing_ritual_event",
                api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPutrefaction(PutrefactionEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.putrefaction_event",
                api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onGodMiracle(GodMiracleEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.god_miracle_event",
                event.getPlayerWW(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSwap(SwapEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.swap_event",
                event.getPlayerWW1(), event.getPlayerWW2(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLoneWolf(LoneWolfEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.lone_wolf",
                event.getPlayerWW(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onGuard(GuardEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.guard_event",
                event.getPlayerWW(), event.getTargetWW(), api.getTimer())
                .setActionableStory(true));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onGuardResurrection(GuardResurrectionEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.guard_resurrection",
                event.getPlayerWW(), event.getTargetWW(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTrouple(TroupleEvent event) {
        WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.trouple_event",
                event.getPlayerWW(), event.getPlayerWWs(), api.getTimer()));
    }


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRegisterAvenger(RegisterAvengerListEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.avenger_list_register",
                playerWW, event.getTargetWW(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeathListAvenger(DeathAvengerListEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        IPlayerWW playerWW = event.getPlayerWW();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.avenger_list_death",
                playerWW, event.getTargetWW(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDrunkenWerewolf(DrunkenWereWolfEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.drunken_werewolf",
                event.getPlayerWW(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAmnesic(AmnesicEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.amnesic_design",
                event.getPlayerWW(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAmnesicReveal(AmnesicTransformEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.amnesic_transform",
                event.getPlayerWW(), event.getTargetWW(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onShaman(ShamanEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.shaman",
                event.getPlayerWW(), event.getTargetWW(), api.getTimer())
                .setActionableStory(true));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onOracle(OracleEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.oracle_see",
                event.getPlayerWW(), event.getTargetWW(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCharmedDeath(CharmedDeathEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.charmed_death_event",
                event.getPlayerWW(), event.getTargetWW(),api.getTimer(),
                event.isBeforeCountDown()?"werewolf.before_count_down":"werewolf.after_count_down"));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCharmerGetEffect(CharmerGetEffectDeathEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.charmer_get_effect",
                event.getPlayerWW(),new HashSet<>(event.getLover().getLovers()), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCharmerCharmed(CharmerEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.charmer_charmed",
                event.getPlayerWW(),event.getTargetWW(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWispTeleport(WillOTheWispTeleportEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.will_o_the_wisp_teleport",
                event.getPlayerWW(),api.getTimer(), event.getNumberUse())
                .setActionableStory(true));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWispRecoverRole(WillOTheWispRecoverRoleEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.will_o_the_wisp_recover_role",
                event.getPlayerWW(),event.getTargetWW(),api.getTimer(),event.getRoleKey()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTwin(TwinRevealEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.twin_reveal",
                event.getPlayerWW(),event.getPlayerWWS(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTwinList(TwinListEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.twin_list",
                event.getPlayerWW(),event.getPlayerWWS(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTwinRole(TwinRoleEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.twin_role",
                event.getPlayerWW(),event.getTargetWW(),api.getTimer(),event.getTargetWW().getRole().getKey()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWerewolfHowler(HowlEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.werewolf_howler",
                event.getPlayerWW(),event.getPlayerWWS(),api.getTimer(), event.getNotWerewolfSize()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAnalystAnalyst(AnalystExtraDetailsEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.analyst_analyst",
                event.getPlayerWW(),event.getTargetWW(),api.getTimer(),
                event.getPotions().stream().map(PotionEffectType::getName).collect(Collectors.joining(", ")))
                .setActionableStory(true));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAnalystSee(AnalystEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.analyst_see",
                event.getPlayerWW(),event.getTargetWW(),api.getTimer(),
                event.hasEffect()?"werewolf.roles.analyst.has_effects":"werewolf.roles.analyst.no_effects")
                .setActionableStory(true));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFruitMerchant(FruitMerchantDeathEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.fruit_merchant_death",
                event.getPlayerWW(),event.getTargetWW(),api.getTimer(),
                String.valueOf(event.getGoldenAppleCount().getOldCount()),event.getGoldenAppleCount().getNewCount()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFruitMerchantCommand(FruitMerchantCommandEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.fruit_merchant_command",
                event.getPlayerWW(),event.getPlayerWWS(), api.getTimer())
                .setActionableStory(true));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFruitMerchantRecoverInformation(FruitMerchantRecoverInformationEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.fruit_merchant_recover",
                event.getPlayerWW(), event.getPlayerWWS(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDruidInfo(DruidUsePowerEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.druid_power",
                event.getPlayerWW(),api.getTimer(),event.getDarkAura()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRevealAuraAmount(WiseElderRevealAuraAmountEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();

        String string = String.format("Neutral: %s; Dark: %s; Light: %s",
                event.getNeutral(), event.getDark(), event.getLight());

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.reveal_aura_amount",
                event.getPlayerWW(), api.getTimer(), string));
    }


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRevealAuraAmount(ServitorMasterChosenEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.servitor_master_chosen",
                event.getPlayerWW(), event.getTargetWW(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDefinitiveMaster(ServitorDefinitiveMasterEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.servitor_definitive_master",
                event.getPlayerWW(), event.getTargetWW(), api.getTimer()));

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onScammer(ScamEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.scam_event",
                event.getPlayerWW(), event.getTargetWW(), api.getTimer())
                .setActionableStory(true));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNewVote(NewVoteResultEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.new_vote",
                event.getPlayerVotedByVillagerWW(), event.getPlayerVotedByWerewolfWW(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMysanthropeSister(MysanthropeSisterEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.mysanthrope_sister",
                event.getPlayerWW(), api.getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTenebrous(TenebrousEvent event) {

        WereWolfAPI api = main.getWereWolfAPI();
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.tenebrous",
                event.getPlayerWW(),
                new HashSet<>(event.getAffectedPlayers()), api.getTimer())
                .setActionableStory(true));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWishEvent(WishChangeEvent event) {

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.commands.player.wish_change",
                event.getPlayerWW(), main.getWereWolfAPI().getTimer(), event.getWish()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRevealWishes(OccultistRevealWishes event) {

        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.reveal_wishes_occultist",
                event.getPlayerWW(), main.getWereWolfAPI().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onThugCommand(ThugEvent event) {
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.thug_command",
                event.getPlayerWW(), event.getTargetWW(), main.getWereWolfAPI().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onThugGoldenApple(ThugRecoverGoldenAppleEvent event) {
        main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.thug_recover_golden_apple",
                event.getPlayerWW(), event.getTargetWW(),  main.getWereWolfAPI().getTimer(), event.getGoldenApple()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onThugRevealEvent(ThugRevealEvent event) {
                main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.thug_reveal_event",
                event.getPlayerWW(), main.getWereWolfAPI().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBarbarianEvent(BarbarianEvent event) {
                main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.barbarian_event",
                event.getPlayerWW(), event.getTargetWW(),  main.getWereWolfAPI().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNecromancerEvent(NecromancerResurrectionEvent event) {
                main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.necromancer_event",
                event.getPlayerWW(), event.getTargetWW(),  main.getWereWolfAPI().getTimer())
                .setActionableStory(true));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onGraveDiggerEvent(TriggerGravediggerClueEvent event) {
                main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.gravedigger_event",
                event.getGravedigger(), event.getPlayerWW(),  main.getWereWolfAPI().getTimer(), event.getClueCount()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHunterShoot(HunterShotEvent event) {
                main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.hunter_shoot_event",
                event.getSource(), event.getTarget(),  main.getWereWolfAPI().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDevotedServant(DevotedServantEvent event) {
                main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.devoted_servant_event",
                event.getPlayerWW(), event.getTargetWW(),  main.getWereWolfAPI().getTimer())
                .setActionableStory(true));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onStoryTeller(StoryTellerEvent event) {
                main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.story_teller_event",
                event.getPlayerWW(), event.getPlayerWWS(),  main.getWereWolfAPI().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSpy(SpyChoseEvent event) {
                main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.spy_choose_event",
                event.getPlayerWW(), event.getTargetWW(),  main.getWereWolfAPI().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSpyResult(SpyResultEvent event) {
                main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.spy_result_event",
                event.getPlayerWW(), event.getTargetWW(),  main.getWereWolfAPI().getTimer(), event.getActionsNumber()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBenefactorGiveHeart(BenefactorGiveHeartEvent event) {
                main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.benefactor_give_heart_event",
                event.getPlayerWW(), event.getTargetWW(),  main.getWereWolfAPI().getTimer())
                .setActionableStory(true));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInterpretGetRole(InterpreterEvent event) {
                main.getCurrentGameReview().addRegisteredAction(new RegisteredAction("werewolf.interpret_event",
                event.getPlayerWW(),  main.getWereWolfAPI().getTimer(), event.getRoleKey()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onIllusionistActivatePower(IllusionistActivatePowerEvent event) {
                main.getCurrentGameReview()
                .addRegisteredAction(new RegisteredAction("werewolf.illusionist_activate_event",
                event.getPlayerWW(),  main.getWereWolfAPI().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onIllusionistGetNames(IllusionistGetNamesEvent event) {
                main.getCurrentGameReview()
                .addRegisteredAction(new RegisteredAction("werewolf.illusionist_get_names_event",
                        event.getPlayerWW(),event.getPlayerWWS(), main.getWereWolfAPI().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onIllusionistAddPlayer(IllusionistAddPlayerOnWerewolfListEvent event) {
                main.getCurrentGameReview()
                .addRegisteredAction(new RegisteredAction("werewolf.illusionist_add_player_event",
                        event.getPlayerWW(),event.getTargetWW(), main.getWereWolfAPI().getTimer())
                        .setActionableStory(true));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRumorsEnable(RumorsEvent event) {
                main.getCurrentGameReview()
                .addRegisteredAction(new RegisteredAction("werewolf.rumors_event",
                         main.getWereWolfAPI().getTimer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRumorsWrite(RumorsWriteEvent event) {

                main.getCurrentGameReview()
                .addRegisteredAction(new RegisteredAction("werewolf.rumors_write_event",
                        event.getPlayerWW(),
                        main.getWereWolfAPI().getTimer(),
                        event.getMessage()));
    }
}
