package ca.maldahleh.stockmarket.commands;

import ca.maldahleh.stockmarket.StockMarket;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class StockAdminCommand implements CommandExecutor {
    private StockMarket stockMarket;

    public StockAdminCommand(StockMarket plugin) {
        stockMarket = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender.hasPermission("stockmarket.admin")) {
            if ((strings.length == 1 || strings.length == 2) && commandSender instanceof Player) {
                Player sender = (Player) commandSender;

                if (stockMarket.getLocalConfig().isNpcsEnabled()) {
                    if (strings[0].equalsIgnoreCase("broker") && strings.length == 1) {
                        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.VILLAGER,
                                ChatColor.GOLD + "" + ChatColor.BOLD + "Stock Broker");
                        npc.setProtected(true);
                        npc.spawn(sender.getLocation());
                        commandSender.sendMessage(stockMarket.getLocalConfig().getStockBrokerSpawned());
                        return true;
                    } else if (strings[0].equalsIgnoreCase("broker")
                            && strings[1].equalsIgnoreCase("remove") && strings.length == 2) {
                        stockMarket.activeKiller.add(sender.getUniqueId());
                        commandSender.sendMessage(stockMarket.getLocalConfig().getStockBrokerRemove());
                        return true;
                    } else if (strings[0].equalsIgnoreCase("broker")
                            && strings[1].equalsIgnoreCase("simple") && strings.length == 2) {
                        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.VILLAGER,
                                ChatColor.GRAY + "" + ChatColor.BOLD + "Stock Broker");
                        npc.setProtected(true);
                        npc.spawn(sender.getLocation());
                        commandSender.sendMessage(stockMarket.getLocalConfig().getStockBrokerSpawned());
                        return true;
                    }
                } else {
                    commandSender.sendMessage(stockMarket.getLocalConfig().getStockBrokersDisabled());
                    return true;
                }
            }

            if (strings.length == 1) {
                if (strings[0].equalsIgnoreCase("reload")) {
                    stockMarket.reloadConfig();
                    stockMarket.getLocalConfig().loadConfiguration();
                    commandSender.sendMessage(stockMarket.getLocalConfig().getConfigReloaded());
                    return true;
                } else if (strings[0].equalsIgnoreCase("tables")) {
                    stockMarket.getMySQL().wipeTables();
                    commandSender.sendMessage(stockMarket.getLocalConfig().getPurgeTables());
                    return true;
                }
            }

            commandSender.sendMessage(stockMarket.getLocalConfig().getInvalidSyntax());
            return true;
        } else {
            commandSender.sendMessage(stockMarket.getLocalConfig().getNoPermission());
            return true;
        }
    }
}