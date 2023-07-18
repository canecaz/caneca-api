package me.caneca.api.api.textcomponent;

import me.caneca.api.api.API;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;

import java.util.List;

public class TCBuilder {

    private TextComponent text;

    public TCBuilder setText(String string){
        text = new TextComponent(TextComponent.fromLegacyText(API.colored(string)));
        return this;
    }

    public TCBuilder setClick(ClickEvent.Action action, String value){
        text.setClickEvent(new ClickEvent(action, value));
        return this;
    }

    public TCBuilder setClick(ClickEvent clickEvent) {
        text.setClickEvent(clickEvent);
        return this;
    }

    public TCBuilder setHover(HoverEvent.Action action, String message){
        text.setHoverEvent(new HoverEvent(action, new ComponentBuilder(API.colored(message)).create()));
        return this;
    }

    public TCBuilder setHover(HoverEvent hoverEvent) {
        text.setHoverEvent(hoverEvent);
        return this;
    }

    public TCBuilder addExtra(TextComponent textComponent){
        text.addExtra(textComponent);
        return this;
    }

    public TCBuilder setBold(){
        text.setBold(true);
        return this;
    }

    public TCBuilder setColor(ChatColor color){
        text.setColor(color);
        return this;
    }

    public TCBuilder setItalic(){
        text.setItalic(true);
        return this;
    }

    public TCBuilder setObfuscated(){
        text.setObfuscated(true);
        return this;
    }

    public TCBuilder setStrikethrough(){
        text.setStrikethrough(true);
        return this;
    }

    public TCBuilder setUnderlined(){
        text.setUnderlined(true);
        return this;
    }

    public TCBuilder setExtra(List<BaseComponent> textComponents){
        text.setExtra(textComponents);
        return this;
    }

    public TextComponent build(){
        return text;
    }
}
