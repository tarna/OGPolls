package dev.tarna.ogpolls.polls;

import dev.tarna.ogpolls.OGPolls;
import dev.tarna.ogpolls.database.DatabaseManager;
import dev.tarna.ogpolls.utils.ComponentUtils;
import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;

public class PollManager implements Listener {
    private final DatabaseManager database;

    @Getter
    private final HashMap<UUID, Poll> polls = new HashMap<>();
    @Getter
    private final HashMap<UUID, Poll> creatingPolls = new HashMap<>();
    public PollManager(OGPolls plugin, DatabaseManager database) {
        this.database = database;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        load();
    }

    private void load() {
        database.getPollsCollection().find().forEach(document -> {
            Poll poll = new Poll(document);
            polls.put(poll.getId(), poll);
        });
    }

    public Poll get(UUID id) {
        return polls.get(id);
    }

    public Poll create(Duration duration, String question, ArrayList<String> options, UUID creator) {
        Poll poll = new Poll(UUID.randomUUID(), duration, question, options, creator, false);
        polls.put(poll.getId(), poll);
        return poll;
    }

    public boolean delete(UUID id) {
        Poll poll = get(id);
        if (poll == null) {
            return false;
        }
        polls.remove(id);
        database.getPollsCollection().deleteOne(eq("_id", id));
        return true;
    }

    public void startCreatingPoll(Player player, Duration duration, String question) {
        if (creatingPolls.containsKey(player.getUniqueId())) {
            player.sendRichMessage("<red>You are already creating a poll. Finish or cancel it first.");
            return;
        }

        Poll poll = create(duration, question, new ArrayList<>(), player.getUniqueId());
        creatingPolls.put(player.getUniqueId(), poll);
        player.sendRichMessage("<green>Start typing options for your poll. Type <yellow>cancel <green>to stop adding options.");
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        if (!creatingPolls.containsKey(player.getUniqueId())) {
            return;
        }

        Poll poll = creatingPolls.get(player.getUniqueId());
        if (poll == null) {
            return;
        }

        event.setCancelled(true);

        String message = ComponentUtils.plainText(event.message());
        if (message.equalsIgnoreCase("cancel") || message.equalsIgnoreCase("stop")) {
            poll.setActive(true);
            poll.save();
            creatingPolls.remove(player.getUniqueId());
            player.sendRichMessage("<red>Poll creation cancelled.");
            return;
        }

        poll.addOption(message);

        if (poll.getOptions().size() >= 6) {
            poll.setActive(true);
            poll.save();
            creatingPolls.remove(player.getUniqueId());
            player.sendRichMessage("<green>Poll created with " + poll.getOptions().size() + " options.");
            return;
        }

        player.sendRichMessage("<green>Added option: <yellow>" + message);
        player.sendRichMessage("<green>Type another option or <yellow>cancel <green>to stop adding options.");
    }
}
