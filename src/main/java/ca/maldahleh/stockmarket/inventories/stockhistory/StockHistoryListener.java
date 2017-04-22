package ca.maldahleh.stockmarket.inventories.stockhistory;

import ca.maldahleh.stockmarket.StockMarket;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class StockHistoryListener implements Listener {
    private StockMarket stockMarket;

    public StockHistoryListener(StockMarket stockMarket) { this.stockMarket = stockMarket; }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onClick (final InventoryClickEvent e) {
        if (e.getInventory() != null) {
            if (e.getInventory().getName().contains("Stock History")
                    && StockMarket.historyMap.containsKey(e.getWhoClicked().getUniqueId())) {
                e.setCancelled(true);

                if (e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.THIN_GLASS &&
                        e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().hasDisplayName()) {
                    String strippedDisplayName = ChatColor.stripColor(e.getCurrentItem().getItemMeta()
                            .getDisplayName().replace("Page ", ""));

                    final int newPage = Integer.parseInt(strippedDisplayName) - 1;
                    final StockHistoryObject historyObject = StockMarket.historyMap.get(e.getWhoClicked().getUniqueId());
                    e.getWhoClicked().closeInventory();
                    Bukkit.getScheduler().runTaskLater(stockMarket, () -> {
                        e.getWhoClicked().openInventory(historyObject.getPageMap().get(newPage));
                        historyObject.setCurrentPage(historyObject.getCurrentPage() + 1);
                        StockMarket.historyMap.put(e.getWhoClicked().getUniqueId(), historyObject);
                    }, 10L);
                }
            }
        }
    }
}