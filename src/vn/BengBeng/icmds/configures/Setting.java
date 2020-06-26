package vn.BengBeng.icmds.configures;

import org.bukkit.configuration.file.FileConfiguration;

import vn.BengBeng.icmds.files.Config;
import vn.BengBeng.icmds.utils.UnitFormat;
import vn.BengBeng.icmds.utils.Utils;

public class Setting {
	
	private static FileConfiguration config;
	
	
	private static String dateFormat;
	
	private static UnitFormat unitFormat;
	
	
	public Setting() {
		load();
	}
	
	
	/**
	 * Khởi chạy.
	 */
	public synchronized static void load() {
		config = Config.getConfig();
		
		dateFormat = config.getString("date-format");
		
		String seconds = Utils.toColor(config.getString("format.seconds"));
		String minutes = Utils.toColor(config.getString("format.minutes"));
		String hours = Utils.toColor(config.getString("format.hours"));
		String days = Utils.toColor(config.getString("format.days"));
		unitFormat = new UnitFormat(seconds, minutes, hours, days);
	}
	
	
	/**
	 * Trả về định dạng thời gian.
	 * @return
	 */
	public static String getDateFormat() {
		return dateFormat;
	}
	
	
	/**
	 * Trả về định dạng đơn vị thời gian.
	 * @return
	 */
	public static UnitFormat getUnitFormat() {
		return unitFormat;
	}
	
}
