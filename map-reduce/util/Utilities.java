package util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Utilities {
    public static void deleteDirectory(String path) {
        if (!Files.exists(Paths.get(path))) {
            System.out.println("The file does not exist " + path);
            return;
        }

        File file = new File(path);
        if (!file.isDirectory()) {
            System.out.println("Deleting " + file);
            file.delete();
            return;
        }

        String[] childFiles = file.list();
        if (childFiles == null) {
            //Directory is empty. Proceed for deletion
            System.out.println("Deleting empty directory " + file);
            file.delete();
            return;
        }

        //Directory has other files.
        //Need to delete them first
        for (String childFilePath : childFiles) {
            //recursively delete the files
            System.out.println("Calling deleteDirectory for " + path + "/" + childFilePath);
            deleteDirectory(path + "/" + childFilePath);
        }
        file.delete();
    }
}
