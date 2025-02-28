package config.system.second;

import config.system.second.members.FirstInnerSystem;
import config.system.second.members.SecondInnerSystem;
import config.system.second.members.ThirdInnerSystem;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@ToString
public class SecondSystemService {
    private String prefix = "";
    private FirstInnerSystem firstInnerSystem;
    private SecondInnerSystem secondInnerSystem;
    private ThirdInnerSystem thirdInnerSystem;
}

