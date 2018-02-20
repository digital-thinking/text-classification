package com.ixeption.ml.text.classification.features.impl;

import com.google.common.collect.Lists;
import com.ixeption.ml.text.classification.features.WordIndexing;
import org.assertj.core.util.Sets;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import smile.math.SparseArray;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(value = Parameterized.class)
public class AbstractBagOfWordsFeatureExtractorTest {


    private static final int N_GRAMS = 2;
    private static final int MIN_LENGTH = 3;
    @Parameterized.Parameter
    public AbstractBagOfWordsFeatureExtractor cut;

    @Parameters(name = "{index}: Impl Class: {0}")
    public static Collection<AbstractBagOfWordsFeatureExtractor> impls() {
        Set<String> corpus = Sets.newLinkedHashSet();
        corpus.add("Hello, my name is Jimmy Pop");
        corpus.add("And I'm a dumb white guy");
        corpus.add("I'm not old or new but middle school");
        corpus.add("Fifth grade like junior high");
        corpus.add("I don't know mofo if y'all peeps");
        corpus.add("Be buggin' give props to my ho 'cause she fly");
        corpus.add("But I can take the heat 'cause I'm the other white meat");
        corpus.add("Known as 'Kid Funky Fried'");
        return Lists.newArrayList(new BagOfWordsFeatureExtractor(N_GRAMS, MIN_LENGTH, corpus), new HashTrickBagOfWordsFeatureExtractor(N_GRAMS, MIN_LENGTH, 1337));
    }

    @Test
    public void testBagOfWords() throws WordIndexing.IndexerException {
        String s = "known my buggin fifth grade";
        SparseArray extract = cut.extract(s);
        String[] tokens = Arrays.stream(s.split(" ")).filter(s1 -> s1.length() >= MIN_LENGTH).toArray(String[]::new);

        int i = 0;
        for (String token : tokens) {
            int index = cut.getIndex(token);
            assertThat(extract.get(index)).isEqualTo(1.0);
            assertThat(cut.getToken(index).equalsIgnoreCase(token));
        }


    }


}