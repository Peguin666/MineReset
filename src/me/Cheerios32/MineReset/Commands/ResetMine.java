package me.Cheerios32.MineReset.Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import me.Cheerios32.MineReset.Mine;
import me.Cheerios32.MineReset.MineManager;
import me.Cheerios32.MineReset.MineReset;

class ResetMine extends SubCommand {

	@Override
	void execute(CommandSender sender, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(helpMsg());
			return;
		}
		else {
			MineManager managerInst = MineReset.getManagerRef();
			if (args[0].contentEquals("*")) {
				List<Mine> allMines = managerInst.getMines();
				for (Mine m: allMines) {
					m.forceReset();
				}
			}
			else {
				Mine which = managerInst.getMineByName(args[0]);
				if (which == null) {
					sender.sendMessage(ChatColor.RED + "Error: there is no such mine!");
					return;
				}
				which.forceReset();
			}
			return;
		}

	}

	@Override
	String helpMsg() {
		// TODO Auto-generated method stub
		return ChatColor.BLUE + "/mine reset <mine>" + ChatColor.GREEN + " : Forces a mine to reset immediately.";
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
			if (argsSoFar[0].contentEquals("")) {
				mineNames.add("*");
			}
			return mineNames;
		}
		return new ArrayList<String>();
	}

}
