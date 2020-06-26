package vn.BengBeng.icmds.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import vn.BengBeng.icmds.item.Item;
import vn.BengBeng.icmds.utils.Utils;

public class PlayerJoinQuit
	implements Listener {
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		String name = player.getName();
		Utils.getUser(name);
		
		if(!Utils.getItems().isEmpty()) {
			for(String key : Utils.getItems().keySet()) {
				Item item = Utils.getItem(key);
				ItemStack stack = item.getItemStack();
				if(item.isFirstJoinReceive()) {
					if(!player.hasPlayedBefore()) {
						player.getInventory().addItem(new ItemStack[] { stack });
						continue;
					}
				}
				if(item.isJoinReceive()) {
					player.getInventory().addItem(new ItemStack[] { stack });
				}
			}
		}
		
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		String name = player.getName();
		if(Utils.getUserMap().containsKey(name)) {
			Utils.getUserMap().remove(name);
		}
	}
	
}
