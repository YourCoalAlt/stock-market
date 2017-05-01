package ca.maldahleh.stockmarket.utils;

import ca.maldahleh.stockmarket.StockMarket;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.*;

public class Utils {
    public static String formatLargeNumber (double number) {
        DecimalFormat moneyFormat = new DecimalFormat("#,###,###,###");
        return moneyFormat.format(number);
    }

    public static String formatDecimal (double number) {
        return StockMarket.getEcon().format(number);
    }

    public static boolean isNumber (String string) {
        try { Integer.parseInt(string); } catch (NumberFormatException e) { return false; }
        return true;
    }
}