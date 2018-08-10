package de.ixeption.classify.features.impl;

import de.ixeption.classify.features.WordIndexing;
import de.ixeption.classify.tokenization.Token;
import de.ixeption.classify.tokenization.impl.StemmingNGramTextTokenizer;
import org.assertj.core.util.Sets;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import smile.math.SparseArray;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


public class AbstractBagOfWordsFeatureExtractorTest {

   private static final int N_GRAMS = 2;
   private static final int MIN_LENGTH = 3;

   public static Stream<AbstractBagOfWordsFeatureExtractor> getImplementations() {
      Set<String> corpus = Sets.newLinkedHashSet();
      corpus.add("Hello, my name is Jimmy Pop");
      corpus.add("And I'm a dumb white guy");
      corpus.add("I'm not old or new but middle school");
      corpus.add("Fifth grade like junior high");
      corpus.add("I don't know mofo if y'all peeps");
      corpus.add("Be buggin' give props to my ho 'cause she fly");
      corpus.add("But I can take the heat 'cause I'm the other white meat");
      corpus.add("Known as 'Kid Funky Fried'");
      StemmingNGramTextTokenizer stemmingNGramTextTokenizer = new StemmingNGramTextTokenizer(1, MIN_LENGTH, 25);
      Set<Token> strings = corpus.stream().map(stemmingNGramTextTokenizer::extractTokens).flatMap(Arrays::stream).collect(Collectors.toSet());

      return Stream.of(new BagOfWordsFeatureExtractor(strings), new HashTrickBagOfWordsFeatureExtractor(1337));
   }

   @ParameterizedTest(name = "{index}: Impl Class: {0}")
   @MethodSource("getImplementations")
   public void testBagOfWords(AbstractBagOfWordsFeatureExtractor cut) throws WordIndexing.IndexerException {
      String[] s = "known my buggin fifth grade".split(" ");
      SparseArray extract = cut.extract(Token.stringArrayToArray(s));
      String[] tokens = Arrays.stream(s).filter(s1 -> s1.length() >= MIN_LENGTH).toArray(String[]::new);

      int i = 0;
      for (String token : tokens) {
         int index = cut.getIndex(token);
         assertThat(extract.get(index)).isEqualTo(1.0);
         assertThat(cut.getToken(index)).isEqualToIgnoringCase(token);
      }

   }

}