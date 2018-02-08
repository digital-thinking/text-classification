package com.ixeption.ml.text.classification;

import com.ixeption.ml.text.classification.pipeline.TextProcessingPipeline;
import com.ixeption.ml.text.classification.pipeline.impl.DefaultTextPipeline;
import smile.classification.SVM;
import smile.math.SparseArray;
import smile.math.kernel.SparseLinearKernel;

import java.util.Arrays;

public class BinaryTextClassifier {

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
}
