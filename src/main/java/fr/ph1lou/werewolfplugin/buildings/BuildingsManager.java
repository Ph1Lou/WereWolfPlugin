package fr.ph1lou.werewolfplugin.buildings;

import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.buildings.*;
import fr.ph1lou.werewolfapi.enums.Camp;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.buildings.SanctuaryVisitEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfapi.utils.Utils;
import fr.ph1lou.werewolfapi.versions.VersionUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.*;

public class BuildingsManager implements IBuildingsManager {

    public static final int COUNCIL_MAX_USES = 3;
    public static final int COUNCIL_COOLDOWN = 10 * 60;
    public static final int SANCTUARY_VISIT_SECONDS = 10;
    private BuildingDefinition placeholderDefinition;

    private final WereWolfAPI game;
    private final BuildingLoader loader = new BuildingLoader();
    private final StructurePaster paster = new StructurePaster();
    private final List<IVoteOffice> voteOffices = new ArrayList<>();
    private final List<CouncilChamber> councilChambers = new ArrayList<>();
    private final List<Sanctuary> sanctuaries = new ArrayList<>();
    private final List<Location> placedCenters = new ArrayList<>();
    private final UrnVoteManager urnVoteManager;
    private boolean loaded = false;

    private final Map<UUID, Integer> councilUses = new HashMap<>();
    private final Map<UUID, Integer> councilLastUse = new HashMap<>();
    private final Set<String> activeCouncilPairs = new HashSet<>();
    private final Map<UUID, Map<Integer, Integer>> sanctuaryProgress = new HashMap<>();
    private final List<int[]> protectedBoxes = new ArrayList<>();
    private final List<PendingBuilding> pendingRealBuildings = new ArrayList<>();
    private final Set<IBuilding> disabledBuildings = new HashSet<>();
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

    public List<IVoteOffice> getVoteOffices() {
        return voteOffices;
    }

    public List<CouncilChamber> getCouncilChambers() {
        return councilChambers;
    }

    public List<Sanctuary> getSanctuaries() {
        return sanctuaries;
    }

    public boolean isDisabled(IBuilding building) {
        return building != null && this.disabledBuildings.contains(building);
    }

    public void setDisabled(IBuilding building, boolean disabled) {
        if (building == null) return;
        if (disabled) {
            this.disabledBuildings.add(building);
        } else {
            this.disabledBuildings.remove(building);
        }
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
                protectedBoxes.add(new int[]{x, y, z,
                        x + definition.getWidth() - 1,
                        y + definition.getHeight() - 1,
                        z + definition.getLength() - 1});
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

                if (isDisabled(sanctuary)) {
                    continue;
                }

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
        playerWW.getRole().enableAbilities();

        int oldHonor = playerWW.getHonor();

        if (playerWW.getRole().isCamp(Camp.VILLAGER)) {
            playerWW.modifyHonor(1);
        } else {
            playerWW.modifyHonorAwayFromZero();
        }

        playerWW.sendMessageWithKey(fr.ph1lou.werewolfapi.basekeys.Prefix.GREEN,
                "werewolf.configurations.buildings.sanctuary_reward",
                Formatter.number(playerWW.getHonor() - oldHonor));
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
            if (isDisabled(chamber)) {
                continue;
            }
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

        boolean w1Can = canConsult(w1.getUUID());
        boolean w2Can = canConsult(w2.getUUID());

        if (!w1Can && !w2Can) {
            sendCouncilBlockedMessage(w1);
            sendCouncilBlockedMessage(w2);
            return;
        }
        if (!w1Can) {
            sendCouncilBlockedMessage(w1);
            w2.sendMessageWithKey(Prefix.RED, "werewolf.configurations.buildings.council_partner_unavailable",
                    Formatter.player(w1.getName()));
            return;
        }
        if (!w2Can) {
            sendCouncilBlockedMessage(w2);
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
        w2.sendMessageWithKey(Prefix.ORANGE, "werewolf.configurations.buildings.council_remaining",
                Formatter.number(getCouncilRemaining(w2.getUUID())));
    }

    private void sendCouncilBlockedMessage(IPlayerWW player) {
        if (getCouncilRemaining(player.getUUID()) <= 0) {
            player.sendMessageWithKey(Prefix.RED, "werewolf.configurations.buildings.council_no_use_left");
        } else {
            player.sendMessageWithKey(Prefix.RED, "werewolf.configurations.buildings.council_cooldown",
                    Formatter.timer(Utils.conversion(getCouncilCooldownRemaining(player.getUUID()))));
        }
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
            if (usePlaceholder()) {
                BuildingDefinition placeholder = getPlaceholderDefinition();
                if (placeholder != null) {
                    paster.paste(placeholder, world, x, y, z, null);
                    protectedBoxes.add(new int[]{x, y, z,
                            x + placeholder.getWidth() - 1,
                            y + placeholder.getHeight() - 1,
                            z + placeholder.getLength() - 1});
                }
                pendingRealBuildings.add(new PendingBuilding(definition, world, x, y, z, bannerColor, urnCapacity));
                return true;
            }
            paster.paste(definition, world, x, y, z, bannerColor);
            protectedBoxes.add(new int[]{x, y, z,
                    x + definition.getWidth() - 1,
                    y + definition.getHeight() - 1,
                    z + definition.getLength() - 1});
            registerBuilding(definition, world, x, y, z, bannerColor, urnCapacity);
            return true;
        } catch (Throwable t) {
            Bukkit.getLogger().warning("[WereWolfPlugin] Failed to paste building: " + t.getMessage());
            return false;
        }
    }

