package de.ixeption.classify.features.impl;

import de.ixeption.classify.features.TextFeature;
import de.ixeption.classify.features.WordIndexing;
import de.ixeption.classify.pipeline.TokenizedText;
import de.ixeption.classify.postprocessing.TokenProcessor;
import de.ixeption.classify.postprocessing.impl.StemmingNGrammProcessor;
import de.ixeption.classify.tokenization.impl.NormalizingTextTokenizer;
import org.assertj.core.util.Sets;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import smile.math.SparseArray;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


public class AbstractBagOfWordsFeatureExtractorTest {

    private static final int MIN_LENGTH = 3;
    static TokenProcessor processor = new StemmingNGrammProcessor(1, MIN_LENGTH, 25);

    public static Stream<AbstractBagOfWordsFeatureExtractor> getImplementations() {
        Set<String> corpus = Sets.newLinkedHashSet();
        corpus.add("Hello, my name is Jimmy Pop");
        corpus.add("And I'm a dumb white guy");
        corpus.add("I'm not old or new but middle school");
        corpus.add("Fifth grade like junior high");
        corpus.add("I don't know mofo if y'all peeps");
        corpus.add("Be buggin' give props to my ho 'cause she fly");
        corpus.add("But I can take the heat 'cause I'm the other white meat");
        corpus.add("Known as 'Kid Funky Fried'");
        NormalizingTextTokenizer normalizingTextTokenizer = new NormalizingTextTokenizer();
        Set<String> strings = corpus.stream().map(TextFeature::new)
                .map(normalizingTextTokenizer::tokenize)
                .map(processor::process)
                .map(TokenizedText::getTokens)
                .flatMap(Arrays::stream)
                .collect(Collectors.toSet());

        return Stream.of(new BagOfWordsFeatureExtractor(strings), new HashTrickBagOfWordsFeatureExtractor(1337));
    }

    @ParameterizedTest(name = "{index}: Impl Class: {0}")
    @MethodSource("getImplementations")
    public void testBagOfWords(AbstractBagOfWordsFeatureExtractor cut) throws WordIndexing.IndexerException {
        String[] s = "known my buggin fifth grade".split(" ");
        SparseArray extract = cut.extract(s);
        String[] tokens = Arrays.stream(s).filter(s1 -> s1.length() >= MIN_LENGTH).toArray(String[]::new);

        for (String token : tokens) {
            int index = cut.getIndex(token);
            assertThat(extract.get(index)).isEqualTo(1.0);
            assertThat(cut.getToken(index)).isEqualToIgnoringCase(token);
        }

    }

}