package dev.tarna.ogpolls.polls;

import com.mongodb.client.model.ReplaceOptions;
import dev.tarna.ogpolls.OGPolls;
import lombok.Data;
import org.bson.Document;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.*;

import static com.mongodb.client.model.Filters.eq;

@Data
public class Poll {
    private UUID id;
    private Duration duration;
    private String question;
    private ArrayList<String> options;
    private UUID creator;
    private boolean active;
    private long createdAt;
    private long expiresAt;

    private Map<String, List<UUID>> votes = new HashMap<>();

    public Poll(UUID id, Duration duration, String question, ArrayList<String> options, UUID creator, boolean active) {
        this.id = id;
        this.duration = duration;
        this.question = question;
        this.options = options;
        this.creator = creator;
        this.active = active;
        this.createdAt = System.currentTimeMillis();
        this.expiresAt = createdAt + duration.toMillis();
        this.votes = new HashMap<>();
    }

    public Poll(Document document) {
        this.id = document.get("_id", UUID.class);
        this.duration = Duration.ofMillis(document.getLong("duration"));
        this.question = document.getString("question");
        this.options = new ArrayList<>(document.getList("options", String.class));
        this.creator = document.get("creator", UUID.class);
        this.active = document.getBoolean("active", false);
        this.createdAt = document.getLong("createdAt");
        this.expiresAt = document.getLong("expiresAt");

        List<Document> votesDocs = document.getList("votes", Document.class);
        for (Document voteDoc : votesDocs) {
            String option = voteDoc.getString("option");
            List<UUID> voters = voteDoc.getList("voters", UUID.class);
            votes.put(option, voters);
        }
    }

    public String getExpiresIn() {
        long remaining = expiresAt - System.currentTimeMillis();
        if (remaining <= 0) return "Expired";

        long days = remaining / (1000 * 60 * 60 * 24);
        long hours = (remaining / (1000 * 60 * 60)) % 24;
        long minutes = (remaining / (1000 * 60)) % 60;
        long seconds = (remaining / 1000) % 60;
        if (days > 0) {
            return String.format("%dd %dh %dm %ds", days, hours, minutes, seconds);
        } else if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds);
        } else {
            return String.format("%ds", seconds);
        }
    }

    public boolean isExpired() {
        return !active || System.currentTimeMillis() > expiresAt;
    }

    public void addOption(String option) {
        options.add(option);
    }

    public void vote(Player player, String option) {
        if (!options.contains(option)) return;
        if (hasVoted(player)) return;

        UUID uuid = player.getUniqueId();
        votes.computeIfAbsent(option, k -> new ArrayList<>()).add(uuid);
        save();
    }

    public boolean hasVoted(Player player) {
        UUID uuid = player.getUniqueId();
        return votes.values().stream()
            .anyMatch(voters -> voters.contains(uuid));
    }

    public int getVoteCount(String option) {
        return votes.getOrDefault(option, Collections.emptyList()).size();
    }

    public Document toDocument() {
        return new Document("_id", id)
            .append("duration", duration.toMillis())
            .append("question", question)
            .append("options", options)
            .append("creator", creator)
            .append("active", active)
            .append("createdAt", createdAt)
            .append("expiresAt", expiresAt)
            .append("votes", votes.entrySet().stream()
                .map(entry -> new Document("option", entry.getKey())
                    .append("voters", entry.getValue()))
                .toList());
    }

    public void save() {
        OGPolls.getPlugin().getDatabaseManager().getPollsCollection()
            .replaceOne(
                eq("_id", id),
                toDocument(),
                new ReplaceOptions().upsert(true)
            );
    }
}
