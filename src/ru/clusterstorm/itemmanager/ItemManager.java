package ru.clusterstorm.itemmanager;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemManager extends JavaPlugin {

	private static ItemManager instance;
	private NMS nms;
	
	@Override
	public void onEnable() {
		instance = this;
		try {
			nms = new NMS(getVersion());
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		PluginCommand command = getCommand("itemmanager");
		if(command != null) command.setExecutor(new ItemCommand());
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
