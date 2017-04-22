package ca.maldahleh.stockmarket.commands;

import ca.maldahleh.stockmarket.StockMarket;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class StockAdminCommand implements CommandExecutor {
    private StockMarket stockMarket;

    public StockAdminCommand (StockMarket plugin) { stockMarket = plugin; }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender.hasPermission("stockmarket.admin")) {
            if (strings.length == 1 || strings.length == 2) {
                if (strings[0].equalsIgnoreCase("broker") && strings.length == 1) {
                    if (stockMarket.getLocalConfig().npcsEnabled) {
                        NPCRegistry npcRegistry = CitizensAPI.getNPCRegistry();
                        NPC npc = npcRegistry.createNPC(EntityType.VILLAGER, ChatColor.GOLD + "" + ChatColor.BOLD + "Stock Broker");
                        npc.setProtected(true);
                        npc.spawn(((Player) commandSender).getLocation());
                        commandSender.sendMessage(stockMarket.getLocalConfig().stockBrokerSpawned);
                        return true;
                    } else {
                        commandSender.sendMessage(stockMarket.getLocalConfig().stockBrokersDisabled);
                        return true;
                    }
                } else if (strings[0].equalsIgnoreCase("broker") && strings[1].equalsIgnoreCase("remove") && strings.length == 2) {
                    if (stockMarket.getLocalConfig().npcsEnabled) {
                        stockMarket.activeKiller.add(((Player) commandSender).getUniqueId());
                        commandSender.sendMessage(stockMarket.getLocalConfig().stockBrokerRemove);
                        return true;
                    } else {
                        commandSender.sendMessage(stockMarket.getLocalConfig().stockBrokersDisabled);
                        return true;
                    }
                } else if (strings[0].equalsIgnoreCase("broker") && strings[1].equalsIgnoreCase("simple") && strings.length == 2) {
                    if (stockMarket.getLocalConfig().npcsEnabled) {
                        NPCRegistry npcRegistry = CitizensAPI.getNPCRegistry();
                        NPC npc = npcRegistry.createNPC(EntityType.VILLAGER, ChatColor.GRAY + "" + ChatColor.BOLD + "Stock Broker");
                        npc.setProtected(true);
                        npc.spawn(((Player) commandSender).getLocation());
                        commandSender.sendMessage(stockMarket.getLocalConfig().stockBrokerSpawned);
                        return true;
                    } else {
                        commandSender.sendMessage(stockMarket.getLocalConfig().stockBrokersDisabled);
                        return true;
                    }
                }
            }

            if (strings.length == 1) {
                if (strings[0].equalsIgnoreCase("reload")) {
                    stockMarket.reloadConfig();
                    stockMarket.getLocalConfig().loadConfiguration();
                    commandSender.sendMessage(stockMarket.getLocalConfig().configReloaded);
                    return true;
                } else if (strings[0].equalsIgnoreCase("tables")) {
                    StockMarket.getMySQL().wipeTables();
                    commandSender.sendMessage(stockMarket.getLocalConfig().purgeTables);
                    return true;
                }
            }

            commandSender.sendMessage(stockMarket.getLocalConfig().invalidSyntax);
            return true;
        } else {
            commandSender.sendMessage(stockMarket.getLocalConfig().noPermission);
            return true;
        }
    }
}
