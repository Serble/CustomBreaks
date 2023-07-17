package net.serble.custombreaks;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import net.serble.custombreaks.Imps.WorldGuardRegion;
import net.serble.custombreaks.Imps.YmlHardnessMappings;
import net.serble.custombreaks.Schemas.IHardnessMapper;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class CustomBreaks extends JavaPlugin {

    private static CustomBreaks instance;
    private IHardnessMapper defaultHardness;
    private RegionsManager regionsManager;
    private BreakHandler breakHandler;
    private ProtocolManager protocolManager;


    public static CustomBreaks getInstance() {
        return instance;
    }

    public RegionsManager getRegionsManager() {
        return regionsManager;
    }

    public IHardnessMapper getDefaultHardness() {
        return defaultHardness;
    }

    public BreakHandler getBreakHandler() {
        return breakHandler;
    }

    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }

    @Override
    public void onEnable() {
        instance = this;

        Config.load();
        Bukkit.getLogger().info("Loaded config.");

        regionsManager = new RegionsManager();
        protocolManager = ProtocolLibrary.getProtocolManager();
        breakHandler = new BreakHandler();

        saveResource("hardness.yml", false);
        saveResource("config.yml", false);

        // Load default hardness values
        defaultHardness = new YmlHardnessMappings();
        try {
            defaultHardness.load();
        } catch (Exception e) {
            Bukkit.getLogger().severe("Default hardness values failed to load.");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        checkEnableWorldGuard();
        Bukkit.getServer().getPluginManager().registerEvents(breakHandler, this);
        Bukkit.getLogger().info("Custom Breaks has been enabled!");
    }

    private void checkEnableWorldGuard() {
        if (!Config.getConfiguration().getBoolean("worldguard")) {
            Bukkit.getLogger().info("WorldGuard is disabled.");
            return;
        }

        if (Bukkit.getPluginManager().getPlugin("WorldGuard") == null) {
            Bukkit.getLogger().severe("WorldGuard is not installed.");
            return;
        }

        Bukkit.getLogger().info("WorldGuard is enabled.");
        regionsManager.createRegion(new WorldGuardRegion());
    }

}