package config.site.first;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class FirstExternalService {
    private boolean enable = false;
    private String ipAddress = "";
    private int port = 0;
}
