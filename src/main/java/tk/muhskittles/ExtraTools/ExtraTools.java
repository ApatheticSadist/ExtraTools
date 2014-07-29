package tk.muhskittles.ExtraTools;

import me.desht.sensibletoolbox.api.SensibleToolbox;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import tk.muhskittles.ExtraTools.items.MiningLaser;

public class ExtraTools extends JavaPlugin {

	@Override
	public void onEnable() {
		
		PluginManager pm = getServer().getPluginManager();
		Plugin stb = pm.getPlugin("SensibleToolbox");
		if (stb != null && stb.isEnabled()) {
			SensibleToolbox.getItemRegistry().registerItem(new MiningLaser(), this);
			getLogger().info("Item(s) have been registered.");
		}
	}
	
	@Override
	public void onDisable() {
		//huehuehue
	}
	
}
