package de.ixeption.classify.features;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class FeatureUtils {
    private FeatureUtils() {
    }

    /**
     * MurmurHash3_x86_32 https://github.com/addthis/stream-lib/blob/master/src/main/java/com/clearspring/analytics/hash/MurmurHash.java
     * used for the hashing trick in NLP
     *
     * @param data   the data to be hashed
     * @param length data length
     * @param seed   the random seed
     * @return the hash
     */
    public static int hash32(final byte[] data, int length, int seed) {
        int m = 0x5bd1e995;
        int r = 24;

        int h = seed ^ length;

        int len_4 = length >> 2;

        for (int i = 0; i < len_4; i++) {
            int i_4 = i << 2;
            int k = data[i_4 + 3];
            k = k << 8;
            k = k | (data[i_4 + 2] & 0xff);
            k = k << 8;
            k = k | (data[i_4 + 1] & 0xff);
            k = k << 8;
            k = k | (data[i_4 + 0] & 0xff);
            k *= m;
            k ^= k >>> r;
            k *= m;
            h *= m;
            h ^= k;
        }

        // avoid calculating modulo
        int len_m = len_4 << 2;
        int left = length - len_m;

        if (left != 0) {
            if (left >= 3) {
                h ^= (int) data[length - 3] << 16;
            }
            if (left >= 2) {
                h ^= (int) data[length - 2] << 8;
            }
            if (left >= 1) {
                h ^= (int) data[length - 1];
            }

            h *= m;
        }

        h ^= h >>> 13;
        h *= m;
        h ^= h >>> 15;

        return h;
    }

    //https://en.wikipedia.org/wiki/Fisher%E2%80%93Yates_shuffle
    public static void shuffle(List<TextFeature> features, List<Integer> labels) {
        Random rnd = ThreadLocalRandom.current();
        for (int i = features.size() - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);

            TextFeature a = features.get(index);
            features.set(index, features.get(i));
            features.set(i, a);

            Integer b = labels.get(index);
            labels.set(index, labels.get(i));
            labels.set(i, b);

        }
    }
}
