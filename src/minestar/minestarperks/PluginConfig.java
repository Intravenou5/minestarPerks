package minestar.minestarperks;

import java.io.File;
import java.util.ArrayList;

public class PluginConfig {
	public Main plugin;
	public PluginConfig(Main instance) {
		plugin = instance;
	}
	
	// LOAD or MAKE CONFIG FILE AND COOLDOWN FILE
	public boolean setup() {
		// config file
		String cop = "setting up plugin."; // current operation (error desc for less tech savy admins to see after stackTrace)
		try {
			// check for plugin folder - make if missing
			if(!plugin.getDataFolder().exists()){
				cop = "making plugin directory."; 
				plugin.getDataFolder().mkdirs();	
			}
			// set configFile
			plugin.configFile = new File(plugin.getDataFolder(), "config.yml");

			// check for configFile - make default if missing
			if(!plugin.configFile.exists()){
				plugin.log.info("Creating new config.yml...");
				cop = "making default config file.";
				createDefaultConfig();
			} 
			
			// load all perkTypes from config.yml keys
			cop = "loading in perkTypes.";
			for(String s : plugin.getConfig().getKeys(false)){
				plugin.perkTypes.add(s);
			}
		
			// if players folder doesnt exist, make one
			cop = "making players folder.";
			File playersDir = new File(plugin.getDataFolder() + "/players");
			if(!playersDir.exists()){
				playersDir.mkdirs();
			}
			
		} catch (Exception e){
			e.printStackTrace();
			plugin.log.warning("Issue while: " + cop);
			return false;
		}
		
		return true;
	}
	
	// CREATE A DEFAULT CONFIG FILE FOR PLUGIN
	private void createDefaultConfig(){
		// build a default file to give Admin some idea (example) of format
		
		ArrayList<String> defaultPerksForAdmin = new ArrayList<String>();
		ArrayList<String> defaultPerksForMod = new ArrayList<String>();
		ArrayList<String> defaultPerksForDonator = new ArrayList<String>();
		ArrayList<String> defaultPerks = new ArrayList<String>();
		ArrayList<String> defaultToolsForDonator = new ArrayList<String>();
		
		// mcItemName || displayName || nameColor || amountToGive || cooldownInMinutes || description/meta
		defaultPerksForAdmin.add("COOKIE||Ultimate Cookie||AQUA||1||0||What a yummy cookie!");
		defaultPerksForAdmin.add("SPECKLED_MELON||Godly Melon||AQUA||1||0||Dripping with godly juices."); // no cd
		defaultPerksForMod.add("MELON||Melon of the Mods||YELLOW||1||0||Nom nom nom melon!"); // no cd
		defaultPerksForDonator.add("COOKED_BEEF||Legendary Steak||BLUE||16||1440||Smells so good, omg!");
		defaultPerksForDonator.add("GOLDEN_APPLE||Complimentary Apple||GOLD||1||1440||So shiny!"); // every 24 hr
		defaultPerks.add("BREAD||Stale Bread||GRAY||8||300||Better than nothing.");	// every 5 hr
		defaultToolsForDonator.add("IRON_PICKAXE||Donator's Pickaxe||AQUA||1||1440||Heigh ho, heigh ho...");
		defaultToolsForDonator.add("IRON_AXE||Donator's Axe||AQUA||1||1440||Where's the wood at?"); // eve
		
		
		plugin.getConfig().createSection("food");
		plugin.getConfig().set("food.admin", defaultPerksForAdmin);
		plugin.getConfig().set("food.mod", defaultPerksForMod);
		plugin.getConfig().set("food.donator", defaultPerksForDonator);
		plugin.getConfig().set("food.default", defaultPerks);
		
		plugin.getConfig().createSection("tools");
		plugin.getConfig().set("tools.donator", defaultToolsForDonator);
		
		plugin.saveConfig();
	}
	
}
