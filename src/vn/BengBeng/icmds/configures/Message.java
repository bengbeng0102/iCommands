package vn.BengBeng.icmds.configures;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.configuration.file.FileConfiguration;

import vn.BengBeng.icmds.files.Config;
import vn.BengBeng.icmds.utils.Utils;

public class Message {
	
	private static FileConfiguration fileCfg;
	
	private static String prefix;
	
	private static Map<String, Object> messages;
	
	
	public Message() {
		messages = Collections.synchronizedMap(new HashMap<String, Object>());
		load();
	}
	
	
	/**
	 * Khởi chạy.
	 */
	public synchronized static void load() {
		fileCfg = Config.getMessage();
		
		prefix = Utils.toColor(Config.getMessage().getString("PREFIX"));
		
		messages.clear();
		fileCfg.getKeys(true).stream().forEach(keys -> {
			if((fileCfg.isConfigurationSection(keys)) || (keys.equals("PREFIX"))) {
				return;
			}
			Object value = fileCfg.get(keys);
			if(value instanceof String) {
				value = Utils.toColor(value.toString().replaceAll(Utils.getRegex("pref(ix)?"), prefix));
			} else if(value instanceof List) {
				value = ((List<String>)value).stream().map(string -> Utils.toColor(string.replaceAll(Utils.getRegex("pref(ix)?"), prefix))).collect(Collectors.toList());
			}
			messages.put(keys, value);
		});
		
	}
	
	
	/**
	 * Trả về tiếp đầu ngữ.
	 * @return
	 */
	public static String getPrefix() {
		return prefix;
	}
	
	
	/**
	 * Lấy HashMap lưu trữ các thông điệp.
	 * @return
	 */
	public static Map<String, Object> getMessages() {
		return messages;
	}
	
	
	/**
	 * Trả về một thông điệp từ đường dẫn.
	 * @param key
	 * @return
	 */
	public static String getMessage(String key) {
		if(!messages.containsKey(key)) {
			return null;
		}
		return String.valueOf(messages.get(key));
	}
	
	/**
	 * Trả về danh sách các thông điệp từ đường dẫn.
	 * @param key
	 * @return
	 */
	public static List<String> getMessages(String key) {
		if(!messages.containsKey(key)) {
			return null;
		}
		return ((List<String>)messages.get(key));
	}
	
}
