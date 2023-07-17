package net.serble.custombreaks;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class Config {
    private static FileConfiguration configuration;
    private static boolean cacheYmlFiles;
    private static boolean benchmark;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void load() {
        CustomBreaks.getInstance().getDataFolder().mkdirs();
        File file = new File(CustomBreaks.getInstance().getDataFolder(), "config.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                Bukkit.getLogger().severe("Failed to create config.yml");
                e.printStackTrace();
                Bukkit.getPluginManager().disablePlugin(CustomBreaks.getInstance());
                return;
            }
        }
        configuration = new YamlConfiguration();
        try {
            configuration.load(file);
        } catch (Exception e) {
            Bukkit.getLogger().severe("Failed to load config.yml");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(CustomBreaks.getInstance());
            return;
        }
        AtomicBoolean changed = loadDefaults();

        // CACHED OPTIONS
        cacheYmlFiles = configuration.getBoolean("cache-yml-files");
        benchmark = configuration.getBoolean("benchmark");

        if (!changed.get()) {
            return;
        }
        try {
            configuration.save(file);
        } catch (Exception e) {
            Bukkit.getLogger().severe("Failed to save config.yml");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(CustomBreaks.getInstance());
        }
    }

    private static AtomicBoolean loadDefaults() {
        AtomicBoolean changed = new AtomicBoolean(false);

        checkOrSet(changed, "default-hardness", -1d);
        checkOrSet(changed, "enabled-worlds", new String[] { "custombreaks" });
        checkOrSet(changed, "worldguard", true);
        checkOrSet(changed, "default-to-vanilla-break-time", true);
        checkOrSet(changed, "cache-yml-files", true);
        checkOrSet(changed, "benchmark", false);

        return changed;
    }

    private static void checkOrSet(AtomicBoolean changed, String key, Object value) {
        if (!configuration.isSet(key)) {
            if (value instanceof Map) {
                configuration.createSection(key, (Map<?, ?>) value);
            } else {
                configuration.set(key, value);
            }
            changed.set(true);
        }
    }

    public static FileConfiguration getConfiguration() {
        return configuration;
    }

    public static boolean isEnabled(String world) {
        return getConfiguration().getStringList("enabled-worlds").contains(world);
    }

    public static boolean isEnabled(World world) {
        return isEnabled(world.getName());
    }

    public static boolean isCacheYmlFiles() {
        return cacheYmlFiles;
    }

    public static boolean isBenchmark() {
        return benchmark;
    }

}
