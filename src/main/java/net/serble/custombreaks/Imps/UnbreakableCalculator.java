package net.serble.custombreaks.Imps;

import net.serble.custombreaks.Schemas.IBreakTimeCalculator;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class UnbreakableCalculator implements IBreakTimeCalculator {
    @Override
    public double getBreakTime(Block block, ItemStack tool, Player player) {
        return -1;
    }

    @Override
    public double getBreakDamage(Block block, ItemStack tool, Player player) {
        return 0;
    }
}
