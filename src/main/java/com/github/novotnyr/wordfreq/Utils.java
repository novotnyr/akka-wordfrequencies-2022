package com.github.novotnyr.wordfreq;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public abstract class Utils {
    public static Map<String, Long> getWordFrequencies(String input) {
        return Stream.of(input.split("\\s"))
                     .collect(groupingBy(String::toString, counting()));
    }

    public static <K> Map<K, Long> mergeAndSumValues(Map<K, Long> firstMap,Map<K, Long> secondMap) {
        Map<K, Long> resultMap = new LinkedHashMap<>(firstMap);
        secondMap.forEach((key, longValue) -> {
            resultMap.merge(key, longValue, Long::sum);
        });
        return resultMap;
    }
}