package ca.maldahleh.stockmarket.stocks;

import java.util.Date;

public class Transactions {
    private int transactionID;
    private Date transactionDate;
    private String transactionType;
    private boolean isConverted;
    private double initialSinglePrice;
    private String initalCurrency;
    private String symbol;
    private double symbolPrice;
    private int quantity;
    private double stockValue;
    private double brokerFees;
    private double totalPrice;
    private double earnings;

    public Transactions (int transactionID, int playerID, String transactionType, Date transactionDate,
                         boolean isConverted, double initalSinglePrice, String initalCurrency, String symbol,
                         double symbolPrice, int quantity, double stockValue, double brokerFees, double totalPrice,
                         double earnings) {
        this.transactionID = transactionID;
        this.transactionType = transactionType;
        this.isConverted = isConverted;
        this.initialSinglePrice = initalSinglePrice;
        this.initalCurrency = initalCurrency.toUpperCase();
        this.symbol = symbol.toUpperCase();
        this.symbolPrice = symbolPrice;
        this.quantity = quantity;
        this.stockValue = stockValue;
        this.brokerFees = brokerFees;
        this.totalPrice = totalPrice;
        this.earnings = earnings;
        this.transactionDate = transactionDate;
    }

    public int getID () { return transactionID; }

    public Date getDate () { return transactionDate; }

    public String getTransactionType() { return transactionType; }

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
        stockValue = (quantity * symbolPrice);
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

    public double getEarnings () { return earnings; }
}