package net.serble.custombreaks.Imps;

import net.serble.custombreaks.Config;
import net.serble.custombreaks.CustomBreaks;
import net.serble.custombreaks.Schemas.IHardnessMapper;
import net.serble.custombreaks.Schemas.ToolType;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class YmlHardnessMappings implements IHardnessMapper {
    private FileConfiguration configuration;
    private final HashMap<Material, Double> hardnessCache = new HashMap<>();
    private final HashMap<Material, ToolType> toolCache = new HashMap<>();

    public double getHardnessFor(Material material) {
        if (hardnessCache.containsKey(material) && Config.isCacheYmlFiles()) {
            return hardnessCache.get(material);
        }

        ConfigurationSection sec = getConfigSection(material);
        if (sec == null) {
            return Config.getConfiguration().getDouble("default-hardness");
        }
        double hardness = sec.getDouble("hardness");
        hardnessCache.put(material, hardness);
        return hardness;
    }

    public ToolType getPreferredTool(Material material) {
        if (toolCache.containsKey(material) && Config.isCacheYmlFiles()) {
            return toolCache.get(material);
        }

        ConfigurationSection sec = getConfigSection(material);
        if (sec == null) {
            return ToolType.NONE;
        }
        String val = sec.getString("tool");
        if (val == null) {
            return ToolType.NONE;
        }
        ToolType type = ToolType.valueOf(val);
        toolCache.put(material, type);
        return type;
    }

    private ConfigurationSection getConfigSection(Material material) {
        Set<String> keys = getConfiguration().getKeys(false);
        for (String key : keys) {
            ConfigurationSection sec = getConfiguration().getConfigurationSection(key);
            if (sec == null) {
                continue;
            }
            String type = sec.getString("type");
            if (type == null) {
                continue;
            }
            String value = sec.getString("value");
            if (value == null) {
                continue;
            }
            switch (type) {
                case "exact":
                    if (value.equalsIgnoreCase(material.name())) {
                        return sec;
                    }
                    break;

                case "contains":
                    if (material.name().contains(value)) {
                        return sec;
                    }
                    break;

                case "starts-with":
                    if (material.name().startsWith(value)) {
                        return sec;
                    }
                    break;

                case "ends-with":
                    if (material.name().endsWith(value)) {
                        return sec;
                    }
                    break;

                case "regex":
                    if (material.name().matches(value)) {
                        return sec;
                    }
                    break;
            }
        }
        return null;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void load(File file) throws IOException, InvalidConfigurationException {
        CustomBreaks.getInstance().getDataFolder().mkdirs();
        file.createNewFile();
        configuration = new YamlConfiguration();
        configuration.load(file);
    }

    public void load() throws IOException, InvalidConfigurationException {
        load(new File(CustomBreaks.getInstance().getDataFolder(), "hardness.yml"));
    }

    private void checkOrSet(AtomicBoolean changed, String key, Object value) {
        if (!configuration.isSet(key)) {
            if (value instanceof Map) {
                configuration.createSection(key, (Map<?, ?>) value);
            } else {
                configuration.set(key, value);
            }
            changed.set(true);
        }
    }

    public FileConfiguration getConfiguration() {
        return configuration;
    }
}
