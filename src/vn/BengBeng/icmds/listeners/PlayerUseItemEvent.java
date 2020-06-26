package vn.BengBeng.icmds.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class PlayerUseItemEvent
	extends Event
	implements Cancellable {
	
	private static final HandlerList handler = new HandlerList();
	
	private boolean cancelled;
	private Player player;
	private ItemStack item;
	private boolean used;
	
	
	public PlayerUseItemEvent(Player player, ItemStack item, boolean used) {
		cancelled = false;
		this.player = player;
		this.item = item;
		this.used = used;
	}
	
	
	@Override
	public boolean isCancelled() {
		return cancelled;
	}
	
	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}
	
	
	/**
	 * Trả về người chơi sử dụng vật phẩm.
	 * @return
	 */
	public Player getPlayer() {
		return player;
	}
	
	/**
	 * Trả về vật phẩm mà người chơi sử dụng.
	 * @return
	 */
	public ItemStack getItemStack() {
		return item;
	}
	
	/**
	 * Trả về true nếu sử dụng vật phẩm thành công.
	 * @return
	 */
	public boolean isUsed() {
		return used;
	}
	
	
	@Override
	public HandlerList getHandlers() {
		return handler;
	}
	
	public static HandlerList getHandlerList() {
		return handler;
	}
	
}
