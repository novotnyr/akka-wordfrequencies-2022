package com.github.novotnyr.wordfreq;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.Routers;

import java.util.Map;

public class Coordinator extends AbstractBehavior<Coordinator.Command> {
    private ActorRef<WordFrequencyCounter.Command> workers;

    public Coordinator(ActorContext<Command> context) {
        super(context);
        this.workers = context.spawn(createPool(), "workers");
    }

    public static Behavior<Command> create() {
        return Behaviors.setup(Coordinator::new);
    }

    private static Behavior<WordFrequencyCounter.Command> createPool() {
        return Routers.pool(3, WordFrequencyCounter.create());
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(CountWordFrequencies.class, this::calculateWordFrequencies)
                .build();
    }

    private Behavior<Command> calculateWordFrequencies(CountWordFrequencies command) {
        String sentence = command.sentence();
        getContext().getLog().debug("Received sentence '{}'", sentence);

        var workerCommand = new WordFrequencyCounter.CountWordFrequencies(sentence);
        this.workers.tell(workerCommand);

        return Behaviors.same();
    }

    public interface Command {};

    public interface Event {}

    public record CountWordFrequencies(String sentence) implements Command{}

    public record AggregateWordFrequencies(Map<String, Long> frequencies) implements Command{}
}