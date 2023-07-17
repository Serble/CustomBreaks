package net.serble.custombreaks.Schemas;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface IRegion {
    IBreakTimeCalculator getBreakTimeCalculator(Player p);
    boolean isInsideArea(Location loc, Player p);
}
