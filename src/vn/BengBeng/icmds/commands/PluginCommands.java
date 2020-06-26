package vn.BengBeng.icmds.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import vn.BengBeng.icmds.configures.Message;
import vn.BengBeng.icmds.configures.Permission;
import vn.BengBeng.icmds.configures.Setting;
import vn.BengBeng.icmds.files.Config;
import vn.BengBeng.icmds.utils.Utils;

public class PluginCommands
	implements CommandExecutor {
	
	private String versionReg = Utils.getRegex("ver(sion)?"),
			labelReg = Utils.getRegex("label"),
			valueReg = Utils.getRegex("value"),
			amountReg = Utils.getRegex("amount"),
			itemReg = Utils.getRegex("item((\\-|\\_)?name)?"),
			playerReg = Utils.getRegex("player"),
			senderReg = Utils.getRegex("sender"),
			listReg = Utils.getRegex("list");
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		String sName = sender.getName();
		if((args.length == 0) || (Utils.regexMatches(args[0], "help \\?"))) {
			if(!Utils.getUser(sName).hasPermission(Permission.getPermission("Help"))) {
				Utils.sendMessage(sender, Message.getMessage("FAIL.no-permission"));
				return true;
			}
			return onHelp(sender, label, args);
		}
		
		String cmd = args[0];
		if(Utils.regexMatches(cmd, "give")) {
			if(!Utils.getUser(sName).hasPermission(Permission.getPermission("Give"))) {
				Utils.sendMessage(sender, Message.getMessage("FAIL.no-permission"));
				return true;
			}
			return onGive(sender, args);
		}
		if(Utils.regexMatches(cmd, "create")) {
			if(!Utils.getUser(sName).hasPermission(Permission.getPermission("Create"))) {
				Utils.sendMessage(sender, Message.getMessage("FAIL.no-permission"));
				return true;
			}
			return onCreate(sender, args);
		}
		if(Utils.regexMatches(cmd, "delete")) {
			if(!Utils.getUser(sName).hasPermission(Permission.getPermission("Delete"))) {
				Utils.sendMessage(sender, Message.getMessage("FAIL.no-permission"));
				return true;
			}
			return onDelete(sender, args);
		}
		if(Utils.regexMatches(cmd, "clear")) {
			if(!Utils.getUser(sName).hasPermission(Permission.getPermission("Clear"))) {
				Utils.sendMessage(sender, Message.getMessage("FAIL.no-permission"));
				return true;
			}
			return onClear(sender, args);
		}
		if(Utils.regexMatches(cmd, "list")) {
			if(!Utils.getUser(sName).hasPermission(Permission.getPermission("List"))) {
				Utils.sendMessage(sender, Message.getMessage("FAIL.no-permission"));
				return true;
			}
			return onList(sender, args);
		}
		if(Utils.regexMatches(cmd, "r(e)?l((oa)?d)?")) {
			if(!Utils.getUser(sName).hasPermission(Permission.getPermission("Reload"))) {
				Utils.sendMessage(sender, Message.getMessage("FAIL.no-permission"));
				return true;
			}
			return onReload(sender, args);
		}
		
		return false;
	}
	
	
	/**
	 * Lệnh trợ giúp.
	 * @param sender
	 * @param label
	 * @param args
	 * @return
	 */
	private boolean onHelp(CommandSender sender, String label, String[] args) {
		String sName = sender.getName();
		Utils.sendMessage(sender, Message.getMessage("HELP.header").replaceAll(versionReg, Utils.getInstance().getDescription().getVersion()));
		Utils.sendMessage(sender, Message.getMessage("HELP.help").replaceAll(labelReg, label));
		if(Utils.getUser(sName).hasPermission(Permission.getPermission("Give"))) {
			Utils.sendMessage(sender, Message.getMessage("HELP.give").replaceAll(labelReg, label));
		}
		if(Utils.getUser(sName).hasPermission(Permission.getPermission("Create"))) {
			Utils.sendMessage(sender, Message.getMessage("HELP.create").replaceAll(labelReg, label));
		}
		if(Utils.getUser(sName).hasPermission(Permission.getPermission("Delete"))) {
			Utils.sendMessage(sender, Message.getMessage("HELP.delete").replaceAll(labelReg, label));
		}
		if(Utils.getUser(sName).hasPermission(Permission.getPermission("Clear"))) {
			Utils.sendMessage(sender, Message.getMessage("HELP.clear").replaceAll(labelReg, label));
		}
		if(Utils.getUser(sName).hasPermission(Permission.getPermission("List"))) {
			Utils.sendMessage(sender, Message.getMessage("HELP.list").replaceAll(labelReg, label));
		}
		if(Utils.getUser(sName).hasPermission(Permission.getPermission("Reload"))) {
			Utils.sendMessage(sender, Message.getMessage("HELP.reload").replaceAll(labelReg, label));
		}
		Utils.sendMessage(sender, Message.getMessage("HELP.footer").replaceAll(versionReg, Utils.getInstance().getDescription().getVersion()));
		return true;
	}
	
	/**
	 * Lệnh trao vật phẩm.
	 * @param sender
	 * @param args
	 * @return
	 */
	private boolean onGive(CommandSender sender, String[] args) {
		if(args.length == 1) {
			Utils.sendMessage(sender, Message.getMessage("FAIL.must-type-name"));
			return true;
		}
		
		String key = args[1];
		if(!Utils.isItemExisted(key)) {
			Utils.sendMessage(sender, Message.getMessage("FAIL.name-not-existed").replaceAll(valueReg, key));
			return true;
		}
		ItemStack item = Utils.getItem(key).getItemStack();
		if((item == null) || (item.getType() == Material.AIR)) {
			Utils.sendMessage(sender, Message.getMessage("FAIL.unknown-error"));
			return true;
		}
		String itemName = (item.hasItemMeta() ? (item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : key) : key);
		
		if(args.length == 2) {
			if(!(sender instanceof Player)) {
				Utils.sendMessage(sender, Message.getMessage("FAIL.must-specify-player"));
				return true;
			}
			Player player = (Player)sender;
			String name = player.getName();
			
			if(Utils.getUser(name).isInventoryFull()) {
				Utils.sendMessage(player, Message.getMessage("FAIL.inventory-is-full"));
				return true;
			}
			
			player.getInventory().addItem(new ItemStack[] { item });
			Utils.sendMessage(player, Message.getMessage("SUCCESS.item-gave").replaceAll(amountReg, String.valueOf(item.getAmount())).replaceAll(itemReg, itemName).replaceAll(playerReg, player.getName()));
			
			return true;
		}
		
		String name = args[2];
		if(!Utils.getUser(name).isActive()) {
			Utils.sendMessage(sender, Message.getMessage("FAIL.player-not-found").replaceAll(valueReg, name));
			Utils.getUserMap().remove(name);
			return true;
		}
		Player target = Utils.getUser(name).getPlayer();
		
		if(Utils.getUser(name).isInventoryFull()) {
			Utils.sendMessage(sender, Message.getMessage("FAIL.inventory-is-full"));
			return true;
		}
		
		if(args.length == 3) {
			target.getInventory().addItem(new ItemStack[] { item });
			
			Utils.sendMessage(sender, Message.getMessage("SUCCESS.item-gave").replaceAll(amountReg, String.valueOf(item.getAmount())).replaceAll(itemReg, itemName).replaceAll(playerReg, target.getName()));
			Utils.sendMessage(target, Message.getMessage("SUCCESS.item-received").replaceAll(amountReg, String.valueOf(item.getAmount())).replaceAll(itemReg, itemName).replaceAll(senderReg, sender.getName()));
			
			return true;
		}
		
		String string = args[3];
		if(!Utils.checkNumber("int", string)) {
			Utils.sendMessage(sender, Message.getMessage("FAIL.not-number").replaceAll(valueReg, string));
			return true;
		}
		int amount = Integer.parseInt(string);
		item.setAmount(amount);
		
		if(args.length == 4) {
			target.getInventory().addItem(new ItemStack[] { item });
			
			Utils.sendMessage(sender, Message.getMessage("SUCCESS.item-gave").replaceAll(amountReg, String.valueOf(item.getAmount())).replaceAll(itemReg, itemName).replaceAll(playerReg, target.getName()));
			Utils.sendMessage(target, Message.getMessage("SUCCESS.item-received").replaceAll(amountReg, String.valueOf(item.getAmount())).replaceAll(itemReg, itemName).replaceAll(senderReg, sender.getName()));
			
			return true;
		}
		if(args.length > 4) {
			Utils.sendMessage(sender, Message.getMessage("FAIL.too-many-args"));
		}
		return true;
	}
	
	/**
	 * Lệnh tạo vật phẩm mới.
	 * @param sender
	 * @param args
	 * @return
	 */
	private boolean onCreate(CommandSender sender, String[] args) {
		if(args.length == 1) {
			Utils.sendMessage(sender, Message.getMessage("FAIL.must-type-name"));
			return true;
		}
		
		String key = args[1];
		if(Utils.isItemExisted(key)) {
			Utils.sendMessage(sender, Message.getMessage("FAIL.name-existed").replaceAll(valueReg, key));
			return true;
		}
		
		if(args.length == 2) {
			Utils.sendMessage(sender, (Utils.createItem(key) ? Message.getMessage("SUCCESS.item-created").replaceAll(itemReg, key) : Message.getMessage("FAIL.unknown-error")));
			return true;
		}
		if(args.length > 2) {
			Utils.sendMessage(sender, Message.getMessage("FAIL.too-many-args"));
		}
		return true;
	}
	
	/**
	 * Lệnh xoá vật phẩm.
	 * @param sender
	 * @param args
	 * @return
	 */
	private boolean onDelete(CommandSender sender, String[] args) {
		if(args.length == 1) {
			Utils.sendMessage(sender, Message.getMessage("FAIL.must-type-name"));
			return true;
		}
		
		String key = args[1];
		if(!Utils.isItemExisted(key)) {
			Utils.sendMessage(sender, Message.getMessage("FAIL.name-not-existed").replaceAll(valueReg, key));
			return true;
		}
		
		if(args.length == 2) {
			Utils.sendMessage(sender, (Utils.deleteItem(key) ? Message.getMessage("SUCCESS.item-deleted").replaceAll(itemReg, key) : Message.getMessage("FAIL.unknown-error")));
			return true;
		}
		if(args.length > 2) {
			Utils.sendMessage(sender, Message.getMessage("FAIL.too-many-args"));
		}
		return true;
	}
	
	/**
	 * Lệnh xoá tất cả vật phẩm.
	 * @param sender
	 * @param args
	 * @return
	 */
	private boolean onClear(CommandSender sender, String[] args) {
		if(Utils.getItems().isEmpty()) {
			Utils.sendMessage(sender, Message.getMessage("FAIL.no-item-existed"));
			return true;
		}
		int size = Utils.getItems().size();
		
		Utils.sendMessage(sender, Message.getMessage("SUCCESS.item-cleared").replaceAll(amountReg, String.valueOf(size)));
		Utils.clearAllItems();
		
		return true;
	}
	
	/**
	 * Lệnh xem danh sách các vật phẩm hiện có.
	 * @param sender
	 * @param args
	 * @return
	 */
	private boolean onList(CommandSender sender, String[] args) {
		if(Utils.getItems().isEmpty()) {
			Utils.sendMessage(sender, Message.getMessage("FAIL.no-item-existed"));
			return true;
		}
		int size = Utils.getItems().size();
		String list = Utils.getItems().keySet().toString().replaceAll("(\\[|\\])", "");
		
		Utils.sendMessage(sender, Message.getMessage("SUCCESS.list-items").replaceAll(amountReg, String.valueOf(size)).replaceAll(listReg, list));
		
		return true;
	}
	
	/**
	 * Lệnh làm mới tập tin.
	 * @param sender
	 * @param args
	 * @return
	 */
	private boolean onReload(CommandSender sender, String[] args) {
		Config.reloadConfig();
		Config.reloadMessage();
		
		Message.load();
		Permission.load();
		Setting.load();
		
		Utils.loadAllItems();
		Utils.loadAllUsers();
		
		Utils.sendMessage(sender, Message.getMessage("SUCCESS.config-reload"));
		return true;
	}

}
