package me.Cheerios32.MineReset.Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

class HelpMine extends SubCommand {

	MineResetCommandExecutor mrce;
	
	HelpMine(MineResetCommandExecutor executor){
		mrce = executor;
	}
	
	@Override
	void execute(CommandSender sender, String[] args) {
		// TODO Auto-generated method stub
		List<SubCommand> playerCommands = mrce.getMineCommands(sender);
		sender.sendMessage(ChatColor.BLUE + "-------[Mine commands]-------");
		for (SubCommand sc: playerCommands) {
			sender.sendMessage(sc.helpMsg());
		}
		sender.sendMessage(ChatColor.BLUE + "Plugin made by Cheerios32.");
	}

	@Override
	String helpMsg() {
		// TODO Auto-generated method stub
		return ChatColor.BLUE + "/mine help" + ChatColor.GREEN + " : Shows a list of plugin commands.";
	}

	@Override
	boolean canRun(CommandSender sender) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	String getNoPermMsg(CommandSender sender) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	List<String> tabComplete(CommandSender sender, String[] argsSoFar) {
		// TODO Auto-generated method stub
		return new ArrayList<String>();
	}

}
