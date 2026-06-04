package jgit.command;

import jgit.exception.GitOperationException;
import jgit.protocol.RequestMessage;
import jgit.protocol.ResponseMessage;

public abstract class CommandProcessor {
    protected final String command;

    public CommandProcessor(String command) {
        this.command = command;
    }

    public abstract ResponseMessage process(RequestMessage request) throws GitOperationException;

    public boolean supports(String cmd) {
        return this.command.equalsIgnoreCase(cmd);
    }
}
