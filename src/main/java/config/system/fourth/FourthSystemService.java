package config.system.fourth;

import config.system.fourth.members.InnerThread;
import config.system.fourth.members.SSL;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@ToString
@Getter
public class FourthSystemService {
    private String innerSystemAddress = "";
    private int innerSystemPort = 0;
    private String exposeAddress = "";
    private int exposePort = 0;
    private SSL ssl;
    private InnerThread innerThread;
}

