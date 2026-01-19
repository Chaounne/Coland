package me.chaounne.coland.commands;

import me.chaounne.coland.Coland;
import me.chaounne.coland.combat.CombatHandler;
import me.chaounne.coland.game.player.inv.classes.ClassInv;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You need to be a player to execute this command !");
            return false;
        }
        Player player = (Player) sender;

        switch (command.getName()){
            case "class" : {
                ClassInv classInv = new ClassInv();
                classInv.open(player);
                return true;
            }
        }
        return false;
    }
}
