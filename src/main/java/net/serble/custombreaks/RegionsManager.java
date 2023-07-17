package net.serble.custombreaks;

import net.serble.custombreaks.Schemas.IRegion;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class RegionsManager {
    private static final List<IRegion> regions = new ArrayList<>();

    public @Nullable IRegion getActiveRegionAt(Location loc, Player p) {
        for (IRegion region : regions) {
            if (region.isInsideArea(loc, p)) {
                return region;
            }
        }
        return null;
    }

    public void createRegion(IRegion region) {
        regions.add(region);
    }

}
