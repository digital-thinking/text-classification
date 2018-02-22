# Text Classify
[![Build Status](https://travis-ci.org/digital-thinking/text-classification.svg?branch=master)](https://travis-ci.org/digital-thinking/text-classification)

Out of the box text classification library in java built on top of the [smile framework](https://github.com/haifengl/smile).

You can use the libraries through Maven central repository by adding the following to your project pom.xml file.
```
    <dependency>
      <groupId>de.ixeption.classify</groupId>
      <artifactId>text-classify</artifactId>
      <version>1.0.0</version>
    </dependency>
```

## Sentiment analysis

```java
     // create a binary classification pipeline
        double softMarginPositive = 0.5;
        double getSoftMarginNegative = 0.5;
        int numberOfNGrams = 3;
        int tokenMonLength = 3;
        int seed = 1337;

        BinaryTextClassifierTrainer binaryTextClassifierTrainer =
                new BinaryTextClassifierTrainer(softMarginPositive, getSoftMarginNegative,
                        new DefaultTextPipeline(
                                new HashTrickBagOfWordsFeatureExtractor(numberOfNGrams, tokenMonLength, seed)));


        // load example data set https://www.kaggle.com/c/si650winter11/data
        TrainingData data = new TrainingData("data/training.txt");
        ArrayList<TextFeature> textFeatures = data.getTextFeatures();
        ArrayList<Integer> labels = data.getLabels();
        FeatureUtils.shuffle(textFeatures, labels);

        // separate train and validation data
        int trainSize = 6000;
        TextFeature[] trainX = textFeatures.stream().limit(trainSize).toArray(TextFeature[]::new);
        int[] trainY = labels.stream().limit(trainSize).mapToInt(Integer::intValue).toArray();

        TextFeature[] testX = textFeatures.stream().skip(trainSize).toArray(TextFeature[]::new);
        int[] testY = labels.stream().skip(trainSize).mapToInt(Integer::intValue).toArray();

        // train the classifier
        TextClassifier classifier = binaryTextClassifierTrainer.train(trainX, trainY);

        // predict data
        int[] predictions = Arrays.stream(testX).map(classifier::predict).mapToInt(Prediction::getLabel).toArray();

        int correct = 0;
        for (int i = 0; i < testY.length; i++) {
            if (predictions[i] == testY[i]) {
                correct++;
            }

        }

        double accuracy = correct / (double) testY.length;
        System.out.println("Accuracy: " + accuracy);
                            
   
```