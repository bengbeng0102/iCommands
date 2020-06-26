package vn.BengBeng.icmds;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import vn.BengBeng.icmds.commands.PluginCommands;
import vn.BengBeng.icmds.configures.Message;
import vn.BengBeng.icmds.configures.Permission;
import vn.BengBeng.icmds.configures.Setting;
import vn.BengBeng.icmds.events.PlayerInteract;
import vn.BengBeng.icmds.events.PlayerJoinQuit;
import vn.BengBeng.icmds.files.Config;
import vn.BengBeng.icmds.hooks.LuckPermsHook;
import vn.BengBeng.icmds.hooks.PermissionHook;
import vn.BengBeng.icmds.hooks.PermissionsExHook;
import vn.BengBeng.icmds.utils.Utils;

public class iCommands
	extends JavaPlugin {
	
	private String prefix;
	private PluginManager pm;
	
	
	private PermissionHook permHook;
	
	public PermissionHook getPermissionHook() {
		return permHook;
	}
	
	
	@Override
	public void onEnable() {
		prefix = "&8[&ciCommands&8]&r ";
		pm = Bukkit.getServer().getPluginManager();
		
		loadFiles();
		loadConfigures();
		
		registerCommands();
		registerEvents();
		
		if(Utils.isHookEconomy()) {
			Utils.sendConsoleMessage(prefix + "&aHooked into: &eVault&a.");
		}
		if(Utils.isHookPlayerPoints()) {
			Utils.sendConsoleMessage(prefix + "&aHooked into: &ePlayerPoints&a.");
		}
		if(Utils.isHooked("PermissionsEx")) {
			 permHook = new PermissionsExHook();
			 Utils.sendConsoleMessage(prefix + "&aHooked into: &ePermissionsEx&a.");
		} else if(Utils.isHooked("LuckPerms")) {
			permHook = new LuckPermsHook();
			Utils.sendConsoleMessage(prefix + "&aHooked into: &eLuckPerms&a.");
		} else {
			permHook = null;
			Utils.sendConsoleMessage(prefix + "&cNo permission plugins found!");
		}
		
		Utils.loadAllItems();
		
		Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
			Collection<? extends Player> players = Bukkit.getServer().getOnlinePlayers();
			if((players == null) || (players.isEmpty())) {
				return;
			}
			for(Player player : players) {
				String name = player.getName();
				if(Utils.getUserMap().containsKey(name)) {
					continue;
				}
				Utils.getUser(name);
			}
		});
	}
	
	@Override
	public void onDisable() {}
	
	
	/**
	 * Khởi chạy các config.
	 */
	private void loadConfigures() {
		new Message();
		new Permission();
		new Setting();
	}
	
	/**
	 * Đăng ký lệnh cho plugin.
	 */
	private void registerCommands() {
		getCommand("icommands").setExecutor(new PluginCommands());
	}
	
	/**
	 * Đăng ký event cho plugin.
	 */
	private void registerEvents() {
		pm.registerEvents(new PlayerInteract(), this);
		pm.registerEvents(new PlayerJoinQuit(), this);
	}
	
	
	/**
	 * Khởi tạo các file chính của plugin.
	 */
	private void loadFiles() {
		Config.loadConfig();
		Config.loadMessage();
		
		Utils.loadItemFolder();
		Utils.loadPlayerFolder();
	}
	
}
