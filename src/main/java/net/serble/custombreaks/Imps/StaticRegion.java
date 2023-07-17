package net.serble.custombreaks.Imps;

import net.serble.custombreaks.Schemas.IBreakTimeCalculator;
import net.serble.custombreaks.Schemas.IRegion;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class StaticRegion implements IRegion {
    private final Location pos1;
    private final Location pos2;
    private final IBreakTimeCalculator hardnessMappings;

    public StaticRegion(Location pos1, Location pos2, IBreakTimeCalculator hardnessMappings) {
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.hardnessMappings = hardnessMappings;
    }

    public Location getPos1() {
        return pos1;
    }

    public Location getPos2() {
        return pos2;
    }

    public IBreakTimeCalculator getBreakTimeCalculator(Player p) {
        return hardnessMappings;
    }

    public boolean isInsideArea(Location loc, Player p) {
        return loc.getX() >= Math.min(pos1.getX(), pos2.getX()) && loc.getX() <= Math.max(pos1.getX(), pos2.getX()) &&
                loc.getY() >= Math.min(pos1.getY(), pos2.getY()) && loc.getY() <= Math.max(pos1.getY(), pos2.getY()) &&
                loc.getZ() >= Math.min(pos1.getZ(), pos2.getZ()) && loc.getZ() <= Math.max(pos1.getZ(), pos2.getZ());
    }
}
