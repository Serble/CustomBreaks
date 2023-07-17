package net.serble.custombreaks.Schemas;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerAttemptBreakBlockEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final Block block;
    private boolean cancelled = false;

    public PlayerAttemptBreakBlockEvent(Player player, Block block) {
        this.player = player;
        this.block = block;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public Block getBlock() {
        return block;
    }

    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
