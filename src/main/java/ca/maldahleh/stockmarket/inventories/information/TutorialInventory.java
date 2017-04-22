package ca.maldahleh.stockmarket.inventories.information;

import ca.maldahleh.stockmarket.utils.ItemStackBuilder;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

public class TutorialInventory {
    private Inventory inventory;

    public TutorialInventory() {
        createInventory();
    }

    private void createInventory() {
        inventory = Bukkit.createInventory(null, 9,
                ChatColor.GOLD + "" + ChatColor.BOLD + "Stocks" + ChatColor.GRAY + " - " + ChatColor.GOLD + "Tutorial");

        inventory.setItem(0, new ItemStackBuilder(Material.BOOK, 1)
                .setDisplayName(ChatColor.GOLD + "What is the Stock Market and Stocks?")
                .addLoreLine(ChatColor.GRAY + "- A stock market is a place where")
                .addLoreLine(ChatColor.GRAY + "stocks are sold and bought.")
                .addLoreLine(ChatColor.GRAY + "- A stock comes in when a company splits")
                .addLoreLine(ChatColor.GRAY + "ownership into small pieces,")
                .addLoreLine(ChatColor.GRAY + "known as shares,")
                .addLoreLine(ChatColor.GRAY + "these shares, also known as stocks,")
                .addLoreLine(ChatColor.GRAY + "are sold to the public.")
                .addLoreLine(ChatColor.GRAY + "Stocks help a company generate")
                .addLoreLine(ChatColor.GRAY + "more money by splitting")
                .addLoreLine(ChatColor.GRAY + "ownership over sometimes millions")
                .addLoreLine(ChatColor.GRAY + "of shares which are sold for money")
                .addLoreLine(ChatColor.GRAY + "in return for small ownership in the company.")
                .buildItemStack());

        inventory.setItem(2, new ItemStackBuilder(Material.BOOK, 1)
                .setDisplayName(ChatColor.GOLD + "Why do people buy stocks?")
                .addLoreLine(ChatColor.GRAY + "- Shareholders, the people who buy stocks,")
                .addLoreLine(ChatColor.GRAY + "want to buy stocks in")
                .addLoreLine(ChatColor.GRAY + "hoping that the value of their stock")
                .addLoreLine(ChatColor.GRAY + "will go up and they will sell")
                .addLoreLine(ChatColor.GRAY + "their shares at a greater")
                .addLoreLine(ChatColor.GRAY + "price.")
                .addLoreLine(ChatColor.GRAY + "- Shareholders usually have an")
                .addLoreLine(ChatColor.GRAY + "interest in a company's")
                .addLoreLine(ChatColor.GRAY + "products or services and")
                .addLoreLine(ChatColor.GRAY + "it's room for growth.")
                .buildItemStack());

        inventory.setItem(4, new ItemStackBuilder(Material.BOOK, 1)
                .setDisplayName(ChatColor.GOLD + "What is an example company?")
                .addLoreLine(ChatColor.GRAY + "Are you into the PlayStation or Xbox?")
                .addLoreLine(ChatColor.GRAY + "Both products are manufactured by")
                .addLoreLine(ChatColor.GRAY + "companies that are listed on the")
                .addLoreLine(ChatColor.GRAY + "stock market, PlayStation is created by")
                .addLoreLine(ChatColor.GRAY + "Sony (Symbol: SNE),")
                .addLoreLine(ChatColor.GRAY + "while Xbox is created by")
                .addLoreLine(ChatColor.GRAY + "Microsoft (Symbol: MSFT).")
                .addLoreLine(ChatColor.GRAY + "Both are public companies owned by")
                .addLoreLine(ChatColor.GRAY + "shareholders, including many more which")
                .addLoreLine(ChatColor.GRAY + "you may know such as McDonalds,")
                .addLoreLine(ChatColor.GRAY + "and Coca-Cola.")
                .addLoreLine(ChatColor.GRAY + "You can look for thousands more")
                .addLoreLine(ChatColor.GRAY + "listed on the Stock Market")
                .addLoreLine(ChatColor.GRAY + "at: https://finance.yahoo.com")
                .buildItemStack());

        inventory.setItem(6, new ItemStackBuilder(Material.BOOK, 1)
                .setDisplayName(ChatColor.GOLD + "What is volume?")
                .addLoreLine(ChatColor.GRAY + "Companies usually split their")
                .addLoreLine(ChatColor.GRAY + "ownership into millions of shares,")
                .addLoreLine(ChatColor.GRAY + "the number of shares issued is the volume.")
                .buildItemStack());

        inventory.setItem(8, new ItemStackBuilder(Material.BOOK, 1)
                .setDisplayName(ChatColor.GOLD + "How can I learn how to use this plugin?")
                .addLoreLine(ChatColor.GRAY + "To learn how to use this plugin, use /stock help")
                .buildItemStack());
    }

    public Inventory getInventory() {
        return inventory;
    }
}