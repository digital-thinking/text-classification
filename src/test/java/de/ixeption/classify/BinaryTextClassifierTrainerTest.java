package de.ixeption.classify;

import de.ixeption.classify.binary.svm.BinaryTextClassifierTrainer;
import de.ixeption.classify.binary.svm.DeserializedSVMTextClassifier;
import de.ixeption.classify.binary.svm.TrainedBinaryTextClassifier;
import de.ixeption.classify.features.TextFeature;
import de.ixeption.classify.features.impl.HashTrickBagOfWordsFeatureExtractor;
import de.ixeption.classify.pipeline.impl.DefaultTextPipeline;
import de.ixeption.classify.postprocessing.impl.StemmingNGrammProcessor;
import de.ixeption.classify.tokenization.impl.NormalizingTextTokenizer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;


class BinaryTextClassifierTrainerTest {

    TextFeature[] testFeatures;
    int[] testLabels;
    private BinaryTextClassifierTrainer binaryTextClassifierTrainer;

    private void initTrainingData() {
        TextFeature textFeatureA = new TextFeature("classA");
        TextFeature textFeatureB = new TextFeature("classB");
        testFeatures = new TextFeature[]{textFeatureA, textFeatureB};
        testLabels = new int[]{0, 1};
        DefaultTextPipeline pipeline = new DefaultTextPipeline(new HashTrickBagOfWordsFeatureExtractor(1337), new NormalizingTextTokenizer());
        pipeline.getTokenProcessors().add(new StemmingNGrammProcessor(2, 2, 25));
        binaryTextClassifierTrainer = new BinaryTextClassifierTrainer(0.5, 0.5, pipeline);

    }

    @Test
    public void testTraining() {
        initTrainingData();

        TrainedBinaryTextClassifier trained = binaryTextClassifierTrainer.train(testFeatures, testLabels, null);
        assertThat(trained.predict(testFeatures[0]).getLabel()).isEqualTo(0);
        assertThat(trained.predict(testFeatures[1]).getLabel()).isEqualTo(1);
    }

    @Test
    public void testPersistence() throws IOException, ClassNotFoundException {
        initTrainingData();

        binaryTextClassifierTrainer.train(testFeatures, testLabels, null);
        Path path = Files.createTempFile("binary-svm", ".model");
        binaryTextClassifierTrainer.saveToFile(path);

        DefaultTextPipeline pipeline = new DefaultTextPipeline(new HashTrickBagOfWordsFeatureExtractor(1337), new NormalizingTextTokenizer());
        pipeline.getTokenProcessors().add(new StemmingNGrammProcessor(2, 2, 25));

        TextClassifier persisted = new DeserializedSVMTextClassifier(
                pipeline, path);
        assertThat(persisted.predict(testFeatures[0]).getLabel()).isEqualTo(0);
        assertThat(persisted.predict(testFeatures[1]).getLabel()).isEqualTo(1);

    }

}