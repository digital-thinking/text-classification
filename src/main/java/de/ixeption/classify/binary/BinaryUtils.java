package de.ixeption.classify.binary;

import smile.validation.ClassificationMeasure;

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

}
