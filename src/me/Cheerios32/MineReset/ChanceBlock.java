package me.Cheerios32.MineReset;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("ChanceBlock")
public class ChanceBlock implements ConfigurationSerializable{

	@Override
	public Map<String, Object> serialize() {
		Map<String,Object> serialData = new HashMap<String,Object>();
		serialData.put("Material", madeFrom.ordinal());
		serialData.put("Chance", drawChance);
		return serialData;
	}
	
	ChanceBlock(Material what,double chance){
		madeFrom = what;
		drawChance = chance;
	}
	
	public static ChanceBlock deserialize(Map<String,Object> serialData) {
		return new ChanceBlock(Material.values()[(int) serialData.get("Material")], (double) serialData.get("Chance"));
	}
	
	Material getMaterial() {
		return madeFrom;
	}
	
	double getChance() {
		return drawChance;
	}
	
	void setChance(double newChance) {
		drawChance = newChance;
	}
	
	private Material madeFrom;
	private double drawChance;
}