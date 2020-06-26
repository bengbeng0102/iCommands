package vn.BengBeng.icmds.hooks;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;

public class LuckPermsHook
	implements PermissionHook {
	
	@Override
	public String getRank(String name) {
		User user = LuckPermsProvider.get().getUserManager().getUser(name);
		return user.getPrimaryGroup();
	}
	
}
