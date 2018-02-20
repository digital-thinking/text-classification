package com.ixeption.ml.text.classification.features.impl;

import com.ixeption.ml.text.classification.features.WordIndexing;
import org.assertj.core.util.Sets;
import org.junit.Before;
import org.junit.Test;

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
        assertThat(bagOfWordsFeatureExtractor.getIndex(bagOfWordsFeatureExtractor.extractTokens("hello")[0])).isEqualTo(1852943150);
        assertThat(bagOfWordsFeatureExtractor.getIndex(bagOfWordsFeatureExtractor.extractTokens("fried")[0])).isEqualTo(-1687772401);
        // ngram 2 words -> 3rd element
        assertThat(bagOfWordsFeatureExtractor.getIndex(bagOfWordsFeatureExtractor.extractTokens("kid funky")[2])).isEqualTo(664005995);
    }

    @Test
    public void testCollision() throws WordIndexing.IndexerException {
        assertThat(bagOfWordsFeatureExtractor.getIndex("disponibilité")).isEqualTo(bagOfWordsFeatureExtractor.getIndex("disponibilità"));
        System.out.println(bagOfWordsFeatureExtractor.getToken(-616469338));
    }

}