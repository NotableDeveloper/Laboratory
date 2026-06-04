package jgit.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jgit.command.CommandProcessor;
import jgit.command.AddProcessor;
import jgit.command.CommitProcessor;
import jgit.command.InitProcessor;
import jgit.command.PushProcessor;
import jgit.command.StatusProcessor;
import jgit.exception.GitOperationException;
import jgit.git.GitRepositoryManager;
import jgit.protocol.RequestMessage;
import jgit.protocol.ResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;

public class GitCommandHandler extends SimpleChannelInboundHandler<String> {
    private static final Logger logger = LoggerFactory.getLogger(GitCommandHandler.class);
    private final List<CommandProcessor> processors;

    public GitCommandHandler() {
        GitRepositoryManager gitManager = new GitRepositoryManager();
        this.processors = new ArrayList<>();
        this.processors.add(new InitProcessor(gitManager));
        this.processors.add(new AddProcessor(gitManager));
        this.processors.add(new CommitProcessor(gitManager));
        this.processors.add(new StatusProcessor(gitManager));
        this.processors.add(new PushProcessor(gitManager));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        logger.debug("Received git command from {}: {}", ctx.channel().remoteAddress(), msg);

        try {
            RequestMessage request = RequestMessage.parse(msg);
            ResponseMessage response = processCommand(request);
            ctx.writeAndFlush(response.toProtocolString() + "\n");
        } catch (Exception e) {
            logger.error("Error processing command from {}: {}", ctx.channel().remoteAddress(), e.getMessage(), e);
            ResponseMessage errorResponse = ResponseMessage.error(
                GitOperationException.ErrorCode.GIT_ERROR,
                e.getMessage()
            );
            ctx.writeAndFlush(errorResponse.toProtocolString() + "\n");
        }
    }

    private ResponseMessage processCommand(RequestMessage request) throws GitOperationException {
        if ("PING".equalsIgnoreCase(request.getCommand())) {
            return ResponseMessage.success("PONG");
        }

        for (CommandProcessor processor : processors) {
            if (processor.supports(request.getCommand())) {
                return processor.process(request);
            }
        }

        throw new GitOperationException(
            GitOperationException.ErrorCode.INVALID_PARAMS,
            "Unknown command: " + request.getCommand()
        );
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Error in git command handler: {}", ctx.channel().remoteAddress(), cause);
        ctx.close();
    }
}
