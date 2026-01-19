package me.chaounne.coland.game.player.inv.classes;

import me.chaounne.coland.Coland;
import me.chaounne.coland.combat.CombatHandler;
import me.chaounne.coland.game.player.Classes;
import me.chaounne.coland.game.player.GamePlayer;
import me.chaounne.fastinv.FastInv;
import me.chaounne.fastinv.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ClassInv extends FastInv {
    public ClassInv() {
        super(9,"Choisissez votre classe");

        setItem(0, new ItemBuilder(Material.IRON_SWORD).name("Assassin").lore("TBA").build(), e -> {
            e.setCancelled(true);
            Player player = (Player) e.getWhoClicked();
            chooseClass(player, Classes.ASSASSIN);
        });
        setItem(1, new ItemBuilder(Material.STICK).name("Invocateur").lore("TBA").build(), e -> {
            e.setCancelled(true);
            Player player = (Player) e.getWhoClicked();
            chooseClass(player, Classes.INVOCATEUR);
        });
        setItem(2, new ItemBuilder(Material.SHIELD).name("Egide").lore("TBA").build(), e -> {
            e.setCancelled(true);
            Player player = (Player) e.getWhoClicked();
            chooseClass(player, Classes.EGIDE);
        });

    }

    private void chooseClass(Player player, Classes classe){
        GamePlayer gp = GamePlayer.getInstance(player);
        gp.setPclass(classe);
        player.closeInventory();
    }
}
