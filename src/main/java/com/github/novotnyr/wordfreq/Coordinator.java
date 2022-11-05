package com.github.novotnyr.wordfreq;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class Coordinator extends AbstractBehavior<Coordinator.Command> {

    public Coordinator(ActorContext<Command> context) {
        super(context);
    }

    public static Behavior<Command> create() {
        return Behaviors.setup(Coordinator::new);
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(CountWordFrequencies.class, this::calculateWordFrequencies)
                .build();
    }

    private Behavior<Command> calculateWordFrequencies(CountWordFrequencies command) {
        return Behaviors.same();
    }

    public interface Command {};

    public interface Event {}

    public record CountWordFrequencies(String sentence) implements Command{}

}