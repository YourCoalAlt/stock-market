package ca.maldahleh.stockmarket.inventories.portfolio;

import ca.maldahleh.stockmarket.StockMarket;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PortfolioHandler {
    private HashMap<UUID, PortfolioObject> portfolioMap;

    public PortfolioHandler(StockMarket stockMarket) {
        this.portfolioMap = new HashMap<>();

        stockMarket.getServer().getPluginManager()
                .registerEvents(new PortfolioListener(stockMarket, this), stockMarket);
    }

    public void openInventory (Player p, String targetPlayer) {
        portfolioMap.put(p.getUniqueId(),
                new PortfolioObject(p.getUniqueId(), p.getName(), targetPlayer, false));
    }

    public void openCombinedInventory (Player p, String targetPlayer) {
        portfolioMap.put(p.getUniqueId(),
                new PortfolioObject(p.getUniqueId(), p.getName(), targetPlayer, true));
    }

    public HashMap<UUID, PortfolioObject> getPortfolioMap() { return portfolioMap; }
}