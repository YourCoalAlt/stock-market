package ca.maldahleh.stockmarket.stocks;

import ca.maldahleh.stockmarket.utils.Utils;

import java.util.Date;

public class Stocks {
    private int stocksID;
    private Date stocksDate;
    private boolean isConverted;
    private double initialSinglePrice;
    private String initalCurrency;
    private String symbol;
    private double symbolPrice;
    private int quantity;
    private double stockValue;
    private double brokerFees;
    private double totalPrice;

    public Stocks (int stocksID, int playerID, Date stocksDate, boolean isConverted, double initalSinglePrice,
                   String initalCurrency, String symbol, double symbolPrice, int quantity, double stockValue,
                   double brokerFees, double totalPrice) {
        this.stocksID = stocksID;
        this.isConverted = isConverted;
        this.initialSinglePrice = initalSinglePrice;
        this.initalCurrency = initalCurrency.toUpperCase();
        this.symbol = symbol.toUpperCase();
        this.symbolPrice = symbolPrice;
        this.quantity = quantity;
        this.stockValue = stockValue;
        this.brokerFees = brokerFees;
        this.totalPrice = totalPrice;
        this.stocksDate = stocksDate;
    }

    public int getID () { return stocksID; }

    public Date getDate () { return stocksDate; }

    public boolean isConverted () { return  isConverted; }

    public double getInitialSinglePrice () { return initialSinglePrice; }

    public String getInitalCurrency () { return  initalCurrency; }

    public String getSymbol () {
        return symbol;
    }

    public double getSymbolPrice () {
        return symbolPrice;
    }

    public int getQuantity(){
        return quantity;
    }

    public void setQuantity (int newQuantity) {
        quantity = newQuantity;
        stockValue = Double.parseDouble(Utils.formatDecimal((float) (quantity * symbolPrice)));
        totalPrice = Double.parseDouble(Utils.formatDecimal((float) (stockValue + brokerFees)));
    }

    public double getStockValue () {
        return stockValue;
    }

    public double getBrokerFees () {
        return brokerFees;
    }

    public double getTotalPrice () {
        return totalPrice;
    }
}