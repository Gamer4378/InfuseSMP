package me.itzoverwolf.infusesmp;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class InfuseSMP extends JavaPlugin implements Listener {

    private PlayerEffectManager playerEffectManager;

    @Override
    public void onEnable() {
        playerEffectManager = new PlayerEffectManager();
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("InfuseSMP has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("InfuseSMP has been disabled!");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        playerEffectManager.initializePlayer(event.getPlayer());
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamage() >= ((Player) event.getEntity()).getHealth()) {
            Player victim = (Player) event.getEntity();
            Player killer = getAttacker(event);

            if (killer != null && !killer.equals(victim)) {
                playerEffectManager.handlePlayerKill(killer, victim);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = event.getEntity().getKiller();

        // Check if the death was caused by another player
        if (killer != null && !killer.equals(victim)) {
            playerEffectManager.handlePlayerDeath(victim, killer);
        }
    }

    private Player getAttacker(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            return (Player) event.getDamager();
        }
        return null;
    }
}