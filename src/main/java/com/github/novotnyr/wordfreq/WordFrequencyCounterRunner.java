package com.github.novotnyr.wordfreq;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.PoolRouter;
import akka.actor.typed.javadsl.Routers;

import java.util.Arrays;

public class WordFrequencyCounterRunner {
    public static void main(String[] args) {
        PoolRouter<WordFrequencyCounter.Command> workerPool = Routers.pool(3, WordFrequencyCounter.create());
        var system = ActorSystem.create(workerPool, "system");

        var sentences = Arrays.asList("Honey, Honey",
                "Gimme, Gimme, Gimme",
                "Money, Money, Money",
                "Andante, Andante",
                "I Do, I Do, I Do, I Do, I Do",
                "Ring Ring",
                "On and On and On"
        );

        for (String sentence : sentences) {
            system.tell(new WordFrequencyCounter.CountWordFrequencies(sentence));
        }
    }
}