    private boolean usePlaceholder() {
        return game.getConfig().isConfigActive(ConfigBase.URN_VOTE);
    }

    private BuildingDefinition getPlaceholderDefinition() {
        if (placeholderDefinition == null) {
            List<BuildingDefinition> defs = loader.getDefinitions(BuildingType.DECORATIVE);
            if (!defs.isEmpty()) {
                placeholderDefinition = defs.get(0);
            }
        }
        return placeholderDefinition;
    }

    private void clearBox(World world, int x, int y, int z, int width, int height, int length) {
        for (int bx = 0; bx < width; bx++) {
            for (int bz = 0; bz < length; bz++) {
                for (int by = 0; by < height; by++) {
                    world.getBlockAt(x + bx, y + by, z + bz).setType(Material.AIR, false);
                }
            }
        }
    }

    private void registerBuilding(BuildingDefinition definition, World world, int x, int y, int z,
                                  DyeColor bannerColor, int urnCapacity) {
        Location banner = paster.functionalLocation(definition, "banner", world, x, y, z);
        switch (definition.getType()) {
            case VOTE_OFFICE: {
                Location urn = paster.functionalLocation(definition, "urn", world, x, y, z);
                Location chest = paster.functionalLocation(definition, "chest", world, x, y, z);
                if (urn == null) {
                    return;
                }
                voteOffices.add(new VoteOffice(world, x, y, z, banner, urn, chest, urnCapacity));
                break;
            }
            case COUNCIL_CHAMBER: {
                Location plate1 = paster.functionalLocation(definition, "plate1", world, x, y, z);
                Location plate2 = paster.functionalLocation(definition, "plate2", world, x, y, z);
                Location glass = paster.functionalLocation(definition, "glass", world, x, y, z);
                if (plate1 == null || plate2 == null) {
                    return;
                }
                councilChambers.add(new CouncilChamber(world, x, y, z, banner, plate1, plate2, glass));
                break;
            }
            case DECORATIVE:
            default:
                break;
        }
    }

    public void revealBuildings() {
        if (pendingRealBuildings.isEmpty()) {
            return;
        }
        List<PendingBuilding> pending = new ArrayList<>(pendingRealBuildings);
        pendingRealBuildings.clear();
        for (PendingBuilding pb : pending) {
            try {
                clearBox(pb.world, pb.x, pb.y, pb.z,
                        pb.definition.getWidth(), pb.definition.getHeight(), pb.definition.getLength());
                paster.paste(pb.definition, pb.world, pb.x, pb.y, pb.z, pb.bannerColor);
                protectedBoxes.add(new int[]{pb.x, pb.y, pb.z,
                        pb.x + pb.definition.getWidth() - 1,
                        pb.y + pb.definition.getHeight() - 1,
                        pb.z + pb.definition.getLength() - 1});
                registerBuilding(pb.definition, pb.world, pb.x, pb.y, pb.z, pb.bannerColor, pb.urnCapacity);
            } catch (Throwable t) {
                Bukkit.getLogger().warning("[WereWolfPlugin] Failed to reveal building: " + t.getMessage());
            }
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

    public IVoteOffice getVoteOffice(Location urnLocation) {
        if (urnLocation == null) {
            return null;
        }
        for (IVoteOffice office : voteOffices) {
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
        protectedBoxes.clear();
        pendingRealBuildings.clear();
    }

    public boolean isProtected(Location loc) {
        if (loc == null || loc.getWorld() != game.getMapManager().getWorld()) {
            return false;
        }
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        for (int[] box : protectedBoxes) {
            if (x >= box[0] && x <= box[3]
                    && y >= box[1] && y <= box[4]
                    && z >= box[2] && z <= box[5]) {
                return true;
            }
        }
        return false;
    }

    private static final class PendingBuilding {
        private final BuildingDefinition definition;
        private final World world;
        private final int x, y, z;
        private final DyeColor bannerColor;
        private final int urnCapacity;

        PendingBuilding(BuildingDefinition definition, World world, int x, int y, int z,
                        DyeColor bannerColor, int urnCapacity) {
            this.definition = definition;
            this.world = world;
            this.x = x;
            this.y = y;
            this.z = z;
            this.bannerColor = bannerColor;
            this.urnCapacity = urnCapacity;
        }
    }
}
