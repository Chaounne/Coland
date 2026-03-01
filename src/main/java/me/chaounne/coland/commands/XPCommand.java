package me.chaounne.coland.commands;

import me.chaounne.coland.game.player.GamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class XPCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Cette commande ne peut être exécutée que par un joueur !");
            return true;
        }

        Player player = (Player) sender;
        GamePlayer gp = GamePlayer.getInstance(player);

        if (args.length == 0) {
            // Afficher les informations XP du joueur
            player.sendMessage("§6§m-----------------§r §6§lXP INFO §r§6§m-----------------");
            player.sendMessage(gp.getLevelDisplay());
            player.sendMessage("§7Progression: §e" + String.format("%.1f", gp.getProgressToNextLevel()) + "%");
            if (!gp.isMaxLevel()) {
                player.sendMessage("§7XP pour le prochain niveau: §e" +
                        (gp.getXPRequiredForNextLevel() - gp.getCurrentXP()));
            }
            player.sendMessage("§6§m-----------------------------------------");
            return true;
        }

        // Commandes admin (nécessite permission)
        if (!player.hasPermission("coland.xp.admin")) {
            player.sendMessage(ChatColor.RED + "Vous n'avez pas la permission d'utiliser cette commande !");
            return true;
        }

        // /xp add <joueur> <quantité>
        if (args[0].equalsIgnoreCase("add") && args.length >= 3) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                player.sendMessage(ChatColor.RED + "Joueur introuvable !");
                return true;
            }

            try {
                int amount = Integer.parseInt(args[2]);
                GamePlayer targetGP = GamePlayer.getInstance(target);
                boolean leveledUp = targetGP.addXP(amount);

                player.sendMessage("§aVous avez ajouté §e" + amount + " XP §aà §6" + target.getName());
                target.sendMessage("§aVous avez reçu §e" + amount + " XP§a !");

                if (leveledUp) {
                    player.sendMessage("§6" + target.getName() + " §aest maintenant niveau §6" + targetGP.getLevel());
                }
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Quantité invalide !");
            }
            return true;
        }

        // /xp remove <joueur> <quantité>
        if (args[0].equalsIgnoreCase("remove") && args.length >= 3) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                player.sendMessage(ChatColor.RED + "Joueur introuvable !");
                return true;
            }

            try {
                int amount = Integer.parseInt(args[2]);
                GamePlayer targetGP = GamePlayer.getInstance(target);
                targetGP.removeXP(amount);

                player.sendMessage("§cVous avez retiré §e" + amount + " XP §cà §6" + target.getName());
                target.sendMessage("§cVous avez perdu §e" + amount + " XP§c !");
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Quantité invalide !");
            }
            return true;
        }

        // /xp set <joueur> <niveau>
        if (args[0].equalsIgnoreCase("set") && args.length >= 3) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                player.sendMessage(ChatColor.RED + "Joueur introuvable !");
                return true;
            }

            try {
                int level = Integer.parseInt(args[2]);
                GamePlayer targetGP = GamePlayer.getInstance(target);
                targetGP.setLevel(level);

                player.sendMessage("§aVous avez défini le niveau de §6" + target.getName() + " §aà §e" + level);
                target.sendMessage("§aVotre niveau a été défini à §6" + level);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Niveau invalide !");
            }
            return true;
        }

        // /xp reset <joueur>
        if (args[0].equalsIgnoreCase("reset") && args.length >= 2) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                player.sendMessage(ChatColor.RED + "Joueur introuvable !");
                return true;
            }

            GamePlayer targetGP = GamePlayer.getInstance(target);
            targetGP.resetLevelAndXP();

            player.sendMessage("§aVous avez réinitialisé le niveau de §6" + target.getName());
            target.sendMessage("§cVotre niveau et votre XP ont été réinitialisés !");
            return true;
        }

        // Message d'aide
        player.sendMessage("§6§lCommandes XP:");
        player.sendMessage("§e/xp §7- Voir vos informations XP");
        player.sendMessage("§e/xp add <joueur> <quantité> §7- Ajouter de l'XP");
        player.sendMessage("§e/xp remove <joueur> <quantité> §7- Retirer de l'XP");
        player.sendMessage("§e/xp set <joueur> <niveau> §7- Définir le niveau");
        player.sendMessage("§e/xp reset <joueur> §7- Réinitialiser le niveau");

        return true;
    }
}