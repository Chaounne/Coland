package me.chaounne.coland;

import me.chaounne.coland.commands.Commands;
import org.bukkit.plugin.java.JavaPlugin;

public final class Coland extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Commands cmd = new Commands();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
