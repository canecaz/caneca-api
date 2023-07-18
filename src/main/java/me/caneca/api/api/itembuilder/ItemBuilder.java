package me.caneca.api.api.itembuilder;

import me.caneca.api.api.API;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class ItemBuilder {

    private ItemStack item = new ItemStack(Material.GLASS);
    private ItemMeta meta = this.item.getItemMeta();

    public ItemBuilder(ItemStack item) {
        this.item = item;
        this.meta = item.getItemMeta();
    }


    public ItemBuilder setType(Material material) {
        this.item.setType(material);

        return this;
    }

    public ItemBuilder setAmount(int amount) {
        this.item.setAmount(amount);

        return this;
    }

    public ItemBuilder setName(String name) {
        this.meta.setDisplayName(API.colored(name));

        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        return setLore(lore, true);
    }

    public ItemBuilder setLore(List<String> lore, boolean colored) {
        this.meta.setLore(colored ? API.coloredLore(lore) : lore);
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        return setLore(true, lore);
    }

    public ItemBuilder setLore(boolean colored, String... lore) {
        this.meta.setLore(colored ? API.coloredLore(lore) : Arrays.asList(lore));
        return this;
    }

    public ItemBuilder addEnchant(Enchantment enc, int level) {
        this.meta.addEnchant(enc, level, true);

        return this;
    }

    public ItemBuilder setData(byte b) {
        this.item.setDurability(b);
        return this;
    }

    public ItemBuilder setDurability(short s) {
        this.item.setDurability(s);
        return this;
    }

    public ItemBuilder addGlow() {
        this.meta.addEnchant(Enchantment.DURABILITY, 1, true);
        this.meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    public ItemBuilder setUnbreakable() {
        this.meta.spigot().setUnbreakable(true);
        return this;
    }

    public ItemBuilder addItemFlags(ItemFlag... itemFlag) {
        this.meta.addItemFlags(itemFlag);
        return this;
    }

    public ItemBuilder removeEnchants(){
        if (!meta.getEnchants().isEmpty()) meta.getEnchants().keySet().forEach(meta::removeEnchant);
        return this;
    }


    public ItemStack build() {
        this.item.setItemMeta(this.meta);

        return this.item;
    }

    public ItemBuilder() {
    }
}
