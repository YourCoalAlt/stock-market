package ca.maldahleh.stockmarket.stocks;

import java.util.UUID;

public class StockPlayer {
    private int playerID;
    private UUID playerUUID;
    private String playerName;

    public StockPlayer (int playerID, UUID playerUUID, String playerName) {
        this.playerID = playerID;
        this.playerUUID = playerUUID;
        this.playerName = playerName;
    }

    public int getPlayerID () { return playerID; }

    public String getPlayerName () { return playerName; }

    public void setPlayerName (String playerNameNew) { playerName = playerNameNew; }

    public UUID getPlayerUUID () { return playerUUID; }

    public void setPlayerUUID (UUID uuid) { playerUUID = uuid; }
}