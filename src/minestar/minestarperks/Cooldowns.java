package minestar.minestarperks;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.entity.Player;

public class Cooldowns {
	public Main plugin;
	public Cooldowns(Main instance) {
		plugin = instance;
	}
	
	
	// GET COOLDOWN FOR PLAYER
	public String getCooldownForPlayer(Player player, String perkType) {
		String answer = "";
		
		//
		String playerUUID = player.getUniqueId().toString();
		File playerCooldownFile = new File(plugin.getDataFolder() + "/players", playerUUID + ".txt");
		// does player file even exist?
		if(!playerCooldownFile.exists()){
			// no cooldown data for this player
			return "";
		}
		// it exists, load in
		ArrayList<String> playerCooldowns = getPlayerCooldowns(playerCooldownFile);
		
		for(String iline : playerCooldowns){
			String[] fullLine = iline.split("\\|\\|");
			String pType = fullLine[0];
			long timeset = Long.parseLong(fullLine[1]);
			long cdTime = Long.parseLong(fullLine[2]);
			
			if(pType.equalsIgnoreCase(perkType)){
				long timeElapsed = plugin.Functions.getTimeElapsed(timeset);
				// this will return how much time has elapsed since it was used in milliseconds
				long timeleft = cdTime - timeElapsed;
				if(timeElapsed < cdTime){
					int seconds = (int) (timeleft / 1000) % 60 ;
					int minutes = (int) ((timeleft / (1000*60)) % 60);
					int hours   = (int) ((timeleft / (1000*60*60)) % 24);
					answer = Integer.toString(hours) + ":" + Integer.toString(minutes) + ":" + Integer.toString(seconds) + "";
				}
				break;
			}
		}
		
		return answer;
	}
		

	
	// SET COOLDOWN FOR PLAYER
	public void setCooldownForPlayer(Player player, long totalmillis, String perkType){
		String playerUUID = player.getUniqueId().toString();
		File playerCooldownFile = new File(plugin.getDataFolder() + "/players", playerUUID + ".txt");
		// load in list of cooldown lines from player file
		ArrayList<String> playerCooldowns = getPlayerCooldowns(playerCooldownFile);
				
		// new cooldown line to add to players cooldown listing
		String newEntry = perkType + "||" + Long.toString(System.currentTimeMillis()) + "||" + Long.toString(totalmillis);
		
		// make players cooldown file
		try{
			FileWriter fw = new FileWriter(playerCooldownFile, false);
			BufferedWriter bw = new BufferedWriter(fw);
			// write the new entry	
			bw.append(newEntry);
			bw.newLine();
			// go through any other existing entries and add them unless they were of this type(old one)
			for(String s : playerCooldowns){
					// look for specific perk type in players cooldown listing
					if(s.contains(perkType)){
						// write nothing
					} else {
						// diff type, transfer line as is
						bw.append(s);
						bw.newLine();
					}
				}				
		    bw.close();
		    fw.close();
		} catch (IOException e) {
		    e.printStackTrace();
		    plugin.log.warning("Issue while: setting/writing to cooldown file for player.");
		}
		
	}
	
	private ArrayList<String> getPlayerCooldowns(File playerFile){
		ArrayList<String> playercds = new ArrayList<String>();
		if(!playerFile.exists()){
			// if the file doesnt exist yet
			return playercds;
		}
		
		try {
			FileReader in = new FileReader(playerFile);
		    BufferedReader br = new BufferedReader(in);
		    String line;
		    while ((line = br.readLine()) != null) {
		        playercds.add(line);
		    }
		    br.close();
		    in.close();
		    
		} catch (Exception e) {
			plugin.log.warning("Issue while: reading players cooldown file.");
			e.printStackTrace();
		}
		return playercds;
	}
	
}
