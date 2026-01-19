package me.chaounne.coland.game.player;

import me.chaounne.coland.Coland;
import me.chaounne.fastinv.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ClassManager {
    public static void giveClassItems(Player player, Classes classe) {
        switch (classe) {
            case INVOCATEUR:
                break;
            case ASSASSIN:
                player.getInventory().addItem(new ItemBuilder(Material.IRON_SWORD).build());
                break;
            case EGIDE:
                player.getInventory().addItem(new ItemBuilder(Material.SHIELD).build());
                break;
        }
        activateClassAttribute(player, classe);
    }

    public static void activateClassAttribute(Player player, Classes classe){
        switch (classe) {
            case INVOCATEUR:
                break;
            case ASSASSIN:
                Coland.getInstance().getCombatHandler().enable18Combat(player);
                break;
            case EGIDE:
                break;
        }
    }

}
