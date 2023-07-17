package net.serble.custombreaks.Imps;

import net.serble.custombreaks.Schemas.IBreakTimeCalculator;
import net.serble.custombreaks.Schemas.IHardnessMapper;
import net.serble.custombreaks.Schemas.ToolLevel;
import net.serble.custombreaks.Schemas.ToolType;
import net.serble.custombreaks.Utils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class VanillaBreakTimeCalculator implements IBreakTimeCalculator {
    private final IHardnessMapper hardnessMapper;

    public VanillaBreakTimeCalculator(IHardnessMapper hardnessMapper) {
        this.hardnessMapper = hardnessMapper;
    }

    @Override
    public double getBreakTime(Block block, ItemStack tool, Player player) {
        ToolType toolType = Utils.getToolType(tool);
        boolean isPreferredTool = toolType != ToolType.NONE && toolType == hardnessMapper.getPreferredTool(block.getType());
        double breakTimeSeconds = hardnessMapper.getHardnessFor(block.getType());
        breakTimeSeconds *= isPreferredTool ? 1.5 : 1.5;  // TODO: How tf does this work? How do i work out if a tool can break a block?

        double ticks = breakTimeSeconds * 20;

        // Round
        ticks = Math.round(ticks);

        return ticks;
    }

    @Override
    public double getBreakDamage(Block block, ItemStack tool, Player player) {
        ToolType toolType = Utils.getToolType(tool);
        boolean isPreferredTool = toolType != ToolType.NONE && toolType == hardnessMapper.getPreferredTool(block.getType());

        double digSpeedMultiplier = 1;
        ToolLevel toolLevel = Utils.getToolLevel(tool);

        switch (toolLevel) {
            case WOOD:
                if (!isPreferredTool) break;
                digSpeedMultiplier = 2;
                break;
            case STONE:
                if (!isPreferredTool) break;
                digSpeedMultiplier = 4;
                break;
            case IRON:
                if (!isPreferredTool) break;
                digSpeedMultiplier = 6;
                break;
            case DIAMOND:
                if (!isPreferredTool) break;
                digSpeedMultiplier = 8;
                break;
            case NETHERITE:
                if (!isPreferredTool) break;
                digSpeedMultiplier = 9;
                break;
            case GOLD:
                if (!isPreferredTool) break;
                digSpeedMultiplier = 12;
                break;
            case SHEARS:
                if (block.getType().name().equals("VINE") || block.getType().name().equals("GLOW_LICHEN")) {
                    // Keep at 1
                    break;
                }
                if (block.getType().name().endsWith("_WOOL")) {
                    digSpeedMultiplier = 5;
                    break;
                }
                if (block.getType().name().equals("COBWEB") || block.getType().name().endsWith("_LEAVES")) {
                    digSpeedMultiplier = 15;
                    break;
                }
                digSpeedMultiplier = 2;
                break;
        }

        if (toolType == ToolType.SWORD) {
            digSpeedMultiplier = 1.5;
            if (block.getType().name().equals("COBWEB")) {
                digSpeedMultiplier = 15;
            }
        }

        if (toolType != ToolType.NONE && isPreferredTool) {
            int efficiencyLevel = tool.getEnchantmentLevel(org.bukkit.enchantments.Enchantment.DIG_SPEED);
            if (efficiencyLevel != 0) {
                digSpeedMultiplier += 1 + (efficiencyLevel * efficiencyLevel);
            }
        }

        PotionEffect hasteEffect = player.getPotionEffect(org.bukkit.potion.PotionEffectType.FAST_DIGGING);
        if (hasteEffect != null) {
            // additional (20×level)% per level of Haste
            digSpeedMultiplier += hasteEffect.getAmplifier() * 0.2;
        }


        // If the player's head is underwater, and they are not wearing a helmet with the Aqua Affinity enchantment, breaking a block takes 5 times as long.
        if (player.getEyeLocation().getBlock().getType().name().endsWith("WATER") && (player.getInventory().getHelmet() == null || player.getInventory().getHelmet().getEnchantmentLevel(org.bukkit.enchantments.Enchantment.WATER_WORKER) == 0)) {
            digSpeedMultiplier /= 5;
        }

        // If the player's feet are not touching the ground, an additional 5x penalty is added
        if (!Utils.isGrounded(player)) {
            digSpeedMultiplier /= 5;
        }

        return digSpeedMultiplier;
    }
}
