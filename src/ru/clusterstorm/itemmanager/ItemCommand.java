package ru.clusterstorm.itemmanager;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemCommand implements CommandExecutor {

	public static final String PREFIX = "§2§lIM §3> §f";
	private static final String LORE = "[\"\",{\"text\":\"\u2718\",\"color\":\"red\",\"clickEvent\":"
			+ "{\"action\":\"run_command\",\"value\":\"/im lore remove {line}\"},\"hoverEvent\":{\""
			+ "action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"Delete\",\"colo"
			+ "r\":\"dark_gray\"}]}}},{\"text\":\" \",\"color\":\"none\"},{\"text\":\"\u270E\",\"colo"
			+ "r\":\"gold\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/im lore set "
			+ "{line} \"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\""
			+ ":[{\"text\":\"Edit\",\"color\":\"dark_gray\"}]}}},{\"text\":\" ({line}): \",\"color\":"
			+ "\"gray\"},{\"text\":\"{str}\",\"color\":\"none\"}]";
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("itemmanager.command")) {
			sender.sendMessage(PREFIX + "§cInsufficient permissions");
			return true;
		}
		if(!(sender instanceof Player)) return true;
		
		Player p = (Player) sender;
		
		if(args.length == 0)
		{
			p.sendMessage(PREFIX + "Set name - /" + label + " name");
			p.sendMessage(PREFIX + "Set lore - /" + label + " lore");
			return true;
		}
		
		if(args[0].equals("name")) {
			ItemStack item = p.getItemInHand();
			if(item == null || item.getType() == Material.AIR) {
				p.sendMessage(PREFIX + "§cYou have no item in hand");
				return true;
			}
			
			ItemMeta meta = item.getItemMeta();
			
			if(args.length < 2) {
				if(!meta.hasDisplayName()) {
					p.sendMessage(PREFIX + "§cNo display name");
					return true;
				}
				p.sendMessage(PREFIX + "Display name: " + meta.getDisplayName());
				return true;
			}
			
			String name = join(args, 1);
			meta.setDisplayName(name);
			item.setItemMeta(meta);
			p.updateInventory();
			p.sendMessage("Set display name: " + name);
			return true;
		}
		
		if(args[0].equalsIgnoreCase("lore")) {
			ItemStack item = p.getItemInHand();
			if(item == null || item.getType() == Material.AIR) {
				p.sendMessage(PREFIX + "§cYou have no item in hand");
				return true;
			}
			
			ItemMeta meta = item.getItemMeta();
			
			if(args.length < 2) {
				p.sendMessage(PREFIX + "Add line - /" + label + " lore add <text...>");
				p.sendMessage(PREFIX + "Set line - /" + label + " lore set <index> <text...>");
				p.sendMessage(PREFIX + "Remove line - /" + label + " lore remove <index>");
				sendLore(p, meta);
				return true;
			}
			
			if(args[1].equalsIgnoreCase("add")) {
				String line = join(args, 2);
				List<String> lore = meta.getLore();
				if(lore == null) lore = new ArrayList<String>();
				lore.add(line);
				meta.setLore(lore);
				item.setItemMeta(meta);
				p.updateInventory();
				p.sendMessage(PREFIX + "Added line: " + (line.isEmpty() ? "§8<empty>" : line));
				return true;
			}
			
			if(args[1].equalsIgnoreCase("set")) {
				if(args.length < 3) {
					p.sendMessage(PREFIX + "§cUsage - /" + label + " lore set <index> <text...>");
					return true;
				}
				
				int line = parseLine(args[2]);
				if(line < 0) {
					p.sendMessage(PREFIX + "§cLine number must be an unsigned integer!");
					return true;
				}
				
				List<String> lore = meta.getLore();
				if(!checkLoreLine(p, lore, line)) return true;
				
				String text = join(args, 3);
				lore.set(line, text);
				meta.setLore(lore);
				item.setItemMeta(meta);
				p.updateInventory();
				sendLore(p, meta);
				return true;
			}
			
			if(args[1].equalsIgnoreCase("remove")) {
				if(args.length < 3) {
					p.sendMessage(PREFIX + "§cUsage - /" + label + " lore remove <index>");
					return true;
				}
				
				int line = parseLine(args[2]);
				if(line < 0) {
					p.sendMessage(PREFIX + "§cLine number must be an unsigned integer!");
					return true;
				}
				
				
				List<String> lore = meta.getLore();
				if(!checkLoreLine(p, lore, line)) return true;
				
				lore.remove(line);
				meta.setLore(lore);
				item.setItemMeta(meta);
				p.updateInventory();
				sendLore(p, meta);
				return true;
			}
			
			
			
			return true;
		}
		
		
		
		
		
		
		
		return true;
	}

	private int parseLine(String s) {
		try {
			return Integer.parseInt(s);
		} catch(NumberFormatException e) {
			return -1;
		}
	}

	private boolean checkLoreLine(Player p, List<String> lore, int line) {
		if(lore == null || lore.isEmpty()) {
			p.sendMessage(PREFIX + "§cYour item does not have lore");
			return false;
		}
		
		int size = lore.size();
		if(line >= size) {
			p.sendMessage(PREFIX + "§cItem's lore has " + size + " lines");
			return false;
		}
		return true;
	}

	private void sendLore(Player p, ItemMeta meta) {
		if(!meta.hasLore()) return;
		p.sendMessage("§8---------------");
		int i = 0;
		for (String s : meta.getLore()) {
			lore(p, s, String.valueOf(i++));
		}
	}

	private String join(String[] args, int start) {
		StringBuilder b = new StringBuilder();
		for (int i = start; i < args.length; i++) {
			b.append(args[i]);
			if(i < args.length - 1) b.append(' ');
		}
		return ChatColor.translateAlternateColorCodes('&', b.toString());
	}

	private void lore(Player p, String s, String line) {
		ItemManager.getNMS().sendJson(p, LORE.replace("{line}", line).replace("{str}", s));
	}

}
