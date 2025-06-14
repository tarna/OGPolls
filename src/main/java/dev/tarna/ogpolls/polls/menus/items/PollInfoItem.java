package dev.tarna.ogpolls.polls.menus.items;

import dev.tarna.ogpolls.polls.Poll;
import dev.tarna.ogpolls.polls.menus.PollMenus;
import dev.tarna.ogpolls.utils.F;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class PollInfoItem extends AbstractItem {
    private final Poll poll;

    @Override
    public ItemProvider getItemProvider() {
        OfflinePlayer creator = Bukkit.getOfflinePlayer(poll.getCreator());
        List<AdventureComponentWrapper> lore = List.of(
                F.colorWrapper("<gray>Created by: " + creator.getName()),
                F.colorWrapper("<gray>Expires in: " + poll.getExpiresIn()),
                F.colorWrapper("<gray>Options:")
        );
        List<AdventureComponentWrapper> optionLines = poll.getOptions().stream()
                .map(opt -> F.colorWrapper("<gray>- " + opt + ": " + poll.getVoteCount(opt)))
                .toList();

        List<AdventureComponentWrapper> fullLore = new ArrayList<>(lore);
        fullLore.addAll(optionLines);
        fullLore.add(F.colorWrapper("<gray>Click to vote on this poll."));
        return new ItemBuilder(Material.PAPER)
            .setDisplayName(F.colorWrapper("<green>Poll: " + poll.getQuestion()))
            .setLore(new ArrayList<>(fullLore));
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent inventoryClickEvent) {
        if (poll.isExpired()) {
            player.sendRichMessage("<red>This poll has expired and cannot be voted on.");
            return;
        }

        PollMenus.openPollVoteMenu(player, poll);
    }
}
