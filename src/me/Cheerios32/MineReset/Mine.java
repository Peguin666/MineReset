package me.Cheerios32.MineReset;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
@SerializableAs("Mine")
public class Mine implements ConfigurationSerializable{
	
	private Location minLoc;
	private Location maxLoc;
	private Location safetyPoint;
	
	private boolean forceReset;
	
	private long lastResetTime;
	private long resetGap;
	
	private List<ChanceBlock> possibleRefillValues = new ArrayList<ChanceBlock>();
	
	private String mineName;
	
	private Random randomGenerator = new Random();
	
	private String parseLoc(Location l) {
		return "(" + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ() + ")";
	}
	
	public String getName() {
		return mineName;
	}
	
	public World getWorld() {
		return minLoc.getWorld();
	}
	
	public void forceReset() {
		forceReset = true;
	}
	
	public void setSafetyPoint(Location newPoint) {
		safetyPoint = newPoint;
	}
	
	public long getTimeUntilReset() {
		if (resetGap <= 0) {
			return 1;
		}
		else {
			return (resetGap + lastResetTime) - Instant.now().getEpochSecond();
		}
	}
	
	public boolean shouldReset() {
		if (forceReset) {
			forceReset = false;
			return true;
		}
		else {
			return getTimeUntilReset() <= 0;
		}
	}
	
	public void setResetGap(long newValue) {
		resetGap = newValue;
	}
	
	public boolean setBlockChance(Material m, double chance) {
		if (!m.isBlock()) {
			return false; //Stupid child :c
		}
		boolean done = false;
		for (int index = 0;index < possibleRefillValues.size();index++) {
			ChanceBlock cb = possibleRefillValues.get(index);
			if (cb == null) {
				continue; //How?
			}
			if (cb.getMaterial().equals(m)) {
				if (chance == 0) {
					possibleRefillValues.remove(cb);
					return true;
				}
				else {
					cb.setChance(chance);
					possibleRefillValues.set(index, cb);
					done = true;
				}
			}
		}
		if (!done) {
			ChanceBlock newBlock = new ChanceBlock(m, chance);
			possibleRefillValues.add(newBlock);
		}
		return true;
	}
	
	List<ChanceBlock> getBlockChances(){
		return possibleRefillValues;
	}
	
	public Mine(Location min, Location max, String name){
		minLoc = min;
		maxLoc = max;
		mineName = name;
	}
	
	private Mine(Location min, Location max, Location safetyTp, String name, List<ChanceBlock> blockChances, long resetDiff, long resetTimeLast) {
		minLoc = min;
		maxLoc = max;
		mineName = name;
		safetyPoint = safetyTp;
		possibleRefillValues = blockChances;
		resetGap = resetDiff;
		lastResetTime = resetTimeLast;
		Bukkit.getLogger().info("Mine loaded: " + this.toString());
	}
	
	@Override
	public String toString() {
		return "Mine called " + mineName + " going from " + parseLoc(minLoc) + " to " + parseLoc(maxLoc);
	}

	public static Mine deserialize(Map<String,Object> serialData) {
		Location min = (Location) serialData.get("MinLoc");
		Location max = (Location) serialData.get("MaxLoc");
		Location safeSpawn = (Location) serialData.get("SafeSpawn");
		String name = (String) serialData.get("Name");
		long lastReset = (int) serialData.getOrDefault("LastReset", 0L);
		long resetDiff = (int) serialData.getOrDefault("ResetTime", 86400L);
		@SuppressWarnings("unchecked")
		List<ChanceBlock> blockChances = (List<ChanceBlock>) serialData.get("Blocks");
		return new Mine(min, max, safeSpawn, name, blockChances, resetDiff, lastReset);
	}
	
	private Material drawBlock(double max) {
		double decimal = randomGenerator.nextDouble() * max;
		double total = 0;
		for (ChanceBlock cb: possibleRefillValues) {
			if (cb == null) {
				continue; //How?
			}
			total += cb.getChance();
			if (total > decimal) {
				return cb.getMaterial();
			}
		}
		return Material.AIR;
	}
	
	private boolean isInDanger(Player pl) {
		Location playerLoc = pl.getLocation();
		if (minLoc.getBlockX() <= playerLoc.getBlockX() && maxLoc.getBlockX() >= playerLoc.getBlockX()) {
			if (minLoc.getBlockY() <= playerLoc.getBlockY() && maxLoc.getBlockY() >= playerLoc.getBlockY()) {
				if (minLoc.getBlockZ() <= playerLoc.getBlockZ() && maxLoc.getBlockZ() >= playerLoc.getBlockZ()) {
					return true;
				}
			}
		}
		return false;
	}
	
	void evacuateMine() {
		if (safetyPoint == null) {
			return; //Can't evacuate :(
		}
		for (Player pl: minLoc.getWorld().getPlayers()) {
			if (isInDanger(pl)) {
				pl.teleport(safetyPoint);
				pl.sendMessage(ChatColor.GREEN + "You were saved from a horrible death!");
			}
		}
	}
	
	public long queueMineRegen(BlockSetter queueArea) {
		//rescale();
		long blocksProcessed = 0;
		double highestRoll = 0;
		for (ChanceBlock cb: possibleRefillValues) {
			if (cb == null) {
				continue; //How?
			}
			highestRoll += cb.getChance();
		}
		MineReset.sendDebugMsg("Setting RNG seed.");
		randomGenerator.setSeed(Instant.now().toEpochMilli());
		MineReset.sendDebugMsg("Starting block drawing.");
		for (int x = minLoc.getBlockX();x<=maxLoc.getBlockX();x++) {
			for (int y = minLoc.getBlockY();y<=maxLoc.getBlockY();y++) {
				for (int z = minLoc.getBlockZ();z<=maxLoc.getBlockZ();z++) {
					//Bukkit.getLogger().info("Getting time.");
					int timeStart = Instant.now().getNano();
					//Bukkit.getLogger().info("Getting block object.");
					Block here = minLoc.getWorld().getBlockAt(x, y, z);
					//Bukkit.getLogger().info("Drawing new material.");
					Material replacement = drawBlock(highestRoll);
					//Bukkit.getLogger().info("Queuing block.");
					queueArea.addBlock(here, replacement);
					blocksProcessed++;
					MineReset.sendDebugMsg("Rolled block at " + x + "," + y + "," + z + " in " + (Instant.now().getNano() - timeStart) + " ns.");
				}
			}
		}
		MineReset.sendDebugMsg("Done drawing blocks.");
		lastResetTime = Instant.now().getEpochSecond();
		return blocksProcessed;
		
	}
	
	@Override
	public Map<String, Object> serialize() {
		// TODO Auto-generated method stub
		Map<String,Object> serialData = new HashMap<String,Object>();
		//rescale();
		serialData.put("MinLoc", minLoc);
		serialData.put("MaxLoc", maxLoc);
		serialData.put("SafeSpawn", safetyPoint);
		serialData.put("Name", mineName);
		serialData.put("Blocks", possibleRefillValues);
		serialData.put("LastReset", lastResetTime);
		serialData.put("ResetTime", resetGap);
		return serialData;
	}
}



