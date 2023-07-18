package me.caneca.api.api.nbt;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class NbtAPI {

    public static ItemStack setNBTData(ItemStack item, String tag, String data){

        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);

        NBTTagCompound itemCompound = (nmsItem.hasTag()) ? nmsItem.getTag() : new NBTTagCompound();

        itemCompound.set(tag, new NBTTagString(data));

        nmsItem.setTag(itemCompound);

        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    public static boolean hasTag(ItemStack item, String tag){
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);

        if(!nmsItem.hasTag()) return false;

        NBTTagCompound itemCompound = nmsItem.getTag();

        return itemCompound.getString(tag) != null;
    }

    public static String getTag(ItemStack item, String tag){
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);

        NBTTagCompound itemCompound = nmsItem.getTag();

        return itemCompound.getString(tag);
    }

}
