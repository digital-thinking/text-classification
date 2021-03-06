package de.ixeption.classify;

import de.ixeption.classify.binary.BinaryUtils;
import de.ixeption.classify.binary.svm.BinaryTextClassifierTrainer;
import de.ixeption.classify.features.FeatureUtils;
import de.ixeption.classify.features.TextFeature;
import de.ixeption.classify.features.impl.BagOfWordsFeatureExtractor;
import de.ixeption.classify.pipeline.TokenizedText;
import de.ixeption.classify.pipeline.impl.DefaultTextPipeline;
import de.ixeption.classify.postprocessing.impl.DefaultProcessor;
import de.ixeption.classify.tokenization.impl.NormalizingTextTokenizer;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


public class TFIDFBagOfWordsIT {

    private static final Logger log = LoggerFactory.getLogger(TFIDFBagOfWordsIT.class);

    @Test
    public void testSentimentAnalysis() {
        // https://www.kaggle.com/c/si650winter11/data
        TrainingData data = new TrainingData("data/training.txt");
        ArrayList<TextFeature> textFeatures = data.getTextFeatures();
        ArrayList<Integer> labels = data.getLabels();
        FeatureUtils.shuffle(textFeatures, labels);

        NormalizingTextTokenizer normalizingTextTokenizer = new NormalizingTextTokenizer();
        DefaultProcessor filteringProcessor = new DefaultProcessor(3, 3, 25);

        Set<String> corpus = textFeatures.stream().map(normalizingTextTokenizer::tokenize).map(filteringProcessor::process).map(TokenizedText::getTokens).flatMap(Arrays::stream).collect(Collectors.toSet());
        DefaultTextPipeline pipeline = new DefaultTextPipeline(new BagOfWordsFeatureExtractor(corpus), normalizingTextTokenizer);
        pipeline.getTokenProcessors().add(filteringProcessor);

        BinaryTextClassifierTrainer binaryTextClassifierTrainer = new BinaryTextClassifierTrainer(0.5, 0.5, pipeline);

        // cross validate
        BinaryUtils.BinaryConfusionMatrixMeasure confusionMatrixMeasure = binaryTextClassifierTrainer
                .crossValidate(textFeatures.toArray(new TextFeature[0]), labels.stream().mapToInt(Integer::intValue).toArray());
        log.info(" confusion matrix\n" + confusionMatrixMeasure);

        // train and validate
        int trainSize = 6000;
        TextFeature[] trainX = textFeatures.stream().limit(trainSize).toArray(TextFeature[]::new);
        int[] trainY = labels.stream().limit(trainSize).mapToInt(Integer::intValue).toArray();

        TextFeature[] testX = textFeatures.stream().skip(trainSize).toArray(TextFeature[]::new);
        int[] testY = labels.stream().skip(trainSize).mapToInt(Integer::intValue).toArray();

        TextClassifier classifier = binaryTextClassifierTrainer.train(trainX, trainY, null);
        int[] predictions = Arrays.stream(testX).map(classifier::predict).mapToInt(Prediction::getLabel).toArray();

        int correct = 0;
        for (int i = 0; i < testY.length; i++) {
            if (predictions[i] == testY[i]) {
                correct++;
            }

        }

        double accurancy = correct / (double) testY.length;
        log.info("Accuracy: " + accurancy);
        assertThat(accurancy).isCloseTo(0.99, Percentage.withPercentage(5));
    }

}
