package ru.clusterstorm.itemmanager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Lists;

public class ItemCommand implements CommandExecutor, TabCompleter {

	public static final String PREFIX = "§2§lIM §3> §f";
	private static final String LORE = "[\"\",{\"text\":\"\u2718\",\"color\":\"red\",\"clickEvent\":"
			+ "{\"action\":\"run_command\",\"value\":\"/im lore remove {line}\"},\"hoverEvent\":{\""
			+ "action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"Delete\",\"colo"
			+ "r\":\"dark_gray\"}]}}},{\"text\":\" \",\"color\":\"none\"},{\"text\":\"\u270E\",\"colo"
			+ "r\":\"gold\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/im lore set "
			+ "{line} {rawstr}\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\""
			+ ":[{\"text\":\"Edit\",\"color\":\"dark_gray\"}]}}},{\"text\":\" ({line}): \",\"color\":"
			+ "\"gray\"},{\"text\":\"{str}\",\"color\":\"white\"}]";

	private static final ArrayList<String> ENCHANTMENTS;

	private static final ArrayList<String> FLAGS;

	static {
		ENCHANTMENTS = new ArrayList<String>();
		for (Enchantment e : Enchantment.values()) {
			ENCHANTMENTS.add(e.getName().toLowerCase());
		}

		FLAGS = new ArrayList<String>();
		for (ItemFlag e : ItemFlag.values()) {
			FLAGS.add(e.name().toLowerCase());
		}
		FLAGS.add("unbreakable");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("itemmanager.command")) {
			sender.sendMessage(PREFIX + "§cInsufficient permissions");
			return true;
		}
		if (!(sender instanceof Player))
			return true;

		Player p = (Player) sender;

		if (args.length == 0) {
			p.sendMessage(PREFIX + "Set name - /" + label + " name");
			p.sendMessage(PREFIX + "Set lore - /" + label + " lore");
			p.sendMessage(PREFIX + "Enchantments - /" + label + " ench");
			p.sendMessage(PREFIX + "Item flags - /" + label + " flag");
			return true;
		}

		if (args[0].equalsIgnoreCase("flag")) {
			ItemStack item = p.getItemInHand();
			if (item == null || item.getType() == Material.AIR) {
				p.sendMessage(PREFIX + "§cYou have no item in hand");
				return true;
			}

			ItemMeta meta = item.getItemMeta();

			if (args.length == 2) {
				if (args[1].equalsIgnoreCase("unbreakable")) {
					p.sendMessage(PREFIX + getItemName(item) + "§7 unbreakable§f: " + meta.spigot().isUnbreakable());
				} else {
					ItemFlag f = getFlag(args[1]);
					if (f == null) {
						p.sendMessage(PREFIX + "§cUnknown flag: " + args[1]);
						return true;
					}
					p.sendMessage(PREFIX + getItemName(item) + ": §7" + f.name().toLowerCase() + "§f: "
							+ (meta.hasItemFlag(f) ? "true" : "false"));
				}
				return true;
			}

			if (args.length > 2) {
				boolean value;
				if (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("1"))
					value = true;
				else if (args[2].equalsIgnoreCase("false") || args[2].equalsIgnoreCase("0"))
					value = false;
				else {
					p.sendMessage(PREFIX + "§c" + args[2] + " is not valid value");
					return true;
				}

				if (args[1].equalsIgnoreCase("unbreakable")) {
					if (meta.spigot().isUnbreakable()) {
						if (value) {
							p.sendMessage(PREFIX + "§c" + getItemName(item) + "§c is already unbreakable");
							return true;
						}
					} else {
						if (!value) {
							p.sendMessage(PREFIX + "§c" + getItemName(item) + "§c is not unbreakable");
							return true;
						}
					}

					meta.spigot().setUnbreakable(value);
					if (value)
						p.sendMessage(PREFIX + "§aAdded flag §7unbreakable §ato §f" + getItemName(item));
					else
						p.sendMessage(PREFIX + "§aRemoved flag §7unbreakable §afrom §f" + getItemName(item));
				} else {
					ItemFlag f = getFlag(args[1]);
					if (f == null) {
						p.sendMessage(PREFIX + "§cUnknown flag: " + args[1]);
						return true;
					}

					if (value) {
						if (meta.hasItemFlag(f)) {
							p.sendMessage(PREFIX + "§c" + getItemName(item) + "§c already has " + f.name().toLowerCase());
							return true;
						}
						meta.addItemFlags(f);
						p.sendMessage(PREFIX + "§aAdded §7" + f.name().toLowerCase() + "§a to §f" + getItemName(item));
					} else {
						if (!meta.hasItemFlag(f)) {
							p.sendMessage(PREFIX + "§c" + getItemName(item) + "§c has not " + f.name().toLowerCase());
							return true;
						}
						meta.removeItemFlags(f);
						p.sendMessage(
								PREFIX + "§aRemoved §7" + f.name().toLowerCase() + "§a from §f" + getItemName(item));
					}
				}
				item.setItemMeta(meta);
				p.updateInventory();
				return true;
			}
			
			p.sendMessage(PREFIX + "§cUsage - /" + label + " flag <flag> <true/false>");
			
			Set<ItemFlag> flags = meta.getItemFlags();
			boolean unbreakable = meta.spigot().isUnbreakable();
			if (flags.isEmpty() && !unbreakable) {
				p.sendMessage(PREFIX + "§c" + getItemName(item) + "§c has no item flags");
				return true;
			}

			StringBuilder b = new StringBuilder();
			for (ItemFlag f : flags) {
				b.append(f.name().toLowerCase());
				b.append(", ");
			}

			String list = b.toString();
			if (unbreakable)
				list += "unbreakable";
			else if (!list.isEmpty())
				list = list.substring(0, list.length() - 2);

			p.sendMessage(PREFIX + "Flags of " + getItemName(item) + "§f: §7" + list);
			return true;
		}

