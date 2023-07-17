package net.serble.custombreaks.Imps;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import net.serble.custombreaks.Schemas.IBreakTimeCalculator;
import net.serble.custombreaks.Schemas.IRegion;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WorldGuardRegion implements IRegion {

    @Override
    public IBreakTimeCalculator getBreakTimeCalculator(Player p) {
        return new UnbreakableCalculator();
    }

    @Override
    public boolean isInsideArea(Location loc, Player p) {
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(p);
        com.sk89q.worldedit.util.Location worldGuardLoc = new com.sk89q.worldedit.util.Location(localPlayer.getExtent(), loc.getX(), loc.getY(), loc.getZ());
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();

        // Can't build
        return !query.testState(worldGuardLoc, localPlayer, Flags.BUILD);
    }
}
