package io.github.ph1lou.werewolfplugin.worldloader;

import io.github.ph1lou.werewolfapi.events.GenerationStartEvent;
import io.github.ph1lou.werewolfapi.events.GenerationStopEvent;
import org.bukkit.*;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;



public class WorldFillTask implements Runnable {
	// general task-related reference data
	private Server server;
	private final World world;
	private final BorderData border;
	private final WorldFileData worldData;
	private boolean readyToGo = false;
	private boolean paused = false;
	private boolean pausedForMemory = false;
	private int taskID = -1;
	private final int chunksPerRun;


	// values for the spiral pattern check which fills out the map to the border
	private int x = 0;
	private int z = 0;
	private boolean isZLeg = false;
	private boolean isNeg = false;
	private int length = -1;
	private int current = 0;
	private boolean insideBorder = true;
	private final List<CoordXZ> storedChunks = new LinkedList<>();
	private final Set<CoordXZ> originalChunks = new HashSet<>();
	private final CoordXZ lastChunk = new CoordXZ(0, 0);

	// for reporting progress back to user occasionally
	private long lastReport = System.currentTimeMillis();
	private long lastAutosave = System.currentTimeMillis();
	private int reportTarget = 0;
	private int reportTotal = 0;
	private int reportNum = 0;
	private boolean finish = false;


	public WorldFillTask(World world, int chunksPerRun, int radius) {

		Bukkit.getPluginManager().callEvent(new GenerationStartEvent());
		this.server = Bukkit.getServer();
		this.chunksPerRun = chunksPerRun;

		this.world = world;

		Location spawn = world.getSpawnLocation();
		this.border = new BorderData(spawn.getX(), spawn.getZ(), radius, radius);

		// load up a new WorldFileData for the world in question, used to scan region files for which chunks are already fully generated and such
		worldData = WorldFileData.create(world);
		if (worldData == null) {
			this.stop();
			return;
		}

		this.x = CoordXZ.blockToChunk((int) border.getX());
		this.z = CoordXZ.blockToChunk((int) border.getZ());

		int chunkWidthX = (int) Math.ceil((double) ((border.getRadiusX() + 16) * 2) / 16);
		int chunkWidthZ = (int) Math.ceil((double) ((border.getRadiusZ() + 16) * 2) / 16);
		int biggerWidth = Math.max(chunkWidthX, chunkWidthZ); //We need to calculate the reportTarget with the bigger width, since the spiral will only stop if it has a size of biggerWidth x biggerWidth
		this.reportTarget = (biggerWidth * biggerWidth) + biggerWidth + 1;

		//This would be another way to calculate reportTarget, it assumes that we don't need time to check if the chunk is outside and then skip it (it calculates the area of the rectangle/ellipse)
		//this.reportTarget = (this.border.getShape()) ? ((int) Math.ceil(chunkWidthX * chunkWidthZ / 4 * Math.PI + 2 * chunkWidthX)) : (chunkWidthX * chunkWidthZ);
		//                                                                       Area of the ellipse                 just to be safe      area of the rectangle


		// keep track of the chunks which are already loaded when the task starts, to not unload them
		Chunk[] originals = world.getLoadedChunks();
		for (Chunk original : originals) {
			originalChunks.add(new CoordXZ(original.getX(), original.getZ()));
		}

		this.readyToGo = true;

	}


	public void setTaskID(int ID) {
		if (ID == -1) this.stop();
		this.taskID = ID;
	}