		if (args[0].equalsIgnoreCase("ench")) {
			ItemStack item = p.getItemInHand();
			if (item == null || item.getType() == Material.AIR) {
				p.sendMessage(PREFIX + "§cYou have no item in hand");
				return true;
			}

			ItemMeta meta = item.getItemMeta();

			if (args.length == 1) {
				Map<Enchantment, Integer> ench = meta.getEnchants();
				p.sendMessage(PREFIX + "Enchantments of " + getItemName(item) + "§f: " + ench.size());
				for (Entry<Enchantment, Integer> e : ench.entrySet()) {
					p.sendMessage(e.getKey().getName().toLowerCase() + ": " + e.getValue());
				}
				return true;
			}

			Enchantment ench = Enchantment.getByName(args[1].toUpperCase());
			if (ench == null) {
				p.sendMessage(PREFIX + "§cUnknown enchantment: " + args[1]);
				return true;
			}

			if (args.length < 3) {
				p.sendMessage(PREFIX + getItemName(item) + "§7: " + ench.getName().toLowerCase() + "§f: "
						+ meta.getEnchantLevel(ench));
				return true;
			}

			int level = parseLine(args[2]);
			if (level <= 0) {
				meta.removeEnchant(ench);
				p.sendMessage(
						PREFIX + "§aRemoved §7" + ench.getName().toLowerCase() + "§a from§f " + getItemName(item));
			} else {
				meta.addEnchant(ench, level, true);
				p.sendMessage(PREFIX + "§aSet §7" + ench.getName().toLowerCase() + "§a level on§f " + getItemName(item)
						+ "§a: " + level);
			}
			item.setItemMeta(meta);
			p.updateInventory();
			return true;
		}

