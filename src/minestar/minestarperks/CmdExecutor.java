package minestar.minestarperks;

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
					String inc = args[0];
					// does inc exist as a perk?
					if(plugin.Functions.doesPerkExist(inc)){
						// exists as a perk
						// does this player have privs to use it?
						if(!plugin.Functions.doesPlayerHavePerkPermission(player, inc)){
							// does not have permission to use this perk
							player.sendMessage(ChatColor.YELLOW + "You do not have access to that type of perk." + ChatColor.RESET);
							return true;
						}
					} else {
						// is not a known perk
						player.sendMessage(ChatColor.YELLOW + "Unknown type of perk." + ChatColor.RESET);
						return true;
					}
					// make sure they're not on a cooldown
					if(plugin.Cooldowns.getCooldownForPlayer(player, inc) != ""){
						player.sendMessage(ChatColor.YELLOW + "Perk is on cooldown. Time remaining: " + plugin.Cooldowns.getCooldownForPlayer(player, inc) + ChatColor.RESET);
						return true;
					}		
					// give perk
					plugin.Functions.givePerkByRank(player, plugin.Functions.getPlayersPerkGroup(player, inc), inc);
					return true;	
				}
				
								
			}
			
		}
		return false;
		
	}
}
