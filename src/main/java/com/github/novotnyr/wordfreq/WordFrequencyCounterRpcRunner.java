package com.github.novotnyr.wordfreq;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.AskPattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.CompletionStage;


public class WordFrequencyCounterRpcRunner {
    public static final Logger logger = LoggerFactory.getLogger(WordFrequencyCounterRpcRunner.class);

    public static void main(String[] args) {
        ActorSystem<WordFrequencyCounter.Command> system = ActorSystem.create(WordFrequencyCounter.create(), "system");

        var sentence = "Gimme, Gimme, Gimme";
        var sentence2 = "Money, Money, Mones";

        AskPattern.
                <WordFrequencyCounter.Command, WordFrequencyCounter.FrequenciesCalculated>ask(system,
                    replyTo -> new WordFrequencyCounter.GetWordFrequencies(sentence, replyTo),
                    Duration.ofSeconds(5),
                    system.scheduler())
                .thenAccept(frequenciesCalculated -> {
                    logger.info("Retrieving frequencies: {}", frequenciesCalculated);
                });

        CompletionStage<WordFrequencyCounter.FrequenciesCalculated> result = AskPattern.
                ask(system,
                        replyTo -> new WordFrequencyCounter.GetWordFrequencies(sentence2, replyTo),
                        Duration.ofSeconds(5), system.scheduler());
        result.thenAccept(frequenciesCalculated -> {
            logger.info("Retrieving frequencies: {}", frequenciesCalculated);
        });

    }
}
