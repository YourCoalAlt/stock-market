package ca.maldahleh.stockmarket.utils;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.*;

public class Utils {
    public static String[] splitArray (String toSplit) throws NumberFormatException {
        return toSplit.split(",");
    }

    public static String formatLargeNumber (double number) {
        DecimalFormat moneyFormat = new DecimalFormat("#,###,###,###");
        return moneyFormat.format(number);
    }

    public static String formatDecimal (double number) {
        return String.format(Locale.ENGLISH, "%.2f", number);
    }

    public static boolean isNumber (String string) {
        try { Integer.parseInt(string); } catch (NumberFormatException e) { return false; }
        return true;
    }

    public static Map sortByValue(Map unsortedMap) {
        Map sortedMap = new TreeMap(new ValueComparator(unsortedMap));
        sortedMap.putAll(unsortedMap);
        return sortedMap;
    }

    public static void createItem (Material material, Inventory inventoryName, int itemSlot, String displayName, String lore) {
        ItemStack itemCreated = new ItemStack(material);
        ItemMeta itemMeta = itemCreated.getItemMeta();
        itemMeta.setDisplayName(displayName);
        ArrayList<String> loreArray = new ArrayList<>();
        loreArray.add(lore);
        itemMeta.setLore(loreArray);
        itemCreated.setItemMeta(itemMeta);

        inventoryName.setItem(itemSlot, itemCreated);
    }

    public static void createItem (Material material, Inventory inventoryName, int itemSlot, String displayName, List<String> lore) {
        ItemStack itemCreated = new ItemStack(material);
        ItemMeta itemMeta = itemCreated.getItemMeta();
        itemMeta.setDisplayName(displayName);
        itemMeta.setLore(lore);
        itemCreated.setItemMeta(itemMeta);

        inventoryName.setItem(itemSlot, itemCreated);
    }
}