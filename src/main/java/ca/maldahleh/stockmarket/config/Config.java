package ca.maldahleh.stockmarket.config;

import ca.maldahleh.stockmarket.StockMarket;
import ca.maldahleh.stockmarket.utils.Utils;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class Config {
    private StockMarket stockMarket;

    public boolean npcsEnabled;
    public boolean commandsDisabled;
    public boolean disableTradingWhenClosed;
    public String feesAccount;
    public String stockAccount;
    public String stockPurchasingAccount;
    public boolean blockNonUSDSales;
    public boolean convertToUSD;
    public boolean preventSaleOfZeroValue;
    public boolean abusePrevention;
    public long abusePreventionSeconds;
    public boolean pennyStockCheck;
    public double pennyStockMinimum;
    public double multiplier;
    public double brokerFeeFlat;
    public double brokerFeePercent;
    public boolean chargeBrokerOnSale;
    public boolean closeInventoryOnSimple;

    public List<String> translatedHelp = new ArrayList<>();

    public String serverCurrency;
    public String currencyNotSupported;
    public String dayNotPassed;
    public List<String> boughtStock = new ArrayList<>();
    public List<String> soldStock = new ArrayList<>();
    public List<String> soldStockList = new ArrayList<>();
    public String soldStockFooter;
    public String noPurchaseNonUSD;
    public String noStockToSell;
    public String errorOccured;
    public String invalidPrice;
    public String improperCompareSyntax;
    public String purgeTables;
    public String compareMinimumTwo;
    public String compareMaximumThree;
    public String invalidStockMessage;
    public String invalidSyntax;
    public String configReloaded;
    public String noPermission;
    public String playerRequired;
    public String notEnoughMoney;
    public String invalidQuantity;
    public String invalidForexQuote;
    public String noStocksTargetPlayer;
    public String noTransactionsTargetPlayer;
    public String noTransactionsStockTarget;
    public String stockPurchaseBroadcast;
    public String stockSaleBroadcast;
    public String noHistoricalData;
    public String noStockPlayers;
    public String noTradingWhenMarketIsClosed;
    public String pennyStockNotMet;

    public String stockCommandDisabled;
    public String stockBrokerSpawned;
    public String stockBrokersDisabled;
    public String stockBrokerRemove;
    public String stockBrokerRemoved;
    public String stockBrokerRemovalModeDisabled;
    public String stockBrokerInvalidArguments;
    public String stockBrokerBuyStock;
    public String stockBrokerSellStock;

    public String stockMarketBrokerStockLookup;
    public String stockMarketBrokerForexLookup;
    public String stockBrokerCompare;
    public String stockBrokerPortfolio;
    public String stockBrokerStockHistory;
    public String stockBrokerTransactionHistory;

    public boolean mysqlEnabled;
    public String mysqlIP;
    public int mysqlPort;
    public String mysqlUsername;
    public String mysqlPassword;
    public String mysqlDatabase;

    public Config(StockMarket stockMarket) {
        this.stockMarket = stockMarket;
        loadSQLValues();
        loadConfiguration();
    }

    public void loadConfiguration() {
        translatedHelp.clear();
        boughtStock.clear();
        soldStock.clear();
        soldStockList.clear();

        pennyStockCheck = stockMarket.getConfig().getBoolean("options.penny-stock-check");
        pennyStockMinimum = stockMarket.getConfig().getDouble("options.penny-stock-minimum");
        npcsEnabled = stockMarket.getConfig().getBoolean("options.npc-enabled");
        commandsDisabled = stockMarket.getConfig().getBoolean("options.commands-disabled");
        disableTradingWhenClosed = stockMarket.getConfig().getBoolean("options.disable-trading-when-closed");
        feesAccount = stockMarket.getConfig().getString("options.fees-account");
        stockAccount = stockMarket.getConfig().getString("options.stock-account");
        stockPurchasingAccount = stockMarket.getConfig().getString("options.stock-purchasing-account");
        blockNonUSDSales = stockMarket.getConfig().getBoolean("options.prevent-non-usd-sale");
        convertToUSD = stockMarket.getConfig().getBoolean("options.convert-non-usd-to-usd");
        preventSaleOfZeroValue = stockMarket.getConfig().getBoolean("options.prevent-sale-of-zero-value");
        abusePrevention = stockMarket.getConfig().getBoolean("options.abuse-prevention");
        abusePreventionSeconds = stockMarket.getConfig().getLong("options.abuse-prevention-time");
        multiplier = stockMarket.getConfig().getDouble("options.multiplier");
        brokerFeeFlat = stockMarket.getConfig().getDouble("options.broker-fee-flat");
        brokerFeePercent = (stockMarket.getConfig().getDouble("options.broker-fee-percent") / 100);
        chargeBrokerOnSale = stockMarket.getConfig().getBoolean("options.charge-broker-on-sale");
        closeInventoryOnSimple = stockMarket.getConfig().getBoolean("options.close-inventory-on-simple");

        for (String toTranslate : stockMarket.getConfig().getStringList("messages.help")) {
            String a = toTranslate.replace("<percent>", String.valueOf((brokerFeePercent * 100)));
            String b = a.replace("<flat-rate>", Utils.formatDecimal((float) brokerFeeFlat));
            translatedHelp.add(ChatColor.translateAlternateColorCodes('&', b));
        }

        for (String toTranslate : stockMarket.getConfig().getStringList("messages.bought-stock")) {
            boughtStock.add(ChatColor.translateAlternateColorCodes('&', toTranslate));
        }

        for (String toTranslate : stockMarket.getConfig().getStringList("messages.sold-stock")) {
            soldStock.add(ChatColor.translateAlternateColorCodes('&', toTranslate));
        }

        for (String toTranslate : stockMarket.getConfig().getStringList("messages.stock-sold-report")) {
            soldStockList.add(ChatColor.translateAlternateColorCodes('&', toTranslate));
        }

        soldStockFooter = ChatColor.translateAlternateColorCodes('&', stockMarket.getConfig().getString("messages.stock-sold-footer"));

        serverCurrency = stockMarket.getConfig().getString("messages.server-currency");
        purgeTables = ChatColor.translateAlternateColorCodes('&', stockMarket.getConfig().getString("messages.purged-tables"));
        dayNotPassed = ChatColor.translateAlternateColorCodes('&', stockMarket.getConfig().getString("messages.day-not-passed"));
        currencyNotSupported = ChatColor.translateAlternateColorCodes('&', stockMarket.getConfig().getString("messages.purchase-currency-not-supported"));
        noPurchaseNonUSD = ChatColor.translateAlternateColorCodes('&', stockMarket.getConfig().getString("messages.no-purchase-non-USD"));
        noStockToSell = ChatColor.translateAlternateColorCodes('&', stockMarket.getConfig().getString("messages.no-stock-to-sell"));
        errorOccured = ChatColor.translateAlternateColorCodes('&', stockMarket.getConfig().getString("messages.error-occured"));
        notEnoughMoney = ChatColor.translateAlternateColorCodes('&', stockMarket.getConfig().getString("messages.not-enough-money"));
        invalidPrice = ChatColor.translateAlternateColorCodes('&', stockMarket.getConfig().getString("messages.invalid-price"));
        improperCompareSyntax = ChatColor.translateAlternateColorCodes('&', stockMarket.getConfig().getString("messages.improper-compare-syntax"));
        compareMinimumTwo = ChatColor.translateAlternateColorCodes('&', stockMarket.getConfig().getString("messages.compare-minimum-two"));
        compareMaximumThree = ChatColor.translateAlternateColorCodes('&', stockMarket.getConfig().getString("messages.compare-maximum-three"));
        invalidStockMessage = ChatColor.translateAlternateColorCodes('&', stockMarket.getConfig().getString("messages.invalid-stock"));
        invalidSyntax = ChatColor.translateAlternateColorCodes('&', stockMarket.getConfig().getString("messages.invalid-syntax"));
        configReloaded = ChatColor.translateAlternateColorCodes('&', stockMarket.getConfig().getString("messages.config-reloaded"));
        noPermission = ChatColor.translateAlternateColorCodes('&', stockMarket.getConfig().getString("messages.no-permission"));
        playerRequired = ChatColor.translateAlternateColorCodes('&', stockMarket.getConfig().getString("messages.you-need-to-be-a-player"));
        invalidQuantity = ChatColor.translateAlternateColorCodes('&', stockMarket.getConfig().getString("messages.invalid-quantity"));
        invalidForexQuote = ChatColor.translateAlternateColorCodes('&', stockMarket.getConfig().getString("messages.invalid-forex-quote"));
        noStocksTargetPlayer = ChatColor.translateAlternateColorCodes('&', stockMarket.getConfig().getString("messages.no-stocks-to-list"));
        noTransactionsTargetPlayer = ChatColor.translateAlternateColorCodes('&', stockMarket.getConfig().getString("messages.no-transactions-to-list"));
        stockPurchaseBroadcast = ChatColor.translateAlternateColorCodes('&', stockMarket.getConfig().getString("messages.stock-purchase-broadcast"));
        stockSaleBroadcast = ChatColor.translateAlternateColorCodes('&', stockMarket.getConfig().getString("messages.stock-sale-broadcast"));
        noHistoricalData = ChatColor.translateAlternateColorCodes('&', stockMarket.getConfig().getString("messages.no-historical-data"));
        noStockPlayers = ChatColor.translateAlternateColorCodes('&', stockMarket.getConfig().getString("messages.no-stock-players"));
        noTransactionsStockTarget = ChatColor.translateAlternateColorCodes('&', stockMarket.getConfig().getString("messages.no-stock-transactions-to-list"));
        noTradingWhenMarketIsClosed = ChatColor.translateAlternateColorCodes('&', stockMarket.getConfig().getString("messages.no-trading-when-market-is-closed"));
        pennyStockNotMet = ChatColor.translateAlternateColorCodes('&', stockMarket.getConfig().getString("messages.penny-stock-minimum-not-met"));

        stockCommandDisabled = ChatColor.translateAlternateColorCodes('&', stockMarket.getConfig().getString("messages.stock-commands-disabled"));
        stockBrokerSpawned = ChatColor.translateAlternateColorCodes('&', stockMarket.getConfig().getString("messages.stock-broker-spawned"));
        stockBrokersDisabled = ChatColor.translateAlternateColorCodes('&', stockMarket.getConfig().getString("messages.stock-brokers-disabled"));
        stockBrokerRemove = ChatColor.translateAlternateColorCodes('&', stockMarket.getConfig().getString("messages.stock-broker-removal"));
        stockBrokerRemoved = ChatColor.translateAlternateColorCodes('&', stockMarket.getConfig().getString("messages.stock-broker-removed"));
        stockBrokerRemovalModeDisabled = ChatColor.translateAlternateColorCodes('&', stockMarket.getConfig().getString("messages.stock-broker-no-npc"));
        stockBrokerInvalidArguments = ChatColor.translateAlternateColorCodes('&', stockMarket.getConfig().getString("messages.stock-broker-invalid-provided"));

        stockBrokerBuyStock = ChatColor.translateAlternateColorCodes('&', stockMarket.getConfig().getString("messages.stock-broker-buy-stocks"));
        stockBrokerSellStock = ChatColor.translateAlternateColorCodes('&', stockMarket.getConfig().getString("messages.stock-broker-sell-stocks"));
        stockMarketBrokerStockLookup = ChatColor.translateAlternateColorCodes('&', stockMarket.getConfig().getString("messages.stock-broker-stock-symbol-lookup"));
        stockMarketBrokerForexLookup = ChatColor.translateAlternateColorCodes('&', stockMarket.getConfig().getString("messages.stock-broker-forex-symbol-lookup"));
        stockBrokerCompare = ChatColor.translateAlternateColorCodes('&', stockMarket.getConfig().getString("messages.stock-broker-compare"));
        stockBrokerPortfolio = ChatColor.translateAlternateColorCodes('&', stockMarket.getConfig().getString("messages.stock-broker-portfolio"));
        stockBrokerStockHistory = ChatColor.translateAlternateColorCodes('&', stockMarket.getConfig().getString("messages.stock-broker-stock-history"));
        stockBrokerTransactionHistory = ChatColor.translateAlternateColorCodes('&', stockMarket.getConfig().getString("messages.stock-broker-transaction-history"));
    }

    private void loadSQLValues() {
        mysqlEnabled = stockMarket.getConfig().getBoolean("database.mysql-enabled");
        mysqlIP = stockMarket.getConfig().getString("database.ip");
        mysqlPort = stockMarket.getConfig().getInt("database.port");
        mysqlDatabase = stockMarket.getConfig().getString("database.database");
        mysqlUsername = stockMarket.getConfig().getString("database.username");
        mysqlPassword = stockMarket.getConfig().getString("database.password");
    }
}
