package vn.BengBeng.icmds.hooks;

import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;
import vn.BengBeng.icmds.utils.Utils;

public class PermissionsExHook
	implements PermissionHook {
	
	@Override
	public String getRank(String name) {
		if(!Utils.isHooked("PermissionsEx")) {
			return "";
		}
		PermissionUser pUser = PermissionsEx.getUser(name);
		for(PermissionGroup groups : PermissionsEx.getPermissionManager().getGroupList()) {
			if(pUser.inGroup(groups)) {
				return groups.getName();
			}
		}
		return "";
	}
	
}
