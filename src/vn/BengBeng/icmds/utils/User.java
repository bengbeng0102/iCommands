package vn.BengBeng.icmds.utils;

import java.io.File;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import vn.BengBeng.icmds.configures.Permission;
import vn.BengBeng.icmds.xseries.XSound;

public class User {
	
	private String name;
	
	/**
	 * Trả về tên người chơi.
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	private OfflinePlayer offPlayer;
	
	public OfflinePlayer getOfflinePlayer() {
		return offPlayer;
	}
	
	public Player getPlayer() {
		return offPlayer.getPlayer();
	}
	
	public UUID getUUID() {
		return offPlayer.getUniqueId();
	}
	
	
	public User(String name) {
		this.name = name;
		offPlayer = Bukkit.getServer().getOfflinePlayer(name);
		
		if(isActive()) {
			loadData();
		}
	}
	
	
	/**
	 * Trả về true nếu người chơi đã tồn tại và đang trực tuyến.
	 * @return
	 */
	public boolean isActive() {
		return ((offPlayer != null) && (offPlayer.isOnline()));
	}
	
	/**
	 * Trả về true nếu người chơi có quyền lợi được xác định.
	 * @param perm
	 * @return
	 */
	public boolean hasPermission(String perm) {
		if(!(getPlayer() instanceof Player)) {
			return true;
		}
		if((offPlayer.isOp()) || (getPlayer().hasPermission(Permission.getPermission("Admin")))) {
			return true;
		}
		return getPlayer().hasPermission(perm);
	}
	
	/**
	 * Trả về true nếu túi đồ của người chơi đã đầy.
	 * @return
	 */
	public boolean isInventoryFull() {
		return (getPlayer().getInventory().firstEmpty() == -1);
	}
	
	
	private int lastSent = 0;
	
	/**
	 * Trả về true nếu thời gian gửi thông điệp là mới nhất.
	 * @return
	 */
	public boolean isLastSent() {
		int current = ((int)(System.currentTimeMillis() / 1000));
		if((lastSent - current) > 0) {
			return true;
		}
		lastSent = (current + 1);
		return false;
	}
	
	
	private File file;
	private FileConfiguration fileCfg;
	
	/**
	 * Khởi chạy dữ liệu cho người chơi.
	 */
	private void loadData() {
		file = new File(Utils.getPlayerFolder(), getUUID().toString() + ".yml");
		if(!file.exists()) {
			try {
				file.createNewFile();
				fileCfg = new YamlConfiguration();
				fileCfg.load(file);
				loadAttributes();
			} catch(Exception error) {
				error.printStackTrace();
			}
		} else {
			fileCfg = new YamlConfiguration();
			try {
				fileCfg.load(file);
			} catch(Exception error) {
				error.printStackTrace();
			}
		}
	}
	
	/**
	 * Lưu lại dữ liệu người chơi.
	 */
	public void saveData() {
		try {
			fileCfg.save(file);
		} catch(Exception error) {
			error.printStackTrace();
		}
	}
	
	/**
	 * Khởi chạy các thuộc tính cho người chơi.
	 */
	private void loadAttributes() {
		fileCfg.set("player-name", name);
		fileCfg.set("item-used", "");
		saveData();
	}
	
	
	private int cooldown = -1;
	
	/**
	 * Trả về true nếu người chơi đã sử dụng vật phẩm gần nhất.
	 * @param cooldown
	 * @return
	 */
	public boolean isCooldown(int cooldown) {
		int time = ((int)(System.currentTimeMillis() / 1000));
		if(this.cooldown > time) {
			return true;
		}
		this.cooldown = (time + cooldown);
		return false;
	}
	
	/**
	 * Lấy thời gian chờ sử dụng vật phẩm tiếp theo.
	 * @return
	 */
	public int getCooldownLeft() {
		int time = ((int)(System.currentTimeMillis() / 1000));
		return (cooldown - time);
	}
	
	
	/**
	 * Trả về tên của những vật phẩm mà người chơi đã sử dụng.
	 * @return
	 */
	public String getItemUsed() {
		return fileCfg.getString("item-used");
	}
	
	/**
	 * Trả về true nếu người chơi đã sử dụng vật phẩm được xác định.
	 * @param key
	 * @return
	 */
	public boolean isUsedItem(String key) {
		return getItemUsed().contains(key);
	}
	
