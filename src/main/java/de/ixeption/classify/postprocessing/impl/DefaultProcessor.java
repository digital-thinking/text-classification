package de.ixeption.classify.postprocessing.impl;

import de.ixeption.classify.pipeline.TokenizedText;
import de.ixeption.classify.postprocessing.LanguageAgnosticStemmer;
import de.ixeption.classify.postprocessing.TokenProcessor;

public class DefaultProcessor implements TokenProcessor {

    private final FilteringProcessor filteringProcessor;
    private final LanguageAgnosticStemmer languageAgnosticStemmer;
    private final NGramProcessor nGramProcessor;

    public DefaultProcessor(int nGramsCount, int tokenMinLength, int tokenMaxLength) {
        this.filteringProcessor = new FilteringProcessor(tokenMinLength, tokenMaxLength);
        this.languageAgnosticStemmer = new LanguageAgnosticStemmer();
        this.nGramProcessor = new NGramProcessor(nGramsCount);
    }

    @Override
    public TokenizedText process(TokenizedText tokenizedText) {
        tokenizedText = filteringProcessor.process(tokenizedText);
        tokenizedText = languageAgnosticStemmer.process(tokenizedText);
        return nGramProcessor.process(tokenizedText);

    }
}
