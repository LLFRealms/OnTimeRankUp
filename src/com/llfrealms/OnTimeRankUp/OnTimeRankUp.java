package com.llfrealms.OnTimeRankUp;

import java.util.ArrayList;
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


public final class OnTimeRankUp extends JavaPlugin
{
	public CommandSender consoleMessage = Bukkit.getConsoleSender();
	
	public static Economy economy = null;
	public static Permission permission = null;
	
	PluginDescriptionFile descFile = this.getDescription(); 
	public String pluginname = descFile.getPrefix();
	public String logPrefix = "&f[&5"+pluginname+"&f]&e";
	
	public float presMonConst;
	public String permSetUp = this.getConfig().getString("permSetUp");
	private boolean prestige = false, presMon = false, presMonChange;
	public Map<String, String> presLvls;
	public List<String> ranks;
	public List<Float> rankMon;
	public Map<String, Float> presMonDyn;
	public Map<String, List<Float>> rankReqs, rankMoney;
	
	
	public void onEnable()
	{
		this.saveDefaultConfig();
    	this.getConfig();
    	ranks = this.getConfig().getStringList("ranks");
		setupEconomy();
		setupPermissions();
		ORTUSetUp();
		
	}
	public void onDisable()
	{
		
	}
	private void ORTUSetUp()
	{
		if(prestige)
		{
			int i = 1;
			Set<String> lvl = this.getConfig().getConfigurationSection("prestigeLevels").getKeys(false);
			List<String> pres = new ArrayList<String>();
			while(this.getConfig().getString("prestigeLevels."+i) != null)
			{	
				String temp = this.getConfig().getString("prestigeLevels."+i);
				pres.add(temp);
				i++;
			}
			i = 0;
			for(String s: lvl)
			{
				presLvls.put(s, pres.get(i));
				i++;
			}
			presMon = this.getConfig().getBoolean("prestigeMoney");
			if(presMon)
			{
				presMonChange = this.getConfig().getBoolean("presMonChange");
				if(presMonChange)
				{
					
				}
			}
		}
		if(presMon)
		{
			if(presMonChange)
			{
				
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
