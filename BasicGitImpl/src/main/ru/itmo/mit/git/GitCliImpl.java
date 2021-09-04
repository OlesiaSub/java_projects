package ru.itmo.mit.git;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.PrintStream;
import java.util.List;

public class GitCliImpl implements GitCli {

    private final GitCommands gitId;
    public static PrintStream outputStream = System.out;

    public GitCliImpl() {
        gitId = new GitCommands();
    }

    @Override
    public void runCommand(@NotNull String command, @NotNull List<@NotNull String> arguments) throws GitException {
        try {
            File helper = new File(System.getProperty("user.dir")
                    + GitCommands.getSeparator() + GitCommands.getCustomGitDirectoryName() + GitCommands.getSeparator()
                    + GitCommands.getHelperDirectoryName() + GitCommands.getSeparator() + GitCommands.getHelperFileName());
            if (helper.exists()) {
                gitId.restart(helper.getAbsolutePath());
            }
            switch (command) {
                case GitConstants.INIT:
                    checkArguments(arguments, 0, 0);
                    gitId.init();
                    break;
                case GitConstants.ADD:
                    gitId.add(arguments);
                    break;
                case GitConstants.COMMIT:
                    checkArguments(arguments, 1, 1);
                    gitId.commit(arguments);
                    break;
                case GitConstants.RM:
                    checkArguments(arguments, 1, -1);
                    gitId.rm(arguments);
                    break;
                case GitConstants.STATUS:
                    checkArguments(arguments, 0, 0);
                    gitId.status();
                    break;
                case GitConstants.RESET:
                    checkArguments(arguments, 1, 1);
                    gitId.reset(arguments);
                    break;
                case GitConstants.LOG:
                    checkArguments(arguments, 0, 1);
                    if (arguments.size() == 0) {
                        gitId.log(null);
                    } else {
                        gitId.log(arguments);
                    }
                    break;
                case GitConstants.CHECKOUT:
                    checkArguments(arguments, 1, -1);
                    gitId.checkout(arguments);
                    break;
                default:
                    outputStream.println("Nonexistent command.");
            }
            gitId.shutdown();
        } catch (GitException ex) {
            throw new GitException(ex.getMessage());
        }
    }

    private void checkArguments(List<@NotNull String> arguments, int min, int max) throws GitException {
        if (arguments.size() < min || (arguments.size() > max && max != -1)) {
            throw new GitException("Incorrect number of arguments");
        }
    }

    @Override
    public void setOutputStream(@NotNull PrintStream outputStream) {
        GitCliImpl.outputStream = outputStream;
    }

    @Override
    public @NotNull String getRelativeRevisionFromHead(int n) throws GitException {
        return gitId.getHash(n);
    }
}
