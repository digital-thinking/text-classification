package com.ixeption.ml.text.classification;

import com.ixeption.ml.text.classification.binary.svm.BinaryTextClassifierTrainer;
import com.ixeption.ml.text.classification.binary.svm.DeserializedSVMTextClassifier;
import com.ixeption.ml.text.classification.binary.svm.TrainedBinaryTextClassifier;
import com.ixeption.ml.text.classification.features.TextFeature;
import com.ixeption.ml.text.classification.pipeline.impl.DefaultTextPipeline;
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
        binaryTextClassifierTrainer = new BinaryTextClassifierTrainer(0.5, 0.5, new DefaultTextPipeline());
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


        TextClassifier persisted = new DeserializedSVMTextClassifier(new DefaultTextPipeline(), path);
        assertThat(persisted.predict(testFeatures[0]).getLabel()).isEqualTo(0);
        assertThat(persisted.predict(testFeatures[1]).getLabel()).isEqualTo(1);


    }

}