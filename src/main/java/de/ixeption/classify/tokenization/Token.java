package de.ixeption.classify.tokenization;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Token {
    private final String text;

    public Token(String text) {
        this.text = text;
    }

    public static Token[] stringArrayToArray(String[] strings) {
        return Arrays.stream(strings).map(Token::new).toArray(Token[]::new);
    }

    public static Set<Token> stringArrayToSet(String[] strings) {
        return Arrays.stream(strings).map(Token::new).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public String getText() {
        return text;
    }

    @Override
    public int hashCode() {
        return text.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Token) {
            return this.text.equals(((Token) obj).getText());
        }
        return super.equals(obj);

    }
}
