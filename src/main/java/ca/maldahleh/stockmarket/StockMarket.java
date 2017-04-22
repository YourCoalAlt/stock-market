package ca.maldahleh.stockmarket;

import ca.maldahleh.stockmarket.api.StockMarketAPI;
import ca.maldahleh.stockmarket.commands.StockAdminCommand;
import ca.maldahleh.stockmarket.commands.StockCommand;
import ca.maldahleh.stockmarket.config.Config;
import ca.maldahleh.stockmarket.inventories.portfolio.PortfolioListener;
import ca.maldahleh.stockmarket.inventories.portfolio.PortfolioObject;
import ca.maldahleh.stockmarket.inventories.stockhistory.StockHistoryListener;
import ca.maldahleh.stockmarket.inventories.stockhistory.StockHistoryObject;
import ca.maldahleh.stockmarket.inventories.transactionhistory.HistoryListener;
import ca.maldahleh.stockmarket.inventories.transactionhistory.HistoryObject;
import ca.maldahleh.stockmarket.listeners.BrokerListeners;
import ca.maldahleh.stockmarket.listeners.PlayerListeners;
import ca.maldahleh.stockmarket.stocks.StockPlayer;
import ca.maldahleh.stockmarket.utils.MySQL;

import net.milkbowl.vault.economy.Economy;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class StockMarket extends JavaPlugin {
    public static HashMap<UUID, StockPlayer> cachedPlayers = new HashMap<>();
    //public static HashMap<UUID, PortfolioObject> portfolioMap = new HashMap<>();
    //public static HashMap<UUID, HistoryObject> transactionMap = new HashMap<>();
    //public static HashMap<UUID, StockHistoryObject> historyMap = new HashMap<>();
    public List<UUID> activeKiller = new ArrayList<>();

    public static StockMarket stockMarket;
    public static StockMarketAPI stockMarketAPI;

    private static Economy econ;
    private static MySQL mySQL;
    private Config config;

    @Override
    public void onEnable() {
        try {
            getLogger().info("Stock Market - Enabling...");
            if (!setupEconomy()) {
                getLogger().severe("Stock Market - Vault or a Vault economy plugin not found, disabling...");
                getServer().getPluginManager().disablePlugin(this);
                return;
            }

            stockMarket = this;
            stockMarketAPI = new StockMarketAPI();
            saveDefaultConfig();

            Logger logger = (Logger) LogManager.getRootLogger();
            logger.setLevel(Level.ALL);
            logger.addFilter(new Filter() {
                @Override
                public Result filter(LogEvent event) {
                    if (event.getMessage().toString().contains("Parsing CSV line:") || event.getMessage().toString().contains("Sending request: http://finance.yahoo.com")
                            || event.getMessage().toString().contains("Sending request: http://ichart.yahoo.com") || event.getMessage().toString().contains("Cannot find time zone for exchange suffix")
                            || event.getMessage().toString().contains("ca.maldahleh.hikari.hikari.HikariDataSource") || event.getMessage().toString().contains("Please check http://ehcache.org")) {
                        return Result.DENY;
                    }
                    return null;
                }

                @Override
                public Result filter(Logger paramLogger, Level paramLevel, Marker paramMarker, String paramString, Object... paramArrayOfObject) {
                    return null;
                }

                @Override
                public Result filter(Logger paramLogger, Level paramLevel, Marker paramMarker, Object paramObject, Throwable paramThrowable) {
                    return null;
                }

                @Override
                public Result filter(Logger paramLogger, Level paramLevel, Marker paramMarker, Message paramMessage, Throwable paramThrowable) {
                    return null;
                }

                @Override
                public Result getOnMatch() {
                    return null;
                }

                @Override
                public Result getOnMismatch() {
                    return null;
                }

            });

            registerCommands();
            registerListeners();
            config = new Config(this);
            mySQL = new MySQL(this);
            mySQL.connectDatabase();
            mySQL.createTables();

            if (getServer().getPluginManager().isPluginEnabled("StockMarket")) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                    @Override
                    public void run() {
                        processAccounts();

                        if (getLocalConfig().npcsEnabled) {
                            if (!getServer().getPluginManager().isPluginEnabled("Citizens")) {
                                getLogger().info("Stock Market - NPCs enabled, but Citizens not found, disabling....");
                                getServer().getPluginManager().disablePlugin(StockMarket.getInstance());
                                return;
                            }

                            getServer().getPluginManager().registerEvents(new BrokerListeners(StockMarket.getInstance()), StockMarket.getInstance());
                        }
                    }
                }, 0L);
            }
        } catch (Exception e) {
            getLogger().severe("Stock Market - Database connection failed.");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("Stock Market - Disabling...");
        stockMarket = null;
        getLogger().info("Stock Market - Disabled.");
    }

    private void registerCommands() {
        getCommand("stockmarket").setExecutor(new StockCommand(this));
        getCommand("sm").setExecutor(new StockCommand(this));
        getCommand("stocks").setExecutor(new StockCommand(this));
        getCommand("stock").setExecutor(new StockCommand(this));
        getCommand("sma").setExecutor(new StockAdminCommand(this));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListeners(this), this);
        //getServer().getPluginManager().registerEvents(new PortfolioListener(this), this);
        //getServer().getPluginManager().registerEvents(new HistoryListener(this), this);
        //getServer().getPluginManager().registerEvents(new StockHistoryListener(this), this);
    }

    public void processAccounts () {
        if (!getEcon().hasAccount(getLocalConfig().feesAccount) && !getLocalConfig().feesAccount.equals("")) {
            getEcon().createPlayerAccount(getLocalConfig().feesAccount);
        }
        if (!getEcon().hasAccount(getLocalConfig().stockAccount) && !getLocalConfig().stockAccount.equals("")) {
            getEcon().createPlayerAccount(getLocalConfig().stockAccount);
        }
        if (!getEcon().hasAccount(getLocalConfig().stockPurchasingAccount) && !getLocalConfig().stockPurchasingAccount.equals("")) {
            getEcon().createPlayerAccount(getLocalConfig().stockPurchasingAccount);
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public Config getLocalConfig () { return config; }

    public static MySQL getMySQL() {
        return mySQL;
    }

    public static Economy getEcon() {
        return econ;
    }

    public static StockMarket getInstance() {
        return stockMarket;
    }

    public static StockMarketAPI getStockMarketAPI() {
        return stockMarketAPI;
    }
}
