package ca.maldahleh.stockmarket.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemStackBuilder {
    private ItemStack is;

    public ItemStackBuilder(Material m, int amount) {
        is = new ItemStack(m, amount);
    }

    public ItemStackBuilder(Material m, int amount, byte durability) {
        is = new ItemStack(m, amount, durability);
    }

    public ItemStackBuilder setDisplayName(String displayName) {
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        is.setItemMeta(im);
        return this;
    }

    public ItemStackBuilder addLoreLine(String line) {
        ItemMeta im = is.getItemMeta();

        List<String> lore;
        if (im.hasLore()) {
            lore = new ArrayList<>(im.getLore());
        } else {
            lore = new ArrayList<>();
        }

        lore.add(ChatColor.translateAlternateColorCodes('&', line));

        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    public ItemStack buildItemStack() {
        return is;
    }
}