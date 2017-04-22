package ca.maldahleh.stockmarket.commands;

import ca.maldahleh.stockmarket.StockMarket;
import ca.maldahleh.stockmarket.handling.StockHandling;
import ca.maldahleh.stockmarket.inventories.portfolio.PortfolioHandler;
import ca.maldahleh.stockmarket.inventories.stockhistory.StockHistoryHandler;
import ca.maldahleh.stockmarket.inventories.transactionhistory.HistoryHandler;
import ca.maldahleh.stockmarket.utils.SharedUtils;
import ca.maldahleh.stockmarket.utils.Utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StockCommand implements CommandExecutor {
    private StockMarket stockMarket;

    public StockCommand(StockMarket plugin) {
        stockMarket = plugin;
    }

    @Override
    public boolean onCommand(final CommandSender commandSender, Command command, final String s, final String[] strings) {
        if (commandSender instanceof Player) {
            Player p = (Player) commandSender;

            if (!stockMarket.getLocalConfig().isCommandsDisabled()
                    || commandSender.hasPermission("stockmarket.nocommandbypass")) {
                if (commandSender.hasPermission("stockmarket.use")) {
                    if (strings.length == 0) {
                        for (String toSend : stockMarket.getLocalConfig().getTranslatedHelp()) {
                            commandSender.sendMessage(toSend);
                        }
                        return true;
                    }

                    if (strings.length == 1) {
                        if (strings[0].equalsIgnoreCase("tutorial")) {
                            stockMarket.getInventoryManager().openTutorialInventory(p, false);
                            return true;
                        }
                    }

                    if (strings.length == 1 || strings.length == 2) {
                        if (strings[0].equalsIgnoreCase("portfolio") && strings.length == 1) {
                            if (commandSender.hasPermission("stockmarket.portfolio")) {
                                PortfolioHandler.openInventory((Player) commandSender, ((Player) commandSender).getPlayer().getName());
                            } else {
                                commandSender.sendMessage(stockMarket.getLocalConfig().getNoPermission());
                            }
                            return true;
                        } else if (strings[0].equalsIgnoreCase("portfolio") && strings.length == 2) {
                            if (commandSender.hasPermission("stockmarket.portfolio.other")) {
                                PortfolioHandler.openInventory((Player) commandSender, strings[1]);
                            } else {
                                commandSender.sendMessage(stockMarket.getLocalConfig().getNoPermission());
                            }
                            return true;
                        }
                    }

                    if (strings.length == 1 || strings.length == 2) {
                        if (strings[0].equalsIgnoreCase("cportfolio") && strings.length == 1) {
                            if (commandSender.hasPermission("stockmarket.portfolio")) {
                                PortfolioHandler.openCombinedInventory((Player) commandSender, ((Player) commandSender).getPlayer().getName());
                            } else {
                                commandSender.sendMessage(stockMarket.getLocalConfig().getNoPermission());
                            }
                            return true;
                        } else if (strings[0].equalsIgnoreCase("cportfolio") && strings.length == 2) {
                            if (commandSender.hasPermission("stockmarket.portfolio.other")) {
                                PortfolioHandler.openCombinedInventory((Player) commandSender, strings[1]);
                            } else {
                                commandSender.sendMessage(stockMarket.getLocalConfig().getNoPermission());
                            }
                            return true;
                        }
                    }

                    if (strings.length == 1 || strings.length == 2) {
                        if (strings[0].equalsIgnoreCase("leaderboard") && strings.length == 1) {
                            if (commandSender.hasPermission("stockmarket.leaderboard")) {
                                stockMarket.getInventoryManager().openPortfolioLeaderboard(p);
                            } else {
                                commandSender.sendMessage(stockMarket.getLocalConfig().getNoPermission());
                            }
                            return true;
                        } else if (strings[0].equalsIgnoreCase("leaderboard") && strings.length == 2
                                && strings[1].equalsIgnoreCase("profit")) {
                            if (commandSender.hasPermission("stockmarket.leaderboard")) {
                                stockMarket.getInventoryManager().openProfitLeaderboard(p);
                            } else {
                                commandSender.sendMessage(stockMarket.getLocalConfig().getNoPermission());
                            }
                            return true;
                        }
                    }

                    if (strings.length == 1 || strings.length == 2) {
                        if (strings[0].equalsIgnoreCase("transactions") && strings.length == 1) {
                            if (commandSender.hasPermission("stockmarket.transactions")) {
                                HistoryHandler.openInventory((Player) commandSender, ((Player) commandSender).getPlayer().getName());
                            } else {
                                commandSender.sendMessage(stockMarket.getLocalConfig().getNoPermission());
                            }
                            return true;
                        } else if (strings[0].equalsIgnoreCase("transactions") && strings.length == 2) {
                            if (commandSender.hasPermission("stockmarket.transactions.other")) {
                                HistoryHandler.openInventory((Player) commandSender, strings[1]);
                            } else {
                                commandSender.sendMessage(stockMarket.getLocalConfig().getNoPermission());
                            }
                            return true;
                        }
                    }

                    if (strings.length == 2) {
                        if (strings[0].equalsIgnoreCase("shistory")) {
                            if (commandSender.hasPermission("stockmarket.stockhistory")) {
                                StockHistoryHandler.openInventory((Player) commandSender, strings[1].toUpperCase());
                            } else {
                                commandSender.sendMessage(stockMarket.getLocalConfig().getNoPermission());
                            }
                            return true;
                        }
                    }

                    if (strings.length >= 1) {
                        if (strings.length == 1) {
                            if (strings[0].equalsIgnoreCase("list")) {
                                stockMarket.getInventoryManager().openListInventory(p, false);
                                return true;
                            } else if (strings[0].equalsIgnoreCase("help")) {
                                for (String toSend : stockMarket.getLocalConfig().getTranslatedHelp()) {
                                    commandSender.sendMessage(toSend);
                                }
                                return true;
                            } else {
                                commandSender.sendMessage(stockMarket.getLocalConfig().getInvalidSyntax());
                                return true;
                            }
                        }

                        if (strings.length == 2) {
                            if (strings[0].equalsIgnoreCase("lookup")) {
                                if (commandSender.hasPermission("stockmarket.lookup")) {
                                    final String toParse = strings[1];
                                    SharedUtils.displayStockLookupInventory(p, toParse, false);
                                } else {
                                    commandSender.sendMessage(stockMarket.getLocalConfig().getNoPermission());
                                }
                            } else if (strings[0].equalsIgnoreCase("compare")) {
                                if (commandSender.hasPermission("stockmarket.compare")) {
                                    final String[] splitCompare;
                                    try {
                                        splitCompare = strings[1].split(",");
                                    } catch (NumberFormatException e) {
                                        commandSender.sendMessage(stockMarket.getLocalConfig().getImproperCompareSyntax());
                                        return true;
                                    }

                                    if (splitCompare.length == 1) {
                                        commandSender.sendMessage(stockMarket.getLocalConfig().getCompareMinimumTwo());
                                    } else if (splitCompare.length == 2) {
                                        SharedUtils.displayCompareInventory(p, splitCompare[0], splitCompare[1], false);
                                    } else if (splitCompare.length == 3) {
                                        SharedUtils.displayCompareInventory(p, splitCompare[0], splitCompare[1], splitCompare[2], false);
                                    } else {
                                        commandSender.sendMessage(stockMarket.getLocalConfig().getCompareMaximumThree());
                                    }
                                } else {
                                    commandSender.sendMessage(stockMarket.getLocalConfig().getNoPermission());
                                }
                            } else {
                                commandSender.sendMessage(stockMarket.getLocalConfig().getInvalidSyntax());
                            }
                        } else if (strings.length == 3) {
                            if (strings[0].equalsIgnoreCase("buy")) {
                                if (!Utils.isNumber(strings[2])) {
                                    commandSender.sendMessage(stockMarket.getLocalConfig().getInvalidQuantity());
                                    return true;
                                }

                                final int quantity = Integer.parseInt(strings[2]);

                                if (quantity <= 0) {
                                    commandSender.sendMessage(stockMarket.getLocalConfig().getInvalidQuantity());
                                    return true;
                                }

                                StockHandling.buyStock((Player) commandSender, strings[1], quantity);
                                return true;
                            } else if (strings[0].equalsIgnoreCase("sell")) {
                                if (!Utils.isNumber(strings[2])) {
                                    commandSender.sendMessage(stockMarket.getLocalConfig().getInvalidQuantity());
                                    return true;
                                }

                                final int quantity = Integer.parseInt(strings[2]);

                                if (quantity <= 0) {
                                    commandSender.sendMessage(stockMarket.getLocalConfig().getInvalidQuantity());
                                    return true;
                                }

                                StockHandling.sellStock((Player) commandSender, quantity, strings[1]);
                                return true;
                            } else {
                                commandSender.sendMessage(stockMarket.getLocalConfig().getInvalidSyntax());
                            }
                        } else {
                            commandSender.sendMessage(stockMarket.getLocalConfig().getInvalidSyntax());
                        }
                    } else {
                        commandSender.sendMessage(stockMarket.getLocalConfig().getInvalidSyntax());
                    }
                } else {
                    commandSender.sendMessage(stockMarket.getLocalConfig().getNoPermission());
                }
            } else {
                commandSender.sendMessage(stockMarket.getLocalConfig().getStockCommandDisabled());
            }
        } else {
            commandSender.sendMessage(stockMarket.getLocalConfig().getPlayerRequired());
        }

        return true;
    }
}