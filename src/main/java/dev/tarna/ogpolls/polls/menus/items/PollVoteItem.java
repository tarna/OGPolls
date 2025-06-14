package dev.tarna.ogpolls.polls.menus.items;

import dev.tarna.ogpolls.polls.Poll;
import dev.tarna.ogpolls.utils.F;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.List;

@AllArgsConstructor
public class PollVoteItem extends AbstractItem {
    private final Poll poll;
    private final String option;

    @Override
    public ItemProvider getItemProvider() {
        return new ItemBuilder(Material.PAPER)
            .setDisplayName(F.colorWrapper("<green>" + option))
            .setLore(List.of(
                F.colorWrapper("<gray>Click to vote for this option."),
                F.colorWrapper("<gray>Current votes: <green>" + poll.getVoteCount(option))
            ));
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent inventoryClickEvent) {
        if (poll.hasVoted(player)) {
            player.sendRichMessage("<red>You have already voted in this poll.");
            return;
        }

        poll.vote(player, option);
        player.sendRichMessage("<green>You voted for: " + option);
        notifyWindows();
    }
}
