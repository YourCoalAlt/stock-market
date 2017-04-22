package ca.maldahleh.stockmarket.inventories.portfolio;

import ca.maldahleh.stockmarket.StockMarket;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class PortfolioListener implements Listener {
    private StockMarket stockMarket;
    private PortfolioHandler portfolioHandler;

    public PortfolioListener(StockMarket stockMarket, PortfolioHandler portfolioHandler) {
        this.stockMarket = stockMarket;
        this.portfolioHandler = portfolioHandler;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onClick(final InventoryClickEvent e) {
        if (e.getInventory() != null && e.getInventory().getName().contains("Portfolio")
                && portfolioHandler.getPortfolioMap().containsKey(e.getWhoClicked().getUniqueId())) {
            e.setCancelled(true);

            if (e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.THIN_GLASS
                    && e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().hasDisplayName()) {
                String strippedDisplayName =
                        ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()
                                .replace("Page ", ""));

                final int newPage = Integer.parseInt(strippedDisplayName) - 1;
                final PortfolioObject portfolioObject =
                        portfolioHandler.getPortfolioMap().get(e.getWhoClicked().getUniqueId());
                e.getWhoClicked().closeInventory();

                Bukkit.getScheduler().runTaskLater(stockMarket, () -> {
                    e.getWhoClicked().openInventory(portfolioObject.getPageMap().get(newPage));
                    portfolioObject.setCurrentPage(portfolioObject.getCurrentPage() + 1);
                    portfolioHandler.getPortfolioMap().put(e.getWhoClicked().getUniqueId(), portfolioObject);
                }, 10L);
            }
        }
    }
}