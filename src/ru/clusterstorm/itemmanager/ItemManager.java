package ru.clusterstorm.itemmanager;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemManager extends JavaPlugin {

	private static ItemManager instance;
	private NMS nms;
	
	@Override
	public void onEnable() {
		instance = this;
		String ver = getVersion();
		try {
			nms = new NMS(ver);
		} catch (Exception e) {
			getLogger().severe("This plugin does not support version " + ver);
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		PluginCommand command = getCommand("itemmanager");
		if(command != null) {
			ItemCommand ic = new ItemCommand();
			command.setExecutor(ic);
			command.setTabCompleter(ic);
		}
	}
	
	private String getVersion() {
		String ver = getServer().getClass().getPackage().getName();
		return ver.substring(ver.lastIndexOf('.') + 1);
	}

	public static ItemManager getInstance() {
		return instance;
	}
	
	public static NMS getNMS() {
		return instance.nms;
	}
}
