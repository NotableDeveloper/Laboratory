package config.system.first;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@ToString
public class FirstSystemService {
    private boolean enable = false;
    private boolean logEnable = false;
    private String ipAddress = "";
    private int port = 0;
}
