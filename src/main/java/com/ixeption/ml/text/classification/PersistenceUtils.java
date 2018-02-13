package com.ixeption.ml.text.classification;

import smile.classification.OnlineClassifier;
import smile.classification.SVM;
import smile.math.SparseArray;

import java.io.*;
import java.nio.file.Path;

public final class PersistenceUtils {
    private PersistenceUtils() {
    }

    ;

    /**
     * reads a SVM from a file
     *
     * @param fileName the target file
     * @return the SVM or null
     */
    @SuppressWarnings("unchecked")
    public static SVM<SparseArray> deserialize(Path fileName) throws IOException, ClassNotFoundException {
        SVM<SparseArray> obj = null;
        FileInputStream fis = new FileInputStream(fileName.toFile());
        BufferedInputStream bis = new BufferedInputStream(fis);
        ObjectInputStream ois = new ObjectInputStream(bis);
        obj = (SVM<SparseArray>) ois.readObject();
        ois.close();
        return obj;
    }

    /**
     * saves a trained SVM to a file
     *
     * @param obj      save a SVM to a target file
     * @param fileName the source file
     */
    public static void serialize(OnlineClassifier<SparseArray> obj, Path fileName) throws IOException {
        FileOutputStream fos = new FileOutputStream(fileName.toFile());
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(obj);
        oos.close();
    }
}
