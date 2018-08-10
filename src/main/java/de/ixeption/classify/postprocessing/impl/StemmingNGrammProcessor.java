package de.ixeption.classify.postprocessing.impl;

import com.google.common.collect.Lists;
import de.ixeption.classify.postprocessing.TokenProcessor;
import de.ixeption.classify.tokenization.Token;
import org.apache.commons.lang3.StringUtils;
import smile.nlp.stemmer.PorterStemmer;
import smile.nlp.stemmer.Stemmer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;


public class StemmingNGrammProcessor implements TokenProcessor {

    public static final String TOKEN_SEPERATOR = " ";
    private final int nGramsCount;
    private final int tokenMinLength;
    private final int tokenMaxLength;
    private final Stemmer _stemmer = new PorterStemmer();

    public StemmingNGrammProcessor(int nGramsCount, int tokenMinLength, int tokenMaxLength) {
        this.nGramsCount = nGramsCount;
        this.tokenMinLength = tokenMinLength;
        this.tokenMaxLength = tokenMaxLength;

    }

    @Override
    public Token[] process(Token[] tokens) {
        Token[] filtered = Arrays.stream(tokens)//
                .map(Token::getText)
                .map(_stemmer::stem) //
                .filter(s -> s.length() >= this.tokenMinLength) // filter short
                .filter(s -> s.length() < this.tokenMaxLength) // filter long
                .filter(s -> !StringUtils.isNumeric(s))// filter numbers
                .filter(s -> {
                    Character.UnicodeScript script = Character.UnicodeScript.of(s.codePointAt(0));
                    return script.equals(Character.UnicodeScript.COMMON) || script.equals(Character.UnicodeScript.LATIN);
                })// latin
                .map(Token::new)
                .toArray(Token[]::new);

        return addNGrams(filtered);
    }

    public int getnGramsCount() {
        return nGramsCount;
    }

    private Token[] addNGrams(Token[] tokens) {
        if (getnGramsCount() == 0) {
            return tokens;
        }
        List<Token> nGramms = Lists.newArrayList();
        // n-gram
        int n = getnGramsCount();
        int counts = tokens.length - n + 1;
        for (int k = 0; k < counts; k++) {
            String ngram = "";
            int end = k + n;
            for (int j = k; j < end; j++) {
                if (ngram.isEmpty()) {
                    ngram = tokens[j].getText();
                } else {
                    ngram = ngram + TOKEN_SEPERATOR + tokens[j].getText();
                }
            }
            nGramms.add(new Token(ngram));
        }
        return Stream.concat(Arrays.stream(tokens), nGramms.stream()).toArray(Token[]::new);
    }
}
