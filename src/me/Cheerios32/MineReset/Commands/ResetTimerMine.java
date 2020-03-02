package me.Cheerios32.MineReset.Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import me.Cheerios32.MineReset.Mine;
import me.Cheerios32.MineReset.MineManager;
import me.Cheerios32.MineReset.MineReset;

class ResetTimerMine extends SubCommand {

	@Override
	void execute(CommandSender sender, String[] args) {
		if (args.length != 2) {
			sender.sendMessage(helpMsg());
			return;
		}
		else {
			long gap;
			try {
				gap = Long.parseLong(args[1]);
			}
			catch (NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + "Error: time must be a number!");
				return;
			}
			MineManager managerInst = MineReset.getManagerRef();
			Mine which = managerInst.getMineByName(args[0]);
			if (which == null) {
				sender.sendMessage(ChatColor.RED + "Error: there is no such mine!");
				return;
			}
			which.setResetGap(gap);
			if (gap > 0) {
				sender.sendMessage(ChatColor.GREEN + "Mine will now automatically reset every " + gap + " seconds.");
			}
			else {
				sender.sendMessage(ChatColor.GREEN + "Mine will no longer automatically reset.");
			}
			return;
		}

	}

	@Override
	String helpMsg() {
		// TODO Auto-generated method stub
		return ChatColor.BLUE + "/mine timer <mine> <time>" + ChatColor.GREEN + " : Sets how frequently this mine will reset in seconds. Remember 1 hour = 3600 seconds. Set to 0 or a negative number for the mine to never automatically reset.";
	}

	@Override
	boolean canRun(CommandSender sender) {
		// TODO Auto-generated method stub
		return sender.hasPermission("mine.reset");
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
