package fr.ph1lou.werewolfplugin.buildings;

import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.buildings.BuildingDefinition;
import fr.ph1lou.werewolfapi.buildings.BuildingType;
import fr.ph1lou.werewolfapi.buildings.IBuildingsManager;
import fr.ph1lou.werewolfapi.buildings.StructurePaster;
import fr.ph1lou.werewolfapi.enums.Camp;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalPotionEffectType;
import fr.ph1lou.werewolfapi.events.buildings.SanctuaryVisitEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfapi.utils.Utils;
import fr.ph1lou.werewolfapi.versions.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class BuildingsManager implements IBuildingsManager {

    public static final int COUNCIL_MAX_USES = 3;
    public static final int COUNCIL_COOLDOWN = 10 * 60;
    public static final int SANCTUARY_VISIT_SECONDS = 10;
    public static final String SILENCE_KEY = "werewolf.urn_vote_silence";

    private final WereWolfAPI game;
    private final BuildingLoader loader = new BuildingLoader();
    private final StructurePaster paster = new StructurePaster();
    private final List<VoteOffice> voteOffices = new ArrayList<>();
    private final List<CouncilChamber> councilChambers = new ArrayList<>();
    private final List<Sanctuary> sanctuaries = new ArrayList<>();
    private final List<Location> placedCenters = new ArrayList<>();
    private final UrnVoteManager urnVoteManager;
    private boolean loaded = false;

    private final Map<UUID, Integer> councilUses = new HashMap<>();
    private final Map<UUID, Integer> councilLastUse = new HashMap<>();
    private final Set<String> activeCouncilPairs = new HashSet<>();
    private final Map<UUID, Map<Integer, Integer>> sanctuaryProgress = new HashMap<>();
    private int sanctuaryTaskId = -1;
    private int councilTaskId = -1;

    public BuildingsManager(WereWolfAPI game) {
        this.game = game;
        this.urnVoteManager = new UrnVoteManager(this, game);
    }

    public void init() {
        if (!loaded) {
            loader.load();
            loaded = true;
        }
    }

    public UrnVoteManager getUrnVoteManager() {
        return urnVoteManager;
    }

    public List<VoteOffice> getVoteOffices() {
        return voteOffices;
    }

    public List<CouncilChamber> getCouncilChambers() {
        return councilChambers;
    }

    public List<Sanctuary> getSanctuaries() {
        return sanctuaries;
    }

    public void generate() {
        init();
        clear();

        if (!game.getConfig().isConfigActive(ConfigBase.BUILDINGS)) {
            return;
        }

        World world = game.getMapManager().getWorld();
        if (world == null) {
            return;
        }

        Location spawn = world.getSpawnLocation();
        Random random = game.getRandom();
        int radius = game.getConfig().getValue(IntValueBase.BUILDINGS_RADIUS);

        if (game.getConfig().isConfigActive(ConfigBase.URN_VOTE)) {
            int count = game.getConfig().getValue(IntValueBase.BUILDINGS_VOTE_OFFICE_COUNT);
            int urnCapacity = game.getConfig().getValue(IntValueBase.URN_CAPACITY);
            generateType(world, spawn, random, radius, BuildingType.VOTE_OFFICE, count, DyeColor.RED, urnCapacity);
        }

        if (game.getConfig().isConfigActive(ConfigBase.BUILDING_COUNCIL_CHAMBER)) {
            int count = game.getConfig().getValue(IntValueBase.BUILDINGS_COUNCIL_CHAMBER_COUNT);
            generateType(world, spawn, random, radius, BuildingType.COUNCIL_CHAMBER, count, DyeColor.BLUE, 0);
        }

        int decorativeCount = game.getConfig().getValue(IntValueBase.BUILDINGS_DECORATIVE_COUNT);
        if (decorativeCount > 0) {
            generateType(world, spawn, random, radius, BuildingType.DECORATIVE, decorativeCount, null, 0);
        }

        startCouncilTask();
    }

    public void generateSanctuaries() {
        init();

        if (!game.getConfig().isConfigActive(ConfigBase.BUILDINGS) || !game.getConfig().isConfigActive(ConfigBase.BUILDING_SANCTUARY)) {
            return;
        }

        World world = game.getMapManager().getWorld();
        if (world == null || !sanctuaries.isEmpty()) {
            return;
        }

        Location spawn = world.getSpawnLocation();
        Random random = game.getRandom();
        int radius = game.getConfig().getValue(IntValueBase.BUILDINGS_RADIUS);

        DyeColor[] colors = {DyeColor.RED, DyeColor.BLUE, DyeColor.GREEN, DyeColor.YELLOW};
        Color[] particleColors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW};

        int count = Math.max(1, game.getConfig().getValue(IntValueBase.BUILDINGS_SANCTUARY_COUNT));

        for (int i = 0; i < count; i++) {
            DyeColor dye = colors[i % colors.length];
            Color particle = particleColors[i % colors.length];
            double angle = (Math.PI * 2 * i) / count + random.nextDouble() * 0.4;
            int distance = (int) (radius * 0.8 + random.nextInt(Math.max(1, radius / 5)));
            int x = spawn.getBlockX() + (int) Math.round(distance * Math.cos(angle));
            int z = spawn.getBlockZ() + (int) Math.round(distance * Math.sin(angle));

            List<BuildingDefinition> definitions = loader.getDefinitions(BuildingType.SANCTUARY);
            if (definitions.isEmpty()) {
                Bukkit.getLogger().warning("[WereWolfPlugin] No structure definition found for sanctuary");
                continue;
            }
            BuildingDefinition definition = definitions.get(random.nextInt(definitions.size()));
            int y = world.getHighestBlockYAt(x, z);

            try {
                paster.paste(definition, world, x, y, z, dye);
                Location banner = paster.functionalLocation(definition, "banner", world, x, y, z);
                sanctuaries.add(new Sanctuary(world, x, y, z, banner, dye, particle));
                placedCenters.add(new Location(world, x, 0, z));
            } catch (Throwable t) {
                Bukkit.getLogger().warning("[WereWolfPlugin] Failed to paste sanctuary: " + t.getMessage());
            }
        }

        startSanctuaryTask();
        Bukkit.broadcastMessage(game.translate(fr.ph1lou.werewolfapi.basekeys.Prefix.ORANGE,
                "werewolf.configurations.buildings.sanctuary_spawn",
                Formatter.number(sanctuaries.size())));
    }

    private void startSanctuaryTask() {
        if (sanctuaryTaskId != -1) {
            return;
        }
        sanctuaryTaskId = BukkitUtils.scheduleSyncRepeatingTask(game, this::tickSanctuaries, 20L, 20L);
    }

    private void tickSanctuaries() {
        if (sanctuaries.isEmpty()) {
            return;
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            IPlayerWW playerWW = game.getPlayerWW(player.getUniqueId()).orElse(null);
            if (playerWW == null || !playerWW.isState(StatePlayer.ALIVE)) {
                continue;
            }
            UUID uuid = player.getUniqueId();
            Map<Integer, Integer> progress = sanctuaryProgress.computeIfAbsent(uuid, k -> new HashMap<>());

            for (int i = 0; i < sanctuaries.size(); i++) {
                Sanctuary sanctuary = sanctuaries.get(i);

                if (sanctuary.hasVisited(uuid)) {
                    continue;
                }

                Location center = sanctuary.getCenter();
                if (center == null || center.getWorld() != player.getLocation().getWorld()) {
                    continue;
                }

                VersionUtils.getVersionUtils().spawnColoredParticle(player, center.clone().add(0, 1, 0), sanctuary.getParticleColor());

                double dist;
                try {
                    dist = player.getLocation().distance(center);
                } catch (Throwable e) {
                    continue;
                }

                if (dist <= 5) {
                    int seconds = progress.getOrDefault(i, 0) + 1;
                    progress.put(i, seconds);

                    if (seconds >= SANCTUARY_VISIT_SECONDS) {
                        completeVisit(playerWW, sanctuary, i, progress);
                    } else if (seconds == 1) {
                        playerWW.sendMessageWithKey(fr.ph1lou.werewolfapi.basekeys.Prefix.YELLOW,
                                "werewolf.configurations.buildings.sanctuary_progress",
                                Formatter.number(SANCTUARY_VISIT_SECONDS));
                    }
                } else {
                    progress.put(i, 0);
                }
            }
        }
    }

    private void completeVisit(IPlayerWW playerWW, Sanctuary sanctuary, int index, Map<Integer, Integer> progress) {
        sanctuary.markVisited(playerWW.getUUID());
        progress.put(index, 0);

        SanctuaryVisitEvent event = new SanctuaryVisitEvent(playerWW, sanctuary.getDyeColor());
        Bukkit.getPluginManager().callEvent(event);

        playerWW.addItem(fr.ph1lou.werewolfapi.enums.UniversalMaterial.GOLDEN_APPLE.getStack());
        playerWW.sendMessageWithKey(fr.ph1lou.werewolfapi.basekeys.Prefix.GREEN,
                "werewolf.configurations.buildings.sanctuary_visited");

        long visited = sanctuaries.stream().filter(s -> s.hasVisited(playerWW.getUUID())).count();
        if (visited >= sanctuaries.size()) {
            applySanctuaryReward(playerWW);
        }
    }

    private void applySanctuaryReward(IPlayerWW playerWW) {
        playerWW.addPotionModifier(PotionModifier.remove(UniversalPotionEffectType.WEAKNESS, SILENCE_KEY, 0));
        playerWW.addPotionModifier(PotionModifier.remove(UniversalPotionEffectType.SLOWNESS, SILENCE_KEY, 0));

        Camp camp = playerWW.getRole().getCamp();
        if (camp == Camp.VILLAGER) {
            playerWW.modifyHonor(1);
        }

        playerWW.sendMessageWithKey(fr.ph1lou.werewolfapi.basekeys.Prefix.GREEN,
                "werewolf.configurations.buildings.sanctuary_reward",
                Formatter.number(camp == Camp.VILLAGER ? 1 : 0));
    }

    public boolean canConsult(UUID player) {
        int uses = councilUses.getOrDefault(player, 0);
        if (uses >= COUNCIL_MAX_USES) {
            return false;
        }
        int last = councilLastUse.getOrDefault(player, -COUNCIL_COOLDOWN);
        return game.getTimer() - last >= COUNCIL_COOLDOWN;
    }

    public int getCouncilUses(UUID player) {
        return councilUses.getOrDefault(player, 0);
    }

    public int getCouncilRemaining(UUID player) {
        return Math.max(0, COUNCIL_MAX_USES - councilUses.getOrDefault(player, 0));
    }

    public int getCouncilCooldownRemaining(UUID player) {
        int last = councilLastUse.getOrDefault(player, -COUNCIL_COOLDOWN);
        int elapsed = game.getTimer() - last;
        return Math.max(0, COUNCIL_COOLDOWN - elapsed);
    }

    public void recordConsult(UUID player) {
        councilUses.merge(player, 1, Integer::sum);
        councilLastUse.put(player, game.getTimer());
    }

    public void startCouncilTask() {
        if (councilTaskId != -1) {
            return;
        }
        councilTaskId = BukkitUtils.scheduleSyncRepeatingTask(game, this::tickCouncil, 20L, 20L);
    }

    private void tickCouncil() {
        if (councilChambers.isEmpty() || !game.isState(StateGame.GAME)) {
            return;
        }
        Set<String> currentPairs = new HashSet<>();
        for (CouncilChamber chamber : councilChambers) {
            Player p1 = findPlayerOn(chamber.getPlate1());
            Player p2 = findPlayerOn(chamber.getPlate2());
            if (p1 == null || p2 == null || p1.getUniqueId().equals(p2.getUniqueId())) {
                continue;
            }
            IPlayerWW w1 = game.getPlayerWW(p1.getUniqueId()).orElse(null);
            IPlayerWW w2 = game.getPlayerWW(p2.getUniqueId()).orElse(null);
            if (w1 == null || w2 == null
                    || !w1.isState(StatePlayer.ALIVE) || !w2.isState(StatePlayer.ALIVE)) {
                continue;
            }
            String key = pairKey(p1.getUniqueId(), p2.getUniqueId());
            currentPairs.add(key);
            // Ne traiter qu'une fois par « présence simultanée » (évite le spam).
            if (activeCouncilPairs.add(key)) {
                processCouncilReveal(w1, w2);
            }
        }
        // Oublie les paires qui ne sont plus présentes simultanément (permet de reconsulter plus tard).
        activeCouncilPairs.retainAll(currentPairs);
    }

    private void processCouncilReveal(IPlayerWW w1, IPlayerWW w2) {
        if (!game.getConfig().isConfigActive(ConfigBase.BUILDING_COUNCIL_CHAMBER)) {
            return;
        }
        if (!game.getConfig().isConfigActive(ConfigBase.HONOR)) {
            w1.sendMessageWithKey(Prefix.RED, "werewolf.configurations.buildings.council_no_honor");
            return;
        }
        if (!canConsult(w1.getUUID())) {
            if (getCouncilRemaining(w1.getUUID()) <= 0) {
                w1.sendMessageWithKey(Prefix.RED, "werewolf.configurations.buildings.council_no_use_left");
            } else {
                w1.sendMessageWithKey(Prefix.RED, "werewolf.configurations.buildings.council_cooldown",
                        Formatter.timer(Utils.conversion(getCouncilCooldownRemaining(w1.getUUID()))));
            }
            return;
        }
        if (!canConsult(w2.getUUID())) {
            w1.sendMessageWithKey(Prefix.RED, "werewolf.configurations.buildings.council_partner_unavailable",
                    Formatter.player(w2.getName()));
            return;
        }
        recordConsult(w1.getUUID());
        recordConsult(w2.getUUID());

        w1.sendMessageWithKey(Prefix.YELLOW, "werewolf.configurations.buildings.council_reveal",
                Formatter.player(w2.getName()), Formatter.number(w2.getHonor()));
        w2.sendMessageWithKey(Prefix.YELLOW, "werewolf.configurations.buildings.council_reveal",
                Formatter.player(w1.getName()), Formatter.number(w1.getHonor()));

        w1.sendMessageWithKey(Prefix.ORANGE, "werewolf.configurations.buildings.council_remaining",
                Formatter.number(getCouncilRemaining(w1.getUUID())));
    }

    private Player findPlayerOn(Location plate) {
        if (plate == null) {
            return null;
        }
        for (org.bukkit.entity.Entity entity : plate.getWorld().getNearbyEntities(plate, 0.6, 2.0, 0.6)) {
            if (entity instanceof Player) {
                return (Player) entity;
            }
        }
        return null;
    }

    private String pairKey(UUID a, UUID b) {
        return a.compareTo(b) < 0 ? a + ":" + b : b + ":" + a;
    }

    private void generateType(World world, Location spawn, Random random, int radius,
                              BuildingType type, int count, DyeColor bannerColor, int urnCapacity) {
        List<BuildingDefinition> definitions = loader.getDefinitions(type);
        if (definitions.isEmpty()) {
            Bukkit.getLogger().warning("[WereWolfPlugin] No structure definition found for building type " + type);
            return;
        }

        int attempts = 0;
        int placed = 0;
        while (placed < count && attempts < count * 20) {
            attempts++;
            double angle = random.nextDouble() * Math.PI * 2;
            int distance = 50 + random.nextInt(Math.max(1, radius - 50));
            int x = spawn.getBlockX() + (int) Math.round(distance * Math.cos(angle));
            int z = spawn.getBlockZ() + (int) Math.round(distance * Math.sin(angle));

            if (!isFarEnough(x, z)) {
                continue;
            }

            BuildingDefinition definition = definitions.get(random.nextInt(definitions.size()));
            int y = world.getHighestBlockYAt(x, z);

            if (tryPlace(definition, world, x, y, z, bannerColor, urnCapacity)) {
                placedCenters.add(new Location(world, x, 0, z));
                placed++;
            }
        }
    }

    private boolean tryPlace(BuildingDefinition definition, World world, int x, int y, int z,
                             DyeColor bannerColor, int urnCapacity) {
        try {
            paster.paste(definition, world, x, y, z, bannerColor);

            Location banner = paster.functionalLocation(definition, "banner", world, x, y, z);

            switch (definition.getType()) {
                case VOTE_OFFICE: {
                    Location urn = paster.functionalLocation(definition, "urn", world, x, y, z);
                    if (urn == null) {
                        return false;
                    }
                    voteOffices.add(new VoteOffice(world, x, y, z, banner, urn, urnCapacity));
                    break;
                }
                case COUNCIL_CHAMBER: {
                    Location plate1 = paster.functionalLocation(definition, "plate1", world, x, y, z);
                    Location plate2 = paster.functionalLocation(definition, "plate2", world, x, y, z);
                    Location glass = paster.functionalLocation(definition, "glass", world, x, y, z);
                    if (plate1 == null || plate2 == null) {
                        return false;
                    }
                    councilChambers.add(new CouncilChamber(world, x, y, z, banner, plate1, plate2, glass));
                    break;
                }
                case DECORATIVE:
                default:
                    break;
            }
            return true;
        } catch (Throwable t) {
            Bukkit.getLogger().warning("[WereWolfPlugin] Failed to paste building: " + t.getMessage());
            return false;
        }
    }

    private boolean isFarEnough(int x, int z) {
        for (Location center : placedCenters) {
            if (center.getBlockX() == x && center.getBlockZ() == z) {
                return false;
            }
            double dx = center.getX() - x;
            double dz = center.getZ() - z;
            if (dx * dx + dz * dz < 40 * 40) {
                return false;
            }
        }
        return true;
    }

    public VoteOffice getVoteOffice(Location urnLocation) {
        if (urnLocation == null) {
            return null;
        }
        for (VoteOffice office : voteOffices) {
            Location urn = office.getUrnLocation();
            if (urn != null && urn.getWorld() == urnLocation.getWorld()
                    && urn.getBlockX() == urnLocation.getBlockX()
                    && urn.getBlockY() == urnLocation.getBlockY()
                    && urn.getBlockZ() == urnLocation.getBlockZ()) {
                return office;
            }
        }
        return null;
    }

    public CouncilChamber getCouncilChamberByPlate(Location plateLocation) {
        if (plateLocation == null) {
            return null;
        }
        for (CouncilChamber chamber : councilChambers) {
            if (matches(chamber.getPlate1(), plateLocation) || matches(chamber.getPlate2(), plateLocation)) {
                return chamber;
            }
        }
        return null;
    }

    private boolean matches(Location a, Location b) {
        return a != null && a.getWorld() == b.getWorld()
                && a.getBlockX() == b.getBlockX()
                && a.getBlockY() == b.getBlockY()
                && a.getBlockZ() == b.getBlockZ();
    }

    public void clear() {
        voteOffices.clear();
        councilChambers.clear();
        sanctuaries.clear();
        placedCenters.clear();
        councilUses.clear();
        councilLastUse.clear();
        activeCouncilPairs.clear();
        sanctuaryProgress.clear();
    }
}
