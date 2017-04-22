package ca.maldahleh.stockmarket.inventories.portfolio;

import ca.maldahleh.stockmarket.StockMarket;
import ca.maldahleh.stockmarket.stocks.MergedStock;
import ca.maldahleh.stockmarket.stocks.Stocks;
import ca.maldahleh.stockmarket.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.quotes.fx.FxQuote;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class PortfolioObject {
    private PortfolioHandler portfolioHandler;
    private UUID playerUUID;
    private String playerName;
    private String targetPlayer;
    private int currentPage;
    private List<Inventory> pageMap = new ArrayList<>();

    PortfolioObject(PortfolioHandler portfolioHandler, UUID playerUUID, String playerName, String targetPlayer,
                    boolean combinedObject) {
        this.portfolioHandler = portfolioHandler;
        this.playerUUID = playerUUID;
        this.playerName = playerName;
        this.targetPlayer = targetPlayer;
        this.currentPage = 1;

        if (!combinedObject) {
            populatePageMap(targetPlayer);
            return;
        }

        populateCombinedMap(targetPlayer);
    }

    int getCurrentPage() {
        return currentPage;
    }

    void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    List<Inventory> getPageMap() { return pageMap; }

    private void populatePageMap(final String targetName) {
        Bukkit.getScheduler().runTask(StockMarket.getInstance(), () -> {
            final ArrayList<List<Stocks>> toPopulate = new ArrayList<>();
            final ArrayList<String[]> symbolList = new ArrayList<>();
            Player executor = null;

            if (Bukkit.getPlayer(playerUUID) != null) {
                executor = Bukkit.getPlayer(playerUUID);
            }

            final Player finalExecutor = executor;

            Bukkit.getScheduler().runTaskAsynchronously(StockMarket.getInstance(), () -> {
                toPopulate.add(0, StockMarket.getMySQL().getAllOwnedStocks(targetName));

                Bukkit.getScheduler().runTask(StockMarket.getInstance(), () -> {
                    boolean toContinue = true;
                    if (toPopulate.get(0).isEmpty()) {
                        if (finalExecutor != null) {
                            finalExecutor.sendMessage(StockMarket.getInstance().getLocalConfig().noStocksTargetPlayer);
                            StockMarket.portfolioMap.remove(finalExecutor.getUniqueId());
                        }

                        toContinue = false;
                    }

                    if (toContinue && (finalExecutor != null)) {
                        List<String> symbolListToInsert = new ArrayList<>();
                        for (Stocks toUse : toPopulate.get(0)) {
                            symbolListToInsert.add(toUse.getSymbol().toUpperCase());
                        }

                        String[] stockArray = new String[symbolListToInsert.size()];
                        stockArray = symbolListToInsert.toArray(stockArray);

                        symbolList.add(0, stockArray);

                        Bukkit.getScheduler().runTaskAsynchronously(StockMarket.getInstance(), new Runnable() {
                            @Override
                            public void run() {
                                final ArrayList<Map<String, Stock>> stockData = new ArrayList<>();
                                try {
                                    stockData.add(0, YahooFinance.get(symbolList.get(0)));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                boolean toContinue = true;

                                if (stockData.get(0) == null || stockData.get(0).isEmpty()) {
                                    finalExecutor.sendMessage(StockMarket.getInstance().getLocalConfig().errorOccured);
                                    toContinue = false;
                                }

                                if (toContinue) {
                                    int totalPages = 0;

                                    if (toPopulate.get(0).size() <= 45) {
                                        totalPages = 1;
                                    } else {
                                        int toPopulateSize = toPopulate.get(0).size();
                                        int pages = (toPopulateSize / 45) + 1;
                                        totalPages += pages;
                                    }

                                    final int finalTotalPages = totalPages;
                                    Bukkit.getScheduler().runTask(StockMarket.getInstance(), new Runnable() {
                                        @Override
                                        public void run() {
                                            final List<Stocks> toUse = toPopulate.get(0);
                                            final Map<String, Stock> stockMap = stockData.get(0);
                                            final HashMap<Integer, Double> priceMap = new HashMap<>();
                                            final HashMap<Integer, Double> portfolioPriceMap = new HashMap<>();

                                            Bukkit.getScheduler().runTaskAsynchronously(StockMarket.getInstance(), new Runnable() {
                                                @Override
                                                public void run() {
                                                    for (Stocks stock : toPopulate.get(0)) {
                                                        boolean converted = false;
                                                        double multipliedValue = 0;
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

                                                        priceMap.put(stock.getID(), multipliedValue);
                                                        portfolioPriceMap.put(stock.getID(), multipliedValue * stock.getQuantity());
                                                    }

                                                    Bukkit.getScheduler().runTask(StockMarket.getInstance(), new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            double portfolioValue = 0;
                                                            double purchaseValue = 0;
                                                            Inventory i;
                                                            int z = 0;

                                                            for (Object o : portfolioPriceMap.entrySet()) {
                                                                Map.Entry entry = (Map.Entry) o;
                                                                portfolioValue += Double.valueOf(entry.getValue().toString());
                                                            }

                                                            for (Stocks stock : toUse) {
                                                                purchaseValue += stock.getTotalPrice();
                                                            }

                                                            String portfolioValueFormatted = Utils.formatDecimal((float) portfolioValue);

                                                            for (int x = 1; x < (finalTotalPages + 1); x++) {
                                                                i = Bukkit.createInventory(null, 54, ChatColor.GOLD + "" + ChatColor.BOLD + "Portfolio " + ChatColor.GRAY + targetPlayer);

                                                                int thisLoop = 0;
                                                                boolean exit = false;
                                                                SimpleDateFormat dateFormat = new SimpleDateFormat();
                                                                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

                                                                do {
                                                                    final String timeStamp;
                                                                    timeStamp = dateFormat.format(toUse.get(z).getDate());
                                                                    final Stock stock = stockMap.get(toUse.get(z).getSymbol().toUpperCase());

                                                                    double singlePrice = priceMap.get(toUse.get(z).getID());
                                                                    double totalValue = singlePrice * toUse.get(z).getQuantity();
                                                                    String totalValueFormatted = Utils.formatDecimal((float) totalValue);
                                                                    String toReplaceNet;

                                                                    double differencePrice = (totalValue - toUse.get(z).getStockValue());
                                                                    if (differencePrice >= 0) {
                                                                        toReplaceNet = (ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "+ " + ChatColor.GREEN + Utils.formatDecimal((float) differencePrice));
                                                                    } else {
                                                                        toReplaceNet = (ChatColor.RED + Utils.formatDecimal((float) differencePrice));
                                                                    }

                                                                    Utils.createItem(Material.BOOK, i, thisLoop, ChatColor.GRAY + "" + ChatColor.BOLD + stock.getName(), Arrays.asList(ChatColor.GRAY + "" + ChatColor.BOLD + "Symbol:",
                                                                            ChatColor.GOLD + stock.getSymbol().toUpperCase(), ChatColor.GRAY + "" + ChatColor.BOLD + "Purchase Date:", ChatColor.GOLD + timeStamp, ChatColor.GRAY + "" + ChatColor.BOLD + "Quantity:", ChatColor.GOLD + String.valueOf(toUse.get(z).getQuantity()),
                                                                            ChatColor.GRAY + "" + ChatColor.BOLD + "Current Value:", ChatColor.GOLD + totalValueFormatted + " " + StockMarket.getInstance().getLocalConfig().serverCurrency,
                                                                            ChatColor.GRAY + "" + ChatColor.BOLD + "Purchase Value:", ChatColor.GOLD + Utils.formatDecimal((float) toUse.get(z).getStockValue()) + " " + StockMarket.getInstance().getLocalConfig().serverCurrency, ChatColor.GRAY + "" + ChatColor.BOLD + "Net:",
                                                                            ChatColor.GOLD + toReplaceNet + " " + StockMarket.getInstance().getLocalConfig().serverCurrency));

                                                                    z++;
                                                                    thisLoop++;
                                                                    if ((z >= (45 * x)) || (z >= toUse.size())) {
                                                                        exit = true;
                                                                    }
                                                                } while (!exit);

                                                                if (x == 1) {
                                                                    Utils.createItem(Material.GLASS, i, 45, ChatColor.GRAY + "" + ChatColor.BOLD + "No Pages", Arrays.asList(ChatColor.GOLD + "This is the first page, as a", ChatColor.GOLD + "result there are no previous pages."));
                                                                    Utils.createItem(Material.EMERALD, i, 49, ChatColor.GRAY + "" + ChatColor.BOLD + "Portfolio Value", ChatColor.GOLD + portfolioValueFormatted + " " + StockMarket.getInstance().getLocalConfig().serverCurrency);
                                                                    if (finalTotalPages == 1) {
                                                                        Utils.createItem(Material.GLASS, i, 53, ChatColor.GRAY + "" + ChatColor.BOLD + "No Pages", Arrays.asList(ChatColor.GOLD + "This is the last page, as a", ChatColor.GOLD + "result there are no next pages."));
                                                                    } else {
                                                                        Utils.createItem(Material.THIN_GLASS, i, 53, ChatColor.GRAY + "" + ChatColor.BOLD + "Page " + (x + 1), ChatColor.GOLD + "Go to the next page. (" + (x + 1) + " / " + finalTotalPages + ")");
                                                                    }
                                                                } else if (x == finalTotalPages) {
                                                                    Utils.createItem(Material.THIN_GLASS, i, 45, ChatColor.GRAY + "" + ChatColor.BOLD + "Page " + (x - 1), ChatColor.GOLD + "Go to the previous page.");
                                                                    Utils.createItem(Material.EMERALD, i, 49, ChatColor.GRAY + "" + ChatColor.BOLD + "Portfolio Value", ChatColor.GOLD + portfolioValueFormatted + " " + StockMarket.getInstance().getLocalConfig().serverCurrency);
                                                                    Utils.createItem(Material.GLASS, i, 53, ChatColor.GRAY + "" + ChatColor.BOLD + "No Pages", Arrays.asList(ChatColor.GOLD + "This is the last page, as a", ChatColor.GOLD + "result there are no next pages."));
                                                                } else {
                                                                    Utils.createItem(Material.THIN_GLASS, i, 45, ChatColor.GRAY + "" + ChatColor.BOLD + "Page " + (x - 1), ChatColor.GOLD + "Go to the previous page. (" + (x - 1) + " / " + finalTotalPages + ")");
                                                                    Utils.createItem(Material.EMERALD, i, 49, ChatColor.GRAY + "" + ChatColor.BOLD + "Portfolio Value", ChatColor.GOLD + portfolioValueFormatted + " " + StockMarket.getInstance().getLocalConfig().serverCurrency);
                                                                    Utils.createItem(Material.THIN_GLASS, i, 53, ChatColor.GRAY + "" + ChatColor.BOLD + "Page " + (x + 1), ChatColor.GOLD + "Go to the next page. (" + (x + 1) + " / " + finalTotalPages + ")");
                                                                }

                                                                Utils.createItem(Material.ENCHANTED_BOOK, i, 47, ChatColor.GRAY + "" + ChatColor.BOLD + "Values", Arrays.asList(ChatColor.GRAY + "" + ChatColor.BOLD + "Purchase Value:", ChatColor.GOLD + Utils.formatDecimal((float) purchaseValue) + " " + StockMarket.getInstance().getLocalConfig().serverCurrency, ChatColor.GRAY + "" + ChatColor.BOLD + "Market Value:", ChatColor.GOLD + Utils.formatDecimal((float) portfolioValue) + " " + StockMarket.getInstance().getLocalConfig().serverCurrency));
                                                                Utils.createItem(Material.ENCHANTED_BOOK, i, 48, ChatColor.GRAY + "" + ChatColor.BOLD + "Holdings on Page", ChatColor.GOLD + "" + thisLoop);
                                                                Utils.createItem(Material.ENCHANTED_BOOK, i, 50, ChatColor.GRAY + "" + ChatColor.BOLD + "Total Holdings", ChatColor.GOLD + "" + toUse.size());
                                                                Utils.createItem(Material.ENCHANTED_BOOK, i, 51, ChatColor.GRAY + "" + ChatColor.BOLD + "Profit Margin", ChatColor.GOLD + "" + ChatColor.GOLD + Utils.formatDecimal((float) (portfolioValue - purchaseValue)) + " " + StockMarket.getInstance().getLocalConfig().serverCurrency);

                                                                if (pageMap.size() == 0) {
                                                                    pageMap.add(0, i);
                                                                } else {
                                                                    pageMap.add(pageMap.size(), i);
                                                                }
                                                            }

                                                            finalExecutor.openInventory(pageMap.get(0));
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
            });
        });
    }

    private void populateCombinedMap(final String targetName) {
        Bukkit.getScheduler().runTask(StockMarket.getInstance(), new Runnable() {
            @Override
            public void run() {
                final ArrayList<List<MergedStock>> toPopulate = new ArrayList<>();
                Player executor = null;

                if (Bukkit.getPlayer(playerUUID) != null) {
                    executor = Bukkit.getPlayer(playerUUID);
                }

                final Player finalExecutor = executor;
                Bukkit.getScheduler().runTaskAsynchronously(StockMarket.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        HashMap<String, MergedStock> toLoad = StockMarket.getMySQL().getAllOwnedStocksCombined(targetName);
                        List<MergedStock> toCombine = new ArrayList<>();

                        for (Map.Entry e : toLoad.entrySet()) {
                            toCombine.add((MergedStock) e.getValue());
                        }

                        toPopulate.add(toCombine);

                        Bukkit.getScheduler().runTask(StockMarket.getInstance(), new Runnable() {
                            @Override
                            public void run() {
                                boolean toContinue = true;
                                if (toPopulate.get(0).isEmpty()) {
                                    if (finalExecutor != null) {
                                        finalExecutor.sendMessage(StockMarket.getInstance().getLocalConfig().noStocksTargetPlayer);
                                        StockMarket.portfolioMap.remove(finalExecutor.getUniqueId());
                                    }

                                    toContinue = false;
                                }

                                if (toContinue && (finalExecutor != null)) {
                                    Bukkit.getScheduler().runTaskAsynchronously(StockMarket.getInstance(), new Runnable() {
                                        @Override
                                        public void run() {
                                            int totalPages = 0;

                                            if (toPopulate.get(0).size() <= 45) {
                                                totalPages = 1;
                                            } else {
                                                int toPopulateSize = toPopulate.get(0).size();
                                                int pages = (toPopulateSize / 45) + 1;
                                                totalPages += pages;
                                            }

                                            final int finalTotalPages = totalPages;
                                            Bukkit.getScheduler().runTask(StockMarket.getInstance(), new Runnable() {
                                                @Override
                                                public void run() {
                                                    Inventory i;
                                                    int z = 0;

                                                    for (int x = 1; x < (finalTotalPages + 1); x++) {
                                                        i = Bukkit.createInventory(null, 54, ChatColor.GOLD + "" + ChatColor.BOLD + "Portfolio " + ChatColor.GRAY + targetPlayer);

                                                        int thisLoop = 0;
                                                        boolean exit = false;

                                                        do {

                                                            Utils.createItem(Material.BOOK, i, thisLoop, ChatColor.GRAY + "" + ChatColor.BOLD + toPopulate.get(0).get(z).getSymbol().toUpperCase(), Arrays.asList(ChatColor.GRAY + "" + ChatColor.BOLD + "Quantity:",
                                                                    ChatColor.GOLD + String.valueOf(toPopulate.get(0).get(z).getQuantity())));

                                                            z++;
                                                            thisLoop++;
                                                            if ((z >= (45 * x)) || (z >= toPopulate.get(0).size())) {
                                                                exit = true;
                                                            }
                                                        } while (!exit);

                                                        if (x == 1) {
                                                            Utils.createItem(Material.GLASS, i, 45, ChatColor.GRAY + "" + ChatColor.BOLD + "No Pages", Arrays.asList(ChatColor.GOLD + "This is the first page, as a", ChatColor.GOLD + "result there are no previous pages."));
                                                            if (finalTotalPages == 1) {
                                                                Utils.createItem(Material.GLASS, i, 53, ChatColor.GRAY + "" + ChatColor.BOLD + "No Pages", Arrays.asList(ChatColor.GOLD + "This is the last page, as a", ChatColor.GOLD + "result there are no next pages."));
                                                            } else {
                                                                Utils.createItem(Material.THIN_GLASS, i, 53, ChatColor.GRAY + "" + ChatColor.BOLD + "Page " + (x + 1), ChatColor.GOLD + "Go to the next page. (" + (x + 1) + " / " + finalTotalPages + ")");
                                                            }
                                                        } else if (x == finalTotalPages) {
                                                            Utils.createItem(Material.THIN_GLASS, i, 45, ChatColor.GRAY + "" + ChatColor.BOLD + "Page " + (x - 1), ChatColor.GOLD + "Go to the previous page.");
                                                            Utils.createItem(Material.GLASS, i, 53, ChatColor.GRAY + "" + ChatColor.BOLD + "No Pages", Arrays.asList(ChatColor.GOLD + "This is the last page, as a", ChatColor.GOLD + "result there are no next pages."));
                                                        } else {
                                                            Utils.createItem(Material.THIN_GLASS, i, 45, ChatColor.GRAY + "" + ChatColor.BOLD + "Page " + (x - 1), ChatColor.GOLD + "Go to the previous page. (" + (x - 1) + " / " + finalTotalPages + ")");
                                                            Utils.createItem(Material.THIN_GLASS, i, 53, ChatColor.GRAY + "" + ChatColor.BOLD + "Page " + (x + 1), ChatColor.GOLD + "Go to the next page. (" + (x + 1) + " / " + finalTotalPages + ")");
                                                        }

                                                        Utils.createItem(Material.ENCHANTED_BOOK, i, 48, ChatColor.GRAY + "" + ChatColor.BOLD + "Holdings on Page", ChatColor.GOLD + "" + thisLoop);
                                                        Utils.createItem(Material.ENCHANTED_BOOK, i, 50, ChatColor.GRAY + "" + ChatColor.BOLD + "Total Holdings", ChatColor.GOLD + "" + toPopulate.get(0).size());

                                                        if (pageMap.size() == 0) {
                                                            pageMap.add(0, i);
                                                        } else {
                                                            pageMap.add(pageMap.size(), i);
                                                        }
                                                    }

                                                    finalExecutor.openInventory(pageMap.get(0));
                                                }
                                            });
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
            }
        });
    }
}
