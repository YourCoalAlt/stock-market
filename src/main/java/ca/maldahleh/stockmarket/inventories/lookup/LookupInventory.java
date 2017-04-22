package ca.maldahleh.stockmarket.inventories.lookup;

import ca.maldahleh.stockmarket.StockMarket;
import ca.maldahleh.stockmarket.utils.ItemStackBuilder;
import ca.maldahleh.stockmarket.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.quotes.fx.FxQuote;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class LookupInventory {
    private StockMarket stockMarket;
    private String stockSymbol;
    private Player target;
    private Inventory inventory;

    public LookupInventory(StockMarket stockMarket, Player target, String stockSymbol) {
        this.stockMarket = stockMarket;
        this.stockSymbol = stockSymbol;
        this.target = target;

        createInventory();
    }

    private void createInventory() {
        inventory = Bukkit.createInventory(null, 36,
                ChatColor.GOLD + "" + ChatColor.BOLD + "Stocks" + ChatColor.GRAY + " - " + ChatColor.GOLD
                        + stockSymbol.toUpperCase());

        Bukkit.getScheduler().runTaskAsynchronously(stockMarket, () -> {
            Stock unfinalStock = null;

            try {
                unfinalStock = YahooFinance.get(stockSymbol, true);
            } catch (IOException e) {
                target.sendMessage(stockMarket.getLocalConfig().getInvalidStockMessage());
            }

            Stock stock = unfinalStock;
            Bukkit.getScheduler().runTask(stockMarket, () -> {
                if (!(stock == null) && !stock.getName().equalsIgnoreCase("N/A")) {
                    final String[] serverPriceString = {Utils.formatDecimal(stock.getQuote().getPrice().doubleValue())};
                    final double[] serverPrice = {stock.getQuote().getPrice().doubleValue()};

                    Bukkit.getScheduler().runTaskAsynchronously(stockMarket, () -> {
                        if ((stockMarket.getLocalConfig().isBlockNonUSDSales() && !stock.getCurrency().equals("USD"))
                                || (stockMarket.getLocalConfig().isPreventSaleOfZeroValue() && stock.getQuote().getPrice().doubleValue() == 0)) {
                            serverPriceString[0] = "Not for Sale";
                        } else {
                            if (stockMarket.getLocalConfig().isConvertToUSD() && !stock.getCurrency().equals("USD")) {
                                FxQuote quote = null;
                                try {
                                    quote = YahooFinance.getFx(stock.getCurrency().toUpperCase() + "USD=X");
                                } catch (IOException ignored) {
                                }

                                assert quote != null;
                                serverPrice[0] = serverPrice[0] * quote.getPrice().doubleValue();
                            }

                            serverPrice[0] = serverPrice[0] * stockMarket.getLocalConfig().getMultiplier();
                            serverPriceString[0] = Utils.formatDecimal(serverPrice[0]);
                        }

                        Bukkit.getScheduler().runTask(stockMarket, () -> {
                            inventory.setItem(0, new ItemStackBuilder(Material.BOOK, 1)
                                    .setDisplayName(ChatColor.GRAY + "" + ChatColor.BOLD + "Name")
                                    .addLoreLine(ChatColor.GOLD + stock.getName())
                                    .buildItemStack());

                            inventory.setItem(1, new ItemStackBuilder(Material.BOOK, 1)
                                    .setDisplayName(ChatColor.GRAY + "" + ChatColor.BOLD + "Stock Exchange")
                                    .addLoreLine(ChatColor.GOLD + stock.getStockExchange())
                                    .buildItemStack());

                            inventory.setItem(2, new ItemStackBuilder(Material.BOOK, 1)
                                    .setDisplayName(ChatColor.GRAY + "" + ChatColor.BOLD + "Market Cap")
                                    .addLoreLine(ChatColor.GREEN + Utils.formatLargeNumber(Double
                                            .parseDouble(Utils.formatDecimal(stock.getStats().getMarketCap()
                                                    .doubleValue()))) + ChatColor.GOLD + " " + stock.getCurrency())
                                    .buildItemStack());

                            inventory.setItem(3, new ItemStackBuilder(Material.EMERALD, 1)
                                    .setDisplayName(ChatColor.GRAY + "" + ChatColor.BOLD + "Current Server Price")
                                    .addLoreLine(ChatColor.GREEN + serverPriceString[0] + " " + ChatColor.GOLD
                                            + StockMarket.getEcon().currencyNamePlural())
                                    .addLoreLine(ChatColor.GREEN + "" + ChatColor.BOLD + "+ "
                                            + ChatColor.GRAY + "Broker Flat Rate")
                                    .addLoreLine(ChatColor.GOLD + " " + Utils.formatDecimal(stockMarket
                                            .getLocalConfig().getBrokerFeeFlat()))
                                    .addLoreLine(ChatColor.GREEN + "" + ChatColor.BOLD + "+ "
                                            + ChatColor.GRAY + "Broker Percent")
                                    .addLoreLine(ChatColor.GOLD + " " + (stockMarket.getLocalConfig()
                                            .getBrokerFeePercent() * 100) + "%")
                                    .buildItemStack());

                            inventory.setItem(4, new ItemStackBuilder(Material.DIAMOND, 1)
                                    .setDisplayName(ChatColor.GRAY + "" + ChatColor.BOLD + "Current Market Price")
                                    .addLoreLine(ChatColor.GREEN + Utils.formatDecimal(stock.getQuote()
                                            .getPrice().floatValue()) + " " + ChatColor.GOLD + stock.getCurrency())
                                    .addLoreLine(ChatColor.GRAY + "Change from close:")
                                    .addLoreLine(ChatColor.GREEN + Utils
                                            .formatDecimal(stock.getQuote().getChange().doubleValue()))
                                    .addLoreLine(ChatColor.GRAY + "Change from year high:")
                                    .addLoreLine(ChatColor.GREEN + Utils
                                            .formatDecimal(stock.getQuote().getChangeFromYearHigh().doubleValue()))
                                    .addLoreLine(ChatColor.GRAY + "Change from year low:")
                                    .addLoreLine(ChatColor.GREEN + Utils
                                            .formatDecimal(stock.getQuote().getChangeFromYearLow().doubleValue()))
                                    .addLoreLine(ChatColor.GRAY + "Change from 200 day moving average:")
                                    .addLoreLine(ChatColor.GREEN + Utils
                                            .formatDecimal(stock.getQuote().getPriceAvg200().doubleValue()))
                                    .addLoreLine(ChatColor.GRAY + "Change from 50 day moving average:")
                                    .addLoreLine(ChatColor.GREEN + Utils
                                            .formatDecimal(stock.getQuote().getPriceAvg50().doubleValue()))
                                    .buildItemStack());

                            inventory.setItem(5, new ItemStackBuilder(Material.EMERALD, 1)
                                    .setDisplayName(ChatColor.GRAY + "" + ChatColor.BOLD + "Current Server Price")
                                    .addLoreLine(ChatColor.GREEN + serverPriceString[0] + " " + ChatColor.GOLD
                                            + StockMarket.getEcon().currencyNamePlural())
                                    .addLoreLine(ChatColor.GREEN + "" + ChatColor.BOLD + "+ "
                                            + ChatColor.GRAY + "Broker Flat Rate")
                                    .addLoreLine(ChatColor.GOLD + " " + Utils.formatDecimal(stockMarket
                                            .getLocalConfig().getBrokerFeeFlat()))
                                    .addLoreLine(ChatColor.GREEN + "" + ChatColor.BOLD + "+ "
                                            + ChatColor.GRAY + "Broker Percent")
                                    .addLoreLine(ChatColor.GOLD + " " + (stockMarket.getLocalConfig()
                                            .getBrokerFeePercent() * 100) + "%")
                                    .buildItemStack());

                            inventory.setItem(6, new ItemStackBuilder(Material.BOOK, 1)
                                    .setDisplayName(ChatColor.GRAY + "" + ChatColor.BOLD + "Annual Yield")
                                    .addLoreLine(ChatColor.GREEN + Utils.formatDecimal(stock.getDividend()
                                            .getAnnualYieldPercent().doubleValue()) + ChatColor.GOLD + "%")
                                    .buildItemStack());

                            inventory.setItem(7, new ItemStackBuilder(Material.BOOK, 1)
                                    .setDisplayName(ChatColor.GRAY + "" + ChatColor.BOLD + "Currency")
                                    .addLoreLine(ChatColor.GOLD + stock.getCurrency())
                                    .buildItemStack());

                            inventory.setItem(8, new ItemStackBuilder(Material.BOOK, 1)
                                    .setDisplayName(ChatColor.GRAY + "" + ChatColor.BOLD + "Symbol")
                                    .addLoreLine(ChatColor.GOLD + stock.getSymbol())
                                    .buildItemStack());

                            inventory.setItem(10, new ItemStackBuilder(Material.BOOK, 1)
                                    .setDisplayName(ChatColor.GRAY + "" + ChatColor.BOLD + "Day High")
                                    .addLoreLine(ChatColor.GREEN + Utils.formatDecimal(stock.getQuote().getDayHigh()
                                            .floatValue()) + " " + ChatColor.GOLD + stock.getCurrency())
                                    .buildItemStack());

                            inventory.setItem(11, new ItemStackBuilder(Material.BOOK, 1)
                                    .setDisplayName(ChatColor.GRAY + "" + ChatColor.BOLD + "Day Low")
                                    .addLoreLine(ChatColor.GREEN + Utils.formatDecimal(stock.getQuote().getDayLow()
                                            .floatValue()) + " " + ChatColor.GOLD + stock.getCurrency())
                                    .buildItemStack());

                            inventory.setItem(12, new ItemStackBuilder(Material.BOOK, 1)
                                    .setDisplayName(ChatColor.GRAY + "" + ChatColor.BOLD + "Today's Open Price")
                                    .addLoreLine(ChatColor.GREEN + Utils.formatDecimal(stock.getQuote().getOpen()
                                            .floatValue()) + " " + ChatColor.GOLD + stock.getCurrency())
                                    .buildItemStack());

                            inventory.setItem(13, new ItemStackBuilder(Material.BOOK, 1)
                                    .setDisplayName(ChatColor.GRAY + "" + ChatColor.BOLD + "Volume")
                                    .addLoreLine(ChatColor.GOLD + String.valueOf(Utils
                                            .formatLargeNumber(stock.getQuote().getVolume())))
                                    .buildItemStack());

                            inventory.setItem(14, new ItemStackBuilder(Material.BOOK, 1)
                                    .setDisplayName(ChatColor.GRAY + "" + ChatColor.BOLD + "Previous Close Price")
                                    .addLoreLine(ChatColor.GREEN + Utils.formatDecimal(stock.getQuote()
                                            .getPreviousClose().floatValue()) + " " + ChatColor.GOLD + stock
                                            .getCurrency())
                                    .buildItemStack());

                            inventory.setItem(15, new ItemStackBuilder(Material.BOOK, 1)
                                    .setDisplayName(ChatColor.GRAY + "" + ChatColor.BOLD + "Year High")
                                    .addLoreLine(ChatColor.GREEN + Utils.formatDecimal(stock.getQuote().getYearHigh()
                                            .floatValue()) + " " + ChatColor.GOLD + stock.getCurrency())
                                    .buildItemStack());

                            inventory.setItem(16, new ItemStackBuilder(Material.BOOK, 1)
                                    .setDisplayName(ChatColor.GRAY + "" + ChatColor.BOLD + "Today's Open Price")
                                    .addLoreLine(ChatColor.GREEN + Utils.formatDecimal(stock.getQuote().getYearLow()
                                            .floatValue()) + " " + ChatColor.GOLD + stock.getCurrency())
                                    .buildItemStack());

                            List<Integer> toLoad = Arrays.asList(18, 19, 20, 21, 22, 23, 24, 25, 26, 30, 31, 32);
                            ItemStack noHistoricalData = new ItemStackBuilder(Material.ENCHANTED_BOOK, 1)
                                    .setDisplayName(ChatColor.GRAY + "" + ChatColor.BOLD + "No Historical Data")
                                    .buildItemStack();

                            int count = 0;
                            for (int x : toLoad) {
                                try {
                                    HistoricalQuote quote = stock.getHistory().get(count);
                                    inventory.setItem(x, new ItemStackBuilder(Material.ENCHANTED_BOOK, 1)
                                            .setDisplayName(ChatColor.GRAY + "" + ChatColor.BOLD + quote.getDate()
                                                    .getTime().toString())
                                            .addLoreLine(ChatColor.GRAY + "" + ChatColor.BOLD + "Day Open:")
                                            .addLoreLine(ChatColor.GREEN + Utils.formatDecimal(quote
                                                    .getOpen().floatValue()) + " " + ChatColor.GOLD + stock.getCurrency())
                                            .addLoreLine(ChatColor.GRAY + "" + ChatColor.BOLD + "Day Close:")
                                            .addLoreLine(ChatColor.GREEN + Utils.formatDecimal(quote.getClose()
                                                    .floatValue()) + " " + ChatColor.GOLD + stock.getCurrency())
                                            .addLoreLine(ChatColor.GRAY + "" + ChatColor.BOLD + "Volume:")
                                            .addLoreLine(ChatColor.GOLD + String.valueOf(Utils.formatLargeNumber(quote.getVolume())))
                                            .addLoreLine(ChatColor.GRAY + "" + ChatColor.BOLD + "Day High:")
                                            .addLoreLine(ChatColor.GREEN + Utils.formatDecimal(quote.getHigh()
                                                    .floatValue()) + " " + ChatColor.GOLD + stock.getCurrency())
                                            .addLoreLine(ChatColor.GRAY + "" + ChatColor.BOLD + "Day Low:")
                                            .addLoreLine(ChatColor.GREEN + Utils.formatDecimal(quote.getLow()
                                                    .floatValue()) + " " + ChatColor.GOLD + stock.getCurrency())
                                            .buildItemStack());
                                } catch (Exception e) {
                                    inventory.setItem(x, noHistoricalData);
                                }

                                count++;
                            }

                            target.openInventory(inventory);
                        });
                    });
                } else {
                    target.sendMessage(stockMarket.getLocalConfig().getInvalidStockMessage());
                }
            });
        });
    }
}
