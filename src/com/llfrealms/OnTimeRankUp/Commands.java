package com.llfrealms.OnTimeRankUp;

import org.bukkit.Bukkit;
import org.bukkit.command.*;

import me.edge209.OnTime.OnTimeAPI;

import com.llfrealms.OnTimeRankUp.OnTimeRankUp;
import com.llfrealms.util.Utilities;

public class Commands implements CommandExecutor 
{
	private OnTimeRankUp plugin;
	public Commands(OnTimeRankUp plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) 
	{
		if (cmd.getName().equalsIgnoreCase("rankup"))
		{

			String perm = permFind(sender), player = sender.toString();
			player = player.replaceAll("CraftPlayer\\{name=", "");
		    player = player.replaceAll("\\}", "");
			if(args.length > 0)
			{
				rankCheck(perm, player, sender, args[0]);
				return true;
			}
			else
			{
				rankCheck(perm, player, sender, "");
				return true;
			}
			
		}
		
		return false;
	}
	public String permFind(CommandSender sender)
	{
		String perm = "";
		if(plugin.prestige)
		{
			
			for(int i = plugin.presLvlLevels.size()-1; i >= 0; i--)
			{
				for(int j = plugin.ranks.size()-1; j >= 0; j--)
				{
					switch(plugin.permOption)//change this to get highest first
					{
						case "rp":
							perm = plugin.ranks.get(j) + "." + plugin.presLvlLevels.get(i);
							break;
						case "pr":
							perm = plugin.presLvlLevels.get(i) + "." + plugin.ranks.get(j);
							break;
					}
					if(OnTimeRankUp.permission.has(sender, perm))
					{
						return perm;
					}
				}
			}
		}
		else
		{
			for(int i = plugin.ranks.size()-1; i >= 0; i--)
			{
				perm = plugin.ranks.get(i);
				if(OnTimeRankUp.permission.has(sender, perm))
				{
					return perm;
				}
			}
		}
		return perm;
	}
	public String nextRank(String rank)
	{
		for(int i = plugin.ranks.size()-1; i >= 0; i--)
		{
			if(plugin.ranks.get(i).equalsIgnoreCase(rank))
			{
				if(rank.equalsIgnoreCase(plugin.ranks.get(plugin.ranks.size()-1)))
				{
					rank = plugin.ranks.get(0);
				}
				else
				{
					rank = plugin.ranks.get(i+1);
				}
				break;
			}
		}
		return rank;
	}
	public String timeConvert (Double time)//convert time from miliseconds to days, hours, and minutes
	{
		int days, hours, minutes, seconds;
		time /= 1000;
		days = (int) (time/86400);
		time -= (days*86400);
		hours = (int) (time/3600);
		time -= (hours*3600);
		minutes = (int)(time/60);
		time -= (minutes*60);
		seconds = (int)(time/60);
		String totalTime = days + " Days " + hours + " Hours " + minutes + " Minutes and " + seconds + " Seconds.";
		return totalTime;
	}
	public void rankCheck(String perm, String player, CommandSender sender, String accept)
	{
		double money, pmoney;
		double time, ptime;
		boolean moneyCheck = false, timeCheck = false;
		if(!plugin.permOption.equalsIgnoreCase("r")) //if using prestige the value will be rp or pr
		{
			String[] perms = perm.split("\\.");
			String rank, level = "You dun fucked up";
			if(plugin.permOption.equalsIgnoreCase("rp"))
			{
				level = Utilities.getKeyByValue(plugin.presLvlsComb, perms[1]);
				rank = nextRank(perms[0]);
				money = plugin.getConfig().getDouble("rankReq." + rank + "."+level+".money");
				time = plugin.getConfig().getDouble("rankReq." + rank + "."+level+".time");
				time *= 60000;
			}
			else
			{
				level = Utilities.getKeyByValue(plugin.presLvlsComb, perms[0]);
				rank = nextRank(perms[1]);
				if(rank.equalsIgnoreCase(plugin.ranks.get(0)))
				{
					money = plugin.presMonValue;
					time = 0;
				}
				else
				{
					money = plugin.getConfig().getDouble("rankReq." + rank + "."+level+".money");
					time = plugin.getConfig().getDouble("rankReq." + rank + "."+level+".time");
				}
				time *= 60000;
			}
			ptime = OnTimeAPI.getPlayerTimeData(player, OnTimeAPI.data.TOTALPLAY);
			pmoney = OnTimeRankUp.economy.getBalance(player);
			if(pmoney >= money)
			{
				moneyCheck=true;
			}
			if(ptime >= time)
			{
				timeCheck=true;
			}
			if(timeCheck && moneyCheck)
			{
				
				if(accept.equalsIgnoreCase("accept"))
				{
					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "promote " + player + " default");
					OnTimeRankUp.economy.withdrawPlayer(player, money);
					Utilities.sendMessage(sender, "&aCongratulations!");
				}
				else
				{
					Utilities.sendMessage(sender, "&aDo you want to rank up costing you $" + money + "?");
					Utilities.sendMessage(sender, "&aType &6/rankup accept &ato accept, otherwise do nothing.");
				}
			}
			else
			{
				if(OnTimeRankUp.permission.has(sender, "rankReq." + rank + "."+level+".skipperm"))
				{
					Utilities.sendMessage(sender, "&9You have the rank requirement skip for this level!");
					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "promote " + player + " default");
					OnTimeRankUp.economy.withdrawPlayer(player, money);
					Utilities.sendMessage(sender, "&aCongratulations!");
				}
				else
				{
					if(!moneyCheck)
					{
						Utilities.sendMessage(sender, "&aYou have $" + pmoney);
						Utilities.sendMessage(sender, "&6You need $" + money);
					}
					 if(!timeCheck)
					{
						Utilities.sendMessage(sender, "&aYou have " + timeConvert(ptime));
						Utilities.sendMessage(sender, "&6You need " + timeConvert(time));
					}
				}
			}
		}
		else //if not using prestige the value will be r (just the rank)
		{
			for(String s: plugin.ranks)
			{
				if(s.equalsIgnoreCase(perm))
				{
					ptime = OnTimeAPI.getPlayerTimeData(player, OnTimeAPI.data.TOTALPLAY);
					pmoney = OnTimeRankUp.economy.getBalance(player);
					money  = plugin.getConfig().getDouble("rankReq." + perm + ".money");
					time = plugin.getConfig().getDouble("rankReq." + perm + ".time");
					time *= 60000;
					if(pmoney >= money)
					{
						moneyCheck=true;
					}
					if(ptime >= time)
					{
						timeCheck=true;
					}
					if(timeCheck && moneyCheck)
					{
						if(accept.equalsIgnoreCase("accept"))
						{
							Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "promote " + player + " default");
							OnTimeRankUp.economy.withdrawPlayer(player, money);
							Utilities.sendMessage(sender, "&aCongratulations!");
						}
						else
						{
							Utilities.sendMessage(sender, "&aDo you want to rank up costing you $" + money + "?");
							Utilities.sendMessage(sender, "&aType &6/rankup accept &ato accept, otherwise do nothing.");
						}
					}
					else
					{
						if(!moneyCheck)
						{
							Utilities.sendMessage(sender, "&aYou have $" + pmoney);
							Utilities.sendMessage(sender, "&6You need $" + money);
						}
						if(!timeCheck)
						{
							Utilities.sendMessage(sender, "&aYou have " + timeConvert(ptime));
							Utilities.sendMessage(sender, "&6You need " + timeConvert(time));
						}
					}
				}
			}
		}
	}

}
