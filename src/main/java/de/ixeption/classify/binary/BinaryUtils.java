package de.ixeption.classify.binary;

import smile.validation.ClassificationMeasure;
import smile.validation.ConfusionMatrix;

public class BinaryUtils {

    private BinaryUtils() {
    }


    public static double getMatthewsCorrelation(int[][] m) {
        double tp = m[0][0];
        double tn = m[1][1];
        double fp = m[0][1];
        double fn = m[1][0];

        double numerator = tp * tn - fp * fn;
        double denominator = Math.sqrt((tp + fp) * (tp + fn) * (tn + fp) * (tn + fn));
        return numerator / denominator;
    }

    public static class BinaryConfusionMatrixMeasure implements ClassificationMeasure {

        int[][] m = new int[2][2];

        public int[][] getMatrix() {
            return m.clone();
        }

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

    public static class MatthewsCorrelationCoefficient implements ClassificationMeasure {

        public double measure(int[] truth, int[] prediction) {
            if (truth.length != prediction.length) {
                throw new IllegalArgumentException(String.format("The vector sizes don't match: %d != %d.", truth.length, prediction.length));
            }
            ConfusionMatrix confusionMatrix = new ConfusionMatrix(truth, prediction);
            int[][] matrix = confusionMatrix.getMatrix();

            if (matrix.length != 2 || matrix[0].length != 2) {
                throw new IllegalArgumentException("MCC can only be applied to binary classification: " + confusionMatrix.toString());
            }

            int tp = matrix[0][0];
            int tn = matrix[1][1];
            int fp = matrix[0][1];
            int fn = matrix[1][0];

            int nominator = (tp * tn - fp * fn);
            double denominator = Math.sqrt((tp + fp) * (tp + fn) * (tn + fp) * (tn + fn));

            if (nominator == 0 || denominator == 0) return 0;

            return nominator / denominator;

        }
    }

}
