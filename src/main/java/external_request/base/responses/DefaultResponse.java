package external_request.base.responses;

import lombok.Getter;

@Getter
public class DefaultResponse {
    private final String requestId;
    private final String sourceAddress;
    private final int sourcePort;

    protected DefaultResponse(String requestId,
                              String sourceAddress, int sourcePort) {
        this.requestId = requestId;
        this.sourceAddress = sourceAddress;
        this.sourcePort = sourcePort;
    }
}
