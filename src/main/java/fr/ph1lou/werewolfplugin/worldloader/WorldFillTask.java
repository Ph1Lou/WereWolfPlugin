package fr.ph1lou.werewolfplugin.worldloader;

import fr.ph1lou.werewolfapi.versions.VersionUtils;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Server;
import org.bukkit.World;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;


public class WorldFillTask implements Runnable
{
	// general task-related reference data
	private transient Server server;
	private final transient World world;
	private transient BorderData border = null;
	private transient WorldFileData worldData = null;
	private transient boolean readyToGo = false;
	private transient boolean paused = false;
	private transient boolean pausedForMemory = false;
	private transient int taskID = -1;
	private final transient int chunksPerRun;
	private final transient boolean forceLoad;

	// values for the spiral pattern check which fills out the map to the border
	private transient int x = 0;
	private transient int z = 0;
	private transient boolean isZLeg = false;
	private transient boolean isNeg = false;
	private transient int length = -1;
	private transient int current = 0;
	private transient boolean insideBorder = true;
	private final transient CoordXZ lastChunk = new CoordXZ(0, 0);

	// for reporting progress back to user occasionally
	private transient long lastReport = System.currentTimeMillis();
	private transient long lastAutosave = System.currentTimeMillis();
	private transient int reportTarget = 0;
	private transient int reportTotal = 0;
	private transient int reportNum = 0;
	private static final Runtime rt = Runtime.getRuntime();
	// A map that holds to-be-loaded chunks, and their coordinates
	private transient Map<CompletableFuture<Void>, CoordXZ> pendingChunks;

	// and a set of "Chunk a needed for Chunk b" dependencies, which
	// unfortunately can't be a Map as a chunk might be needed for
	// several others.
	private transient Set<UnloadDependency> preventUnload;
	private boolean ended = false;

	private static class UnloadDependency
	{
		final int neededX;
		final int neededZ;
		final int forX;
		final int forZ;

		UnloadDependency(int neededX, int neededZ, int forX, int forZ)
		{
			this.neededX=neededX;
			this.neededZ=neededZ;
			this.forX=forX;
			this.forZ=forZ;
		}

		@Override
		public boolean equals(Object other)
		{
			if (!(other instanceof UnloadDependency))
				return false;

			return this.neededX == ((UnloadDependency) other).neededX
					&&  this.neededZ == ((UnloadDependency) other).neededZ
					&&  this.forX    == ((UnloadDependency) other).forX
					&&  this.forZ    == ((UnloadDependency) other).forZ;
		}

		@Override
		public int hashCode()
		{
			int hash = 7;
			hash = 79 * hash + this.neededX;
			hash = 79 * hash + this.neededZ;
			hash = 79 * hash + this.forX;
			hash = 79 * hash + this.forZ;
			return hash;
		}
	}

	public WorldFillTask(Server theServer,
						 String worldName,
						 int chunksPerRun,
						 boolean forceLoad,
						 int radius)
	{
		this.server = theServer;
		this.chunksPerRun = chunksPerRun;
		this.forceLoad = forceLoad;

		this.world = server.getWorld(worldName);
		if (this.world == null)
		{
			if (worldName.isEmpty())
				sendMessage("You must specify a world!");
			else
				sendMessage("World \"" + worldName + "\" not found!");
			this.stop();
			return;
		}

		this.border = new BorderData(x, z, radius, radius, true, false);


		// load up a new WorldFileData for the world in question, used to scan region files for which chunks are already fully generated and such
		worldData = WorldFileData.create(world);
		if (worldData == null)
		{
			this.stop();
			return;
		}

		pendingChunks = new HashMap<>();
		preventUnload = new HashSet<>();

		// these are only stored for saving task to config
		int fillDistance = 208;
		this.border.setRadiusX(border.getRadiusX() + fillDistance);
		this.border.setRadiusZ(border.getRadiusZ() + fillDistance);
		this.x = CoordXZ.blockToChunk((int)border.getX());
		this.z = CoordXZ.blockToChunk((int)border.getZ());

		int chunkWidthX = (int) Math.ceil((double)((border.getRadiusX() + 16) * 2) / 16);
		int chunkWidthZ = (int) Math.ceil((double)((border.getRadiusZ() + 16) * 2) / 16);
		int biggerWidth = Math.max(chunkWidthX, chunkWidthZ); //We need to calculate the reportTarget with the bigger width, since the spiral will only stop if it has a size of biggerWidth x biggerWidth
		this.reportTarget = (biggerWidth * biggerWidth) + biggerWidth + 1;

		//This would be another way to calculate reportTarget, it assumes that we don't need time to check if the chunk is outside and then skip it (it calculates the area of the rectangle/ellipse)
		//this.reportTarget = (this.border.getShape()) ? ((int) Math.ceil(chunkWidthX * chunkWidthZ / 4 * Math.PI + 2 * chunkWidthX)) : (chunkWidthX * chunkWidthZ);
		//                                                                       Area of the ellipse                 just to be safe      area of the rectangle

		this.readyToGo = true;
	}


