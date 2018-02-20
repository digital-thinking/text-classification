package com.ixeption.ml.text.classification.features.impl;

import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.ixeption.ml.text.classification.features.FeatureUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smile.math.SparseArray;

import java.util.Collection;

public class HashTrickBagOfWordsFeatureExtractor extends AbstractBagOfWordsFeatureExtractor {


    private static final Logger log = LoggerFactory.getLogger(HashTrickBagOfWordsFeatureExtractor.class);

    private final int seed;
    private transient Multimap<Integer, String> _indexToName = HashMultimap.create(100000, 1);


    public HashTrickBagOfWordsFeatureExtractor(int nGrams, int tokenMinLength, int seed) {
        super(nGrams, tokenMinLength);
        this.seed = seed;
    }


    @Override
    protected int getIndexInternal(String s) {
        int index = FeatureUtils.hash32(s.getBytes(), s.length(), seed);
        if (_indexToName.containsKey(index)) {
            Collection<String> existing = _indexToName.get(index);
            if (!existing.contains(s)) {
                log.warn("Hash collision: " + index + ":  " + s + " : " + existing);
                existing.add(s);
            }
        } else {
            _indexToName.put(index, s);
        }

        return index;
    }

    @Override
    protected String getTokenInternal(int index) throws IndexerException {
        if (_indexToName.containsKey(index)) {
            return Joiner.on(',').join(_indexToName.get(index));
        }
        throw new IndexerException("Invalid index " + index);
    }

    @Override
    protected SparseArray scale(SparseArray features) {
        return features;
    }

}
