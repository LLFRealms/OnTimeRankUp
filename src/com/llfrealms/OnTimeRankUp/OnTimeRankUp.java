package com.llfrealms.OnTimeRankUp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.llfrealms.util.Utilities;
import com.llfrealms.OnTimeRankUp.Commands;


public final class OnTimeRankUp extends JavaPlugin
{
	public CommandSender consoleMessage = Bukkit.getConsoleSender(); //for sending things to or as the console
	
	public static Economy economy = null; //for Vault
	public static Permission permission = null; //for Vault
	
	PluginDescriptionFile descFile = this.getDescription(); //grabs info from plugin.yml
	public String pluginname = descFile.getPrefix(); //name of the plugin, uses prefix due to length of actual name
	public String logPrefix = "&f[&5"+pluginname+"&f]&e"; //prefix for custom log
	
	public String permSetUp = this.getConfig().getString("permSetUp"), permOption = this.getConfig().getString("permSetUp"); //
	public boolean prestige = false, presMon = false, presMonChange = false; //checks to see if using prestiges, money for prestiges, and if the money is dynamic or static
	public Map<String, String> presLvlsComb = new HashMap<String, String>(); //holds the list of prestiges and their lvl marker i.e. (lvl1, e)
	public List<String> ranks = new ArrayList<String>(), presLvlLevels = new ArrayList<String>(); //holds the list of ranks, list of the prestige lvls, i.e. lvl1, lvl2, lvl3, etc
	public double presMonValue = 0; //holds the money value for static value prestige
	public Map<String, Double> presMonDyn = new HashMap<String, Double>(); //holds money value for dynamic  value prestige
	
	public void onEnable()
	{
		sendLog("OnTimeRankUp is attempting to load");
		this.saveDefaultConfig();
    	this.getConfig();
    	sendLog("Loading commands");
    	getCommand("rankup").setExecutor(new Commands(this));
    	ranks = this.getConfig().getStringList("ranks");
    	prestige = this.getConfig().getBoolean("prestige");
		setupEconomy(); //vault
		setupPermissions(); //vault
		PresSetUp(); //setup prestige variables
		sendLog("OnTimeRankUp sucessfully loaded!");
		
	}
	public void onDisable()
	{
		
	}
	private void PresSetUp()
	{
		if(prestige)
		{
			int i = 1;
			Set<String> lvl = this.getConfig().getConfigurationSection("prestigeLevels").getKeys(false);
			List<String> pres = new ArrayList<String>();
			sendLog("Attempting to gather Prestige Levels");
			while(this.getConfig().getString("prestigeLevels.lvl"+i) != null)
			{
				String temp = this.getConfig().getString("prestigeLevels.lvl"+i);
				pres.add((i-1),temp);
				i++;
			}
			i = 0;
			for(String s: lvl)
			{
				presLvlLevels.add(pres.get(i));
				presLvlsComb.put(s, pres.get(i));
				i++;
			}
			if(presLvlsComb.isEmpty())
			{
				sendLog("Prestige Levels not loaded. Sad Face.");
			}
			else
			{
				sendLog("Prestige Levels sucessfully loaded!");
			}
			presMon = this.getConfig().getBoolean("prestigeMoney");
			if(presMon)
			{
				presMonChange = this.getConfig().getBoolean("presMonChange");
				if(presMonChange)
				{
					sendLog("Dynamic prestige money currently not supported!");
				}
			}
		}
		if(presMon)
		{
			sendLog("Attempting to load prestige money requirements.");
			if(presMonChange)
			{
				int j = 1;
				for(int i=0; i < presLvlsComb.size(); i++)
				{
					String path = "presMonDyn.lvl"+j;
					presMonDyn.put("lvl"+j, this.getConfig().getDouble(path));
					j++;
				}				
			}
			else
			{
				sendLog("Setting constant money req for prestige");
				presMonValue = this.getConfig().getDouble("presMonConst");
			}
			if(presMonDyn.isEmpty() && presMonChange)
			{
				sendLog("Prestige money requirements not loaded. Sad Face.");
			}
			else if(!presMonDyn.isEmpty() && presMonChange)
			{
				sendLog("Prestige money requirements sucessfully loaded!");
			}
			if(presMonValue > 0 && !presMonChange)
			{
				sendLog("Prestige money requirements sucessfully loaded!");
			}
		}
	}
	private boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }
	private boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }
    public boolean sendLog(String message)
    {
    	ConsoleCommandSender p = Bukkit.getConsoleSender();
        if (message ==null || message.isEmpty()) return true;
        p.sendMessage(Utilities.colorChat(logPrefix+message));
        return true;
    }
}
