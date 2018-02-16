package com.ixeption.ml.text.classification.features.impl;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;

import java.util.Set;

public class BagOfWordsFeatureExtractor extends AbstractBagOfWordsFeatureExtractor {


    private final BiMap<Integer, String> _indexToName = Maps.synchronizedBiMap(HashBiMap.create());
    private final BiMap<String, Integer> _nameToIndex;


    public BagOfWordsFeatureExtractor(int nGrams, int tokenMinLength, Set<String> corpus) {
        super(nGrams, tokenMinLength);
        buildCorpus(corpus);
        _nameToIndex = _indexToName.inverse();
    }

    private void buildCorpus(Set<String> corpus) {
        int currentIndex = 0;
        for (String next : corpus) {
            for (String s : getTokens(next)) {
                if (!_indexToName.containsValue(s)) {
                    _indexToName.put(currentIndex++, s);
                }
            }

        }

    }

    @Override
    public int getIndexInternal(String s) throws IndexerException {
        if (_nameToIndex.containsKey(s)) {
            return _nameToIndex.get(s);
        }
        throw new IndexerException("Invalid token " + s);
    }

    @Override
    public String getTokenInternal(int index) throws IndexerException {
        if (_indexToName.containsKey(index)) {
            return _indexToName.get(index);
        }
        throw new IndexerException("Invalid index " + index);
    }


}
