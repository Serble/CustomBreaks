package net.serble.custombreaks.Schemas;

import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.File;
import java.io.IOException;

public interface IHardnessMapper {
    double getHardnessFor(Material material);
    ToolType getPreferredTool(Material material);
    void load() throws IOException, InvalidConfigurationException;
    void load(File file) throws IOException, InvalidConfigurationException;
}
