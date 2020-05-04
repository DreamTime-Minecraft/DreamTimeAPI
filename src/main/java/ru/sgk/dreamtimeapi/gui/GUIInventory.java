package ru.sgk.dreamtimeapi.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import ru.sgk.dreamtimeapi.gui.handlers.GUIInventoryHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

public class GUIInventory {
    private Inventory inv;
    private List<GUIItem> items;
    private Map<String, GUIInventoryHandler> handlers = new ConcurrentHashMap<>();
    private BiFunction<String, Player, String>  placeholderFunction;
    private String title;
    /**
     * @param title Название инвентаря
     * @param rows Количество строчек
     */
    public GUIInventory(String title, int rows) {
        this.title = title;
        this.inv = Bukkit.createInventory(null, rows*9, title);
        Player p = null;
        items = new ArrayList<>();
    }

    /**
     * x и y начинаются с 1!
     * @return GUIItem соответствующий слоту. null, если айтема в слоту нет
     */
    public GUIItem getItem(int x, int y)
    {
        int index = ((y-1)*9)+(x-1);
        return getItem(index);
    }

    /**
     * index начинается с нуля!
     * @return GUIItem соответствующий слоту. null, если айтема в слоту нет
     */
    public GUIItem getItem(int index)
    {
        for (GUIItem item : items) {
            if (item.getIndex() == index) {
                return item;
            }
        }
        return null;
    }

    public void addItem(GUIItem item)
    {
        item.setInventory(this);
        this.items.add(item);
    }


    public GUIItem addItem(ItemStack item, int x, int y)
    {
        int index = ((y-1)*9)+(x-1);
        GUIItem gItem = new GUIItem(item, index);
        if (slotEmpty(index)) {
            addItem(gItem);
        }
        return gItem;
    }

    public boolean slotEmpty(int index)
    {
        for (GUIItem item : items)
        {
            if (item.getIndex() == index){
                return false;
            }
        }
        return true;
    }
    public boolean slotEmpty(int x, int y)
    {
        int index = ((y-1)*9)+(x-1);
        return slotEmpty(index);
    }

    public void open(Player p)
    {
        updateItems();
        Inventory inv = Bukkit.createInventory(null, this.inv.getSize(), this.title);
        for (GUIItem item : items) {
            inv.setItem(item.getIndex(), item.getItem().clone());
        }
        InventoryView view = p.openInventory(inv);
        handleOpen(new InventoryOpenEvent(view));
        p.updateInventory();
    }


    public void setCloseHandler(GUIInventoryHandler handler)
    {
        handlers.put("close", handler);
    }
    public void setOpenHandler(GUIInventoryHandler handler)
    {
        handlers.put("open", handler);
    }

    public void handleClose(InventoryCloseEvent e)
    {
        GUIInventoryHandler handler = handlers.get("close");
        if (handler != null) {
            handler.handle(e, this);

        }
    }
    public void handleOpen(InventoryOpenEvent e)
    {
        if (placeholderFunction != null)
        {

            for (GUIItem i : items)
            {
                // Устанавливаем плейсхолдеры
                e.getInventory().getItem(i.getIndex()).setItemMeta(i.setPlaceholders(this.placeholderFunction, (Player)e.getPlayer()));
            }
        }
        GUIInventoryHandler handler = handlers.get("open");
        if (handler != null) {
            handler.handle(e, this);

        }
    }

    public boolean hasHandler(String handlerName)
    {
        return handlers.containsKey(handlerName);
    }

    public List<GUIItem> getItems() {
        return items;
    }

    public Inventory getInv() {
        return inv;
    }

    public void setInv(Inventory inv) {
        this.inv = inv;
    }

    public void updateItems()
    {
        for (GUIItem item : items) {
            item.setInventory(this);
        }
    }

    public void setPlaceholderFunction(BiFunction<String, Player, String> placeholderFunction) {
        this.placeholderFunction = placeholderFunction;
    }

    public BiFunction<String, Player, String>  getPlaceholderFunction() {
        return placeholderFunction;
    }
}
