package minestar.minestarperks;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import net.md_5.bungee.api.ChatColor;

public class Functions {
	public Main plugin;
	public Functions(Main instance) {
		plugin = instance;
	}
	
	// RETURN A CREATED ITEM
	public ItemStack createItem(Player player, String iMat, String displayName, ArrayList<String> lore, String iDisplayColor, int amount) {
		// ItemStack(Material type, int amount)
		// An item stack with no extra data
		ChatColor displayColor = ChatColor.valueOf(iDisplayColor.toUpperCase()); 
		Material mat = Material.getMaterial(iMat);
		if(mat instanceof Material){
			// if valid material is passed in, make it
			ItemStack newItem = new ItemStack(mat, amount);
			ItemMeta newMeta = newItem.getItemMeta();

			if(displayName.length() > 0){
				displayName = displayName.replaceAll("_", " ");
				newMeta.setDisplayName(displayColor + displayName);	
			}
		
			if(lore.size() > 0){
				newMeta.setLore(lore);	
			}
			newItem.setItemMeta(newMeta);
			
			return newItem;
		} else {
			player.sendMessage("Cannot create item. Unknown material used.");
		}
		return null;
	}
	
	// GLOW TOGGLE ON/OFF FOR A PLAYER
	public void togglePlayerGlow(Player player) {	
		if(player.getPotionEffect(PotionEffectType.GLOWING) != null){
			player.removePotionEffect(PotionEffectType.GLOWING);
			player.sendMessage("The glow around you has faded.");	
		} 
		else 
		{
			if(player.isGlowing()){
				// remove any actual glow since it may override a potioneffect glow
				player.setGlowing(false);
			}
			PotionEffectType etype = PotionEffectType.GLOWING;
			int duration = Integer.MAX_VALUE;
			int amplifier = 1;
			boolean ambient = false;
			boolean particles = false;
			//Color color = Color.fromBGR(55, 175, 212); // gold
			Color color = Color.YELLOW;
			PotionEffect pe = new PotionEffect(etype, duration, amplifier, ambient, particles, color);
			player.addPotionEffect(pe);
			player.sendMessage("A glow surrounds you.");
		}		
	}
	
	
	
	// GET ELAPSED (SYSTEM)TIME SINCE
	public long getTimeElapsed(long dt){
		long now = System.currentTimeMillis();
		return now - dt;
	}
	
	
	
	// GIVE PLAYER PERK BASED ON RANK/PRIV
	public void givePerkByRank(Player player, String rank, String perkType){
		List<String> perks = plugin.getConfig().getStringList(perkType + "." + rank);
				
		if(perks.size() > 0 && perks != null){
			// do they have room for incoming items?
			int space = 0;
			for(ItemStack content : player.getInventory().getContents()) {
			    // TODO: check if material-type is same as perk material-type, try to stack if possible/has-room
				if(content == null) {
			        space++;
			    }
			}							 
			if(space < perks.size()) {
				// TODO: maybe we want this to drop beside them instead of full denial???
				player.sendMessage("You must free up some inventory space first.");
				return;
			}	
			
			// break up the perk lines from the config, give each perk to the player
			boolean perkadded = false;
			long itemCooldown = 10000;
			
			String cop = "giving perk to group: " + rank; 
			for(String p : perks){
				try {
					String[] itemdata = p.split("\\|\\|");
					String itemMCName = itemdata[0];
					String itemName = itemdata[1];
					String itemColor = itemdata[2];
					
					cop = "parsing perk itemAmount value.";
					int itemAmount = Integer.parseInt(itemdata[3]);	
					
					cop = "parsing perk itemCooldown value.";
					itemCooldown = Long.parseLong(itemdata[4]) * 60 * 1000;
					
					cop = "adding perk itemMeta.";
					String itemMeta = itemdata[5];
					ArrayList<String> lore = new ArrayList<String>();
					lore.add(itemMeta);
					
					cop = "adding item to player inventory.";
					player.getInventory().addItem(plugin.Functions.createItem(player, itemMCName, itemName, lore, itemColor, itemAmount));
					perkadded = true;

				} catch (Exception e){
					e.printStackTrace();
					plugin.log.warning("Issue while: " + cop);
				}								
			}
			if(perkadded){
				// if perk was given, add to cooldown list
				plugin.Cooldowns.setCooldownForPlayer(player, itemCooldown, perkType);
			}
		}
	}
	
	public boolean doesPerkExist(String perkType){
		for(String p : plugin.perkTypes){
			if(p.equalsIgnoreCase(perkType)){
				return true;
			}
		}
		return false;
	}
	
	public ArrayList<String> getPerkGroups(String perkType){
		ArrayList<String> allGroups = new ArrayList<String>();
		for(String s : plugin.getConfig().getConfigurationSection(perkType).getKeys(false)){
			allGroups.add(s);	
		}
		return allGroups;
	}
	
	public boolean doesPlayerHavePerkPermission(Player player, String perkType){
		ArrayList<String> perkGroups = getPerkGroups(perkType);
		for(String p : perkGroups){
			String perm = "msperk." + perkType + "." + p;
			if(player.hasPermission(perm)){
				return true;
			}
		}
		return false;
	}
	
	public String getPlayersPerkGroup(Player player, String perkType){
		// return the group player belongs to for this perk type (if they have priv)
		String result = "";
		ArrayList<String> perkGroups = getPerkGroups(perkType);
		for(String p : perkGroups){
			String perm = "msperk." + perkType + "." + p;
			if(player.hasPermission(perm)){
				return p;
			}
		}
		return result;
	}
	

}
