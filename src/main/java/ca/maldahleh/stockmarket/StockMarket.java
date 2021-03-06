package ca.maldahleh.stockmarket;

import ca.maldahleh.stockmarket.api.StockMarketAPI;
import ca.maldahleh.stockmarket.commands.StockAdminCommand;
import ca.maldahleh.stockmarket.commands.StockCommand;
import ca.maldahleh.stockmarket.config.Config;
import ca.maldahleh.stockmarket.inventories.InventoryManager;
import ca.maldahleh.stockmarket.listeners.BrokerListeners;
import ca.maldahleh.stockmarket.listeners.PlayerListeners;
import ca.maldahleh.stockmarket.storage.SQL;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class StockMarket extends JavaPlugin {
    //public static HashMap<UUID, PortfolioObject> portfolioMap = new HashMap<>();
    //public static HashMap<UUID, HistoryObject> transactionMap = new HashMap<>();
    //public static HashMap<UUID, StockHistoryObject> historyMap = new HashMap<>();
    public List<UUID> activeKiller = new ArrayList<>();

    public static StockMarket stockMarket;
    public static StockMarketAPI stockMarketAPI;

    private InventoryManager inventoryManager;

    private static Economy econ;
    private SQL mySQL;
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
            stockMarketAPI = new StockMarketAPI(this);
            config = new Config(this);
            mySQL = new SQL(this);

            inventoryManager = new InventoryManager(this);

            registerCommands();
            registerListeners();
            processAccounts();

            if (getLocalConfig().isNpcsEnabled()) {
                getServer().getPluginManager().registerEvents(new BrokerListeners(this), this);
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
        getCommand("stock").setExecutor(new StockCommand(this));
        getCommand("sma").setExecutor(new StockAdminCommand(this));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListeners(this), this);
        //getServer().getPluginManager().registerEvents(new PortfolioListener(this), this);
        //getServer().getPluginManager().registerEvents(new HistoryListener(this), this);
        //getServer().getPluginManager().registerEvents(new StockHistoryListener(this), this);
    }

    private void processAccounts() {
        if (!getEcon().hasAccount(getLocalConfig().getFeesAccount())
                && !getLocalConfig().getFeesAccount().equals("")) {
            getEcon().createPlayerAccount(getLocalConfig().getFeesAccount());
        }
        if (!getEcon().hasAccount(getLocalConfig().getStockAccount())
                && !getLocalConfig().getStockAccount().equals("")) {
            getEcon().createPlayerAccount(getLocalConfig().getStockAccount());
        }
        if (!getEcon().hasAccount(getLocalConfig().getStockPurchasingAccount())
                && !getLocalConfig().getStockPurchasingAccount().equals("")) {
            getEcon().createPlayerAccount(getLocalConfig().getStockPurchasingAccount());
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

    public InventoryManager getInventoryManager() { return inventoryManager; }

    public Config getLocalConfig () { return config; }

    public SQL getMySQL() {
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