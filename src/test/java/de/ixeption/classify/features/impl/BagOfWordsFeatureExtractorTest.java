package de.ixeption.classify.features.impl;

import de.ixeption.classify.features.TextFeature;
import de.ixeption.classify.features.WordIndexing;
import de.ixeption.classify.postprocessing.impl.StemmingNGrammProcessor;
import de.ixeption.classify.tokenization.Token;
import de.ixeption.classify.tokenization.impl.NormalizingTextTokenizer;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toCollection;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class BagOfWordsFeatureExtractorTest {

    public static final int MIN_LENGTH = 3;
    private static BagOfWordsFeatureExtractor bagOfWordsFeatureExtractor;
    static StemmingNGrammProcessor processor = new StemmingNGrammProcessor(2, MIN_LENGTH, 25);
    private static NormalizingTextTokenizer normalizingTextTokenizer = new NormalizingTextTokenizer();
    private static LinkedHashSet<Token> corpus;
    private static Set<String> sentences;

    @BeforeAll
    public static void setUp() {
        sentences = Sets.newLinkedHashSet();
        sentences.add("Hello, my name is Jimmy Pop");
        sentences.add("And I'm a dumb white guy");
        sentences.add("I'm not old or new but middle school");
        sentences.add("Fifth grade like junior high");
        sentences.add("I don't know mofo if y'all peeps");
        sentences.add("Be buggin' give props to my ho 'cause she fly");
        sentences.add("But I can take the heat 'cause I'm the other white meat");
        sentences.add("Known as 'Kid Funky Fried'");

        corpus = sentences.stream()
                .map(TextFeature::new)
                .map(normalizingTextTokenizer::tokenize)
                .map(processor::process)
                .flatMap(Arrays::stream)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        bagOfWordsFeatureExtractor = new BagOfWordsFeatureExtractor(corpus);
    }

    @Test
    public void testIndexExists() throws WordIndexing.IndexerException {

        assertThat(bagOfWordsFeatureExtractor
                .getIndex(processor.process(normalizingTextTokenizer.tokenize(new TextFeature("hello")))[0].getText())).isEqualTo(0);
        assertThat(bagOfWordsFeatureExtractor
                .getIndex(processor.process(normalizingTextTokenizer.tokenize(new TextFeature("fried")))[0].getText())).isEqualTo(73);
        assertThat(bagOfWordsFeatureExtractor
                .getIndex(processor.process(normalizingTextTokenizer.tokenize(new TextFeature("kid funky")))[2].getText())).isEqualTo(75);
    }

    @Test
    public void testDictSize() {
        int ngrams = processor.getnGramsCount();
        StemmingNGrammProcessor processor1 = new StemmingNGrammProcessor(1, MIN_LENGTH, 25);
        int numTokens = sentences.stream().map(TextFeature::new).map(normalizingTextTokenizer::tokenize).map(processor1::process).flatMap(Arrays::stream).collect(toCollection(LinkedHashSet::new)).size();
        assertThat(bagOfWordsFeatureExtractor.getDictSize()).isEqualTo(numTokens + numTokens - ngrams - 1);
    }

    @Test()
    public void testIndexNotExists() {
        assertThrows(WordIndexing.IndexerException.class, () -> bagOfWordsFeatureExtractor.getIndex("Rofl"));

    }

    @Test()
    public void testIndexNotNGramExists() {
        assertThrows(WordIndexing.IndexerException.class, () -> bagOfWordsFeatureExtractor.getIndex("as 'Kid"));

    }

}
