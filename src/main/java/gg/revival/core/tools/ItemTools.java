package gg.revival.core.tools;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemTools {

    /**
     * Returns an ItemStack by name
     * @param name Item name
     * @return The ItemStack result
     */
    public ItemStack getItemByName(String name) {
        for(Material mats : Material.values()) {
            String cleanedUpName = mats.name().toLowerCase().replace("_", "");

            if(name.equalsIgnoreCase(cleanedUpName))
                return new ItemStack(mats);
        }

        return null;
    }

    /**
     * Returns a named version of the given item
     * @param name Name to be applied
     * @param item
     * @return
     */
    public ItemStack applyName(String name, ItemStack item) {
        if(name == null || item == null) return null;

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);

        return item;
    }

    /**
     * Returns an enchanted version of the given item
     * @param enchantment Enchantment to be applied
     * @param level Enchantment level
     * @param item Item to be enchanted
     * @return The enchanted item
     */
    public ItemStack applyEnchantment(Enchantment enchantment, int level, ItemStack item) {
        if(enchantment == null || level == 0 || item == null) return null;

        item.addUnsafeEnchantment(enchantment, level);

        return item;
    }

    /**
     * Returns an Enchantment enum based on what the enchantments are actually called
     * @param enchant Enchantment name
     * @return Matching enchantment enum
     */
    public Enchantment getEnchantFromString(String enchant) {
        String ls = enchant.toLowerCase();

        if(ls.startsWith("aqua"))
            return Enchantment.WATER_WORKER;
        else if(ls.startsWith("bane"))
            return Enchantment.DAMAGE_ARTHROPODS;
        else if(ls.startsWith("blast"))
            return Enchantment.PROTECTION_EXPLOSIONS;
        else if(ls.startsWith("eff"))
            return Enchantment.DIG_SPEED;
        else if(ls.startsWith("feather"))
            return Enchantment.PROTECTION_FALL;
        else if(ls.startsWith("firea"))
            return Enchantment.FIRE_ASPECT;
        else if(ls.startsWith("firep"))
            return Enchantment.PROTECTION_FIRE;
        else if(ls.startsWith("flame"))
            return Enchantment.ARROW_FIRE;
        else if(ls.startsWith("fortune"))
            return Enchantment.LOOT_BONUS_BLOCKS;
        else if(ls.startsWith("inf"))
            return Enchantment.ARROW_INFINITE;
        else if(ls.startsWith("knock"))
            return Enchantment.KNOCKBACK;
        else if(ls.startsWith("loot"))
            return Enchantment.LOOT_BONUS_MOBS;
        else if(ls.startsWith("luck"))
            return Enchantment.LUCK;
        else if(ls.startsWith("lure"))
            return Enchantment.LURE;
        else if(ls.startsWith("power"))
            return Enchantment.ARROW_DAMAGE;
        else if(ls.startsWith("proj"))
            return Enchantment.PROTECTION_PROJECTILE;
        else if(ls.startsWith("prot"))
            return Enchantment.PROTECTION_ENVIRONMENTAL;
        else if(ls.startsWith("punch"))
            return Enchantment.ARROW_KNOCKBACK;
        else if(ls.startsWith("sharp"))
            return Enchantment.DAMAGE_ALL;
        else if(ls.startsWith("resp"))
            return Enchantment.OXYGEN;
        else if(ls.startsWith("silk"))
            return Enchantment.SILK_TOUCH;
        else if(ls.startsWith("smite"))
            return Enchantment.DAMAGE_UNDEAD;
        else if(ls.startsWith("thorns"))
            return Enchantment.THORNS;
        else if(ls.startsWith("unb"))
            return Enchantment.DURABILITY;
        else if(ls.startsWith("dep"))
            return Enchantment.DEPTH_STRIDER;

        return null;
    }

}
