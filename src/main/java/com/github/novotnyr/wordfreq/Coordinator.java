package com.github.novotnyr.wordfreq;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.SupervisorStrategy;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.Routers;

import java.util.HashMap;
import java.util.Map;

public class Coordinator extends AbstractBehavior<Coordinator.Command> {
    private ActorRef<WordFrequencyCounter.Command> workers;

    private ActorRef<WordFrequencyCounter.FrequenciesCalculated> messageAdapter;

    private Map<String, Long> allFrequencies = new HashMap<>();

    public Coordinator(ActorContext<Command> context) {
        super(context);
        this.workers = context.spawn(createPool(), "workers");
        this.messageAdapter = context.messageAdapter(WordFrequencyCounter.FrequenciesCalculated.class, this::onFrequenciesCalculated);
    }

    public static Behavior<Command> create() {
        return Behaviors.setup(Coordinator::new);
    }

    private static Behavior<WordFrequencyCounter.Command> createPool() {
        return Routers.pool(3, createSupervisedWorker());
    }

    private static Behavior<WordFrequencyCounter.Command> createSupervisedWorker() {
        return Behaviors.supervise(WordFrequencyCounter.create())
                        .onFailure(IllegalStateException.class, SupervisorStrategy.restart());
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(CountWordFrequencies.class, this::calculateWordFrequencies)
                .onMessage(AggregateWordFrequencies.class, this::aggregateWordFrequencies)
                .build();
    }

    private Behavior<Command> calculateWordFrequencies(CountWordFrequencies command) {
        String sentence = command.sentence();
        getContext().getLog().debug("Received sentence '{}'", sentence);

        var workerCommand = new WordFrequencyCounter.GetWordFrequencies(sentence, this.messageAdapter);
        this.workers.tell(workerCommand);

        return Behaviors.same();
    }

    private Behavior<Command> aggregateWordFrequencies(AggregateWordFrequencies command) {
        var partialFrequencies = command.frequencies();
        getContext().getLog().debug("Received partial results: '{}'", partialFrequencies);

        this.allFrequencies = Utils.mergeAndSumValues(this.allFrequencies, partialFrequencies);

        getContext().getLog().debug("Global frequencies: '{}'", this.allFrequencies);

        return Behaviors.same();
    }

    private Command onFrequenciesCalculated(WordFrequencyCounter.FrequenciesCalculated event) {
        return new AggregateWordFrequencies(event.frequencies());
    }

    public interface Command {};

    public interface Event {}

    public record CountWordFrequencies(String sentence) implements Command{}

    public record AggregateWordFrequencies(Map<String, Long> frequencies) implements Command{}
}