package ca.maldahleh.stockmarket.utils;

import ca.maldahleh.stockmarket.StockMarket;
import ca.maldahleh.stockmarket.stocks.StockPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.quotes.fx.FxQuote;

import java.io.IOException;
import java.util.*;

public class SharedUtils {
    public static void displayBrokerInventory(final Player commandSender, boolean npcIsSpawned, String inventoryName) {
        if (npcIsSpawned && inventoryName.equalsIgnoreCase(ChatColor.GOLD + "" + ChatColor.BOLD + "Stock Broker")) {
            Inventory i = Bukkit.createInventory(null, 45, ChatColor.GOLD + "" + ChatColor.BOLD + "Stock Broker");
            Utils.createItem(Material.BOOK, i, 11, ChatColor.GOLD + "" + ChatColor.BOLD + "Help", ChatColor.GRAY + "View the help menu.");
            Utils.createItem(Material.BOOK, i, 12, ChatColor.GOLD + "" + ChatColor.BOLD + "Tutorial", ChatColor.GRAY + "View the tutorial.");
            Utils.createItem(Material.BOOK, i, 13, ChatColor.GOLD + "" + ChatColor.BOLD + "Stock Lookup", Arrays.asList(ChatColor.GRAY + "Lookup a Stock.", ChatColor.GRAY + "Ex: AAPL"));
            Utils.createItem(Material.BOOK, i, 14, ChatColor.GOLD + "" + ChatColor.BOLD + "Forex Lookup", Arrays.asList(ChatColor.GRAY + "Lookup a Forex Symbol.", ChatColor.GRAY + "Ex: EURUSD"));
            Utils.createItem(Material.BOOK, i, 15, ChatColor.GOLD + "" + ChatColor.BOLD + "List", ChatColor.GRAY + "View a list of stocks.");
            Utils.createItem(Material.BOOK, i, 20, ChatColor.GOLD + "" + ChatColor.BOLD + "Buy a Stock", ChatColor.GRAY + "Make a stock purchase.");
            Utils.createItem(Material.BOOK, i, 21, ChatColor.GOLD + "" + ChatColor.BOLD + "Sell a Stock", ChatColor.GRAY + "Make a stock sale.");
            Utils.createItem(Material.BOOK, i, 22, ChatColor.GOLD + "" + ChatColor.BOLD + "Compare", ChatColor.GRAY + "Make a stock comparison.");
            Utils.createItem(Material.BOOK, i, 23, ChatColor.GOLD + "" + ChatColor.BOLD + "Portfolio", ChatColor.GRAY + "View a stock portfolio.");
            Utils.createItem(Material.BOOK, i, 24, ChatColor.GOLD + "" + ChatColor.BOLD + "Stock History", ChatColor.GRAY + "View a stock's transaction history.");
            Utils.createItem(Material.BOOK, i, 30, ChatColor.GOLD + "" + ChatColor.BOLD + "Portfolio Leaderboard", ChatColor.GRAY + "View the leaderboard by portfolio value.");
            Utils.createItem(Material.BOOK, i, 31, ChatColor.GOLD + "" + ChatColor.BOLD + "Transaction History", ChatColor.GRAY + "View a player's transaction history.");
            Utils.createItem(Material.BOOK, i, 32, ChatColor.GOLD + "" + ChatColor.BOLD + "Profit Leaderboard", ChatColor.GRAY + "View the leaderboard by profit margin.");
            commandSender.openInventory(i);
        } else if (npcIsSpawned && inventoryName.equalsIgnoreCase(ChatColor.GRAY + "" + ChatColor.BOLD + "Stock Broker")) {
            List<String> stockList = Arrays.asList("Amazon.com, INC.;AMZN", "American Express;AXP", "Apple;AAPL", "AT&T;T", "Boeing;BA", "Chipotle Mexican Grill;CMG", "Coca-Cola;KO", "Costco Wholesale;COST", "Facebook;FB",
                    "FedEx;FDX", "General Motors;GM", "Google;GOOG", "Home Depot;HD", "International Business Machines;IBM", "Kellogg's;K", "Lowe's;LOW", "Mastercard;MA", "McDonald's;MCD",
                    "Microsoft;MSFT", "Netflix;NFLX", "NVIDIA;NVDA", "Starbucks;SBUX", "Verizon Communications;VZ", "Visa;V", "Time Warner Inc.;TWX", "Twitter;TWTR", "Wal-Mart Stores;WMT", "Walt Disney Company;DIS", "Whole Foods Market;WFM", "Yahoo!;YHOO");
            final Inventory listInventory = Bukkit.createInventory(null, 36, ChatColor.GRAY + "" + ChatColor.BOLD + "Stock Broker");
            int loopNumber = 0;

            for (int x = 0; x < 9; x++) {
                String[] splitString = stockList.get(loopNumber).split(";");
                Utils.createItem(Material.BOOK, listInventory, x, ChatColor.GRAY + "" + ChatColor.BOLD + splitString[0], Arrays.asList(ChatColor.GOLD + "Symbol:", ChatColor.GRAY + splitString[1], ChatColor.GOLD + "Shift-Left Click:", ChatColor.GRAY + "Buy 5", ChatColor.GOLD + "Left Click:", ChatColor.GRAY + "Buy 1", ChatColor.GOLD + "Middle Click:", ChatColor.GRAY + "Lookup Stock", ChatColor.GOLD + "Right Click:", ChatColor.GRAY + "Sell 1", ChatColor.GOLD + "Shift-Right Click:", ChatColor.GRAY + "Sell 5"));
                loopNumber++;
            }
            for (int x = 9; x < 18; x++) {
                String[] splitString = stockList.get(loopNumber).split(";");
                Utils.createItem(Material.BOOK, listInventory, x, ChatColor.GRAY + "" + ChatColor.BOLD + splitString[0], Arrays.asList(ChatColor.GOLD + "Symbol:", ChatColor.GRAY + splitString[1], ChatColor.GOLD + "Shift-Left Click:", ChatColor.GRAY + "Buy 5", ChatColor.GOLD + "Left Click:", ChatColor.GRAY + "Buy 1", ChatColor.GOLD + "Middle Click:", ChatColor.GRAY + "Lookup Stock", ChatColor.GOLD + "Right Click:", ChatColor.GRAY + "Sell 1", ChatColor.GOLD + "Shift-Right Click:", ChatColor.GRAY + "Sell 5"));
                loopNumber++;
            }
            for (int x = 18; x < 27; x++) {
                String[] splitString = stockList.get(loopNumber).split(";");
                Utils.createItem(Material.BOOK, listInventory, x, ChatColor.GRAY + "" + ChatColor.BOLD + splitString[0], Arrays.asList(ChatColor.GOLD + "Symbol:", ChatColor.GRAY + splitString[1], ChatColor.GOLD + "Shift-Left Click:", ChatColor.GRAY + "Buy 5", ChatColor.GOLD + "Left Click:", ChatColor.GRAY + "Buy 1", ChatColor.GOLD + "Middle Click:", ChatColor.GRAY + "Lookup Stock", ChatColor.GOLD + "Right Click:", ChatColor.GRAY + "Sell 1", ChatColor.GOLD + "Shift-Right Click:", ChatColor.GRAY + "Sell 5"));
                loopNumber++;
            }
            for (int x = 30; x < 33; x++) {
                String[] splitString = stockList.get(loopNumber).split(";");
                Utils.createItem(Material.BOOK, listInventory, x, ChatColor.GRAY + "" + ChatColor.BOLD + splitString[0], Arrays.asList(ChatColor.GOLD + "Symbol:", ChatColor.GRAY + splitString[1], ChatColor.GOLD + "Shift-Left Click:", ChatColor.GRAY + "Buy 5", ChatColor.GOLD + "Left Click:", ChatColor.GRAY + "Buy 1", ChatColor.GOLD + "Middle Click:", ChatColor.GRAY + "Lookup Stock", ChatColor.GOLD + "Right Click:", ChatColor.GRAY + "Sell 1", ChatColor.GOLD + "Shift-Right Click:", ChatColor.GRAY + "Sell 5"));
                loopNumber++;
            }

            commandSender.openInventory(listInventory);
        }
    }

