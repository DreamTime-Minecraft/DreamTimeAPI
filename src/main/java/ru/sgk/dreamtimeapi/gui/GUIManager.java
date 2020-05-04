package ru.sgk.dreamtimeapi.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GUIManager implements Listener {

    private Map<String, GUIInventory> inventories = new ConcurrentHashMap<>();


    public GUIManager(JavaPlugin plugin)
    {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e)
    {
        Player p = (Player)e.getWhoClicked();
        GUIInventory GUIInv = inventories.get(p.getOpenInventory().getTitle());

        if (GUIInv != null) {
            int slot = e.getSlot();
            GUIItem item = GUIInv.getItem(slot);
            if (item != null)
            {
                item.handle(e);
            }
        }
    }
//    @EventHandler
//    public void onOpen(InventoryOpenEvent e)
//    {
//        GUIInventory GUIInv = inventories.get(e.getView().getTitle());
//        if (GUIInv != null && GUIInv.hasHandler("open")) {
//            GUIInv.handleOpen(e);
//        }
//    }
    @EventHandler
    public void onClose(InventoryCloseEvent e)
    {
        GUIInventory GUIInv = inventories.get(e.getView().getTitle());
        if (GUIInv != null && GUIInv.hasHandler("close"))
            GUIInv.handleClose(e);
    }


    public GUIInventory addInventory(String title, int rows)
    {
        GUIInventory inv = new GUIInventory(title, rows);
        inventories.put(title, inv);
        return inventories.get(title);
    }

    public void removeInventory(String title)
    {

    }

    public GUIInventory getInventory(String title)
    {
        return inventories.get(title);
    }
}
