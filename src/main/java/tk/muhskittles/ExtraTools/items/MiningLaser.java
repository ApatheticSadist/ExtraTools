package tk.muhskittles.ExtraTools.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.material.MaterialData;

import me.desht.sensibletoolbox.api.energy.Chargeable;
import me.desht.sensibletoolbox.api.items.BaseSTBItem;
import me.desht.sensibletoolbox.api.util.STBUtil;
import me.desht.sensibletoolbox.items.components.EnergizedIronIngot;
import me.desht.sensibletoolbox.items.components.IntegratedCircuit;
import me.desht.sensibletoolbox.items.energycells.FiftyKEnergyCell;

public class MiningLaser extends BaseSTBItem implements Chargeable {
	
	private static final MaterialData md = new MaterialData(Material.DIAMOND_AXE);
	private double charge;
	
	public MiningLaser() {
		super();
		charge = 0;
	}
	
	public MiningLaser(ConfigurationSection conf) {
		super();
		charge = 0;
	}


	public String getItemName() {
		return "Mining Laser";
	}

	public String[] getLore() {
		return new String[]{
				"L-click: " + ChatColor.RESET + "Shoot Laser",
				"R-click: " + ChatColor.RESET + "Change Mode"
		};
	}
	
	public String[] getExtraLore() {
		return new String[]{STBUtil.getChargeString(this)};
	}

	public MaterialData getMaterialData() {
		return md;
	}

	public Recipe getRecipe() {
		ShapedRecipe recipe = new ShapedRecipe(toItemStack());
		EnergizedIronIngot ei = new EnergizedIronIngot();
		IntegratedCircuit ic = new IntegratedCircuit();
		FiftyKEnergyCell ec = new FiftyKEnergyCell();
		ec.setCharge(0.0);
		registerCustomIngredients(ei, ic, ec);
		recipe.shape("GDB", "ICP", "GDB");
		recipe.setIngredient('G', ei.toItemStack().getData());
		recipe.setIngredient('D', Material.DIAMOND);
		recipe.setIngredient('B', Material.BLAZE_ROD);
		recipe.setIngredient('I', ic.toItemStack().getData());
		recipe.setIngredient('C', STBUtil.makeWildCardMaterialData(ec));
		recipe.setIngredient('P', Material.DIAMOND_PICKAXE);
		return recipe;
	}

	public double getCharge() {
		return charge;
	}

	public int getChargeRate() {
		return 1000;
	}

	public int getMaxCharge() {
		return 50000;
	}

	public void setCharge(double charge) {
		this.charge = charge;
	}

}
