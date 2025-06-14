package dev.tarna.ogpolls.utils.commands.arguments;

import dev.tarna.ogpolls.polls.Poll;
import dev.tarna.ogpolls.polls.PollManager;
import dev.tarna.ogpolls.utils.commands.CommandCaption;
import lombok.AllArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.caption.CaptionVariable;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.exception.parsing.ParserException;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
public class PollArgument<C> implements ArgumentParser<C, Poll> {
    private PollManager pollManager;

    @Override
    public @NonNull ArgumentParseResult<Poll> parse(@NonNull CommandContext<C> commandContext, @NonNull CommandInput commandInput) {
        String input = commandInput.readString();
        try {
            UUID pollId = UUID.fromString(input);
            Poll poll = pollManager.get(pollId);
            if (poll != null) {
                return ArgumentParseResult.success(poll);
            } else {
                return ArgumentParseResult.failure(new PollParseException(input, commandContext));
            }
        } catch (IllegalArgumentException e) {
            return ArgumentParseResult.failure(new PollParseException(input, commandContext));
        }
    }

    @Override
    public @NotNull SuggestionProvider<C> suggestionProvider() {
        return (context, input) -> {
            List<Suggestion> suggestions = pollManager.getPolls().values().stream()
                    .map(item -> Suggestion.suggestion(item.getId().toString()))
                    .toList();
            return CompletableFuture.completedFuture(suggestions);
        };
    }

    static class PollParseException extends ParserException {
        private final String input;
        private final CommandContext<?> commandContext;
        public PollParseException(String input, CommandContext<?> context) {
            super(
                PollArgument.class,
                context,
                CommandCaption.ARGUMENT_PARSE_FAILURE_POLL,
                CaptionVariable.of("input", input)
            );
            this.input = input;
            this.commandContext = context;
        }

        public String input() {
            return input;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PollParseException that)) return false;
            return input.equals(that.input) && commandContext.equals(that.commandContext);
        }

        @Override
        public int hashCode() {
            return 31 * input.hashCode() + commandContext.hashCode();
        }
    }
}
