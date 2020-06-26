package vn.BengBeng.icmds.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import vn.BengBeng.icmds.configures.Message;
import vn.BengBeng.icmds.item.Item;
import vn.BengBeng.icmds.listeners.PlayerUseItemEvent;
import vn.BengBeng.icmds.utils.Utils;

public class PlayerInteract
	implements Listener {
	
	private List<String> clicked = Collections.synchronizedList(new ArrayList<String>());
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		String name = player.getName();
		
		ItemStack hand = player.getInventory().getItemInMainHand();
		String key = checkItem(hand);
		if(key == null) {
			return;
		}
		
		event.setCancelled(true);
		event.setUseInteractedBlock(Result.DENY);
		event.setUseItemInHand(Result.DENY);
		
		Action action = event.getAction();
		if((action != Action.LEFT_CLICK_AIR) && (action != Action.LEFT_CLICK_BLOCK) && (action != Action.RIGHT_CLICK_AIR) && (action != Action.RIGHT_CLICK_BLOCK)) {
			return;
		}
		if(action == Action.RIGHT_CLICK_BLOCK) { // Tránh trường hợp bị nhấp 2 lần (dù thật sự chỉ nhấp 1 lần).
			if(!clicked.contains(name)) {
				clicked.add(name);
			} else {
				clicked.remove(name);
				return;
			}
		}
		
		Item item = Utils.getItem(key);
		
		// Kiểm tra vật phẩm có bị vô hiệu hoá hay không.
		if(!item.isEnabled()) {
			Utils.sendDelayMessage(player, Message.getMessage("FAIL.ON_USE.item-is-disabled"));
			return;
		}
		
		// Kiểm tra nếu vật phẩm có thời hạn sử dụng.
		if(item.hasDateExpired()) {
			String date = item.getDateExpired();
			if(Utils.isExpired(date)) {
				Utils.sendDelayMessage(player, Message.getMessage("FAIL.ON_USE.item-is-expired"));
				return;
			}
		}
		
		// Trả về true nếu người chơi đang trong thời gian chờ cho lần sử dụng tiếp theo.
		int cooldown = item.getCooldown();
		if(cooldown > 0) {
			if(Utils.getUser(name).isCooldown(cooldown)) {
				Utils.sendDelayMessage(player, Message.getMessage("FAIL.ON_USE.must-slow").replaceAll(Utils.getRegex("time"), Utils.getFormatTime(Utils.getUser(name).getCooldownLeft())));
				Event useEvent = new PlayerUseItemEvent(player, item.getItemStack(), false);
				Bukkit.getServer().getPluginManager().callEvent(useEvent);
				return;
			}
		}
		
		// Kiểm tra nếu vật phẩm chỉ cho phép sử dụng một lần duy nhất.
		boolean oneUse = item.isOneUse();
		if(oneUse) {
			if(Utils.getUser(name).isUsedItem(key)) {
				Utils.sendDelayMessage(player, Message.getMessage("FAIL.ON_USE.item-already-used"));
				Event useEvent = new PlayerUseItemEvent(player, item.getItemStack(), false);
				Bukkit.getServer().getPluginManager().callEvent(useEvent);
				return;
			}
		}
		
		// Kiểm tra số lượng vật phẩm sẽ bị trừ đi sau khi sử dụng.
		int takeAmount = item.getRemoveOnUse();
		if(takeAmount > 0) {
			int current = hand.getAmount();
			if(current < takeAmount) {
				Utils.sendDelayMessage(player, Message.getMessage("FAIL.ON_USE.not-enough-item").replaceAll(Utils.getRegex("amount"), String.valueOf(takeAmount)));
				Event useEvent = new PlayerUseItemEvent(player, item.getItemStack(), false);
				Bukkit.getServer().getPluginManager().callEvent(useEvent);
				return;
			}
			current -= takeAmount;
			hand.setAmount(current);
		}
		
		boolean takePlayer = false;
		double money = -1.0;
		int point = -1;
		// Các yêu cầu trước khi người chơi có thể sử dụng vật phẩm.
		if(item.getRequirement().hasAnyRequirements()) {
			// Nếu vật phẩm yêu cầu quyền để sử dụng.
			if(item.getRequirement().hasPermissionSection()) {
				String perm = item.getRequirement().getPermission();
				if(!Utils.getUser(name).hasPermission(perm)) {
					Utils.sendDelayMessage(player, Message.getMessage("FAIL.ON_USE.no-permission").replaceAll(Utils.getRegex("perm(ission)?"), perm));
					Event useEvent = new PlayerUseItemEvent(player, item.getItemStack(), false);
					Bukkit.getServer().getPluginManager().callEvent(useEvent);
					return;
				}
			}
			
			// Nếu vật phẩm yêu cầu cấp bậc để sử dụng.
			if(item.getRequirement().hasRankSection()) {
				String rank = item.getRequirement().getRank();
				if(Utils.getInstance().getPermissionHook() != null) {
					String playerRank = Utils.getInstance().getPermissionHook().getRank(name);
					if((playerRank.isEmpty()) || (!rank.equalsIgnoreCase(playerRank))) {
						Utils.sendDelayMessage(player, Message.getMessage("FAIL.ON_USE.rank-not-match").replaceAll(Utils.getRegex("rank"), rank));
						Event useEvent = new PlayerUseItemEvent(player, item.getItemStack(), false);
						Bukkit.getServer().getPluginManager().callEvent(useEvent);
						return;
					}
				}
			}
			
			// Nếu vật phẩm yêu cầu kiểm tra tên người chơi để sử dụng.
			if(item.getRequirement().hasPlayerSection()) {
				List<String> players = item.getRequirement().getPlayers();
				if(!players.contains(name)) {
					Utils.sendDelayMessage(player, Message.getMessage("FAIL.ON_USE.player-not-allowed"));
					Event useEvent = new PlayerUseItemEvent(player, item.getItemStack(), false);
					Bukkit.getServer().getPluginManager().callEvent(useEvent);
					return;
				}
				takePlayer = item.getRequirement().isTakePlayer();
			}
			
			// Nếu vật phẩm yêu cầu người chơi phải có tiền.
			if(item.getRequirement().hasMoneySection()) {
				double value = item.getRequirement().getMoney();
				if(Utils.isHookEconomy()) {
					double current = Utils.getEconomy().getBalance(name);
					if(current < value) {
						Utils.sendDelayMessage(player, Message.getMessage("FAIL.ON_USE.not-enough-money").replaceAll(Utils.getRegex("money", "bal(ance)?"), String.valueOf(value)));
						Event useEvent = new PlayerUseItemEvent(player, item.getItemStack(), false);
						Bukkit.getServer().getPluginManager().callEvent(useEvent);
						return;
					}
					money = value;
				}
			}
			
			// Nếu vật phẩm yêu cầu người chơi phải có point.
			if(item.getRequirement().hasPointSection()) {
				int value = item.getRequirement().getPoint();
				if(Utils.isHookPlayerPoints()) {
					int current = Utils.getPlayerPoints().getAPI().look(name);
					if(current < value) {
						Utils.sendDelayMessage(player, Message.getMessage("FAIL.ON_USE.not-enough-point").replaceAll(Utils.getRegex("point(s)?"), String.valueOf(value)));
						Event useEvent = new PlayerUseItemEvent(player, item.getItemStack(), false);
						Bukkit.getServer().getPluginManager().callEvent(useEvent);
						return;
					}
					point = value;
				}
			}
			
		}
		
		if(Utils.getUser(name).isInventoryFull()) {
			Utils.sendDelayMessage(player, Message.getMessage("FAIL.inventory-is-full"));
			Event useEvent = new PlayerUseItemEvent(player, item.getItemStack(), false);
			Bukkit.getServer().getPluginManager().callEvent(useEvent);
			return;
		}
		
		if(oneUse) {
			Utils.getUser(name).addUsedItem(key);
		}
		if(takePlayer) {
			List<String> players = item.getRequirement().getPlayers();
			players.remove(name);
			item.set("REQUIREMENT.Player.list", players);
		}
		if(money != -1.0) {
			Utils.getEconomy().withdrawPlayer(name, money);
		}
		if(point != -1) {
			Utils.getPlayerPoints().getAPI().take(name, point);
		}
		
		// Các phần thưởng cho người chơi sau khi sử dụng vật phẩm.
		if(item.getReward().hasAnyRewards()) {
			// Nếu có phần thưởng là các vật phẩm.
			if(item.getReward().hasAnyItems()) {
				List<ItemStack> items = item.getReward().getItems();
				for(ItemStack itemRewards : items) {
					player.getInventory().addItem(new ItemStack[] { itemRewards });
				}
			}
			
			// Nếu có phần thưởng là các câu lệnh.
			if(item.getReward().hasCommands()) {
				ItemStack stack = item.getItemStack();
				String itemName = (stack.hasItemMeta() ? (stack.getItemMeta().hasDisplayName() ? stack.getItemMeta().getDisplayName() : key) : key);
				
				// Sử dụng lệnh thông thường.
				List<String> normalCmds = item.getReward().getNormalCommands();
				if(!normalCmds.isEmpty()) {
					for(String cmds : normalCmds) {
						cmds = cmds.replaceAll(Utils.getRegex("player"), name).replaceAll(Utils.getRegex("item((\\-|\\_)?name)?"), itemName);
						Utils.getUser(name).execute(cmds);
					}
				}
				
				// Sử dụng lệnh ngẫu nhiên.
				List<String> randomCmds = item.getReward().getRandomCommands();
				if(!randomCmds.isEmpty()) {
					int random = new Random().nextInt(randomCmds.size());
					String cmd = randomCmds.get(random).replaceAll(Utils.getRegex("player"), name).replaceAll(Utils.getRegex("item((\\-|\\_)?name)?"), itemName);
					Utils.getUser(name).execute(cmd);
				}
			}
			
		}
		
		Event useEvent = new PlayerUseItemEvent(player, item.getItemStack(), true);
		Bukkit.getServer().getPluginManager().callEvent(useEvent);
		
	}
	
	
	/**
	 * Trả về true nếu vật phẩm được kiểm tra trùng với
	 * vật phẩm có trong danh sách.
	 * @param item
	 * @return
	 */
	private String checkItem(ItemStack item) {
		if(Utils.getItems().isEmpty()) {
			return null;
		}
		for(String key : Utils.getItems().keySet()) {
			ItemStack stack = Utils.getItem(key).getItemStack();
			if(item.isSimilar(stack)) {
				return key;
			}
		}
		return null;
	}
	
}
