package me.caneca.api.api.itembuilder;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import me.caneca.api.api.API;
import net.minecraft.server.v1_8_R3.NBTBase;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class SkullBuilder {

    private ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
    private SkullMeta meta = (SkullMeta)this.item.getItemMeta();

    public SkullBuilder(ItemStack item) {
        if (item.getType() != Material.SKULL_ITEM)
            return;
        this.item = item;
        this.meta = (SkullMeta)item.getItemMeta();
    }

    public SkullBuilder setOwner(String owner) {
        this.meta.setOwner(owner);
        return this;
    }

    public SkullBuilder setTexture(String texture) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = texture.getBytes();

        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));

        try {
            Field profileField = this.meta.getClass().getDeclaredField("profile");

            profileField.setAccessible(true);
            profileField.set(this.meta, profile);
        } catch (NoSuchFieldException|IllegalArgumentException|IllegalAccessException exception) {
            exception.printStackTrace();
        }

        return this;
    }

    public SkullBuilder setAmount(int amount) {
        this.item.setAmount(amount);
        return this;
    }

    public SkullBuilder setName(String name) {
        this.meta.setDisplayName(API.colored(name));
        return this;
    }

    public SkullBuilder setLore(List<String> lore) {
        return setLore(lore, true);
    }

    public SkullBuilder setLore(List<String> lore, boolean colored) {
        this.meta.setLore(colored ? API.coloredLore(lore) : lore);
        return this;
    }

    public SkullBuilder setLore(String... lore) {
        return setLore(true, lore);
    }

    public SkullBuilder setLore(boolean colored, String... lore) {
        this.meta.setLore(colored ? API.coloredLore(lore) : Arrays.asList(lore));
        return this;
    }

    public SkullBuilder addTag(String tag, NBTBase value) {
        net.minecraft.server.v1_8_R3.ItemStack item = CraftItemStack.asNMSCopy(this.item);
        NBTTagCompound tagCompound = (item.getTag() != null) ? item.getTag() : new NBTTagCompound();

        tagCompound.set(tag, value);

        item.setTag(tagCompound);

        this.meta = (SkullMeta)CraftItemStack.getItemMeta(item);

        return this;
    }

    public SkullBuilder setGlowing(boolean glow) {
        net.minecraft.server.v1_8_R3.ItemStack itemStack = CraftItemStack.asNMSCopy(this.item);
        NBTTagCompound tag = itemStack.hasTag() ? itemStack.getTag() : new NBTTagCompound();

        tag.set("ench", glow ? new NBTTagList() : null);

        this.meta = (SkullMeta)CraftItemStack.getItemMeta(itemStack);

        return this;
    }


    public ItemStack build() {
        this.item.setItemMeta(this.meta);
        return this.item;
    }

    public SkullBuilder() {}
}