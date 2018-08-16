package de.ixeption.classify.binary.svm;

import de.ixeption.classify.PersistenceUtils;
import de.ixeption.classify.Prediction;
import de.ixeption.classify.binary.BinaryUtils;
import de.ixeption.classify.features.TextFeature;
import de.ixeption.classify.pipeline.TextProcessingPipeline;
import de.ixeption.classify.pipeline.impl.DefaultTextPipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smile.classification.SVM;
import smile.math.SparseArray;
import smile.math.kernel.SparseLinearKernel;
import smile.validation.*;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

@NotThreadSafe
public class BinaryTextClassifierTrainer {

    private static final Logger log = LoggerFactory.getLogger(BinaryTextClassifierTrainer.class);

    private final TextProcessingPipeline textProcessingPipeline;
    private final double softmarginPenaltyPositive;
    private final double softmarginPenaltyNegative;
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

    /**
     * train the classifier
     *
     * @param features the features
     * @param labels   the labels
     * @param batchSize null or batch size
     * @return a trained classifier
     */
    public TrainedBinaryTextClassifier train(TextFeature[] features, int[] labels, Integer batchSize) {
        log.info("Starting training");
        sparseArraySVM = new SVM<>(new SparseLinearKernel(), softmarginPenaltyPositive, softmarginPenaltyNegative);
        trainInternal(sparseArraySVM, features, labels, batchSize == null ? features.length : batchSize);
        log.info("Training finished");
        return new TrainedBinaryTextClassifier(this);
    }


    private void trainInternal(SVM<SparseArray> sparseArraySVM, TextFeature[] features, int[] labels, int batchSize) {
        SparseArray[] sparseArrays = Arrays.stream(features)//
                .map(this::transform)//
                .toArray(SparseArray[]::new);

        int rest = sparseArrays.length % batchSize;
        int chunks = sparseArrays.length / batchSize + (rest > 0 ? 1 : 0);
        log.info("Batched Training batchSize: " + batchSize + " batches: " + chunks);
        for (int i = 0; i < (rest > 0 ? chunks - 1 : chunks); i++) {
            SparseArray[] featureBatch = Arrays.copyOfRange(sparseArrays, i * batchSize, i * batchSize + batchSize);
            int[] lablelBatch = Arrays.copyOfRange(labels, i * batchSize, i * batchSize + batchSize);
            sparseArraySVM.learn(featureBatch, lablelBatch);
            sparseArraySVM.finish();
            log.info("Finished: " + (i + 1) + "/" + chunks);
        }
        if (rest > 0) {
            SparseArray[] featureBatch = Arrays.copyOfRange(sparseArrays, (chunks - 1) * batchSize, (chunks - 1) * batchSize + rest);
            int[] lablelBatch = Arrays.copyOfRange(labels, (chunks - 1) * batchSize, (chunks - 1) * batchSize + rest);
            sparseArraySVM.learn(featureBatch, lablelBatch);
            sparseArraySVM.finish();
            log.info("Finished: " + chunks + "/" + chunks);
        }

        sparseArraySVM.trainPlattScaling(sparseArrays, labels);

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

    public BinaryUtils.BinaryConfusionMatrixMeasure crossValidate(TextFeature[] features, int[] labels) {
        log.info("Starting cross validation");
        SVM.Trainer<SparseArray> svmTrainer = new SVM.Trainer<>(new SparseLinearKernel(), softmarginPenaltyPositive,
                softmarginPenaltyNegative);
        BinaryUtils.BinaryConfusionMatrixMeasure confusionMatrix = new BinaryUtils.BinaryConfusionMatrixMeasure();
        SparseArray[] sparseArrays = Arrays.stream(features)//
                .map(this::transform)//
                .toArray(SparseArray[]::new);
        double[] measures = Validation.cv(10, svmTrainer, sparseArrays, labels,
                new ClassificationMeasure[]{new Accuracy(), new Sensitivity(), new Precision(), confusionMatrix});

        log.info("...cross validation finished");
        log.info("accuracy: {}", measures[0]);
        log.info("recall: {}", measures[1]);
        log.info("precision: {}", measures[2]);
        return confusionMatrix;
    }

    public void saveToFile(Path file) throws IOException {
        log.info("Saving model to file: {}", file);
        if (this.sparseArraySVM == null) {
            throw new RuntimeException("cannot save until model is trained");
        }
        PersistenceUtils.serialize(this.sparseArraySVM, file);
    }


}
