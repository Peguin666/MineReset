package me.Cheerios32.MineReset;

import java.time.Instant;
import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;

class BlockSetter {
	private class SettableBlock{
		private Block toSet;
		private Material toWhat;
		SettableBlock(Block where, Material what){
			toSet = where;
			toWhat = what;
		}
		
		void setBlock() {
			Bukkit.getScheduler().runTask(MineReset.getRef(), () -> toSet.setType(toWhat));
		}
	}
	private volatile LinkedList<SettableBlock> blocksQueue = new LinkedList<SettableBlock>();
	
	void addBlock(Block where, Material newMat) {
		blocksQueue.addLast(new SettableBlock(where, newMat));
	}
	
	/** 
	 * Places as many blocks as it possibly can until one of two conditions happen:
	 * 1) It runs out of blocks.
	 * 2) It's execution time runs out.
	 * @param timeSliceMs How many milliseconds the code has to place blocks. Note it won't be 100% accurate, as blocks are batch processed.
	 * @return true if it ended due to lack of blocks, false if it ended due to lack of time.
	 */
	boolean processUntil(long timeSliceMs) {
		MineReset.sendDebugMsg("Starting block place in tick.");
		long timeNow = Instant.now().toEpochMilli();
		do {
			for (int i = 0;i<250;i++) {
				//Will process 250 blocks at a time.
				if (!blocksQueue.isEmpty()) {
					blocksQueue.pop().setBlock();
				}
				else {
					return true;
				}
			}
		}
		while (timeNow + timeSliceMs > Instant.now().toEpochMilli() && !blocksQueue.isEmpty());
		MineReset.sendDebugMsg("Stopped placing blocks in tick.");
		return blocksQueue.isEmpty();
	}
	
}
