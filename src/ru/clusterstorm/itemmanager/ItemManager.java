package ru.clusterstorm.itemmanager;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemManager extends JavaPlugin {

	private static ItemManager instance;
	
	@Override
	public void onEnable() {
		instance = this;
		
		PluginCommand command = getCommand("itemmanager");
		if(command != null) {
			ItemCommand ic = new ItemCommand();
			command.setExecutor(ic);
			command.setTabCompleter(ic);
		}
	}

	public static ItemManager getInstance() {
		return instance;
	}
}
