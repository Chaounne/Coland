package me.chaounne.coland.events;

import me.chaounne.coland.game.player.GamePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;

/**
 * Listener pour gérer le système d'XP personnalisé et bloquer l'XP vanilla
 */
public class XPListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onExpChange(PlayerExpChangeEvent event) {
        // Annuler complètement le gain d'XP vanilla
        event.setAmount(0);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLevelChange(PlayerLevelChangeEvent event) {
        Player player = event.getPlayer();
        GamePlayer gp = GamePlayer.getInstance(player);

        // Forcer le niveau à celui du système custom
        if (event.getNewLevel() != gp.getLevel()) {
            player.setLevel(gp.getLevel());
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event){
        if(!(event.getEntity().getKiller() instanceof Player)) return;
        Player killer = event.getEntity().getKiller();
        GamePlayer gp = GamePlayer.getInstance(killer);

        gp.addXP(10);
    }
}