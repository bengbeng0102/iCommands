package vn.BengBeng.icmds.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;
import vn.BengBeng.icmds.iCommands;
import vn.BengBeng.icmds.configures.Setting;
import vn.BengBeng.icmds.item.Item;

public class Utils {
	
	private static iCommands main = iCommands.getPlugin(iCommands.class);
	
	/**
	 * Trả về class chính.
	 * @return
	 */
	public static iCommands getInstance() {
		return main;
	}
	
	
	/*
	 * KHAI BÁO CLASS:
	 */
	
	private static Map<String, User> maps = Collections.synchronizedMap(new HashMap<String, User>());
	
	public static Map<String, User> getUserMap() {
		return maps;
	}
	
	public static User getUser(String name) {
		if(!maps.containsKey(name)) {
			maps.put(name, new User(name));
		}
		return maps.get(name);
	}
	
	public static void loadAllUsers() {
		maps.clear();
		Collection<? extends Player> players = Bukkit.getServer().getOnlinePlayers();
		if((players != null) && (!players.isEmpty())) {
			for(Player player : players) {
				String name = player.getName();
				getUser(name);
			}
		}
	}
	
	
	private static Map<String, Item> items = Collections.synchronizedMap(new HashMap<String, Item>());
	
	/**
	 * Trả về HashMap, nơi lưu trữ các vật phẩm.
	 * @return
	 */
	public static Map<String, Item> getItems() {
		return items;
	}
	
	/**
	 * Trả về true nếu vật phẩm đã tồn tại.
	 * @param key
	 * @return
	 */
	public static boolean isItemExisted(String key) {
		return items.containsKey(key);
	}
	
	/**
	 * Trả về item.
	 * @param key
	 * @return
	 */
	public static Item getItem(String key) {
		return items.get(key);
	}
	
	/**
	 * Tạo vật phẩm mới.
	 * @param key
	 */
	public static boolean createItem(String key) {
		if(items.containsKey(key)) {
			return false;
		}
		items.put(key, new Item(key));
		return true;
	}
	
	/**
	 * Xoá vật phẩm.
	 * @param key
	 */
	public static boolean deleteItem(String key) {
		if(!items.containsKey(key)) {
			return false;
		}
		items.remove(key);
		File file = new File(itemFolder, key + ".yml");
		file.delete();
		return true;
	}
	
	/**
	 * Xoá tất cả vật phẩm.
	 * @return
	 */
	public static boolean clearAllItems() {
		if(items.isEmpty()) {
			return false;
		}
		for(String key : items.keySet()) {
			File file = new File(itemFolder, key + ".yml");
			file.delete();
		}
		items.clear();
		return true;
	}
	
	/**
	 * Khởi tạo tất cả vật phẩm.
	 */
	public static void loadAllItems() {
		if(!items.isEmpty()) {
			items.clear();
		}
		File[] files = itemFolder.listFiles();
		int fileLength = files.length;
		if(fileLength <= 0) {
			return;
		}
		for(int x = 0; x < fileLength; x++) {
			String key = files[x].getName().replaceAll("(\\.yml)$", "");
			if(items.containsKey(key)) {
				continue;
			}
			items.put(key, new Item(key));
		}
	}
	
	
	/**
	 * Trả về thời gian hiện tại.
	 * @return
	 */
	public static String getCurrentDate() {
		TimeZone zone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
		Calendar cal = Calendar.getInstance(zone);
		SimpleDateFormat format = new SimpleDateFormat(Setting.getDateFormat());
		return format.format(cal.getTime());
	}
	
	
	/**
	 * Định dạng thời gian từ giây sang giờ chuẩn.
	 * @param time
	 * @return
	 */
	public static String getFormatTime(int time) {
		YearMonth year = YearMonth.now();
		int dayInMonth = year.lengthOfMonth();
		int seconds = time % 60;
		int minutes = time / 60 % 60;
		int hours = time / (60 * 60) % 24;
		int days = time / (60 * 60 * 24) % dayInMonth;
		
		String dayStr = Setting.getUnitFormat().getDay();
		String hourStr = Setting.getUnitFormat().getHour();
		String minuteStr = Setting.getUnitFormat().getMinute();
		String secondStr = Setting.getUnitFormat().getSecond();
		
		StringBuilder builder = new StringBuilder();
		if(days != 0) {
			builder.append(days + " " + dayStr + " ");
		}
		if(hours != 0) {
			builder.append(hours + " " + hourStr + " ");
		}
		if(minutes != 0) {
			builder.append(minutes + " " + minuteStr + " ");
		}
		if(seconds != 0) {
			builder.append(seconds + " " + secondStr);
		}
		
		String result = builder.toString().replaceAll("(\\s)$", "");
		result = (result.isEmpty() ? ("0 " + secondStr) : result);
		
		return result;
	}
	
	
	private static File itemFolder;
	
	/**
	 * Trả về thư mục lưu trữ các vật phẩm.
	 * @return
	 */
	public static File getItemFolder() {
		return itemFolder;
	}
	
	/**
	 * Khởi chạy thư mục lưu trữ các vật phẩm.
	 */
	public static void loadItemFolder() {
		itemFolder = new File(main.getDataFolder(), "items");
		if(!itemFolder.exists()) {
			itemFolder.mkdirs();
		}
	}
	
	
	private static File playerFolder;
	
	/**
	 * Trả về thư mục lưu trữ thông tin các người chơi.
	 * @return
	 */
	public static File getPlayerFolder() {
		return playerFolder;
	}
	
