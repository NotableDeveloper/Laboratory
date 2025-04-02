package logback_filtering;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.CompositeConverter;
import java.util.regex.Pattern;

public class UserDataConverter extends CompositeConverter<ILoggingEvent> {
    private static final Pattern USER_NAME = Pattern.compile("[가-힣]{2,}");
    private static final Pattern PHONE_PATTERN = Pattern.compile("010\\d{8}");

    @Override
    protected String transform(ILoggingEvent event, String s) {
        if (s == null) return null;

        String result = PHONE_PATTERN.matcher(s).replaceAll("");
        result = USER_NAME.matcher(result).replaceAll("");

        return result;
    }
}
