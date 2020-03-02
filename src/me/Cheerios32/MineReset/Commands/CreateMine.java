package me.Cheerios32.MineReset.Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.world.World;

import me.Cheerios32.MineReset.Mine;
import me.Cheerios32.MineReset.MineReset;

class CreateMine extends SubCommand {

	@Override
	void execute(CommandSender sender, String[] args) {
		// TODO Auto-generated method stub
		if (args.length == 0) {
			sender.sendMessage(helpMsg());
			return;
		}
		if (sender instanceof Player) {
			String name = args[0];
			if (name.contentEquals("*")) {
				sender.sendMessage(ChatColor.RED + "Error: the name '*' is reserved, you may not use it.");
				return;
			}
			else if (MineReset.getManagerRef().getMineByName(name) != null) {
				sender.sendMessage(ChatColor.RED + "Error: this mine already exists!");
				return;
			}
			
			LocalSession weSelection = MineReset.getWERef().getSession((Player) sender);
			org.bukkit.World currentWorld = ((Player) sender).getWorld();
			World sk89World = new BukkitWorld(currentWorld);
			RegionSelector selectRegion = weSelection.getRegionSelector(sk89World);
			Region selectedArea;
			try {
				selectedArea = selectRegion.getRegion();
			} catch (IncompleteRegionException e) {
				// TODO Auto-generated catch block
				sender.sendMessage(ChatColor.RED + "Error: Please select a region!");
				return;
			}
			BlockVector3 minPoint = selectedArea.getMinimumPoint();
			BlockVector3 maxPoint = selectedArea.getMaximumPoint();
			
			Mine createdMine = new Mine(MineReset.blockVectorToLoc(minPoint, currentWorld), MineReset.blockVectorToLoc(maxPoint, currentWorld), name);
			sender.sendMessage(createdMine.toString());
			MineReset.getManagerRef().addMine(createdMine);
			createdMine.setResetGap(86400);
			sender.sendMessage(ChatColor.GREEN + "Mine will reset once per day by default. Use /mine timer to change this.");
		}
		else {
			sender.sendMessage(ChatColor.RED + "Error: this command must be run as a player!");
		}
	}

	@Override
	String helpMsg() {
		// TODO Auto-generated method stub
		return ChatColor.BLUE + "/mine create <name>" + ChatColor.GREEN + " : Creates a mine called <name>. No spaces allowed in the name.";
	}

	@Override
	boolean canRun(CommandSender sender) {
		// TODO Auto-generated method stub
		return sender.hasPermission("mine.create");
	}

	@Override
	String getNoPermMsg(CommandSender sender) {
		// TODO Auto-generated method stub
		return ChatColor.RED + "You do not have permission to do that.";
	}

	@Override
	List<String> tabComplete(CommandSender sender, String[] argsSoFar) {
		// TODO Auto-generated method stub
		return new ArrayList<String>();
	}

}
