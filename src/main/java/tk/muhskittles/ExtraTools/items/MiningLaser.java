package tk.muhskittles.ExtraTools.items;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.material.MaterialData;
import org.bukkit.util.BlockIterator;

import me.desht.sensibletoolbox.api.SensibleToolbox;
import me.desht.sensibletoolbox.api.energy.Chargeable;
import me.desht.sensibletoolbox.api.items.BaseSTBItem;
import me.desht.sensibletoolbox.api.util.BlockProtection;
import me.desht.sensibletoolbox.api.util.STBUtil;
import me.desht.sensibletoolbox.api.util.VanillaInventoryUtils;
import me.desht.sensibletoolbox.items.components.EnergizedIronIngot;
import me.desht.sensibletoolbox.items.components.IntegratedCircuit;
import me.desht.sensibletoolbox.items.energycells.FiftyKEnergyCell;

public class MiningLaser extends BaseSTBItem implements Chargeable {
	
	private enum Mode {
		SINGLE, EXPLOSIVE;
	}

	private static final MaterialData md = new MaterialData(Material.DIAMOND_AXE);
	private double charge;
	private Mode mode;
	private int dist = 10;
	private ItemStack pick = new ItemStack(Material.DIAMOND_PICKAXE);
	private double scuPerOp = 30.0;
	private double scuNeeded;
	
	public MiningLaser() {
		super();
		charge = 0;
		mode = Mode.SINGLE;
	}
	
	public MiningLaser(ConfigurationSection conf) {
        super(conf);
        mode = Mode.valueOf(conf.getString("mode"));
        charge = conf.getDouble("charge");
	}


	public String getItemName() {
		return "Mining Laser";
	}
	
	public String getDisplaySuffix() {
		switch (getMode()) {
		case SINGLE:
			return ChatColor.RED + "[Laser]";
		case EXPLOSIVE:
			return ChatColor.GOLD + "[Explosive]";
		default:
			return null;
		}
	}

	public String[] getLore() {
		switch (mode) {
		case SINGLE:
			return new String[]{
				"L-click: " + ChatColor.RESET + "Shoot Laser",
				"⇧ + Scroll: " + ChatColor.RESET + "Change Mode"
			};
		case EXPLOSIVE:
			return new String[]{
				"L-click: " + ChatColor.RESET + "Cause Explosion",
				"⇧ + Scroll: " + ChatColor.RESET + "Change Mode"
			};
		default:
			return new String[0];
		}
		
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

	public int getChargeRate() {
		return 1000;
	}

	public int getMaxCharge() {
		return 50000;
	}
	
	public Mode getMode() {
		return mode;
	}
	
	public void setMode(Mode mode) {
		this.mode = mode;
	}
	
	@Override
	public void onInteractItem(PlayerInteractEvent event) {
		
		if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
			
			switch (getMode()) {
			case SINGLE:
				handleSingleLaser(event);
			case EXPLOSIVE:
				handleExplosionEvent(event);
			default:
				break;
			}
			
		}
		
	}

	@SuppressWarnings("deprecation")
	private void handleExplosionEvent(PlayerInteractEvent event) {
		Player player2 = event.getPlayer();
		int sharpness2 = player2.getItemInHand().getEnchantmentLevel(Enchantment.DAMAGE_ALL);
		int efficiency2 = player2.getItemInHand().getEnchantmentLevel(Enchantment.DIG_SPEED);
		dist = (int) (dist * Math.pow(1.2, sharpness2));
		Block target = player2.getTargetBlock(null, dist);
		scuNeeded = 500 * Math.pow(0.87, efficiency2);
		
		if (scuNeeded <= getCharge()) {
			setCharge(getCharge() - scuNeeded);
			target.getWorld().createExplosion(target.getLocation(), (float) (4.0 * Math.pow(1.1, sharpness2)));
			player2.setItemInHand(toItemStack());
		}
		
		event.setCancelled(true);
	}

	private void handleSingleLaser(PlayerInteractEvent event) {
		
		Player player = event.getPlayer();
		Location loc = player.getEyeLocation();
		int sharpness = player.getItemInHand().getEnchantmentLevel(Enchantment.DAMAGE_ALL);
		int efficiency = player.getItemInHand().getEnchantmentLevel(Enchantment.DIG_SPEED);
		dist = (int) (dist * Math.pow(1.2, sharpness));
		BlockIterator laserSight = new BlockIterator(loc, 0.0, dist);
		Block nextBlock;
		
		while(laserSight.hasNext()) {
			nextBlock = laserSight.next();
			scuNeeded = scuPerOp * STBUtil.getMaterialHardness(nextBlock.getType()) * Math.pow(0.8, efficiency);
			
			if (canBreak(player, nextBlock) && scuNeeded <= getCharge()) {
				setCharge(getCharge() - scuNeeded);
				nextBlock.getWorld().playEffect(nextBlock.getLocation(), Effect.STEP_SOUND, nextBlock.getType());
				nextBlock.breakNaturally(pick);
				event.getPlayer().setItemInHand(toItemStack());
			}
			
		}
		
		event.setCancelled(true);
		
	}
	
    private boolean canBreak(Player player, Block b) {
        if (SensibleToolbox.getBlockAt(b.getLocation()) != null) {
            return false;
        } else if (VanillaInventoryUtils.isVanillaInventory(b)) {
            return false;
        } else if (STBUtil.getMaterialHardness(b.getType()) == Double.MAX_VALUE) {
        	return false;
        } else {
            return SensibleToolbox.getBlockProtection().playerCanBuild(player, b, BlockProtection.Operation.BREAK);
        }
    }

	public double getCharge() {
		return charge;
	}

	public void setCharge(double charge) {
		this.charge = charge;
	}
	
    @Override
    public YamlConfiguration freeze() {
        YamlConfiguration map = super.freeze();
        map.set("mode", mode.toString());
        map.set("charge", charge);
        return map;
    }
    
    public void onItemHeld(PlayerItemHeldEvent event) {
        int delta = event.getNewSlot() - event.getPreviousSlot();
        if (delta == 0) {
            return;
        } else if (delta >= 6) {
            delta -= 9;
        } else if (delta <= -6) {
            delta += 9;
        }
        delta = (delta > 0) ? 1 : -1;
        int o = getMode().ordinal() + delta;
        if (o < 0) {
            o = Mode.values().length - 1;
        } else if (o >= Mode.values().length) {
            o = 0;
        }
        setMode(Mode.values()[o]);
        event.getPlayer().setItemInHand(toItemStack());
    }
}