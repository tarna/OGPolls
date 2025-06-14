package dev.tarna.ogpolls.polls.menus;

import dev.tarna.ogpolls.OGPolls;
import dev.tarna.ogpolls.polls.Poll;
import dev.tarna.ogpolls.polls.PollManager;
import dev.tarna.ogpolls.polls.menus.items.PollInfoItem;
import dev.tarna.ogpolls.polls.menus.items.PollVoteItem;
import dev.tarna.ogpolls.utils.F;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PollMenus {
    private static final PollManager pollManager = OGPolls.getPlugin().getPollManager();

    public static void openPollsMenu(Player player) {
        Item border = new SimpleItem(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE));
        Gui gui = Gui.normal()
            .setStructure(
                "# # # # # # # # #",
                "# . . . . . . . #",
                "# . . . . . . . #",
                "# . . . . . . . #",
                "# # # # # # # # #")
            .addIngredient('#', border)
            .build();

        HashMap<UUID, Poll> polls = pollManager.getPolls();
        if (polls.isEmpty()) {
            Item noPollsItem = new SimpleItem(new ItemBuilder(Material.BARRIER)
                .setDisplayName(F.colorWrapper("<red>No active polls found!"))
                .setLore(List.of(
                    F.colorWrapper("<gray>Use /polls create to start a new poll.")
                ))
            );
            gui.setItem(4, 2, noPollsItem);
        } else {
            ArrayList<Integer> slots = new ArrayList<>(List.of(
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34
            ));

            for (Poll poll : polls.values()) {
                if (poll.isExpired()) {
                    continue;
                }

                Item pollItem = new PollInfoItem(poll);
                gui.setItem(slots.removeFirst(), pollItem);
            }
        }

        if (player.hasPermission("ogpolls.closed.view")) {
            Item closedPollsItem = new AbstractItem() {
                @Override
                public ItemProvider getItemProvider() {
                    return new ItemBuilder(Material.RED_CONCRETE)
                            .setDisplayName(F.colorWrapper("<red>Closed Polls"))
                            .setLore(List.of(
                                    F.colorWrapper("<gray>Click to view closed polls.")
                            ));
                }

                @Override
                public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent inventoryClickEvent) {
                    openClosedPollsMenu(player);
                }
            };
            gui.setItem(4, 4, closedPollsItem);
        }

        Window window = Window.single()
            .setViewer(player)
            .setTitle("Polls")
            .setGui(gui)
            .build();

        window.open();
    }

    public static void openPollVoteMenu(Player player, Poll poll) {
        Item border = new SimpleItem(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE));
        Gui gui = Gui.normal()
            .setStructure(
                "# # # # # # # # #",
                "# . . . . . . . #",
                "# # # # # # # # #")
            .addIngredient('#', border)
            .build();

        for (String option : poll.getOptions()) {
            Item voteItem = new PollVoteItem(poll, option);
            int slot = 10 + poll.getOptions().indexOf(option);
            gui.setItem(slot, voteItem);
        }

        Item pollInfoItem = new PollInfoItem(poll);
        gui.setItem(4, pollInfoItem);

        Window window = Window.single()
            .setViewer(player)
            .setTitle("Poll")
            .setGui(gui)
            .build();

        window.open();
    }

    public static void openClosedPollsMenu(Player player) {
        Item border = new SimpleItem(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE));
        Gui gui = Gui.normal()
            .setStructure(
                "# # # # # # # # #",
                "# . . . . . . . #",
                "# . . . . . . . #",
                "# # # # # # # # #")
            .addIngredient('#', border)
            .build();

        HashMap<UUID, Poll> polls = pollManager.getPolls();
        boolean hasClosedPolls = false;

        ArrayList<Integer> slots = new ArrayList<>(List.of(
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34
        ));
        for (Poll poll : polls.values()) {
            if (!poll.isExpired()) {
                continue;
            }
            hasClosedPolls = true;
            Item pollItem = new PollInfoItem(poll);
            gui.setItem(slots.removeFirst(), pollItem);
        }

        if (!hasClosedPolls) {
            Item noClosedPollsItem = new SimpleItem(new ItemBuilder(Material.BARRIER)
                .setDisplayName(F.colorWrapper("<red>No closed polls found!"))
                .setLore(List.of(
                    F.colorWrapper("<gray>Use /polls create to start a new poll.")
                ))
            );
            gui.setItem(4, 2, noClosedPollsItem);
        }

        Window window = Window.single()
            .setViewer(player)
            .setTitle("Closed Polls")
            .setGui(gui)
            .build();

        window.open();
    }
}
