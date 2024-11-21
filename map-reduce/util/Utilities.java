package util;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class Utilities {
    public static void deleteDirectory(String path) {
        if (!Files.exists(Paths.get(path))) {
            return;
        }

        File file = new File(path);
        if (!file.isDirectory()) {
            file.delete();
            return;
        }

        String[] childFiles = file.list();
        if (childFiles == null) {
            //Directory is empty. Proceed for deletion
            file.delete();
            return;
        }

        //Directory has other files.
        //Need to delete them first
        for (String childFilePath : childFiles) {
            //recursively delete the files
            deleteDirectory(path + "/" + childFilePath);
        }
        file.delete();
    }

    public static List<org.apache.hadoop.fs.Path> getFilesInDirectory(FileSystem fs, org.apache.hadoop.fs.Path dir)
            throws IOException {
        List<Path> files = new ArrayList<>();

        // List the files in the directory
        FileStatus[] fileStatuses = fs.listStatus(dir);

        for (FileStatus fileStatus : fileStatuses) {
            if (!fileStatus.isDir()) {
                files.add(fileStatus.getPath());
            }
        }

        return files;
    }

    public static void moveDirectoryContents(String src, String dst) throws IOException {
        File srcDir = new File(src);
        File dstDir = new File(dst);

        // Ensure the source directory exists and is a directory
        if (!srcDir.exists() || !srcDir.isDirectory()) {
            throw new IOException("Source directory does not exist or is not a directory: " + src);
        }

        // Ensure the destination directory exists; create it if not
        if (!dstDir.exists()) {
            if (!dstDir.mkdirs()) {
                throw new IOException("Failed to create destination directory: " + dst);
            }
        }

        // Iterate through the contents of the source directory
        File[] contents = srcDir.listFiles();
        if (contents != null) {
            for (File file : contents) {
                File destFile = new File(dstDir, file.getName());
                if (file.isDirectory()) {
                    // Recursively move subdirectory
                    moveDirectoryContents(file.getAbsolutePath(), destFile.getAbsolutePath());
                    // Delete the empty source directory
                    if (!file.delete()) {
                        throw new IOException("Failed to delete source directory: " + file.getAbsolutePath());
                    }
                } else {
                    // Move file
                    Files.move(file.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }
}
