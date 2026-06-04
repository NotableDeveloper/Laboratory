package jgit.protocol;

import java.util.Arrays;
import java.util.List;

public class RequestMessage {
    private final String command;
    private final List<String> parameters;

    public RequestMessage(String command, List<String> parameters) {
        this.command = command;
        this.parameters = parameters;
    }

    public String getCommand() {
        return command;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public String getParameter(int index) {
        if (index >= parameters.size()) {
            return null;
        }
        return parameters.get(index);
    }

    public int getParameterCount() {
        return parameters.size();
    }

    public static RequestMessage parse(String message) throws Exception {
        if (message == null || message.trim().isEmpty()) {
            throw new Exception("Empty message");
        }

        String[] parts = message.trim().split("\\|", -1);
        if (parts.length < 1) {
            throw new Exception("Invalid message format");
        }

        String command = parts[0].toUpperCase();
        List<String> parameters = Arrays.asList(Arrays.copyOfRange(parts, 1, parts.length));

        return new RequestMessage(command, parameters);
    }
}
