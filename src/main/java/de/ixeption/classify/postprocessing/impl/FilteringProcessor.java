package de.ixeption.classify.postprocessing.impl;

import de.ixeption.classify.pipeline.TokenizedText;
import de.ixeption.classify.postprocessing.TokenProcessor;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;


public class FilteringProcessor implements TokenProcessor {


    private final int tokenMinLength;
    private final int tokenMaxLength;


    public FilteringProcessor(int tokenMinLength, int tokenMaxLength) {
        this.tokenMinLength = tokenMinLength;
        this.tokenMaxLength = tokenMaxLength;


    }

    public static String replaceToken(String s) {
        if (StringUtils.isNumericSpace(s)) {
            return "token_number";
        }
        if (StringUtils.containsAny(s, '€', '$')) {
            return "token_currency";
        }

        if (!StringUtils.isAlphanumericSpace(s)) {
            if (s.length() > 5 && !s.contains("_")) {
                return "token_symbols";
            }
        }
        return s;
    }

    @Override
    public TokenizedText process(TokenizedText tokenizedText) {
        String[] filtered = Arrays.stream(tokenizedText.getTokens())//
                .filter(s -> {
                    Character.UnicodeScript script = Character.UnicodeScript.of(s.codePointAt(0));
                    return script.equals(Character.UnicodeScript.COMMON) || script.equals(Character.UnicodeScript.LATIN);
                })// latin
                .map(s -> StringUtils.remove(s, '\'')) // replace quotes
                .map(s -> StringUtils.remove(s, '\"')) // replace quotes
                .map(StringUtils::stripAccents)
                .map(FilteringProcessor::replaceToken)
                .filter(s -> s.length() >= this.tokenMinLength) // filter short
                .filter(s -> s.length() < this.tokenMaxLength) // filter long
                .toArray(String[]::new);

        tokenizedText.setTokens(filtered);
        return tokenizedText;
    }


}
