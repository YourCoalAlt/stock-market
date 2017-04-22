package ca.maldahleh.stockmarket.inventories.transactionhistory;

import ca.maldahleh.stockmarket.StockMarket;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class HistoryListener implements Listener {
    private StockMarket stockMarket;

    public HistoryListener (StockMarket stockMarket) { this.stockMarket = stockMarket; }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onClick (final InventoryClickEvent e) {
        if (e.getInventory() != null) {
            if (e.getInventory().getName().contains("History") && StockMarket.transactionMap.containsKey(e.getWhoClicked().getUniqueId())) {
                e.setCancelled(true);

                if (e.getCurrentItem() != null) {
                    if (e.getCurrentItem().getType() == Material.THIN_GLASS) {
                        if (e.getCurrentItem().getItemMeta().hasDisplayName()) {
                            String strippedDisplayName = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName().replace("Page ", ""));
                            int newPage = 1;
                            try {
                                newPage = Integer.parseInt(strippedDisplayName) - 1;
                            } catch (NumberFormatException e1) {
                                stockMarket.getLogger().info("Stock Market - Error parsing page number: ");
                                e1.printStackTrace();
                                return;
                            }

                            final HistoryObject historyObject = StockMarket.transactionMap.get(e.getWhoClicked().getUniqueId());
                            e.getWhoClicked().closeInventory();
                            final int finalNewPage = newPage;
                            Bukkit.getScheduler().runTaskLater(stockMarket, new Runnable() {
                                @Override
                                public void run() {
                                    e.getWhoClicked().openInventory(historyObject.getPageMap().get(finalNewPage));
                                    historyObject.setCurrentPage(historyObject.getCurrentPage() + 1);
                                    StockMarket.transactionMap.put(e.getWhoClicked().getUniqueId(), historyObject);
                                }
                            }, 10L);
                        }
                    }
                }
            }
        } else {
            e.setCancelled(true);
        }
    }
}
