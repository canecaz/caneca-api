package me.caneca.api;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public static Main getInstance() {
        return Main.getPlugin(Main.class);
    }

    private LuckPerms luckAPI;

    private int permissionId = 0;
    private boolean tagsAPI = false;

    public boolean isLuck() {
        return permissionId == 2;
    }

    public boolean isTagsAPI() {
        return tagsAPI;
    }

    public LuckPerms getLuckAPI() {
        return luckAPI;
    }

    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage("[" + getDescription().getName() + "] §aPlugin enabled!");
        getServer().getScheduler().runTaskLater(this, () -> {
            if (checkNull("PermissionsEx")) permissionId = 1;
            else if (checkNull("LuckPerms")) {
                permissionId = 2;
                luckAPI = LuckPermsProvider.get();
            }
            if (checkNull("MusicTags")) tagsAPI = true;
        }, 1L);
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    private boolean checkNull(String plugin) {
        return getServer().getPluginManager().getPlugin(plugin) != null;
    }
}
