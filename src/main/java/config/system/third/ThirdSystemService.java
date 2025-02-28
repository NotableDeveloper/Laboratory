package config.system.third;

import config.system.third.members.FirstInnerSystem;
import config.system.third.members.SecondInnerSystem;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@ToString
@Getter
public class ThirdSystemService {
    private int socketPort = 0;
    private String loginMode = "";
    private FirstInnerSystem heartbeat;
    private SecondInnerSystem ssl;
}
