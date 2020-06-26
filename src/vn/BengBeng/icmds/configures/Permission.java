package vn.BengBeng.icmds.configures;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

import vn.BengBeng.icmds.files.Config;

public class Permission {
	
	private static Map<String, String> perms;
	
	private static FileConfiguration fileCfg;
	
	
	public Permission() {
		perms = Collections.synchronizedMap(new HashMap<String, String>());
		load();
	}
	
	
	/**
	 * Khởi chạy.
	 */
	public synchronized static void load() {
		fileCfg = Config.getConfig();
		
		perms.clear();
		fileCfg.getConfigurationSection("PERMISSIONS").getKeys(true).stream().forEach(keys -> {
			if(fileCfg.isConfigurationSection(keys)) {
				return;
			}
			String value = fileCfg.getString("PERMISSIONS." + keys);
			perms.put(keys, value);
		});
		
	}
	
	
	/**
	 * Trả về danh sách các quyền lợi.
	 * @return
	 */
	public static Map<String, String> getPermissions() {
		return perms;
	}
	
	
	/**
	 * Trả về quyền lợi từ đường dẫn.
	 * @param key
	 * @return
	 */
	public static String getPermission(String key) {
		if(!perms.containsKey(key)) {
			return null;
		}
		return perms.get(key);
	}
	
}
