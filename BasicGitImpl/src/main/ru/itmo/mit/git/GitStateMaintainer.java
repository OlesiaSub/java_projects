package ru.itmo.mit.git;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;

public class GitStateMaintainer {

    private static final String commitsInfoFileName = "commitsInfo.ser";
    private static final String commitsLocationsFileName = "commitLocationsInfo.ser";
    private static final String commitsStatesFileName = "commitStatesInfo.ser";
    private static final String indexStatesFileName = "indexStatesInfo.ser";
    private static final String commandsHashesFilename = "commandsHashes.ser";

    public void storeData(GitCommits gitCommitsInstance, String helperDirectory, GitIndex gitIndex) throws GitException {
        FileOutputStream fileOutput;
        ObjectOutputStream objectOutput;

        try {
            fileOutput = new FileOutputStream(helperDirectory + GitCommands.getSeparator() + commitsInfoFileName);
            objectOutput = new ObjectOutputStream(fileOutput);
            objectOutput.writeObject(gitCommitsInstance.commitsInfo);
            objectOutput.close();
        } catch (IOException e) {
            throw new GitException("Unable to store commitsInfo.");
        }

        try {
            fileOutput = new FileOutputStream(helperDirectory + GitCommands.getSeparator() + commitsStatesFileName);
            objectOutput = new ObjectOutputStream(fileOutput);
            objectOutput.writeObject(gitCommitsInstance.commitStates);
            objectOutput.close();
        } catch (IOException e) {
            throw new GitException("Unable to store commitStates.");
        }

        try {
            fileOutput = new FileOutputStream(helperDirectory + GitCommands.getSeparator() + commitsLocationsFileName);
            objectOutput = new ObjectOutputStream(fileOutput);
            objectOutput.writeObject(gitCommitsInstance.commitsLocations);
            objectOutput.close();
        } catch (IOException e) {
            throw new GitException("Unable to store commitLocations.");
        }

        try {
            fileOutput = new FileOutputStream(helperDirectory + GitCommands.getSeparator() + indexStatesFileName);
            objectOutput = new ObjectOutputStream(fileOutput);
            objectOutput.writeObject(gitIndex.states);
            objectOutput.close();
        } catch (IOException e) {
            throw new GitException("Unable to store indexStates.");
        }

        try {
            fileOutput = new FileOutputStream(helperDirectory + GitCommands.getSeparator() + commandsHashesFilename);
            objectOutput = new ObjectOutputStream(fileOutput);
            objectOutput.writeObject(GitCommands.hashes);
            objectOutput.close();
        } catch (IOException e) {
            throw new GitException("Unable to store commandHashes.");
        }
    }

    @SuppressWarnings("unchecked")
    public void restoreData(GitCommits gitCommitsInstance, String helperDirectory, GitIndex gitIndex) throws GitException {
        FileInputStream fileInput;
        ObjectInputStream objectInput;

        try {
            fileInput = new FileInputStream(helperDirectory + GitCommands.getSeparator() + commitsInfoFileName);
            objectInput = new ObjectInputStream(fileInput);
            gitCommitsInstance.commitsInfo = (HashMap<String, GitCommits.CommitInfo>) objectInput.readObject();
            objectInput.close();
        } catch (IOException | ClassNotFoundException e) {
            throw new GitException("Unable to restore commitsInfo.");
        }

        try {
            fileInput = new FileInputStream(helperDirectory + GitCommands.getSeparator() + commitsStatesFileName);
            objectInput = new ObjectInputStream(fileInput);
            gitCommitsInstance.commitStates = (HashMap<String, HashMap<String, GitState>>) objectInput.readObject();
            objectInput.close();
        } catch (IOException | ClassNotFoundException e) {
            throw new GitException("Unable to restore commitStates.");
        }

        try {
            fileInput = new FileInputStream(helperDirectory + GitCommands.getSeparator() + commitsLocationsFileName);
            objectInput = new ObjectInputStream(fileInput);
            gitCommitsInstance.commitsLocations = (HashMap<String, String>) objectInput.readObject();
            objectInput.close();
        } catch (IOException | ClassNotFoundException e) {
            throw new GitException("Unable to restore commitLocations.");
        }

        try {
            fileInput = new FileInputStream(helperDirectory + GitCommands.getSeparator() + indexStatesFileName);
            objectInput = new ObjectInputStream(fileInput);
            gitIndex.states = (HashMap<String, GitState>) objectInput.readObject();
            objectInput.close();
        } catch (IOException | ClassNotFoundException e) {
            throw new GitException("Unable to restore indexStates.");
        }

        try {
            fileInput = new FileInputStream(helperDirectory + GitCommands.getSeparator() + commandsHashesFilename);
            objectInput = new ObjectInputStream(fileInput);
            GitCommands.hashes = (HashSet<String>) objectInput.readObject();
            objectInput.close();
        } catch (IOException | ClassNotFoundException e) {
            throw new GitException("Unable to restore commandsHashes.");
        }
    }
}
