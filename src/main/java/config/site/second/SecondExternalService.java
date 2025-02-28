package config.site.second;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@Getter
public class SecondExternalService {
    private boolean enable = false;
    private String ipAddress = "";
    private int port = 0;
}
