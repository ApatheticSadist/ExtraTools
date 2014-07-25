package tk.muhskittles.ExtraTools;

import me.desht.sensibletoolbox.core.STBItemRegistry;

import org.bukkit.plugin.java.JavaPlugin;

import tk.muhskittles.ExtraTools.items.MiningLaser;

public class ExtraTools extends JavaPlugin {
	
	private STBItemRegistry itemRegistry;

	@Override
	public void onEnable() {
		itemRegistry = new STBItemRegistry();
		registerItems();
	}
	
	@Override
	public void onDisable() {
		//huehuehue
	}
	
	public void registerItems() {
		itemRegistry.registerItem(new MiningLaser(), this);
	}
	
}
