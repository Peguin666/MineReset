package me.Cheerios32.MineReset.Commands;

import java.util.List;

import org.bukkit.command.CommandSender;

abstract class SubCommand {
	abstract void execute(CommandSender sender, String[] args);
	abstract String helpMsg();
	abstract boolean canRun(CommandSender sender);
	abstract String getNoPermMsg(CommandSender sender);
	abstract List<String> tabComplete(CommandSender sender, String[] argsSoFar);
	
}
