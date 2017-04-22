package ca.maldahleh.stockmarket.handling;

import ca.maldahleh.stockmarket.StockMarket;
import ca.maldahleh.stockmarket.stocks.StockPlayer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.UUID;

public class PlayerHandling {
    private StockMarket stockMarket;

    public PlayerHandling(StockMarket stockMarket) {
        this.stockMarket = stockMarket;
    }

    protected static void addPlayer(final Player p) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = StockMarket.getMySQL().hikari.getConnection();
            preparedStatement = connection.prepareStatement("INSERT INTO sm_players (player_uuid, player_name) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, p.getUniqueId().toString());
            preparedStatement.setString(2, p.getName());
            preparedStatement.executeUpdate();

            resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                StockMarket.cachedPlayers.put(p.getUniqueId(), new StockPlayer(resultSet.getInt(1), p.getUniqueId(), p.getName()));
            } else {
                throw new SQLException("Stock Market - Player addition failed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ignored) {}
            }

            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException ignored) {}
            }

            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException ignored) {}
            }
        }
    }

    public static StockPlayer getPlayer(Player p) {
        StockPlayer stockPlayer = lookupPlayerUUID(p.getUniqueId());
        if (stockPlayer != null) {
            return stockPlayer;
        }

        stockPlayer = lookupPlayerName(p.getName());
        if (stockPlayer != null) {
            return stockPlayer;
        }

        return null;
    }

    public static StockPlayer getPlayer(String pName) {
        Player p = Bukkit.getPlayer(pName);
        if (p != null) {
            return getPlayer(p);
        }

        StockPlayer stockPlayer = lookupPlayerName(pName);
        if (stockPlayer != null) {
            return stockPlayer;
        }

        return null;
    }

    public static StockPlayer lookupPlayerName(final String pName) {
        StockPlayer stockPlayer = null;

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = StockMarket.getMySQL().hikari.getConnection();
            preparedStatement = connection.prepareStatement("SELECT player_id, player_uuid, player_name FROM sm_players WHERE player_name = ?");
            preparedStatement.setString(1, pName);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                stockPlayer = new StockPlayer(resultSet.getInt(1), UUID.fromString(resultSet.getString(2)), resultSet.getString(3));
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

        return stockPlayer;
    }

    public static StockPlayer lookupPlayerUUID(final UUID pUUID) {
        StockPlayer stockPlayer = null;

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = StockMarket.getMySQL().hikari.getConnection();
            preparedStatement = connection.prepareStatement("SELECT player_id, player_uuid, player_name FROM sm_players WHERE player_uuid = ?");
            preparedStatement.setString(1, pUUID.toString());
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                stockPlayer = new StockPlayer(resultSet.getInt(1), UUID.fromString(resultSet.getString(2)), resultSet.getString(3));
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

        return stockPlayer;
    }

    public static void cacheProvidedPlayer(final Player p) {
        StockPlayer stockPlayer = getPlayer(p);

        if (stockPlayer != null) {
            stockPlayer = compareCache(p, stockPlayer);
            StockMarket.cachedPlayers.put(p.getUniqueId(), stockPlayer);
            return;
        }

        addPlayer(p);
    }

    protected static StockPlayer compareCache(Player p, StockPlayer stockPlayer) {
        if (!p.getName().equals(stockPlayer.getPlayerName())) {
            stockPlayer.setPlayerName(p.getName());
            updatePlayerFromCache(stockPlayer);
        }

        if (!p.getUniqueId().equals(stockPlayer.getPlayerUUID())) {
            stockPlayer.setPlayerUUID(p.getUniqueId());
            updatePlayerFromCache(stockPlayer);
        }

        return stockPlayer;
    }

    protected static void updatePlayerFromCache(final StockPlayer stockPlayer) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = StockMarket.getMySQL().hikari.getConnection();
            preparedStatement = connection.prepareStatement("UPDATE sm_players SET player_uuid = ?, player_name = ? WHERE player_id = ?");
            preparedStatement.setString(1, stockPlayer.getPlayerUUID().toString());
            preparedStatement.setString(2, stockPlayer.getPlayerName());
            preparedStatement.setInt(3, stockPlayer.getPlayerID());
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
