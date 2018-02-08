package com.ixeption.ml.text.classification;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BinaryTextClassifierTest {

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
        BinaryTextClassifier binaryTextClassifier = new BinaryTextClassifier(0.5, 0.5);
        TrainedBinaryTextClassifier trained = binaryTextClassifier.train(testFeatures, testLabels);
        assertThat(trained.predict(testFeatures[0])).isEqualTo(0);
        assertThat(trained.predict(testFeatures[1])).isEqualTo(1);
    }

}