package net.auroramc.lobby;

import net.auroramc.lobby.listeners.ShutdownEventListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class AuroraMCLobby extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new ShutdownEventListener(), this);
    }

}
