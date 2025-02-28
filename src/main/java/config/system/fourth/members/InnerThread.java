package config.system.fourth.members;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@ToString
@Getter
public class InnerThread {
    private int threadSize = 0;
    private int threadMax = 0;
    private int threadTimeout = 0;
    private String threadTimeUnit = "";
}