    public static void displayCompareInventory(final Player commandSender, final String stockOneCheck, final String stockTwoCheck, final boolean delayedOpen) {
        final Stock[] stockOne = new Stock[1];
        final Stock[] stockTwo = new Stock[1];

        Bukkit.getScheduler().runTaskAsynchronously(StockMarket.getInstance(), new Runnable() {
            @Override
            public void run() {
                try {
                    stockOne[0] = YahooFinance.get(stockOneCheck);
                    stockTwo[0] = YahooFinance.get(stockTwoCheck);
                } catch (IOException | NumberFormatException | NullPointerException | ArrayIndexOutOfBoundsException e) {
                    commandSender.sendMessage(StockMarket.getInstance().getLocalConfig().invalidStockMessage);
                }

                Bukkit.getScheduler().runTask(StockMarket.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        boolean toContinue = true;
                        if (stockOne[0].getName().equalsIgnoreCase("N/A") || stockTwo[0].getName().equalsIgnoreCase("N/A")) {
                            commandSender.sendMessage(StockMarket.getInstance().getLocalConfig().invalidStockMessage);
                            toContinue = false;
                        }

                        if (toContinue) {
                            final Inventory compareInventory = Bukkit.createInventory(null, 36, ChatColor.GOLD + "" + ChatColor.BOLD + "Stocks" + ChatColor.GRAY + " - " + ChatColor.GOLD + "Compare");
                            Stock toUse;

                            for (int x = 0; x < 2; x++) {
                                if (x == 0) {
                                    toUse = stockOne[0];
                                } else {
                                    toUse = stockTwo[0];
                                }
                                Utils.createItem(Material.BOOK, compareInventory, (18 * x), ChatColor.GRAY + "" + ChatColor.BOLD + "Name", ChatColor.GOLD + toUse.getName());
                                Utils.createItem(Material.BOOK, compareInventory, 1 + (18 * x), ChatColor.GRAY + "" + ChatColor.BOLD + "Stock Exchange", ChatColor.GOLD + toUse.getStockExchange());
                                Utils.createItem(Material.DIAMOND, compareInventory, 4 + (18 * x), ChatColor.GRAY + "" + ChatColor.BOLD + "Current Price", ChatColor.GREEN + Utils.formatDecimal(toUse.getQuote().getPrice().floatValue()) + " " + ChatColor.GOLD + toUse.getCurrency());
                                Utils.createItem(Material.BOOK, compareInventory, 7 + (18 * x), ChatColor.GRAY + "" + ChatColor.BOLD + "Currency", ChatColor.GOLD + toUse.getCurrency());
                                Utils.createItem(Material.BOOK, compareInventory, 8 + (18 * x), ChatColor.GRAY + "" + ChatColor.BOLD + "Symbol", ChatColor.GOLD + toUse.getSymbol());
                                Utils.createItem(Material.BOOK, compareInventory, 10 + (18 * x), ChatColor.GRAY + "" + ChatColor.BOLD + "Day High", ChatColor.GREEN + Utils.formatDecimal(toUse.getQuote().getDayHigh().floatValue()) + " " + ChatColor.GOLD + toUse.getCurrency());
                                Utils.createItem(Material.BOOK, compareInventory, 11 + (18 * x), ChatColor.GRAY + "" + ChatColor.BOLD + "Day Low", ChatColor.GREEN + Utils.formatDecimal(toUse.getQuote().getDayLow().floatValue()) + " " + ChatColor.GOLD + toUse.getCurrency());
                                Utils.createItem(Material.BOOK, compareInventory, 12 + (18 * x), ChatColor.GRAY + "" + ChatColor.BOLD + "Today's Open Price", ChatColor.GREEN + Utils.formatDecimal(toUse.getQuote().getOpen().floatValue()) + " " + ChatColor.GOLD + toUse.getCurrency());
                                Utils.createItem(Material.BOOK, compareInventory, 13 + (18 * x), ChatColor.GRAY + "" + ChatColor.BOLD + "Volume", ChatColor.GOLD + String.valueOf(Utils.formatLargeNumber((double) toUse.getQuote().getVolume())));
                                Utils.createItem(Material.BOOK, compareInventory, 14 + (18 * x), ChatColor.GRAY + "" + ChatColor.BOLD + "Previous Close Price", ChatColor.GREEN + Utils.formatDecimal(toUse.getQuote().getPreviousClose().floatValue()) + " " + ChatColor.GOLD + toUse.getCurrency());
                                Utils.createItem(Material.BOOK, compareInventory, 15 + (18 * x), ChatColor.GRAY + "" + ChatColor.BOLD + "Year High", ChatColor.GREEN + Utils.formatDecimal(toUse.getQuote().getYearHigh().floatValue()) + " " + ChatColor.GOLD + toUse.getCurrency());
                                Utils.createItem(Material.BOOK, compareInventory, 16 + (18 * x), ChatColor.GRAY + "" + ChatColor.BOLD + "Year Low", ChatColor.GREEN + Utils.formatDecimal(toUse.getQuote().getYearLow().floatValue()) + " " + ChatColor.GOLD + toUse.getCurrency());
                            }

                            if (delayedOpen) {
                                Bukkit.getScheduler().runTaskLater(StockMarket.getInstance(), new Runnable() {
                                    @Override
                                    public void run() {
                                        commandSender.openInventory(compareInventory);
                                    }
                                }, 5L);
                            } else {
                                commandSender.openInventory(compareInventory);
                            }
                        }
                    }
                });
            }
        });
    }

    public static void displayCompareInventory(final Player commandSender, final String stockOneCheck, final String stockTwoCheck, final String stockThreeCheck, final boolean delayedOpen) {
        final Stock[] stockOne = new Stock[1];
        final Stock[] stockTwo = new Stock[1];
        final Stock[] stockThree = new Stock[1];

        Bukkit.getScheduler().runTaskAsynchronously(StockMarket.getInstance(), new Runnable() {
            @Override
            public void run() {
                try {
                    stockOne[0] = YahooFinance.get(stockOneCheck);
                    stockTwo[0] = YahooFinance.get(stockTwoCheck);
                    stockThree[0] = YahooFinance.get(stockThreeCheck);
                } catch (IOException | NumberFormatException | NullPointerException | ArrayIndexOutOfBoundsException e) {
                    commandSender.sendMessage(StockMarket.getInstance().getLocalConfig().invalidStockMessage);
                }

                Bukkit.getScheduler().runTask(StockMarket.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        boolean toContinue = true;
                        if (stockOne[0].getName().equalsIgnoreCase("N/A") || stockTwo[0].getName().equalsIgnoreCase("N/A") || stockThree[0].getName().equalsIgnoreCase("N/A")) {
                            commandSender.sendMessage(StockMarket.getInstance().getLocalConfig().invalidStockMessage);
                            toContinue = false;
                        }

                        if (toContinue) {
                            final Inventory compareInventory = Bukkit.createInventory(null, 54, ChatColor.GOLD + "" + ChatColor.BOLD + "Stocks" + ChatColor.GRAY + " - " + ChatColor.GOLD + "Compare");
                            Stock toUse;

                            for (int x = 0; x < 3; x++) {
                                if (x == 0) {
                                    toUse = stockOne[0];
                                } else if (x == 1) {
                                    toUse = stockTwo[0];
                                } else {
                                    toUse = stockThree[0];
                                }

                                Utils.createItem(Material.BOOK, compareInventory, (18 * x), ChatColor.GRAY + "" + ChatColor.BOLD + "Name", ChatColor.GOLD + toUse.getName());
                                Utils.createItem(Material.BOOK, compareInventory, 1 + (18 * x), ChatColor.GRAY + "" + ChatColor.BOLD + "Stock Exchange", ChatColor.GOLD + toUse.getStockExchange());
                                Utils.createItem(Material.DIAMOND, compareInventory, 4 + (18 * x), ChatColor.GRAY + "" + ChatColor.BOLD + "Current Price", ChatColor.GREEN + Utils.formatDecimal(toUse.getQuote().getPrice().floatValue()) + " " + ChatColor.GOLD + toUse.getCurrency());
                                Utils.createItem(Material.BOOK, compareInventory, 7 + (18 * x), ChatColor.GRAY + "" + ChatColor.BOLD + "Currency", ChatColor.GOLD + toUse.getCurrency());
                                Utils.createItem(Material.BOOK, compareInventory, 8 + (18 * x), ChatColor.GRAY + "" + ChatColor.BOLD + "Symbol", ChatColor.GOLD + toUse.getSymbol());
                                Utils.createItem(Material.BOOK, compareInventory, 10 + (18 * x), ChatColor.GRAY + "" + ChatColor.BOLD + "Day High", ChatColor.GREEN + Utils.formatDecimal(toUse.getQuote().getDayHigh().floatValue()) + " " + ChatColor.GOLD + toUse.getCurrency());
                                Utils.createItem(Material.BOOK, compareInventory, 11 + (18 * x), ChatColor.GRAY + "" + ChatColor.BOLD + "Day Low", ChatColor.GREEN + Utils.formatDecimal(toUse.getQuote().getDayLow().floatValue()) + " " + ChatColor.GOLD + toUse.getCurrency());
                                Utils.createItem(Material.BOOK, compareInventory, 12 + (18 * x), ChatColor.GRAY + "" + ChatColor.BOLD + "Today's Open Price", ChatColor.GREEN + Utils.formatDecimal(toUse.getQuote().getOpen().floatValue()) + " " + ChatColor.GOLD + toUse.getCurrency());
                                Utils.createItem(Material.BOOK, compareInventory, 13 + (18 * x), ChatColor.GRAY + "" + ChatColor.BOLD + "Volume", ChatColor.GOLD + String.valueOf(Utils.formatLargeNumber((double) toUse.getQuote().getVolume())));
                                Utils.createItem(Material.BOOK, compareInventory, 14 + (18 * x), ChatColor.GRAY + "" + ChatColor.BOLD + "Previous Close Price", ChatColor.GREEN + Utils.formatDecimal(toUse.getQuote().getPreviousClose().floatValue()) + " " + ChatColor.GOLD + toUse.getCurrency());
                                Utils.createItem(Material.BOOK, compareInventory, 15 + (18 * x), ChatColor.GRAY + "" + ChatColor.BOLD + "Year High", ChatColor.GREEN + Utils.formatDecimal(toUse.getQuote().getYearHigh().floatValue()) + " " + ChatColor.GOLD + toUse.getCurrency());
                                Utils.createItem(Material.BOOK, compareInventory, 16 + (18 * x), ChatColor.GRAY + "" + ChatColor.BOLD + "Year Low", ChatColor.GREEN + Utils.formatDecimal(toUse.getQuote().getYearLow().floatValue()) + " " + ChatColor.GOLD + toUse.getCurrency());
                            }

                            if (delayedOpen) {
                                Bukkit.getScheduler().runTaskLater(StockMarket.getInstance(), new Runnable() {
                                    @Override
                                    public void run() {
                                        commandSender.openInventory(compareInventory);
                                    }
                                }, 5L);
                            } else {
                                commandSender.openInventory(compareInventory);
                            }
                        }
                    }
                });
            }
        });
    }

    public static void displayProfitLeaderboard (final Player commandSender) {
            final ArrayList<List<StockPlayer>> toUse = new ArrayList<>();
            Bukkit.getScheduler().runTaskAsynchronously(StockMarket.getInstance(), new Runnable() {
                @Override
                public void run() {
                    toUse.add(0, StockMarket.getMySQL().getAllStockPlayers());
                    boolean toContinue = true;

                    if (toUse.get(0).isEmpty()) {
                        commandSender.sendMessage(StockMarket.getInstance().getLocalConfig().noStockPlayers);
                        toContinue = false;
                    }

                    if (toContinue) {
                        final TreeMap<Double, String> transactionValue = new TreeMap<>(Collections.reverseOrder());
                        for (int x = 0; x < toUse.get(0).size(); x++) {
                            transactionValue.put(Double.valueOf(StockMarket.getStockMarketAPI().getProfitMargin(toUse.get(0).get(x).getPlayerUUID())), toUse.get(0).get(x).getPlayerName());
                        }

                        int toDisplay = 44;

                        if (transactionValue.size() < 44) {
                            toDisplay = transactionValue.size() + 1;
                        }
                        final int finalToDisplay = toDisplay;
                        Bukkit.getScheduler().runTask(StockMarket.getInstance(), new Runnable() {
                            @Override
                            public void run() {
                                final Inventory leaderboardInventory = Bukkit.createInventory(null, 54, ChatColor.GOLD + "" + ChatColor.BOLD + "Stocks " + ChatColor.GRAY + "-" + ChatColor.GOLD + " Leaderboard");
                                int tally = 0;
                                for (Map.Entry<Double, String> e : transactionValue.entrySet()) {
                                    OfflinePlayer p = Bukkit.getOfflinePlayer(e.getValue());
                                    ItemStack skull = new ItemStack(397, 1, (short) 3);
                                    SkullMeta meta = (SkullMeta) skull.getItemMeta();
                                    meta.setDisplayName(ChatColor.GOLD + "#" + (tally + 1) + ChatColor.GRAY + " " + p.getName());
                                    meta.setOwner(p.getName());

                                    List<String> lore = new ArrayList<>();
                                    lore.add(ChatColor.GREEN + "" + Utils.formatDecimal(e.getKey().floatValue()) + " " + ChatColor.GRAY + StockMarket.getInstance().getLocalConfig().serverCurrency);
                                    meta.setLore(lore);
                                    skull.setItemMeta(meta);
                                    leaderboardInventory.setItem(tally, skull);
                                    tally++;

                                    if (tally >= finalToDisplay) {
                                        break;
                                    }
                                }

                                int playerPosition = 1;
                                double playerProfitMargin = 0;
                                String commandSenderName = commandSender.getName();
                                for (Map.Entry<Double, String> e : transactionValue.entrySet()) {
                                    if (e.getValue().equalsIgnoreCase(commandSenderName)) {
                                        playerProfitMargin = e.getKey();
                                        break;
                                    }

                                    playerPosition++;
                                }

                                Utils.createItem(Material.ENCHANTED_BOOK, leaderboardInventory, 48, ChatColor.GRAY + "" + ChatColor.BOLD + "Your Position", ChatColor.GOLD + "" + playerPosition);
                                Utils.createItem(Material.ENCHANTED_BOOK, leaderboardInventory, 50, ChatColor.GRAY + "" + ChatColor.BOLD + "Your Profit Margin", ChatColor.GOLD + "" + Utils.formatDecimal((float) playerProfitMargin));
                                commandSender.openInventory(leaderboardInventory);
                            }
                        });
                    }
                }
            });
    }

    public static void displayPortfolioLeaderboard (final Player commandSender) {
        final ArrayList<List<StockPlayer>> toUse = new ArrayList<>();
        Bukkit.getScheduler().runTaskAsynchronously(StockMarket.getInstance(), new Runnable() {
            @Override
            public void run() {
                toUse.add(0, StockMarket.getMySQL().getAllStockPlayers());
                boolean toContinue = true;

                if (toUse.get(0).isEmpty()) {
                    commandSender.sendMessage(StockMarket.getInstance().getLocalConfig().noStockPlayers);
                    toContinue = false;
                }

                if (toContinue) {
                    final TreeMap<Double, String> transactionValue = new TreeMap<>(Collections.reverseOrder());
                    for (int x = 0; x < toUse.get(0).size(); x++) {
                        transactionValue.put(Double.valueOf(StockMarket.getStockMarketAPI().getPortfolioValue(toUse.get(0).get(x).getPlayerUUID())), toUse.get(0).get(x).getPlayerName());
                    }

                    int toDisplay = 44;

                    if (transactionValue.size() < 44) {
                        toDisplay = transactionValue.size() + 1;
                    }
                    final int finalToDisplay = toDisplay;
                    Bukkit.getScheduler().runTask(StockMarket.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            final Inventory leaderboardInventory = Bukkit.createInventory(null, 54, ChatColor.GOLD + "" + ChatColor.BOLD + "Stocks " + ChatColor.GRAY + "-" + ChatColor.GOLD + " Leaderboard");
                            int tally = 0;
                            for (Map.Entry<Double, String> e : transactionValue.entrySet()) {
                                if (e.getKey().floatValue() != 0) {
                                    OfflinePlayer p = Bukkit.getOfflinePlayer(e.getValue());
                                    ItemStack skull = new ItemStack(397, 1, (short) 3);
                                    SkullMeta meta = (SkullMeta) skull.getItemMeta();
                                    meta.setDisplayName(ChatColor.GOLD + "#" + (tally + 1) + ChatColor.GRAY + " " + p.getName());
                                    meta.setOwner(p.getName());

                                    List<String> lore = new ArrayList<>();
                                    lore.add(ChatColor.GREEN + "" + Utils.formatDecimal(e.getKey().floatValue()) + " " + ChatColor.GRAY + StockMarket.getInstance().getLocalConfig().serverCurrency);
                                    meta.setLore(lore);
                                    skull.setItemMeta(meta);
                                    leaderboardInventory.setItem(tally, skull);
                                }
                                tally++;

                                if (tally >= finalToDisplay) {
                                    break;
                                }
                            }

                            int playerPosition = 1;
                            double playerPortfolioValue = 0;
                            String commandSenderName = commandSender.getName();
                            for (Map.Entry<Double, String> e : transactionValue.entrySet()) {
                                if (e.getValue().equalsIgnoreCase(commandSenderName)) {
                                    playerPortfolioValue = e.getKey();
                                    break;
                                }

                                playerPosition++;
                            }

                            Utils.createItem(Material.ENCHANTED_BOOK, leaderboardInventory, 48, ChatColor.GRAY + "" + ChatColor.BOLD + "Your Position", ChatColor.GOLD + "" + playerPosition);
                            Utils.createItem(Material.ENCHANTED_BOOK, leaderboardInventory, 50, ChatColor.GRAY + "" + ChatColor.BOLD + "Your Portfolio Value", ChatColor.GOLD + "" + Utils.formatDecimal((float) playerPortfolioValue));
                            commandSender.openInventory(leaderboardInventory);
                        }
                    });
                }
            }
        });
    }
}