	@Override
	public void run() {

		if (pausedForMemory) {    // if available memory gets too low, we automatically pause, so handle that

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

		for (int loop = 0; loop < chunksPerRun; loop++) {
			// in case the task has been paused while we're repeating...
			if (paused || pausedForMemory)
				return;

			long now = System.currentTimeMillis();

			// every 5 seconds or so, give basic progress report to let user know how it's going
			if (now > lastReport + 5000)
				reportProgress();

			// if this iteration has been running for 45ms (almost 1 tick) or more, stop to take a breather
			if (now > loopStartTime + 45) {
				readyToGo = true;
				return;
			}

			// if we've made it at least partly outside the border, skip past any such chunks
			while (!border.insideBorder(CoordXZ.chunkToBlock(x) + 8, CoordXZ.chunkToBlock(z) + 8)) {
				if (cannotMoveToNext())
					return;
			}
			insideBorder = true;

			while (worldData.isChunkFullyGenerated(x, z)) {
				insideBorder = true;
				if (cannotMoveToNext())
					return;
			}

			// load the target chunk and generate it if necessary
			world.loadChunk(x, z, true);
			worldData.chunkExistsNow(x, z);

			// There need to be enough nearby chunks loaded to make the server populate a chunk with trees, snow, etc.
			// So, we keep the last few chunks loaded, and need to also temporarily load an extra inside chunk (neighbor closest to center of map)
			int popX = !isZLeg ? x : (x + (isNeg ? -1 : 1));
			int popZ = isZLeg ? z : (z + (!isNeg ? -1 : 1));
			world.loadChunk(popX, popZ, false);

			// make sure the previous chunk in our spiral is loaded as well (might have already existed and been skipped over)
			if (!storedChunks.contains(lastChunk) && !originalChunks.contains(lastChunk)) {
				world.loadChunk(lastChunk.x, lastChunk.z, false);
				storedChunks.add(new CoordXZ(lastChunk.x, lastChunk.z));
			}

			// Store the coordinates of these latest 2 chunks we just loaded, so we can unload them after a bit...
			storedChunks.add(new CoordXZ(popX, popZ));
			storedChunks.add(new CoordXZ(x, z));

			// If enough stored chunks are buffered in, go ahead and unload the oldest to free up memory
			while (storedChunks.size() > 8) {
				CoordXZ cord = storedChunks.remove(0);
				if (!originalChunks.contains(cord))
					world.unloadChunkRequest(cord.x, cord.z);
			}

			// move on to next chunk
			if (cannotMoveToNext())
				return;
		}

		// ready for the next iteration to run
		readyToGo = true;
	}

	// step through chunks in spiral pattern from center; returns false if we're done, otherwise returns true
	public boolean cannotMoveToNext() {

		if (paused || pausedForMemory)
			return true;

		reportNum++;

		// keep track of progress in case we need to save to config for restoring progress after server restart

		// make sure of the direction we're moving (X or Z? negative or positive?)
		if (current < length)
			current++;
		else {    // one leg/side of the spiral down...

			current = 0;
			isZLeg ^= true;
			if (isZLeg) {    // every second leg (between X and Z legs, negative or positive), length increases
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
		if (isZLeg && isNeg && current == 0) {    // see if we've been outside the border for the whole loop

			if (!insideBorder) {    // and finish if so
				finish();
				return true;
			}    // otherwise, reset the "inside border" flag
			else
				insideBorder = false;
		}
		return false;

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
	public void finish() {
		this.paused = true;
		this.finish = true;
		reportProgress();
		world.save();
		sendMessage("task successfully completed for world \"" + refWorld() + "\"!");
		this.stop();
	}

	// for cancelling prematurely

	// we're done, whether finished or cancelled
	public void stop() {

		Bukkit.getPluginManager().callEvent(new GenerationStopEvent());

		if (server == null)
			return;

		readyToGo = false;
		if (taskID != -1)
			server.getScheduler().cancelTask(taskID);
		server = null;

		// go ahead and unload any chunks we still have loaded
		while (!storedChunks.isEmpty()) {
			CoordXZ cord = storedChunks.remove(0);
			if (!originalChunks.contains(cord))
				world.unloadChunkRequest(cord.x, cord.z);
		}
	}

	// is this task still valid/workable?

	// let the user know how things are coming along
	private void reportProgress() {
		lastReport = System.currentTimeMillis();
		double percentage = getPercentageCompleted();
		if (percentage > 100) percentage = 100;
		sendMessage(reportNum + " more chunks processed (" + (reportTotal + reportNum) + " total, ~" + new DecimalFormat("0.0").format(percentage) + "%" + ")");
		reportTotal += reportNum;
		reportNum = 0;

		// go ahead and save world to disk every 30 seconds or so by default, just in case; can take a couple of seconds or more, so we don't want to run it too often
		int fillAutoSaveFrequency = 30;
		if (lastAutosave + (fillAutoSaveFrequency * 1000) < lastReport) {
			lastAutosave = lastReport;
			sendMessage("Saving the world to disk, just to be on the safe side.");
			world.save();
		}
	}

	// send a message to the server console/log and possibly to an in-game player
	private void sendMessage(String text) {
		// Due to chunk generation eating up memory and Java being too slow about GC, we need to track memory availability
		int availMem = AvailableMemory();

		System.out.println("[Fill] " + text + " (free mem: " + availMem + " MB)");


		if (availMem < 200) {    // running low on memory, auto-pause

			pausedForMemory = true;
			text = "Available memory is very low, task is pausing. A cleanup will be attempted now, and the task will automatically continue if/when sufficient memory is freed up.\n Alternatively, if you restart the server, this task will automatically continue once the server is back up.";
			System.out.println("[Fill] " + text);

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
	public double getPercentageCompleted() {
		if (finish) return 100;
		return Math.min(100, ((double) (reportTotal + reportNum) / (double) reportTarget) * 100);
	}


	public int AvailableMemory() {
		Runtime rt = Runtime.getRuntime();
		return (int) ((rt.maxMemory() - rt.totalMemory() + rt.freeMemory()) / 1048576);  // 1024*1024 = 1048576 (bytes in 1 MB)
	}

	public boolean AvailableMemoryTooLow() {
		return AvailableMemory() < 500;
	}
}
