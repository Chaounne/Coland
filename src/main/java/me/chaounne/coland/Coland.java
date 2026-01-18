package me.chaounne.coland;

import me.chaounne.coland.combat.CombatHandler;
import me.chaounne.coland.commands.Commands;
import me.chaounne.coland.db.MySQLManager;
import me.chaounne.coland.events.ClassHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class Coland extends JavaPlugin {
    private CombatHandler combatHandler;
    private static Coland instance;

    @Override
    public void onEnable() {
        instance = this;
        try {
            combatHandler = new CombatHandler(this);
            System.out.println("CombatHandler (ProtocolLib) initialisé avec succès");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation de ProtocolLib : " + e.getMessage());
            e.printStackTrace();
        }

        // Sauvegarder la config par défaut si elle n'existe pas
        saveDefaultConfig();

        // Plugin startup logic
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try {
                getLogger().info("Connexion à la base de données...");
                MySQLManager.connect(getConfig());
                getLogger().info("Connecté à la base de données avec succès !");
            } catch (SQLException e) {
                getLogger().severe("Erreur lors de la connexion à la base de données :");
                e.printStackTrace();
            }
        });

        Commands cmd = new Commands();
        getServer().getPluginManager().registerEvents(new ClassHandler(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        try {
            if (MySQLManager.isConnected()) {
                MySQLManager.disconnect();
                getLogger().info("Déconnecté de la base de données.");
            }
        } catch (SQLException e) {
            getLogger().warning("Erreur lors de la déconnexion de la base de données :");
            e.printStackTrace();
        }
    }

    public CombatHandler getCombatHandler() {
        return combatHandler;
    }

    public static Coland getInstance() {
        return instance;
    }

}