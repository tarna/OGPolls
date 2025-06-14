package dev.tarna.ogpolls;

import dev.tarna.ogpolls.commands.PollsCommand;
import dev.tarna.ogpolls.database.DatabaseManager;
import dev.tarna.ogpolls.polls.Poll;
import dev.tarna.ogpolls.polls.PollManager;
import dev.tarna.ogpolls.utils.commands.CommandCaption;
import dev.tarna.ogpolls.utils.commands.CommandSenderMapper;
import dev.tarna.ogpolls.utils.commands.arguments.PollArgument;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.caption.CaptionProvider;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.parser.ParserDescriptor;
import xyz.xenondevs.invui.InvUI;

@Getter
public class OGPolls extends JavaPlugin {
    @Getter
    private static OGPolls plugin;

    private PaperCommandManager<CommandSender> commandManager;
    private AnnotationParser<CommandSender> annotationParser;

    private DatabaseManager databaseManager;
    private PollManager pollManager;

    @Override
    public void onEnable() {
        plugin = this;
        InvUI.getInstance().setPlugin(this);

        saveDefaultConfig();

        String mongoUri = getConfig().getString("mongo.uri", "mongodb://localhost:27017");
        String databaseName = getConfig().getString("mongo.database", "ogpolls");

        databaseManager = new DatabaseManager(mongoUri, databaseName);
        pollManager = new PollManager(this, databaseManager);

        registerCommands();

        getLogger().info("OGPolls has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("OGPolls has been disabled!");
    }

    @SuppressWarnings("UnstableApiUsage")
    private void registerCommands() {
        commandManager = PaperCommandManager.builder(new CommandSenderMapper())
            .executionCoordinator(ExecutionCoordinator.simpleCoordinator())
            .buildOnEnable(this);

        annotationParser = new AnnotationParser<>(
            commandManager,
            CommandSender.class
        );

        commandManager.captionRegistry().registerProvider(
            CaptionProvider.constantProvider(CommandCaption.ARGUMENT_PARSE_FAILURE_POLL, "Invalid poll ID: <input>")
        );

        commandManager.parserRegistry().registerParser(
            ParserDescriptor.of(
                new PollArgument<>(pollManager),
                Poll.class
            )
        );

        annotationParser.parse(
            new PollsCommand(pollManager)
        );
    }
}
