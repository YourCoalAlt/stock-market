package ca.maldahleh.stockmarket.config;

import ca.maldahleh.stockmarket.StockMarket;
import ca.maldahleh.stockmarket.utils.Utils;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class Config {
    private StockMarket stockMarket;

    private boolean npcsEnabled;
    private boolean commandsDisabled;
    private boolean disableTradingWhenClosed;
    private String feesAccount;
    private String stockAccount;
    private String stockPurchasingAccount;
    private boolean blockNonUSDSales;
    private boolean convertToUSD;
    private boolean preventSaleOfZeroValue;
    private boolean abusePrevention;
    private long abusePreventionSeconds;
    private boolean pennyStockCheck;
    private double pennyStockMinimum;
    private double multiplier;
    private double brokerFeeFlat;
    private double brokerFeePercent;
    private boolean chargeBrokerOnSale;
    private boolean closeInventoryOnSimple;

    private List<String> translatedHelp = new ArrayList<>();

    private String serverCurrency;
    private String currencyNotSupported;
    private String dayNotPassed;
    private List<String> boughtStock = new ArrayList<>();
    private List<String> soldStock = new ArrayList<>();
    private List<String> soldStockList = new ArrayList<>();
    private String soldStockFooter;
    private String noPurchaseNonUSD;
    private String noStockToSell;
    private String errorOccured;
    private String invalidPrice;
    private String improperCompareSyntax;
    private String purgeTables;
    private String compareMinimumTwo;
    private String compareMaximumThree;
    private String invalidStockMessage;
    private String invalidSyntax;
    private String configReloaded;
    private String noPermission;
    private String playerRequired;
    private String notEnoughMoney;
    private String invalidQuantity;
    private String invalidForexQuote;
    private String noStocksTargetPlayer;
    private String noTransactionsTargetPlayer;
    private String noTransactionsStockTarget;
    private String noStockPlayers;
    private String noTradingWhenMarketIsClosed;
    private String pennyStockNotMet;

    private String stockCommandDisabled;
    private String stockBrokerSpawned;
    private String stockBrokersDisabled;
    private String stockBrokerRemove;
    private String stockBrokerRemoved;
    private String stockBrokerRemovalModeDisabled;
    private String stockBrokerInvalidArguments;
    private String stockBrokerBuyStock;
    private String stockBrokerSellStock;

    private String stockMarketBrokerStockLookup;
    private String stockMarketBrokerForexLookup;
    private String stockBrokerCompare;
    private String stockBrokerPortfolio;
    private String stockBrokerStockHistory;
    private String stockBrokerTransactionHistory;

    private boolean mysqlEnabled;
    private String mysqlIP;
    private int mysqlPort;
    private String mysqlUsername;
    private String mysqlPassword;
    private String mysqlDatabase;

    public Config(StockMarket stockMarket) {
        this.stockMarket = stockMarket;

        stockMarket.saveDefaultConfig();
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

    public StockMarket getStockMarket() {
        return stockMarket;
    }

    public boolean isNpcsEnabled() {
        return npcsEnabled;
    }

    public boolean isCommandsDisabled() {
        return commandsDisabled;
    }

    public boolean isDisableTradingWhenClosed() {
        return disableTradingWhenClosed;
    }

    public String getFeesAccount() {
        return feesAccount;
    }

    public String getStockAccount() {
        return stockAccount;
    }

    public String getStockPurchasingAccount() {
        return stockPurchasingAccount;
    }

    public boolean isBlockNonUSDSales() {
        return blockNonUSDSales;
    }

    public boolean isConvertToUSD() {
        return convertToUSD;
    }

    public boolean isPreventSaleOfZeroValue() {
        return preventSaleOfZeroValue;
    }

    public boolean isAbusePrevention() {
        return abusePrevention;
    }

    public long getAbusePreventionSeconds() {
        return abusePreventionSeconds;
    }

    public boolean isPennyStockCheck() {
        return pennyStockCheck;
    }

    public double getPennyStockMinimum() {
        return pennyStockMinimum;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public double getBrokerFeeFlat() {
        return brokerFeeFlat;
    }

    public double getBrokerFeePercent() {
        return brokerFeePercent;
    }

    public boolean isChargeBrokerOnSale() {
        return chargeBrokerOnSale;
    }

    public boolean isCloseInventoryOnSimple() {
        return closeInventoryOnSimple;
    }

    public List<String> getTranslatedHelp() {
        return translatedHelp;
    }

    public String getServerCurrency() {
        return serverCurrency;
    }

    public String getCurrencyNotSupported() {
        return currencyNotSupported;
    }

    public String getDayNotPassed() {
        return dayNotPassed;
    }

    public List<String> getBoughtStock() {
        return boughtStock;
    }

    public List<String> getSoldStock() {
        return soldStock;
    }

    public List<String> getSoldStockList() {
        return soldStockList;
    }

    public String getSoldStockFooter() {
        return soldStockFooter;
    }

    public String getNoPurchaseNonUSD() {
        return noPurchaseNonUSD;
    }

    public String getNoStockToSell() {
        return noStockToSell;
    }

    public String getErrorOccured() {
        return errorOccured;
    }

    public String getInvalidPrice() {
        return invalidPrice;
    }

    public String getImproperCompareSyntax() {
        return improperCompareSyntax;
    }

    public String getPurgeTables() {
        return purgeTables;
    }

    public String getCompareMinimumTwo() {
        return compareMinimumTwo;
    }

    public String getCompareMaximumThree() {
        return compareMaximumThree;
    }

    public String getInvalidStockMessage() {
        return invalidStockMessage;
    }

    public String getInvalidSyntax() {
        return invalidSyntax;
    }

    public String getConfigReloaded() {
        return configReloaded;
    }

    public String getNoPermission() {
        return noPermission;
    }

    public String getPlayerRequired() {
        return playerRequired;
    }

    public String getNotEnoughMoney() {
        return notEnoughMoney;
    }

    public String getInvalidQuantity() {
        return invalidQuantity;
    }

    public String getInvalidForexQuote() {
        return invalidForexQuote;
    }

    public String getNoStocksTargetPlayer() {
        return noStocksTargetPlayer;
    }

    public String getNoTransactionsTargetPlayer() {
        return noTransactionsTargetPlayer;
    }

    public String getNoTransactionsStockTarget() {
        return noTransactionsStockTarget;
    }

    public String getNoStockPlayers() {
        return noStockPlayers;
    }

    public String getNoTradingWhenMarketIsClosed() {
        return noTradingWhenMarketIsClosed;
    }

    public String getPennyStockNotMet() {
        return pennyStockNotMet;
    }

    public String getStockCommandDisabled() {
        return stockCommandDisabled;
    }

    public String getStockBrokerSpawned() {
        return stockBrokerSpawned;
    }

    public String getStockBrokersDisabled() {
        return stockBrokersDisabled;
    }

    public String getStockBrokerRemove() {
        return stockBrokerRemove;
    }

    public String getStockBrokerRemoved() {
        return stockBrokerRemoved;
    }

    public String getStockBrokerRemovalModeDisabled() {
        return stockBrokerRemovalModeDisabled;
    }

    public String getStockBrokerInvalidArguments() {
        return stockBrokerInvalidArguments;
    }

    public String getStockBrokerBuyStock() {
        return stockBrokerBuyStock;
    }

    public String getStockBrokerSellStock() {
        return stockBrokerSellStock;
    }

    public String getStockMarketBrokerStockLookup() {
        return stockMarketBrokerStockLookup;
    }

    public String getStockMarketBrokerForexLookup() {
        return stockMarketBrokerForexLookup;
    }

    public String getStockBrokerCompare() {
        return stockBrokerCompare;
    }

    public String getStockBrokerPortfolio() {
        return stockBrokerPortfolio;
    }

    public String getStockBrokerStockHistory() {
        return stockBrokerStockHistory;
    }

    public String getStockBrokerTransactionHistory() {
        return stockBrokerTransactionHistory;
    }

    public boolean isMysqlEnabled() {
        return mysqlEnabled;
    }

    public String getMysqlIP() {
        return mysqlIP;
    }

    public int getMysqlPort() {
        return mysqlPort;
    }

    public String getMysqlUsername() {
        return mysqlUsername;
    }

    public String getMysqlPassword() {
        return mysqlPassword;
    }

    public String getMysqlDatabase() {
        return mysqlDatabase;
    }
}