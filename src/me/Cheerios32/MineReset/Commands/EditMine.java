package me.Cheerios32.MineReset.Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.Cheerios32.MineReset.Mine;
import me.Cheerios32.MineReset.MineReset;

class EditMine extends SubCommand {

	@Override
	void execute(CommandSender sender, String[] args) {
		// TODO Auto-generated method stub
		if (args.length != 2) {
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
			String chanceAsStr = args[1];
			double chance;
			try {
				chance = Double.parseDouble(chanceAsStr);
			}
			catch (NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + "Error: chance must be a number!");
				return;
			}
			ItemStack heldItem = ((Player) sender).getInventory().getItemInMainHand();
			Material setIntoMine = heldItem.getType();
			if (affected.setBlockChance(setIntoMine, chance)) {
				if (chance == 0) {
					sender.sendMessage(ChatColor.GREEN + "Block removed successfully!");
				}
				else {
					sender.sendMessage(ChatColor.GREEN + "Block " + setIntoMine.toString() + " added with a weight of " + chance + ".");
				}
			}
			else {
				sender.sendMessage(ChatColor.RED + "Error: you aren't holding a block!");
			}
			return;
			
		}
		else {
			sender.sendMessage(ChatColor.RED + "Error: this command must be run as a player!");
		}
	}

	@Override
	String helpMsg() {
		// TODO Auto-generated method stub
		return ChatColor.BLUE + "/mine edit <name> <chance>" + ChatColor.GREEN + " : Adds the block in your hand to the mine, with the specified weight. The weight is the relative chance of the block spawning: i.e. if a block has a weight of 2 it'll spawn twice as often as a block with a weight of 1. Set to 0 to remove the block from the mine.";
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
