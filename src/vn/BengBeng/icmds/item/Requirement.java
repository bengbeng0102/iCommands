package vn.BengBeng.icmds.item;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import vn.BengBeng.icmds.exception.InvalidConfigurationException;

public class Requirement {
	
	private String prefix;
	
	private String key;
	private ConfigurationSection section;
	
	public Requirement(String key, FileConfiguration fileCfg) {
		prefix = "REQUIREMENT";
		this.key = key;
		section = fileCfg.getConfigurationSection(prefix);
	}
	
	
	/**
	 * Trả về true nếu có yêu cầu bất kỳ cái gì đó.
	 * @return
	 */
	public boolean hasAnyRequirements() {
		return ((section != null) && (!section.getKeys(false).isEmpty()));
	}
	
	
	/**
	 * Trả về true nếu có yêu cầu quyền.
	 * @return
	 */
	public boolean hasPermissionSection() {
		if((!section.contains("permission")) || (!section.isString("permission")) || (section.getString("permission").isEmpty())) {
			return false;
		}
		return true;
	}
	
	/**
	 * Trả về quyền yêu cầu.
	 * @return
	 */
	public String getPermission() {
		return section.getString("permission");
	}
	
	
	/**
	 * Trả về true nếu có yêu cầu cấp bậc.
	 * @return
	 */
	public boolean hasRankSection() {
		if((!section.contains("rank")) || (!section.isString("rank")) || (section.getString("rank").isEmpty())) {
			return false;
		}
		return true;
	}
	
	/**
	 * Trả về cấp bậc yêu cầu.
	 * @return
	 */
	public String getRank() {
		return section.getString("rank");
	}
	
	
	/**
	 * Trả về true nếu có yêu cầu về tên người chơi.
	 * @return
	 */
	public boolean hasPlayerSection() {
		if((!section.contains("Player")) || (section.getConfigurationSection("Player").getKeys(false).isEmpty())) {
			return false;
		}
		if((!section.contains("Player.values")) || (!section.isList("Player.values")) || (section.getStringList("Player.values").isEmpty())) {
			return false;
		}
		if((!section.contains("Player.take")) || (!section.isBoolean("Player.take"))) {
			throw new InvalidConfigurationException("Missing '" + prefix + ".Player.take' section in file '" + key + ".yml' or not a BOOLEAN.");
		}
		return true;
	}
	
	/**
	 * Trả về danh sách những người chơi được phép sử dụng vật phẩm.
	 * @return
	 */
	public List<String> getPlayers() {
		return section.getStringList("Player.values");
	}
	
	/**
	 * Trả về true nếu cho phép xoá người chơi khỏi danh sách
	 * sau khi sử dụng vật phẩm.
	 * @return
	 */
	public boolean isTakePlayer() {
		return section.getBoolean("Player.take");
	}
	
	
	/**
	 * Trả về true nếu có yêu cầu có tiền.
	 * @return
	 */
	public boolean hasMoneySection() {
		if((!section.contains("Money")) || (section.getConfigurationSection("Money").getKeys(false).isEmpty())) {
			return false;
		}
		if((!section.contains("Money.value")) || (!section.isDouble("Money.value")) || (section.getDouble("Money.value") <= 0.0)) {
			return false;
		}
		if((!section.contains("Money.take")) || (!section.isBoolean("Money.take"))) {
			throw new InvalidConfigurationException("Missing '" + prefix + ".Money.take' section in file '" + key + ".yml' or not a BOOLEAN.");
		}
		return true;
	}
	
	/**
	 * Trả về số tiền yêu cầu.
	 * @return
	 */
	public double getMoney() {
		return section.getDouble("Money.value");
	}
	
	
	/**
	 * Trả về true nếu có yêu cầu có tiền.
	 * @return
	 */
	public boolean hasPointSection() {
		if((!section.contains("Point")) || (section.getConfigurationSection("Point").getKeys(false).isEmpty())) {
			return false;
		}
		if((!section.contains("Point.value")) || (!section.isInt("Point.value")) || (section.getInt("Point.value") <= 0)) {
			return false;
		}
		if((!section.contains("Point.take")) || (!section.isBoolean("Point.take"))) {
			throw new InvalidConfigurationException("Missing '" + prefix + ".Point.take' section in file '" + key + ".yml' or not a BOOLEAN.");
		}
		return true;
	}
	
	/**
	 * Trả về số tiền yêu cầu.
	 * @return
	 */
	public int getPoint() {
		return section.getInt("Point.value");
	}
	
}