	public static void loadPlayerFolder() {
		playerFolder = new File(main.getDataFolder(), "players");
		if(!playerFolder.exists()) {
			playerFolder.mkdirs();
		}
	}
	
	
	/**
	 * Sao chép file nguồn sang file chính.
	 * @param in
	 * @param out
	 */
	public static void copyFile(InputStream in, File out) {
		InputStream fis = in;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(out);
			byte[] buf = new byte[1024];
			int i = 0;
			while((i = fis.read(buf)) != -1) {
				fos.write(buf, 0, i);
			}
		} catch(IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				fis.close();
				fos.close();
			} catch(IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	
	/**
	 * Trả về true nếu đã hết hạn.
	 * @param date
	 * @return
	 */
	public static boolean isExpired(String date) {
		TimeZone zone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
		Calendar cal = Calendar.getInstance(zone);
		Date current = cal.getTime();
		SimpleDateFormat format = new SimpleDateFormat(Setting.getDateFormat());
		try {
			Date time = format.parse(date);
			int compare = time.compareTo(current);
			if((compare == 0) || (compare == -1)) {
				return true;
			}
		} catch(ParseException error) {
			error.printStackTrace();
		}
		return false;
	}
	
	
	/**
	 * Trả về true nếu số ngẫu nhiên bé hơn hoặc bằng giá trị đã được xác định.
	 * @param value
	 * @return
	 */
	public static boolean randomSuccess(double value) {
		double random = formatDouble(Math.random() * 100.0);
		return (random <= value);
	}
	
	
	/*
	 * CÁC PHẦN KHÁC:
	 */
	
	public static double formatDouble(double value) {
		DecimalFormat format = new DecimalFormat("##.##");
		return Double.parseDouble(format.format(value).replace(',', '.'));
	}
	
	public static boolean checkNumber(String type, String str) {
		try {
			switch(type) {
				case "short":
					Short.parseShort(str);
					break;
				case "byte":
					Byte.parseByte(str);
					break;
				case "float":
					Float.parseFloat(str);
					break;
				case "double":
					Double.parseDouble(str);
					break;
				case "int":
					Integer.parseInt(str);
					break;
				case "long":
					Long.parseLong(str);
					break;
				default: break;
			}
		} catch(NumberFormatException error) {
			return false;
		}
		return true;
	}
	
	
	public static String getRegex(String... str) {
		int length = str.length;
		StringBuilder builder = new StringBuilder();
		builder.append("(?ium)(");
		for(int x = 0; x < length; x++) {
			String string = str[x];
			builder.append("\\{" + string + "}|\\%" + string + "%" + (((x + 1) >= length) ? "" : "|"));
		}
		builder.append(")");
		return builder.toString();
	}
	
	public static boolean regexMatches(String str, String regex) {
		return str.matches("(?ium)(" + regex.replaceAll("( |_|-)", "|") + ")");
	}
	
	public static String toColor(String str) {
		return ChatColor.translateAlternateColorCodes('&', str);
	}
	
	public static List<String> toColor(List<String> list) {
		return list.stream().map(string -> toColor(string)).collect(Collectors.toList());
	}
	
	public static void sendMessage(CommandSender sender, String msg) {
		msg = toColor(msg);
		if(sender != null) {
			sender.sendMessage(msg);
		}
	}
	
	public static void sendPlayerMessage(String msg) {
		msg = toColor(msg);
		for(Player players : Bukkit.getServer().getOnlinePlayers()) {
			if(players != null) {
				players.sendMessage(msg);
			}
		}
	}
	
	public static void sendConsoleMessage(String msg) {
		msg = toColor(msg);
		ConsoleCommandSender ccs = Bukkit.getServer().getConsoleSender();
		ccs.sendMessage(msg);
	}
	
	public static void broadcastMessage(String msg) {
		msg = toColor(msg);
		ConsoleCommandSender ccs = Bukkit.getServer().getConsoleSender();
		ccs.sendMessage(msg);
		for(Player players : Bukkit.getServer().getOnlinePlayers()) {
			if(players != null) {
				players.sendMessage(msg);
			}
		}
	}
	
	public static void sendDelayMessage(CommandSender sender, String msg) {
		if(sender instanceof Player) {
			String name = sender.getName();
			if(getUser(name).isLastSent()) {
				return;
			}
		}
		sendMessage(sender, msg);
	}
	
	
	/*
	 * LIÊN KẾT VỚI CÁC PLUGIN KHÁC:
	 */
	
	private static PluginManager pm = Bukkit.getServer().getPluginManager();
	
	public static boolean isHooked(String plName) {
		return ((pm.getPlugin(plName) != null) && (pm.getPlugin(plName).isEnabled()));
	}
	
	
	private static Economy econ;
	
	public static boolean isHookEconomy() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("Vault");
		if((plugin == null) || (!plugin.isEnabled())) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return (econ != null);
	}
	
	public static Economy getEconomy() {
		isHookEconomy();
		return econ;
	}
	
	
	private static PlayerPoints playerPoints;
	
	public static boolean isHookPlayerPoints() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("PlayerPoints");
		if((plugin == null) || (!plugin.isEnabled())) {
			return false;
		}
		playerPoints = PlayerPoints.class.cast(plugin);
		return playerPoints != null;
	}
	
	public static PlayerPoints getPlayerPoints() {
		isHookPlayerPoints();
		return playerPoints;
	}
	
}
