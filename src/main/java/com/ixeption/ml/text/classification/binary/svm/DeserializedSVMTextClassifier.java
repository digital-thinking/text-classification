package com.ixeption.ml.text.classification.binary.svm;

import com.ixeption.ml.text.classification.PersistenceUtils;
import com.ixeption.ml.text.classification.Prediction;
import com.ixeption.ml.text.classification.TextClassifier;
import com.ixeption.ml.text.classification.features.TextFeature;
import com.ixeption.ml.text.classification.pipeline.TextProcessingPipeline;
import smile.classification.SVM;
import smile.math.SparseArray;

import java.io.IOException;
import java.nio.file.Path;

/**
 * This is a representation of a deserialized binary SVM model
 * Be aware, that no checks are executed to make sure the loaded model fits to the attached processing pipeline
 * In case of using a dictionary, make sure to make in available in the pipeline
 */
public class DeserializedSVMTextClassifier implements TextClassifier {

    private final TextProcessingPipeline textProcessingPipeline;
    private final SVM<SparseArray> sparseArraySVM;

    public DeserializedSVMTextClassifier(TextProcessingPipeline textPreprocessor, Path file) throws IOException, ClassNotFoundException {
        this.textProcessingPipeline = textPreprocessor;
        sparseArraySVM = PersistenceUtils.deserialize(file);
    }

    @Override
    public Prediction predict(TextFeature textFeature) {
        double[] posterior = new double[2];
        int predict = sparseArraySVM.predict(textProcessingPipeline.process(textFeature), posterior);
        return new Prediction(predict, posterior);
    }
}
