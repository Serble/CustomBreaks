package net.serble.custombreaks.Imps;

import net.serble.custombreaks.Schemas.IBreakTimeCalculator;
import net.serble.custombreaks.Schemas.IRegion;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Objects;

public class WorldRegion implements IRegion {
    private final World world;
    private final IBreakTimeCalculator breakTimeCalculator;

    @Override
    public IBreakTimeCalculator getBreakTimeCalculator(Player p) {
        return breakTimeCalculator;
    }

    @Override
    public boolean isInsideArea(Location loc, Player p) {
        return Objects.requireNonNull(loc.getWorld()).getName().equals(world.getName());
    }

    public WorldRegion(World world, IBreakTimeCalculator breakTimeCalculator) {
        this.world = world;
        this.breakTimeCalculator = breakTimeCalculator;
    }
}
