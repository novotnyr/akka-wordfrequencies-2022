package com.github.novotnyr.wordfreq;

public class WordFrequencyCounter {
    public interface Command {
    }

    public interface Event {
    }

    public record CountWordFrequencies(String sentence) implements Command {
    }
}
