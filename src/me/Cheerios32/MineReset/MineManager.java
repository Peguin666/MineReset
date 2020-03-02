package me.Cheerios32.MineReset;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitTask;

public class MineManager {
	private List<Mine> mines = new ArrayList<Mine>();
	
	private File targetFile;
	
	private volatile boolean currentlyResetting = false;
	
	private YamlConfiguration saveFile;
	
	private BukkitTask checkTimerTask;
	
	@SuppressWarnings("unchecked")
	MineManager(File loadFrom){
		targetFile = loadFrom;
		saveFile = YamlConfiguration.loadConfiguration(loadFrom);
		mines = (List<Mine>) saveFile.get("Mines", new ArrayList<Mine>());
		checkTimerTask = Bukkit.getScheduler().runTaskTimerAsynchronously(MineReset.getRef(), () -> mineResetTimer(), 20, 20);
	}
	
	public void saveChanges() throws IOException {
		saveFile.set("Mines", mines);
		saveFile.save(targetFile);
		checkTimerTask.cancel();
	}
	
	public void addMine(Mine toAdd) {
		mines.add(toAdd);
	}
	
	public Mine getMineByName(String name) {
		for (Mine m: mines) {
			if (m == null) {
				continue;
			}
			if (m.getName().equalsIgnoreCase(name)) {
				return m;
			}
		}
		return null;
	}
	
	public void removeMine(Mine toDelete) {
		mines.remove(toDelete);
	}
	
	public List<Mine> getMines(){
		return mines;
	}
	
	public void resetMineVisible(Mine which,CommandSender sender) {
		Bukkit.getScheduler().runTaskAsynchronously(MineReset.getRef(), () -> resetMine(which, sender));
	}
	/**
	 * Resets a mine. RUN ASYNC ONLY.
	 * @param which
	 * @param trigger
	 */
	
	private void resetMine(Mine which, CommandSender trigger) {
		//Designed to be run async
		currentlyResetting = true;
		long currentTime = Instant.now().toEpochMilli();
		MineReset.sendDebugMsg("Starting reset...");
		BlockSetter blockQueueProcessor = new BlockSetter();
		MineReset.sendDebugMsg("Starting block rolling...");
		which.queueMineRegen(blockQueueProcessor);
		Bukkit.getScheduler().runTask(MineReset.getRef(), () -> which.evacuateMine());
		long newTime = Instant.now().toEpochMilli();
		while (!blockQueueProcessor.processUntil(10)) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace(); //Reee
			}
		}
		long finalTime = Instant.now().toEpochMilli();
		Bukkit.getScheduler().runTask(MineReset.getRef(), () -> trigger.sendMessage(ChatColor.GOLD + "Mine blocks queued in " + (newTime - currentTime) + "ms. Blocks placed in " + (finalTime - newTime) + "ms."));
		currentlyResetting = false;
		MineReset.mineInform(ChatColor.BLUE + "[Mine] " + ChatColor.GREEN + "The mine " + which.getName() + " has reset!", which.getWorld());
	}
	
	private void mineResetTimer() {
		for (Mine m: mines) {
			if (m == null) {
				//...
				continue;
			}
			else if(m.getTimeUntilReset() == 600) {
				//Ten minute warning.
				MineReset.mineInform(ChatColor.BLUE + "[Mine] " + ChatColor.GREEN + "The mine " + m.getName() + " is resetting in ten minutes!", m.getWorld());
			}
			else if(m.getTimeUntilReset() == 300) {
				//Five minute warning.
				MineReset.mineInform(ChatColor.BLUE + "[Mine] " + ChatColor.GREEN + "The mine " + m.getName() + " is resetting in five minutes!", m.getWorld());
			}
			else if(m.getTimeUntilReset() == 60) {
				//One minute warning.
				MineReset.mineInform(ChatColor.BLUE + "[Mine] " + ChatColor.GREEN + "The mine " + m.getName() + " is resetting in one minute!", m.getWorld());
			}
			else if(m.getTimeUntilReset() == 10) {
				//Ten minute warning.
				MineReset.mineInform(ChatColor.BLUE + "[Mine] " + ChatColor.GREEN + "The mine " + m.getName() + " is resetting in ten seconds!", m.getWorld());
			}
			else if (m.shouldReset()) {
				if (currentlyResetting) {
					//Pass so we don't lag the server.
					return;
				}
				MineReset.mineInform(ChatColor.BLUE + "[Mine] " + ChatColor.GREEN + "The mine " + m.getName() + " is resetting!", m.getWorld());
				resetMine(m, Bukkit.getConsoleSender());
				break; //Only want to queue one at a time.
			}
		}
	}
	
}
