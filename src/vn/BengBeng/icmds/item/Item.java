package vn.BengBeng.icmds.item;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import vn.BengBeng.icmds.utils.Utils;
import vn.BengBeng.icmds.xseries.XMaterial;

public class Item {
	
	private String key;
	
	
	public Item(String key) {
		this.key = key;
		load();
	}
	
	
	private boolean enabled;
	
	private String dateExpired;
	private boolean hasDateExpired;
	
	private int cooldown;
	
	private boolean firstJoinReceive, joinReceive, oneUse;
	
	private int removeOnUse;
	
	private ItemStack item;
	
	
	private File file;
	private FileConfiguration fileCfg;
	
	/**
	 * Khởi tạo vật phẩm.
	 */
	public synchronized void load() {
		file = new File(Utils.getItemFolder(), key + ".yml");
		if(!file.exists()) {
			try {
				file.createNewFile();
				InputStream input = getClass().getResourceAsStream("/example_item.yml");
				Utils.copyFile(input, file);
				fileCfg = new YamlConfiguration();
				fileCfg.load(file);
			} catch(IOException | InvalidConfigurationException error) {
				error.printStackTrace();
			}
		} else {
			fileCfg = new YamlConfiguration();
			try {
				fileCfg.load(file);
			} catch(IOException | InvalidConfigurationException error) {
				error.printStackTrace();
			}
		}
		
		/****************************************************************************************/
		
		enabled = fileCfg.getBoolean("SETTINGS.enabled");
		
		dateExpired = fileCfg.getString("SETTINGS.date-expired");
		hasDateExpired = (((dateExpired.isEmpty()) || (dateExpired.equalsIgnoreCase("none"))) ? false : true);
		
		cooldown = fileCfg.getInt("SETTINGS.cooldown");
		
		firstJoinReceive = fileCfg.getBoolean("SETTINGS.first-join-receive");
		joinReceive = fileCfg.getBoolean("SETTINGS.join-receive");
		oneUse = fileCfg.getBoolean("SETTINGS.one-use");
		
		removeOnUse = fileCfg.getInt("SETTINGS.remove-on-use");
		
		/****************************************************************************************/
		
		String type = fileCfg.getString("SETTINGS.type");
		String materialString = type.substring(0, type.indexOf(':'));
		int amount = Integer.parseInt(type.substring(type.indexOf(':') + 1, type.lastIndexOf(':')));
		byte data = Byte.parseByte(type.substring(type.lastIndexOf(':') + 1));
		XMaterial xMater = XMaterial.matchXMaterial(materialString, data).get();
		
		item = new ItemStack(xMater.parseMaterial(), amount, data);
		ItemMeta meta = item.getItemMeta();
		
		String name = fileCfg.getString("SETTINGS.name");
		if((name != null) && (!name.isEmpty())) {
			meta.setDisplayName(Utils.toColor("&a&b&6&e&r" + name));
		}
		
		List<String> lores = fileCfg.getStringList("SETTINGS.lore");
		if((lores != null) && (!lores.isEmpty())) {
			meta.setLore(Utils.toColor(lores));
		}
		
		item.setItemMeta(meta);
		
		if((fileCfg.contains("SETTINGS.glow")) && (fileCfg.getBoolean("SETTINGS.glow"))) {
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
			item.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
		}
		
		/****************************************************************************************/
		
	}
	
	/**
	 * Đặt dữ liệu cho vật phẩm.
	 * @param key
	 * @param value
	 */
	public void set(String key, Object value) {
		fileCfg.set(key, value);
		saveData();
	}
	
	/**
	 * Lưu lại dữ liệu sau khi sửa đổi.
	 */
	public void saveData() {
		try {
			fileCfg.save(file);
		} catch(IOException error) {
			error.printStackTrace();
		}
	}
	
	
	/**
	 * Trả về true nếu vật phẩm được phép sử dụng.
	 * @return
	 */
	public boolean isEnabled() {
		return enabled;
	}
	
	
	/**
	 * Trả về thời gian hết hạn sử dụng của vật phẩm.
	 * @return
	 */
	public String getDateExpired() {
		if(dateExpired.equalsIgnoreCase("none")) {
			return "";
		}
		return dateExpired;
	}
	
	/**
	 * Trả về true nếu vật phẩm có thời hạn sử dụng.
	 * @return
	 */
	public boolean hasDateExpired() {
		return hasDateExpired;
	}
	
	
	/**
	 * Trả về thời gian chờ cho mỗi lần sử dụng vật phẩm.
	 * @return
	 */
	public int getCooldown() {
		return cooldown;
	}
	
	/**
	 * Trả về true nếu trao vật phẩm này cho người chơi lần đầu tham gia máy chủ.
	 * @return
	 */
	public boolean isFirstJoinReceive() {
		return firstJoinReceive;
	}
	
	/**
	 * Trả về true nếu trao vật phẩm cho người chơi tham gia máy chủ.
	 * @return
	 */
	public boolean isJoinReceive() {
		return joinReceive;
	}
	
	/**
	 * Trả về true nếu chỉ cho phép người chơi sử dụng vật phẩm một lần duy nhất.
	 * @return
	 */
	public boolean isOneUse() {
		return oneUse;
	}
	
	/**
	 * Trả về số lượng vật phẩm sẽ bị trừ đi sau khi sử dụng.
	 * @return
	 */
	public int getRemoveOnUse() {
		return removeOnUse;
	}
	
	/**
	 * Trả về vật phẩm.
	 * @return
	 */
	public ItemStack getItemStack() {
		return item;
	}
	
	
	/**
	 * Trả về các yêu cầu cần thiết trước khi có thể sử dụng vật phẩm.
	 * @return
	 */
	public Requirement getRequirement() {
		return (new Requirement(key, fileCfg));
	}
	
	/**
	 * Trả về các phần thưởng sau khi sử dụng vật phẩm.
	 * @return
	 */
	public Reward getReward() {
		return (new Reward(key, fileCfg));
	}
	
}
