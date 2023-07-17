package net.serble.custombreaks;

import net.serble.custombreaks.Schemas.ToolLevel;
import net.serble.custombreaks.Schemas.ToolType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static ItemStack getPlayerActiveTool(Player p) {
        return p.getInventory().getItemInMainHand();
    }

    public static ToolType getToolType(Material material) {
        String name = material.name();
        if (name.endsWith("_PICKAXE")) {
            return ToolType.PICKAXE;
        }
        if (name.endsWith("_AXE")) {
            return ToolType.AXE;
        }
        if (name.endsWith("_SHOVEL")) {
            return ToolType.SHOVEL;
        }
        if (name.endsWith("_HOE")) {
            return ToolType.HOE;
        }
        if (name.endsWith("_SWORD")) {
            return ToolType.SWORD;
        }
        if (name.equalsIgnoreCase("SHEARS")) {
            return ToolType.SHEARS;
        }
        if (name.equalsIgnoreCase("FLINT_AND_STEEL")) {
            return ToolType.FLINT_AND_STEEL;
        }
        return ToolType.NONE;
    }

    public static ToolType getToolType(ItemStack itemStack) {
        if (itemStack == null) return ToolType.NONE;
        return getToolType(itemStack.getType());
    }

    public static ToolLevel getToolLevel(Material itemStack) {
        String name = itemStack.name();
        if (name.startsWith("WOODEN_")) {
            return ToolLevel.WOOD;
        }
        if (name.startsWith("STONE_")) {
            return ToolLevel.STONE;
        }
        if (name.startsWith("IRON_")) {
            return ToolLevel.IRON;
        }
        if (name.startsWith("DIAMOND_")) {
            return ToolLevel.DIAMOND;
        }
        if (name.startsWith("NETHERITE_")) {
            return ToolLevel.NETHERITE;
        }
        if (name.startsWith("GOLDEN_")) {
            return ToolLevel.GOLD;
        }
        if (name.startsWith("SHEARS")) {
            return ToolLevel.SHEARS;
        }
        return ToolLevel.NONE;
    }

    public static ToolLevel getToolLevel(ItemStack itemStack) {
        if (itemStack == null) return ToolLevel.NONE;
        return getToolLevel(itemStack.getType());
    }

    public static boolean isGrounded(Player p) {
        if (getBlocksPlayerIsStandingOn(p, 0).stream().noneMatch(Material::isSolid)) return false;
        // Check if they are perfectly on the ground (on a y coord that is a whole number or 0.5 within 0.1)
        double y = p.getLocation().getY();
        return (y % 1 == 0 || y % 1 == 0.5) && (y % 1 <= 0.1 || y % 1 >= 0.9);
    }

    public static List<Material> getBlocksPlayerIsStandingOn(Player p, int yOffset) {
        List<Material> materialList = new ArrayList<>();
        Location playerLocation = p.getLocation();
        double x = playerLocation.getX();
        double y = playerLocation.getY() + yOffset;
        double z = playerLocation.getZ();

        // get 4 corners of the player hitbox
        Location corner1 = new Location(playerLocation.getWorld(), x + 0.3, y-1, z + 0.3);
        Location corner2 = new Location(playerLocation.getWorld(), x - 0.3, y-1, z + 0.3);
        Location corner3 = new Location(playerLocation.getWorld(), x + 0.3, y-1, z - 0.3);
        Location corner4 = new Location(playerLocation.getWorld(), x - 0.3, y-1, z - 0.3);

        // add the blocks to the list
        materialList.add(corner1.getBlock().getType());
        materialList.add(corner2.getBlock().getType());
        materialList.add(corner3.getBlock().getType());
        materialList.add(corner4.getBlock().getType());
        return materialList;
    }

}
