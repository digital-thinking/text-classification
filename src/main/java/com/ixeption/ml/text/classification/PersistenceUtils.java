package com.ixeption.ml.text.classification;

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
     * @return the object
     */
    @SuppressWarnings("unchecked")
    public static <T> T deserialize(Path fileName) throws IOException, ClassNotFoundException {
        T obj = null;
        FileInputStream fis = new FileInputStream(fileName.toFile());
        BufferedInputStream bis = new BufferedInputStream(fis);
        ObjectInputStream ois = new ObjectInputStream(bis);
        obj = (T) ois.readObject();
        ois.close();
        return obj;
    }

    /**
     * saves a trained SVM to a file
     *
     * @param obj      save a object
     * @param fileName the source file
     */
    public static <T> void serialize(T obj, Path fileName) throws IOException {
        FileOutputStream fos = new FileOutputStream(fileName.toFile());
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(obj);
        oos.close();
    }
}
