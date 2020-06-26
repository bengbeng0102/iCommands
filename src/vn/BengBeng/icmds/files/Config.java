package vn.BengBeng.icmds.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import vn.BengBeng.icmds.utils.Utils;

public class Config {
	
	private static File getPluginFolder() {
		return Utils.getInstance().getDataFolder();
	}
	
	
	private static FileConfiguration config, message;
	private static File configF, messageF;
	private static boolean loaded = false;
	
	
	/*
	 * KHỞI ĐỘNG FILE:
	 */
	
	public static void loadConfig() {
		configF = new File(getPluginFolder(), "config.yml");
		if(!configF.exists()) {
			getPluginFolder().mkdirs();
			try {
				configF.createNewFile();
				InputStream in = Config.class.getResourceAsStream("/config.yml");
				Utils.copyFile(in, configF);
				config = new YamlConfiguration();
				config.load(configF);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			loaded = true;
		} else {
			config = new YamlConfiguration();
			try {
				config.load(configF);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			loaded = true;
		}
	}
	
	public static void loadMessage() {
		messageF = new File(getPluginFolder(), "message.yml");
		if(!messageF.exists()) {
			getPluginFolder().mkdirs();
			try {
				messageF.createNewFile();
				InputStream in = Config.class.getResourceAsStream("/message.yml");
				Utils.copyFile(in, messageF);
				message = new YamlConfiguration();
				message.load(messageF);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			loaded = true;
		} else {
			message = new YamlConfiguration();
			try {
				message.load(messageF);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			loaded = true;
		}
	}
	
	
	
	/*
	 * LƯU FILE:
	 */
	
	public static void saveConfig() {
		if(!loaded) {
			loadConfig();
		} else {
			try {
				config.save(configF);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static void saveMessage() {
		if(!loaded) {
			loadMessage();
		} else {
			try {
				message.save(messageF);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	
	
	/*
	 * LÀM MỚI FILE:
	 */
	
	public static void reloadConfig() {
		try {
			config.load(configF);
			InputStream input = new FileInputStream(configF);
			if(input != null) {
				config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(input, StandardCharsets.UTF_8)));
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void reloadMessage() {
		try {
			message.load(messageF);
			InputStream input = new FileInputStream(messageF);
			if(input != null) {
				message.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(input, StandardCharsets.UTF_8)));
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
	
	/*
	 * LẤY FILE:
	 */
	
	public static FileConfiguration getConfig() {
		if(!loaded) {
			loadConfig();
		}
		return config;
	}
	
	public static FileConfiguration getMessage() {
		if(!loaded) {
			loadMessage();
		}
		return message;
	}
	
}
