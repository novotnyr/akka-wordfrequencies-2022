package com.github.novotnyr.wordfreq;

import akka.actor.typed.ActorSystem;

import java.util.Arrays;

public class CoordinatorRunner {
    public static void main(String[] args) {
        var system = ActorSystem.create(Coordinator.create(), "system");

        var sentences = Arrays.asList("Honey, Honey",
                "Gimme, Gimme, Gimme",
                "Money, Money, Money",
                "Andante, Andante",
                "I Do, I Do, I Do, I Do, I Do",
                "Ring Ring",
                "On and On and On"
        );

        for (String sentence : sentences) {
            system.tell(new Coordinator.CountWordFrequencies(sentence));
        }
    }
}
