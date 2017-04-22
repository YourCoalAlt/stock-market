package ca.maldahleh.stockmarket.api;

import ca.maldahleh.stockmarket.StockMarket;
import ca.maldahleh.stockmarket.stocks.StockPlayer;
import ca.maldahleh.stockmarket.stocks.Stocks;
import ca.maldahleh.stockmarket.utils.Utils;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.quotes.fx.FxQuote;

import java.util.*;
import java.util.concurrent.*;

public class StockMarketAPI {
    private StockMarket stockMarket;
    private ExecutorService executorService;

    public StockMarketAPI(StockMarket stockMarket) {
        this.stockMarket = stockMarket;
        this.executorService = Executors.newCachedThreadPool();
    }

    /**
     * Returns a formatted string of the portfolio value of the given player with the specified UUID.
     *
     * @param pUUID the UUID of the player to be looked up
     * @param playerStocks the list of player owned stocks if already established
     * @return a formatted string containing the portfolio value, if not found value will be 0
     */
    public String getPortfolioValue(final UUID pUUID, final List<Stocks> playerStocks) {
        Future<String> future = executorService.submit(() -> {
            List<Stocks> toPopulate;
            if (pUUID == null) {
                toPopulate = playerStocks;
            } else {
                 toPopulate = stockMarket.getMySQL().getAllOwnedStocks(pUUID);
            }

            double portfolioValue = 0;
            if (!toPopulate.isEmpty()) {
                List<String> symbolListToInsert = new ArrayList<>();
                for (Stocks toUse : toPopulate) {
                    symbolListToInsert.add(toUse.getSymbol().toUpperCase());
                }

                Map<String, Stock> stockData = YahooFinance.get((String[]) symbolListToInsert.toArray());
                if (!(stockData == null || stockData.isEmpty())) {
                    for (Stocks stock : toPopulate) {
                        double multipliedValue;

                        if (stockMarket.getLocalConfig().isConvertToUSD() && !stockData.get(stock.getSymbol()
                                .toUpperCase()).getCurrency().equalsIgnoreCase("USD")) {
                            FxQuote forexStock = YahooFinance.getFx(stockData
                                    .get(stock.getSymbol().toUpperCase()).getCurrency().toUpperCase() + "USD=X");

                            multipliedValue = stockData.get(stock.getSymbol().toUpperCase()).getQuote()
                                    .getPrice().floatValue() * forexStock.getPrice().doubleValue() * stockMarket
                                    .getLocalConfig().getMultiplier();
                        } else {
                            multipliedValue = stockData.get(stock.getSymbol().toUpperCase())
                                    .getQuote().getPrice().doubleValue() * stockMarket.getLocalConfig().getMultiplier();
                        }

                        portfolioValue = portfolioValue + multipliedValue * stock.getQuantity();
                    }
                }
            }

            return Utils.formatDecimal(portfolioValue);
        });

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Returns the profit margin of the specified player on their portfolio.
     *
     * @param pUUID the UUID of the player to lookup
     * @return a formatted string of the profit margin
     */
    public String getProfitMargin(final UUID pUUID) {
        Future<String> future = executorService.submit(() -> {
            List<Stocks> toPopulate = stockMarket.getMySQL().getAllOwnedStocks(pUUID);
            String profitMarginFormatted = "0.00";

            if (!toPopulate.isEmpty()) {
                List<String> symbolListToInsert = new ArrayList<>();
                for (Stocks toUse : toPopulate) {
                    symbolListToInsert.add(toUse.getSymbol().toUpperCase());
                }

                Map<String, Stock> stockData = YahooFinance.get((String[]) symbolListToInsert.toArray());
                if (!(stockData == null || stockData.isEmpty())) {
                    double purchaseValue = 0;
                    for (Stocks stock : toPopulate) {
                        purchaseValue += stock.getTotalPrice();
                    }

                    profitMarginFormatted = Utils.formatDecimal(
                            Double.valueOf(getPortfolioValue(null, toPopulate)) - purchaseValue);
                }
            }

            return profitMarginFormatted;
        });

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Returns a sorted map containing the top portfolio values.
     *
     * @return a sorted map containing the top portfolio values, null if none.
     */
    public Map<UUID, Double> getSortedPortfolioValues() {
        Future<Map<UUID, Double>> future = executorService.submit(() -> {
            List<StockPlayer> toUse = stockMarket.getMySQL().getAllStockPlayers();

            Map<UUID, Double> sortedMap = null;
            if (!toUse.isEmpty()) {
                Map<UUID, Double> unsortedMap = new HashMap<>();
                for (StockPlayer stockPlayer : toUse) {
                    unsortedMap.put(stockPlayer.getPlayerUUID(), Double
                            .valueOf(getPortfolioValue(stockPlayer.getPlayerUUID(), null)));
                }

                sortedMap = Utils.sortByValue(unsortedMap);
            }

            return sortedMap;
        });

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Return a list of all the stocks a player owns.
     *
     * @param pUUID UUID of the target player
     * @return a list of stocks owned by the player
     */
    public List<Stocks> getPlayerOwnedStocks(final UUID pUUID) {
        Future<List<Stocks>> future = executorService.submit(() -> stockMarket.getMySQL().getAllOwnedStocks(pUUID));

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }
}