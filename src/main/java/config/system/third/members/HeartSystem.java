package config.system.third.members;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class HeartSystem {
    private boolean enable = false;
    private int interval = 0;
    private int aliveCount = 0;
}
