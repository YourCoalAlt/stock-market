package ca.maldahleh.stockmarket.inventories;

import ca.maldahleh.stockmarket.StockMarket;
import ca.maldahleh.stockmarket.inventories.information.ListInventory;
import ca.maldahleh.stockmarket.inventories.information.TutorialInventory;
import ca.maldahleh.stockmarket.inventories.portfolio.PortfolioHandler;
import ca.maldahleh.stockmarket.inventories.stockhistory.StockHistoryHandler;
import ca.maldahleh.stockmarket.inventories.transactionhistory.HistoryHandler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class InventoryManager {
    private StockMarket stockMarket;

    private HistoryHandler historyHandler;
    private PortfolioHandler portfolioHandler;
    private StockHistoryHandler stockHistoryHandler;

    private ListInventory listInventory;
    private TutorialInventory tutorialInventory;

    public InventoryManager(StockMarket stockMarket) {
        this.stockMarket = stockMarket;

        this.historyHandler = new HistoryHandler();
        this.portfolioHandler = new PortfolioHandler();
        this.stockHistoryHandler = new StockHistoryHandler();

        this.listInventory = new ListInventory();
        this.tutorialInventory = new TutorialInventory();
    }

    public HistoryHandler getHistoryHandler() {
        return historyHandler;
    }

    public PortfolioHandler getPortfolioHandler() {
        return portfolioHandler;
    }

    public StockHistoryHandler getStockHistoryHandler() {
        return stockHistoryHandler;
    }

    public void openListInventory(Player toOpen, boolean delayedOpen) {
        openInventory(toOpen, delayedOpen, listInventory.getInventory());
    }

    public void openTutorialInventory(Player toOpen, boolean delayedOpen) {
        openInventory(toOpen, delayedOpen, tutorialInventory.getInventory());
    }

    private void openInventory(Player toOpen, boolean delayedOpen, Inventory inventory) {
        if (delayedOpen) {
            Bukkit.getScheduler().runTaskLater(stockMarket, () -> toOpen.openInventory(inventory), 10L);
            return;
        }

        toOpen.openInventory(inventory);
    }
}
