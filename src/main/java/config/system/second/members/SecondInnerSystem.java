package config.system.second.members;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@Getter
public class SecondInnerSystem {
    private int commandCount = 0;
    private int eventCount = 0;
    private int dataCount = 0;
}

