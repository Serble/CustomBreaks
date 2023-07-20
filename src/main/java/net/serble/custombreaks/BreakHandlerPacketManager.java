package net.serble.custombreaks;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.*;
import net.serble.custombreaks.Imps.VanillaBreakTimeCalculator;
import net.serble.custombreaks.Schemas.PlayerAttemptBreakBlockEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class BreakHandlerPacketManager extends PacketAdapter {
    private final BreakHandler breakHandler;

    public BreakHandlerPacketManager(BreakHandler bh) {
        super(CustomBreaks.getInstance(), ListenerPriority.HIGHEST, new ArrayList<>() {
            {
                add(PacketType.Play.Client.BLOCK_DIG);
            }
        }, ListenerOptions.SYNC);
        breakHandler = bh;
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        if (!Config.isEnabled(event.getPlayer().getWorld())) {
            return;
        }

        if (!Config.isBenchmark()) {
            executeWrapper(event);
            return;
        }
        Long start = System.currentTimeMillis();
        executeWrapper(event);
        Long end = System.currentTimeMillis();
        int timeTaken = (int) (end - start);
        if (timeTaken > 0) {
            Bukkit.getLogger().warning("Packet took " + timeTaken + "ms to execute");
        }
    }

    private void executeWrapper(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        int action = packet.getPlayerDigTypes().read(0).ordinal();

        Location blockLoc = packet.getBlockPositionModifier().read(0).toLocation(event.getPlayer().getWorld());
        Block block = blockLoc.getBlock();

        if (action == 1) {  // Cancel digging
            breakHandler.resetPlayer(event.getPlayer());
            return;
        }

        if (action == 2) {  // Finish digging (Shouldn't happen)
            breakHandler.resetPlayer(event.getPlayer());
            player.sendBlockChange(blockLoc, block.getBlockData());
            event.setCancelled(true);
            return;
        }

        if (action != 0) {  // Start digging
            return;
        }
        event.setCancelled(true);  // Don't let the server handle this

        // Insta break check
        // If the client thinks it can insta break then it won't send a stop mining packet
        // We need to tell it to go fuck itself
        VanillaBreakTimeCalculator vanillaBreakTimeCalculator = new VanillaBreakTimeCalculator(CustomBreaks.getInstance().getDefaultHardness());
        double damage = vanillaBreakTimeCalculator.getBreakDamage(block, Utils.getPlayerActiveTool(player), player);
        double maxDamage = vanillaBreakTimeCalculator.getBreakTime(block, Utils.getPlayerActiveTool(player), player);
        if (damage >= maxDamage) {
            // INSTA-BREAK
            player.sendBlockChange(blockLoc, block.getBlockData());
        }

        // Run event sync and wait for it to finish
        if (playerStartBreaking(player, block)) {
            return;
        }

        breakHandler.setPlayerMining(player, block);
    }

    // Returns: whether the event was cancelled
    private boolean playerStartBreaking(Player player, Block block) {
        PlayerAttemptBreakBlockEvent attemptBreakBlockEvent = new PlayerAttemptBreakBlockEvent(player, block);
        Bukkit.getServer().getPluginManager().callEvent(attemptBreakBlockEvent);

        if (attemptBreakBlockEvent.isCancelled()) {
            return true;
        }

        breakHandler.setPlayerMining(player, block);
        return false;
    }
}
