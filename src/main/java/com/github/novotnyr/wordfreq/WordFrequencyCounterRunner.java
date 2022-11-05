package com.github.novotnyr.wordfreq;

import akka.actor.typed.ActorSystem;

import java.util.Arrays;

public class WordFrequencyCounterRunner {
    public static void main(String[] args) {
        var system = ActorSystem.create(WordFrequencyCounter.create(), "system");

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