		if (args[0].equalsIgnoreCase("name")) {
			ItemStack item = p.getItemInHand();
			if (item == null || item.getType() == Material.AIR) {
				p.sendMessage(PREFIX + "§cYou have no item in hand");
				return true;
			}

			ItemMeta meta = item.getItemMeta();

			if (args.length < 2) {
				if (!meta.hasDisplayName()) {
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
			p.sendMessage(PREFIX + "Set display name: " + name);
			return true;
		}

		if (args[0].equalsIgnoreCase("lore")) {
			ItemStack item = p.getItemInHand();
			if (item == null || item.getType() == Material.AIR) {
				p.sendMessage(PREFIX + "§cYou have no item in hand");
				return true;
			}

			ItemMeta meta = item.getItemMeta();

			if (args.length < 2) {
				p.sendMessage(PREFIX + "Add line - /" + label + " lore add <text...>");
				p.sendMessage(PREFIX + "Set line - /" + label + " lore set <index> <text...>");
				p.sendMessage(PREFIX + "Remove line - /" + label + " lore remove <index>");
				sendLore(p, meta);
				return true;
			}

			if (args[1].equalsIgnoreCase("add")) {
				String line = join(args, 2);
				List<String> lore = meta.getLore();
				if (lore == null)
					lore = new ArrayList<String>();
				lore.add(line);
				meta.setLore(lore);
				item.setItemMeta(meta);
				p.updateInventory();
				p.sendMessage(PREFIX + "Added line: " + (line.isEmpty() ? "§8<empty>" : line));
				return true;
			}

			if (args[1].equalsIgnoreCase("set")) {
				if (args.length < 3) {
					p.sendMessage(PREFIX + "§cUsage - /" + label + " lore set <index> <text...>");
					return true;
				}

				int line = parseLine(args[2]);
				if (line < 0) {
					p.sendMessage(PREFIX + "§cLine number must be an unsigned integer!");
					return true;
				}

				List<String> lore = meta.getLore();
				if (!checkLoreLine(p, lore, line))
					return true;

				String text = join(args, 3);
				lore.set(line, text);
				meta.setLore(lore);
				item.setItemMeta(meta);
				p.updateInventory();
				sendLore(p, meta);
				return true;
			}

			if (args[1].equalsIgnoreCase("remove")) {
				if (args.length < 3) {
					p.sendMessage(PREFIX + "§cUsage - /" + label + " lore remove <index>");
					return true;
				}

				int line = parseLine(args[2]);
				if (line < 0) {
					p.sendMessage(PREFIX + "§cLine number must be an unsigned integer!");
					return true;
				}

				List<String> lore = meta.getLore();
				if (!checkLoreLine(p, lore, line))
					return true;

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

	private ItemFlag getFlag(String flag) {
		try {
			return ItemFlag.valueOf(flag.toUpperCase());
		} catch (Exception e) {
			return null;
		}
	}

	private int parseLine(String s) {
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	private boolean checkLoreLine(Player p, List<String> lore, int line) {
		if (lore == null || lore.isEmpty()) {
			p.sendMessage(PREFIX + "§cYour item does not have lore");
			return false;
		}

		int size = lore.size();
		if (line >= size) {
			p.sendMessage(PREFIX + "§cItem's lore has " + size + " lines");
			return false;
		}
		return true;
	}

	private void sendLore(Player p, ItemMeta meta) {
		if (!meta.hasLore())
			return;
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
			if (i < args.length - 1)
				b.append(' ');
		}
		return ChatColor.translateAlternateColorCodes('&', b.toString());
	}

	private void lore(Player p, String s, String line) {
		ItemManager.getNMS().sendJson(p, LORE
				.replace("{line}", line).replace("{str}", s).replace("{rawstr}", s.replace("§", "&")));
	}

	private static String getItemName(ItemStack item) {
		if (!item.hasItemMeta())
			return getMaterialName(item);
		ItemMeta meta = item.getItemMeta();
		if (!meta.hasDisplayName())
			return getMaterialName(item);
		return meta.getDisplayName();
	}

	private static String getMaterialName(ItemStack item) {
		String type = item.getType().toString().toLowerCase();
		type = Character.toUpperCase(type.charAt(0)) + type.substring(1);
		return type.replace("_", " ");
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("itemmanager.command"))
			return Lists.newArrayList();

		if (args.length == 1) {
			return filter(Lists.newArrayList("name", "lore", "ench", "flag"), args);
		}
		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("lore"))
				return filter(Lists.newArrayList("add", "set", "remove"), args);
			if (args[0].equalsIgnoreCase("ench"))
				return filter(ENCHANTMENTS, args);
			if (args[0].equalsIgnoreCase("flag"))
				return filter(FLAGS, args);
		}

		if (args.length == 3) {
			if (args[0].equalsIgnoreCase("flag"))
				return filter(Lists.newArrayList("true", "false"), args);
		}

		return Lists.newArrayList();
	}

	private List<String> filter(ArrayList<String> list, String[] args) {
		String last = args[args.length - 1].toLowerCase();
		List<String> result = new ArrayList<String>();
		for (String s : list)
			if (s.startsWith(last))
				result.add(s);
		return result;
	}

}
