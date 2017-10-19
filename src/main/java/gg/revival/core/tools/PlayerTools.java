package gg.revival.core.tools;

import com.google.common.collect.Maps;
import gg.revival.core.Revival;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class PlayerTools {

    /**
     * Opens a GUI containing the players inventory contents, armor contents, health, food and potions
     * @param viewer The player viewing the GUI
     * @param viewed The player being viewd in the GUI
     */
    public void openInventory(Player viewer, Player viewed) {
        Inventory gui = Bukkit.createInventory(viewed, 54, ChatColor.BLACK + "Player Inventory");

        int cursor = 0;

        for(ItemStack contents : viewed.getInventory().getContents()) {
            if(contents == null) {
                cursor++;
                continue;
            }

            gui.setItem(cursor, contents);
            cursor++;
        }

        ItemStack health = new ItemStack(Material.MELON); ItemStack food = new ItemStack(Material.COOKED_BEEF); ItemStack potions = new ItemStack(Material.POTION);
        ItemMeta healthMeta = health.getItemMeta(), foodMeta = food.getItemMeta(), potionMeta = potions.getItemMeta();

        healthMeta.setDisplayName(ChatColor.RED + "Health");
        foodMeta.setDisplayName(ChatColor.AQUA + "Food");
        potionMeta.setDisplayName(ChatColor.DARK_PURPLE + "Potions");

        health.setAmount((int)viewed.getHealth());
        food.setAmount(viewed.getFoodLevel());
        potions.setAmount(viewed.getActivePotionEffects().size());

        List<String> potionLore = new ArrayList<>();

        for(PotionEffect potion : viewed.getActivePotionEffects())
            potionLore.add(ChatColor.RESET + "" + ChatColor.WHITE + StringUtils.capitalize(potion.getType().getName().toLowerCase().replace("_", " ") + ": " +
                    ChatColor.DARK_AQUA + potion.getDuration() / 20));

        potionMeta.setLore(potionLore);

        health.setItemMeta(healthMeta);
        food.setItemMeta(foodMeta);
        potions.setItemMeta(potionMeta);

        gui.setItem(53, potions);
        gui.setItem(52, food);
        gui.setItem(51, health);

        int armorCursor = 45;

        for(ItemStack armor : viewed.getInventory().getArmorContents()) {
            if(armor == null) {
                armorCursor++;
                continue;
            }

            gui.setItem(armorCursor, armor);
        }

        viewer.openInventory(gui);
    }

    /**
     * Returns a GameMode ENUM based on name
     * @param name The gamemode name
     * @return Gamemode ENUM
     */
    public GameMode getGamemodeByName(String name)
    {
        if(name.equalsIgnoreCase("survival") || name.equalsIgnoreCase("0"))
            return GameMode.SURVIVAL;

        if(name.equalsIgnoreCase("creative") || name.equalsIgnoreCase("1"))
            return GameMode.CREATIVE;

        if(name.equalsIgnoreCase("adventure") || name.equalsIgnoreCase("2"))
            return GameMode.ADVENTURE;

        if(name.equalsIgnoreCase("spectator") || name.equalsIgnoreCase("3"))
            return GameMode.SPECTATOR;

        return null;
    }

    /**
     * Send a message to players who have a given permission
     * @param message The message to be sent
     * @param permission The permission needed to see the message
     */
    public void sendPermissionMessage(String message, String permission) {
        for(Player players : Bukkit.getOnlinePlayers()) {
            if(!players.hasPermission(permission)) continue;

            players.sendMessage(message);
        }
    }

    /**
     * Returns a callback containing an OfflinePlayer lookup for UUID and Username
     * @param name Username to ping the Mojang API
     * @param callback Callback result containing UUID and Username
     */
    public void getOfflinePlayer(String name, OfflinePlayerCallback callback) {
        if(Bukkit.getPlayer(name) != null) {
            Player player = Bukkit.getPlayer(name);
            UUID uuid = player.getUniqueId();
            String username = player.getName();

            callback.onQueryDone(uuid, username);
            return;
        }

        new BukkitRunnable() {
            public void run() {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);

                if(offlinePlayer != null) {
                    UUID uuid = offlinePlayer.getUniqueId();
                    String username = offlinePlayer.getName();

                    new BukkitRunnable() {
                        public void run() {
                            callback.onQueryDone(uuid, username);
                        }
                    }.runTask(Revival.getCore());
                }

                else {
                    new BukkitRunnable() {
                        public void run() {
                            callback.onQueryDone(null, null);
                        }
                    }.runTask(Revival.getCore());
                }
            }
        }.runTaskAsynchronously(Revival.getCore());
    }

    public void getOfflinePlayer(UUID uuid, OfflinePlayerCallback callback) {
        if(Bukkit.getPlayer(uuid) != null) {
            Player player = Bukkit.getPlayer(uuid);
            String username = player.getName();

            callback.onQueryDone(player.getUniqueId(), username);
            return;
        }

        new BukkitRunnable() {
            public void run() {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

                if(offlinePlayer != null) {
                    UUID uuid = offlinePlayer.getUniqueId();
                    String username = offlinePlayer.getName();

                    new BukkitRunnable() {
                        public void run() {
                            callback.onQueryDone(uuid, username);
                        }
                    }.runTask(Revival.getCore());
                }

                else {
                    new BukkitRunnable() {
                        public void run() {
                            callback.onQueryDone(null, null);
                        }
                    }.runTask(Revival.getCore());
                }
            }
        }.runTaskAsynchronously(Revival.getCore());
    }

    public void getManyOfflinePlayersByUUID(Set<UUID> uuids, ManyOfflinePlayerCallback callback) {
        Map<UUID, String> result = Maps.newHashMap();

        for(UUID uuid : uuids) {
            if(Bukkit.getPlayer(uuid) != null) {
                Player player = Bukkit.getPlayer(uuid);
                String username = player.getName();

                result.put(uuid, username);
            }

            if(result.size() == uuids.size()) {
                callback.onQueryDone(result);
                return;
            }

            new BukkitRunnable() {
                public void run() {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                    String username = offlinePlayer.getName();

                    result.put(uuid, username);

                    if(result.size() == uuids.size()) {
                        new BukkitRunnable() {
                            public void run() {
                                callback.onQueryDone(result);
                                return;
                            }
                        }.runTask(Revival.getCore());
                    }
                }
            }.runTaskAsynchronously(Revival.getCore());
        }
    }

}
