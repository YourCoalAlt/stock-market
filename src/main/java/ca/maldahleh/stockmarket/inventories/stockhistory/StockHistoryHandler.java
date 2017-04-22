package ca.maldahleh.stockmarket.inventories.stockhistory;

import ca.maldahleh.stockmarket.StockMarket;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class StockHistoryHandler {
    public static void openInventory (Player p, String targetStock) {
        if (!StockMarket.historyMap.containsKey(p.getUniqueId())) {
            StockMarket.historyMap.put(p.getUniqueId(), new StockHistoryObject(p.getUniqueId(), p.getName(), targetStock));
        } else {
            StockMarket.historyMap.remove(p.getUniqueId());
            StockMarket.historyMap.put(p.getUniqueId(), new StockHistoryObject(p.getUniqueId(), p.getName(), targetStock));
        }
    }
}
