package me.caneca.api.api;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.caneca.api.Main;
import me.caneca.music.tags.utils.api.TagsAPI;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class API {

    private static final Random random = new Random();

    public static void registerCommand(JavaPlugin plugin, String command, CommandExecutor executor) {
        plugin.getCommand(command).setExecutor(executor);
    }

    public static void registerListener(JavaPlugin plugin, Listener listener) {
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    public static void registerTabCompleter(JavaPlugin plugin, String command, TabCompleter tabCompleter) {
        plugin.getCommand(command).setTabCompleter(tabCompleter);
    }

    public static void playSound(CommandSender sender, Sound sound, float f){
        if (Bukkit.getPlayer(sender.getName()) != null)
            ((Player)sender).playSound(((Player)sender).getLocation(), sound, 2, f);
    }

    public static void playSound(Player player, Sound sound, float f){
        player.playSound(player.getLocation(), sound, 2, f);
    }

    public static void semPerm(CommandSender sender, boolean sound){
        sender.sendMessage(colored("&cVocê não tem permissão para isso."));
        if (sound)
            VILLAGER_NO(sender);
    }

    public static boolean isConsole(CommandSender sender) {
        return !(sender instanceof Player);
    }

    public static void semPerm(Player player, boolean sound){
        player.sendMessage(colored("&cVocê não tem permissão para isso."));
        if (sound)
            VILLAGER_NO(player);
    }

    public static void offline(CommandSender sender, boolean sound){
        sender.sendMessage(colored("&cJogador offline."));
        if (sound)
            VILLAGER_NO(sender);
    }

    public static void offline(Player player, boolean sound){
        player.sendMessage(colored("&cJogador offline."));
        if (sound)
            VILLAGER_NO(player);
    }

    public static void nick(CommandSender sender, boolean sound){
        sender.sendMessage(colored("&cNão utilize seu nick aqui."));
        if (sound)
            VILLAGER_NO(sender);
    }

    public static void nick(Player player, boolean sound){
        player.sendMessage(colored("&cNão utilize seu nick aqui."));
        if (sound)
            VILLAGER_NO(player);
    }

    public static boolean hasPermission(CommandSender sender, String group) {
        if (!sender.hasPermission("permissions." + group)) {
            semPerm(sender, true);
            return false;
        }
        return true;
    }

    public static boolean hasPermission(Player player, String group) {
        if (!player.hasPermission("permissions." + group)) {
            semPerm(player, true);
            return false;
        }
        return true;
    }

    public static void utilize(CommandSender sender, String string) {
        sender.sendMessage(colored("&cUtilize: /" + string + "."));
        VILLAGER_NO(sender);
    }

    public static void VILLAGER_NO(CommandSender sender) {
        playSound(sender, Sound.VILLAGER_NO, 1);
    }

    public static void VILLAGER_NO(Player player) {
        playSound(player, Sound.VILLAGER_NO, 1);
    }

    public static void LEVEL_UP(CommandSender sender) {
        playSound(sender, Sound.LEVEL_UP, 2);
    }

    public static void LEVEL_UP(Player player) {
        playSound(player, Sound.LEVEL_UP, 2);
    }

    public static boolean isSameNick(CommandSender sender, Player target){
        return sender instanceof Player && sender.getName().equalsIgnoreCase(target.getName());
    }

    public static void setItem(Inventory inv, int slot, Material m, byte b, int amount, String name, List<String> lore){
        ItemStack item = new ItemStack(m, amount, b);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(slot, item);
    }

    public static boolean isInt(String string){
        try{
            Integer.parseInt(string);
        } catch (NumberFormatException e){
            return false;
        }
        return true;
    }

    public static boolean isDouble(String string){
        try{
            Double.parseDouble(string);
        } catch (NumberFormatException e){
            return false;
        }
        return true;
    }

    public static void invalidNumber(Player player) {
        player.sendMessage(colored("&cNúmero inválido."));
        VILLAGER_NO(player);
    }

    public static void invalidNumber(CommandSender sender) {
        sender.sendMessage(colored("&cNúmero inválido."));
        VILLAGER_NO(sender);
    }

    public static String getName(String player){
        return Main.getInstance().isLuck() && Main.getInstance().isTagsAPI() ?
                TagsAPI.getPlayerName(player) :
                getPrefix(player) + player;
    }

    public static String getName(OfflinePlayer player){
        return player.getName();
    }

    public static String getName(Player player){
        return getName(player.getName());
    }

    public static String getPrefix(Player player) {
        return getPrefix(player.getName());
    }

    public static String getPrefix(String player){
        if (Main.getInstance().isLuck()) {
            if (!Main.getInstance().isTagsAPI()) {
                if (Bukkit.getPlayer(player) == null) return player;
                User user = Main.getInstance().getLuckAPI().getUserManager().getUser(player);
                if (user == null) return player;
                String groupString = user.getPrimaryGroup();
                if (groupString.equals("")) return player;
                Group group = Main.getInstance().getLuckAPI().getGroupManager().getGroup(groupString);
                if (group == null) return player;
                return colored(Objects.requireNonNull(group.getCachedData().getMetaData().getPrefix()));
            }
            return TagsAPI.getPlayerPrefix(player);
        }
        return "";
    }

    public static String getGroup(Player player) {
        return getGroup(player.getName());
    }

    public static String getGroup(String player) {
        if (Bukkit.getPlayer(player) == null) return "";
        User user = Main.getInstance().getLuckAPI().getUserManager().getUser(player);
        if (user == null) return player;
        String groupString = user.getPrimaryGroup();
        if (groupString.equals("")) return "";
        Group group = Main.getInstance().getLuckAPI().getGroupManager().getGroup(groupString);
        if (group == null) return "";
        return group.getName();
    }

    public static String format(long time) {
        String format = "";
        long days = TimeUnit.MILLISECONDS.toDays(time);
        long daysInMillis = TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(time - daysInMillis);
        long hoursInMillis = TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(time - (daysInMillis + hoursInMillis));
        long minutesInMillis = TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(time - (daysInMillis + hoursInMillis + minutesInMillis));
        if (days > 0)
            format = days + (hours > 0 || minutes > 0 || seconds > 0 ? "d, " : "d");
        if (hours > 0)
            format += hours + (minutes > 0 || seconds > 0 ? "h, " : "h");
        if (minutes > 0)
            format += minutes + (seconds > 0 ? "m, " : "m");
        if (seconds > 0)
            format += seconds + "s";
        if (format.equals("")) {
            long rest = time / 100;
            if (rest == 0)
                rest = 1;
            format = "0." + rest + "s";
        }
        return format;
    }

    public static String formatPretty(long time) {
        String format = "";
        long days = TimeUnit.MILLISECONDS.toDays(time);
        long daysInMillis = TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(time - daysInMillis);
        long hoursInMillis = TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(time - (daysInMillis + hoursInMillis));
        long minutesInMillis = TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(time - (daysInMillis + hoursInMillis + minutesInMillis));
        if (days > 0)
            format = days + (hours > 0 || minutes > 0 || seconds > 0 ? " dias, " : " dias");
        if (hours > 0)
            format += hours + (minutes > 0 || seconds > 0 ? " horas, " : " horas");
        if (minutes > 0)
            format += minutes + (seconds > 0 ? " minutos, " : " minutos");
        if (seconds > 0)
            format += seconds + " segundos";
        if (format.equals("")) {
            long rest = time / 100;
            if (rest == 0)
                rest = 1;
            format = "0." + rest + "s";
        }
        return format;
    }

    public static List<Block> getBlocksBetweenPoints(Location l1, Location l2) {

        List<Block> blocks = new ArrayList<>();

        int topBlockX = (Math.max(l1.getBlockX(), l2.getBlockX()));
        int bottomBlockX = (Math.min(l1.getBlockX(), l2.getBlockX()));
        int topBlockY = (Math.max(l1.getBlockY(), l2.getBlockY()));
        int bottomBlockY = (Math.min(l1.getBlockY(), l2.getBlockY()));
        int topBlockZ = (Math.max(l1.getBlockZ(), l2.getBlockZ()));
        int bottomBlockZ = (Math.min(l1.getBlockZ(), l2.getBlockZ()));

        for(int x = bottomBlockX; x <= topBlockX; x++) {
            for(int y = bottomBlockY; y <= topBlockY; y++) {
                for(int z = bottomBlockZ; z <= topBlockZ; z++) {
                    Block block = l1.getWorld().getBlockAt(x, y, z);
                    blocks.add(block);
                }
            }
        }
        return blocks;
    }

    public static String getDate(boolean week){
        Locale locale = new Locale("en", "UK");
        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(locale);
        String pattern = "dd/MM HH:mm:ss";
        if (week) {
            dateFormatSymbols.setWeekdays(new String[]{
                    "Unused",
                    "Domingo",
                    "Segunda-Feira",
                    "Terça-Feira",
                    "Quarta-Feira",
                    "Quinta-Feira",
                    "Sexta-Feira",
                    "Sábado",
            });
            pattern = "EEEEE dd/MM HH:mm:ss";
        }

        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat(pattern, dateFormatSymbols);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT-3"));
        return simpleDateFormat.format(new Date());
    }

    private static String formatValue(double value) {
        boolean isWholeNumber = (value == Math.round(value));
        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
        formatSymbols.setDecimalSeparator('.');
        String pattern = isWholeNumber ? "###,###.###" : "###,##0.00";
        DecimalFormat df = new DecimalFormat(pattern, formatSymbols);
        return df.format(value);
    }

    public static String normalFormat(double amount) {
        amount = getMoneyRounded(amount);
        return formatValue(amount);
    }

    public static double getMoneyRounded(double amount) {
        DecimalFormat twoDForm = new DecimalFormat("###.##");
        String formattedAmount = twoDForm.format(amount);
        formattedAmount = formattedAmount.replace(",", ".");
        return Double.parseDouble(formattedAmount);
    }

    private static final String[] suffix = { "", "K", "M", "B", "T", "Q", "QQ", "S", "SS", "OC", "N", "D", "UN", "DD", "TR", "QT", "QN", "SD", "SPD", "OD", "ND", "VG", "UVG", "DVG", "TVG", "QVG", "QQVG", "SVG", "SSVG", "OVG", "NVG", "TG", "UTG", "DTG", "TTG", "QTG", "QQTG", "STG", "SSTG", "OTG", "NTG", "QG", "UQG", "DQG", "TQG", "QQG", "QQQG", "SQG", "SSQG", "OQG", "NQG", "QuG", "UQuG", "DQuG", "TQuG", "QQuG", "QQQuG", "SuG", "SSuG", "OuG", "NuG", "SG", "USG", "DSG", "TSG", "QSG", "QQSG", "SSG", "SSSG", "OSG", "NSG", "SeG", "USeG", "DSeG", "TSeG", "QSeG", "QQSeG", "SSeG", "SSSeG", "OSeG", "NSeG", "OG", "UOG", "DOG", "TOG", "QOG", "QQOG", "SOG", "SSOG", "OOG", "NOG", "NG", "UNG", "DNG", "TNG", "QNG", "QQNG", "SNG", "SSNG", "ONG", "NNG", "CT"};

    public static BigDecimal backwards(String string) {
        int zeros = 3;
        for (String suffix : suffix) {
            if (suffix.isEmpty()) continue;
            if (string.endsWith(suffix) && isInt(String.valueOf(string.charAt(string.length()-suffix.length()-1))))
                return new BigDecimal(string.substring(0, string.length() - suffix.length())).movePointRight(zeros);
            zeros+= 3;
        }
        return new BigDecimal(string);
    }

    public static String opFormat(Number number) {
        return opFormat(new BigDecimal(number.toString()));
    }

    public static String opFormat(BigDecimal decimal) {
        decimal = decimal.setScale(0, RoundingMode.DOWN);
        int zeros = decimal.precision() - 1;
        if (zeros >= 3) {
            int k = zeros / 3;
            if (k >= suffix.length)
                return "999.9CT";
            return decimal.movePointLeft((k * 3)).setScale(1, RoundingMode.DOWN) + suffix[k];
        } else
            return decimal.toString();
    }

    public static int getRandom(int lowest, int highest) {
        return random.nextInt((highest - lowest) + 1) + lowest;
    }

    public static double getRandom(double lowest, double highest) {
        return lowest + (highest - lowest) * random.nextDouble();
    }

    public static int getRandom(){
        return random.nextInt(100) + 1;
    }

    public static double round(double value, int places) {
        if (places < 0)
            throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static void sendActionBar(Player p, String msg) {
        IChatBaseComponent cbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + msg + "\"}");
        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, (byte)2);
        (((CraftPlayer)p).getHandle()).playerConnection.sendPacket(ppoc);
    }



    public static void sendTitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle) {
        PlayerConnection connection = (((CraftPlayer)player).getHandle()).playerConnection;

        PacketPlayOutTitle packetPlayOutTimes = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TIMES, null, fadeIn, stay, fadeOut);
        connection.sendPacket(packetPlayOutTimes);
        if (subtitle != null) {

            subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);
            IChatBaseComponent titleSub = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + subtitle + "\"}");
            PacketPlayOutTitle packetPlayOutSubTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, titleSub);
            connection.sendPacket(packetPlayOutSubTitle);
        }
        if (title != null) {

            title = ChatColor.translateAlternateColorCodes('&', title);
            IChatBaseComponent titleMain = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + title + "\"}");
            PacketPlayOutTitle packetPlayOutTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, titleMain);
            connection.sendPacket(packetPlayOutTitle);
        }
    }

    public static void removeAI(Entity entity){
        net.minecraft.server.v1_8_R3.Entity nmsEn = ((CraftEntity) entity).getHandle();
        NBTTagCompound comp = new NBTTagCompound();
        nmsEn.c(comp);
        comp.setByte("NoAI", (byte) 1);
        nmsEn.f(comp);
        nmsEn.b(true);
    }

    public static void saveFile(File file, YamlConfiguration f){
        try{
            f.save(file);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void log(String message){
        Bukkit.getServer().getConsoleSender().sendMessage(message);
    }

    public static void drawTornado(Plugin plugin, final Location loc, float radius, float increase, final EnumParticle particle) {
        float y = (float)loc.getY(); double t;
        for (t = 0.0D; t < 10.0D; t += 0.05D) {
            final float x = radius * (float)Math.sin(t);
            final float z = radius * (float)Math.cos(t);
            final float finalY = y;
            (new BukkitRunnable()
            {
                public void run() {
                    PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(particle, true, (float)loc.getX() + x, finalY, (float)loc.getZ() + z, 0.0F, 0.0F, 0.0F, 0.0F, 1);
                    for (Player online : Bukkit.getOnlinePlayers()) {
                        if (online.getWorld().getName().equals(loc.getWorld().getName())) {
                            (((CraftPlayer) online).getHandle()).playerConnection.sendPacket(packet);
                        }
                    }
                }
            }).runTaskAsynchronously(plugin);
            y += 0.01F;
        }
    }

    public static void sendParticle(Plugin plugin, final Location loc, EnumParticle particle){
        (new BukkitRunnable()
        {
            public void run() {
                PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(particle, true, (float)loc.getX(), (float)loc.getY() + 2.0F, (float)loc.getZ(), 0.0F, 0.0F, 0.0F, 0.0F, 1);
                for (Player online : Bukkit.getOnlinePlayers()) {
                    if (online.getWorld().getName().equals(loc.getWorld().getName())) {
                        (((CraftPlayer) online).getHandle()).playerConnection.sendPacket(packet);
                    }
                }
            }
        }).runTaskAsynchronously(plugin);
    }

    public static void drawCircle(Plugin plugin, final Location loc, float radius, final EnumParticle particle) {
        for (double t = 0.0D; t < 50.0D; t += 0.5D) {
            final float x = radius * (float)Math.sin(t);
            final float z = radius * (float)Math.cos(t);
            (new BukkitRunnable()
            {
                public void run() {
                    PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(particle, true, (float)loc.getX() + x, (float)loc.getY() + 2.0F, (float)loc.getZ() + z, 0.0F, 0.0F, 0.0F, 0.0F, 1);
                    for (Player online : Bukkit.getOnlinePlayers()) {
                        if (online.getWorld().getName().equals(loc.getWorld().getName())) {
                            (((CraftPlayer) online).getHandle()).playerConnection.sendPacket(packet);
                        }
                    }
                }
            }).runTaskAsynchronously(plugin);
        }
    }

    public enum Yaw {
        NORTH, SOUTH, EAST, WEST;

        public static Yaw getYaw(Player player) {
            float yaw = player.getLocation().getYaw();
            if (yaw < 0)
                yaw += 360;
            if (yaw >= 315 || yaw < 45)
                return Yaw.SOUTH;
            else if (yaw < 135)
                return Yaw.WEST;
            else if (yaw < 225)
                return Yaw.NORTH;
            else if (yaw < 315)
                return Yaw.EAST;
            return Yaw.NORTH;
        }
    }

    public static Location getLocationFromString(String string) {
        String[] split = string.split(";");
        World world = Bukkit.getWorld(split[0]);
        double x = split.length > 1 ? Double.parseDouble(split[1]) : 0D;
        double y = split.length > 2 ? Double.parseDouble(split[2]) : 0D;
        double z = split.length > 3 ? Double.parseDouble(split[3]) : 0D;
        float yaw = split.length > 4 ? Float.parseFloat(split[4]) : 0F;
        float pitch = split.length > 5 ? Float.parseFloat(split[5]) : 0F;
        return new Location(world, x, y, z, yaw, pitch);
    }

    public static String getStringFromLocation(Location location) {
        return getStringFromLocation(location, true);
    }

    public static String getStringFromLocation(Location location, boolean yawAndPitch){
        String loc = location.getWorld().getName() + ";" + location.getX() + ";" + location.getY() + ";" + location.getZ();
        if (yawAndPitch) loc += ";" + location.getYaw() + ";" + location.getPitch();
        return loc;

    }

    public static String colored(String string) {
        return string.equals("") ? "" : ChatColor.translateAlternateColorCodes('&', string);
    }

    public static String getFromArgs(int startIndex, String[] args, boolean colorized) {
        StringBuilder sb = new StringBuilder();
        for (int i = startIndex; i < args.length; i++)
            sb.append(colorized ? colored(args[i]) : args[i]).append(" ");
        return sb.toString().trim();
    }

    public static boolean checkPlayer(CommandSender sender, Player target) {
        if (target == null) {
            offline(sender, true);
            return false;
        }
        if (isSameNick(sender, target)) {
            nick(sender, true);
            return false;
        }
        return true;
    }

    public static boolean isNotAir(ItemStack item) {
        return item != null && item.getType() != Material.AIR;
    }

    public static void connectToServer(JavaPlugin plugin, Player player, String server) {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF("Connect");
        output.writeUTF(server);
        player.sendPluginMessage(plugin, "BungeeCord", output.toByteArray());
    }

    public static void sendMessageToBungee(JavaPlugin plugin, Player player, String channel, String message) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF(channel);
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.sendPluginMessage(Main.getPlugin(Main.class), "BungeeCord", b.toByteArray());
    }

    public static List<String> coloredLore(String... strings) {
        return Arrays.stream(strings).map(API::colored).collect(Collectors.toList());
    }

    public static List<String> coloredLore(List<String> lore) {
        return lore.stream().map(API::colored).collect(Collectors.toList());
    }

}

