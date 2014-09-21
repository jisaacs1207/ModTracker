package io.github.jisaacs1207.modtracker;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;


public final class ModTracker extends JavaPlugin implements Listener{
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		getConfig();
		getLogger().info("Tracking mods (shady crackheads).");
	}
 
	@Override
	public void onDisable() {
		getLogger().info("Stopping mod tracking.");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player player = (Player)sender;
		if ((player.hasPermission("mt.admin"))||(player.isOp())){
			if (args.length == 0) {
				if (commandLabel.equalsIgnoreCase("mt")){
					player.sendMessage("Anita pestered me.");
				}
			}
			if (args.length == 1) {
				if (commandLabel.equalsIgnoreCase("mt")){
					String name = args[0].toUpperCase();
					long currentTime = (int) Calendar.getInstance().getTimeInMillis();
					long lastTime = this.getConfig().getLong(name+".lastTime");
					int totalTime =  (int) (currentTime-lastTime);
					int addedTime = this.getConfig().getInt(name+".totalTime")+totalTime;
					int gmNum = this.getConfig().getInt(name+".GMs");
					if(gmNum<2){
						player.sendMessage("Sorry, there is not enough data on " + args[0] + ".");
					}
					player.sendMessage("They've entered GM a total of " + gmNum + " times.");
					if(totalTime/1000<60){
						player.sendMessage("Their GM time this session is/was less than a minute.");
					}
					else{
						player.sendMessage("Their GM time this session is/was " + TimeUnit.MILLISECONDS.toMinutes(totalTime) + " min.");
					}
					if(addedTime/1000<60){
						player.sendMessage("Their total GM time is less than a minute.");
					}
					else{
						player.sendMessage("Their total GM time is " + TimeUnit.MILLISECONDS.toMinutes(addedTime) + " min.");
					}
					if((addedTime/gmNum)/1000<60){
						player.sendMessage("Their average GM time is less than a minute.");
					}
					else{
						player.sendMessage("Their average GM time is " + TimeUnit.MILLISECONDS.toMinutes(addedTime/gmNum) + " min.");
					}	
				}
				
			}
		}
		else{
			player.sendMessage("Huh?");
		}
		return false;
	}
	
	@EventHandler(priority=EventPriority.LOW)
	public void onPlayerGM(PlayerGameModeChangeEvent event) {
		Player player = (Player)event.getPlayer();
		long currentTime = (int) Calendar.getInstance().getTimeInMillis();
		String name = player.getName().toUpperCase();
		String gameMode = event.getNewGameMode().toString().toUpperCase();
		if((player.hasPermission("mt.track"))&&(!player.isOp())){
			if(this.getConfig().getInt(name+ ".GMs")>1){
				if(gameMode.equals("CREATIVE")){
					player.getServer().broadcastMessage(ChatColor.BLUE + "["+ ChatColor.GOLD + "STAFF" +ChatColor.BLUE +  "] " + ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.DARK_AQUA +" has entered " + ChatColor.GREEN +"CREATIVE"+ChatColor.DARK_AQUA+ "!");
					int gmNum = this.getConfig().getInt(name+".GMs");
					this.getConfig().set(name+".GMs", gmNum+1);
					this.getConfig().set(name+".lastTime", currentTime);
					saveConfig();	
				}
				else{
					player.getServer().broadcastMessage(ChatColor.BLUE + "["+ ChatColor.GOLD + "STAFF" +ChatColor.BLUE +  "] " + ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.DARK_AQUA +" has entered " + ChatColor.RED +"SURVIVAL"+ChatColor.DARK_AQUA+ "!");
					long lastTime = this.getConfig().getLong(name+".lastTime");
					int totalTime =  (int) (currentTime-lastTime);
					int addedTime = this.getConfig().getInt(name+".totalTime")+totalTime;
					int gmNum = this.getConfig().getInt(name+".GMs");
					player.sendMessage("You've entered GM a total of " + gmNum + " times.");
					if(totalTime/1000<60){
						player.sendMessage("Your GM time this session was less than a minute.");
					}
					else{
						player.sendMessage("Your GM time this session was " + TimeUnit.MILLISECONDS.toMinutes(totalTime) + " min.");
					}
					if(addedTime/1000<60){
						player.sendMessage("Your total GM time is less than a minute.");
					}
					else{
						player.sendMessage("Your total GM time is " + TimeUnit.MILLISECONDS.toMinutes(addedTime) + " min.");
					}
					if((addedTime/gmNum)/1000<60){
						player.sendMessage("Your average GM time is less than a minute.");
					}
					else{
						player.sendMessage("Your average GM time is " + TimeUnit.MILLISECONDS.toMinutes(addedTime/gmNum) + " min.");
					}
					this.getConfig().set(name + ".totalTime", addedTime);
					saveConfig();	
				}
			}
			else{
				this.getConfig().set(name+".GMs", this.getConfig().getInt(name+".GMs")+1);
			}
		}
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = (Player)event.getPlayer();
		if((player.hasPermission("mt.track"))&&(!player.isOp())){
			if(player.getGameMode().toString().toUpperCase().equalsIgnoreCase("CREATIVE")){
				player.setGameMode(GameMode.SURVIVAL);
			}
		}
	}
}
