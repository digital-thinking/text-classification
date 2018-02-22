package de.ixeption.classify;

import de.ixeption.classify.features.TextFeature;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class TrainingData {
    private ArrayList<TextFeature> textFeatures;
    private ArrayList<Integer> labels;

    TrainingData(String file) {
        read(file);
    }

    ArrayList<TextFeature> getTextFeatures() {
        return textFeatures;
    }

    ArrayList<Integer> getLabels() {
        return labels;
    }

    void read(String file) {
        String line = "";
        String cvsSplitBy = "\t";
        textFeatures = new ArrayList<>();
        labels = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] labelText = line.split(cvsSplitBy);
                TextFeature textFeature = new TextFeature(labelText[1]);
                textFeatures.add(textFeature);
                labels.add(Integer.parseInt(labelText[0]));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
