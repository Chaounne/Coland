package me.chaounne.coland.combat;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedParticle;
import me.chaounne.coland.game.player.Classes;
import me.chaounne.coland.game.player.GamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CombatHandler implements Listener {

    private final ProtocolManager protocolManager;
    private final Plugin plugin;

    private final Set<UUID> processingDamage = new HashSet<>();

    public CombatHandler(Plugin plugin) {
        this.plugin = plugin;
        this.protocolManager = ProtocolLibrary.getProtocolManager();

        Bukkit.getPluginManager().registerEvents(this, plugin);
        registerPacketListeners();
    }

    private void registerPacketListeners() {
        protocolManager.addPacketListener(new PacketAdapter(
                plugin,
                ListenerPriority.HIGHEST,
                PacketType.Play.Server.WORLD_PARTICLES
        ) {
            @Override
            public void onPacketSending(PacketEvent event) {
                Player player = event.getPlayer();
                GamePlayer gp = GamePlayer.getInstance(player);

                if(gp.getPclass() != Classes.ASSASSIN) return;

                try {
                    WrappedParticle<?> particle = event.getPacket().getNewParticles().read(0);

                    if (particle.getParticle() == Particle.SWEEP_ATTACK) {
                        event.setCancelled(true);
                    }
                } catch (Exception e) {
                }
            }
        });
    }
    
    /**
     * Active le mode de combat 1.8 pour un joueur
     */
    public void enable18Combat(Player player) {
        try {
            player.getAttribute(Attribute.ATTACK_SPEED).setBaseValue(24.0);

            // Définir les noDamageTicks max à 10 (0.5 seconde comme en 1.8)
            player.setMaximumNoDamageTicks(10);

            // Réinitialiser le cooldown
            player.setCooldown(Material.AIR, 0);
        } catch (Exception e) {
            plugin.getLogger().warning("Erreur lors de l'activation du combat 1.8 pour " + player.getName());
            e.printStackTrace();
        }
    }

    /**
     * Désactive le mode de combat 1.8 pour un joueur
     */
    public void disable18Combat(Player player) {
        try {
            player.getAttribute(Attribute.ATTACK_SPEED).setBaseValue(4.0);

            // Remettre les noDamageTicks normaux (20 ticks = 1 seconde)
            player.setMaximumNoDamageTicks(20);

            // Réinitialiser le cooldown
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item != null && item.getType() != Material.AIR) {
                player.setCooldown(item.getType(), 0);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Erreur lors de la désactivation du combat 1.8 pour " + player.getName());
            e.printStackTrace();
        }
    }
}