package ca.maldahleh.stockmarket.inventories.leaderboards;

import ca.maldahleh.stockmarket.StockMarket;
import ca.maldahleh.stockmarket.utils.ItemStackBuilder;
import ca.maldahleh.stockmarket.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class ProfitLeaderboardInventory {
    private StockMarket stockMarket;
    private Inventory inventory;
    private Player target;

    public ProfitLeaderboardInventory(StockMarket stockMarket, Player target) {
        this.stockMarket = stockMarket;
        this.target = target;

        inventory = Bukkit.createInventory(null, 54, ChatColor.GOLD + "" + ChatColor.BOLD + "Stocks "
                + ChatColor.GRAY + "-" + ChatColor.GOLD + " Leaderboard");
        createInventory();
    }

    private void createInventory() {
        Bukkit.getScheduler().runTaskAsynchronously(StockMarket.getInstance(), () -> {
            List<StockPlayer> toUse = stockMarket.getMySQL().getAllStockPlayers();

            if (!toUse.isEmpty()) {
                final TreeMap<Double, UUID> transactionValue = new TreeMap<>(Collections.reverseOrder());
                for (StockPlayer aToUse : toUse) {
                    transactionValue.put(Double.valueOf(StockMarket.getStockMarketAPI()
                            .getProfitMargin(aToUse.getPlayerUUID())), aToUse.getPlayerUUID());
                }

                int toDisplay = 44;
                if (transactionValue.size() < 44) {
                    toDisplay = transactionValue.size() + 1;
                }

                final int finalToDisplay = toDisplay;
                Bukkit.getScheduler().runTask(StockMarket.getInstance(), () -> {
                    int tally = 0;
                    for (Map.Entry<Double, UUID> e : transactionValue.entrySet()) {
                        OfflinePlayer p = Bukkit.getOfflinePlayer(e.getValue());
                        ItemStack skull = new ItemStack(397, 1, (short) 3);
                        SkullMeta meta = (SkullMeta) skull.getItemMeta();
                        meta.setDisplayName(ChatColor.GOLD + "#" + (tally + 1) + ChatColor.GRAY + " " + p.getName());
                        meta.setOwner(p.getName());

                        List<String> lore = new ArrayList<>();
                        lore.add(ChatColor.GREEN + "" + Utils.formatDecimal(e.getKey().floatValue()) + " "
                                + ChatColor.GRAY + StockMarket.getEcon().currencyNamePlural());
                        meta.setLore(lore);
                        skull.setItemMeta(meta);
                        inventory.setItem(tally, skull);
                        tally++;

                        if (tally >= finalToDisplay) {
                            break;
                        }
                    }

                    int playerPosition = 1;
                    double playerProfitMargin = 0;
                    for (Map.Entry<Double, UUID> e : transactionValue.entrySet()) {
                        if (e.getValue().equals(target.getUniqueId())) {
                            playerProfitMargin = e.getKey();
                            break;
                        }

                        playerPosition++;
                    }

                    inventory.setItem(48, new ItemStackBuilder(Material.ENCHANTED_BOOK, 1)
                            .setDisplayName(ChatColor.GRAY + "" + ChatColor.BOLD + "Your Position")
                            .addLoreLine(ChatColor.GOLD + "" + playerPosition)
                            .buildItemStack());
                    inventory.setItem(50, new ItemStackBuilder(Material.ENCHANTED_BOOK, 1)
                            .setDisplayName(ChatColor.GRAY + "" + ChatColor.BOLD + "Your Profit Margin")
                            .addLoreLine(ChatColor.GOLD + "" + Utils.formatDecimal(playerProfitMargin))
                            .buildItemStack());

                    target.openInventory(inventory);
                });
            } else {
                target.sendMessage(stockMarket.getLocalConfig().getNoStockPlayers());
            }
        });
    }
}