	/**
	 * Thêm vật phẩm đã sử dụng vào danh sách.
	 * @param key
	 */
	public void addUsedItem(String key) {
		String used = getItemUsed();
		if(used.isEmpty()) {
			fileCfg.set("item-used", key);
		} else {
			fileCfg.set("item-used", used + ", " + key);
		}
		saveData();
	}
	
	
	public synchronized void execute(String cmd) {
		boolean matched = false;
		
		Pattern chancePattern = Pattern.compile("(\\<(?ium)(chance)\\=(?<chance>(\\d+(\\.\\d+)?))(\\%)?\\>)");
		Matcher chanceMatcher = chancePattern.matcher(cmd);
		
		Pattern pattern = Pattern.compile("\\[(?ium)(op)\\]\\s(?<cmd>.*)");
		Matcher matcher = pattern.matcher(cmd);
		if(matcher.find()) {
			matched = true;
			String newCmd = Utils.toColor(matcher.group("cmd"));
			
			if(chanceMatcher.find()) {
				double chance = Double.parseDouble(chanceMatcher.group("chance"));
				if(!Utils.randomSuccess(chance)) {
					return;
				}
				newCmd = newCmd.replaceAll(chancePattern.pattern(), "").replaceAll(Utils.getRegex("chance"), String.valueOf(chance));
			}
			
			if(!getPlayer().isOp()) {
				try {
					getPlayer().setOp(true);
					Bukkit.getServer().dispatchCommand(getPlayer(), newCmd);
				} finally {
					getPlayer().setOp(false);
				}
			} else {
				Bukkit.getServer().dispatchCommand(getPlayer(), newCmd);
			}
		}
		
		pattern = Pattern.compile("\\[(?ium)(console)\\]\\s(?<cmd>.*)");
		matcher = pattern.matcher(cmd);
		if(matcher.find()) {
			matched = true;
			String newCmd = Utils.toColor(matcher.group("cmd"));
			
			if(chanceMatcher.find()) {
				double chance = Double.parseDouble(chanceMatcher.group("chance"));
				if(!Utils.randomSuccess(chance)) {
					return;
				}
				newCmd = newCmd.replaceAll(chancePattern.pattern(), "").replaceAll(Utils.getRegex("chance"), String.valueOf(chance));
			}
			
			Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), newCmd);
		}
		
		pattern = Pattern.compile("\\[(?ium)(broadcast)\\]\\s(?<msg>.*)");
		matcher = pattern.matcher(cmd);
		if(matcher.find()) {
			matched = true;
			String msg = Utils.toColor(matcher.group("msg"));
			
			if(chanceMatcher.find()) {
				double chance = Double.parseDouble(chanceMatcher.group("chance"));
				if(!Utils.randomSuccess(chance)) {
					return;
				}
				msg = msg.replaceAll(chancePattern.pattern(), "").replaceAll(Utils.getRegex("chance"), String.valueOf(chance));
			}
			
			Utils.broadcastMessage(msg);
		}
		
		pattern = Pattern.compile("\\[(?ium)(message)\\]\\s(?<msg>.*)");
		matcher = pattern.matcher(cmd);
		if(matcher.find()) {
			matched = true;
			String msg = Utils.toColor(matcher.group("msg"));
			
			if(chanceMatcher.find()) {
				double chance = Double.parseDouble(chanceMatcher.group("chance"));
				if(!Utils.randomSuccess(chance)) {
					return;
				}
				msg = msg.replaceAll(chancePattern.pattern(), "").replaceAll(Utils.getRegex("chance"), String.valueOf(chance));
			}
			
			Utils.sendMessage(getPlayer(), msg);
		}
		
		pattern = Pattern.compile("\\[(?ium)(sound)\\]\\s(?<sound>.*)");
		matcher = pattern.matcher(cmd);
		if(matcher.find()) {
			matched = true;
			if(chanceMatcher.find()) {
				double chance = Double.parseDouble(chanceMatcher.group("chance"));
				if(!Utils.randomSuccess(chance)) {
					return;
				}
			}
			
			String sound = matcher.group("sound").replaceAll(chancePattern.pattern(), "").toUpperCase();
			XSound.playSoundFromString(getPlayer(), sound);
		}
		
		if(!matched) {
			if(chanceMatcher.find()) {
				double chance = Double.parseDouble(chanceMatcher.group("chance"));
				if(!Utils.randomSuccess(chance)) {
					return;
				}
				cmd = cmd.replaceAll(chancePattern.pattern(), "").replaceAll(Utils.getRegex("chance"), String.valueOf(chance));
			}
			Bukkit.getServer().dispatchCommand(getPlayer(), Utils.toColor(cmd));
		}
	}
	
}
