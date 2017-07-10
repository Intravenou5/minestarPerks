package minestar.minestarperks;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.permission.Permission;

public class Main extends JavaPlugin {
	public static Main plugin;
	public Logger log;
	public Permission permissions;
	
	public File configFile;					// plugin config file (yml)
	public File cdFile;						// cooldown file (txt)	
	
	
	public PluginConfig PluginConfig;		// plugin setup functions
	public Functions Functions;				// all general functions
	
	public ArrayList<String> cooldowns;		// hold list of cooldown times 
	public ArrayList<String> perkTypes;		// holds name of each perkType found in config.yml (ie: food)
	
	@Override
	public void onDisable(){
		plugin = null;
	}
	
	@Override
	public void onEnable() {
		log = Logger.getLogger("Minecraft");
		PluginConfig = new PluginConfig(this);
		Functions = new Functions(this);
		cooldowns = new ArrayList<String>();
		perkTypes = new ArrayList<String>();
		
		// plugin config, make/load
		if(!PluginConfig.setup()){
			log.severe("Unable to setup Config! Disabling plugin...");
			onDisable();
		}
		// permissions hook
		if(!setupPermissions()){
			log.warning("Unable to setup Permissions! Only Ops will have access.");
		}
		
		// command set
		getCommand("perk").setExecutor(new CmdExecutor(this));
		getCommand("glow").setExecutor(new CmdExecutor(this));
	}
			
	private boolean setupPermissions()
	{
		RegisteredServiceProvider<Permission> provider = this.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
		if(provider != null) { permissions = provider.getProvider(); }
		return (permissions != null);
	}
}
