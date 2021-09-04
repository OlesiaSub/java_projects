package ru.itmo.mit.git;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GitIndex {

    public final GitFilesHashing gitFilesHashing = new GitFilesHashing();
    public HashMap<String, GitState> states = new HashMap<>();

    public void getFilesStatus(String currentIndexDirectory, List<String> files, boolean status, String projectDirectory, boolean toAdd)
            throws GitException {
        GitException exception = new GitException();
        try {
            List<String> prevFiles = traverseTree(new File(currentIndexDirectory));
            for (String filename : files) {
                if (!filename.contains(projectDirectory)) {
                    filename = projectDirectory + GitCommands.getSeparator() + filename;
                }
                if (filename.contains(GitCommands.getCustomGitDirectoryName() + GitCommands.getSeparator())) {
                    continue;
                }
                if (!prevFiles.contains(currentIndexDirectory + filename)) {
                    if (!status || (states.containsKey(filename) &&
                            !states.get(filename).equals(GitState.NEW_UNTRACKED))) {
                        states.put(filename, GitState.NEW);
                    } else {
                        states.put(filename, GitState.NEW_UNTRACKED);
                    }
                } else {
                    String prevFilename = null;
                    for (String tmpPrevFilename : prevFiles) {
                        if (tmpPrevFilename.equals(currentIndexDirectory + filename)) {
                            prevFilename = tmpPrevFilename;
                            break;
                        }
                    }
                    try {
                        String encodedCurrentFile = new String(Files.readAllBytes(Paths.get(filename)));
                        assert prevFilename != null;
                        String decodedPrevFile = gitFilesHashing.decodeLocal(prevFilename, prevFilename);
                        if (!decodedPrevFile.equals(encodedCurrentFile)) {
                            states.put(filename, GitState.MODIFIED);
                        }
                    } catch (IOException ex) {
                        exception.addSuppressed(ex);
                    }
                }
            }
            for (String prevFilename : prevFiles) {
                String s = prevFilename.substring(projectDirectory.length() + projectDirectory.length() + 30);
                String otherS = prevFilename.substring(projectDirectory.length() + 29);
                if (!toAdd && (!files.contains(s) && (!files.contains(otherS)))) {
                    prevFilename = prevFilename.substring(projectDirectory.length() + 29);
                    states.put(prevFilename, GitState.DELETED);
                }
            }
            if (!status) {
                modifyFilesState(currentIndexDirectory);
            }
        } catch (GitException | IOException ex) {
            exception.addSuppressed(ex);
            throw exception;
        }
    }

    public void modifyFilesState(String currentIndexDirectory) throws IOException, GitException {
        try {
            for (String filename : states.keySet()) {
                if (states.get(filename).equals(GitState.NEW)) {
                    gitFilesHashing.encode(new File(filename), currentIndexDirectory);
                } else if (states.get(filename).equals(GitState.MODIFIED)) {
                    gitFilesHashing.encode(new File(filename), currentIndexDirectory);
                    states.put(filename, GitState.NON_MODIFIED);
                } else if (states.get(filename).equals(GitState.DELETED)) {
                    states.put(filename, GitState.NEW_UNTRACKED);
                }
            }
        } catch (GitException ex) {
            throw new GitException("Unable to modify files state.");
        }
    }

    public void printFilesStatus(File currentDirectory, File prevDirectory) throws GitException {
        try {
            List<String> files = traverseTree(currentDirectory);
            getFilesStatus(prevDirectory.getAbsolutePath(), files, true, currentDirectory.getAbsolutePath(), false);
        } catch (GitException ex) {
            throw new GitException(ex.getMessage());
        }

        if (states.values().stream().allMatch(x -> (x == GitState.NON_MODIFIED))) {
            GitCliImpl.outputStream.println("Everything is up to date.");
            return;
        }

        Set<String> newFiles = new HashSet<>();
        Set<String> newUntrackedFiles = new HashSet<>();
        Set<String> deletedFiles = new HashSet<>();
        Set<String> modifiedFiles = new HashSet<>();

        for (String key : states.keySet()) {
            if (states.get(key) == GitState.NEW) {
                newFiles.add(key);
            } else if (states.get(key) == GitState.NEW_UNTRACKED) {
                newUntrackedFiles.add(key);
            } else if (states.get(key) == GitState.DELETED) {
                deletedFiles.add(key);
            } else if (states.get(key) == GitState.MODIFIED) {
                modifiedFiles.add(key);
            }
        }

        if (states.containsValue(GitState.NEW)) {
            GitCliImpl.outputStream.println("\nNew ready to commit files:");
            newFiles.forEach(file -> GitCliImpl.outputStream.println(file));
        }
        if (states.containsValue(GitState.NEW_UNTRACKED)) {
            GitCliImpl.outputStream.println("\nNew untracked files:");
            newUntrackedFiles.forEach(file -> GitCliImpl.outputStream.println(file));
        }
        if (states.containsValue(GitState.MODIFIED)) {
            GitCliImpl.outputStream.println("\nModified files:");
            modifiedFiles.forEach(file -> GitCliImpl.outputStream.println(file));
        }
        if (states.containsValue(GitState.DELETED)) {
            GitCliImpl.outputStream.println("\nDeleted files:");
            deletedFiles.forEach(file -> GitCliImpl.outputStream.println(file));
        }
    }

    public List<String> traverseTree(File directory) throws GitException {
        try (Stream<Path> walk = Files.walk(directory.toPath())) {
            return walk.filter(Files::isRegularFile)
                    .map(Path::toString).collect(Collectors.toList());
        } catch (IOException e) {
            throw new GitException("Can not traverse files in a tree.");
        }
    }

    public void removeFiles(@NotNull List<@NotNull String> files, String currentIndexDirectory, String projectDirectory) throws GitException {
        try {
            for (String filename : files) {
                if (!filename.contains(projectDirectory)) {
                    filename = projectDirectory + GitCommands.getSeparator() + filename;
                }
                File indexFile = new File(currentIndexDirectory + filename);
                if (!indexFile.delete()) {
                    throw new GitException("Unable to remove file: " + filename);
                }
                states.put(filename, GitState.NEW_UNTRACKED);
            }
        } catch (GitException ex) {
            throw new GitException("Unable to remove file.");
        }
    }
}
