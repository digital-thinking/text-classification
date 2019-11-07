package de.ixeption.classify.pipeline.impl;

import com.google.common.collect.Lists;
import de.ixeption.classify.features.TextFeature;
import de.ixeption.classify.features.TextFeatureExtractor;
import de.ixeption.classify.pipeline.TextProcessingPipeline;
import de.ixeption.classify.pipeline.TokenizedText;
import de.ixeption.classify.postprocessing.TokenProcessor;
import de.ixeption.classify.preprocessing.TextPreprocessor;
import de.ixeption.classify.tokenization.TextTokenizer;
import smile.math.SparseArray;

import java.util.List;


public class DefaultTextPipeline implements TextProcessingPipeline {


    private List<TokenProcessor> tokenProcessors;
    private List<TextPreprocessor> preprocessors;
    private TextTokenizer textTokenizer;
    private TextFeatureExtractor textFeatureExtractor;

    public DefaultTextPipeline(TextFeatureExtractor textFeatureExtractor, TextTokenizer textTokenizer) {
        preprocessors = Lists.newArrayList();
        tokenProcessors = Lists.newArrayList();
        this.textFeatureExtractor = textFeatureExtractor;
        this.textTokenizer = textTokenizer;
    }

    public List<TextPreprocessor> getPreprocessors() {
        return preprocessors;
    }

    public void setPreprocessors(List<TextPreprocessor> preprocessors) {
        this.preprocessors = preprocessors;
    }

    public TextTokenizer getTextTokenizer() {
        return textTokenizer;
    }

    public void setTextTokenizer(TextTokenizer textTokenizer) {
        this.textTokenizer = textTokenizer;
    }

    public TextFeatureExtractor getTextFeatureExtractor() {
        return textFeatureExtractor;
    }

    public void setTextFeatureExtractor(TextFeatureExtractor textFeatureExtractor) {
        this.textFeatureExtractor = textFeatureExtractor;
    }

    @Override
    public SparseArray process(TextFeature textFeature) {
        return textFeatureExtractor.extract(prepare(textFeature).getTokens());

    }

    @Override
    public TokenizedText prepare(TextFeature textFeature) {
        textFeature = preprocess(textFeature);
        TokenizedText tokenizedText = tokenize(textFeature);
        tokenizedText = processTokens(tokenizedText);
        return tokenizedText;

    }

    public TokenizedText processTokens(TokenizedText tokenizedText) {
        for (TokenProcessor tp : tokenProcessors) {
            tokenizedText = tp.process(tokenizedText);
        }
        return tokenizedText;
    }

    public TokenizedText tokenize(TextFeature textFeature) {
        return textTokenizer.tokenize(textFeature);
    }

    public TextFeature preprocess(TextFeature textFeature) {
        for (TextPreprocessor tp : preprocessors) {
            textFeature = tp.preprocess(textFeature);
        }
        return textFeature;
    }

    @Override
    public int getIndex(String s) throws IndexerException {
        return textFeatureExtractor.getIndex(s);
    }

    @Override
    public String getToken(int index) throws IndexerException {
        return textFeatureExtractor.getToken(index);
    }

    public List<TokenProcessor> getTokenProcessors() {
        return tokenProcessors;
    }

    public void setTokenProcessors(List<TokenProcessor> tokenProcessors) {
        this.tokenProcessors = tokenProcessors;
    }
}
