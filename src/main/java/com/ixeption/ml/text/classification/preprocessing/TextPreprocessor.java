package com.ixeption.ml.text.classification.preprocessing;

import java.util.Set;

public interface TextPreprocessor {

    String preprocess(String input, Set<String> replaceRegEx, String language);
}
