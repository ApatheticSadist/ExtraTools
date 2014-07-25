package tk.muhskittles.ExtraTools.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
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
	
	private static final MaterialData md = new MaterialData(Material.DIAMOND_HOE);
	private double charge;
	
	public MiningLaser() {
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
		EnergizedIronIngot ei = new EnergizedIronIngot();
		IntegratedCircuit ic = new IntegratedCircuit();
		FiftyKEnergyCell ec = new FiftyKEnergyCell();
		
		ShapedRecipe recipe = new ShapedRecipe(toItemStack());
		recipe.shape("GDB", "ICP", "GDB");
		recipe.setIngredient('G', ei.getMaterialData());
		recipe.setIngredient('D', Material.DIAMOND);
		recipe.setIngredient('B', Material.BLAZE_ROD);
		recipe.setIngredient('I', ic.getMaterialData());
		recipe.setIngredient('C', ec.getMaterialData());
		recipe.setIngredient('P', Material.DIAMOND_PICKAXE);
		return null;
	}

	public double getCharge() {
		return charge;
	}

	public int getChargeRate() {
		return 1000;
	}

	public int getMaxCharge() {
		// TODO Auto-generated method stub
		return 50000;
	}

	public void setCharge(double charge) {
		this.charge = charge;
	}

}
