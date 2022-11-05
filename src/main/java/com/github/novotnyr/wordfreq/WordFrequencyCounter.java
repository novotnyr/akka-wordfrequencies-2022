package com.github.novotnyr.wordfreq;

 import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

 import static com.github.novotnyr.wordfreq.Utils.getWordFrequencies;
 import static com.github.novotnyr.wordfreq.WordFrequencyCounter.Command;

public class WordFrequencyCounter extends AbstractBehavior<Command> {

    private WordFrequencyCounter(ActorContext<Command> context) {
        super(context);
    }

    public static Behavior<Command> create() {
        return Behaviors.setup(WordFrequencyCounter::new);
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(CountWordFrequencies.class, this::countWordFrequencies)
                .build();
    }

    private Behavior<Command> countWordFrequencies(CountWordFrequencies command) {
        String sentence = command.sentence();
        getContext().getLog().debug("Sentence '{}': {}", sentence, getWordFrequencies(sentence));
        return Behaviors.same();
    }

    public interface Command {
    }

    public interface Event {
    }

    public record CountWordFrequencies(String sentence) implements Command {
    }
}
