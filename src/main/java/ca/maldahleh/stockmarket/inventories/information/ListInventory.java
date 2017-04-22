package ca.maldahleh.stockmarket.inventories.information;

import ca.maldahleh.stockmarket.utils.ItemStackBuilder;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class ListInventory {
    private Inventory inventory;

    public ListInventory() {
        createInventory();
    }

    private void createInventory() {
        List<String> stockList = Arrays.asList("Amazon.com, INC.;AMZN", "Apple;AAPL", "AT&T;T", "Boeing;BA",
                "Cedar Fair Entertainment Company; FUN", "Chipotle Mexican Grill;CMG", "Coca-Cola;KO",
                "Costco Wholesale;COST", "Facebook;FB", "FedEx;FDX", "Ford;F", "General Motors;GM", "Google;GOOG",
                "Home Depot;HD", "Kellogg's;K", "Lowe's;LOW", "Mastercard;MA", "McDonald's;MCD", "Microsoft;MSFT",
                "Netflix;NFLX", "NVIDIA;NVDA", "Starbucks;SBUX", "United;UAL", "Verizon Communications;VZ",
                "Visa;V", "Time Warner Inc.;TWX", "Twitter;TWTR", "Wal-Mart Stores;WMT", "Walt Disney Company;DIS",
                "Whole Foods Market;WFM");

        inventory = Bukkit.createInventory(null, 36,
                ChatColor.GOLD + "" + ChatColor.BOLD + "Stocks" + ChatColor.GRAY + " - " + ChatColor.GOLD + "List");

        List<Integer> toLoad = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
                20, 21, 22, 23, 24, 25, 26, 30, 31, 32);
        int loopNumber = 0;

        for (int x : toLoad) {
            String[] splitString = stockList.get(loopNumber).split(";");
            inventory.setItem(x, new ItemStackBuilder(Material.BOOK, 1)
                    .setDisplayName(ChatColor.GRAY + "" + ChatColor.BOLD + splitString[0])
                    .addLoreLine(ChatColor.GOLD + splitString[1])
                    .buildItemStack());

            loopNumber++;
        }

        ItemStack moreStocks = new ItemStackBuilder(Material.ENCHANTED_BOOK, 1)
                .setDisplayName(ChatColor.GRAY + "" + ChatColor.BOLD + "Thousands more at:")
                .addLoreLine(ChatColor.GOLD + "https://finance.yahoo.com/")
                .buildItemStack();

        inventory.setItem(27, moreStocks);
        inventory.setItem(35, moreStocks);
    }

    public Inventory getInventory() {
        return inventory;
    }
}