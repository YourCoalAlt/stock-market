package ca.maldahleh.stockmarket.inventories.transactionhistory;

import ca.maldahleh.stockmarket.StockMarket;
import ca.maldahleh.stockmarket.stocks.Transactions;
import ca.maldahleh.stockmarket.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.text.SimpleDateFormat;
import java.util.*;

public class HistoryObject {
    private UUID playerUUID;
    private String playerName;
    private String targetPlayer;
    private int currentPage;
    private List<Inventory> pageMap = new ArrayList<>();

    public HistoryObject(UUID playerUUID, String playerName, String targetPlayer) {
        this.playerUUID = playerUUID;
        this.playerName = playerName;
        this.targetPlayer = targetPlayer;
        this.currentPage = 1;
        populatePageMap(targetPlayer);
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getTargetPlayer() {
        return targetPlayer;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public List<Inventory> getPageMap() {
        return pageMap;
    }

    public void setPlayerUUID(UUID newUUID) {
        playerUUID = newUUID;
    }

    public void setPlayerName(String newPlayerName) {
        playerName = newPlayerName;
    }

    public void setTargetPlayer(String newTargetPlayer) {
        targetPlayer = newTargetPlayer;
    }

    public void setCurrentPage(int newCurrentPage) {
        currentPage = newCurrentPage;
    }

    private void populatePageMap(final String targetName) {
        final ArrayList<List<Transactions>> toPopulate = new ArrayList<>();
        Player executor = null;

        if (Bukkit.getPlayer(playerUUID) != null) {
            executor = Bukkit.getPlayer(playerUUID);
        }

        final Player finalExecutor = executor;
        Bukkit.getScheduler().runTaskAsynchronously(StockMarket.getInstance(), new Runnable() {
            @Override
            public void run() {
                toPopulate.add(0, StockMarket.getMySQL().getAllPlayerTransactions(targetName));

                Bukkit.getScheduler().runTask(StockMarket.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        boolean toContinue = true;
                        if (toPopulate.get(0).isEmpty()) {
                            if (finalExecutor != null) {
                                finalExecutor.sendMessage(StockMarket.getInstance().getLocalConfig().noTransactionsTargetPlayer);
                                StockMarket.transactionMap.remove(finalExecutor.getUniqueId());
                            }

                            toContinue = false;
                        }

                        if (toContinue && (finalExecutor != null)) {
                            int totalPages = 0;
                            if (toPopulate.get(0).size() <= 45) {
                                totalPages = 1;
                            } else {
                                int toPopulateSize = toPopulate.get(0).size();
                                int pages = (toPopulateSize / 45) + 1;
                                totalPages += pages;
                            }

                            final int finalTotalPages = totalPages;
                            final List<Transactions> toUse = toPopulate.get(0);
                            Inventory i;
                            int z = 0;

                            for (int x = 1; x < (finalTotalPages + 1); x++) {
                                i = Bukkit.createInventory(null, 54, ChatColor.GOLD + "" + ChatColor.BOLD + "History " + ChatColor.GRAY + targetPlayer);

                                int thisLoop = 0;
                                boolean exit = false;
                                SimpleDateFormat dateFormat = new SimpleDateFormat();
                                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

                                do {
                                    final String timeStamp = dateFormat.format(toUse.get(z).getDate());
                                    final Transactions transaction = toUse.get(z);

                                    String earnings;
                                    if (transaction.getEarnings() != 0) {
                                        earnings = Utils.formatDecimal((float) transaction.getEarnings()) + " " + StockMarket.getInstance().getLocalConfig().serverCurrency;
                                    } else {
                                        earnings = "N/A";
                                    }

                                    Utils.createItem(Material.BOOK, i, thisLoop, ChatColor.GRAY + "" + ChatColor.BOLD + transaction.getSymbol().toUpperCase(), Arrays.asList(ChatColor.GRAY + "" + ChatColor.BOLD + "Transaction Date:", ChatColor.GOLD + timeStamp,
                                            ChatColor.GRAY + "" + ChatColor.BOLD + "Transaction Type:", ChatColor.GOLD + transaction.getTransactionType().toUpperCase(), ChatColor.GRAY + "" + ChatColor.BOLD + "Quantity:", ChatColor.GOLD + String.valueOf(toUse.get(z).getQuantity()),
                                            ChatColor.GRAY + "" + ChatColor.BOLD + "Transaction Value:", ChatColor.GOLD + Utils.formatDecimal((float) transaction.getStockValue()) + " " + StockMarket.getInstance().getLocalConfig().serverCurrency,
                                            ChatColor.GRAY + "" + ChatColor.BOLD + "Broker Fees:", ChatColor.GOLD + Utils.formatDecimal((float) transaction.getBrokerFees()) + " " + StockMarket.getInstance().getLocalConfig().serverCurrency, ChatColor.GRAY + "" + ChatColor.BOLD + "Transaction Total:",
                                            ChatColor.GOLD + Utils.formatDecimal((float) transaction.getTotalPrice()) + " " + StockMarket.getInstance().getLocalConfig().serverCurrency, ChatColor.GRAY + "" + ChatColor.BOLD + "Earnings:", ChatColor.GOLD + earnings));

                                    z++;
                                    thisLoop++;
                                    if ((z >= (45 * x)) || (z >= toUse.size())) {
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

                                Utils.createItem(Material.ENCHANTED_BOOK, i, 48, ChatColor.GRAY + "" + ChatColor.BOLD + "Transactions on Page", ChatColor.GOLD + "" + thisLoop);
                                Utils.createItem(Material.ENCHANTED_BOOK, i, 50, ChatColor.GRAY + "" + ChatColor.BOLD + "Total Transactions", ChatColor.GOLD + "" + toUse.size());

                                if (pageMap.size() == 0) {
                                    pageMap.add(0, i);
                                } else {
                                    pageMap.add(pageMap.size(), i);
                                }
                            }

                            finalExecutor.openInventory(pageMap.get(0));
                        }
                    }
                });
            }
        });
    }
}
