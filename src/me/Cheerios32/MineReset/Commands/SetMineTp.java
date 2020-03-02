package me.Cheerios32.MineReset.Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.Cheerios32.MineReset.Mine;
import me.Cheerios32.MineReset.MineReset;

class SetMineTp extends SubCommand {

	@Override
	void execute(CommandSender sender, String[] args) {
		// TODO Auto-generated method stub
		if (args.length == 0) {
			sender.sendMessage(helpMsg());
			return;
		}
		if (sender instanceof Player) {
			String mineName = args[0];
			Mine affected = MineReset.getManagerRef().getMineByName(mineName);
			if (affected == null) {
				sender.sendMessage(ChatColor.RED + "Error: that mine does not exist!");
				return;
			}
			affected.setSafetyPoint(((Player) sender).getLocation());
			sender.sendMessage(ChatColor.GREEN + "Safety teleport set to your location for " + mineName);
		}
	}

	@Override
	String helpMsg() {
		// TODO Auto-generated method stub
		return ChatColor.BLUE + "/mine settp <name>" + ChatColor.GREEN + " : Sets the place where you will be teleported to safety if you're in the mine when it resets.";
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
		if (argsSoFar.length == 1) {
			List<Mine> allMines = MineReset.getManagerRef().getMines();
			List<String> mineNames = new ArrayList<String>();
			for (Mine m:allMines) {
				String mName = m.getName();
				if (mName.startsWith(argsSoFar[0])) {
					mineNames.add(mName);
				}
			}
			return mineNames;
		}
		return new ArrayList<String>();
	}

}
