package ru.itmo.mit.git;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws GitException {
        GitCliImpl git = new GitCliImpl();
        String command = args[0];
        List<String> commandArguments = Arrays.stream(args).skip(1).collect(Collectors.toList());
        git.runCommand(command, commandArguments);
    }
}
