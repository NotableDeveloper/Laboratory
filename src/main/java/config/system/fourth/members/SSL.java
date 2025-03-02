package config.system.fourth.members;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
public class SSL {
    private boolean enable = false;

    private String serverAddress = "";
    private int serverPort = 0;

    private String keystoreServerFile = "";
    private String keystoreServerPassword = "";
    private String keystoreServerTrustManager = "";
    private String keystoreServerManagerPassword = "";

    private String keystoreClientFile = "";
    private String keystoreClientPassword = "";
    private String keystoreClientTrustManager = "";
    private String keystoreClientManagerPassword = "";
}
