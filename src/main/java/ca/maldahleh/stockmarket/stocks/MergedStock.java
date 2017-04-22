package ca.maldahleh.stockmarket.stocks;

public class MergedStock {
    private String symbol;
    private int quantity;

    public MergedStock(String symbol, int quantity) {
        this.symbol = symbol.toUpperCase();
        this.quantity = quantity;
    }

    public String getSymbol () {
        return symbol;
    }

    public int getQuantity(){
        return quantity;
    }

    public void setQuantity (int newQuantity) { quantity = newQuantity; }
}
