package me.chaounne.coland.events;

import me.chaounne.coland.Coland;
import me.chaounne.coland.events.armor.ArmorEquipEvent;
import me.chaounne.coland.game.player.ClassManager;
import me.chaounne.coland.game.player.Classes;
import me.chaounne.coland.game.player.GamePlayer;
import me.chaounne.coland.game.player.inv.classes.ClassInv;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

public class ClassHandler implements Listener {

    private static final int SHIELD_COOLDOWN_DURATION = 80;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        GamePlayer gp = GamePlayer.getInstance(p);

        if(gp.getPclass() == null){
            Coland.getInstance().getCombatHandler().disable18Combat(p);
            p.teleport(p.getWorld().getSpawnLocation());
            // ouvrir inventaire classe
            ClassInv classInv = new ClassInv();
            classInv.open(p);
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
        if(!(event.getEntity() instanceof Player)) return;
        Entity attacker = event.getDamager();
        Player victim = (Player) event.getEntity();
        GamePlayer gpVictim = GamePlayer.getInstance(victim);

        // Si la victime est un Égide et qu'il bloque avec son bouclier
        if(gpVictim.getPclass() == Classes.EGIDE && victim.isBlocking()){
            // Vérifier si le bouclier est en cooldown
            if(victim.hasCooldown(Material.SHIELD)){
                return; // Le bouclier est désactivé, ne pas appliquer les effets
            }

            // Si le joueur est accroupi, activer la compétence de taunt
            if(victim.isSneaking()){
                activateTauntAbility(victim, attacker);
                event.setCancelled(false); // Laisser le coup passer pour déclencher le cooldown naturel
                return;
            }

            // Comportement normal : knockback de l'attaquant
            Vector direction = attacker.getLocation().toVector().subtract(victim.getLocation().toVector()).normalize();
            direction.setY(0.3);
            direction.multiply(1.5);
            attacker.setVelocity(direction);
        }
    }

    /**
     * Active la compétence de taunt de l'Égide
     * Attire toutes les entités hostiles dans un rayon et désactive le bouclier
     */
    private void activateTauntAbility(Player egide, Entity source) {
        // Désactiver le bouclier pendant 4 secondes
        disableShield(egide);

        // Rayon de l'effet de taunt
        double radius = 10.0;

        // Récupérer la team du joueur Égide
        Team egideTeam = egide.getScoreboard().getEntryTeam(egide.getName());

        // Parcourir toutes les entités proches
        for(Entity entity : egide.getNearbyEntities(radius, radius, radius)){
            if(!(entity instanceof LivingEntity)) continue;
            if(entity.equals(egide)) continue;

            // Vérifier si l'entité est de la même team
            if(entity instanceof Player){
                Player targetPlayer = (Player) entity;
                Team targetTeam = targetPlayer.getScoreboard().getEntryTeam(targetPlayer.getName());

                // Si même team ou pas de team, ignorer
                if(egideTeam != null && egideTeam.equals(targetTeam)) continue;
                if(egideTeam == null && targetTeam == null) continue;
            }

            // Attirer l'entité vers l'Égide
            LivingEntity livingEntity = (LivingEntity) entity;
            Vector direction = egide.getLocation().toVector().subtract(entity.getLocation().toVector()).normalize();
            direction.setY(direction.getY() + 0.5); // Composante verticale pour un effet plus spectaculaire
            // direction.multiply(1.5); // Force d'attraction

            livingEntity.setVelocity(direction);
        }

        // Effets visuels et sonores
        egide.getWorld().playSound(egide.getLocation(), Sound.ITEM_SHIELD_BLOCK, 2.0f, 0.5f);
        egide.sendMessage(ChatColor.GOLD + "✦ Compétence Taunt activée ! Bouclier désactivé pendant 4 secondes.");
    }

    /**
     * Désactive le bouclier d'un joueur pendant 4 secondes
     */
    private void disableShield(Player player) {
        if(!player.hasCooldown(Material.SHIELD)) {
            player.setCooldown(Material.SHIELD, SHIELD_COOLDOWN_DURATION);
            ItemStack playerShield = null;
            ItemMeta playerShieldMeta = null;

            // Retirer temporairement le bouclier de la main secondaire
            ItemStack offhand = player.getInventory().getItemInOffHand();
            ItemStack mainhand = player.getInventory().getItemInMainHand();

            boolean shieldInOffhand = offhand.getType() == Material.SHIELD;
            boolean shieldInMainhand = mainhand.getType() == Material.SHIELD;

            if(shieldInOffhand){
                player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
                playerShield = offhand;
                playerShieldMeta = playerShield.getItemMeta();
            }
            if(shieldInMainhand){
                player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                playerShield = mainhand;
                playerShieldMeta = playerShield.getItemMeta();
            }

            ItemStack finalPlayerShield = playerShield;
            ItemMeta finalPlayerShieldMeta = playerShieldMeta;
            finalPlayerShield.setItemMeta(finalPlayerShieldMeta);
            Bukkit.getScheduler().runTaskLater(Coland.getInstance(), () -> {
                if(finalPlayerShield != null && player.getInventory().getItemInOffHand().getType() == Material.AIR){
                    player.getInventory().setItemInOffHand(finalPlayerShield);
                } else if(finalPlayerShield != null && player.getInventory().getItemInMainHand().getType() == Material.AIR){
                    player.getInventory().setItemInMainHand(finalPlayerShield);
                } else {
                    player.getInventory().addItem(finalPlayerShield);
                }
                player.sendMessage(ChatColor.GREEN +  "Votre bouclier est de nouveau actif !");
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0f, 2.0f);
            }, 80L); // 4secondes
        }


    }

    @EventHandler
    public void onPlayerCraft(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player p = (Player) event.getWhoClicked();
        GamePlayer gp = GamePlayer.getInstance(p);
        ItemStack craftedItem = event.getCurrentItem();

        if(craftedItem == null) return;

        // Empêcher le craft de bouclier si pas Égide
        if(craftedItem.getType() == Material.SHIELD && gp.getPclass() != Classes.EGIDE){
            event.setCancelled(true);
        }

        // Empêcher le craft d'armure Netherite si pas Égide
        if(isNetheriteArmor(craftedItem.getType()) && gp.getPclass() != Classes.EGIDE){
            event.setCancelled(true);
            p.sendMessage(ChatColor.RED + "Seuls les Égides peuvent fabriquer des armures en Netherite !");
        }
    }

    @EventHandler
    public void onArmorEquip(ArmorEquipEvent event) {
        Player p = event.getPlayer();
        GamePlayer gp = GamePlayer.getInstance(p);
        ItemStack newArmor = event.getNewArmorPiece();

        // Vérifier si le joueur essaie d'équiper une armure Netherite
        if(newArmor != null && isNetheriteArmor(newArmor.getType())){
            // Si le joueur n'est pas un Égide, empêcher l'équipement
            if(gp.getPclass() != Classes.EGIDE){
                event.setCancelled(true);
                p.sendMessage(ChatColor.RED + "Seuls les Égides peuvent porter des armures en Netherite !");
            }
        }
    }

    /**
     * Vérifie si le matériau est une pièce d'armure en Netherite
     */
    private boolean isNetheriteArmor(Material material) {
        return material == Material.NETHERITE_HELMET ||
                material == Material.NETHERITE_CHESTPLATE ||
                material == Material.NETHERITE_LEGGINGS ||
                material == Material.NETHERITE_BOOTS;
    }
}