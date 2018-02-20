package com.ixeption.ml.text.classification.features.impl;

import com.ixeption.ml.text.classification.features.WordIndexing;
import org.assertj.core.util.Sets;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static org.assertj.core.api.Assertions.assertThat;

public class BagOfWordsFeatureExtractorTest {

    public static final int MIN_LENGHT = 3;
    BagOfWordsFeatureExtractor bagOfWordsFeatureExtractor;
    private LinkedHashSet<String> corpus;

    @Before
    public void setUp() {
        corpus = Sets.newLinkedHashSet();
        corpus.add("Hello, my name is Jimmy Pop");
        corpus.add("And I'm a dumb white guy");
        corpus.add("I'm not old or new but middle school");
        corpus.add("Fifth grade like junior high");
        corpus.add("I don't know mofo if y'all peeps");
        corpus.add("Be buggin' give props to my ho 'cause she fly");
        corpus.add("But I can take the heat 'cause I'm the other white meat");
        corpus.add("Known as 'Kid Funky Fried'");

        bagOfWordsFeatureExtractor = new BagOfWordsFeatureExtractor(2, MIN_LENGHT, corpus);
    }

    @Test
    public void testIndexExists() throws WordIndexing.IndexerException {
        assertThat(bagOfWordsFeatureExtractor.getIndex(bagOfWordsFeatureExtractor.extractTokens("hello")[0])).isEqualTo(0);
        assertThat(bagOfWordsFeatureExtractor.getIndex(bagOfWordsFeatureExtractor.extractTokens("fried")[0])).isEqualTo(73);
        assertThat(bagOfWordsFeatureExtractor.getIndex(bagOfWordsFeatureExtractor.extractTokens("kid funky")[2])).isEqualTo(80);
    }

    @Test
    public void testDictSize() {
        int ngrams = bagOfWordsFeatureExtractor.getnGramsCount();
        BagOfWordsFeatureExtractor noNGram = new BagOfWordsFeatureExtractor(0, MIN_LENGHT, corpus);
        int expectedTokens = corpus.stream().map(noNGram::extractTokens).mapToInt(value -> value.length).map(count -> count + (count - ngrams + 1)).sum();
        Map<String, Long> counts = corpus.stream().map(noNGram::extractTokens).flatMap(Arrays::stream).collect(groupingBy(Function.identity(), counting()));
        int multiOccurrences = counts.entrySet().stream().mapToInt(value -> (int) (value.getValue() - 1L)).sum();
        assertThat(bagOfWordsFeatureExtractor.getDictSize()).isEqualTo(expectedTokens - multiOccurrences);
    }

    @Test(expected = WordIndexing.IndexerException.class)
    public void testIndexNotExists() throws WordIndexing.IndexerException {
        assertThat(bagOfWordsFeatureExtractor.getIndex("Rofl")).isEqualTo(0);

    }

    @Test(expected = WordIndexing.IndexerException.class)
    public void testIndexNotNGramExists() throws WordIndexing.IndexerException {
        assertThat(bagOfWordsFeatureExtractor.getIndex("as 'Kid")).isEqualTo(0);

    }


}
