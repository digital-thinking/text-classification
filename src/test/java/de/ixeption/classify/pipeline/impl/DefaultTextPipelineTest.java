package de.ixeption.classify.pipeline.impl;

import com.google.common.collect.Sets;
import de.ixeption.classify.features.TextFeature;
import de.ixeption.classify.features.WordIndexing;
import de.ixeption.classify.features.impl.BagOfWordsFeatureExtractor;
import de.ixeption.classify.features.impl.HashTrickBagOfWordsFeatureExtractor;
import de.ixeption.classify.postprocessing.impl.StemmingNGrammProcessor;
import de.ixeption.classify.tokenization.impl.NormalizingTextTokenizer;
import org.junit.jupiter.api.Test;
import smile.math.SparseArray;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;


class DefaultTextPipelineTest {

    public static final int N_GRAMS = 2;

    @Test
    void testFeatureIndex() throws WordIndexing.IndexerException {
        DefaultTextPipeline cut = new DefaultTextPipeline(new HashTrickBagOfWordsFeatureExtractor(1337), new NormalizingTextTokenizer());
        cut.getTokenProcessors().add(new StemmingNGrammProcessor(N_GRAMS, 2, 25));

        cut.process(new TextFeature("Hallo this is a test"));
        int index = cut.getIndex("hallo");
        assertThat(cut.getToken(index)).isEqualTo("hallo");
    }

    @Test
    public void testPersistence() throws IOException, ClassNotFoundException {
        BagOfWordsFeatureExtractor featureExtractor = new BagOfWordsFeatureExtractor(Sets.newHashSet("Zero", "Two", "Three"));
        TextFeature textFeature = new TextFeature("zero Three");

        DefaultTextPipeline cut = new DefaultTextPipeline(featureExtractor, new NormalizingTextTokenizer());
        cut.getTokenProcessors().add(new StemmingNGrammProcessor(N_GRAMS, 2, 25));
        SparseArray sparseArray = cut.process(textFeature);

        Path path = Files.createTempFile("bow", ".dict");
        featureExtractor.saveDictionary(path);

        BagOfWordsFeatureExtractor deserialize = BagOfWordsFeatureExtractor.deserialize(path);
        DefaultTextPipeline cut2 = new DefaultTextPipeline(deserialize, new NormalizingTextTokenizer());
        cut2.getTokenProcessors().add(new StemmingNGrammProcessor(N_GRAMS, 2, 25));
        SparseArray sparseArray2 = cut2.process(textFeature);
        assertThat(sparseArray2.size()).isEqualTo(sparseArray.size());
        for (SparseArray.Entry entry : sparseArray) {
            assertThat(sparseArray2.get(entry.i)).isEqualTo(entry.x);
        }
    }
}