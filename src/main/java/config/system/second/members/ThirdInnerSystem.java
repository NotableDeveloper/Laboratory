package config.system.second.members;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class ThirdInnerSystem {
    private boolean enable = false;
    private boolean encryptEnable = false;
    private String fileFormat = "";
    private String fileMask = "";
}