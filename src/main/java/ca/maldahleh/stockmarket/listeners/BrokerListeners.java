package ca.maldahleh.stockmarket.listeners;

import ca.maldahleh.stockmarket.StockMarket;
import ca.maldahleh.stockmarket.handling.StockHandling;
import ca.maldahleh.stockmarket.inventories.portfolio.PortfolioHandler;
import ca.maldahleh.stockmarket.inventories.stockhistory.StockHistoryHandler;
import ca.maldahleh.stockmarket.inventories.transactionhistory.HistoryHandler;
import ca.maldahleh.stockmarket.utils.SharedUtils;
import ca.maldahleh.stockmarket.utils.Utils;

import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class BrokerListeners implements Listener {
    private StockMarket stockMarket;
    private HashMap<UUID, String> handlingUUIDs = new HashMap<>();

    public BrokerListeners(StockMarket stockMarket) {
        this.stockMarket = stockMarket;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamage(NPCLeftClickEvent e) {
        if (stockMarket.activeKiller.contains(e.getClicker().getUniqueId())) {
            stockMarket.activeKiller.remove(e.getClicker().getUniqueId());
            NPC npc = e.getNPC();
            if (npc.isSpawned() && (npc.getName().equalsIgnoreCase(ChatColor.GOLD + "" + ChatColor.BOLD + "Stock Broker") || npc.getName().equalsIgnoreCase(ChatColor.GRAY + "" + ChatColor.BOLD + "Stock Broker"))) {
                npc.destroy();
                e.getClicker().sendMessage(stockMarket.getLocalConfig().stockBrokerRemoved);
            } else {
                e.getClicker().sendMessage(stockMarket.getLocalConfig().stockBrokerRemovalModeDisabled);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent e) {
        if (stockMarket.activeKiller.contains(e.getPlayer().getUniqueId())) {
            stockMarket.activeKiller.remove(e.getPlayer().getUniqueId());
            e.getPlayer().sendMessage(stockMarket.getLocalConfig().stockBrokerRemovalModeDisabled);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onClick(NPCLeftClickEvent e) {
        if (handlingUUIDs.containsKey(e.getClicker().getUniqueId())) {
            handlingUUIDs.remove(e.getClicker().getUniqueId());
        }

        SharedUtils.displayBrokerInventory(e.getClicker(), e.getNPC().isSpawned(), e.getNPC().getName());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onRightClick(NPCRightClickEvent e) {
        if (handlingUUIDs.containsKey(e.getClicker().getUniqueId())) {
            handlingUUIDs.remove(e.getClicker().getUniqueId());
        }

        SharedUtils.displayBrokerInventory(e.getClicker(), e.getNPC().isSpawned(), e.getNPC().getName());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClick(final InventoryClickEvent e) {
        if (e.getInventory().getName().equalsIgnoreCase(ChatColor.GOLD + "" + ChatColor.BOLD + "Stock Broker")) {
            if (e.getCurrentItem() != null) {
                if (e.getCurrentItem().hasItemMeta()) {
                    if (e.getCurrentItem().getType() == Material.BOOK) {
                        if (e.getCurrentItem().getItemMeta().hasDisplayName()) {
                            if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "" + ChatColor.BOLD + "Help")) {
                                e.getWhoClicked().closeInventory();
                                for (String toSend : stockMarket.getLocalConfig().translatedHelp) {
                                    e.getWhoClicked().sendMessage(toSend);
                                }
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "" + ChatColor.BOLD + "Tutorial")) {
                                e.getWhoClicked().closeInventory();
                                SharedUtils.displayTutorialInventory((Player) e.getWhoClicked(), true);
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "" + ChatColor.BOLD + "Stock Lookup")) {
                                e.getWhoClicked().closeInventory();
                                handlingUUIDs.put(e.getWhoClicked().getUniqueId(), "StockLookup");
                                e.getWhoClicked().sendMessage(stockMarket.getLocalConfig().stockMarketBrokerStockLookup);
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "" + ChatColor.BOLD + "Forex Lookup")) {
                                e.getWhoClicked().closeInventory();
                                handlingUUIDs.put(e.getWhoClicked().getUniqueId(), "ForexLookup");
                                e.getWhoClicked().sendMessage(stockMarket.getLocalConfig().stockMarketBrokerForexLookup);
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "" + ChatColor.BOLD + "List")) {
                                e.getWhoClicked().closeInventory();
                                SharedUtils.displayListInventory((Player) e.getWhoClicked(), true);
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "" + ChatColor.BOLD + "Buy a Stock")) {
                                e.getWhoClicked().closeInventory();
                                handlingUUIDs.put(e.getWhoClicked().getUniqueId(), "Buy");
                                e.getWhoClicked().sendMessage(stockMarket.getLocalConfig().stockBrokerBuyStock);
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "" + ChatColor.BOLD + "Sell a Stock")) {
                                e.getWhoClicked().closeInventory();
                                handlingUUIDs.put(e.getWhoClicked().getUniqueId(), "Sell");
                                e.getWhoClicked().sendMessage(stockMarket.getLocalConfig().stockBrokerSellStock);
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "" + ChatColor.BOLD + "Compare")) {
                                e.getWhoClicked().closeInventory();
                                handlingUUIDs.put(e.getWhoClicked().getUniqueId(), "Compare");
                                e.getWhoClicked().sendMessage(stockMarket.getLocalConfig().stockBrokerCompare);
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "" + ChatColor.BOLD + "Portfolio")) {
                                e.getWhoClicked().closeInventory();
                                handlingUUIDs.put(e.getWhoClicked().getUniqueId(), "Portfolio");
                                e.getWhoClicked().sendMessage(stockMarket.getLocalConfig().stockBrokerPortfolio);
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "" + ChatColor.BOLD + "Stock History")) {
                                e.getWhoClicked().closeInventory();
                                handlingUUIDs.put(e.getWhoClicked().getUniqueId(), "StockHistory");
                                e.getWhoClicked().sendMessage(stockMarket.getLocalConfig().stockBrokerStockHistory);
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "" + ChatColor.BOLD + "Portfolio Leaderboard")) {
                                e.getWhoClicked().closeInventory();
                                final Player commandSender = (Player) e.getWhoClicked();
                                if (commandSender.hasPermission("stockmarket.leaderboard")) {
                                    SharedUtils.displayPortfolioLeaderboard(commandSender);
                                } else {
                                    commandSender.sendMessage(stockMarket.getLocalConfig().noPermission);
                                }
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "" + ChatColor.BOLD + "Transaction History")) {
                                e.getWhoClicked().closeInventory();
                                handlingUUIDs.put(e.getWhoClicked().getUniqueId(), "TransactionHistory");
                                e.getWhoClicked().sendMessage(stockMarket.getLocalConfig().stockBrokerTransactionHistory);
                            } else if ((e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "" + ChatColor.BOLD + "Profit Leaderboard"))) {
                                e.getWhoClicked().closeInventory();
                                final Player commandSender = (Player) e.getWhoClicked();
                                if (commandSender.hasPermission("stockmarket.leaderboard")) {
                                    SharedUtils.displayProfitLeaderboard(commandSender);
                                } else {
                                    commandSender.sendMessage(stockMarket.getLocalConfig().noPermission);
                                }
                            }
                        }
                    }
                }
            }
            e.setCancelled(true);
            e.setResult(Event.Result.DENY);
        } else if (e.getInventory().getName().equalsIgnoreCase(ChatColor.GRAY + "" + ChatColor.BOLD + "Stock Broker")) {
            if (e.getCurrentItem() != null) {
                if (e.getCurrentItem().hasItemMeta()) {
                    if (e.getCurrentItem().getType() == Material.BOOK) {
                        if (e.getCurrentItem().getItemMeta().hasDisplayName()) {
                            final String stockSymbol = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getLore().get(1));
                            if (e.getClick() == ClickType.LEFT) {
                                StockHandling.buyStock((Player) e.getWhoClicked(), stockSymbol, 1);
                            } else if (e.getClick() == ClickType.RIGHT) {
                                StockHandling.sellStock((Player) e.getWhoClicked(), 1, stockSymbol);
                            } else if (e.getClick() == ClickType.SHIFT_LEFT) {
                                StockHandling.buyStock((Player) e.getWhoClicked(), stockSymbol, 5);
                            } else if (e.getClick() == ClickType.SHIFT_RIGHT) {
                                StockHandling.sellStock((Player) e.getWhoClicked(), 5, stockSymbol);
                            } else if (e.getClick() == ClickType.MIDDLE) {
                                e.getWhoClicked().closeInventory();
                                final Player commandSender = (Player) e.getWhoClicked();
                                SharedUtils.displayStockLookupInventory(commandSender, stockSymbol, true);
                            }
                        }
                    }
                }
            }

            e.setCancelled(true);
            e.setResult(Event.Result.DENY);
            if (e.getWhoClicked() instanceof Player && stockMarket.getLocalConfig().closeInventoryOnSimple) {
                Player p = (Player) e.getWhoClicked();
                p.closeInventory();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(final AsyncPlayerChatEvent e) {
        boolean handled = false;
        if (handlingUUIDs.containsKey(e.getPlayer().getUniqueId())) {
            final String[] split = e.getMessage().split(" ");
            if (split.length == 1 && (handlingUUIDs.get(e.getPlayer().getUniqueId()).equalsIgnoreCase("StockLookup") || handlingUUIDs.get(e.getPlayer().getUniqueId()).equalsIgnoreCase("ForexLookup"))
                    || handlingUUIDs.get(e.getPlayer().getUniqueId()).equalsIgnoreCase("Compare") || handlingUUIDs.get(e.getPlayer().getUniqueId()).equalsIgnoreCase("Portfolio")
                    || handlingUUIDs.get(e.getPlayer().getUniqueId()).equalsIgnoreCase("StockHistory") || handlingUUIDs.get(e.getPlayer().getUniqueId()).equalsIgnoreCase("TransactionHistory")) {
                if (handlingUUIDs.get(e.getPlayer().getUniqueId()).equalsIgnoreCase("StockLookup")) {
                    final Player commandSender = e.getPlayer();
                    if (commandSender.hasPermission("stockmarket.lookup")) {
                        final String toParse = split[0];
                        SharedUtils.displayStockLookupInventory(commandSender, toParse, true);
                    } else {
                        commandSender.sendMessage(stockMarket.getLocalConfig().noPermission);
                    }
                    handled = true;
                } else if (handlingUUIDs.get(e.getPlayer().getUniqueId()).equalsIgnoreCase("ForexLookup")) {
                    final Player commandSender = e.getPlayer();
                    SharedUtils.displayForexLookupInventory(commandSender, split[0], true);
                    handled = true;
                } else if (handlingUUIDs.get(e.getPlayer().getUniqueId()).equalsIgnoreCase("Compare")) {
                    final Player commandSender = e.getPlayer();
                    if (commandSender.hasPermission("stockmarket.compare")) {
                        final String[] splitCompare;
                        try {
                            splitCompare = Utils.splitArray(split[0]);
                        } catch (NumberFormatException ex) {
                            commandSender.sendMessage(stockMarket.getLocalConfig().improperCompareSyntax);
                            return;
                        }

                        if (splitCompare.length == 1) {
                            commandSender.sendMessage(stockMarket.getLocalConfig().compareMinimumTwo);
                        } else if (splitCompare.length == 2) {
                            SharedUtils.displayCompareInventory(commandSender, splitCompare[0], splitCompare[1], true);
                        } else if (splitCompare.length == 3) {
                            SharedUtils.displayCompareInventory(commandSender, splitCompare[0], splitCompare[1], splitCompare[2], true);
                        } else {
                            commandSender.sendMessage(stockMarket.getLocalConfig().compareMaximumThree);
                        }
                    } else {
                        commandSender.sendMessage(stockMarket.getLocalConfig().noPermission);
                    }
                    handled = true;
                } else if (handlingUUIDs.get(e.getPlayer().getUniqueId()).equalsIgnoreCase("Portfolio")) {
                    final Player commandSender = e.getPlayer();
                    boolean handledSecondary = false;
                    if (commandSender.hasPermission("stockmarket.portfolio") && split[0].equalsIgnoreCase(commandSender.getName())) {
                        Bukkit.getScheduler().runTaskLater(stockMarket,
                                () -> PortfolioHandler.openInventory(commandSender, commandSender.getPlayer().getName()), 5L);
                        handledSecondary = true;
                    }

                    if (commandSender.hasPermission("stockmarket.portfolio.other") && !handledSecondary) {
                        Bukkit.getScheduler().runTaskLater(stockMarket,
                                () -> PortfolioHandler.openInventory(commandSender, split[0]), 5L);
                        handledSecondary = true;
                    }

                    if (!handledSecondary) {
                        commandSender.sendMessage(stockMarket.getLocalConfig().noPermission);
                    }
                    handled = true;
                } else if (handlingUUIDs.get(e.getPlayer().getUniqueId()).equalsIgnoreCase("StockHistory")) {
                    final Player commandSender = e.getPlayer();
                    if (commandSender.hasPermission("stockmarket.stockhistory")) {
                        StockHistoryHandler.openInventory(commandSender, split[0].toUpperCase());
                    } else {
                        commandSender.sendMessage(stockMarket.getLocalConfig().noPermission);
                    }
                    handled = true;
                } else if (handlingUUIDs.get(e.getPlayer().getUniqueId()).equalsIgnoreCase("TransactionHistory")) {
                    final Player commandSender = e.getPlayer();
                    boolean handledSecondary = false;
                    if (commandSender.hasPermission("stockmarket.transactions") && split[0].equalsIgnoreCase(commandSender.getName())) {
                        HistoryHandler.openInventory(commandSender, commandSender.getPlayer().getName());
                        handledSecondary = true;
                    }

                    if (commandSender.hasPermission("stockmarket.transactions.other") && !handledSecondary) {
                        HistoryHandler.openInventory(commandSender, split[0]);
                        handledSecondary = true;
                    }

                    if (!handledSecondary) {
                        commandSender.sendMessage(stockMarket.getLocalConfig().noPermission);
                    }
                    handled = true;
                }
            } else if (split.length == 2 && (handlingUUIDs.get(e.getPlayer().getUniqueId()).equalsIgnoreCase("Buy") || handlingUUIDs.get(e.getPlayer().getUniqueId()).equalsIgnoreCase("Sell"))) {
                if (handlingUUIDs.get(e.getPlayer().getUniqueId()).equalsIgnoreCase("Buy")) {
                    Player commandSender = e.getPlayer();
                    boolean toContinue = true;
                    if (!Utils.isNumber(split[1])) {
                        commandSender.sendMessage(stockMarket.getLocalConfig().invalidQuantity);
                        toContinue = false;
                    }

                    if (toContinue) {
                        final int quantity = Integer.parseInt(split[1]);

                        if (quantity <= 0) {
                            commandSender.sendMessage(stockMarket.getLocalConfig().invalidQuantity);
                            toContinue = false;
                        }

                        if (toContinue) {
                            StockHandling.buyStock(commandSender, split[0], quantity);
                        }
                    }
                    handled = true;
                } else if (handlingUUIDs.get(e.getPlayer().getUniqueId()).equalsIgnoreCase("Sell")) {
                    Player commandSender = e.getPlayer();
                    boolean toContinue = true;
                    if (!Utils.isNumber(split[1])) {
                        commandSender.sendMessage(stockMarket.getLocalConfig().invalidQuantity);
                        toContinue = false;
                    }

                    if (toContinue) {
                        final int quantity = Integer.parseInt(split[1]);

                        if (quantity <= 0) {
                            commandSender.sendMessage(stockMarket.getLocalConfig().invalidQuantity);
                            toContinue = false;
                        }

                        if (toContinue) {
                            StockHandling.sellStock(commandSender, quantity, split[0]);
                        }
                    }
                    handled = true;
                }
            }

            if (!handled) {
                e.getPlayer().sendMessage(stockMarket.getLocalConfig().stockBrokerInvalidArguments);
            }

            handlingUUIDs.remove(e.getPlayer().getUniqueId());
            e.setCancelled(true);
            return;
        }

        Bukkit.getScheduler().runTask(StockMarket.getInstance(), () -> {
            Set<Player> recipients = e.getRecipients();
            recipients.removeIf(player -> handlingUUIDs.containsKey(player.getUniqueId()));
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent e) {
        if (stockMarket.activeKiller.contains(e.getPlayer().getUniqueId())) {
            stockMarket.activeKiller.remove(e.getPlayer().getUniqueId());
        }

        if (handlingUUIDs.containsKey(e.getPlayer().getUniqueId())) {
            handlingUUIDs.remove(e.getPlayer().getUniqueId());
        }
    }
}
