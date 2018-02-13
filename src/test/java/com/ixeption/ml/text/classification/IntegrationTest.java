package com.ixeption.ml.text.classification;

import com.ixeption.ml.text.classification.binary.svm.BinaryTextClassifierTrainer;
import com.ixeption.ml.text.classification.features.FeatureUtils;
import com.ixeption.ml.text.classification.features.TextFeature;
import com.ixeption.ml.text.classification.pipeline.impl.DefaultTextPipeline;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class IntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(IntegrationTest.class);

    public BinaryTextClassifierTrainer binaryTextClassifierTrainer = new BinaryTextClassifierTrainer(0.5, 0.5, new DefaultTextPipeline());

    @Test
    @Disabled
    public void testSentimentAnalysis() {
        // https://www.kaggle.com/c/si650winter11/data
        Data data = new Data("data/training.txt");
        ArrayList<TextFeature> textFeatures = data.getTextFeatures();
        ArrayList<Integer> labels = data.getLabels();
        FeatureUtils.shuffle(textFeatures, labels);

        // cross validate
        BinaryTextClassifierTrainer.ConfusionMatrixMeasure confusionMatrixMeasure = binaryTextClassifierTrainer.crossValidate(textFeatures.toArray(new TextFeature[0]), labels.stream().mapToInt(Integer::intValue).toArray());
        log.info(" confusion matrix\n" + confusionMatrixMeasure);

        // train and validate
        int trainSize = 6000;
        TextFeature[] trainX = textFeatures.stream().limit(trainSize).toArray(TextFeature[]::new);
        int[] trainY = labels.stream().limit(trainSize).mapToInt(Integer::intValue).toArray();

        TextFeature[] testX = textFeatures.stream().skip(trainSize).toArray(TextFeature[]::new);
        int[] testY = labels.stream().skip(trainSize).mapToInt(Integer::intValue).toArray();

        TextClassifier classifier = binaryTextClassifierTrainer.train(trainX, trainY);
        int[] predictions = Arrays.stream(testX).map(classifier::predict).mapToInt(Prediction::getLabel).toArray();


        int correct = 0;
        for (int i = 0; i < testY.length; i++) {
            if (predictions[i] == testY[i]) {
                correct++;
            }

        }

        log.info("Accuracy: " + correct / (double) testY.length);


    }

    private class Data {
        private ArrayList<TextFeature> textFeatures;
        private ArrayList<Integer> labels;

        Data(String file) {
            read(file);
        }

        ArrayList<TextFeature> getTextFeatures() {
            return textFeatures;
        }

        ArrayList<Integer> getLabels() {
            return labels;
        }

        void read(String file) {
            String line = "";
            String cvsSplitBy = "\t";
            textFeatures = new ArrayList<>();
            labels = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                while ((line = br.readLine()) != null) {
                    // use comma as separator
                    String[] labelText = line.split(cvsSplitBy);
                    TextFeature textFeature = new TextFeature(labelText[1]);
                    textFeatures.add(textFeature);
                    labels.add(Integer.parseInt(labelText[0]));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
