package com.github.novotnyr.wordfreq;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.util.Map;

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
                .onMessage(GetWordFrequencies.class, this::getWordFrequencies)
                .build();
    }

    private Behavior<Command> countWordFrequencies(CountWordFrequencies command) {
        String sentence = command.sentence();
        getContext().getLog().debug("Sentence '{}': {}", sentence, Utils.getWordFrequencies(sentence));
        return Behaviors.same();
    }

    private Behavior<Command> getWordFrequencies(GetWordFrequencies command) {
        String sentence = command.sentence();
        var wordFrequencies = Utils.getWordFrequencies(sentence);
        var replyTo = command.replyTo();

        if (Math.random() < 0.5) {
            throw new IllegalStateException("Unable to handle sentence '" + sentence + "'");
        }

        getContext().getLog().debug("Reply to {} on sentence '{}': {}", replyTo, sentence, wordFrequencies);
        replyTo.tell(new FrequenciesCalculated(wordFrequencies));

        return Behaviors.same();
    }

    public interface Command {
    }

    public interface Event {
    }

    public record CountWordFrequencies(String sentence) implements Command {
    }

    public record GetWordFrequencies(String sentence, ActorRef<FrequenciesCalculated> replyTo) implements Command {};

    public record FrequenciesCalculated(Map<String, Long> frequencies) implements Event {
    }
}
