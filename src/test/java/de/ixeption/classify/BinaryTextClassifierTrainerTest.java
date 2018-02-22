package de.ixeption.classify;

import de.ixeption.classify.binary.svm.BinaryTextClassifierTrainer;
import de.ixeption.classify.binary.svm.DeserializedSVMTextClassifier;
import de.ixeption.classify.binary.svm.TrainedBinaryTextClassifier;
import de.ixeption.classify.features.TextFeature;
import de.ixeption.classify.features.impl.HashTrickBagOfWordsFeatureExtractor;
import de.ixeption.classify.pipeline.impl.DefaultTextPipeline;
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
        TextFeature textFeatureA = new TextFeature("class_a");
        TextFeature textFeatureB = new TextFeature("class_b");
        testFeatures = new TextFeature[]{textFeatureA, textFeatureB};
        testLabels = new int[]{0, 1};
        binaryTextClassifierTrainer = new BinaryTextClassifierTrainer(0.5, 0.5,
                new DefaultTextPipeline(new HashTrickBagOfWordsFeatureExtractor(1337, 2, 2)));
    }

    @Test
    public void testTraining() {
        initTrainingData();

        TrainedBinaryTextClassifier trained = binaryTextClassifierTrainer.train(testFeatures, testLabels);
        assertThat(trained.predict(testFeatures[0]).getLabel()).isEqualTo(0);
        assertThat(trained.predict(testFeatures[1]).getLabel()).isEqualTo(1);
    }

    @Test
    public void testPersistence() throws IOException, ClassNotFoundException {
        initTrainingData();

        binaryTextClassifierTrainer.train(testFeatures, testLabels);
        Path path = Files.createTempFile("binary-svm", ".model");
        binaryTextClassifierTrainer.saveToFile(path);


        TextClassifier persisted = new DeserializedSVMTextClassifier(new DefaultTextPipeline(
                new HashTrickBagOfWordsFeatureExtractor(1337, 2, 2)), path);
        assertThat(persisted.predict(testFeatures[0]).getLabel()).isEqualTo(0);
        assertThat(persisted.predict(testFeatures[1]).getLabel()).isEqualTo(1);


    }

}