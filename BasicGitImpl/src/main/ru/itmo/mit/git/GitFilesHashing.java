package ru.itmo.mit.git;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.DeflaterInputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import java.util.zip.InflaterOutputStream;

public class GitFilesHashing {

    public void encode(@NotNull File filename, String currentIndexDirectory) throws GitException {
        String newFileName = currentIndexDirectory + filename;
        File newFile = new File(newFileName);
        try {
            if (!newFile.exists()) {
                Path pathToFile = Paths.get(newFileName);
                Files.createDirectories(pathToFile.getParent());
                Files.createFile(pathToFile);
            }
        } catch (IOException ex) {
            throw new GitException("Unable to add encoded file to index.");
        }
        try (DeflaterInputStream fr = new DeflaterInputStream(new FileInputStream(filename));
             DeflaterOutputStream fw = new DeflaterOutputStream(new FileOutputStream(newFileName))) {
            int c = fr.read();
            while (c != -1) {
                fw.write(c);
                c = fr.read();
            }
        } catch (IOException e) {
            throw new GitException("Unable to encode file.");
        }
    }

    public void decode(@NotNull String sourceFilename, @NotNull String destFilename) throws GitException {
        try (InflaterInputStream fr = new InflaterInputStream(new FileInputStream(sourceFilename));
             InflaterOutputStream fw = new InflaterOutputStream(new FileOutputStream(destFilename))) {
            int c = fr.read();
            while (c != -1) {
                fw.write(c);
                c = fr.read();
            }
        } catch (IOException e) {
            throw new GitException("Unable to decode file from index.");
        }
    }

    public String decodeLocal(@NotNull String sourceFilename, @NotNull String destFilename) throws GitException {
        try (InflaterInputStream fr = new InflaterInputStream(new FileInputStream(sourceFilename));
             InflaterOutputStream fw = new InflaterOutputStream(new FileOutputStream(destFilename + "local"))) {
            int c = fr.read();
            while (c != -1) {
                fw.write(c);
                c = fr.read();
            }
        } catch (IOException ex) {
            throw new GitException("Unable to decode file from index.");
        }
        try {
            String toReturn = new String(Files.readAllBytes(Paths.get(destFilename + "local")));
            File file = new File(destFilename + "local");
            file.delete();
            return toReturn;
        } catch (IOException ex) {
            throw new GitException("Unable to remove auxiliary decoded file.");
        }
    }
}