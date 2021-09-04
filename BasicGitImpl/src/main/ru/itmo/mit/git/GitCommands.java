package ru.itmo.mit.git;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class GitCommands {

    private File projectDirectory;
    private String indexDirectory;
    private String masterIndexDirectory;
    private String helperDirectory;
    private GitIndex gitIndexInstance;
    private String head;
    private String masterHead;
    private String prevHead;
    private String masterPrevHead;
    private boolean wasInitialized = false;
    private GitCommits gitCommitsInstance;
    private GitStateMaintainer gitStateMaintainerInstance;
    public static HashSet<String> hashes = new HashSet<>();

    private static final String separator = java.io.File.separator;
    private static final String customGitDirectoryName = ".customGit";
    private static final String commitDirectoryName = "commit_";
    private static final String helperFileName = "gitHelper.txt";
    private static final String helperDirectoryName = ".helperDir";
    private static final String initialCommitName = "initialCommit";

    public static String getSeparator() {
        return separator;
    }

    public static String getCustomGitDirectoryName() {
        return customGitDirectoryName;
    }

    public static String getCommitDirectoryName() {
        return commitDirectoryName;
    }

    public static String getHelperFileName() {
        return helperFileName;
    }

    public static String getHelperDirectoryName() {
        return helperDirectoryName;
    }

    public static String getInitialCommitName() {
        return initialCommitName;
    }

    public void checkInitialized() throws GitException {
        if (!wasInitialized) {
            throw new GitException("Error: not a customGit repository.");
        }
    }

    public void init() throws GitException {
        if (wasInitialized) {
            throw new GitException("Repository was already initialized");
        }
        createDirectory(System.getProperty("user.dir"));
        gitCommitsInstance = new GitCommits();
        gitStateMaintainerInstance = new GitStateMaintainer();
        prevHead = masterPrevHead = initialCommitName;
        head = masterHead = gitCommitsInstance.addCommit(Collections.singletonList("Initial empty commit"), prevHead,
                projectDirectory.getAbsolutePath(), gitIndexInstance, null, true);
        hashes.add(head);
        indexDirectory = masterIndexDirectory = projectDirectory + separator + customGitDirectoryName + separator
                + commitDirectoryName + head;
        gitIndexInstance = new GitIndex();
        GitCliImpl.outputStream.println("Project initialized");
        wasInitialized = true;
        checkDirectories();
    }

    private void createDirectory(String directoryPath) throws GitException {
        File directory = new File(directoryPath + separator + customGitDirectoryName);
        projectDirectory = new File(directoryPath);
        if (!directory.exists() && !directory.mkdirs()) {
            throw new GitException("Unable to create .customGit directory: " + directory.getAbsolutePath());
        }
        File helper = new File(directoryPath + separator + customGitDirectoryName + separator + helperDirectoryName);
        helperDirectory = directoryPath + separator + customGitDirectoryName + separator + helperDirectoryName;
        if (!helper.exists() && !helper.mkdirs()) {
            throw new GitException("Unable to create .helperDir directory: " + helper.getAbsolutePath());
        }
    }

    private void checkDirectories() {
        String actualDirectory = System.getProperty("user.dir");
        if (!projectDirectory.getAbsolutePath().equals(actualDirectory)) {
            for (String key : gitCommitsInstance.commitsLocations.keySet()) {
                String newLocation = actualDirectory + gitCommitsInstance.commitsLocations.get(key)
                        .substring(projectDirectory.getAbsolutePath().length());
                gitCommitsInstance.commitsLocations.put(gitCommitsInstance.commitsLocations.get(key), newLocation);
            }
            projectDirectory = new File(actualDirectory + separator + customGitDirectoryName);
            helperDirectory = actualDirectory + separator + customGitDirectoryName + separator + helperDirectoryName;
            indexDirectory = masterIndexDirectory = projectDirectory + separator + customGitDirectoryName + separator
                    + commitDirectoryName + head;
        }
    }

    public void restart(String currentFileName) throws GitException {
        gitCommitsInstance = new GitCommits();
        gitIndexInstance = new GitIndex();
        gitStateMaintainerInstance = new GitStateMaintainer();
        try {
            FileReader fileReader = new FileReader(currentFileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            projectDirectory = new File(bufferedReader.readLine());
            indexDirectory = bufferedReader.readLine();
            masterIndexDirectory = bufferedReader.readLine();
            helperDirectory = bufferedReader.readLine();
            head = bufferedReader.readLine();
            masterHead = bufferedReader.readLine();
            prevHead = bufferedReader.readLine();
            masterPrevHead = bufferedReader.readLine();
            wasInitialized = (bufferedReader.readLine().equals("t"));
            gitCommitsInstance.detachedHead = bufferedReader.readLine().equals("t");
            fileReader.close();
        } catch (IOException e) {
            throw new GitException("Error while restarting.");
        }
        checkDirectories();
        gitStateMaintainerInstance.restoreData(gitCommitsInstance, helperDirectory, gitIndexInstance);
    }

    public void shutdown() throws GitException {
        try {
            String currentFileName = helperDirectory + separator + helperFileName;
            PrintWriter writer = new PrintWriter(currentFileName, StandardCharsets.UTF_8);
            writer.println(projectDirectory.getAbsolutePath());
            writer.println(indexDirectory);
            writer.println(masterIndexDirectory);
            writer.println(helperDirectory);
            writer.println(head);
            writer.println(masterHead);
            writer.println(prevHead);
            writer.println(masterPrevHead);
            writer.println(wasInitialized ? "t" : "f");
            writer.println(gitCommitsInstance.detachedHead ? "t" : "f");
            writer.close();
        } catch (IOException e) {
            throw new GitException("Error while shutdown.");
        }
        gitStateMaintainerInstance.storeData(gitCommitsInstance, helperDirectory, gitIndexInstance);
    }

    public void add(@NotNull List<@NotNull String> files) throws GitException {
        checkInitialized();
        checkDirectories();
        gitIndexInstance.getFilesStatus(indexDirectory, files, false, projectDirectory.getAbsolutePath(), true);
        GitCliImpl.outputStream.println("Addition completed successfully");
    }

    public void rm(@NotNull List<@NotNull String> files) throws GitException {
        checkInitialized();
        gitIndexInstance.removeFiles(files, indexDirectory, projectDirectory.getAbsolutePath());
        GitCliImpl.outputStream.println("Removal completed successfully");
    }

    public void status() throws GitException {
        checkInitialized();
        if (gitCommitsInstance.detachedHead) {
            GitCliImpl.outputStream.println("HEAD is detached.");
            return;
        }
        GitCliImpl.outputStream.println("On branch: master");
        GitCliImpl.outputStream.println("Current commit hash: " + head);
        if (gitCommitsInstance.commitsLocations.size() == 1) {
            GitCliImpl.outputStream.println("No commits yet");
        }
        File indexDirFile = new File(indexDirectory);
        gitIndexInstance.printFilesStatus(projectDirectory, indexDirFile);
    }

    public void commit(@NotNull List<@NotNull String> message) throws GitException {
        checkInitialized();
        String prevHash = head;
        head = masterHead = gitCommitsInstance.addCommit(message, prevHead, projectDirectory.getAbsolutePath(),
                gitIndexInstance, head, false);
        prevHead = masterPrevHead = prevHash;
        hashes.add(head);
        indexDirectory = masterIndexDirectory = projectDirectory + separator + customGitDirectoryName + separator +
                commitDirectoryName + head;
        GitCliImpl.outputStream.println("Commit completed successfully");
    }

    public void reset(@NotNull List<@NotNull String> revision) throws GitException {
        checkInitialized();
        head = revision.get(0);
        prevHead = masterPrevHead = head;
        head = masterHead = gitCommitsInstance.resetHead(gitIndexInstance, head, projectDirectory, null, false, false);
        indexDirectory = masterIndexDirectory = gitCommitsInstance.commitsLocations.get(prevHead);
        GitCliImpl.outputStream.println("Reset completed successfully");
    }

    public void log(List<@NotNull String> revision) throws GitException {
        checkInitialized();
        String currentHead = initialCommitName;
        if (revision != null) {
            currentHead = revision.get(0);
        }
        gitCommitsInstance.traverseCommits(prevHead, currentHead);
    }

    public void checkout(@NotNull List<@NotNull String> files) throws GitException {
        checkInitialized();
        switch (files.get(0)) {
            case "--":
                prevHead = masterPrevHead;
                head = masterHead;
                indexDirectory = masterIndexDirectory;
                gitCommitsInstance.resetHead(gitIndexInstance, prevHead, projectDirectory, files, true, false);
                gitCommitsInstance.detachedHead = false;
                break;
            case "master":
                prevHead = masterPrevHead;
                head = masterHead;
                indexDirectory = masterIndexDirectory;
                gitCommitsInstance.resetHead(gitIndexInstance, prevHead, projectDirectory, null, false, false);
                gitCommitsInstance.detachedHead = false;
                break;
            case "HEAD~":
                masterPrevHead = prevHead;
                masterHead = head;
                masterIndexDirectory = indexDirectory;
                head = gitCommitsInstance.getEnumeratedCommit(files.get(0), prevHead);
                gitCommitsInstance.checkout(head, gitIndexInstance, projectDirectory);
                gitCommitsInstance.detachedHead = true;
                prevHead = head;
                indexDirectory = gitCommitsInstance.commitsLocations.get(prevHead);
                head = gitCommitsInstance.resetHead(gitIndexInstance, head, projectDirectory, null, false, false);
                break;
            default:
                masterPrevHead = prevHead;
                masterHead = head;
                masterIndexDirectory = indexDirectory;
                head = files.get(0);
                gitCommitsInstance.checkout(head, gitIndexInstance, projectDirectory);
                gitCommitsInstance.detachedHead = true;
                prevHead = head;
                indexDirectory = gitCommitsInstance.commitsLocations.get(prevHead);
                head = gitCommitsInstance.resetHead(gitIndexInstance, head, projectDirectory, null, false, false);
                break;
        }
        GitCliImpl.outputStream.println("Checkout completed successfully");
    }

    public String getHash(int n) throws GitException {
        return gitCommitsInstance.getEnumeratedCommit("HEAD~" + n, prevHead);
    }

}
