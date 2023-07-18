package me.caneca.api.api.stringbuilder;

import org.bukkit.ChatColor;

public class Builder {

    private final StringBuilder string = new StringBuilder();

    public Builder add(String string) {
        this.string.append(this.string.length() == 0 ? "" : "\n")
                .append(ChatColor.translateAlternateColorCodes('&', string.length() == 0 ? "&f " : string));
        return this;
    }

    public String build() {
        return this.string.toString();
    }
}
