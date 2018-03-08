package de.ixeption.classify.binary;

import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BinaryUtilsTest {

    @Test
    public void getMatthewsCorrelationAllTrue() {
        int[][] matrix = new int[][]{
                {24, 0},
                {0, 327}
        };
        assertThat(BinaryUtils.getMatthewsCorrelation(matrix)).isEqualTo(1.0);

    }


    @Test
    public void getMatthewsCorrelationAllWrong() {
        int[][] matrix = new int[][]{
                {0, 124},
                {123, 0}
        };
        assertThat(BinaryUtils.getMatthewsCorrelation(matrix)).isEqualTo(-1.0);

    }

    @Test
    public void getMatthewsCorrelationAllRandom() {
        int[][] matrix = new int[][]{
                {900, 100},
                {90, 10}
        };
        assertThat(BinaryUtils.getMatthewsCorrelation(matrix)).isEqualTo(0);
    }

    @Test
    public void getMatthewsCorrelationAllOk() {
        int[][] matrix = new int[][]{
                {900, 100},
                {10, 90}
        };
        assertThat(BinaryUtils.getMatthewsCorrelation(matrix)).isCloseTo(0.6, Percentage.withPercentage(10));

    }


}