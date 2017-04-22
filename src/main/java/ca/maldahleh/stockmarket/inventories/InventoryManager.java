package ca.maldahleh.stockmarket.inventories;

import ca.maldahleh.stockmarket.StockMarket;
import ca.maldahleh.stockmarket.inventories.portfolio.PortfolioHandler;
import ca.maldahleh.stockmarket.inventories.stockhistory.StockHistoryHandler;
import ca.maldahleh.stockmarket.inventories.transactionhistory.HistoryHandler;

public class InventoryManager {
    private StockMarket stockMarket;

    private HistoryHandler historyHandler;
    private PortfolioHandler portfolioHandler;
    private StockHistoryHandler stockHistoryHandler;

    public InventoryManager(StockMarket stockMarket) {
        this.stockMarket = stockMarket;

        this.historyHandler = new HistoryHandler();
        this.portfolioHandler = new PortfolioHandler();
        this.stockHistoryHandler = new StockHistoryHandler();
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
}
