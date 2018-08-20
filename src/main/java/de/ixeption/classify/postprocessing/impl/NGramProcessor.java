package de.ixeption.classify.postprocessing.impl;

import com.google.common.collect.Lists;
import de.ixeption.classify.pipeline.TokenizedText;
import de.ixeption.classify.postprocessing.TokenProcessor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class NGramProcessor implements TokenProcessor {


    public static final String TOKEN_SEPERATOR = " ";
    private final int nGramsCount;

    public NGramProcessor(int nGramsCount) {
        this.nGramsCount = nGramsCount;
    }

    public int getnGramsCount() {
        return nGramsCount;
    }

    private String[] addNGrams(String[] tokens) {
        if (getnGramsCount() == 0) {
            return tokens;
        }
        List<String> nGramms = Lists.newArrayList();
        // n-gram
        int n = getnGramsCount();
        int counts = tokens.length - n + 1;
        for (int k = 0; k < counts; k++) {
            String ngram = "";
            int end = k + n;
            for (int j = k; j < end; j++) {
                if (ngram.isEmpty()) {
                    ngram = tokens[j];
                } else {
                    ngram = ngram + TOKEN_SEPERATOR + tokens[j];
                }
            }
            nGramms.add(ngram);
        }
        return Stream.concat(Arrays.stream(tokens), nGramms.stream()).toArray(String[]::new);
    }

    @Override
    public TokenizedText process(TokenizedText tokenizedText) {
        tokenizedText.setTokens(addNGrams(tokenizedText.getTokens()));
        return tokenizedText;
    }
}
