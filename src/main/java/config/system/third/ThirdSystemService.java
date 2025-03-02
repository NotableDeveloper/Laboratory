package config.system.third;

import config.system.third.members.HeartSystem;
import config.system.third.members.SSL;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@ToString
@Getter
public class ThirdSystemService {
    private int socketPort = 0;
    private String loginMode = "";
    private HeartSystem heartbeat;
    private SSL ssl;
}
