package de.ixeption.classify.pipeline.impl;

import com.google.common.collect.Lists;
import de.ixeption.classify.features.TextFeature;
import de.ixeption.classify.features.TextFeatureExtractor;
import de.ixeption.classify.pipeline.TextProcessingPipeline;
import de.ixeption.classify.preprocessing.TextPreprocessor;
import de.ixeption.classify.tokenization.TextTokenizer;
import de.ixeption.classify.tokenization.Token;
import smile.math.SparseArray;

import java.util.List;


public class DefaultTextPipeline implements TextProcessingPipeline {

   private final List<TextPreprocessor> preprocessors;
   private final TextTokenizer textTokenizer;
   private final TextFeatureExtractor textFeatureExtractor;

   public DefaultTextPipeline(TextFeatureExtractor textFeatureExtractor, TextTokenizer textTokenizer, TextPreprocessor... textPreprocessors) {
      preprocessors = Lists.newArrayList(textPreprocessors);
      this.textFeatureExtractor = textFeatureExtractor;
      this.textTokenizer = textTokenizer;
   }

   @Override
   public SparseArray process(TextFeature textFeature) {
      for (TextPreprocessor tp : preprocessors) {
         textFeature = tp.preprocess(textFeature);
      }
      Token[] tokens = textTokenizer.tokenize(textFeature);
      return textFeatureExtractor.extract(tokens);

   }

   @Override
   public int getIndex(String s) throws IndexerException {
      return textFeatureExtractor.getIndex(s);
   }

   @Override
   public String getToken(int index) throws IndexerException {
      return textFeatureExtractor.getToken(index);
   }

}