	public void setTaskID(int ID)
	{
		if (ID == -1) this.stop();
		this.taskID = ID;
	}

	@Override
	public void run()
	{
		if (pausedForMemory)
		{	// if available memory gets too low, we automatically pause, so handle that
			if (AvailableMemoryTooLow())
				return;

			pausedForMemory = false;
			readyToGo = true;
			sendMessage("Available memory is sufficient, automatically continuing.");
		}

		if (server == null || !readyToGo || paused)
			return;

		// this is set so it only does one iteration at a time, no matter how frequently the timer fires
		readyToGo = false;
		// and this is tracked to keep one iteration from dragging on too long and possibly choking the system if the user specified a really high frequency
		long loopStartTime = System.currentTimeMillis();

		// Process async results from last time. We don't make a difference
		// whether they were really async, or sync.

		// First, Check which chunk generations have been finished.
		// Mark those chunks as existing and unloadable, and remove
		// them from the pending set.
		int chunksProcessedLastTick = 0;
		Map<CompletableFuture<Void>, CoordXZ> newPendingChunks = new HashMap<>();
		Set<CoordXZ> chunksToUnload = new HashSet<>();
		for (CompletableFuture<Void> cf : pendingChunks.keySet())
		{
			if (cf.isDone())
			{
				++chunksProcessedLastTick;
				// If cf.get() returned the chunk reliably, pendingChunks could
				// be a set and we wouldn't have to map CFs to coords ...
				CoordXZ xz = pendingChunks.get(cf);
				worldData.chunkExistsNow(xz.x, xz.z);
				chunksToUnload.add(xz);
			}
			else
				newPendingChunks.put(cf, pendingChunks.get(cf));
		}
		pendingChunks = newPendingChunks;

		// Next, check which chunks had been loaded because a to-be-generated
		// chunk needed them, and don't have to remain in memory any more.
		Set<UnloadDependency> newPreventUnload = new HashSet<>();
		for (UnloadDependency dependency : preventUnload)
		{
			if (worldData.doesChunkExist(dependency.forX, dependency.forZ))
				chunksToUnload.add(new CoordXZ(dependency.neededX, dependency.neededZ));
			else
				newPreventUnload.add(dependency);
		}
		preventUnload = newPreventUnload;

		// Unload all chunks that aren't needed anymore. NB a chunk could have
		// been needed for two different others, or been generated and needed
		// for one other chunk, so it might be in the unload set wrongly.
		// The ChunkUnloadListener checks this anyway, but it doesn't hurt to
		// save a few µs by not even requesting the unload.

		for (CoordXZ unload : chunksToUnload)
		{
			if (!chunkOnUnloadPreventionList(unload.x, unload.z))
			{
				VersionUtils.getVersionUtils().setChunkForceLoaded(world, unload.x, unload.z, false );
				world.unloadChunkRequest(unload.x, unload.z);
			}
		}

		// Put some damper on chunksPerRun. We don't want the queue to be too
		// full; only fill it to a bit more than what we can
		// process per tick. This ensures the background task can run at
		// full speed and we recover from a temporary drop in generation rate,
		// but doesn't push user-induced chunk generations behind a very
		// long queue of fill-generations.

		int chunksToProcess = chunksPerRun;
		if (chunksProcessedLastTick > 0 || pendingChunks.size() > 0)
		{
			// Note we generally queue 3 chunks, so real numbers are 1/3 of chunksProcessedLastTick and pendingchunks.size
			int chunksExpectedToGetProcessed = (chunksProcessedLastTick - pendingChunks.size()) / 3 + 3;
			if (chunksExpectedToGetProcessed < chunksToProcess)
				chunksToProcess = chunksExpectedToGetProcessed;
		}

		for (int loop = 0; loop < chunksToProcess; loop++)
		{
			// in case the task has been paused while we're repeating...
			if (paused || pausedForMemory)
				return;

			long now = System.currentTimeMillis();

			// every 5 seconds or so, give basic progress report to let user know how it's going
			if (now > lastReport + 5000)
				reportProgress();

			// if this iteration has been running for 45ms (almost 1 tick) or more, stop to take a breather
			if (now > loopStartTime + 45)
			{
				readyToGo = true;
				return;
			}

			// if we've made it at least partly outside the border, skip past any such chunks
			while (!border.insideBorder(CoordXZ.chunkToBlock(x) + 8, CoordXZ.chunkToBlock(z) + 8))
			{
				if (!moveToNext())
					return;
			}
			insideBorder = true;

			if (!forceLoad)
			{
				// skip past any chunks which are confirmed as fully generated using our super-special isChunkFullyGenerated routine
				int rLoop = 0;
				while (worldData.isChunkFullyGenerated(x, z))
				{
					rLoop++;
					insideBorder = true;
					if (!moveToNext())
						return;

					if (rLoop > 255)
					{	// only skim through max 256 chunks (~8 region files) at a time here, to allow process to take a break if needed
						readyToGo = true;
						return;
					}
				}
			}

			pendingChunks.put(getPaperLibChunk(world, x, z, true), new CoordXZ(x, z));

			// There need to be enough nearby chunks loaded to make the server populate a chunk with trees, snow, etc.
			// So, we keep the last few chunks loaded, and need to also temporarily load an extra inside chunk (neighbor closest to center of map)
			int popX = !isZLeg ? x : (x + (isNeg ? -1 : 1));
			int popZ = isZLeg ? z : (z + (!isNeg ? -1 : 1));

			pendingChunks.put(getPaperLibChunk(world, popX, popZ, false), new CoordXZ(popX, popZ));
			preventUnload.add(new UnloadDependency(popX, popZ, x, z));

			// make sure the previous chunk in our spiral is loaded as well (might have already existed and been skipped over)
			pendingChunks.put(getPaperLibChunk(world, lastChunk.x, lastChunk.z, false), new CoordXZ(lastChunk.x, lastChunk.z)); // <-- new CoordXZ as lastChunk isn't immutable
			preventUnload.add(new UnloadDependency(lastChunk.x, lastChunk.z, x, z));

			// move on to next chunk
			if (!moveToNext())
				return;
		}
		// ready for the next iteration to run
		readyToGo = true;
	}

