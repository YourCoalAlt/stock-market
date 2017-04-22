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

            if (!stockMarket.getLocalConfig().commandsDisabled || commandSender.hasPermission("stockmarket.nocommandbypass")) {
                if (commandSender.hasPermission("stockmarket.use")) {
                    if (strings.length == 0) {
                        for (String toSend : stockMarket.getLocalConfig().translatedHelp) {
                            commandSender.sendMessage(toSend);
                        }
                        return true;
                    }

                    if (strings.length == 1) {
                        if (strings[0].equalsIgnoreCase("tutorial")) {
                            SharedUtils.displayTutorialInventory(p, false);
                            return true;
                        }
                    }

                    if (strings.length == 1 || strings.length == 2) {
                        if (strings[0].equalsIgnoreCase("portfolio") && strings.length == 1) {
                            if (commandSender.hasPermission("stockmarket.portfolio")) {
                                PortfolioHandler.openInventory((Player) commandSender, ((Player) commandSender).getPlayer().getName());
                            } else {
                                commandSender.sendMessage(stockMarket.getLocalConfig().noPermission);
                            }
                            return true;
                        } else if (strings[0].equalsIgnoreCase("portfolio") && strings.length == 2) {
                            if (commandSender.hasPermission("stockmarket.portfolio.other")) {
                                PortfolioHandler.openInventory((Player) commandSender, strings[1]);
                            } else {
                                commandSender.sendMessage(stockMarket.getLocalConfig().noPermission);
                            }
                            return true;
                        }
                    }

                    if (strings.length == 1 || strings.length == 2) {
                        if (strings[0].equalsIgnoreCase("cportfolio") && strings.length == 1) {
                            if (commandSender.hasPermission("stockmarket.portfolio")) {
                                PortfolioHandler.openCombinedInventory((Player) commandSender, ((Player) commandSender).getPlayer().getName());
                            } else {
                                commandSender.sendMessage(stockMarket.getLocalConfig().noPermission);
                            }
                            return true;
                        } else if (strings[0].equalsIgnoreCase("cportfolio") && strings.length == 2) {
                            if (commandSender.hasPermission("stockmarket.portfolio.other")) {
                                PortfolioHandler.openCombinedInventory((Player) commandSender, strings[1]);
                            } else {
                                commandSender.sendMessage(stockMarket.getLocalConfig().noPermission);
                            }
                            return true;
                        }
                    }

                    if (strings.length == 1 || strings.length == 2) {
                        if (strings[0].equalsIgnoreCase("leaderboard") && strings.length == 1) {
                            if (commandSender.hasPermission("stockmarket.leaderboard")) {
                                SharedUtils.displayPortfolioLeaderboard(p);
                            } else {
                                commandSender.sendMessage(stockMarket.getLocalConfig().noPermission);
                            }
                            return true;
                        } else if (strings[0].equalsIgnoreCase("leaderboard") && strings.length == 2 && strings[1].equalsIgnoreCase("profit")) {
                            if (commandSender.hasPermission("stockmarket.leaderboard")) {
                                SharedUtils.displayProfitLeaderboard(p);
                            } else {
                                commandSender.sendMessage(stockMarket.getLocalConfig().noPermission);
                            }
                            return true;
                        }
                    }

                    if (strings.length == 1 || strings.length == 2) {
                        if (strings[0].equalsIgnoreCase("transactions") && strings.length == 1) {
                            if (commandSender.hasPermission("stockmarket.transactions")) {
                                HistoryHandler.openInventory((Player) commandSender, ((Player) commandSender).getPlayer().getName());
                            } else {
                                commandSender.sendMessage(stockMarket.getLocalConfig().noPermission);
                            }
                            return true;
                        } else if (strings[0].equalsIgnoreCase("transactions") && strings.length == 2) {
                            if (commandSender.hasPermission("stockmarket.transactions.other")) {
                                HistoryHandler.openInventory((Player) commandSender, strings[1]);
                            } else {
                                commandSender.sendMessage(stockMarket.getLocalConfig().noPermission);
                            }
                            return true;
                        }
                    }

                    if (strings.length == 2) {
                        if (strings[0].equalsIgnoreCase("shistory")) {
                            if (commandSender.hasPermission("stockmarket.stockhistory")) {
                                StockHistoryHandler.openInventory((Player) commandSender, strings[1].toUpperCase());
                            } else {
                                commandSender.sendMessage(stockMarket.getLocalConfig().noPermission);
                            }
                            return true;
                        }
                    }

                    if (strings.length >= 1) {
                        if (strings.length == 1) {
                            if (strings[0].equalsIgnoreCase("list")) {
                                SharedUtils.displayListInventory(p, false);
                                return true;
                            } else if (strings[0].equalsIgnoreCase("help")) {
                                for (String toSend : stockMarket.getLocalConfig().translatedHelp) {
                                    commandSender.sendMessage(toSend);
                                }
                                return true;
                            } else {
                                commandSender.sendMessage(stockMarket.getLocalConfig().invalidSyntax);
                                return true;
                            }
                        }

                        if (strings.length == 2) {
                            if (strings[0].equalsIgnoreCase("lookup")) {
                                if (commandSender.hasPermission("stockmarket.lookup")) {
                                    final String toParse = strings[1];
                                    SharedUtils.displayStockLookupInventory(p, toParse, false);
                                } else {
                                    commandSender.sendMessage(stockMarket.getLocalConfig().noPermission);
                                }
                            } else if (strings[0].equalsIgnoreCase("compare")) {
                                if (commandSender.hasPermission("stockmarket.compare")) {
                                    final String[] splitCompare;
                                    try {
                                        splitCompare = Utils.splitArray(strings[1]);
                                    } catch (NumberFormatException e) {
                                        commandSender.sendMessage(stockMarket.getLocalConfig().improperCompareSyntax);
                                        return true;
                                    }

                                    if (splitCompare.length == 1) {
                                        commandSender.sendMessage(stockMarket.getLocalConfig().compareMinimumTwo);
                                    } else if (splitCompare.length == 2) {
                                        SharedUtils.displayCompareInventory(p, splitCompare[0], splitCompare[1], false);
                                    } else if (splitCompare.length == 3) {
                                        SharedUtils.displayCompareInventory(p, splitCompare[0], splitCompare[1], splitCompare[2], false);
                                    } else {
                                        commandSender.sendMessage(stockMarket.getLocalConfig().compareMaximumThree);
                                    }
                                } else {
                                    commandSender.sendMessage(stockMarket.getLocalConfig().noPermission);
                                }
                            } else if (strings[0].equalsIgnoreCase("flookup")) {
                                if (commandSender.hasPermission("stockmarket.lookup.forex")) {
                                    SharedUtils.displayForexLookupInventory(p, strings[1], false);
                                } else {
                                    commandSender.sendMessage(stockMarket.getLocalConfig().noPermission);
                                }
                            } else {
                                commandSender.sendMessage(stockMarket.getLocalConfig().invalidSyntax);
                            }
                        } else if (strings.length == 3) {
                            if (strings[0].equalsIgnoreCase("buy")) {
                                if (!Utils.isNumber(strings[2])) {
                                    commandSender.sendMessage(stockMarket.getLocalConfig().invalidQuantity);
                                    return true;
                                }

                                final int quantity = Integer.parseInt(strings[2]);

                                if (quantity <= 0) {
                                    commandSender.sendMessage(stockMarket.getLocalConfig().invalidQuantity);
                                    return true;
                                }

                                StockHandling.buyStock((Player) commandSender, strings[1], quantity);
                                return true;
                            } else if (strings[0].equalsIgnoreCase("sell")) {
                                if (!Utils.isNumber(strings[2])) {
                                    commandSender.sendMessage(stockMarket.getLocalConfig().invalidQuantity);
                                    return true;
                                }

                                final int quantity = Integer.parseInt(strings[2]);

                                if (quantity <= 0) {
                                    commandSender.sendMessage(stockMarket.getLocalConfig().invalidQuantity);
                                    return true;
                                }

                                StockHandling.sellStock((Player) commandSender, quantity, strings[1]);
                                return true;
                            } else {
                                commandSender.sendMessage(stockMarket.getLocalConfig().invalidSyntax);
                            }
                        } else {
                            commandSender.sendMessage(stockMarket.getLocalConfig().invalidSyntax);
                        }
                    } else {
                        commandSender.sendMessage(stockMarket.getLocalConfig().invalidSyntax);
                    }
                } else {
                    commandSender.sendMessage(stockMarket.getLocalConfig().noPermission);
                }
            } else {
                commandSender.sendMessage(stockMarket.getLocalConfig().stockCommandDisabled);
            }
        } else {
            commandSender.sendMessage(stockMarket.getLocalConfig().playerRequired);
        }
        return true;
    }
}
