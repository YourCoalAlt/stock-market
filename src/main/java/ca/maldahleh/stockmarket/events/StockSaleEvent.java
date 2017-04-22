package ca.maldahleh.stockmarket.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class StockSaleEvent extends Event {
    private static HandlerList handlerList = new HandlerList();

    private Player p;
    private String stockSymbol;
    private int quantity;
    private String stockValue;
    private String brokerFees;
    private String grandTotal;
    private String initalPurchase;
    private String netOnTransaction;

    public StockSaleEvent (Player p, String stockSymbol, int quantity, String stockValue, String brokerFees,
                           String grandTotal, String initalPurchase, String netOnTransaction) {
        this.p = p;
        this.stockSymbol = stockSymbol;
        this.quantity = quantity;
        this.stockValue = stockValue;
        this.brokerFees = brokerFees;
        this.grandTotal = grandTotal;
        this.initalPurchase = initalPurchase;
        this.netOnTransaction = netOnTransaction;
    }

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public Player getPlayer() {
        return p;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getStockValue() {
        return stockValue;
    }

    public String getBrokerFees() {
        return brokerFees;
    }

    public String getGrandTotal() {
        return grandTotal;
    }

    public String getInitialPurchase() { return initalPurchase; }

    public String getNetOnTransaction() { return netOnTransaction; }
}