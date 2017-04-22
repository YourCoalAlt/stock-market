package ca.maldahleh.stockmarket.api;

import ca.maldahleh.stockmarket.StockMarket;
import ca.maldahleh.stockmarket.stocks.StockPlayer;
import ca.maldahleh.stockmarket.stocks.Stocks;
import ca.maldahleh.stockmarket.utils.Utils;
import ca.maldahleh.stockmarket.utils.ValueComparator;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.quotes.fx.FxQuote;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class StockMarketAPI {
    public ExecutorService executorService = Executors.newCachedThreadPool();

    public String getPortfolioValue(final UUID pUUID) {
        final ArrayList<List<Stocks>> toPopulate = new ArrayList<>();
        final ArrayList<String[]> symbolList = new ArrayList<>();
        final String[] portfolioValueFormatted = {"0.00"};

        Future<String> future = executorService.submit(() -> {
            toPopulate.add(0, StockMarket.getMySQL().getAllOwnedStocks(pUUID));
            boolean toContinue = true;
            if (toPopulate.get(0).isEmpty()) {
                toContinue = false;
            }

            if (toContinue) {
                List<String> symbolListToInsert = new ArrayList<>();
                for (Stocks toUse : toPopulate.get(0)) {
                    symbolListToInsert.add(toUse.getSymbol().toUpperCase());
                }

                String[] stockArray = new String[symbolListToInsert.size()];
                stockArray = symbolListToInsert.toArray(stockArray);

                symbolList.add(0, stockArray);
                final ArrayList<Map<String, Stock>> stockData = new ArrayList<>();
                try {
                    stockData.add(0, YahooFinance.get(symbolList.get(0)));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (stockData.get(0) == null || stockData.get(0).isEmpty()) {
                    toContinue = false;
                }

                if (toContinue) {
                    final HashMap<Integer, Double> portfolioPriceMap = new HashMap<>();

                    for (Stocks stock : toPopulate.get(0)) {
                        double multipliedValue = 0;
                        boolean converted = false;
                        if (StockMarket.getInstance().getLocalConfig().convertToUSD) {
                            if (!stockData.get(0).get(stock.getSymbol().toUpperCase()).getCurrency().equalsIgnoreCase("USD")) {
                                final FxQuote[] forexStock = new FxQuote[1];

                                try {
                                    forexStock[0] = YahooFinance.getFx(stockData.get(0).get(stock.getSymbol().toUpperCase()).getCurrency().toUpperCase() + "USD=X");
                                } catch (IOException ignored) {
                                }

                                multipliedValue = (stockData.get(0).get(stock.getSymbol().toUpperCase()).getQuote().getPrice().floatValue() * forexStock[0].getPrice().doubleValue());

                                if (StockMarket.getInstance().getLocalConfig().multiplier != 0) {
                                    multipliedValue = multipliedValue * StockMarket.getInstance().getLocalConfig().multiplier;
                                }

                                converted = true;
                            }
                        }

                        if (!converted) {
                            multipliedValue = stockData.get(0).get(stock.getSymbol().toUpperCase()).getQuote().getPrice().doubleValue();

                            if (StockMarket.getInstance().getLocalConfig().multiplier != 0) {
                                multipliedValue = multipliedValue * StockMarket.getInstance().getLocalConfig().multiplier;
                            }
                        }

                        portfolioPriceMap.put(stock.getID(), multipliedValue * stock.getQuantity());
                    }

                    double portfolioValue = 0;
                    for (Object o : portfolioPriceMap.entrySet()) {
                        Map.Entry entry = (Map.Entry) o;
                        portfolioValue += Double.valueOf(entry.getValue().toString());
                    }

                    portfolioValueFormatted[0] = Utils.formatDecimal((float) portfolioValue);
                }
            }

            return portfolioValueFormatted[0];
        });

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getProfitMargin(final UUID pUUID) {
        final ArrayList<List<Stocks>> toPopulate = new ArrayList<>();
        final ArrayList<String[]> symbolList = new ArrayList<>();
        final String[] profitMarginFormatted = {"0.00"};

        Future<String> future = executorService.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                toPopulate.add(0, StockMarket.getMySQL().getAllOwnedStocks(pUUID));
                boolean toContinue = true;
                if (toPopulate.get(0).isEmpty()) {
                    toContinue = false;
                }

                if (toContinue) {
                    List<String> symbolListToInsert = new ArrayList<>();
                    for (Stocks toUse : toPopulate.get(0)) {
                        symbolListToInsert.add(toUse.getSymbol().toUpperCase());
                    }

                    String[] stockArray = new String[symbolListToInsert.size()];
                    stockArray = symbolListToInsert.toArray(stockArray);

                    symbolList.add(0, stockArray);
                    final ArrayList<Map<String, Stock>> stockData = new ArrayList<>();
                    try {
                        stockData.add(0, YahooFinance.get(symbolList.get(0)));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (stockData.get(0) == null || stockData.get(0).isEmpty()) {
                        toContinue = false;
                    }

                    if (toContinue) {
                        final HashMap<Integer, Double> portfolioPriceMap = new HashMap<>();
                        double purchaseValue = 0;

                        for (Stocks stock : toPopulate.get(0)) {
                            purchaseValue += stock.getTotalPrice();
                            double multipliedValue = 0;
                            boolean converted = false;
                            if (StockMarket.getInstance().getLocalConfig().convertToUSD) {
                                if (!stockData.get(0).get(stock.getSymbol().toUpperCase()).getCurrency().equalsIgnoreCase("USD")) {
                                    final FxQuote[] forexStock = new FxQuote[1];

                                    try {
                                        forexStock[0] = YahooFinance.getFx(stockData.get(0).get(stock.getSymbol().toUpperCase()).getCurrency().toUpperCase() + "USD=X");
                                    } catch (IOException ignored) {
                                    }

                                    multipliedValue = (stockData.get(0).get(stock.getSymbol().toUpperCase()).getQuote().getPrice().floatValue() * forexStock[0].getPrice().doubleValue());

                                    if (StockMarket.getInstance().getLocalConfig().multiplier != 0) {
                                        multipliedValue = multipliedValue * StockMarket.getInstance().getLocalConfig().multiplier;
                                    }

                                    converted = true;
                                }
                            }

                            if (!converted) {
                                multipliedValue = stockData.get(0).get(stock.getSymbol().toUpperCase()).getQuote().getPrice().doubleValue();

                                if (StockMarket.getInstance().getLocalConfig().multiplier != 0) {
                                    multipliedValue = multipliedValue * StockMarket.getInstance().getLocalConfig().multiplier;
                                }
                            }

                            portfolioPriceMap.put(stock.getID(), multipliedValue * stock.getQuantity());
                        }

                        double portfolioValue = 0;
                        for (Object o : portfolioPriceMap.entrySet()) {
                            Map.Entry entry = (Map.Entry) o;
                            portfolioValue += Double.valueOf(entry.getValue().toString());
                        }

                        profitMarginFormatted[0] = Utils.formatDecimal((float) (portfolioValue - purchaseValue));
                    }
                }

                return profitMarginFormatted[0];
            }
        });

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Map<UUID, Double> getSortedPortfolioValues() {
        Future<Map<UUID, Double>> future = executorService.submit(() -> {
            List<StockPlayer> toUse;
            toUse = StockMarket.getMySQL().getAllStockPlayers();

            boolean toContinue = true;
            if (toUse.isEmpty()) {
                toContinue = false;
            }

            Map<UUID, Double> hashMap = new HashMap<>();
            Map<UUID, Double> sortedMap = null;
            if (toContinue) {
                for (StockPlayer stockPlayer : toUse) {
                    hashMap.put(stockPlayer.getPlayerUUID(),
                            Double.valueOf(StockMarket.getStockMarketAPI().getPortfolioValue(stockPlayer.getPlayerUUID())));
                }

                sortedMap = sortByValue(hashMap);
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

    public List<Stocks> getPlayerOwnedStocks (final UUID pUUID) {
        Future<List<Stocks>> future = executorService.submit(() -> {
            List<Stocks> playerOwnedStocks;
            playerOwnedStocks = StockMarket.getMySQL().getAllOwnedStocks(pUUID);

            return playerOwnedStocks;
        });

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Map sortByValue (Map unsortedMap) {
        Map sortedMap = new TreeMap(new ValueComparator(unsortedMap));
        sortedMap.putAll(unsortedMap);
        return sortedMap;
    }
}
