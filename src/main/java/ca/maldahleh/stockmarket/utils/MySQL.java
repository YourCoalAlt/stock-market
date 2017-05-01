package ca.maldahleh.stockmarket.utils;

import ca.maldahleh.stockmarket.StockMarket;
import ca.maldahleh.stockmarket.handling.PlayerHandling;
import ca.maldahleh.stockmarket.stocks.MergedStock;
import ca.maldahleh.stockmarket.stocks.StockPlayer;
import ca.maldahleh.stockmarket.stocks.Stocks;
import ca.maldahleh.stockmarket.stocks.Transactions;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MySQL {
    private StockMarket stockMarket;
    public HikariDataSource hikari;

    public MySQL(StockMarket stockMarket) {
        this.stockMarket = stockMarket;

        connectDatabase();
        createTables();
    }

    public void connectDatabase() {
        if (stockMarket.getLocalConfig().mysqlEnabled) {
            hikari = new HikariDataSource();
            hikari.setMaximumPoolSize(50);
            hikari.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");

            hikari.addDataSourceProperty("serverName", stockMarket.getLocalConfig().mysqlIP);
            hikari.addDataSourceProperty("port", stockMarket.getLocalConfig().mysqlPort);
            hikari.addDataSourceProperty("databaseName", stockMarket.getLocalConfig().mysqlDatabase);
            hikari.addDataSourceProperty("user", stockMarket.getLocalConfig().mysqlUsername);
            hikari.addDataSourceProperty("password", stockMarket.getLocalConfig().mysqlPassword);
        } else {
            HikariConfig config = new HikariConfig();
            config.setPoolName("StockMarket-SQLitePool");
            config.setDriverClassName("org.sqlite.JDBC");
            config.setJdbcUrl("jdbc:sqlite:plugins/StockMarket/StockMarket.db");
            config.setConnectionTestQuery("SELECT 1");
            config.setMaxLifetime(60000);
            config.setIdleTimeout(45000);
            config.setMaximumPoolSize(100);
            hikari = new HikariDataSource(config);
        }
    }

    public void createTables() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = hikari.getConnection();

            if (stockMarket.getLocalConfig().mysqlEnabled) {
                String statement = "CREATE TABLE IF NOT EXISTS sm_players(player_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, player_uuid VARCHAR(36), player_name VARCHAR(16))";
                preparedStatement = connection.prepareStatement(statement);
                preparedStatement.execute();

                statement = "CREATE TABLE IF NOT EXISTS sm_stocks(stocks_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, player_id INTEGER, stock_date DATETIME, symbol VARCHAR(12), symbol_price DOUBLE, quantity INTEGER, stock_value DOUBLE, broker_fees DOUBLE, total_price DOUBLE)";
                preparedStatement = connection.prepareStatement(statement);
                preparedStatement.execute();

                statement = "CREATE TABLE IF NOT EXISTS sm_transactions(tran_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, tran_type ENUM('purchase', 'sale'), tran_date DATETIME, player_id INTEGER, symbol VARCHAR(12), symbol_price DOUBLE, quantity INTEGER, stock_value DOUBLE, broker_fees DOUBLE, total_price DOUBLE, earnings DOUBLE)";
                preparedStatement = connection.prepareStatement(statement);
                preparedStatement.execute();
            } else {
                String statement = "CREATE TABLE IF NOT EXISTS sm_players(player_id INTEGER NOT NULL, player_uuid VARCHAR(36), player_name VARCHAR(16), PRIMARY KEY (player_id))";
                preparedStatement = connection.prepareStatement(statement);
                preparedStatement.execute();

                statement = "CREATE TABLE IF NOT EXISTS sm_stocks(stocks_id INTEGER NOT NULL, player_id INTEGER, stock_date DATETIME, symbol VARCHAR(12), symbol_price DOUBLE, quantity INTEGER, stock_value DOUBLE, broker_fees DOUBLE, total_price DOUBLE, PRIMARY KEY (stocks_id))";
                preparedStatement = connection.prepareStatement(statement);
                preparedStatement.execute();

                statement = "CREATE TABLE IF NOT EXISTS sm_transactions(tran_id INTEGER NOT NULL, tran_type VARCHAR(8), tran_date DATETIME, player_id INTEGER, is_converted BOOLEAN, inital_price_single DOUBLE, inital_currency VARCHAR(3), symbol VARCHAR(12), symbol_price DOUBLE, quantity INTEGER, stock_value DOUBLE, broker_fees DOUBLE, total_price DOUBLE, earnings DOUBLE, PRIMARY KEY (tran_id))";
                preparedStatement = connection.prepareStatement(statement);
                preparedStatement.execute();
            }
        } catch (Exception e) {
            Bukkit.getLogger().info("Stock Market - Database connection failed, disabling plugin...");
            Bukkit.getServer().getPluginManager().disablePlugin(StockMarket.getInstance());
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException ignored) {
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    public void processPurchase(final Player p, final String symbol, final double singlePrice, final int amount, final double stockValue, final double brokerFees, final double totalPrice) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            StockPlayer stockPlayer = PlayerHandling.getPlayer(p);
            connection = hikari.getConnection();

            if (!(stockPlayer == null)) {
                if (stockMarket.getLocalConfig().mysqlEnabled) {
                    preparedStatement = connection.prepareStatement("INSERT INTO sm_transactions (tran_type, tran_date, player_id, symbol, symbol_price, quantity, stock_value, broker_fees, total_price) VALUES ('purchase', UTC_TIMESTAMP(), ?, ?, ?, ?, ?, ?, ?)");
                } else {
                    preparedStatement = connection.prepareStatement("INSERT INTO sm_transactions (tran_type, tran_date, player_id, symbol, symbol_price, quantity, stock_value, broker_fees, total_price) VALUES ('purchase', date('now'), ?, ?, ?, ?, ?, ?, ?)");
                }
                preparedStatement.setInt(1, stockPlayer.getPlayerID());
                preparedStatement.setString(2, symbol.toUpperCase());
                preparedStatement.setDouble(3, Double.valueOf(Utils.formatDecimal((float) singlePrice)));
                preparedStatement.setInt(4, amount);
                preparedStatement.setDouble(5, Double.valueOf(Utils.formatDecimal((float) stockValue)));
                preparedStatement.setDouble(6, Double.valueOf(Utils.formatDecimal((float) brokerFees)));
                preparedStatement.setDouble(7, Double.valueOf(Utils.formatDecimal((float) totalPrice)));
                preparedStatement.executeUpdate();

                if (stockMarket.getLocalConfig().mysqlEnabled) {
                    preparedStatement = connection.prepareStatement("INSERT INTO sm_stocks (player_id, stock_date, symbol, symbol_price, quantity, stock_value, broker_fees, total_price) VALUES (?, UTC_TIMESTAMP(), ?, ?, ?, ?, ?, ?)");
                } else {
                    preparedStatement = connection.prepareStatement("INSERT INTO sm_stocks (player_id, stock_date, symbol, symbol_price, quantity, stock_value, broker_fees, total_price) VALUES (?, date('now'), ?, ?, ?, ?, ?, ?)");
                }
                preparedStatement.setInt(1, stockPlayer.getPlayerID());
                preparedStatement.setString(2, symbol.toUpperCase());
                preparedStatement.setDouble(3, Double.valueOf(Utils.formatDecimal((float) singlePrice)));
                preparedStatement.setInt(4, amount);
                preparedStatement.setDouble(5, Double.valueOf(Utils.formatDecimal((float) stockValue)));
                preparedStatement.setDouble(6, Double.valueOf(Utils.formatDecimal((float) brokerFees)));
                preparedStatement.setDouble(7, Double.valueOf(Utils.formatDecimal((float) totalPrice)));
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException ignored) {
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ignored) {
                }
            }
        }

    }

    public void processSale(final Player p, final String symbol, final boolean isConverted, final double initalSingle, final String initalCurrency, final double singlePrice, final int amount, final double stockValue, final double brokerFees, final double totalPrice, final double difference) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            StockPlayer stockPlayer = PlayerHandling.getPlayer(p);
            connection = hikari.getConnection();

            if (stockPlayer == null) {
                return;
            }

            if (stockMarket.getLocalConfig().mysqlEnabled) {
                preparedStatement = connection.prepareStatement("INSERT INTO sm_transactions (tran_type, tran_date, player_id, is_converted, inital_price_single, inital_currency, symbol, symbol_price, quantity, stock_value, broker_fees, total_price, earnings) VALUES ('sale', UTC_TIMESTAMP(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            } else {
                preparedStatement = connection.prepareStatement("INSERT INTO sm_transactions (tran_type, tran_date, player_id, is_converted, inital_price_single, inital_currency, symbol, symbol_price, quantity, stock_value, broker_fees, total_price, earnings) VALUES ('sale', date('now'), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            }
            preparedStatement.setInt(1, stockPlayer.getPlayerID());
            preparedStatement.setBoolean(2, isConverted);
            preparedStatement.setDouble(3, initalSingle);
            preparedStatement.setString(4, initalCurrency);
            preparedStatement.setString(5, symbol.toUpperCase());
            preparedStatement.setDouble(6, Double.valueOf(Utils.formatDecimal((float) singlePrice)));
            preparedStatement.setInt(7, amount);
            preparedStatement.setDouble(8, Double.valueOf(Utils.formatDecimal((float) stockValue)));
            preparedStatement.setDouble(9, Double.valueOf(Utils.formatDecimal((float) brokerFees)));
            preparedStatement.setDouble(10, Double.valueOf(Utils.formatDecimal((float) totalPrice)));
            preparedStatement.setDouble(11, Double.valueOf(Utils.formatDecimal((float) difference)));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException ignored) {
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    public List<Stocks> getPlayerSymbolOwned(final Player p, final String symbol) {
        final ArrayList<Stocks> stocksOwned = new ArrayList<>();

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = StockMarket.getMySQL().hikari.getConnection();

            StockPlayer stockPlayer = PlayerHandling.getPlayer(p);
            if (stockPlayer != null) {
                preparedStatement = connection.prepareStatement("SELECT stocks_id, player_id, stock_date, is_converted, inital_price_single, inital_currency, symbol, symbol_price, quantity, stock_value, broker_fees, total_price FROM sm_stocks WHERE player_id = ? AND symbol = ? ORDER BY stocks_id");
                preparedStatement.setInt(1, stockPlayer.getPlayerID());
                preparedStatement.setString(2, symbol);
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    stocksOwned.add(new Stocks(resultSet.getInt(1), resultSet.getInt(2), resultSet.getTimestamp(3), resultSet.getBoolean(4), resultSet.getDouble(5), resultSet.getString(6), resultSet.getString(7), resultSet.getDouble(8), resultSet.getInt(9), resultSet.getDouble(10), resultSet.getDouble(11), resultSet.getDouble(12)));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ignored) {
                }
            }

            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException ignored) {
                }
            }

            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException ignored) {
                }
            }
        }

        return stocksOwned;
    }

    public List<Stocks> getAllOwnedStocks(final String pName) {
        final ArrayList<Stocks> stocksList = new ArrayList<>();

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = hikari.getConnection();

            StockPlayer stockPlayer = PlayerHandling.getPlayer(pName);
            if (stockPlayer != null) {
                preparedStatement = connection.prepareStatement("SELECT stocks_id, player_id, stock_date, is_converted, inital_price_single, inital_currency, symbol, symbol_price, quantity, stock_value, broker_fees, total_price FROM sm_stocks WHERE player_id = ? ORDER BY symbol, stocks_id");
                preparedStatement.setInt(1, stockPlayer.getPlayerID());
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    stocksList.add(new Stocks(resultSet.getInt(1), resultSet.getInt(2), resultSet.getTimestamp(3), resultSet.getBoolean(4), resultSet.getDouble(5), resultSet.getString(6), resultSet.getString(7), resultSet.getDouble(8), resultSet.getInt(9), resultSet.getDouble(10), resultSet.getDouble(11), resultSet.getDouble(12)));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ignored) {
                }
            }

            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException ignored) {
                }
            }

            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException ignored) {
                }
            }
        }

        return stocksList;
    }

    public HashMap<String, MergedStock> getAllOwnedStocksCombined(final String pName) {
        final HashMap<String, MergedStock> stocksList = new HashMap<>();

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = hikari.getConnection();

            StockPlayer stockPlayer = PlayerHandling.getPlayer(pName);
            if (stockPlayer != null) {
                preparedStatement = connection.prepareStatement("SELECT stocks_id, player_id, stock_date, is_converted, inital_price_single, inital_currency, symbol, symbol_price, quantity, stock_value, broker_fees, total_price FROM sm_stocks WHERE player_id = ? ORDER BY symbol, stocks_id");
                preparedStatement.setInt(1, stockPlayer.getPlayerID());
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    if (stocksList.get(resultSet.getString(7)) != null) {
                        MergedStock stock = stocksList.get(resultSet.getString(7));
                        stocksList.remove(resultSet.getString(7));
                        stock.setQuantity(stock.getQuantity() + resultSet.getInt(9));

                        stocksList.put(stock.getSymbol(), stock);
                    } else {
                        stocksList.put(resultSet.getString(7), new MergedStock(resultSet.getString(7), resultSet.getInt(9)));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ignored) {
                }
            }

            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException ignored) {
                }
            }

            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException ignored) {
                }
            }
        }

        return stocksList;
    }

    public List<Stocks> getAllOwnedStocks(final UUID pUUID) {
        final ArrayList<Stocks> stocksList = new ArrayList<>();

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = hikari.getConnection();

            StockPlayer stockPlayer = PlayerHandling.lookupPlayerUUID(pUUID);
            if (stockPlayer != null) {
                preparedStatement = connection.prepareStatement("SELECT stocks_id, player_id, stock_date, is_converted, inital_price_single, inital_currency, symbol, symbol_price, quantity, stock_value, broker_fees, total_price FROM sm_stocks WHERE player_id = ? ORDER BY symbol, stocks_id");
                preparedStatement.setInt(1, stockPlayer.getPlayerID());
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    stocksList.add(new Stocks(resultSet.getInt(1), resultSet.getInt(2), resultSet.getTimestamp(3), resultSet.getBoolean(4), resultSet.getDouble(5), resultSet.getString(6), resultSet.getString(7), resultSet.getDouble(8), resultSet.getInt(9), resultSet.getDouble(10), resultSet.getDouble(11), resultSet.getDouble(12)));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ignored) {
                }
            }

            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException ignored) {
                }
            }

            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException ignored) {
                }
            }
        }

        return stocksList;
    }

    public List<StockPlayer> getAllStockPlayers() {
        final ArrayList<StockPlayer> stockPlayerList = new ArrayList<>();

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = hikari.getConnection();
                preparedStatement = connection.prepareStatement("SELECT * FROM sm_players ORDER BY player_id");
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    stockPlayerList.add(new StockPlayer(resultSet.getInt(1), UUID.fromString(resultSet.getString(2)), resultSet.getString(3)));
                }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ignored) {
                }
            }

            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException ignored) {
                }
            }

            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException ignored) {
                }
            }
        }

        return stockPlayerList;
    }

    public List<Transactions> getAllPlayerTransactions(final String pName) {
        final ArrayList<Transactions> transactionsList = new ArrayList<>();

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = hikari.getConnection();

            StockPlayer stockPlayer = PlayerHandling.getPlayer(pName);
            if (stockPlayer != null) {
                preparedStatement = connection.prepareStatement("SELECT tran_id, tran_type, tran_date, player_id, is_converted, inital_price_single, inital_currency, symbol, symbol_price, quantity, stock_value, broker_fees, total_price, earnings FROM sm_transactions WHERE player_id = ? ORDER BY symbol, tran_date");
                preparedStatement.setInt(1, stockPlayer.getPlayerID());
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    transactionsList.add(new Transactions(resultSet.getInt(1), resultSet.getInt(4), resultSet.getString(2).toUpperCase(), resultSet.getTimestamp(3), resultSet.getBoolean(5), resultSet.getDouble(6), resultSet.getString(7), resultSet.getString(8), resultSet.getDouble(9), resultSet.getInt(10), resultSet.getDouble(11), resultSet.getDouble(12), resultSet.getDouble(13), resultSet.getDouble(14)));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ignored) {
                }
            }

            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException ignored) {
                }
            }

            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException ignored) {
                }
            }
        }

        return transactionsList;
    }

    public List<Transactions> getAllStockTransactions (final String stockSymbol) {
        final ArrayList<Transactions> transactionsList = new ArrayList<>();

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = hikari.getConnection();

            preparedStatement = connection.prepareStatement("SELECT tran_id, tran_type, tran_date, player_id, is_converted, inital_price_single, inital_currency, symbol, symbol_price, quantity, stock_value, broker_fees, total_price, earnings FROM sm_transactions WHERE symbol = ? ORDER BY tran_date");
            preparedStatement.setString(1, stockSymbol.toUpperCase());
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                transactionsList.add(new Transactions(resultSet.getInt(1), resultSet.getInt(4), resultSet.getString(2).toUpperCase(), resultSet.getTimestamp(3), resultSet.getBoolean(5), resultSet.getDouble(6), resultSet.getString(7), resultSet.getString(8), resultSet.getDouble(9), resultSet.getInt(10), resultSet.getDouble(11), resultSet.getDouble(12), resultSet.getDouble(13), resultSet.getDouble(14)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ignored) {
                }
            }

            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException ignored) {
                }
            }

            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException ignored) {
                }
            }
        }

        return transactionsList;
    }

    public void updatePlayerOwnedStock(final Stocks stocks) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = StockMarket.getMySQL().hikari.getConnection();

            preparedStatement = connection.prepareStatement("UPDATE sm_stocks SET quantity = ?, stock_value = ?, total_price = ? WHERE stocks_id = ?");
            preparedStatement.setInt(1, stocks.getQuantity());
            preparedStatement.setDouble(2, stocks.getStockValue());
            preparedStatement.setDouble(3, stocks.getTotalPrice());
            preparedStatement.setInt(4, stocks.getID());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ignored) {
                }
            }

            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    public void deletePlayerOwnedStock(final Stocks stocks) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = StockMarket.getMySQL().hikari.getConnection();

            preparedStatement = connection.prepareStatement("DELETE FROM sm_stocks WHERE stocks_id = ?");
            preparedStatement.setInt(1, stocks.getID());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ignored) {
                }
            }

            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }
}
