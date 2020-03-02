package me.Cheerios32.MineReset;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class MineHologram extends PlaceholderExpansion {

	private MineReset plugin;
	
	public MineHologram(MineReset us) {
		plugin = us;
	}
	/**
     * Because this is an internal class,
     * you must override this method to let PlaceholderAPI know to not unregister your expansion class when
     * PlaceholderAPI is reloaded
     *
     * @return true to persist through reloads
     */
    @Override
    public boolean persist(){
        return true;
    }
    
    /**
     * The name of the person who created this expansion should go here.
     * <br>For convienience do we return the author from the plugin.yml
     * 
     * @return The name of the author as a String.
     */
    @Override
    public String getAuthor(){
        return plugin.getDescription().getAuthors().toString();
    }

    /**
     * The placeholder identifier should go here.
     * <br>This is what tells PlaceholderAPI to call our onRequest 
     * method to obtain a value if a placeholder starts with our 
     * identifier.
     * <br>This must be unique and can not contain % or _
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public String getIdentifier(){
        return "MineReset";
    }

    /**
     * This is the version of the expansion.
     * <br>You don't have to use numbers, since it is set as a String.
     *
     * For convienience do we return the version from the plugin.yml
     *
     * @return The version as a String.
     */
    @Override
    public String getVersion(){
        return plugin.getDescription().getVersion();
    }

    private String parseTime(long secLeft) {
    	String retValue = "";
    	//We'll go up to weeks, just in case
    	long weeks = Math.floorDiv(secLeft, 7*24*3600);
    	long rem = Math.floorMod(secLeft, 7*24*3600);
    	long days = Math.floorDiv(rem, 86400);
    	rem = Math.floorMod(rem, 86400);
    	long hours = Math.floorDiv(rem, 3600);
    	rem = Math.floorMod(rem, 3600);
    	long mins = Math.floorDiv(rem, 60);
    	long sec = Math.floorMod(rem, 60);
    	if (weeks != 0) {
    		retValue += (weeks + " weeks, ");
    	}
    	if (days != 0) {
    		retValue += (days + " days, ");
    	}
    	if (hours != 0) {
    		retValue += (hours + " hours, ");
    	}
    	if (mins != 0) {
    		retValue += (mins + " minutes, ");
    	}
    	if (sec != 0) {
    		retValue += (sec + " seconds, ");
    	}
    	int finalComma = retValue.lastIndexOf(",");
    	if (finalComma != -1) {
    		return retValue.substring(0, finalComma);
    	}
    	else {
    		return retValue; //Wtf?
    	}
    	
    }
    
    /**
     * This is the method called when a placeholder with our identifier 
     * is found and needs a value.
     * <br>We specify the value identifier in this method.
     * <br>Since version 2.9.1 can you use OfflinePlayers in your requests.
     *
     * @param  player
     *         A {@link org.bukkit.Player Player}.
     * @param  identifier
     *         A String containing the identifier/value.
     *
     * @return possibly-null String of the requested identifier.
     */
    @Override
    public String onPlaceholderRequest(Player player, String identifier){
    	Mine referenced = MineReset.getManagerRef().getMineByName(identifier);
    	if (referenced == null) {
    		return ChatColor.RED + "Unknown mine " + identifier + "!";
    	}
    	else {
    		long timeUntilReset = referenced.getTimeUntilReset();
    		return "Resets in " + parseTime(timeUntilReset);
    	}
    }

}
