package com.ixeption.ml.text.classification;

import com.ixeption.ml.text.classification.pipeline.TextProcessingPipeline;
import com.ixeption.ml.text.classification.pipeline.impl.DefaultTextPipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smile.classification.SVM;
import smile.math.SparseArray;
import smile.math.kernel.SparseLinearKernel;
import smile.validation.*;

import java.util.Arrays;

public class BinaryTextClassifier {

    private static final Logger log = LoggerFactory.getLogger(BinaryTextClassifier.class);

    private TextProcessingPipeline textProcessingPipeline;
    private double softmarginPenaltyPositive;
    private double softmarginPenaltyNegative;
    private SVM<SparseArray> sparseArraySVM;

    public BinaryTextClassifier(double softmarginPenaltyPositive, double softmarginPenaltyNegative) {
        textProcessingPipeline = new DefaultTextPipeline();
        this.softmarginPenaltyNegative = softmarginPenaltyNegative;
        this.softmarginPenaltyPositive = softmarginPenaltyPositive;

    }


    public TrainedBinaryTextClassifier train(TextFeature[] features, int[] labels) {
        SparseArray[] sparseArrays = Arrays.stream(features)//
                .map(this::transform)//
                .toArray(SparseArray[]::new);

        sparseArraySVM = new SVM<>(new SparseLinearKernel(), softmarginPenaltyPositive, softmarginPenaltyNegative);
        sparseArraySVM.learn(sparseArrays, labels);
        sparseArraySVM.finish();
        sparseArraySVM.trainPlattScaling(sparseArrays, labels);
        return new TrainedBinaryTextClassifier(this);
    }

    private SparseArray transform(TextFeature textFeature) {
        return textProcessingPipeline.process(textFeature);
    }

    int predict(TextFeature textFeature) {
        if (sparseArraySVM == null) {
            // should not be called by users
            throw new RuntimeException("Model was not trained");
        }
        return sparseArraySVM.predict(transform(textFeature));
    }

    public ConfusionMatrixMeasure crossValidate(TextFeature[] features, int[] labels) {
        SVM.Trainer<SparseArray> svmTrainer = new SVM.Trainer<>(new SparseLinearKernel(), softmarginPenaltyPositive,
                softmarginPenaltyNegative);
        ConfusionMatrixMeasure confusionMatrix = new ConfusionMatrixMeasure();
        SparseArray[] sparseArrays = Arrays.stream(features)//
                .map(this::transform)//
                .toArray(SparseArray[]::new);
        double[] measures = Validation.cv(10, svmTrainer, sparseArrays, labels,
                new ClassificationMeasure[]{new Accuracy(), new Sensitivity(), new Precision(), confusionMatrix});

        log.info("...cross validation finished");
        log.info(String.format(" accuracy: %.3f", measures[0]));
        log.info(String.format(" recall (true positives/positives): %.3f", measures[1]));
        log.info(String.format(" precision (true positives/reported positives): %.3f", measures[2]));
        return confusionMatrix;
    }

    public static class ConfusionMatrixMeasure implements ClassificationMeasure {

        int[][] m = new int[2][2];


        @Override
        public double measure(int[] truth, int[] prediction) {
            for (int i = 0; i < truth.length; i++) {
                m[truth[i]][prediction[i]]++;
            }
            return 0;
        }

        @Override
        public String toString() {
            return m[0][0] + "\t" + m[0][1] + "\n" + m[1][0] + "\t" + m[1][1];
        }
    }

}
