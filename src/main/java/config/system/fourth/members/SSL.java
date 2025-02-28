package config.system.fourth.members;

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

    public boolean isEnable() {
        return enable;
    }

    public String getKeystoreServerFile() {
        return keystoreServerFile;
    }

    public String getKeystoreServerPassword() {
        return keystoreServerPassword;
    }

    public String getKeystoreServerTrustManager() {
        return keystoreServerTrustManager;
    }

    public String getKeystoreServerManagerPassword() {
        return keystoreServerManagerPassword;
    }

    public String getKeystoreClientFile() {
        return keystoreClientFile;
    }

    public String getKeystoreClientPassword() {
        return keystoreClientPassword;
    }

    public String getKeystoreClientTrustManager() {
        return keystoreClientTrustManager;
    }

    public String getKeystoreClientManagerPassword() {
        return keystoreClientManagerPassword;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public int getServerPort() {
        return serverPort;
    }

    @Override
    public String toString() {
        return "SSL config {" +
                "enable=" + enable +
                ", serverAddress='" + serverAddress + '\'' +
                ", serverPort=" + serverPort +
                ", keystoreServerFile='" + keystoreServerFile + '\'' +
                ", keystoreServerPassword='" + keystoreServerPassword + '\'' +
                ", keystoreServerTrustManager='" + keystoreServerTrustManager + '\'' +
                ", keystoreServerManagerPassword='" + keystoreServerManagerPassword + '\'' +
                ", keystoreClientFile='" + keystoreClientFile + '\'' +
                ", keystoreClientPassword='" + keystoreClientPassword + '\'' +
                ", keystoreClientTrustManager='" + keystoreClientTrustManager + '\'' +
                ", keystoreClientManagerPassword='" + keystoreClientManagerPassword + '\'' +
                " }";
    }
}
