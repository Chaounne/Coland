package me.chaounne.coland.game.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GamePlayer {

    private static final Map<UUID, GamePlayer> players = new HashMap<>();
    private final UUID playerUUID;
    private Classes pclass;
    private int levels;

    private GamePlayer(Player player){
        this.playerUUID = player.getUniqueId();
    }

    public static GamePlayer getInstance(Player player) {
        UUID playerUUID = player.getUniqueId();
        if (!players.containsKey(playerUUID))
            players.put(playerUUID, new GamePlayer(player));
        return players.get(playerUUID);
    }

    public Player getPlayer(){
        return Bukkit.getPlayer(this.playerUUID);
    }

    public Classes getPclass() {
        return pclass;
    }

    public void setPclass(Classes pclass) {
        this.pclass = pclass;
        ClassManager.giveClassItems(getPlayer(), pclass);
    }

    public void setLevels(int levels){
        this.levels = levels;
    }

    public int getLevels(){
        return levels;
    }

    public void addLevels(int levels){
        if(levels <= 0) return;
        this.levels += levels;
    }
}
