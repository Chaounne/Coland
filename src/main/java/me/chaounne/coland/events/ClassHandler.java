package me.chaounne.coland.events;

import me.chaounne.coland.Coland;
import me.chaounne.coland.combat.CombatHandler;
import me.chaounne.coland.game.player.ClassManager;
import me.chaounne.coland.game.player.Classes;
import me.chaounne.coland.game.player.GamePlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class ClassHandler implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        GamePlayer gp = GamePlayer.getInstance(p);

        if(gp.getPclass() == null){
            p.teleport(p.getWorld().getSpawnLocation());
            // ouvrir inventaire classe
        } else {
            ClassManager.activateClassAttribute(p, gp.getPclass());
        }
    }

    @EventHandler
    public void onPlayerDamageEntity(EntityDamageEvent event){
        if(!(event.getEntity() instanceof Player)) return;
        Player p = (Player) event.getEntity();
        GamePlayer gp = GamePlayer.getInstance(p);

        if(gp.getPclass() == Classes.ASSASSIN){
            if(!(p.getInventory().getItemInMainHand().getType() == Material.WOODEN_SWORD || p.getInventory().getItemInMainHand().getType() == Material.STONE_SWORD || p.getInventory().getItemInMainHand().getType() == Material.IRON_SWORD || p.getInventory().getItemInMainHand().getType() == Material.GOLDEN_SWORD || p.getInventory().getItemInMainHand().getType() == Material.DIAMOND_SWORD || p.getInventory().getItemInMainHand().getType() == Material.NETHERITE_SWORD || p.getInventory().getItemInMainHand().getType() == Material.AIR)){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event){
        if(!(event.getDamager() instanceof Player)) return;
        if(!(event.getEntity() instanceof Player)) return;
        Player p = (Player) event.getDamager();
        Player p2 = (Player) event.getEntity();
        GamePlayer gp = GamePlayer.getInstance(p);
        GamePlayer gp2 = GamePlayer.getInstance(p2);

        if(gp2.getPclass() == Classes.EGIDE){
            
        }

    }

    @EventHandler
    public void onPlayerCraft(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player p = (Player) event.getWhoClicked();
        GamePlayer gp = GamePlayer.getInstance(p);
        ItemStack craftedItem = event.getCurrentItem();

        if(craftedItem.getType() == Material.SHIELD && !(gp.getPclass() != Classes.EGIDE)){
            event.setCancelled(true);
        }
    }
}
