package me.chaounne.coland;

import me.chaounne.coland.combat.CombatHandler;
import me.chaounne.coland.commands.Commands;
import me.chaounne.coland.commands.XPCommand;
import me.chaounne.coland.db.MySQLManager;
import me.chaounne.coland.events.ClassHandler;
import me.chaounne.coland.events.XPListener;
import me.chaounne.coland.events.armor.ArmorListener;
import me.chaounne.coland.events.armor.DispenserArmorListener;
import me.chaounne.fastinv.FastInvManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class Coland extends JavaPlugin {
    private CombatHandler combatHandler;
    private static Coland instance;

    @Override
    public void onEnable() {
        // Sauvegarder la config par défaut si elle n'existe pas
        saveDefaultConfig();
        instance = this;
        try {
            combatHandler = new CombatHandler(this);
            System.out.println("CombatHandler (ProtocolLib) initialisé avec succès");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation de ProtocolLib : " + e.getMessage());
            e.printStackTrace();
        }

        getServer().getPluginManager().registerEvents(new ArmorListener(getConfig().getStringList("blocked")), this);
        getServer().getPluginManager().registerEvents(new ClassHandler(), this);
        getServer().getPluginManager().registerEvents(new XPListener(), this);
        try{
            //Better way to check for this? Only in 1.13.1+?
            Class.forName("org.bukkit.event.block.BlockDispenseArmorEvent");
            getServer().getPluginManager().registerEvents(new DispenserArmorListener(), this);
        }catch(Exception ignored){}

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

        FastInvManager.register(this);
        Commands cmd = new Commands();
        XPCommand xpCmd = new XPCommand();
        getCommand("class").setExecutor(cmd);
        getCommand("xp").setExecutor(xpCmd);
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