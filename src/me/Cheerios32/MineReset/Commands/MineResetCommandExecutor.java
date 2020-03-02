package me.Cheerios32.MineReset.Commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class MineResetCommandExecutor implements CommandExecutor, TabCompleter {

	private Map<String,SubCommand> commandList = new HashMap<String,SubCommand>();
	
	void addCommandRegistry(SubCommand registeredCmd, String friendlyName) {
		commandList.put(friendlyName, registeredCmd);
	}
	
	public MineResetCommandExecutor(){
		addCommandRegistry(new CreateMine(), "create");
		addCommandRegistry(new EditMine(), "edit");
		addCommandRegistry(new SetMineTp(), "settp");
		addCommandRegistry(new ResetMine(), "reset");
		addCommandRegistry(new RemoveMine(), "remove");
		addCommandRegistry(new HelpMine(this), "help");
		addCommandRegistry(new ResetTimerMine(), "timer");
	}
	
	List<SubCommand> getMineCommands(CommandSender requester){
		List<SubCommand> output = new ArrayList<SubCommand>();
		for (String key: commandList.keySet()) {
			SubCommand sc = commandList.get(key);
			if (sc.canRun(requester)) {
				output.add(sc);
			}
		}
		return output;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		// TODO Auto-generated method stub
		if (args.length == 1) {
			//First one, so tab complete possible sub commands
			List<String> possible = new ArrayList<String>();
			Set<String> all = commandList.keySet();
			for (String s: all) {
				if (s.startsWith(args[0])) {
					possible.add(s);
				}
			}
			return possible;
		}
		else {
			//Delegate to sub command to finish tab completing.
			SubCommand sc = commandList.get(args[0]);
			if (sc != null) {
				return sc.tabComplete(sender, Arrays.copyOfRange(args,1,args.length));
			}
			return new ArrayList<String>();
		}
	}
	
	

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0) {
			return false;
		}
		else {
			SubCommand sc = commandList.get(args[0]);
			if (sc == null) {
				return false;
			}
			else if(!sc.canRun(sender)) {
				sender.sendMessage(sc.getNoPermMsg(sender));
			}
			else {
				sc.execute(sender, Arrays.copyOfRange(args,1,args.length));
			}
			return true;
		}
	}

}
