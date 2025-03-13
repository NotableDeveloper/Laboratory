package config.system.third.members;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class SSL {
    private boolean enable = false;
    private String keystoreFile = "";
    private String keystorePassword = "";
    private String keyPassword = "";
}