	// step through chunks in spiral pattern from center; returns false if we're done, otherwise returns true
	public boolean moveToNext()
	{
		if (paused || pausedForMemory)
			return false;

		reportNum++;

		// make sure of the direction we're moving (X or Z? negative or positive?)
		if (current < length)
			current++;
		else
		{	// one leg/side of the spiral down...
			current = 0;
			isZLeg ^= true;
			if (isZLeg)
			{	// every second leg (between X and Z legs, negative or positive), length increases
				isNeg ^= true;
				length++;
			}
		}

		// keep track of the last chunk we were at
		lastChunk.x = x;
		lastChunk.z = z;

		// move one chunk further in the appropriate direction
		if (isZLeg)
			z += (isNeg) ? -1 : 1;
		else
			x += (isNeg) ? -1 : 1;

		// if we've been around one full loop (4 legs)...
		if (isZLeg && isNeg && current == 0)
		{	// see if we've been outside the border for the whole loop
			if (!insideBorder)
			{	// and finish if so
				finish();
				return false;
			}	// otherwise, reset the "inside border" flag
			else
				insideBorder = false;
		}
		return true;

		/* reference diagram used, should move in this pattern:
		 *  8 [>][>][>][>][>] etc.
		 * [^][6][>][>][>][>][>][6]
		 * [^][^][4][>][>][>][4][v]
		 * [^][^][^][2][>][2][v][v]
		 * [^][^][^][^][0][v][v][v]
		 * [^][^][^][1][1][v][v][v]
		 * [^][^][3][<][<][3][v][v]
		 * [^][5][<][<][<][<][5][v]
		 * [7][<][<][<][<][<][<][7]
		 */
	}

