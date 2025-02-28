package config.site.third;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class ThirdExternalService {
    private boolean enable = false;
    private String ipAddress = "";
    private int port = 0;
}
