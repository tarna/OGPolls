package dev.tarna.ogpolls.utils.commands;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.SenderMapper;
import org.jspecify.annotations.Nullable;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class CommandSenderMapper implements SenderMapper<CommandSourceStack, CommandSender> {
    @Override
    public @NonNull CommandSender map(@NonNull CommandSourceStack source) {
        return source.getSender();
    }

    @Override
    public @NonNull CommandSourceStack reverse(@NonNull CommandSender sender) {
        return new CommandSourceStack() {
            @Override
            public Location getLocation() {
                if (sender instanceof Entity entity) {
                    return entity.getLocation();
                }

                List<World> worlds = Bukkit.getWorlds();
                return new Location(
                        worlds.isEmpty() ? null : worlds.getFirst(),
                        0, 0, 0, 0, 0
                );
            }

            @Override
            public CommandSender getSender() {
                return sender;
            }

            @Override
            public @Nullable Entity getExecutor() {
                return sender instanceof Entity entity ? entity : null;
            }

            @Override
            public CommandSourceStack withLocation(Location location) {
                return this;
            }

            @Override
            public CommandSourceStack withExecutor(Entity entity) {
                return this;
            }
        };
    }
}
