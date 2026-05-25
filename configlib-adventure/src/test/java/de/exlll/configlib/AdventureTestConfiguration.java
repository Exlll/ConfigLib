package de.exlll.configlib;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.List;
import java.util.Map;

@Configuration
public class AdventureTestConfiguration {
    Component simpleText = Component.text("Hello World");

    Component coloredText = Component.text("Red Text", NamedTextColor.RED);

    Component decoratedText = Component.text("Bold Italic",
            NamedTextColor.BLUE, TextDecoration.BOLD, TextDecoration.ITALIC);

    Component clickLink = Component.text("Click Me")
            .clickEvent(ClickEvent.openUrl("https://example.com"));

    Component hoverText = Component.text("Hover Me")
            .hoverEvent(HoverEvent.showText(
                    Component.text("Hover Content", NamedTextColor.GREEN)));

    Component gradientText = MiniMessage.miniMessage()
            .deserialize("<gradient:#ff5555:#5555ff>Gradient Text</gradient>");

    Component rainbowText = MiniMessage.miniMessage()
            .deserialize("<rainbow>Rainbow</rainbow>");

    Component formattedText = MiniMessage.miniMessage()
            .deserialize("<bold>Bold</bold> " +
                    "<italic>Italic</italic> " +
                    "<underlined>Underlined</underlined> " +
                    "<strikethrough>Strike</strikethrough> " +
                    "<obfuscated>Obfuscated</obfuscated>");

    Component clickText = MiniMessage.miniMessage()
            .deserialize("<click:run_command:/test>Click Command</click>");

    // Note: Hover events with complex content inside might be sensitive to
    // serialization round trips
    Component hoverTextComplex = MiniMessage.miniMessage()
            .deserialize("<hover:show_text:'<red>Red Hover'>Hover Text</hover>");
    Component keybindText = MiniMessage.miniMessage()
            .deserialize("<key:key.code>");
    Component translatableText = MiniMessage.miniMessage()
            .deserialize("<lang:block.minecraft.stone>");

    Key simpleKey = Key.key("namespace", "value");

    Sound simpleSound = Sound.sound(
            Key.key("minecraft:entity.player.levelup"),
            Sound.Source.MASTER,
            1f,
            1f);

    List<Component> componentList = List.of(
            Component.text("Item 1"),
            Component.text("Item 2", NamedTextColor.GOLD));

    Map<String, Component> componentMap = Map.of("welcome", Component.text("Welcome!"),
            "goodbye", Component.text("Goodbye!", NamedTextColor.RED));

    // Testing specific component types if possible via MiniMessage or construction
    Component translatable = Component.translatable("item.minecraft.diamond_sword");
    Component keybind = Component.keybind("key.jump");
}
