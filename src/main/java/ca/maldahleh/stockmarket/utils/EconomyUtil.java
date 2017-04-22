package ca.maldahleh.stockmarket.utils;

import ca.maldahleh.stockmarket.StockMarket;

import org.bukkit.Bukkit;

import java.util.UUID;

public class EconomyUtil {
    public static boolean hasMoney (UUID playerUUID, double amount) {
        return StockMarket.getEcon().has(Bukkit.getOfflinePlayer(playerUUID), amount);
    }

    public static void removeMoney (UUID playerUUID, double amount) {
        StockMarket.getEcon().withdrawPlayer(Bukkit.getOfflinePlayer(playerUUID), amount);
    }

    public static void addMoney (UUID playerUUID, double amount) {
        StockMarket.getEcon().depositPlayer(Bukkit.getOfflinePlayer(playerUUID), amount);
    }
}