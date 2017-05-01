package ca.maldahleh.stockmarket.listeners;

import ca.maldahleh.stockmarket.StockMarket;
import ca.maldahleh.stockmarket.utils.SharedUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListeners implements Listener {
    private StockMarket stockMarket;

    public PlayerListeners (StockMarket stockMarket) { this.stockMarket = stockMarket; }

    @EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClick (final InventoryClickEvent e) {
        if (e.getInventory() != null) {
            if (e.getInventory().getName().contains("Stocks") && !ChatColor.stripColor(e.getInventory().getName()).equalsIgnoreCase("Stocks - List")) {
                e.setCancelled(true);
                e.setResult(Event.Result.DENY);
                return;
            }

            if (ChatColor.stripColor(e.getInventory().getName()).equalsIgnoreCase("Stocks - List")) {
                if (e.getCurrentItem() != null) {
                    if (e.getCurrentItem().hasItemMeta()) {
                        if (e.getCurrentItem().getType() == Material.BOOK) {
                            final String toLookupSymbol = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getLore().get(0));
                            final Player commandSender = (Player) e.getWhoClicked();
                            e.getWhoClicked().closeInventory();
                            SharedUtils.displayStockLookupInventory(commandSender, toLookupSymbol, false);
                        }
                    }
                }
                e.setCancelled(true);
            }
        } else {
            e.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryDrag (final InventoryDragEvent e) {
        if (e.getInventory() != null) {
            if (e.getInventory().getName().contains("Stocks")) {
                e.setCancelled(true);
                e.setResult(Event.Result.DENY);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin (final PlayerJoinEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(stockMarket, new Runnable() {
            @Override
            public void run() {
                PlayerHandling.cacheProvidedPlayer(e.getPlayer());
            }
        });
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onQuit (PlayerQuitEvent e) {
        StockMarket.cachedPlayers.remove(e.getPlayer().getUniqueId());

        if (StockMarket.portfolioMap.containsKey(e.getPlayer().getUniqueId())) {
            StockMarket.portfolioMap.remove(e.getPlayer().getUniqueId());
        }

        if (StockMarket.transactionMap.containsKey(e.getPlayer().getUniqueId())) {
            StockMarket.transactionMap.remove(e.getPlayer().getUniqueId());
        }

        if (StockMarket.historyMap.containsKey(e.getPlayer().getUniqueId())) {
            StockMarket.historyMap.remove(e.getPlayer().getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClose (InventoryCloseEvent e) {
        if (e.getInventory().getName().contains("Portfolio") && StockMarket.portfolioMap.containsKey(e.getPlayer().getUniqueId())) {
            StockMarket.portfolioMap.remove(e.getPlayer().getUniqueId());
        }

        if (e.getInventory().getName().contains("History") && StockMarket.transactionMap.containsKey(e.getPlayer().getUniqueId())) {
            StockMarket.transactionMap.remove(e.getPlayer().getUniqueId());
        }

        if (e.getInventory().getName().contains("Stock History") && StockMarket.historyMap.containsKey(e.getPlayer().getUniqueId())) {
            StockMarket.historyMap.remove(e.getPlayer().getUniqueId());
        }
    }
}
