package dev.tarna.ogpolls.commands;

import dev.tarna.ogpolls.polls.Poll;
import dev.tarna.ogpolls.polls.PollManager;
import dev.tarna.ogpolls.polls.menus.PollMenus;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotation.specifier.Greedy;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

import java.time.Duration;

@AllArgsConstructor
@Command("poll|polls")
@Permission("ogpolls.command.polls")
@CommandDescription("Commands for managing polls")
public class PollsCommand {
    private PollManager pollManager;

    @Command("")
    @CommandDescription("View all active polls")
    public void list(Player player) {
        PollMenus.openPollsMenu(player);
    }

    @Command("create <duration> <question>")
    @Permission("ogpolls.command.polls.create")
    @CommandDescription("Create a new poll with a specified duration and question")
    public void create(
        Player player,
        @Argument("duration") Duration duration,
        @Argument("question") @Greedy String question
    ) {
        pollManager.startCreatingPoll(player, duration, question);
    }

    @Command("close <poll>")
    @Permission("ogpolls.command.polls.close")
    @CommandDescription("Close an active poll")
    public void close(
        Player player,
        @Argument("poll") Poll poll
    ) {
        poll.setActive(false);
        player.sendRichMessage("<green>Poll closed successfully!");
    }

    @Command("delete <poll>")
    @Permission("ogpolls.command.polls.delete")
    @CommandDescription("Delete a poll")
    public void delete(
        Player player,
        @Argument("poll") Poll poll
    ) {
        if (pollManager.delete(poll.getId())) {
            player.sendRichMessage("<green>Poll deleted successfully!");
        } else {
            player.sendRichMessage("<red>Failed to delete poll. It may not exist.");
        }
    }
}
