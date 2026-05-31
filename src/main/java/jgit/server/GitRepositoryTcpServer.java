package jgit.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GitRepositoryTcpServer {
    private static final Logger logger = LoggerFactory.getLogger(GitRepositoryTcpServer.class);
    private static final int DEFAULT_PORT = 9000;

    private final int port;
    private TcpServerBootstrap tcpServer;

    public GitRepositoryTcpServer(int port) {
        this.port = port;
    }

    public void start() throws InterruptedException {
        logger.info("Starting Git Repository TCP Server on port {}", port);
        tcpServer = new TcpServerBootstrap(port);
        tcpServer.start();
        logger.info("Git Repository TCP Server is running");
    }

    public void stop() {
        logger.info("Stopping Git Repository TCP Server");
        if (tcpServer != null) {
            tcpServer.stop();
        }
    }

    public void awaitTermination() throws InterruptedException {
        if (tcpServer != null) {
            tcpServer.awaitTermination();
        }
    }

    public static void main(String[] args) {
        int port = DEFAULT_PORT;

        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number: " + args[0]);
                System.exit(1);
            }
        }

        GitRepositoryTcpServer server = new GitRepositoryTcpServer(port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutdown signal received");
            server.stop();
        }));

        try {
            server.start();
            server.awaitTermination();
        } catch (InterruptedException e) {
            logger.error("Server interrupted", e);
            Thread.currentThread().interrupt();
            System.exit(1);
        } catch (Exception e) {
            logger.error("Server error", e);
            System.exit(1);
        }
    }
}
