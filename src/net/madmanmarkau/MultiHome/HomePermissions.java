package net.madmanmarkau.MultiHome;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import ru.tehkode.permissions.bukkit.PermissionsEx;

import com.nijikokun.bukkit.Permissions.Permissions;

import com.platymuus.bukkit.permissions.Group;
import com.platymuus.bukkit.permissions.PermissionsPlugin;

import de.bananaco.bpermissions.api.ApiLayer;
import de.bananaco.bpermissions.api.util.CalculableType;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;

/**
*
* @author Sleaker
*
*/
public class HomePermissions {
	private static PermissionsHandler handler;
	private static Plugin permissionPlugin = null;


	private enum PermissionsHandler {
		PERMISSIONSEX, PERMISSIONS, PERMISSIONSBUKKIT, GROUPMANAGER, BPERMISSIONS, SUPERPERMS, NONE
	}

	public static boolean initialize(JavaPlugin plugin) {
		Plugin perm = Bukkit.getServer().getPluginManager().getPlugin("Permissions");
		Plugin permex = Bukkit.getServer().getPluginManager().getPlugin("PermissionsEx");
		Plugin bukkitperms = Bukkit.getServer().getPluginManager().getPlugin("PermissionsBukkit");
		Plugin bukkitperms1_1 = Bukkit.getServer().getPluginManager().getPlugin("PermissionsBukkit-1.1");
		Plugin groupmanager = Bukkit.getServer().getPluginManager().getPlugin("GroupManager");
		Plugin bPermissions = Bukkit.getServer().getPluginManager().getPlugin("bPermissions");

		if (permex != null) {
			permissionPlugin = permex;
			handler = PermissionsHandler.PERMISSIONSEX;
			Messaging.logInfo("Using PermissionsEx for permissions system.", plugin);
			return true;
		} else if (bukkitperms != null) {
			permissionPlugin = bukkitperms;
			handler = PermissionsHandler.PERMISSIONSBUKKIT;
			Messaging.logInfo("Using PermissionsBukkit for permissions system.", plugin);
			return true;
		} else if (bukkitperms1_1 != null) {
			permissionPlugin = bukkitperms1_1;
			handler = PermissionsHandler.PERMISSIONSBUKKIT;
			Messaging.logInfo("Using PermissionsBukkit for permissions system.", plugin);
			return true;
		} else if (perm != null) {
			permissionPlugin = perm;
			handler = PermissionsHandler.PERMISSIONS;
			Messaging.logInfo("Using Permissions for permissions system.", plugin);
			return true;
		} else if  (groupmanager != null) {
			permissionPlugin = groupmanager;
			handler = PermissionsHandler.GROUPMANAGER;
			Messaging.logInfo("Using GroupManager for permissions system.", plugin);
			return true;
		} else if (bPermissions != null) {
			permissionPlugin = bPermissions;
			handler = PermissionsHandler.BPERMISSIONS;
			Messaging.logInfo("Using bPermissions for permissions system.", plugin);
			return true;
		} else {
			handler = PermissionsHandler.SUPERPERMS;
			Messaging.logWarning("A permission plugin was not detected! Defaulting to CraftBukkit permissions system.", plugin);
			Messaging.logWarning("Groups disabled. All players defaulting to \"default\" group.", plugin);
			return true;
		}
	}

	public static boolean has(Player player, String permission) {
		boolean blnHasPermission;

		switch (handler) {
			case PERMISSIONSEX:
				blnHasPermission = PermissionsEx.getPermissionManager().has(player, permission);
				break;
			case PERMISSIONS:
				blnHasPermission = ((Permissions) permissionPlugin).getHandler().has(player, permission);
				break;
			case GROUPMANAGER:
			case BPERMISSIONS:
			case PERMISSIONSBUKKIT:
			case SUPERPERMS:
				blnHasPermission = player.hasPermission(permission);
				break;
			default:
				blnHasPermission = player.isOp();
				break;
		}

		return blnHasPermission;
	}

	public static String getGroup(String world, String player) {
		String[] groups = {};
		
		if (world != null && world.length() > 0 && player != null && player.length() > 0) {
			switch (handler) {
				case PERMISSIONSEX:
					groups = PermissionsEx.getPermissionManager().getUser(player).getGroupsNames();
					
					if (groups != null && groups.length > 0) {
						return groups[0];
					}
					break;
					
				case PERMISSIONS:
					groups = ((Permissions) permissionPlugin).getHandler().getGroups(world, player);
					
					if (groups != null && groups.length > 0) {
						return groups[0];
					}
					break;
					
				case PERMISSIONSBUKKIT:
					List<Group> playerGroups;
					
					playerGroups = ((PermissionsPlugin) permissionPlugin).getGroups(player);
					
					if (playerGroups != null && playerGroups.size() > 0) {
						return playerGroups.get(0).getName();
					}
					break;

				case GROUPMANAGER:
					AnjoPermissionsHandler handler = ((GroupManager) permissionPlugin).getWorldsHolder().getWorldPermissions(world);

					if (handler != null ){
						return handler.getGroup(player);
					} 
					break;
					
				case BPERMISSIONS:
					String[] bplayerGroups = ApiLayer.getGroups(world, CalculableType.USER, player);
					
					if (bplayerGroups != null && bplayerGroups.length > 0 ){
						return bplayerGroups[0];
					}
					break;

				case SUPERPERMS:
				case NONE:
					break; // Groups not supported.
			}
		}

		return "default";
	}
}
