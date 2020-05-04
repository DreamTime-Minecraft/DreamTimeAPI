package ru.sgk.dreamtimeapi.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.sgk.dreamtimeapi.gui.handlers.GUIItemHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

@SuppressWarnings("unused")
public class GUIItem {
    private final int index;
    private final ItemStack item;
    private boolean enchanted;
    private GUIItemHandler handler;
    private GUIInventory parentInv;

    /**
     * Внимание! Мета обнуляется!
     * @param item
     * @param slotIndex
     */
    public GUIItem(ItemStack item,int slotIndex) {
        this.index = slotIndex;
        this.item = item;
        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.STICK);
        this.setItemMeta(meta);
    }
    public GUIItem(ItemStack item,int x, int y) {
        int index = ((y-1)*9)+(x-1);
        this.index = index;
        this.item = item;
    }

    public void createHandler(GUIItemHandler handler)
    {
        this.handler = handler;
    }

    public void clearEnchantments()
    {
        if (item != null) {
            for (Enchantment e : item.getEnchantments().keySet()) {
                item.removeEnchantment(e);
            }
        }

    }

    public int getIndex() {
        return index;
    }

    /**
     * Устанавливает видимость того, что предмет зачарован
     */
    public void setEnchanted(boolean b)
    {
        clearEnchantments();
        this.enchanted = b;
        if (b) {
            if (item != null) {
                item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
            }
        }
    }


    public boolean isEnchanted() {
        return enchanted;
    }

    public ItemStack getItem()
    {
        return item;
    }

    public void handle(InventoryClickEvent e)
    {
        if (this.handler != null)
            this.handler.click(e);
    }

    public void setTitle(String title)
    {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + title);
        setItemMeta(meta);
    }

    public void setLore(List<String> lore)
    {
        ItemMeta meta = item.getItemMeta();
        if (lore != null) {
            List<String> newLore = new ArrayList<>();
            for (String s : lore) {
                newLore.add(ChatColor.RESET + s);
            }
            meta.setLore(newLore);
        } else {
            meta.setLore(null);
        }
        setItemMeta(meta);
    }

    public ItemMeta setPlaceholders(BiFunction<String, Player, String> placeholderFunction, Player p)
    {
        ItemMeta meta = item.getItemMeta().clone();
        String name = meta.getDisplayName();
        String newName = placeholderFunction.apply(name, p);
        meta.setDisplayName(newName);
        List<String> lore = (meta.getLore() == null) ? new ArrayList<>() : meta.getLore();
        List<String> newLore = new ArrayList<>();
        for (String s : lore) {
            newLore.add(placeholderFunction.apply(s, p));
        }
        meta.setLore(newLore);
        return meta;
    }

    public void setItemMeta(ItemMeta meta)
    {
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        updateInInventory();
    }

    public void setInventory(GUIInventory inv)
    {
        this.parentInv = inv;
        inv.getInv().setItem(this.index, this.item);
    }
    private void updateInInventory()
    {
        if(parentInv != null) {
            parentInv.getInv().setItem(this.index, this.item);
        }
    }
}
