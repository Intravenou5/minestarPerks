package minestar.minestarperks;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.ChatColor;

public class CmdExecutor implements CommandExecutor {
	
	public Main plugin;
	public CmdExecutor(Main instance) {
		plugin = instance;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(sender instanceof Player) {	
			Player player = (Player) sender;
			
			if(args.length == 0){

				// GLOW
				if(label.equalsIgnoreCase("glow")){
					if(player.hasPermission("msperk.glow")){
						plugin.Functions.togglePlayerGlow(player);
					}
					return true;	
				}
				
			}
			
			if(args.length == 1){
			
				// PERK <type>
				if(label.equalsIgnoreCase("perk")){
					String perkWanted = args[0];
					String perkGroup = "";
					String permString = "";
					String perk = "";
					boolean playerHasPermission = false;
					
					for(String perkType : plugin.perkTypes){
						if(perkType.equalsIgnoreCase(perkWanted)){
							// found perk, check permission, then cooldown, then give to player
							// 1. get all groups in this perkType
							List<String> allGroups = new ArrayList<String>();
							for(String s : plugin.getConfig().getConfigurationSection(perkType).getKeys(false)){
								allGroups.add(s);	
							}
							// 2. does player have a permission that matches any of these?		
							for(String g : allGroups){
								permString = "msperk." + perkType + "." + g;
								if(player.hasPermission(permString)){
									perk = perkType;
									perkGroup = g;
									playerHasPermission = true;
									break;
								}
							}
							
						}
						if(playerHasPermission){break;} // dont stay in for-loop if dont need
					}
															
					// make sure they're not on a cooldown
					// TODO: Switch cooldown to work by perkType not just one entry per player
					if(plugin.Cooldowns.getCooldownForPlayer(player, perkWanted) != ""){
						player.sendMessage(ChatColor.YELLOW + "Perk is on cooldown. Time remaining: " + plugin.Cooldowns.getCooldownForPlayer(player, perkWanted) + ChatColor.RESET);
						return true;
					}
					
					// give perk if playerHasPermission
					if(playerHasPermission && perkGroup != "" && perk != ""){
						plugin.Functions.givePerkByRank(player, perkGroup, perk);
						return true;	
					}
					
					player.sendMessage(ChatColor.YELLOW + "You do not have access to that type of perk." + ChatColor.RESET);
					return true;	
				}
				
								
			}
			
		}
		return false;
		
	}
}
