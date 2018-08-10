package de.ixeption.classify.features.impl;

import de.ixeption.classify.features.TextFeatureExtractor;
import de.ixeption.classify.tokenization.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smile.math.SparseArray;


public abstract class AbstractBagOfWordsFeatureExtractor implements TextFeatureExtractor {

   private static final Logger log = LoggerFactory.getLogger(AbstractBagOfWordsFeatureExtractor.class);

   @Override
   public final SparseArray extract(Token[] tokens) {
      SparseArray features = transform(tokens);
      return scale(features);
   }

   protected SparseArray transform(Token[] tokens) {
      SparseArray features = new SparseArray();
      for (Token token : tokens) {
         try {
            int index = getIndexInternal(token.getText());
            features.set(index, 1.0f);
         } catch (IndexerException e) {
            log.warn("Ignored non indexed word :" + token, e);
         }
      }
      return features;

   }

   protected abstract SparseArray scale(SparseArray features);

   @Override
   public String getToken(int index) throws IndexerException {
      return getTokenInternal(index);
   }

   protected abstract String getTokenInternal(int index) throws IndexerException;

   @Override
   public int getIndex(String s) throws IndexerException {
      return getIndexInternal(s);
   }

   protected abstract int getIndexInternal(String token) throws IndexerException;

}
