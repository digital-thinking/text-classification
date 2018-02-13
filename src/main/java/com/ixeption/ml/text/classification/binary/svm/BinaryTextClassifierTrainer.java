package com.ixeption.ml.text.classification.binary.svm;

import com.ixeption.ml.text.classification.PersistenceUtils;
import com.ixeption.ml.text.classification.Prediction;
import com.ixeption.ml.text.classification.features.TextFeature;
import com.ixeption.ml.text.classification.pipeline.TextProcessingPipeline;
import com.ixeption.ml.text.classification.pipeline.impl.DefaultTextPipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smile.classification.SVM;
import smile.math.SparseArray;
import smile.math.kernel.SparseLinearKernel;
import smile.validation.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

public class BinaryTextClassifierTrainer {

    private static final Logger log = LoggerFactory.getLogger(BinaryTextClassifierTrainer.class);

    private TextProcessingPipeline textProcessingPipeline;
    private double softmarginPenaltyPositive;
    private double softmarginPenaltyNegative;
    private SVM<SparseArray> sparseArraySVM;

    /**
     * creates a trainer, with a {@link DefaultTextPipeline}
     *
     * @param softmarginPenaltyPositive the soft margin penalty parameter for positive instances.
     * @param softmarginPenaltyNegative the soft margin penalty parameter for negative instances.
     * @param textProcessingPipeline    the pre processing {@link TextProcessingPipeline}
     */
    public BinaryTextClassifierTrainer(double softmarginPenaltyPositive, double softmarginPenaltyNegative, TextProcessingPipeline textProcessingPipeline) {
        this.textProcessingPipeline = textProcessingPipeline;
        this.softmarginPenaltyNegative = softmarginPenaltyNegative;
        this.softmarginPenaltyPositive = softmarginPenaltyPositive;

    }

    public TrainedBinaryTextClassifier train(TextFeature[] features, int[] labels) {
        SparseArray[] sparseArrays = Arrays.stream(features)//
                .map(this::transform)//
                .toArray(SparseArray[]::new);

        log.info("Starting training");
        sparseArraySVM = new SVM<>(new SparseLinearKernel(), softmarginPenaltyPositive, softmarginPenaltyNegative);
        sparseArraySVM.learn(sparseArrays, labels);
        sparseArraySVM.finish();
        sparseArraySVM.trainPlattScaling(sparseArrays, labels);
        log.info("Training finished");
        return new TrainedBinaryTextClassifier(this);
    }

    private SparseArray transform(TextFeature textFeature) {
        return textProcessingPipeline.process(textFeature);
    }

    Prediction predict(TextFeature textFeature) {
        if (sparseArraySVM == null) {
            // should not be called by users
            throw new RuntimeException("Model was not trained");
        }
        double[] posterior = new double[2];
        int predict = sparseArraySVM.predict(transform(textFeature), posterior);
        return new Prediction(predict, posterior);
    }

    public ConfusionMatrixMeasure crossValidate(TextFeature[] features, int[] labels) {
        log.info("Starting cross validation");
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

    public void saveToFile(Path file) throws IOException {
        log.info("Saving model to file " + file);
        if (this.sparseArraySVM == null) {
            throw new RuntimeException("cannot save until model is trained");
        }
        PersistenceUtils.serialize(this.sparseArraySVM, file);
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
