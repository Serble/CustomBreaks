package net.serble.custombreaks;

import net.serble.custombreaks.Imps.UnbreakableCalculator;
import net.serble.custombreaks.Imps.VanillaBreakTimeCalculator;
import net.serble.custombreaks.Schemas.IBreakTimeCalculator;
import net.serble.custombreaks.Schemas.IRegion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class BreakHandler implements Listener {
    private static final HashMap<Player, Block> isMining = new HashMap<>();
    private static final HashMap<Player, Double> breakTime = new HashMap<>();

    public void setPlayerMining(Player p, Block b) {
        if (b == null) {
            isMining.remove(p);
            return;
        }
        isMining.put(p, b);
        breakTime.put(p, 0.0);
    }

    public void resetPlayer(Player p) {
        isMining.remove(p);
        breakTime.remove(p);
    }

    private static void removeMineSpeed(Player p) {
        UUID uuid = UUID.fromString("55fced67-e92a-486e-9800-b47f202c4386");
        AttributeInstance instance = p.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        if (instance != null) {
            instance.getModifiers().stream().filter(m -> m.getUniqueId().equals(uuid)).findFirst().ifPresent(instance::removeModifier);
        }
    }

    private void checkInitPlayer(Player p) {
        if (Config.isEnabled(p.getWorld())) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 999999, 255, false, false));
            removeMineSpeed(p);
        }
    }

    private void deInitPlayer(Player p) {
        resetPlayer(p);
        p.removePotionEffect(PotionEffectType.SLOW_DIGGING);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        checkInitPlayer(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWorldChange(PlayerChangedWorldEvent e) {
        checkInitPlayer(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLeave(PlayerQuitEvent e) {
        if (Config.isEnabled(e.getPlayer().getWorld())) {
            deInitPlayer(e.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTeleport(PlayerTeleportEvent e) {
        if (Objects.requireNonNull(e.getFrom().getWorld()).getName().equals(Objects.requireNonNull(Objects.requireNonNull(e.getTo()).getWorld()).getName())) {
            return;  // Same world
        }
        deInitPlayer(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent e) {
        if (Config.isEnabled(e.getEntity().getWorld())) {
            deInitPlayer(e.getEntity());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent e) {
        checkInitPlayer(e.getPlayer());
    }

    public BreakHandler() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(CustomBreaks.getInstance(), () -> {
            if (!Config.isBenchmark()) {
                mineCalculator();
                return;
            }
            long startTime = System.currentTimeMillis();
            mineCalculator();
            int timeTaken = (int) (System.currentTimeMillis() - startTime);
            if (timeTaken > 0) {
                CustomBreaks.getInstance().getLogger().info("BreakHandler took " + timeTaken + "ms");
            }
        }, 0L, 1L);

        CustomBreaks.getInstance().getProtocolManager().addPacketListener(new BreakHandlerPacketManager(this));
    }

    private void mineCalculator() {
        for (HashMap.Entry<Player, Block> entry : isMining.entrySet()) {
            Player player = entry.getKey();
            Block block = entry.getValue();

            if (!Config.isEnabled(player.getWorld())) {
                resetPlayer(player);
                return;
            }

            IRegion region = CustomBreaks.getInstance().getRegionsManager().getActiveRegionAt(block.getLocation(), player);
            IBreakTimeCalculator breakTimeCalculator;
            if (region == null) {
                if (Config.getConfiguration().getBoolean("default-to-vanilla-break-time")) {
                    breakTimeCalculator = new VanillaBreakTimeCalculator(CustomBreaks.getInstance().getDefaultHardness());
                } else {
                    breakTimeCalculator = new UnbreakableCalculator();
                }
            } else {
                breakTimeCalculator = region.getBreakTimeCalculator(player);
            }
            double maxBreakTime = breakTimeCalculator.getBreakTime(block, Utils.getPlayerActiveTool(player), player);
            double breakDamage = breakTimeCalculator.getBreakDamage(block, Utils.getPlayerActiveTool(player), player);

            if (maxBreakTime < 0) {
                return;
            }

            // add 1 to break time
            breakTime.put(player, breakTime.get(player) + breakDamage);
            //player.sendMessage("Break time: " + breakTime.get(player) + " (x" + breakDamage + ") / " + maxBreakTime);

            // Set the break progress animation
            float breakProgress = (float) (breakTime.get(player) / maxBreakTime);
            if (breakProgress > 1) {
                breakProgress = 1;
            }

            player.sendBlockDamage(block.getLocation(), breakProgress);

            // if break time is greater than the max, break the block
            if (breakTime.get(player) >= maxBreakTime) {
                isMining.remove(player);
                breakTime.remove(player);
                breakBlock(block, player);
            }

        }
    }

    private void breakBlock(Block block, Player p) {
        BlockBreakEvent event = new BlockBreakEvent(block, p);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        if (event.isDropItems()) {
            block.breakNaturally(Utils.getPlayerActiveTool(p));
        } else {
            block.setType(Material.AIR);
        }
        p.getWorld().playSound(p.getLocation(), block.getType().createBlockData().getSoundGroup().getBreakSound(), 1, 1);
    }

}
