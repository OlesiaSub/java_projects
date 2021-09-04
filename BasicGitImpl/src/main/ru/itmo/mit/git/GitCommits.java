package ru.itmo.mit.git;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class GitCommits {

    static class CommitInfo implements Serializable {
        public final String commitHash;
        public final String userName;
        public final String date;
        public final CommitInfo parentCommit;
        public List<CommitInfo> children;
        public final String message;

        public CommitInfo(String commitHash, String userName, String date, CommitInfo parent, String message) {
            this.commitHash = commitHash;
            this.userName = userName;
            this.date = date;
            this.parentCommit = parent;
            this.children = new LinkedList<>();
            this.message = message;
        }
    }

    public HashMap<String, String> commitsLocations = new HashMap<>();
    public HashMap<String, CommitInfo> commitsInfo = new HashMap<>();
    public HashMap<String, HashMap<String, GitState>> commitStates = new HashMap<>();
    public boolean detachedHead = false;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    private static final int hashLength = 10;

    public String addCommit(@NotNull List<@NotNull String> message, String head, String directory,
                            GitIndex currentIndex, String currentHash, boolean firstCommit) throws GitException {
        LocalDateTime currentTime = LocalDateTime.now();

        CommitInfo currentCommit = new CommitInfo(firstCommit ? head : currentHash, "Test user",
                formatter.format(currentTime), commitsInfo.get(head), message.get(0));
        commitsInfo.put(firstCommit ? head : currentHash, currentCommit);
        if (!firstCommit) {
            CommitInfo info = commitsInfo.get(head);
            info.children.add(currentCommit);
        }
        try {
            return addCommitDirectory(directory, currentHash, currentIndex, firstCommit);
        } catch (GitException ex) {
            throw new GitException("Unable to add commit.");
        }
    }

    public String addCommitDirectory(String directory, String currentHash, GitIndex currentIndex, boolean firstCommit)
            throws GitException {
        String newHash = generateHash();
        while (GitCommands.hashes.contains(newHash)) {
            newHash = generateHash();
        }
        File currentCommitDirectory = new File(directory + GitCommands.getSeparator() +
                GitCommands.getCustomGitDirectoryName() + GitCommands.getSeparator() + GitCommands.getCommitDirectoryName() + newHash);
        currentCommitDirectory.mkdir();
        commitsLocations.put(newHash, currentCommitDirectory.getAbsolutePath());
        if (!firstCommit) {
            commitStates.put(currentHash, currentIndex.states);
            try {
                FileUtils.copyDirectory(new File(commitsLocations.get(currentHash)), currentCommitDirectory);
            } catch (IOException ex) {
                throw new GitException("Unable to fill new commit directory.");
            }
            for (String filename : currentIndex.states.keySet()) {
                if (currentIndex.states.get(filename) != GitState.NEW_UNTRACKED) {
                    currentIndex.states.put(filename, GitState.NON_MODIFIED);
                }
            }
        }
        return newHash;
    }

    private String generateHash() {
        int leftLimit = 48;
        int rightLimit = 122;
        Random random = new Random();
        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(hashLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public void traverseCommits(String currentHead, String boundary) {
        HashMap<String, Boolean> used = new HashMap<>();
        while (true) {
            if (used.get(currentHead) != null && used.get(currentHead))
                return;
            used.put(currentHead, true);
            CommitInfo parent = commitsInfo.get(currentHead);
            GitCliImpl.outputStream.println("Commit hash: " + parent.commitHash);
            GitCliImpl.outputStream.println("Author: " + parent.userName);
            GitCliImpl.outputStream.println("Date: " + parent.date);
            GitCliImpl.outputStream.println("Message: " + parent.message + '\n');
            if (currentHead.equals(boundary) || parent.parentCommit.commitHash.equals(GitCommands.getInitialCommitName())) {
                parent = parent.parentCommit;
                GitCliImpl.outputStream.println("Commit hash: " + parent.commitHash);
                GitCliImpl.outputStream.println("Author: " + parent.userName);
                GitCliImpl.outputStream.println("Date: " + parent.date);
                GitCliImpl.outputStream.println("Message: " + parent.message + '\n');
                return;
            }
            currentHead = parent.parentCommit.commitHash;
        }
    }

    public String getEnumeratedCommit(String revision, String head) throws GitException {
        if (revision.equals("HEAD")) {
            return head;
        }
        revision = revision.substring("HEAD~".length());
        int commitNumber = 0;
        for (char ch : revision.toCharArray()) {
            if (ch > 47 && ch < 58) {
                commitNumber = commitNumber * 10 + (ch - '0');
            } else {
                throw new GitException("Impossible commit number required.");
            }
        }
        CommitInfo currentInfo = commitsInfo.get(head);
        for (int i = 0; i < commitNumber; i++) {
            currentInfo = commitsInfo.get(currentInfo.parentCommit.commitHash);
        }
        head = currentInfo.commitHash;
        return head;
    }

    public String resetHead(GitIndex gitIndexInstance, String head, File projectDirectory, List<@NotNull String> files,
                            boolean checkout, boolean moveHead) throws GitException {
        try {
            gitIndexInstance.states = commitStates.get(head);
            List<String> newFiles = gitIndexInstance.traverseTree(new File(commitsLocations.get(head)));
            List<String> oldFiles = gitIndexInstance.traverseTree(projectDirectory);
            for (String filename : oldFiles) {
                if (!filename.contains(GitCommands.getSeparator() + GitCommands.getCustomGitDirectoryName()) &&
                        (!checkout || files.contains(filename)) && !gitIndexInstance.states.get(filename).equals(GitState.NEW_UNTRACKED)) {
                    File currentFile = new File(filename);
                    currentFile.delete();
                }
            }
            for (String filename : newFiles) {
                String oldFileName = filename;
                filename = filename.substring(filename.indexOf(GitCommands.getSeparator() +
                        GitCommands.getCustomGitDirectoryName() + GitCommands.getSeparator() + "commit"));
                filename = filename.substring(hashLength + (GitCommands.getCustomGitDirectoryName() +
                        GitCommands.getCommitDirectoryName()).length() + 2 * GitCommands.getSeparator().length());
                String shortFilename = filename.substring(projectDirectory.getAbsolutePath().length() + GitCommands.getSeparator().length());
                if (!checkout || files.contains(filename) || files.contains(shortFilename)) {
                    File currentFile = new File(filename);
                    currentFile.createNewFile();
                    gitIndexInstance.gitFilesHashing.decode(oldFileName, filename);
                }
            }
            String toReturn = "";
            if (!moveHead) {
                CommitInfo info = commitsInfo.get(head);
                info.children = new LinkedList<>();
                toReturn = addCommitDirectory(projectDirectory.getAbsolutePath(), head, gitIndexInstance, false);
            }
            return toReturn;
        } catch (GitException | IOException ex) {
            throw new GitException("Unable to reset.");
        }
    }

    public void checkout(String head, GitIndex gitIndexInstance, File projectDirectory) throws GitException {
        GitException exception = new GitException();
        try {
            List<String> newFiles = gitIndexInstance.traverseTree(new File(commitsLocations.get(head)));
            List<String> oldFiles = gitIndexInstance.traverseTree(projectDirectory);

            for (String filename : oldFiles) {
                if (!filename.contains(GitCommands.getSeparator() + GitCommands.getCustomGitDirectoryName())
                        && !gitIndexInstance.states.get(filename).equals(GitState.NEW_UNTRACKED)) {
                    File currentFile = new File(filename);
                    currentFile.delete();
                }
            }
            for (String filename : newFiles) {
                String oldFilename = filename;
                filename = filename.substring(filename.indexOf(GitCommands.getSeparator() + GitCommands.getCustomGitDirectoryName() +
                        GitCommands.getSeparator() + "commit"));
                int length = (GitCommands.getCustomGitDirectoryName() + GitCommands.getCommitDirectoryName()).length() + 2 + hashLength;
                filename = filename.substring(length);
                File currentFile = new File(filename);
                try {
                    currentFile.createNewFile();
                } catch (IOException ex) {
                    exception.addSuppressed(ex);
                }
                gitIndexInstance.gitFilesHashing.decode(oldFilename, filename);
            }
        } catch (GitException ex) {
            exception.addSuppressed(ex);
            throw exception;
        }
        if (exception.getMessage() != null) {
            throw exception;
        }
    }

}