	// for successful completion
	public void finish()
	{
		this.paused = true;
		reportProgress();
		world.save();
		this.ended = true;
		sendMessage("task successfully completed for world \"" + refWorld() + "\"!");
		this.stop();
	}

	// for cancelling prematurely
	public void cancel()
	{
		this.stop();
	}

	// we're done, whether finished or cancelled
	private void stop()
	{
		if (server == null)
			return;

		readyToGo = false;
		if (taskID != -1)
			server.getScheduler().cancelTask(taskID);
		server = null;

		// go ahead and unload any chunks we still have loaded
		// Set preventUnload to empty first so the ChunkUnloadEvent Listener
		// doesn't get in our way
		if (preventUnload != null)
		{
			Set<UnloadDependency> tempPreventUnload = preventUnload;
			preventUnload = null;
			for (UnloadDependency entry: tempPreventUnload)
			{
				VersionUtils.getVersionUtils().setChunkForceLoaded(world,entry.neededX, entry.neededZ, false);
				world.unloadChunkRequest(entry.neededX, entry.neededZ);
			}
		}
	}

	public boolean chunkOnUnloadPreventionList(int x, int z)
	{
		if (preventUnload != null)
		{
			for (UnloadDependency entry: preventUnload)
			{
				if (entry.neededX == x && entry.neededZ == z)
					return true;
			}
		}
		return false;
	}

	public World getWorld()
	{
		return world;
	}

	// let the user know how things are coming along
	private void reportProgress()
	{
		lastReport = System.currentTimeMillis();
		double perc = getPercentageCompleted();
		if (perc > 100) perc = 100;
		sendMessage(reportNum + " more chunks processed (" + (reportTotal + reportNum) + " total, ~" + new DecimalFormat("0.0").format(perc) + "%" + ")");
		reportTotal += reportNum;
		reportNum = 0;

		// go ahead and save world to disk every 30 seconds or so by default, just in case; can take a couple of seconds or more, so we don't want to run it too often
		if (lastAutosave + (30 * 1000) < lastReport)
		{
			lastAutosave = lastReport;
			sendMessage("Saving the world to disk, just to be on the safe side.");
			world.save();
		}
	}

	// send a message to the server console/log and possibly to an in-game player
	private void sendMessage(String text)
	{
		// Due to chunk generation eating up memory and Java being too slow about GC, we need to track memory availability
		int availMem = AvailableMemory();

		Bukkit.getLogger().info("[Fill] " + text + " (free mem: " + availMem + " MB)");

		if (availMem < 200)
		{	// running low on memory, auto-pause
			pausedForMemory = true;
			text = "Available memory is very low, task is pausing. A cleanup will be attempted now, and the task will automatically continue if/when sufficient memory is freed up.\n Alternatively, if you restart the server, this task will automatically continue once the server is back up.";
			Bukkit.getLogger().info("[Fill] " + text);
			// prod Java with a request to go ahead and do GC to clean unloaded chunks from memory; this seems to work wonders almost immediately
			// yes, explicit calls to System.gc() are normally bad, but in this case it otherwise can take a long long long time for Java to recover memory
			System.gc();
		}
	}


	public String refWorld()
	{
		return world.getName();
	}

	/**
	 * Get the percentage completed for the fill task.
	 *
	 * @return Percentage
	 */
	public double getPercentageCompleted()
	{
		if(this.ended){
			return 100;
		}
		return ((double) (reportTotal + reportNum) / (double) reportTarget) * 100;
	}



	private CompletableFuture<Void> getPaperLibChunk(World world, int x, int z, boolean gen)
	{
		return PaperLib.getChunkAtAsync(world, x, z, gen).thenAccept( (Chunk chunk) ->
		{
			if (chunk != null)
			{
				// toggle "force loaded" flag on for chunk to prevent it from being unloaded while we need it
				VersionUtils.getVersionUtils().setChunkForceLoaded(world, x, z, true);

				// alternatively for 1.14.4+
				//world.addPluginChunkTicket(x, z, pluginInstance);
			}
		});
	}

	public int AvailableMemory()
	{
		return (int)((rt.maxMemory() - rt.totalMemory() + rt.freeMemory()) / 1048576);  // 1024*1024 = 1048576 (bytes in 1 MB)
	}

	public boolean AvailableMemoryTooLow()
	{
		int fillMemoryTolerance = 500;
		return AvailableMemory() < fillMemoryTolerance;
	}
}