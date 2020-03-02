package me.Cheerios32.MineReset;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.BlockVector3;

import me.Cheerios32.MineReset.Commands.MineResetCommandExecutor;

public class MineReset extends JavaPlugin{
	private static MineReset meRef;
	private static WorldEditPlugin wep;
	private MineResetCommandExecutor mrce;
	private static MineManager manager;
	
	private File mineSaveLoc;
	
	private static boolean debug = true;
	private static boolean useGlobalMsg = true;
	
	public static MineReset getRef() {
		return meRef;
	}
	
	public static MineManager getManagerRef() {
		return manager;
	}
	
	public static void sendDebugMsg(String msgContents) {
		if (debug) {
			final String newMsgContents = ChatColor.GOLD + "[Mine debug] " + ChatColor.GREEN + msgContents;
			for (Player pl: Bukkit.getOnlinePlayers()) {
				if (pl.hasPermission("mine.debug")) {
					Bukkit.getScheduler().runTask(getRef(), () -> pl.sendMessage(newMsgContents));
				}
			}
			Bukkit.getLogger().info(newMsgContents);
		}
	}
	
	public static WorldEditPlugin getWERef() {
		return wep;
	}
	
	public static Location blockVectorToLoc(BlockVector3 bv, org.bukkit.World currentWorld) {
		return new Location(currentWorld,bv.getBlockX(), bv.getBlockY(), bv.getBlockZ());
	}
	
	public static void mineInform(String msg, World where) {
		if (useGlobalMsg) {
			for (Player pl: where.getPlayers()) {
				pl.sendMessage(msg);
			}
		}
	}
	
	@Override
	public void onEnable() {
		ConfigurationSerialization.registerClass(Mine.class, "Mine");
		ConfigurationSerialization.registerClass(ChanceBlock.class, "ChanceBlock");
		
		saveDefaultConfig();
		
		debug = getConfig().getBoolean("debug", false);
		useGlobalMsg = getConfig().getBoolean("globalMineMsg", false);
		
		meRef = this;
		wep = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
		if (wep == null) {
			getLogger().severe("Error: cannot find World Edit plugin. Disabling.");
			getServer().getPluginManager().disablePlugin(this);
		}
		mrce = new MineResetCommandExecutor();
		getCommand("mine").setExecutor(mrce);
		getCommand("mine").setTabCompleter(mrce);
		mineSaveLoc = new File(getDataFolder().getAbsolutePath() + "/mines.yml");
		manager = new MineManager(mineSaveLoc);
		
		if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
            new MineHologram(this).register(); //Register with placeholderAPI
      }
		
	}
	
	@Override
	public void onDisable() {
		try {
			manager.saveChanges();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); //Ah hell
			getLogger().severe("Error saving mines! Changes made will not be saved.");
		}
	}
	
}
