package me.chaounne.coland.game.player;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GamePlayer {

    private static final Map<UUID, GamePlayer> players = new HashMap<>();
    private final UUID playerUUID;
    private Classes pclass;
    private int level;
    private int currentXP;

    private static final int MAX_LEVEL = 100;

    private static final int BASE_XP_PER_LEVEL = 150;

    private GamePlayer(Player player){
        this.playerUUID = player.getUniqueId();
        this.level = 1;
        this.currentXP = 0;
    }

    public static GamePlayer getInstance(Player player) {
        UUID playerUUID = player.getUniqueId();
        if (!players.containsKey(playerUUID))
            players.put(playerUUID, new GamePlayer(player));
        return players.get(playerUUID);
    }

    public Player getPlayer(){
        return Bukkit.getPlayer(this.playerUUID);
    }

    public Classes getPclass() {
        return pclass;
    }

    public void setPclass(Classes pclass) {
        this.pclass = pclass;
        ClassManager.giveClassItems(getPlayer(), pclass);
    }

    /**
     * Obtient le niveau actuel du joueur
     */
    public int getLevel() {
        return level;
    }

    /**
     * Obtient l'XP actuel du joueur
     */
    public int getCurrentXP() {
        return currentXP;
    }

    /**
     * Calcule l'XP requis pour passer au niveau suivant
     */
    public int getXPRequiredForNextLevel() {
        if (level >= MAX_LEVEL) return 0;
        return level * (level/2) * BASE_XP_PER_LEVEL;
    }

    /**
     * Définit le niveau du joueur directement
     */
    public void setLevel(int level) {
        if (level < 1) level = 1;
        if (level > MAX_LEVEL) level = MAX_LEVEL;
        this.level = level;
        this.currentXP = 0;
        updatePlayerDisplay();
    }

    /**
     * Ajoute de l'XP au joueur et gère la montée de niveau
     * @param xp Quantité d'XP à ajouter
     * @return true si le joueur a gagné au moins un niveau
     */
    public boolean addXP(int xp) {
        if (xp <= 0 || level >= MAX_LEVEL) return false;

        Player player = getPlayer();
        if (player == null) return false;

        currentXP += xp;
        boolean hasLeveledUp = false;

        // Vérifier si le joueur monte de niveau
        while (currentXP >= getXPRequiredForNextLevel() && level < MAX_LEVEL) {
            currentXP -= getXPRequiredForNextLevel();
            level++;
            hasLeveledUp = true;

            // Effets de montée de niveau
            onLevelUp(player);
        }

        // Si niveau max atteint, réinitialiser l'XP
        if (level >= MAX_LEVEL) {
            currentXP = 0;
        }

        updatePlayerDisplay();
        return hasLeveledUp;
    }

    /**
     * Retire de l'XP au joueur
     * @param xp Quantité d'XP à retirer
     */
    public void removeXP(int xp) {
        if (xp <= 0) return;

        currentXP -= xp;

        // Si l'XP devient négatif, descendre de niveau
        while (currentXP < 0 && level > 1) {
            level--;
            currentXP += getXPRequiredForNextLevel();
        }

        // Empêcher l'XP négatif au niveau 1
        if (level == 1 && currentXP < 0) {
            currentXP = 0;
        }

        updatePlayerDisplay();
    }

    /**
     * Appelé lorsque le joueur monte de niveau
     */
    private void onLevelUp(Player player) {
        // Message de niveau
        player.sendMessage("§6§l✦ NIVEAU SUPÉRIEUR ✦");
        player.sendMessage("§eVous êtes maintenant niveau §6§l" + level + "§e !");

        // Effets visuels et sonores
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.5f, 1.2f);

        // Titre
        player.sendTitle("§6§lNIVEAU " + level, "§eFélicitations !", 10, 40, 10);

        // Particules ou autres effets peuvent être ajoutés ici
    }

    /**
     * Met à jour l'affichage XP du joueur (utilise la barre d'XP de Minecraft)
     */
    public void updatePlayerDisplay() {
        Player player = getPlayer();
        if (player == null) return;

        // Désactiver le système de niveau Minecraft
        player.setLevel(level);

        // Utiliser la barre d'XP pour montrer la progression
        if (level >= MAX_LEVEL) {
            player.setExp(1.0f); // Barre complète au niveau max
        } else {
            float progress = (float) currentXP / (float) getXPRequiredForNextLevel();
            player.setExp(Math.min(progress, 1.0f));
        }
    }

    /**
     * Obtient le pourcentage de progression vers le niveau suivant
     */
    public double getProgressToNextLevel() {
        if (level >= MAX_LEVEL) return 100.0;
        return ((double) currentXP / (double) getXPRequiredForNextLevel()) * 100.0;
    }

    /**
     * Vérifie si le joueur est au niveau maximum
     */
    public boolean isMaxLevel() {
        return level >= MAX_LEVEL;
    }

    /**
     * Réinitialise complètement le niveau et l'XP
     */
    public void resetLevelAndXP() {
        this.level = 1;
        this.currentXP = 0;
        updatePlayerDisplay();
    }

    /**
     * Obtient une représentation textuelle du niveau et de l'XP
     */
    public String getLevelDisplay() {
        if (isMaxLevel()) {
            return "§6Niveau " + level + " §e(MAX)";
        }
        return "§6Niveau " + level + " §7(" + currentXP + "/" + getXPRequiredForNextLevel() + " XP)";
    }
}