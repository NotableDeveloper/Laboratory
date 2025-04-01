package logback_filter;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class User {
    private String name;
    private String phoneNumber;
    private String email;
}
