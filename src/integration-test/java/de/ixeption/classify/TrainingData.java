package de.ixeption.classify;

import de.ixeption.classify.features.TextFeature;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class TrainingData {
    protected ArrayList<TextFeature> textFeatures;
    protected ArrayList<Integer> labels;

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
        parse(0, 1, "\t", Paths.get(file), false);
    }

    void parse(int labelcolumn, int textColumn, String split, Path file, boolean header) {
        String line = "";

        textFeatures = new ArrayList<>();
        labels = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file.toFile()))) {
            if (header) br.readLine();
            while ((line = br.readLine()) != null) {
                // use comma as separator

                String[] labelText = line.split(split);
                TextFeature textFeature = new TextFeature(labelText[textColumn].trim());
                textFeatures.add(textFeature);
                labels.add(Integer.parseInt(labelText[labelcolumn]));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
