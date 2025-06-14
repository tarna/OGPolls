package dev.tarna.ogpolls.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;

public class F {
    private static final MiniMessage mm = MiniMessage.miniMessage();

    public static Component color(String text, TagResolver... tagResolvers) {
        return mm.deserialize(text, tagResolvers);
    }

    public static AdventureComponentWrapper colorWrapper(String text, TagResolver... tagResolvers) {
        return new AdventureComponentWrapper(color(text, tagResolvers));
    }
}
