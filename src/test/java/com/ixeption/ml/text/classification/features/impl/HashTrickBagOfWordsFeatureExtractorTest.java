package com.ixeption.ml.text.classification.features.impl;

import com.ixeption.ml.text.classification.features.WordIndexing;
import org.assertj.core.util.Sets;
import org.junit.Before;
import org.junit.Test;
import smile.math.SparseArray;

import java.util.Arrays;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class HashTrickBagOfWordsFeatureExtractorTest {

    public static final int MIN_LENGHT = 3;
    HashTrickBagOfWordsFeatureExtractor bagOfWordsFeatureExtractor;

    @Before
    public void setUp() {
        Set<String> corpus = Sets.newLinkedHashSet();
        corpus.add("Hello, my name is Jimmy Pop");
        corpus.add("And I'm a dumb white guy");
        corpus.add("I'm not old or new but middle school");
        corpus.add("Fifth grade like junior high");
        corpus.add("I don't know mofo if y'all peeps");
        corpus.add("Be buggin' give props to my ho 'cause she fly");
        corpus.add("But I can take the heat 'cause I'm the other white meat");
        corpus.add("Known as 'Kid Funky Fried'");

        bagOfWordsFeatureExtractor = new HashTrickBagOfWordsFeatureExtractor(2, MIN_LENGHT, 133);
    }

    @Test
    public void testIndexExists() throws WordIndexing.IndexerException {
        assertThat(bagOfWordsFeatureExtractor.getIndex("Hello")).isEqualTo(1852943150);
        assertThat(bagOfWordsFeatureExtractor.getIndex("Fried")).isEqualTo(-1687772401);
    }

    @Test
    public void testBagOfWords() throws WordIndexing.IndexerException {
        String s = "Known my buggin Fifth grade";
        SparseArray extract = bagOfWordsFeatureExtractor.extract(s);
        String[] tokens = Arrays.stream(s.split(" ")).filter(s1 -> s1.length() >= MIN_LENGHT).toArray(String[]::new);

        int i = 0;
        for (String token : tokens) {
            int index = bagOfWordsFeatureExtractor.getIndex(token);
            assertThat(extract.get(index)).isEqualTo(1.0);
            assertThat(bagOfWordsFeatureExtractor.getToken(index).equalsIgnoreCase(token));

        }

    }


}