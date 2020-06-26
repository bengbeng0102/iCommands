package vn.BengBeng.icmds.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import vn.BengBeng.icmds.exception.InvalidConfigurationException;

public class Reward {
	
	private String prefix;
	
	private String key;
	private ConfigurationSection section;
	
	public Reward(String key, FileConfiguration fileCfg) {
		prefix = "REWARDS";
		this.key = key;
		section = fileCfg.getConfigurationSection(prefix);
	}
	
	
	/**
	 * Trả về true nếu có bất kỳ phần thưởng nào.
	 * @return
	 */
	public boolean hasAnyRewards() {
		return ((section != null) && (!section.getKeys(false).isEmpty()));
	}
	
	
	/**
	 * Trả về true nếu có bất kỳ vật phẩm thưởng.
	 * @return
	 */
	public boolean hasAnyItems() {
		if((!section.contains("items")) || (!section.isSet("items")) || (section.getConfigurationSection("items").getKeys(false).isEmpty())) {
			return false;
		}
		return true;
	}
	
	/**
	 * Trả về danh sách các vật phẩm thưởng.
	 * @return
	 */
	public List<ItemStack> getItems() {
		List<ItemStack> items = Collections.synchronizedList(new ArrayList<ItemStack>());
		for(String keys : section.getConfigurationSection("items").getKeys(false)) {
			if(!section.isItemStack("items." + keys)) {
				continue;
			}
			ItemStack item = section.getItemStack("items." + keys);
			if((item.getType() == Material.AIR) || (items.contains(item))) {
				continue;
			}
			items.add(item);
		}
		return items;
	}
	
	
	/**
	 * Trả về true nếu có lệnh thưởng.
	 * @return
	 */
	public boolean hasCommands() {
		if((!section.contains("commands")) || (!section.isSet("commands")) || (section.getConfigurationSection("commands").getKeys(false).isEmpty())) {
			return false;
		}
		if((!section.contains("commands.normal")) || (!section.isList("commands.normal"))) {
			throw new InvalidConfigurationException("Missing '" + prefix + ".commands.normal' section in file '" + key + ".yml' or not a LIST.");
		}
		if((!section.contains("commands.random")) || (!section.isList("commands.random"))) {
			throw new InvalidConfigurationException("Missing '" + prefix + ".commands.random' section in file '" + key + ".yml' or not a LIST.");
		}
		if((section.getStringList("commands.normal").isEmpty()) && (section.getStringList("commands.random").isEmpty())) {
			return false;
		}
		return true;
	}
	
	/**
	 * Trả về danh sách các câu lệnh thông thường.
	 * @return
	 */
	public List<String> getNormalCommands() {
		return section.getStringList("commands.normal");
	}
	
	/**
	 * Trả về danh sách các câu lệnh ngẫu nhiên.
	 * @return
	 */
	public List<String> getRandomCommands() {
		return section.getStringList("commands.random");
	}
	
}
