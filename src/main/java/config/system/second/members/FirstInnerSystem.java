package config.system.second.members;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@ToString
public class FirstInnerSystem {
    private String ipAddress = "";
    private int port = 0;
    private String user = "";
    private String password = "";
}


