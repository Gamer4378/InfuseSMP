package me.itzoverwolf.infusesmp;


import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PlayerEffectManager {

    private Map<String, List<PotionEffect>> playerEffects;

    public PlayerEffectManager() {
        this.playerEffects = new HashMap<>();
    }

    public void initializePlayer(Player player) {
        // Initialize the player's effects list when they join
        playerEffects.put(player.getName(), new ArrayList<>());
    }

    public void handlePlayerKill(Player killer, Player victim) {
        // Check if the killer is not the victim
        if (!killer.equals(victim)) {
            // Apply random positive effect to the killer
            applyRandomPositiveEffect(killer);
        }

        // Save victim's potion effects on death and apply random negative effect
        savePlayerEffects(victim);
        applyRandomNegativeEffect(victim);
    }

    public void handlePlayerDeath(Player victim, Player killer) {
        // Check if the death was caused by another player
        if (killer != null && !killer.equals(victim)) {
            // Negative effects are now applied in the reapplyEffects method
            // Remove applyNegativeEffect(victim);
        }
    }

    public void handleEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamage() >= ((Player) event.getEntity()).getHealth()) {
            Player victim = (Player) event.getEntity();
            Player killer = getAttacker(event);

            if (killer != null && !killer.equals(victim)) {
                handlePlayerKill(killer, victim);
            }
        }
    }

    public void reapplyEffects(Player player) {
        // Reapply effects after respawn or under certain conditions
        List<PotionEffect> savedEffects = playerEffects.getOrDefault(player.getName(), new ArrayList<>());

        for (PotionEffect savedEffect : savedEffects) {
            player.addPotionEffect(savedEffect);
        }

        if (savedEffects.size() >= 8) {
            // Ban the player if they have accumulated 8 negative effects
            banPlayer(player);
        }
    }

    private Player getAttacker(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            return (Player) event.getDamager();
        }
        return null;
    }

    private void applyRandomPositiveEffect(Player player) {
        // Add a random positive effect
        PotionEffectType[] positiveEffects = {
                PotionEffectType.INCREASE_DAMAGE,
                PotionEffectType.SPEED,
                PotionEffectType.FAST_DIGGING,
                PotionEffectType.FIRE_RESISTANCE,
                PotionEffectType.HEALTH_BOOST,
                PotionEffectType.DOLPHINS_GRACE,
                PotionEffectType.LUCK,
                PotionEffectType.WATER_BREATHING
        };

        Random random = new Random();
        PotionEffectType selectedEffect = positiveEffects[random.nextInt(positiveEffects.length)];

        player.addPotionEffect(new PotionEffect(selectedEffect, Integer.MAX_VALUE, 1));
        player.sendMessage(ChatColor.GREEN + "You gained " + selectedEffect.getName() + "!");
    }

    private void applyRandomNegativeEffect(Player player) {
        // Add a random negative effect
        PotionEffectType[] negativeEffects = {
                PotionEffectType.WEAKNESS,
                PotionEffectType.SLOW,
                PotionEffectType.SLOW_DIGGING,
                PotionEffectType.JUMP,
                PotionEffectType.SLOW_FALLING,
                PotionEffectType.GLOWING,
                PotionEffectType.UNLUCK,
                PotionEffectType.HUNGER
        };

        Random random = new Random();
        PotionEffectType selectedEffect = negativeEffects[random.nextInt(negativeEffects.length)];

        player.addPotionEffect(new PotionEffect(selectedEffect, Integer.MAX_VALUE, 1));
        player.sendMessage(ChatColor.RED + "You gained " + selectedEffect.getName() + "!");
    }

    private void savePlayerEffects(Player player) {
        // Save the player's potion effects on death
        List<PotionEffect> effects = new ArrayList<>(player.getActivePotionEffects());
        playerEffects.put(player.getName(), effects);
    }

    private void banPlayer(Player player) {
        // Perform ban actions here
        // You might want to use your ban plugin or Bukkit's ban API

        // Example using Bukkit's ban API:
        String playerName = player.getName();
        String reason = "Accumulated 8 negative effects in InfuseSMP";

        Bukkit.getBanList(BanList.Type.NAME).addBan(playerName, reason, null, null);
        player.kickPlayer("You have been banned from the server. Reason: " + reason);
    }
}