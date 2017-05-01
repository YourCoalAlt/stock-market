package ca.maldahleh.stockmarket.handling;

import ca.maldahleh.stockmarket.StockMarket;
import ca.maldahleh.stockmarket.events.StockPurchaseEvent;
import ca.maldahleh.stockmarket.events.StockSaleEvent;
import ca.maldahleh.stockmarket.stocks.Stocks;
import ca.maldahleh.stockmarket.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.quotes.fx.FxQuote;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class StockHandling {
    private StockMarket stockMarket;

    public StockHandling(StockMarket stockMarket) {
        this.stockMarket = stockMarket;
    }

    public void buyStock(final Player p, final String stockSymbol, final int quantity) {
        Bukkit.getScheduler().runTaskAsynchronously(stockMarket, () -> {
            Stock stock;
            try {
                stock = YahooFinance.get(stockSymbol);
            } catch (IOException | NumberFormatException | NullPointerException e) {
                p.sendMessage(StockMarket.getInstance().getLocalConfig().getInvalidStockMessage());
                return;
            }

            if (stock.getQuote().getPrice().doubleValue() <= 0) {
                p.sendMessage(StockMarket.getInstance().getLocalConfig().getInvalidPrice()
                        .replace("<symbol>", stockSymbol.toUpperCase()));
                return;
            }

            if (StockMarket.getInstance().getLocalConfig().isPennyStockCheck()) {
                if (stock.getQuote().getPrice().doubleValue() <= StockMarket.getInstance().getLocalConfig().getPennyStockMinimum()) {
                    p.sendMessage(StockMarket.getInstance().getLocalConfig().getPennyStockNotMet());
                    return;
                }
            }

            if (StockMarket.getInstance().getLocalConfig().isDisableTradingWhenClosed()) {
                boolean result;
                try {
                    result = isMarketOpen(stock.getSymbol().toUpperCase());
                } catch (Exception e) {
                    result = false;
                }

                if (!result) {
                    p.sendMessage(StockMarket.getInstance().getLocalConfig().getNoTradingWhenMarketIsClosed());
                    return;
                }
            }

            Bukkit.getScheduler().runTask(StockMarket.getInstance(), () -> {
                if (StockMarket.getInstance().getLocalConfig().isBlockNonUSDSales()
                        && !stock.getCurrency().equalsIgnoreCase("USD")) {
                    p.sendMessage(StockMarket.getInstance().getLocalConfig().getNoPurchaseNonUSD());
                    return;
                }

                Bukkit.getScheduler().runTaskAsynchronously(StockMarket.getInstance(), () -> {
                    final double stockPrice = Double.valueOf(Utils.formatDecimal(stock.getQuote().getPrice().floatValue()));
                    double preStockConverted = 0;
                    double preStockValue = 0;
                    double preStockValueConverted = 0;
                    double singleStockPrice;

                    if (StockMarket.getInstance().getLocalConfig().isConvertToUSD() &&
                            !stock.getCurrency().equalsIgnoreCase("USD")) {
                        final FxQuote forexStock;
                        try {
                            forexStock = YahooFinance.getFx(stock.getCurrency().toUpperCase() + "USD=X");
                        } catch (IOException e) {
                            e.printStackTrace();
                            return;
                        }

                        preStockConverted = stockPrice * forexStock.getPrice().doubleValue()
                                * StockMarket.getInstance().getLocalConfig().getMultiplier();
                        preStockValueConverted = preStockConverted * quantity;
                    } else {

                    }

                    if (preStockValueConverted[0] != 0) {
                        preStockValue[0] = preStockValueConverted[0];
                    } else {
                        multipliedValue = stockPrice * StockMarket.getInstance().getLocalConfig().multiplier;
                        preStockValue[0] = Double.valueOf(Utils.formatDecimal(((float) (multipliedValue * quantity))));
                    }

                    if (preStockConverted[0] != 0) {
                        singleStockPrice[0] = Double.valueOf(Utils.formatDecimal((float) preStockConverted[0]));
                    } else {
                        singleStockPrice[0] = Double.valueOf(Utils.formatDecimal((float) multipliedValue));
                    }

                    final double brokerFees = Double.valueOf(Utils.formatDecimal((float) ((StockMarket.getInstance().getLocalConfig().brokerFeePercent * preStockValue[0]) + StockMarket.getInstance().getLocalConfig().brokerFeeFlat)));
                    final double grandTotal = Double.valueOf(Utils.formatDecimal((float) (preStockValue[0] + brokerFees)));

                    double playerBalance = StockMarket.getEcon().getBalance(p);

                    if (playerBalance < grandTotal) {
                        String inital = StockMarket.getInstance().getLocalConfig().getNotEnoughMoney()
                                .replace("<quantity>", String.valueOf(quantity));
                        String modifiedSymbol = inital.replace("<symbol>", stockSymbol.toUpperCase());
                        String modifiedGrandTotal = modifiedSymbol
                                .replace("<total>", String.valueOf(Utils.formatDecimal(grandTotal)));
                        p.sendMessage(modifiedGrandTotal);
                    } else {
                        StockMarket.getEcon().withdrawPlayer(p, grandTotal);
                        if (!StockMarket.getInstance().getLocalConfig().feesAccount.equals("")) {
                            StockMarket.getEcon().depositPlayer(StockMarket.getInstance().getLocalConfig().feesAccount, brokerFees);
                        }

                        if (!StockMarket.getInstance().getLocalConfig().stockAccount.equals("")) {
                            StockMarket.getEcon().depositPlayer(StockMarket.getInstance().getLocalConfig().stockAccount, preStockValue[0]);
                        }

                        List<String> boughtMessage = StockMarket.getInstance().getLocalConfig().boughtStock;
                        SimpleDateFormat dateFormat = new SimpleDateFormat();
                        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

                        String timeStamp;
                        timeStamp = dateFormat.format(new Date());

                        for (String toSend : boughtMessage) {
                            String a = toSend.replace("<date>", timeStamp + " UTC");
                            String b = a.replace("<quantity>", String.valueOf(quantity));
                            String c = b.replace("<symbol>", stockSymbol.toUpperCase());
                            String d = c.replace("<company>", stock[0].getName());
                            String e = d.replace("<stock-value>", "(" + quantity + " * " + String.valueOf(Utils.formatDecimal((float) singleStockPrice[0])) + ") " + String.valueOf(Utils.formatDecimal((float) preStockValue[0])) + " " + StockMarket.getInstance().getLocalConfig().serverCurrency);
                            String f = e.replace("<broker-fees>", String.valueOf(Utils.formatDecimal((float) brokerFees)) + " " + StockMarket.getInstance().getLocalConfig().serverCurrency);
                            String g = f.replace("<total>", String.valueOf(Utils.formatDecimal((float) grandTotal)) + " " + StockMarket.getInstance().getLocalConfig().serverCurrency);
                            p.sendMessage(g);
                        }

                        Bukkit.getPluginManager().callEvent(new StockPurchaseEvent(p, stockSymbol, quantity, String.valueOf(Utils.formatDecimal((float) preStockValue[0])), String.valueOf(Utils.formatDecimal((float) brokerFees)), String.valueOf(Utils.formatDecimal((float) grandTotal))));

                        Bukkit.getScheduler().runTaskAsynchronously(StockMarket.getInstance(), new Runnable() {
                            @Override
                            public void run() {
                                StockMarket.getMySQL().processPurchase(p, stockSymbol.toUpperCase(), isConverted[0], stockPrice, stock[0].getCurrency(), Double.parseDouble(Utils.formatDecimal((float) singleStockPrice[0])), quantity, preStockValue[0], brokerFees, grandTotal);
                            }
                        });
                    }
                });
            });
        });

    }

    public void sellStock(final Player p, final int quantityToSell, final String stockSymbol) {
        final Integer[] quantityToSellFinal = {quantityToSell};
        final double[] preStockConverted = {0};
        final double[] preStockValue = {0};
        final double[] preStockValueConverted = {0};
        final boolean[] isConverted = {false};
        final double[] singleStockPrice = new double[1];

        Bukkit.getScheduler().runTaskAsynchronously(stockMarket, () -> {
            Stock stock;
            try {
                stock = YahooFinance.get(stockSymbol);
            } catch (IOException | NumberFormatException | NullPointerException e) {
                p.sendMessage(stockMarket.getLocalConfig().getInvalidStockMessage());
                return;
            }

            if (stockMarket.getLocalConfig().isDisableTradingWhenClosed()) {
                boolean result;
                try {
                    result = isMarketOpen(stock.getSymbol().toUpperCase());
                } catch (Exception e) {
                    result = false;
                }

                if (!result) {
                    p.sendMessage(stockMarket.getLocalConfig().getNoTradingWhenMarketIsClosed());
                    return;
                }
            }

            final double stockPrice = Double.parseDouble(Utils.formatDecimal(stock.getQuote().getPrice().doubleValue()));
            Bukkit.getScheduler().runTaskAsynchronously(StockMarket.getInstance(), () -> {
                if (stockMarket.getLocalConfig().isConvertToUSD()) {
                    if (!stock.getCurrency().equalsIgnoreCase("USD")) {
                        FxQuote forexStock;

                        try {
                            forexStock = YahooFinance.getFx(stock.getCurrency().toUpperCase() + "USD=X");
                        } catch (IOException ignored) {
                            return;
                        }

                        preStockConverted[0] = stockPrice * forexStock.getPrice().doubleValue()
                                * stockMarket.getLocalConfig().getMultiplier();

                        isConverted[0] = true;
                    }
                }

                List<Stocks> stocksList = stockMarket.getMySQL().getPlayerSymbolOwned(p, stockSymbol);
                Bukkit.getScheduler().runTask(StockMarket.getInstance(), () -> {
                    int quantityTest = 0;
                    for (Stocks stocks : stocksList) {
                        quantityTest += stocks.getQuantity();
                    }

                    if (quantityTest < quantityToSell) {
                        p.sendMessage(StockMarket.getInstance().getLocalConfig().getNoStockToSell()
                                .replace("<symbol>", stock.getSymbol().toUpperCase()));
                        return;
                    }

                    List<String> processedPurchases = new ArrayList<>();
                    for (final Stocks stocks : stocksList) {
                        if (stockMarket.getLocalConfig().isAbusePrevention()) {
                            Date buyTime = stocks.getDate();
                            int seconds = (int) ((getUTCdatetimeAsDate().getTime() - buyTime.getTime()) / 1000);
                            if (seconds < stockMarket.getLocalConfig().getAbusePreventionSeconds()) {
                                long millis = (stockMarket.getLocalConfig().getAbusePreventionSeconds() * 1000) - (seconds * 1000);
                                String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                                        TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                                        TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));

                                p.sendMessage(stockMarket.getLocalConfig().getDayNotPassed()
                                        .replace("<symbol>", stock.getSymbol()).replace("<hours>", hms));
                                return;
                            }
                        }

                        final int soldStocksOwned;
                        if (quantityToSellFinal[0] > stocks.getQuantity()) {
                            soldStocksOwned = stocks.getQuantity();
                        } else {
                            soldStocksOwned = quantityToSellFinal[0];
                        }

                        stocks.setQuantity(stocks.getQuantity() - soldStocksOwned);

                        Bukkit.getScheduler().runTaskAsynchronously(StockMarket.getInstance(), () -> {
                            if (stocks.getQuantity() <= 0) {
                                stockMarket.getMySQL().deletePlayerOwnedStock(stocks);
                            } else {
                                stockMarket.getMySQL().updatePlayerOwnedStock(stocks);
                            }
                        });


                        quantityToSellFinal[0] -= soldStocksOwned;

                        if (soldStocksOwned > 0) {
                            double multipliedValue = 0;
                            if (preStockConverted[0] != 0) {
                                preStockValueConverted[0] = preStockConverted[0] * soldStocksOwned;
                                preStockValue[0] = preStockValueConverted[0];
                            }

                            if (preStockValueConverted[0] == 0) {
                                if (StockMarket.getInstance().getLocalConfig().multiplier != 0) {
                                    multipliedValue = stockPrice * StockMarket.getInstance().getLocalConfig().multiplier;
                                    preStockValue[0] = Double.valueOf(Utils.formatDecimal(((float) (multipliedValue * soldStocksOwned))));
                                } else {
                                    preStockValue[0] = Double.valueOf(Utils.formatDecimal(((float) (stockPrice * soldStocksOwned))));
                                }
                            }

                            if (preStockConverted[0] != 0) {
                                singleStockPrice[0] = Double.valueOf(Utils.formatDecimal((float) preStockConverted[0]));
                            } else {
                                if (multipliedValue != 0) {
                                    singleStockPrice[0] = Double.valueOf(Utils.formatDecimal((float) multipliedValue));
                                } else {
                                    singleStockPrice[0] = Double.valueOf(Utils.formatDecimal((float) stockPrice));
                                }
                            }

                            final double brokerFees;

                            if (StockMarket.getInstance().getLocalConfig().chargeBrokerOnSale) {
                                brokerFees = Double.valueOf(Utils.formatDecimal((float) ((StockMarket.getInstance().getLocalConfig().brokerFeePercent * preStockValue[0]) + StockMarket.getInstance().getLocalConfig().brokerFeeFlat)));
                            } else {
                                brokerFees = 0;
                            }

                            final double grandTotal = Double.valueOf(Utils.formatDecimal((float) (preStockValue[0] - brokerFees)));
                            final double differencePrice = ((singleStockPrice[0] - stocks.getSymbolPrice()) * soldStocksOwned);

                            StockMarket.getEcon().depositPlayer(p, grandTotal);
                            if (!StockMarket.getInstance().getLocalConfig().stockPurchasingAccount.equals("")) {
                                StockMarket.getEcon().withdrawPlayer(StockMarket.getInstance().getLocalConfig().stockPurchasingAccount, grandTotal);
                            }
                            processedPurchases.add(String.valueOf(soldStocksOwned) + ";" + String.valueOf(preStockValue[0]) + ";" + String.valueOf(brokerFees) + ";" + String.valueOf(grandTotal) + ";" + String.valueOf(stocks.getSymbolPrice() + ";" + stocks.getID()));

                            Bukkit.getScheduler().runTaskAsynchronously(StockMarket.getInstance(), new Runnable() {
                                @Override
                                public void run() {
                                    StockMarket.getMySQL().processSale(p, stockSymbol.toUpperCase(), isConverted[0], stockPrice, stock[0].getCurrency(), Double.parseDouble(Utils.formatDecimal((float) singleStockPrice[0])), soldStocksOwned, preStockValue[0], brokerFees, grandTotal, differencePrice);
                                }
                            });
                        }

                        if (quantityToSellFinal[0] <= 0) {
                            List<String> soldMessage = StockMarket.getInstance().getLocalConfig().soldStock;
                            List<String> soldStocksMessage = StockMarket.getInstance().getLocalConfig().soldStockList;
                            SimpleDateFormat dateFormat = new SimpleDateFormat();
                            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

                            String timeStamp;
                            timeStamp = dateFormat.format(new Date());

                            for (String toSend : soldMessage) {
                                String a = toSend.replace("<date>", timeStamp + " UTC");
                                String b = a.replace("<quantity>", String.valueOf(quantityToSell));
                                String c = b.replace("<symbol>", stockSymbol.toUpperCase());
                                String d = c.replace("<company>", stock[0].getName());
                                p.sendMessage(d);
                            }

                            double totalPreStockValue = 0;
                            double totalBrokerFees = 0;
                            double totalFees = 0;
                            double totalNet = 0;
                            String[] split = new String[0];

                            for (String toHandle : processedPurchases) {
                                split = toHandle.split(";");

                                String toReplaceNet;
                                double differencePrice = (((singleStockPrice[0] - Double.valueOf(split[4])) * Double.valueOf(split[0])) - Double.valueOf(Utils.formatDecimal(Float.valueOf(split[2]))));
                                if (differencePrice > 0) {
                                    toReplaceNet = (ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "+ " + ChatColor.GREEN + Utils.formatDecimal((float) differencePrice));
                                } else {
                                    toReplaceNet = (ChatColor.RED + Utils.formatDecimal((float) differencePrice));
                                }

                                for (String toSend : soldStocksMessage) {
                                    String a = toSend.replace("<stock-id>", split[5]);
                                    String b = a.replace("<quantity-of-stock>", split[0]);
                                    String c = b.replace("<stock-value>", "(" + split[0] + " * " + String.valueOf(Utils.formatDecimal((float) singleStockPrice[0])) + ") " + Utils.formatDecimal(Float.valueOf(split[1])) + " " + StockMarket.getInstance().getLocalConfig().serverCurrency);
                                    String d = c.replace("<broker-fees>", Utils.formatDecimal(Float.valueOf(split[2])) + " " + StockMarket.getInstance().getLocalConfig().serverCurrency);
                                    String e = d.replace("<total>", Utils.formatDecimal(Float.valueOf(split[3])) + " " + StockMarket.getInstance().getLocalConfig().serverCurrency);
                                    String f = e.replace("<purchase>", String.valueOf(Utils.formatDecimal((float) (stocks.getSymbolPrice() * soldStocksOwned))) + " " + StockMarket.getInstance().getLocalConfig().serverCurrency);
                                    String g = f.replace("<net>", toReplaceNet + " " + StockMarket.getInstance().getLocalConfig().serverCurrency);
                                    p.sendMessage(g);
                                }

                                totalPreStockValue += Double.valueOf(Utils.formatDecimal(Float.valueOf(split[1])));
                                totalBrokerFees += Double.valueOf(Utils.formatDecimal(Float.valueOf(split[2])));
                                totalFees += Double.valueOf(Utils.formatDecimal(Float.valueOf(split[3])));
                                totalNet += Double.valueOf(Utils.formatDecimal((float) differencePrice));
                            }

                            String toSend = StockMarket.getInstance().getLocalConfig().soldStockFooter.replace("<total-net>", Utils.formatDecimal((float) totalNet));
                            p.sendMessage(toSend);

                            Bukkit.getPluginManager().callEvent(new StockSaleEvent(p, stockSymbol.toUpperCase(), quantityToSell, Utils.formatDecimal((float) totalPreStockValue), Utils.formatDecimal((float) totalBrokerFees), Utils.formatDecimal((float) totalFees), Utils.formatDecimal((float) (Double.valueOf(split[4]) * quantityToSell)), Utils.formatDecimal((float) totalNet)));

                            break;
                        }
                    }
                });
            });
        });
    }

    public static boolean isMarketOpen(String symbolUpperCase) throws Exception {
        try {
            String requestXml = "<?xml version='1.0' encoding='utf−8'?><request devtype='Apple_OSX' " +
                    "deployver='APPLE_DASHBOARD_1_0' app='YGoAppleStocksWidget' appver='unknown' " +
                    "api='finance' apiver='1.0.1' acknotification='0000'><query id='0' timestamp='`" +
                    "date +%s000`' type='getquotes'><list><symbol>" + symbolUpperCase + "</symbol></list>" +
                    "</query></request>";
            URL url = new URL("http://wu-quotes.apple.com/dgw?imei=42&apptype=finance");
            URLConnection con = url.openConnection();
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setConnectTimeout(20000);
            con.setReadTimeout(20000);
            con.setUseCaches(false);
            con.setDefaultUseCaches(false);
            con.setRequestProperty("Content-Type", "text/xml");
            OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
            writer.write(requestXml);
            writer.flush();
            writer.close();

            InputStreamReader reader = new InputStreamReader(con.getInputStream());
            StringBuilder buf = new StringBuilder();
            char[] cbuf = new char[2048];
            int num;
            while (-1 != (num = reader.read(cbuf))) {
                buf.append(cbuf, 0, num);
            }
            String result = buf.toString();
            String[] initalSplit = result.split("<status>");
            String[] finalSplit = initalSplit[1].split("</status>");

            return finalSplit[0].equalsIgnoreCase("1");
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
    }

    static final String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";

    public static Date getUTCdatetimeAsDate() {
        return stringDateToDate(getUTCdatetimeAsString());
    }

    public static String getUTCdatetimeAsString() {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String utcTime = sdf.format(new Date());

        return utcTime;
    }

    public static Date stringDateToDate(String strDate) {
        Date dateToReturn = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);

        try {
            dateToReturn = dateFormat.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateToReturn;
    }
}
