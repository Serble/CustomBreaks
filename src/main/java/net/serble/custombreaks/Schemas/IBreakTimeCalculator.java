package net.serble.custombreaks.Schemas;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface IBreakTimeCalculator {
    /*
        @param material The material of the block being broken
        @param tool The tool being used to break the block
        @return The time in ticks it takes to break the block
     */
    double getBreakTime(Block block, ItemStack tool, Player player);

    double getBreakDamage(Block block, ItemStack tool, Player player);
}
