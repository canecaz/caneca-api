package me.caneca.api.api.itemserialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.lang.reflect.Method;
import java.math.BigInteger;

import me.caneca.api.Main;
import org.bukkit.inventory.ItemStack;

public class ItemSerialize {
    public static String toBase64(ItemStack itemStack) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutput = new DataOutputStream(outputStream);
        try {
            Object nbtTagListItems = Reflection.getClass_NBTTagList().newInstance();
            Object nbtTagCompoundItem = Reflection.getClass_NBTTagCompound().newInstance();
            Object nms = Reflection.getMethod_asNMSCopy().invoke(null, itemStack);
            Reflection.getMethod_SaveItem().invoke(nms, nbtTagCompoundItem);
            Reflection.getMethod_Add().invoke(nbtTagListItems, nbtTagCompoundItem);
            Reflection.getMethod_Save().invoke(null, nbtTagCompoundItem, dataOutput);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        return (new BigInteger(1, outputStream.toByteArray())).toString(32);
    }

    public static ItemStack fromBase64(String data) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream((new BigInteger(data, 32)).toByteArray());
        Object nmsItem = null;
        Object toReturn = null;
        try {
            Object nbtTagCompoundRoot = Reflection.getMethod_A().invoke(null, new DataInputStream(inputStream));
            if (nbtTagCompoundRoot != null)
                nmsItem = Reflection.getMethod_CreateStack().invoke(null, nbtTagCompoundRoot);
            toReturn = Reflection.getMethod_AsBukkitCopy().invoke(null, nmsItem);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        return (ItemStack)toReturn;
    }

    static class Reflection {
        private static String versionPrefix = "";

        private static Class<?> class_ItemStack;

        private static Class<?> class_NBTBase;

        private static Class<?> class_NBTTagCompound;

        private static Class<?> class_NBTTagList;

        private static Class<?> class_CraftItemStack;

        private static Class<?> class_NBTCompressedStreamTools;

        private static Method method_asNMSCopy;

        private static Method method_SaveItem;

        private static Method method_Add;

        private static Method method_Save;

        private static Method method_A;

        private static Method method_CreateStack;

        private static Method method_AsBukkitCopy;

        static {
            loadClasses();
            loadMethods();
        }

        public static void loadClasses() {
            try {
                String className = Main.getInstance().getServer().getClass().getName();
                String[] packages = className.split("\\.");
                if (packages.length == 5)
                    versionPrefix = packages[3] + ".";
                class_ItemStack = fixBukkitClass("net.minecraft.server.ItemStack");
                class_NBTBase = fixBukkitClass("net.minecraft.server.NBTBase");
                class_NBTTagCompound = fixBukkitClass("net.minecraft.server.NBTTagCompound");
                class_NBTTagList = fixBukkitClass("net.minecraft.server.NBTTagList");
                class_CraftItemStack = fixBukkitClass("org.bukkit.craftbukkit.inventory.CraftItemStack");
                class_NBTCompressedStreamTools = fixBukkitClass("net.minecraft.server.NBTCompressedStreamTools");
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }

        public static void loadMethods() {
            try {
                method_asNMSCopy = getClass_CraftItemStack().getMethod("asNMSCopy", ItemStack.class);
                method_SaveItem = getClass_ItemStack().getMethod("save", getClass_NBTTagCompound());
                method_Add = getClass_NBTTagList().getMethod("add", getClass_NBTBase());
                method_Save = getClass_NBTCompressedStreamTools().getMethod("a", getClass_NBTTagCompound(), DataOutput.class);
                method_A = getClass_NBTCompressedStreamTools().getMethod("a", DataInputStream.class);
                method_CreateStack = getClass_ItemStack().getMethod("createStack", getClass_NBTTagCompound());
                method_AsBukkitCopy = getClass_CraftItemStack().getMethod("asBukkitCopy", getClass_ItemStack());
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }

        private static Class<?> fixBukkitClass(String className) {
            className = className.replace("org.bukkit.craftbukkit.", "org.bukkit.craftbukkit." + versionPrefix);
            className = className.replace("net.minecraft.server.", "net.minecraft.server." + versionPrefix);
            try {
                return Class.forName(className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }

        static Method getMethod_AsBukkitCopy() {
            return method_AsBukkitCopy;
        }

        static Method getMethod_CreateStack() {
            return method_CreateStack;
        }

        static Method getMethod_A() {
            return method_A;
        }

        static Method getMethod_asNMSCopy() {
            return method_asNMSCopy;
        }

        static Method getMethod_SaveItem() {
            return method_SaveItem;
        }

        static Method getMethod_Add() {
            return method_Add;
        }

        static Method getMethod_Save() {
            return method_Save;
        }

        private static Class<?> getClass_ItemStack() {
            return class_ItemStack;
        }

        private static Class<?> getClass_NBTBase() {
            return class_NBTBase;
        }

        static Class<?> getClass_NBTTagCompound() {
            return class_NBTTagCompound;
        }

        static Class<?> getClass_NBTTagList() {
            return class_NBTTagList;
        }

        private static Class<?> getClass_CraftItemStack() {
            return class_CraftItemStack;
        }

        private static Class<?> getClass_NBTCompressedStreamTools() {
            return class_NBTCompressedStreamTools;
        }
    }
}
