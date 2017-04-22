package ca.maldahleh.stockmarket.inventories.transactionhistory;

import ca.maldahleh.stockmarket.StockMarket;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class HistoryHandler {
    public static void openInventory (Player p, String targetPlayer) {
        if (!StockMarket.transactionMap.containsKey(p.getUniqueId())) {
            StockMarket.transactionMap.put(p.getUniqueId(), new HistoryObject(p.getUniqueId(), p.getName(), targetPlayer));
        } else {
            StockMarket.transactionMap.remove(p.getUniqueId());
            StockMarket.transactionMap.put(p.getUniqueId(), new HistoryObject(p.getUniqueId(), p.getName(), targetPlayer));
        }
    }
}
