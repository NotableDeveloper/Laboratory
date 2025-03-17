package external_request.base.requests;

import lombok.Getter;
import java.util.UUID;

@Getter
public class DefaultRequest {
    private final String requestId;
    private final RequestType requestType;
    private final ProtocolType protocolType;
    private final String targetAddress;
    private final int targetPort;

    protected DefaultRequest(RequestType requestType,
                             ProtocolType protocolType,
                             String targetAddress,
                             int targetPort) {
        this.requestId = UUID.randomUUID().toString();
        this.requestType = requestType;
        this.protocolType = protocolType;
        this.targetAddress = targetAddress;
        this.targetPort = targetPort;
    }
}
