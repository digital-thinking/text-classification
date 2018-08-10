package de.ixeption.classify.features.impl;

import de.ixeption.classify.PersistenceUtils;
import de.ixeption.classify.tokenization.Token;
import gnu.trove.map.TIntDoubleMap;
import gnu.trove.map.hash.TIntDoubleHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smile.math.SparseArray;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toMap;


public class BagOfWordsFeatureExtractor extends AbstractBagOfWordsFeatureExtractor {

   private static final Logger log = LoggerFactory.getLogger(BagOfWordsFeatureExtractor.class);

   private Map<Integer, String> _indexToName = new HashMap<>(1000);
   private Map<String, Integer> _nameToIndex = new HashMap<>(1000);
   private TIntDoubleMap _idfMap = new TIntDoubleHashMap(1000);

   public BagOfWordsFeatureExtractor(Set<Token> corpus) {
      buildCorpus(corpus);
   }

   private BagOfWordsFeatureExtractor() {

   }

   public static BagOfWordsFeatureExtractor deserialize(Path dictionary) throws IOException, ClassNotFoundException {
      ArrayList<Object> data = PersistenceUtils.deserialize(dictionary);
      BagOfWordsFeatureExtractor extractor = new BagOfWordsFeatureExtractor();
      extractor._indexToName = (Map<Integer, String>) data.get(0);
      extractor._nameToIndex = extractor._indexToName.entrySet().stream().collect(toMap(Map.Entry::getValue, Map.Entry::getKey));
      extractor._idfMap = (TIntDoubleMap) data.get(1);
      return extractor;
   }

   public void saveDictionary(Path path) throws IOException {
      ArrayList<Object> data = new ArrayList<>(2);
      data.add(_indexToName);
      data.add(_idfMap);
      PersistenceUtils.serialize(data, path);

   }

   private void buildCorpus(Set<Token> corpus) {
      int currentIndex = 0;
      for (Token s : corpus) {
         if (!_indexToName.containsValue(s.getText())) {
            _indexToName.put(currentIndex, s.getText());
            _nameToIndex.put(s.getText(), currentIndex);
            _idfMap.put(currentIndex, 1.0);
            currentIndex++;
         } else {
            _idfMap.adjustValue(_nameToIndex.get(s.getText()), 1.0);
         }

      }
      _idfMap.transformValues(value -> Math.log(corpus.size() / value));
   }

   @Override
   public int getIndexInternal(String s) throws IndexerException {
      if (_nameToIndex.containsKey(s)) {
         return _nameToIndex.get(s);
      }
      throw new IndexerException("Invalid token " + s);
   }

   @Override
   protected SparseArray scale(SparseArray features) {
      for (SparseArray.Entry entry : features) {
         if (entry.x > 1.0) {
            double tf = entry.x / features.size();
            entry.x = tf * _idfMap.get(entry.i);
         }
      }
      return features;
   }

   @Override
   protected SparseArray transform(Token[] tokens) {
      SparseArray features = new SparseArray();
      for (Token token : tokens) {
         try {
            int index = getIndexInternal(token.getText());
            features.set(index, features.get(index) + 1.0);
         } catch (IndexerException e) {
            log.warn("Ignored non indexed word :" + token, e);
         }
      }
      return features;
   }

   @Override
   public String getTokenInternal(int index) throws IndexerException {
      if (_indexToName.containsKey(index)) {
         return _indexToName.get(index);
      }
      throw new IndexerException("Invalid index " + index);
   }

   public int getDictSize() {
      return this._indexToName.size();
   }

}
