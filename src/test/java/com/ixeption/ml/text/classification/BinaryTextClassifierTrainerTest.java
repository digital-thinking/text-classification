package com.ixeption.ml.text.classification;

import com.ixeption.ml.text.classification.binary.svm.BinaryTextClassifierTrainer;
import com.ixeption.ml.text.classification.binary.svm.TrainedBinaryTextClassifier;
import com.ixeption.ml.text.classification.features.TextFeature;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class BinaryTextClassifierTrainerTest {

    TextFeature[] testFeatures;
    int[] testLabels;

    private void initTrainingData() {
        TextFeature textFeatureA = new TextFeature("class_a");
        TextFeature textFeatureB = new TextFeature("class_b");
        testFeatures = new TextFeature[]{textFeatureA, textFeatureB};
        testLabels = new int[]{0, 1};
    }

    @Test
    public void testTraining() {
        initTrainingData();
        BinaryTextClassifierTrainer binaryTextClassifierTrainer = new BinaryTextClassifierTrainer(0.5, 0.5);
        TrainedBinaryTextClassifier trained = binaryTextClassifierTrainer.train(testFeatures, testLabels);
        assertThat(trained.predict(testFeatures[0])).isEqualTo(0);
        assertThat(trained.predict(testFeatures[1])).isEqualTo(1);
    }

    @Test
    public void testPersistence() throws IOException, ClassNotFoundException {
        initTrainingData();
        BinaryTextClassifierTrainer binaryTextClassifierTrainer = new BinaryTextClassifierTrainer(0.5, 0.5);
        binaryTextClassifierTrainer.train(testFeatures, testLabels);
        Path path = Files.createTempFile("binary-svm", ".model");
        binaryTextClassifierTrainer.saveToFile(path);

        binaryTextClassifierTrainer = new BinaryTextClassifierTrainer(0.5, 0.5);
        TrainedBinaryTextClassifier persisted = binaryTextClassifierTrainer.readFromFile(path);
        assertThat(persisted.predict(testFeatures[0])).isEqualTo(0);
        assertThat(persisted.predict(testFeatures[1])).isEqualTo(1);


    }

}