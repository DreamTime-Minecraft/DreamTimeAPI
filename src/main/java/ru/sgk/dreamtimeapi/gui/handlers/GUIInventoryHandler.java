package ru.sgk.dreamtimeapi.gui.handlers;

import org.bukkit.event.inventory.InventoryEvent;
import ru.sgk.dreamtimeapi.gui.GUIInventory;

public interface GUIInventoryHandler {
    public void handle(InventoryEvent event, GUIInventory inv);
}
